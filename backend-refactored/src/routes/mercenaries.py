from math import floor
import random
from time import time
from fastapi import APIRouter
from fastapi.responses import JSONResponse
from fastapi.encoders import jsonable_encoder

from sqlmodel import Session, delete,select,and_,func, update
from starlette.status import *
from bcrypt import *
from uuid_utils import *

from MySql import SessionManager
from MySql.tables import *
from Models.mercenaries import *
from Models.commons import*
from routes.middlewares.getPlayer import *
from routes.middlewares.exp import *
from routes.middlewares.key_names import *
from routes.middlewares.checkAuthTokenExpiration import *

router = APIRouter(
    prefix="/mercenaries",
    tags=["mercenaries"]
)

session = SessionManager.global_session

#routes
@router.post("/hireable")
async def get_available(request:BasicAuthTokenRequest):
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
    playerInfo:Player = getPlayerData(player,session)[PLAYER]
    playerLevel = getLevel(playerInfo.exp,session)
    employed_mercenaries = select(Mercenary.id).where(
         and_(
              EmployedMercenary.idMercenary == Mercenary.id,
              EmployedMercenary.idPlayer == player.idPlayer
         )
    )

    availables = select(Mercenary).where(
        and_(~Mercenary.id.in_(employed_mercenaries.scalar_subquery()),
             Mercenary.requiredLevel <= playerLevel
        )
    )

    result = session.exec(availables)
    mercenaries = result.fetchall()

    response = AvaliableResponse(mercenaries=[BuyableMercenary(cost=x.employmentCost,name=x.name,power=x.power) for x in mercenaries])

    return JSONResponse(
         status_code= HTTP_200_OK,
         content=jsonable_encoder(response)
    )
    
@router.post("/employ")
async def employ(request:EmployRequest):
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
    playerInfo:Player = getPlayerData(player,session)[PLAYER]
    playerLevel = getLevel(playerInfo.exp,session)
    employed_mercenaries = select(Mercenary.id).where(
         and_(
              EmployedMercenary.idMercenary == Mercenary.id,
              EmployedMercenary.idPlayer == player.idPlayer
         )
    )

    availables = select(Mercenary).where(
        and_(~Mercenary.id.in_(employed_mercenaries.scalar_subquery()),
              Mercenary.id == request.idMercenary
        )
    )

    result = session.exec(availables)
    mercenary = result.first()

    if mercenary == None:
         return JSONResponse(status_code=HTTP_400_BAD_REQUEST,content={"message":"Mercenary does not exist or is already employed"})

    if mercenary.requiredLevel > playerLevel:
         return JSONResponse(status_code=HTTP_401_UNAUTHORIZED,content={"message":"Player level too low to buy this mercenary"})
    
    if mercenary.employmentCost > playerInfo.money:
         return JSONResponse(status_code=HTTP_402_PAYMENT_REQUIRED,content={"message":"Not enough money to buy this mercenary"})
    
    #add via transaction

    try:
        employment = EmployedMercenary(id=None,idPlayer=player.idPlayer,idMercenary=mercenary.id)
        session.add(employment)
        playerInfo.money-=mercenary.employmentCost
        session.commit()
    except:
        session.rollback()
        return JSONResponse(status_code=HTTP_500_INTERNAL_SERVER_ERROR,content={"message":"Error occured while trying to buy this element"})
    
    response = EmployResponse(idMercenary=mercenary.id)
    return JSONResponse(status_code=HTTP_200_OK,content=jsonable_encoder(response))


@router.post("/player/all")
async def get_available(request:BasicAuthTokenRequest):
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
    playerInfo:Player = getPlayerData(player,session)[PLAYER]

    employed_mercenaries = select(Mercenary,EmployedMercenary).where(
         and_(
              EmployedMercenary.idMercenary == Mercenary.id,
              EmployedMercenary.idPlayer == player.idPlayer
         )
    )

    result = session.exec(employed_mercenaries)
    mercenaries = result.fetchall()

    response = PlayerAllResponse(mercenaries=[BaseMercenaryInfo(name=x.name,power=x.power) for x,y in mercenaries])

    return JSONResponse(
         status_code= HTTP_200_OK,
         content=jsonable_encoder(response)
    )


@router.post("/player/unassigned")
async def get_available(request:BasicAuthTokenRequest):
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

    active_mercenaries_id = select(AssignedMercenary.idEmployedMercenary).distinct().where(
         and_(ActiveContract.idContract == Contract.id,
              ActiveContract.id==AssignedMercenary.idActiveContract,
              AssignedMercenary.idEmployedMercenary==EmployedMercenary.id,
              EmployedMercenary.idMercenary == Mercenary.id,
              EmployedMercenary.idPlayer==player.idPlayer))
    
    unassigned_mercenaries = select(Mercenary,EmployedMercenary).distinct().where(
                 and_(~EmployedMercenary.id.in_(active_mercenaries_id.scalar_subquery()),
                 EmployedMercenary.idPlayer == player.idPlayer,
                 EmployedMercenary.idMercenary == Mercenary.id
        )
    )

    result = session.exec(unassigned_mercenaries)
    mercenaries = result.fetchall()

    response = UnassignedResponse(mercenaries=[BaseMercenaryInfo(name=x.name,power=x.power) for x,y in mercenaries])

    return JSONResponse(
         status_code= HTTP_200_OK,
         content=jsonable_encoder(response)
    )