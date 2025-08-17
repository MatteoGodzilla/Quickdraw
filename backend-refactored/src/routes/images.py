from base64 import b64encode
from fastapi import APIRouter, HTTPException
from fastapi.responses import JSONResponse
from Models.commons import BasicAuthTokenRequest, ImageRequest
from pathlib import Path
from starlette.status import *

from routes.middlewares.checkAuthTokenExpiration import checkAuthTokenValidity
from routes.middlewares.key_names import *
from routes.middlewares import getPlayer
from MySql.tables import Login

router = APIRouter(
    prefix="/image",
    tags=["image"]
)

# working directory is /src/, while /images is a sibling
filesystem_root = "../images/"

@router.post("/weapon")
def weapon(request: ImageRequest):
    p = Path(filesystem_root) / "weapon" / str(request.id)
    return _fetch_image(p)

@router.post("/bullet")
def bullet(request: ImageRequest):
    p = Path(filesystem_root) / "bullet" / str(request.id)
    return _fetch_image(p)

@router.post("/medikit")
def medikit(request: ImageRequest):
    p = Path(filesystem_root) / "medikit" / str(request.id)
    return _fetch_image(p)

@router.post("/upgrade")
def upgrade(request: ImageRequest):
    p = Path(filesystem_root) / "upgrade" / str(request.id)
    return _fetch_image(p)

@router.post("/player")
def player(request: ImageRequest):
    p = Path(filesystem_root) / "player" / str(request.id)
    return _fetch_image(p)

def _fetch_image(p: Path):
    if not p.exists():
        raise HTTPException(status_code=404)
    with open(p, "rb") as file:
        result = b64encode(file.read())
        return JSONResponse(
            status_code = HTTP_200_OK,
            content={ "image" : result.decode() }
        )



@router.post("/updatePic")
def updatePic(request: BasicAuthTokenRequest):
    # Copy-pasted from shop.py
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
