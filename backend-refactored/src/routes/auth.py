from fastapi import APIRouter
from fastapi.exceptions import RequestValidationError
from fastapi.responses import JSONResponse

from sqlmodel import Session
from starlette.status import *
from bcrypt import *
import uuid_v7.base
import uuid_v7.cli

from MySql import connection,tables
from Models.auth import *
import json
import uuid_v7

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
    session.flush()

    #hash password
    salted_password=hashpw(request.password.encode(),gensalt())
    new_login_info = tables.Login(email=request.email,username = request.username,idPlayer=new_player.id,password=salted_password)
    session.add(new_login_info)
    session.commit()

    #create response
    auth_token = uuid_v7.base.uuid7().hex
    response = RegisterResponse(idPlayer = new_player.id, authToken = auth_token)

    return JSONResponse(
         status_code = HTTP_200_OK,
         content = json.dumps(response.model_dump())
    )

@router.post("/login")
async def login(request: AuthRequest):
    return 0

@router.post("/tokenLogin")
async def tokenLogin(request: AuthRequestWithToken):
    return 0