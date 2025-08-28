from datetime import datetime, timedelta
from random import Random
import time
from typing import Counter
from fastapi import APIRouter
from fastapi.exceptions import RequestValidationError
from fastapi.responses import JSONResponse
from fastapi.encoders import jsonable_encoder

from sqlmodel import Session, and_,select
from starlette.status import *
from bcrypt import *
from uuid_utils import *

from MySql import SessionManager, connection
from MySql.tables import *
from Models.auth import *
import json
from routes.middlewares.key_names import *
from routes.middlewares.checkAuthTokenExpiration import *
from routes.middlewares.getPlayer import *
from Models.commons import BasicAuthTokenRequest
from routes.middlewares.getPlayer import *
from Models.bandit import BanditData, BanditFreezeRequest, BanditRepsonse, FightRequest, FightRewards
from routes.middlewares.exp import getLevel
from routes.middlewares.boosts import *

router = APIRouter(
    prefix="/bandit",
    tags=["bandit"]
)

POOL_REQUEST_LOOP_TIME = timedelta(minutes=30)
session = SessionManager.global_session

#routes
@router.post("/pool")
async def pool(request: BasicAuthTokenRequest):
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
    
    loginInfo:Login = obtain_player[PLAYER]
    select_request = select(PoolRequest).where(PoolRequest.idPlayer==loginInfo.idPlayer)
    result:PoolRequest = SessionManager.safe_exec(select_request).first()
    newPool = PoolRequest(idPlayer=loginInfo.idPlayer,expireTime=datetime.now()+POOL_REQUEST_LOOP_TIME)

    if result != None:
        newPool.id = result.id
        if result.expireTime > datetime.now(): #not expired
              banditIstance = select(BanditIstance,Bandit).where(
                   and_(BanditIstance.idRequest==result.id,
                        BanditIstance.idBandit==Bandit.id,
                        BanditIstance.defeated==False
                    )
                )
              #if there is a frozen bandit,it must be unfrozen
              bandits = session.exec(banditIstance).fetchall()
              for x,y in bandits:
                   x.frozen = False
              session.commit()

              return JSONResponse(
                   status_code=HTTP_200_OK,
                   content=jsonable_encoder([
                        BanditRepsonse(expires=result.expireTime,idIstance=x.id,
                            stats=BanditData(name=y.name,hp=y.hp,minDamage=y.minDamage,maxDamage=y.maxDamage,minSpeed=y.minSpeed,maxSpeed=y.maxSpeed)) for x,y in bandits
                   ])
              )
        else:
                toEliminate = select(BanditIstance).where(
                   BanditIstance.idRequest==result.id
                )
                bandits = session.exec(toEliminate).fetchall()
                result.expireTime = newPool.expireTime
                for bandit in bandits:
                    if bandit.frozen==False:
                        session.delete(bandit)
                session.flush()
    else:
        #add request first
        session.add(newPool)
        session.flush()

    #at this point in the code,the player should have a fresh new pool request with no bandit istances associated
    #first we need the player level
    playerInfo:Player = getPlayerData(loginInfo)[PLAYER]
    level = getLevel(playerInfo.exp)
    response = []

    #select all pools allowed
    pools = select(BanditPool).where(BanditPool.levelRequired == level)
    poolResult = session.exec(pools).fetchall()
    #generate the spawn chance and the random numbers
    for pool in poolResult:
        rand = Random()
        spawn = rand.randint(pool.minSpawn,pool.maxSpawn)
        spawnNumber = rand.randint(1,100)
        if spawnNumber<=pool.spawnChance:
             #successful spawn
             getBandit = select(Bandit).where(Bandit.id==pool.banditId)
             res = session.exec(getBandit).first()
             for i in range(spawn):
                  print(res.id)
                  band = BanditIstance(idBandit=pool.banditId,idRequest=newPool.id,defeated=False,fronze=False)
                  session.add(band)
                  session.flush()
                  responseBandit = BanditData(name=res.name,hp=res.hp,minDamage=res.minDamage,maxDamage=res.maxDamage,minSpeed=res.minSpeed,maxSpeed=res.maxSpeed)
                  response.append(BanditRepsonse(expires=newPool.expireTime,idIstance=band.id,stats=responseBandit))
                  session.flush()

    session.commit()

    return JSONResponse(
         status_code=HTTP_200_OK,
         content = jsonable_encoder(response)
    )
              
              
@router.post("/freeze")
async def freeze(request: BanditFreezeRequest):
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
    loginInfo:Login=obtain_player[PLAYER]
    
    #check a pool request exists
    getRequest = select(PoolRequest).where(PoolRequest.idPlayer==loginInfo.idPlayer)
    pool:PoolRequest = session.exec(getRequest).first()
    if pool==None:
         return JSONResponse(status_code=HTTP_403_FORBIDDEN,content={"message":"player has no bandits associated"})
    
    banditIstance = select(BanditIstance).where(
         and_(
              BanditIstance.id==request.idIstance,
              BanditIstance.idRequest == pool.id
         )
    )
    bandit = session.exec(banditIstance).first()
    if bandit==None:
         return JSONResponse(status_code=HTTP_403_FORBIDDEN,content={"message":"bandit not found"})
    
    #check if another bandit is frozen and unfreeze it if expired
    froze_bandits = select(BanditIstance).where(
         and_(
              BanditIstance.id != bandit.id,
              BanditIstance.idRequest == pool.id,
              BanditIstance.frozen == True
         )
    )

    result = session.exec(froze_bandits).fetchall()
    for x in result:
         x.frozen = False
         session.commit()
    
    bandit.frozen = True
    session.commit()

    return JSONResponse(status_code=HTTP_200_OK,content={"success":True})
    

