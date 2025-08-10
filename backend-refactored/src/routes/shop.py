from fastapi import APIRouter
from fastapi.encoders import jsonable_encoder
from fastapi.responses import JSONResponse
from MySql import SessionManager
from MySql.tables import *
from Models.commons import BasicAuthTokenRequest, BuyRequest
from routes.middlewares.checkAuthTokenExpiration import checkAuthTokenValidity 
from routes.middlewares.key_names import SUCCESS, ERROR, HTTP_CODE, PLAYER
from routes.middlewares.getPlayer import getPlayer, getPlayerData
from sqlalchemy import and_, or_, func, join
from sqlmodel import Session, select
from starlette.status import *

from Models.shop import *

session = SessionManager.global_session

router = APIRouter(
    prefix="/shop",
    tags=["shop"]
)

@router.post("/weapons")
async def weapons(request: BasicAuthTokenRequest):
    # Copy-pasted from contracts/active
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
   
    available_weapons = select(Weapon).where(Weapon.id.not_in(
        select(PlayerWeapon.idWeapon).where(PlayerWeapon.idPlayer == player.idPlayer)
    ))

    result = session.exec(available_weapons) 
    response_weapons = []
    for w in result.fetchall():
        response_weapons.append({
            "id": w.id,
            "name" : w.name,
            "damage" : w.damage,
            "cost" : w.cost
            # bullet type
        })

    return JSONResponse(
        status_code = HTTP_200_OK,
        content=response_weapons
    )

@router.post("/bullets")
async def bullets(request: BasicAuthTokenRequest):
    # Copy-pasted from above 
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
   
    available_bullets = select(BulletShop,Bullet).where(BulletShop.idBullet == Bullet.type)

    result = session.exec(available_bullets) 
    response_bullets = []
    for bulletShop, bullet in result.fetchall():
        response_bullets.append({
            "id": bulletShop.id,
            "type": bullet.type, 
            "name" : bullet.description,
            "cost" : bulletShop.cost,
            "quantity" : bulletShop.quantity,
            "capacity" : bullet.capacity,
        })

    return JSONResponse(
        status_code = HTTP_200_OK,
        content=response_bullets
    )

@router.post("/medikits")
async def bullets(request: BasicAuthTokenRequest):
    # Copy-pasted from above 
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
   
    available_medikits = select(MedikitShop,Medikit).where(MedikitShop.idMedikit == Medikit.id)

    result = session.exec(available_medikits) 
    response_medikits = []
    for medikitShop, medikit in result.fetchall():
        response_medikits.append({
            "id": medikitShop.id,
            "idMedikit": medikit.id,
            "description" : medikit.description,
            "healthRecover": medikit.healthRecover,
            "cost" : medikitShop.cost,
            "quantity" : medikitShop.quantity,
            "capacity" : medikit.capacity,
        })

    return JSONResponse(
        status_code = HTTP_200_OK,
        content=response_medikits
    )

@router.post("/upgrades")
async def bullets(request: BasicAuthTokenRequest):
    # Copy-pasted from above 
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
  
    #SELECT us.idUpgrade, us.type, min(us.level), us.cost FROM UpgradeShop us 
    #LEFT JOIN (
    #	SELECT type as t, max(level) as maxLevel FROM PlayerUpgrade pu
	#    JOIN UpgradeShop us ON pu.idUpgrade = us.idUpgrade
	#    WHERE pu.idPlayer = 1
	#    GROUP BY type
    #) as pml ON us.type = pml.t
    #WHERE pml.maxLevel IS NULL OR us.level = pml.maxLevel + 1
    #GROUP BY us.type

    player_max_upgrades = select(UpgradeShop.type.label("type"), func.max(UpgradeShop.level).label("maxLevel")).where(and_(
            PlayerUpgrade.idPlayer == player.idPlayer,
            PlayerUpgrade.idUpgrade == UpgradeShop.idUpgrade
        )).group_by(UpgradeShop.type).subquery()

    available_upgrades = select(
            UpgradeShop.idUpgrade,
            UpgradeShop.type,
            func.min(UpgradeShop.level),
            UpgradeShop.cost,
            UpgradeTypes.description
        ).select_from(
            join(UpgradeShop, player_max_upgrades, UpgradeShop.type == player_max_upgrades.c.type, isouter = True)
        ).where(or_(
            player_max_upgrades.c.maxLevel == None,
            UpgradeShop.level == player_max_upgrades.c.maxLevel + 1,
        )).where(
            UpgradeTypes.id == UpgradeShop.type
        ).group_by(UpgradeShop.type)

    result = session.exec(available_upgrades)
    response_upgrades = []
    for upgrade_id, upgrade_type, level, cost, description in result.fetchall():
        response_upgrades.append({
            "id": upgrade_id,
            "type": upgrade_type,
            "description": description,
            "level": level,
            "cost": cost,
        })

    return JSONResponse(
        status_code = HTTP_200_OK,
        content=response_upgrades
    )


