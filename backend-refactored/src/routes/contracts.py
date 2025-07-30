from math import floor
import random
from time import time
from fastapi import APIRouter
from fastapi.responses import JSONResponse
from fastapi.encoders import jsonable_encoder

from sqlmodel import Session, delete,select,and_,func, update
from starlette.status import *
from bcrypt import *
from uuid_utils import *

from MySql import SessionManager
from MySql.tables import *
from Models.contracts import *
from Models.commons import*
from routes.middlewares.getPlayer import *
from routes.middlewares.key_names import *
from routes.middlewares.checkAuthTokenExpiration import *

router = APIRouter(
    prefix="/contracts",
    tags=["contracts"]
)

session = SessionManager.global_session

#routes
@router.post("/active")
async def get_actives(request:basicAuthTokenRequest):
    check_token = checkAuthTokenValidity(request.authToken)
    if check_token[SUCCESS] == False:
        return JSONResponse(
            status_code = check_token[HTTP_CODE],
            content={"message":check_token[ERROR]}
        )

    obtain_player = getPlayer(request.authToken,session)
    if obtain_player[SUCCESS] == False:
            return JSONResponse(
            status_code = obtain_player[HTTP_CODE],
            content={"message":obtain_player[ERROR]}
        )

    player:Login = obtain_player[PLAYER]
    active_contracts = select(ActiveContract,Contract).distinct().where(
         and_(ActiveContract.idContract == Contract.id,
              ActiveContract.id==AssignedMercenary.idActiveContract,
              AssignedMercenary.idEmployedMercenary==EmployedMercenary.id,
              EmployedMercenary.idMercenary == Mercenary.id,
              EmployedMercenary.idPlayer==player.idPlayer))
    
    result = session.exec(active_contracts)
    contracts = result.fetchall()
    response = []
    for active,contract in contracts:
        response.append(ActiveContractResponseElement(id=contract.id,name=contract.name,
                                                      requiredTime=contract.requiredTime,startTime=active.startTime))
    return JSONResponse(
        status_code = HTTP_200_OK,
        content={"contracts":jsonable_encoder(response)}
    )

@router.post("/redeem")
async def redeem(request:RedeemContractRequest):
    check_token = checkAuthTokenValidity(request.authToken)
    if check_token[SUCCESS] == False:
        return JSONResponse(
            status_code = check_token[HTTP_CODE],
            content={"message":check_token[ERROR]}
        )

    obtain_player = getPlayer(request.authToken,session)
    if obtain_player[SUCCESS] == False:
            return JSONResponse(
            status_code = obtain_player[HTTP_CODE],
            content={"message":obtain_player[ERROR]}
    )

    player:Login = obtain_player[PLAYER]
    active_contract = select(ActiveContract,Contract).where(
         and_(ActiveContract.idContract == Contract.id,
              ActiveContract.id==AssignedMercenary.idActiveContract,
              AssignedMercenary.idEmployedMercenary==EmployedMercenary.id,
              EmployedMercenary.idMercenary == Mercenary.id,
              EmployedMercenary.idPlayer==player.idPlayer,
              ActiveContract.id == request.idContract))

    result = session.exec(active_contract)
    contract = result.first()

    if contract == None:
         return JSONResponse(
              status_code= HTTP_400_BAD_REQUEST,
              content={"message":"Contract does not exist"}
         )
    
    if contract[0].startTime + contract[1].requiredTime > time():
        return JSONResponse(
              status_code= HTTP_403_FORBIDDEN,
              content={"message":"Contract required time has not passed yet"}
        )

    mercenaryPower = select(func.sum(Mercenary.power)).where(
         and_(
              ActiveContract.id==AssignedMercenary.idActiveContract,
              AssignedMercenary.idEmployedMercenary==EmployedMercenary.idMercenary,
              EmployedMercenary.idMercenary == Mercenary.id,
              EmployedMercenary.idPlayer==player.idPlayer,
              ActiveContract.idContract == request.idContract
         )
    )

    result = session.exec(mercenaryPower)
    power = result.first()

    ratio = float(power)/float(contract[1].requiredPower)
    success = (random.random() <= min(1.0,ratio))
    reward = int(success)*(random.randint(contract[1].minReward,contract[1].maxReward))
    response = ContractRedeemedResponse(success = success,reward=reward)

    #update player balance and remove column
    try:
        playerData : Player = getPlayerData(player)[PLAYER]
        playerData.money += reward*int(success)
        session.exec(delete(ActiveContract).where(ActiveContract.id == contract[0].id))
        session.commit()
    except:
        session.rollback()
        return JSONResponse(
            status_code= HTTP_500_INTERNAL_SERVER_ERROR,
            content = {"message":"failed to update player balance"}
        )

    

    return JSONResponse(
        status_code= HTTP_200_OK,
        content = jsonable_encoder(response)
    )

