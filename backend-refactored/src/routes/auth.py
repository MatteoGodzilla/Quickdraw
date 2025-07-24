from fastapi import APIRouter
from fastapi.exceptions import RequestValidationError
from fastapi.responses import JSONResponse

from sqlmodel import Session
from starlette.status import *
from bcrypt import *

from MySql import connection,tables
from Models.auth import *

router = APIRouter(
    prefix="/auth",
    tags=["auth"]
)

engine = connection.create_db_connection()
session = Session(engine)

#routes
@router.post("/register")
async def register(request: RegisterRequest):
    new_player = tables.Player(username=request.username)
    session.add(new_player)

    #hash password
    salted_password=hashpw(request.password,gensalt())
    new_login_info = tables.Login(username = request.username,idPlayer=new_player.id,password=salted_password)
    session.add(new_login_info)

    session.commit()

    return JSONResponse(
         status = HTTP_200_OK,
         content = "TODO: make a response"
    )

@router.post("/login")
async def login(request: AuthRequest):
    return 0

@router.post("/tokenLogin")
async def tokenLogin(request: AuthRequestWithToken):
    return 0