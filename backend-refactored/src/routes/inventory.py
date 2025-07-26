import time
from fastapi import APIRouter
from fastapi.exceptions import RequestValidationError
from fastapi.responses import JSONResponse

from sqlmodel import Session,select
from starlette.status import *
from bcrypt import *
from uuid_utils import *

from MySql import connection
from MySql.tables import *
from Models.inventory import *
from routes.middlewares.key_names import *
from routes.middlewares.checkAuthTokenExpiration import *
from routes.middlewares.getPlayer import *
import json

router = APIRouter(
    prefix="/inventory",
    tags=["inventory"]
)

engine = connection.create_db_connection()
session = Session(engine)

#routes
@router.post("/inventory")
async def get_inventory(request: InventoryRequest):
    check_token = checkAuthTokenValidity(request.authToken)
    if check_token[SUCCESS] == False:
        return JSONResponse(
            code = check_token[HTTP_CODE],
            content={"message":check_token[ERROR]}
        )
    
    obtain_player = getPlayer(request.authToken,session)
    if obtain_player[SUCCESS] == False:
            return JSONResponse(
            code = obtain_player[HTTP_CODE],
            content={"message":obtain_player[ERROR]}
        )
    
    player:Player = obtain_player[PLAYER]
    #obtain upgrades
    upgrades_query = select(PlayerUpgrade,UpgradeShop,UpgradeTypes
                            ).where(PlayerUpgrade.idUpgrade == UpgradeShop.idUpgrade and 
                                    UpgradeShop.type == UpgradeTypes.id and 
                                    PlayerUpgrade.idPlayer == player.id)
    
    result = session.exec(upgrades_query)
    upgrades = result.fetchall()
    #obtain weapons
    #obtain medkits
    #obtain bullets