@router.post("/bullets/buy")
async def buyBullets(request: BuyRequest):
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

    obtain_bullet = select(Bullet,BulletShop).where(
         and_(
              BulletShop.id == request.id,
              BulletShop.idBullet == Bullet.type
         )
    )

    result = session.exec(obtain_bullet)
    bulletInfo = result.first()

    #bullet does not exist
    if bulletInfo == None or bulletInfo[1]==None:
        return JSONResponse(
            status_code = HTTP_406_NOT_ACCEPTABLE,
            content={"message":"Required bullet sale does not exist"}
        )
    
    playerInfo = getPlayerData(player,session)
    if playerInfo[SUCCESS] == False:
        return JSONResponse(
            status_code = HTTP_500_INTERNAL_SERVER_ERROR,
            content={"message":"Could not find player data"}
        )
    
    #too expensive 
    playerInfo:Player = playerInfo[PLAYER]
    if bulletInfo[1].cost > playerInfo.money:
        return JSONResponse(
            status_code = HTTP_402_PAYMENT_REQUIRED,
            content={"message":"Insuffucient founds"}
        )
    
    #check if player already has bullet or not
    getBullet = select(Bullet,PlayerBullet).where(
         and_(
              PlayerBullet.idPlayer==playerInfo.id,
              PlayerBullet.idBullet==bulletInfo[0].type
        )
    )

    result = session.exec(getBullet)
    bulletPlayer = result.first()

    #case 1: player does not already own bullet:
    if bulletPlayer==None:
        try:
            playerInfo.money-=bulletInfo[1].cost
            playerRow = PlayerBullet(idPlayer=player.idPlayer,idBullet=bulletInfo[1].idBullet,amount=bulletInfo[1].quantity)
            session.add(playerRow)
            session.commit()
        except:
            session.rollback()
            return JSONResponse(status_code=HTTP_500_INTERNAL_SERVER_ERROR,content={"message":"error while doing purchase"})
    #case 2: player is not buying for the first time
    else:
        try:
            playerInfo.money-=bulletInfo[1].cost
            bulletPlayer[1].amount = min(bulletPlayer[1].amount+bulletInfo[1].quantity,bulletInfo[0].capacity)
            session.commit()
        except Exception as e:
            session.rollback()
            return JSONResponse(status_code=HTTP_500_INTERNAL_SERVER_ERROR,content={"message":"error while doing purchase"})

    #By passing directly bulletInfo[1] it returns an array...
    response = BuyBulletResponse(id = bulletInfo[1].id,type=bulletInfo[1].idBullet,name=bulletInfo[0].description,cost = bulletInfo[1].cost,quantity=bulletInfo[1].quantity,capacity=bulletInfo[0].capacity)
    return JSONResponse(status_code=HTTP_200_OK,content=jsonable_encoder(response))
    
         

