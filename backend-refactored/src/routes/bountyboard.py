from fastapi import APIRouter
from Models.commons import BasicAuthTokenRequest
from MySql import SessionManager
from routes.middlewares.checkAuthTokenExpiration import checkAuthTokenValidity 
from routes.middlewares.key_names import SUCCESS, ERROR, HTTP_CODE, PLAYER
from routes.middlewares.getPlayer import getPlayer
from sqlmodel import Session, select, delete
from MySql.tables import Friendship, Player
from sqlalchemy import and_, desc
from fastapi.responses import JSONResponse
from starlette.status import HTTP_200_OK, HTTP_400_BAD_REQUEST
from Models.friends import FriendRequest

session = SessionManager.global_session

router = APIRouter(
    prefix="/bounty",
    tags=[]
)

@router.post("/friends")
async def friends(request: BasicAuthTokenRequest):
    # Copy pasted
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

    pl:Login = obtain_player[PLAYER]

    friends = select(Friendship, Player).where(and_(
        Friendship.idPlayerFrom == pl.idPlayer,
        Friendship.idPlayerTo == Player.id
    )).order_by(desc(Player.bounty))

    result = session.execute(friends)
    response = []

    for f, p in result.fetchall():
        response.append({
            "id": p.id,
            "bounty": p.bounty,
            "username": p.username
        })

    return JSONResponse(
        status_code = HTTP_200_OK,
        content = response
    )

@router.post("/friends/add")
async def addFriend(request: FriendRequest):
    # Copy pasted
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

    pl:Login = obtain_player[PLAYER]

    friend_exists = select(Friendship, Player).where(and_(
        Friendship.idPlayerFrom == pl.idPlayer,
        Friendship.idPlayerTo == request.idFriend
    ))
    
    result = SessionManager.safe_exec(friend_exists)
    check = result.first()

    print(check)

    if check is not None:
        return JSONResponse(
            status_code = HTTP_400_BAD_REQUEST,
            content={"message":"Id provided is for someone which is a friend already"}
        )

    new_friend = Friendship(idPlayerFrom = pl.idPlayer, idPlayerTo = request.idFriend)
    session.add(new_friend)
    session.commit()

    return JSONResponse(
        status_code = HTTP_200_OK, 
        content={"message": "Friend added successfully"}
    )

@router.post("/friends/remove")
async def removeFriend(request: FriendRequest):
    # Copy pasted
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

    pl:Login = obtain_player[PLAYER]

    friend_exists = select(Friendship, Player).where(and_(
        Friendship.idPlayerFrom == pl.idPlayer,
        Friendship.idPlayerTo == request.idFriend
    ))
    
    result = SessionManager.safe_exec(friend_exists)
    check = result.first()

    if check is None:
        return JSONResponse(
            status_code = HTTP_400_BAD_REQUEST,
            content={"message":"Id provided is for someone which is not a friend" }
        )

    delete_query = delete(Friendship).where(and_(
        Friendship.idPlayerFrom == pl.idPlayer,
        Friendship.idPlayerTo == request.idFriend
    ))
    result = SessionManager.safe_exec(delete_query)
    session.commit()

    return JSONResponse(
        status_code = HTTP_200_OK, 
        content={"message": "Friend removed successfully"}
    )

@router.get("/leaderboard")
async def leaderboard():
    leaderboard = select(Player.id, Player.bounty, Player.username).order_by(desc(Player.bounty)).limit(50)
    result = session.execute(leaderboard)

    response = []
    for id, bounty, username in result.fetchall():
        response.append({
            "id": id,
            "bounty": bounty,
            "username": username
        })

    return JSONResponse(
        status_code = HTTP_200_OK,
        content = response
    )
