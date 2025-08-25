from fastapi import APIRouter
from Models.duel import DuelSubmitData, WON, LOST, DRAW
from fastapi.responses import JSONResponse
from routes.middlewares.checkAuthTokenExpiration import checkAuthTokenValidity 
from routes.middlewares.key_names import SUCCESS, ERROR, HTTP_CODE, PLAYER
from routes.middlewares.getPlayer import getPlayer, getPlayerData
from sqlmodel import select, update, insert
from MySql.tables import PlayerWeapon, Weapon, PlayerBullet, Bullet, PlayerUpgrade, Player, UpgradeShop, UpgradeTypes, Duel, Round
from sqlalchemy import and_, desc
from MySql.SessionManager import safe_exec, safe_commit, global_session
from starlette.status import *
import time

router = APIRouter(
    prefix="/duel",
    tags=["duel"]
)

@router.post("")
async def duel(request: DuelSubmitData):
    # Copy pasted from shop
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

    player:Login = obtain_player[PLAYER]
    playerInfo = getPlayerData(player)
    if playerInfo[SUCCESS] == False:
        return JSONResponse(
            status_code = HTTP_500_INTERNAL_SERVER_ERROR,
            content={"message":"Could not find player data"}
        )
    player:Player = playerInfo[PLAYER]

    # Get owned weapons 
    weapon_query = select(Weapon).where(and_(
        PlayerWeapon.idPlayer == player.id,
        PlayerWeapon.idWeapon == Weapon.id
    ))
    result = safe_exec(weapon_query)
    weapons = result.fetchall()

    # Get owned bullets
    bullets_query  = select(PlayerBullet, Bullet).where(and_(
        PlayerBullet.idPlayer == player.id,
        Bullet.type == PlayerBullet.idBullet
    ))
    result = safe_exec(bullets_query)
    bullets = result.fetchall()

    # Get Player Upgrades
    upgrades_query = select(PlayerUpgrade,UpgradeShop,UpgradeTypes).where(and_(
        PlayerUpgrade.idUpgrade == UpgradeShop.idUpgrade,
        UpgradeShop.type == UpgradeTypes.id,
        PlayerUpgrade.idPlayer == player.id
    ))
    response_upgrades = []
    result = safe_exec(upgrades_query)
    upgrades = result.fetchall()

    # Get bounty of other player
    bounty_query = select(Player.bounty).where(Player.id == request.idOpponent)  
    result = safe_exec(bounty_query)
    opponent_bounty = result.first()

    # Calculate total damage
    total_damage = 0
    rounds_lost = 0
    rounds_won = 0
    for r in request.rounds:
        if r.won == LOST:
            total_damage = total_damage + r.damage
            rounds_lost = rounds_lost + 1
        elif r.won == WON:
            rounds_won = rounds_won + 1

    moneyMult = 100
    bountyMult = 100
    expMult = 100
    for pu, us, ut in upgrades:
        if us.type == 3:
            moneyMult = moneyMult + us.modifier
        elif us.type == 4:
            bountyMult = bountyMult + us.modifier
        elif us.type == 5:
            expMult = expMult + us.modifier

    # give exp
    exp = 10
    if rounds_won > rounds_lost:
        exp = exp + 2
    if player.bounty < opponent_bounty:
        exp = exp + 3

    # gib muny
    money = 0
    if rounds_won > rounds_lost:
        money = 10 + opponent_bounty * 3 // 2
    # gib bounty
    bounty = 0
    if rounds_won > rounds_lost:
        bounty = player.bounty + money * bountyMult / 100

    player_query = update(Player).where(Player.id == player.id).values(
       health = max(player.health - total_damage, 0),
       exp = player.exp + exp * expMult / 100,
       money = player.money + money * moneyMult / 100,
       bounty = bounty 
    )
    safe_exec(player_query)
    
    # Remove used bullets
    bullets_used = {} # id: amount
    for r in request.rounds:
        weapon = next(w for w in weapons if w.id == r.idWeaponUsed)
        bullet = next(x[1] for x in bullets if x[1].type == weapon.bulletType)
        if bullet.type not in bullets_used:
            bullets_used[bullet.type] = 0
        bullets_used[bullet.type] = bullets_used[bullet.type] + weapon.bulletsShot

    for bullet_type in bullets_used.keys():
        old_amount = next(x[0] for x in bullets if x[1].type == bullet_type).amount
        print("---")
        print(old_amount)
        print(bullets_used[bullet_type])
        bullet_query = update(PlayerBullet).where(and_(
            PlayerBullet.idPlayer == player.id,
            PlayerBullet.idBullet == bullet_type
        )).values(amount=old_amount - bullets_used[bullet_type])
        safe_exec(bullet_query)

    # Add rows to database for statistics
    
    # Check if opponent has uploaded before us
    check_duel_exists = select(Duel).where(and_(
        Duel.idPlayerA == request.idOpponent,
        Duel.idPlayerB == player.id,
        Duel.status == 0
    )).order_by(desc(Duel.timestamp))
    result = safe_exec(check_duel_exists)
    duel = result.first()
    duel_id = 0

    if duel != None:
        duel_id = duel.id
        
        mark_duel_verified = update(Duel).where(Duel.id == duel_id).values(status = 1)
        safe_exec(mark_duel_verified)
    else:
        create_duel = Duel( 
            idPlayerA = player.id,
            idPlayerB = request.idOpponent,
            status = 0, 
            timestamp = time.time()
        )
        global_session.add(create_duel)
        global_session.flush()
        duel_id = create_duel.id

    for i, r in enumerate(request.rounds):
        create_round = Round(
            idDuel = duel_id,
            roundNumber = i + 1,
            idPlayer = player.id,
            won = r.won,
            idWeaponUsed = r.idWeaponUsed,
            bulletsUsed = r.bulletsUsed,
            damage = r.damage
        )
        global_session.add(create_round)

    global_session.commit()

    return JSONResponse(status_code = HTTP_200_OK, content="")