@router.post("/start")
async def start_contract(request:StartContractRequest):

    if len(request.mercenaries) < 1:
        return JSONResponse(
            status_code = HTTP_400_BAD_REQUEST,
            content={"message":"mercenearies field is provided but is empty"}
        )

    check_token = checkAuthTokenValidity(request.authToken)
    if check_token[SUCCESS] == False:
        return JSONResponse(
            status_code = check_token[HTTP_CODE],
            content={"message":check_token[ERROR]}
        )

    obtain_player = getPlayer(request.authToken,session)
    if obtain_player[SUCCESS] == False:
            return JSONResponse(
            status_code = obtain_player[HTTP_CODE],
            content={"message":obtain_player[ERROR]}
        )

    player:Login = obtain_player[PLAYER]
    active_contract_id = select(ActiveContract.idContract).where(
         and_(
              ActiveContract.id==AssignedMercenary.idActiveContract,
              AssignedMercenary.idEmployedMercenary==EmployedMercenary.id,
              EmployedMercenary.idMercenary == Mercenary.id,
              EmployedMercenary.idPlayer==player.idPlayer)
    )
    
    available_contact = select(Contract).where(
        and_(~Contract.id.in_(active_contract_id.scalar_subquery()),Contract.id==request.contract)
    )
    result = session.exec(available_contact)

    contract_to_start = result.first()
    if contract_to_start == None:
        return JSONResponse(
            status_code = HTTP_200_OK,
            content={"message":"Selected contract doesnt exist or it is not available"}
        )
    
    playerData:Player = getPlayerData(player,session)[PLAYER]
    if playerData.money < contract_to_start.startCost:
        return JSONResponse(
            status_code = HTTP_402_PAYMENT_REQUIRED,
            content={"message":"Founds not sufficient for starting contract"}
        )

    #check mercenaries
    select_mercenaries = select(EmployedMercenary).where(and_(EmployedMercenary.idMercenary.in_(request.mercenaries),EmployedMercenary.idPlayer==player.idPlayer))
    result = session.exec(select_mercenaries)
    mercenariesData = result.fetchall()
    mercenaries = []

    if len(mercenariesData) != len(request.mercenaries):
        return JSONResponse(
            status_code = HTTP_403_FORBIDDEN,
            content={"message":"Mismatch between mercenaries that player possesses and the ones that were given"}
    )
    
    if len(mercenariesData) > contract_to_start.maxMercenaries:
        return JSONResponse(
            status_code = HTTP_403_FORBIDDEN,
            content={"message":"Too many mercenaries for given contract"}
        )

    assigned = select(AssignedMercenary.idEmployedMercenary).where(and_(EmployedMercenary.idPlayer == player.idPlayer,AssignedMercenary.idEmployedMercenary == EmployedMercenary.id))

    already_in_use = select(EmployedMercenary).where(and_(EmployedMercenary.idMercenary.in_(assigned.scalar_subquery()),EmployedMercenary.idMercenary.in_(request.mercenaries)))

    result = session.exec(already_in_use)
    if len(result.fetchall()) > 0:
        return JSONResponse(
            status_code = HTTP_403_FORBIDDEN,
            content={"message":"One or more given mercenaries are already assigned to other contracts"}
        )

    try:
        start = floor(time())
        #create contract
        newContract = ActiveContract(
                                     idContract = contract_to_start.id,
                                     startTime = start)
        session.add(newContract)
        session.flush()
        mercenaries = [AssignedMercenary(idActiveContract=newContract.id,idEmployedMercenary=m.id) for m in mercenariesData]
        session.add_all(mercenaries)
        playerData.money-=contract_to_start.startCost
        session.commit()
    except Exception as e:
        session.rollback()
        return JSONResponse(
            status_code = HTTP_500_INTERNAL_SERVER_ERROR,
            content={"message":"Something went wrong in creating contract and assigning mercenaries:"+str(e)}
        )
    
    return JSONResponse(
        status_code = HTTP_200_OK,
        content=jsonable_encoder(ContractStartResponse(success=True))
    )


@router.post("/available")
async def get_availables(request:basicAuthTokenRequest):
    check_token = checkAuthTokenValidity(request.authToken)
    if check_token[SUCCESS] == False:
        return JSONResponse(
            status_code = check_token[HTTP_CODE],
            content={"message":check_token[ERROR]}
        )

    obtain_player = getPlayer(request.authToken,session)
    if obtain_player[SUCCESS] == False:
            return JSONResponse(
            status_code = obtain_player[HTTP_CODE],
            content={"message":obtain_player[ERROR]}
        )

    player:Login = obtain_player[PLAYER]
    active_contracts_id = select(ActiveContract.idContract).where(
         and_(ActiveContract.id == Contract.id,
              ActiveContract.id==AssignedMercenary.idActiveContract,
              AssignedMercenary.idEmployedMercenary==EmployedMercenary.id,
              EmployedMercenary.idMercenary == Mercenary.id,
              EmployedMercenary.idPlayer==player.idPlayer))
    
    available_contacts = select(Contract).where(
        ~Contract.id.in_(active_contracts_id.scalar_subquery())
    )
    result = session.exec(available_contacts)
    contracts = result.fetchall()
    response = []
    for contract in contracts:
        response.append(AvailableContractResponseElement(id=contract.id,name=contract.name,
                                                      requiredTime=contract.requiredTime,requiredPower=contract.requiredPower,maxMercenaries=contract.maxMercenaries,startCost=contract.startCost))
    return JSONResponse(
        status_code = HTTP_200_OK,
        content={"contracts":jsonable_encoder(response)}
    )