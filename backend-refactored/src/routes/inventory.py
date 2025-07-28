import time
from fastapi import APIRouter
from fastapi.encoders import jsonable_encoder
from fastapi.exceptions import RequestValidationError
from fastapi.responses import JSONResponse

from sqlmodel import Session, and_,select
from starlette.status import *
from bcrypt import *
from uuid_utils import *

from MySql import SessionManager, connection
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

#routes
@router.post("")
async def get_inventory(request: InventoryRequest):
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
    #obtain upgrades
    upgrades_query = select(PlayerUpgrade,UpgradeShop,UpgradeTypes
                            ).where(and_(PlayerUpgrade.idUpgrade == UpgradeShop.idUpgrade,
                                    UpgradeShop.idUpgrade == UpgradeTypes.id,
                                    PlayerUpgrade.idPlayer == player.idPlayer))
    response_upgrades = []
    result = SessionManager.global_session.exec(upgrades_query)
    upgrades = result.fetchall()
    for playerUpgrade,upgradeShop,upgradeType in upgrades:
        response_object = InventoryResponseUpgrade(
             idUpgrade = playerUpgrade.idUpgrade,description = upgradeType.description, type= upgradeShop.type, level = upgradeShop.level)
        response_upgrades.append(response_object)
    #obtain weapons
    weapon_query = select (PlayerWeapon,Weapon).where(and_(PlayerWeapon.idPlayer == player.idPlayer,PlayerWeapon.idWeapon == Weapon.id))
    result = SessionManager.global_session.exec(weapon_query)
    weapons = result.fetchall()
    response_weapons = []
    for playerWeapon,weapon in weapons:
        response_object = InventoryResponseWeapon(name=weapon.name, damage=weapon.damage, cost = weapon.cost, bulletType=weapon.bulletType)
        response_weapons.append(response_object)
    #obtain medkits
    medkit_query  = select (PlayerMedikit,Medikit).where(and_(PlayerMedikit.idPlayer == player.idPlayer , Medikit.id == PlayerMedikit.idMediKit))
    result = SessionManager.global_session.exec(medkit_query)
    medkits = result.fetchall()
    response_medkits = []
    for playerMedkit,medkit in medkits:
        response_object = InventoryResponseMedkit(healthRecover=medkit.healthRecover, capacity=medkit.capacity, description = medkit.description)
        response_medkits.append(response_object)
    #obtain bullets
    bullets_query  = select (PlayerBullet,Bullet).where(and_(PlayerBullet.idPlayer == player.idPlayer , Bullet.type == PlayerBullet.idBullet))
    result = SessionManager.global_session.exec(bullets_query)
    bullets = result.fetchall()
    response_bullets = []
    for playerBullet,bullet in bullets:
        response_object = InventoryResponseBullet(type=playerBullet.idBullet, capacity=bullet.capacity, description = bullet.description)
        response_bullets.append(response_object)

    #response
    return JSONResponse(
         status_code = HTTP_200_OK,
         content={
              "upgrades":jsonable_encoder(response_upgrades),
              "weapons":jsonable_encoder(response_weapons),
              "bullets":jsonable_encoder(response_bullets),
              "medikits":jsonable_encoder(response_medkits)
         }
    )