from datetime import datetime, timedelta
from random import Random
import time
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
from Models.bandit import BanditData, BanditFreezeRequest, BanditRepsonse
from routes.middlewares.exp import getLevel

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
        if result.expireTime > datetime.now(): #expired
              banditIstance = select(BanditIstance,Bandit).where(
                   and_(BanditIstance.idRequest==result.id,
                        BanditIstance.idBandit==Bandit.id,
                        BanditIstance.defeated==False
                    )
                )
              bandits = session.exec(banditIstance).fetchall()
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
    pools = select(BanditPool).where(BanditPool.levelRequired <= level)
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
async def tokenLogin(request: AuthRequestWithToken):
    return []
