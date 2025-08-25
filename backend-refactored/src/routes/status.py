import time
from fastapi import APIRouter
from fastapi.exceptions import RequestValidationError
from fastapi.responses import JSONResponse
from fastapi.encoders import jsonable_encoder

from sqlmodel import Session,select
from starlette.status import *
from bcrypt import *
from uuid_utils import *

from MySql import SessionManager, connection
from MySql.tables import *
from Models.auth import *
import json
from routes.middlewares.key_names import *
from routes.middlewares.checkAuthTokenExpiration import *
from Models.commons import BasicAuthTokenRequest
from routes.middlewares.getPlayer import *
from routes.middlewares.key_names import *
router = APIRouter(
    prefix="/status",
    tags=["status"]
)

session = SessionManager.global_session

#routes
@router.post("/")
async def status(request: BasicAuthTokenRequest):

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
    
    playerData = getPlayerData(obtain_player[PLAYER])

    if playerData[SUCCESS] == False:
            return JSONResponse(
            status_code = playerData[HTTP_CODE],
            content={"message":playerData[ERROR]}
        )

    return JSONResponse(
         status_code = HTTP_200_OK,
         content = jsonable_encoder(playerData[PLAYER])
    )

@router.get("/levels")
async def levels():
    level_query = select(Level) # order by Level.level
    result = session.execute(level_query).all()

    return JSONResponse(
        status_code = HTTP_200_OK,
        content=[ item[0].expRequired for item in result]
    )

@router.get("/baseStats")
async def stats():
    query = select(BaseStats) # order by Level.level
    result = session.exec(query).all()

    return JSONResponse(
        status_code = HTTP_200_OK,
        content=jsonable_encoder(result)
    )