@router.post("/medikits/buy")
async def buyMedikit(request: BuyRequest):
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
    obtain_medikit = select(Medikit,MedikitShop).where(
         and_(
              MedikitShop.id == request.id,
              MedikitShop.idMedikit == Medikit.id
         )
    )

    result = session.exec(obtain_medikit)
    medikitInfo = result.first()

    #medikit does not exist
    if medikitInfo == None or medikitInfo[1]==None:
        return JSONResponse(
            status_code = HTTP_406_NOT_ACCEPTABLE,
            content={"message":"Required medikit sale does not exist"}
        )
    
    playerInfo = getPlayerData(player,session)
    if playerInfo[SUCCESS] == False:
        return JSONResponse(
            status_code = HTTP_500_INTERNAL_SERVER_ERROR,
            content={"message":"Could not find player data"}
        )
    
    #too expensive 
    playerInfo:Player = playerInfo[PLAYER]
    if medikitInfo[1].cost > playerInfo.money:
        return JSONResponse(
            status_code = HTTP_402_PAYMENT_REQUIRED,
            content={"message":"Insuffucient founds"}
        )
    
    #check if player already has bullet or not
    getMedikit = select(Medikit,PlayerMedikit).where(
         and_(
              PlayerMedikit.idPlayer==playerInfo.id,
              PlayerMedikit.idMediKit==medikitInfo[0].id
        )
    )

    result = session.exec(getMedikit)
    medikitPlayer = result.first()

    #case 1: player does not already own medikit:
    if medikitPlayer==None:
        try:
            playerInfo.money-=medikitInfo[1].cost
            playerRow = PlayerMedikit(idPlayer=player.idPlayer,idMediKit=medikitInfo[1].idMedikit,amount=medikitInfo[1].quantity)
            session.add(playerRow)
            session.commit()
        except:
            session.rollback()
            return JSONResponse(status_code=HTTP_500_INTERNAL_SERVER_ERROR,content={"message":"error while doing purchase"})
    #case 2: player is not buying for the first time
    else:
        try:
            playerInfo.money-=medikitInfo[1].cost
            medikitPlayer[1].amount = min(medikitPlayer[1].amount+medikitPlayer[1].amount,medikitPlayer[0].capacity)
            session.commit()
        except Exception as e:
            session.rollback()
            return JSONResponse(status_code=HTTP_500_INTERNAL_SERVER_ERROR,content={"message":"error while doing purchase"})


    response = BuyMedikitResponse(
        id = medikitInfo[1].id,
        idMedikit=medikitInfo[1].idMedikit,
        description=medikitInfo[0].description,
        healthRecover=medikitInfo[0].healthRecover,
        cost = medikitInfo[1].cost,
        quantity=medikitInfo[1].quantity,
        capacity=medikitInfo[0].capacity
    )
    return JSONResponse(status_code=HTTP_200_OK,content=jsonable_encoder(response))


@router.post("/weapons/buy")
async def buyWeapon(request: BuyRequest):
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
    obtain_weapon = select(Weapon).where(
         and_(
              Weapon.id == request.id
         )
    )

    result = session.exec(obtain_weapon)
    weaponInfo = result.first()

    #weapon does not exist
    if weaponInfo==None:
        return JSONResponse(
            status_code = HTTP_406_NOT_ACCEPTABLE,
            content={"message":"Required weapon sale does not exist"}
        )
    
    playerInfo = getPlayerData(player,session)
    if playerInfo[SUCCESS] == False:
        return JSONResponse(
            status_code = HTTP_500_INTERNAL_SERVER_ERROR,
            content={"message":"Could not find player data"}
        )
    
    #too expensive 
    playerInfo:Player = playerInfo[PLAYER]
    if weaponInfo.cost > playerInfo.money:
        return JSONResponse(
            status_code = HTTP_402_PAYMENT_REQUIRED,
            content={"message":"Insuffucient founds"}
        )
    
    #check if player already has bullet or not
    getWeapon = select(Weapon,PlayerWeapon).where(
         and_(
              PlayerWeapon.idWeapon==weaponInfo.id,
              PlayerWeapon.idPlayer==player.idPlayer
        )
    )

    result = session.exec(getWeapon)
    weaponPlayer = result.first()

    #case 1: player does not already own weapon:
    if weaponPlayer==None:
        try:
            playerInfo.money-=weaponInfo.cost
            playerRow = PlayerWeapon(idPlayer=player.idPlayer,idWeapon=weaponInfo.id)
            session.add(playerRow)
            session.commit()
        except:
            session.rollback()
            return JSONResponse(status_code=HTTP_500_INTERNAL_SERVER_ERROR,content={"message":"error while doing purchase"})
    #case 2: player already owns weapon,not allowed
    else:
        return JSONResponse(status_code=HTTP_406_NOT_ACCEPTABLE,content={"message":"Player already owns the weapon"})


    response = BuyWeaponResponse(
        id = weaponInfo.id,
        name = weaponInfo.name,
        damage= weaponInfo.damage,
        cost = weaponInfo.cost
    )
    return JSONResponse(status_code=HTTP_200_OK,content=jsonable_encoder(response))