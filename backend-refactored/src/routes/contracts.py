import time
from fastapi import APIRouter
from fastapi.exceptions import RequestValidationError
from fastapi.responses import JSONResponse
from fastapi.encoders import jsonable_encoder

from sqlmodel import Session,select,and_
from starlette.status import *
from bcrypt import *
from uuid_utils import *

from MySql import SessionManager
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


#routes
@router.post("/active")
async def get_actives(request:basicAuthTokenRequest):
    check_token = checkAuthTokenValidity(request.authToken)
    if check_token[SUCCESS] == False:
        return JSONResponse(
            status_code = check_token[HTTP_CODE],
            content={"message":check_token[ERROR]}
        )

    obtain_player = getPlayer(request.authToken,SessionManager.global_session)
    if obtain_player[SUCCESS] == False:
            return JSONResponse(
            status_code = obtain_player[HTTP_CODE],
            content={"message":obtain_player[ERROR]}
        )

    player:Login = obtain_player[PLAYER]


@router.post("/redeem")
async def redeem():
    return 0

@router.post("/available")
async def get_availables(request:basicAuthTokenRequest):
    return 0