@router.post("/fight")
async def tokenLogin(request: FightRequest):
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
    loginInfo:Login=obtain_player[PLAYER]

    #check request isn't expired 
    pool = select(PoolRequest).where(PoolRequest.idPlayer == loginInfo.idPlayer)
    poolResult = session.exec(pool).first()

    banditInfo = select(BanditIstance,Bandit).where(and_(BanditIstance.id == request.idIstance,Bandit.id==BanditIstance.idBandit))
    bandit = session.exec(banditInfo).first()
    
    #check bandit exist
    if bandit==None:
         return JSONResponse(status_code=HTTP_400_BAD_REQUEST,content={"message":"Given bandit istance does not exist"})
    
    if poolResult == None or (poolResult.expireTime < datetime.now() and bandit[0].frozen==False):
         return JSONResponse(status_code=HTTP_400_BAD_REQUEST,content={"message":"Player has invalid pool request associated"})

    
    #check not already defeated
    if bandit[0].defeated:
         return JSONResponse(status_code=HTTP_400_BAD_REQUEST,content={"message":"Given bandit istance does already defeated"})

    #check consistency of weapon data:
    #1) check all weapons used belong to player (and map weapons damage)
    weaponIds = set([x.idWeapon for x in request.fights])
    playerWeapons = select(PlayerWeapon,Weapon).where(and_(PlayerWeapon.idPlayer==loginInfo.idPlayer,PlayerWeapon.idWeapon==Weapon.id))
    resultWeapon = session.exec(playerWeapons).fetchall()
    
    idPossessed = [x.idWeapon for x,y in resultWeapon]
    
    weaponDamage = {}

    for x in weaponIds:
         if not x in idPossessed:
            return JSONResponse(status_code=HTTP_401_UNAUTHORIZED,content={"message":"weapon usage not matching player possession"})

    for pw,w in resultWeapon:
         weaponDamage[pw.idWeapon] = w.damage

    #2) check bullet usage (and update without commit)
    playerBullets = select(PlayerBullet,Bullet).where(and_(PlayerBullet.idBullet==Bullet.type,PlayerBullet.idPlayer==loginInfo.idPlayer))
    result = session.exec(playerBullets).fetchall()

    map_bullets = {}
    map_weapons = {}
    badResponse = JSONResponse(status_code=HTTP_401_UNAUTHORIZED,content={"message":"bullet possession inconsistent with given fight"})

    weapon_to_bullet = {wp.idWeapon:w.bulletType for wp,w in resultWeapon}

    for pb,b in result:
         map_bullets[b.type] = pb
    for w,pw in resultWeapon:
         map_weapons[w.idWeapon] = pw


    for x in request.fights:
        if not weapon_to_bullet[x.idWeapon] in map_bullets.keys():
             return badResponse
        map_bullets[weapon_to_bullet[x.idWeapon]].amount-=map_weapons[x.idWeapon].bulletsShot
        
        if map_bullets[weapon_to_bullet[x.idWeapon]].amount<0:
            return badResponse

    #Damage calculation
    damageToPlayer = 0
    damageToBandit = 0

    for round in request.fights:
        if round.wins:
             damageToBandit+=weaponDamage[round.idWeapon]
        else:
             #check damage is in range:
             damageToPlayer+=min(bandit[1].maxDamage,max(round.banditDamage,bandit[1].minDamage))
        
         
    bandit[0].frozen = False
    #determine result: player wins (give reward), bandit wins (player goes to 10 hp), or incomplete battle (nothing happens,no changes happen)
    playerInfo:Player = getPlayerData(loginInfo)[PLAYER]
    if playerInfo.health <= damageToPlayer:
         playerInfo.health = 10
         session.commit()
         return JSONResponse(status_code=HTTP_200_OK,content=jsonable_encoder(FightRewards(money=0,exp=0)))
    if bandit[1].hp <= damageToBandit:
         #generate prizes 
         rand = Random()
         moneyPrize = rand.randint(bandit[1].minMoney,bandit[1].maxMoney)
         expPrize = rand.randint(bandit[1].minExp,bandit[1].maxExp)
         corrected_money = boostedMoney(moneyPrize,playerInfo)
         corrected_exp = boostedExp(expPrize,playerInfo)
         print(corrected_money,corrected_exp)
         playerInfo.money+= corrected_money
         playerInfo.exp+= corrected_exp
         playerInfo.money+= corrected_money
         playerInfo.health-=damageToPlayer
         bandit[0].defeated = True
         session.commit()
         return JSONResponse(status_code=HTTP_200_OK,content=jsonable_encoder(FightRewards(money=corrected_money,exp=corrected_exp)))
    
    #in this scenario fight was probably interrupted, player loses bullets 
    #TODO: if player has 0 bullets,then they actually have lost,save changes only in that case
    session.commit()
    return JSONResponse(status_code=HTTP_200_OK,content=jsonable_encoder(FightRewards(money=0,exp=0)))