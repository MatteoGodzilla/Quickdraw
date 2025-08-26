from math import floor
import random
from time import time
from fastapi import APIRouter
from fastapi.responses import JSONResponse
from fastapi.encoders import jsonable_encoder

from sqlmodel import Sequence, Session, delete,select,and_,func, update
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
from routes.middlewares.boosts import boostedMoney

router = APIRouter(
    prefix="/contracts",
    tags=["contracts"]
)

session = SessionManager.global_session

#routes
@router.post("/active")
async def get_actives(request:BasicAuthTokenRequest):
    check_token = checkAuthTokenValidity(request.authToken)
    if check_token[SUCCESS] == False:
        return JSONResponse(
            status_code = check_token[HTTP_CODE],
            content={"message":check_token[ERROR]}
        )

    obtain_player = getPlayer(request.authToken)
    if obtain_player[SUCCESS] == False:
            return JSONResponse(
            status_code = obtain_player[HTTP_CODE],
            content={"message":obtain_player[ERROR]}
        )

    player:Login = obtain_player[PLAYER]
    active_contracts = select(StartedContract,Contract).distinct().where(and_(
        StartedContract.idContract == Contract.id,
        StartedContract.id == AssignedMercenary.idActiveContract,
        StartedContract.redeemed == 0,
        AssignedMercenary.idEmployedMercenary == EmployedMercenary.id,
        EmployedMercenary.idMercenary == Mercenary.id,
        EmployedMercenary.idPlayer == player.idPlayer
    ))
    
    result = session.exec(active_contracts)
    contracts = result.fetchall()
    contract_ids = [x.id for x,y in contracts]

    used_mercenaries = select(AssignedMercenary,EmployedMercenary,Mercenary).where(
         and_(
              AssignedMercenary.idEmployedMercenary == EmployedMercenary.id,
              EmployedMercenary.idMercenary == Mercenary.id,
              EmployedMercenary.idPlayer == player.idPlayer,
              AssignedMercenary.idActiveContract.in_(contract_ids)
         )
    )

    result = session.exec(used_mercenaries)
    inUseMercenaries = result.fetchall()

    response = []
    for active,contract in contracts:
        response.append(ActiveContractResponseElement(activeId=active.id,name=contract.name,
            requiredTime=contract.requiredTime,
            startTime=active.startTime,
            mercenaries=[InUseMercenary(idEmployment=y.id,power=z.power,name=z.name) for x,y,z in inUseMercenaries if x.idActiveContract==active.id])
        )
        
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

    obtain_player = getPlayer(request.authToken)
    if obtain_player[SUCCESS] == False:
            return JSONResponse(
            status_code = obtain_player[HTTP_CODE],
            content={"message":obtain_player[ERROR]}
    )

    player:Login = obtain_player[PLAYER]
    active_contract = select(StartedContract,Contract).where(and_(
        StartedContract.idContract == Contract.id,
        StartedContract.id == AssignedMercenary.idActiveContract,
        StartedContract.redeemed == 0,
        AssignedMercenary.idEmployedMercenary == EmployedMercenary.id,
        EmployedMercenary.idMercenary == Mercenary.id,
        EmployedMercenary.idPlayer == player.idPlayer,
        StartedContract.id == request.idContract
    ))

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

    mercenaryPower = select(func.sum(Mercenary.power)).where(and_(
          StartedContract.id == AssignedMercenary.idActiveContract,
          AssignedMercenary.idEmployedMercenary == EmployedMercenary.id,
          EmployedMercenary.idMercenary == Mercenary.id,
          EmployedMercenary.idPlayer==player.idPlayer,
          StartedContract.id == request.idContract
    ))

    result = session.exec(mercenaryPower)
    power = result.first()

    ratio = float(power)/float(max(1,contract[1].requiredPower))
    success = (random.random() <= min(1.0,ratio))
    reward = int(success)*(random.randint(contract[1].minReward,contract[1].maxReward))
    toReclaim = AvailableContractResponseElement(
        id=contract[1].id,
        name=contract[1].name,
        requiredTime=contract[1].requiredTime,
        requiredPower=contract[1].requiredPower,
        maxMercenaries=contract[1].maxMercenaries,
        startCost=contract[1].startCost)
    
    #update player balance and remove column
    try:
        playerData : Player = getPlayerData(player)[PLAYER]
        correct_value = boostedMoney(reward,playerData)
        playerData.money += correct_value*int(success)
        update_contract = update(StartedContract).where(StartedContract.id == contract[0].id).values(
            redeemed = True,
            successful = bool(int(success)),
            reward = correct_value
        )
        session.exec(update_contract)
        session.exec(delete(AssignedMercenary).where(AssignedMercenary.idActiveContract == contract[0].id))
        session.commit()
    except:
        session.rollback()
        return JSONResponse(
            status_code= HTTP_500_INTERNAL_SERVER_ERROR,
            content = {"message":"failed to update player balance"}
        )
    
    response = ContractRedeemedResponse(success = True,
        reward=correct_value,
        returnableContract=toReclaim
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

    obtain_player = getPlayer(request.authToken)
    if obtain_player[SUCCESS] == False:
            return JSONResponse(
            status_code = obtain_player[HTTP_CODE],
            content={"message":obtain_player[ERROR]}
        )

    player:Login = obtain_player[PLAYER]
    active_contract_id = select(StartedContract.idContract).where(and_(
          StartedContract.id == AssignedMercenary.idActiveContract,
          StartedContract.redeemed == 0,
          AssignedMercenary.idEmployedMercenary == EmployedMercenary.id,
          EmployedMercenary.idMercenary == Mercenary.id,
          EmployedMercenary.idPlayer == player.idPlayer
    ))
    
    available_contact = select(Contract).where(and_(
        ~Contract.id.in_(active_contract_id.scalar_subquery()),
        Contract.id == request.contract
    ))
    result = session.exec(available_contact)

    contract_to_start = result.first()
    if contract_to_start == None:
        return JSONResponse(
            status_code = HTTP_406_NOT_ACCEPTABLE,
            content={"message":"Selected contract doesnt exist or it is not available"}
        )
    
    playerData:Player = getPlayerData(player)[PLAYER]
    if playerData.money < contract_to_start.startCost:
        return JSONResponse(
            status_code = HTTP_402_PAYMENT_REQUIRED,
            content={"message":"Founds not sufficient for starting contract"}
        )

    #check mercenaries
    select_mercenaries = select(EmployedMercenary).where(and_(
        EmployedMercenary.id.in_(request.mercenaries),
        EmployedMercenary.idPlayer == player.idPlayer
    ))
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

    assigned = select(AssignedMercenary.idEmployedMercenary).where(and_(
        EmployedMercenary.idPlayer == player.idPlayer,
        AssignedMercenary.idEmployedMercenary == EmployedMercenary.id
    ))

    already_in_use = select(EmployedMercenary).where(and_(
        EmployedMercenary.id.in_(assigned.scalar_subquery()),
        EmployedMercenary.id.in_(request.mercenaries)
    ))

    result = session.exec(already_in_use)
    if len(result.fetchall()) > 0:
        return JSONResponse(
            status_code = HTTP_403_FORBIDDEN,
            content={"message":"One or more given mercenaries are already assigned to other contracts"}
        )

    try:
        start = floor(time())
        #create contract
        newContract = StartedContract(
             idContract = contract_to_start.id,
             idPlayer = player.idPlayer,
             startTime = start,
             redeemed = False,
             successful = False,
             reward = 0
        )
        session.add(newContract)
        session.flush()
        mercenaries = [AssignedMercenary(idActiveContract=newContract.id,idEmployedMercenary=m.id) for m in mercenariesData]
        session.add_all(mercenaries)
        playerData.money -= contract_to_start.startCost
        session.commit()
    except Exception as e:
        print(e)
        session.rollback()
        return JSONResponse(
            status_code = HTTP_500_INTERNAL_SERVER_ERROR,
            content={"message":"Something went wrong in creating contract and assigning mercenaries:"+str(e)}
        )
    
    info = StartedContractResponse(
        startTime=newContract.startTime,
        idActiveContract=newContract.id
    )

    return JSONResponse(
        status_code = HTTP_200_OK,
        content=jsonable_encoder(ContractStartResponse(success=True,contractInfo=info))
    )


@router.post("/available")
async def get_availables(request:BasicAuthTokenRequest):
    check_token = checkAuthTokenValidity(request.authToken)
    if check_token[SUCCESS] == False:
        return JSONResponse(
            status_code = check_token[HTTP_CODE],
            content={"message":check_token[ERROR]}
        )

    obtain_player = getPlayer(request.authToken)
    if obtain_player[SUCCESS] == False:
            return JSONResponse(
            status_code = obtain_player[HTTP_CODE],
            content={"message":obtain_player[ERROR]}
        )

    player:Login = obtain_player[PLAYER]
    active_contracts_id = select(StartedContract.idContract).where(and_(
        StartedContract.idContract == Contract.id,
        StartedContract.id == AssignedMercenary.idActiveContract,
        StartedContract.redeemed == 0,
        AssignedMercenary.idEmployedMercenary == EmployedMercenary.id,
        EmployedMercenary.idMercenary == Mercenary.id,
        EmployedMercenary.idPlayer == player.idPlayer
    ))
    
    available_contacts = select(Contract).where(
        ~Contract.id.in_(active_contracts_id.scalar_subquery())
    )
    result = session.exec(available_contacts)
    contracts = result.fetchall()
    response = []
    for contract in contracts:
        response.append(AvailableContractResponseElement(
            id=contract.id,
            name=contract.name,
            requiredTime=contract.requiredTime,
            requiredPower=contract.requiredPower,
            maxMercenaries=contract.maxMercenaries,
            startCost=contract.startCost
        ))
    return JSONResponse(
        status_code = HTTP_200_OK,
        content={"contracts":jsonable_encoder(response)}
    )

@router.post("/stats")
async def get_contracts_stats(request:BasicAuthTokenRequest):
    check_token = checkAuthTokenValidity(request.authToken)
    if check_token[SUCCESS] == False:
        return JSONResponse(
            status_code = check_token[HTTP_CODE],
            content={"message":check_token[ERROR]}
        )

    obtain_player = getPlayer(request.authToken)
    if obtain_player[SUCCESS] == False:
        return JSONResponse(
            status_code = obtain_player[HTTP_CODE],
            content={"message":obtain_player[ERROR]}
        )

    player:Login = obtain_player[PLAYER]

    all_contracts_query = select(StartedContract).where(StartedContract.idPlayer == player.idPlayer)
    result = session.exec(all_contracts_query) 
    all_contracts = result.fetchall()

    started = len(all_contracts)
    completed = len([c for c in all_contracts if c.redeemed])
    successful = len([c for c in all_contracts if c.redeemed and c.successful])

    return JSONResponse(
        status_code = HTTP_200_OK,
        content={"started":started, "completed": completed, "successful": successful}
    )
