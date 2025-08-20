from fastapi import APIRouter
from Models.consumables import UseMedikitRequest
from routes.middlewares.checkAuthTokenExpiration import checkAuthTokenValidity 
from routes.middlewares.key_names import SUCCESS, ERROR, HTTP_CODE, PLAYER
from routes.middlewares.getPlayer import getPlayer, getPlayerData
from sqlmodel import Session, select, and_, func, update, delete
from MySql.SessionManager import safe_exec
from MySql.tables import PlayerMedikit, Medikit, Player, UpgradeShop, BaseStats, PlayerUpgrade
from fastapi.responses import JSONResponse
from starlette.status import *
from MySql import SessionManager

session = SessionManager.global_session

router = APIRouter(
    prefix="/use",
    tags=["use"]
)

@router.post("/medikit")
async def useMedikit(request: UseMedikitRequest):
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

    print(playerData)
    print(request.type)

    amount_query = select(PlayerMedikit, Medikit, Player).where(and_(
            PlayerMedikit.idPlayer == playerData[PLAYER].id,
            PlayerMedikit.idPlayer == Player.id,
            PlayerMedikit.idMediKit == request.type,
            PlayerMedikit.idMediKit == Medikit.id
        ))

    result = safe_exec(amount_query)
    row = result.first() 

    if row is None:
        return JSONResponse(
            status_code = HTTP_400_BAD_REQUEST,
            content={"message":"Trying to use an invalid consumable"}
        )
    else:
        # Item exists in players' hands
        pm, m, p = row
        if pm.amount > 0:
            # Get max health modifier, based from updates
            max_health_query = select(func.sum(UpgradeShop.modifier)).where(and_(
                UpgradeShop.idUpgrade == PlayerUpgrade.idUpgrade,
                PlayerUpgrade.idPlayer == playerData[PLAYER].id,
                UpgradeShop.type == 1,
                BaseStats.upgradeType == 1
            ))
            max_health_row = safe_exec(max_health_query).first()
            modifier = max_health_row if max_health_row is not None else 0 
            # Get base stat for health
            base_stats_query = select(BaseStats.baseValue).where(BaseStats.upgradeType == 1)
            base_stats = safe_exec(base_stats_query).first()
            max_health = base_stats + modifier

            new_health = min(p.health + m.healthRecover, max_health)
            update_player = update(Player).where(Player.id == playerData[PLAYER].id).values(health = new_health)
            safe_exec(update_player)
            print("-----")
            print(pm.amount)
            pm.amount = pm.amount - 1
            print(pm.amount)
            print("-----")

            if pm.amount <= 0:
                # Remove medikit row from inventory, because we finished this item
                remove_row = delete(PlayerMedikit).where(and_(
                        PlayerMedikit.idPlayer == playerData[PLAYER].id,
                        PlayerMedikit.idMediKit == request.type
                    ))
                safe_exec(remove_row)
            else:
                update_row = update(PlayerMedikit).where(and_(
                        PlayerMedikit.idPlayer == playerData[PLAYER].id,
                        PlayerMedikit.idMediKit == request.type
                    )).values(amount = pm.amount)
                safe_exec(update_row)

            session.commit()

            return JSONResponse(
                status_code = HTTP_200_OK,
                content={
                    "newHealth": str(new_health),
                    "amountLeft": pm.amount
                } 
            )

