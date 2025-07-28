import time
from fastapi import APIRouter
from fastapi.encoders import jsonable_encoder
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
    response_upgrades = []
    result = session.exec(upgrades_query)
    upgrades = result.fetchall()
    for playerUpgrade,upgradeShop,upgradeType in upgrades:
        response_object = InventoryResponseUpgrade(
             idUpgrade = playerUpgrade.idUpgrade,description = upgradeType.description, value = upgradeShop.value,type= upgradeShop.type)
        response_upgrades.append(response_object)
    #obtain weapons
    weapon_query = select (PlayerWeapon,Weapon).where(PlayerWeapon.idPlayer == player.id and PlayerWeapon.idWeapon == Weapon.id)
    result = session.exec(weapon_query)
    weapons = result.fetchall()
    response_weapons = []
    for playerWeapon,weapon in weapons:
        response_object = InventoryResponseWeapon(name=weapon.name, damage=weapon.damage, cost = weapon.cost, bulletType=weapon.bulletType)
        response_weapons.append(response_object)
    #obtain medkits
    #obtain bullets
    return JSONResponse(
         code = HTTP_200_OK,
         content={
              "upgrades":jsonable_encoder(response_upgrades),
              "weapons":jsonable_encoder(response_weapons)
         }
    )