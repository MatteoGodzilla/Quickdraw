import time
from fastapi import APIRouter
from fastapi.exceptions import RequestValidationError
from fastapi.responses import JSONResponse
from fastapi.encoders import jsonable_encoder

from sqlmodel import Session,select,and_
from starlette.status import *
from bcrypt import *
from uuid_utils import *

from MySql import SessionManager, connection
from MySql.tables import *
from Models.contracts import *
from Models.commons import*
import json
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
    active_contracts = select(ActiveContract,Contract).where(
         and_(ActiveContract.id==AssignedMercenary.idActiveContract,AssignedMercenary.idEmployedMercenary==EmployedMercenary.idMercenary,EmployedMercenary.idPlayer==player.idPlayer))
    
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
async def redeem():
    return 0

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
         and_(ActiveContract.id==AssignedMercenary.idActiveContract,AssignedMercenary.idEmployedMercenary==EmployedMercenary.idMercenary,EmployedMercenary.idPlayer==player.idPlayer))
    
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