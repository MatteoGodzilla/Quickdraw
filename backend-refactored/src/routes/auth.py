import time
from fastapi import APIRouter
from fastapi.exceptions import RequestValidationError
from fastapi.responses import JSONResponse
from fastapi.encoders import jsonable_encoder

from sqlmodel import Session,select
from starlette.status import *
from bcrypt import *
from uuid_utils import *

from MySql import connection
from MySql.tables import *
from Models.auth import *
import json
from routes.middlewares.key_names import *
from routes.middlewares.checkAuthTokenExpiration import *

router = APIRouter(
    prefix="/auth",
    tags=["auth"]
)

engine = connection.create_db_connection()
session = Session(engine)

#routes
@router.post("/register")
async def register(request: RegisterRequest):
    try:
        new_player = Player(username=request.username)
        session.add(new_player)
        session.flush()

        #hash password
        salted_password=hashpw(request.password.encode(),gensalt())
        auth_token = uuid7().urn.replace("urn:uuid:","")
        new_login_info = Login(email=request.email,username = request.username,idPlayer=new_player.id,password=salted_password,authToken = auth_token)

        session.add(new_login_info)
        session.commit()
    except:
        session.rollback()
        return JSONResponse(
            status_code = HTTP_500_INTERNAL_SERVER_ERROR,
            content = {"message":"Something went wrong"}
        )
    #create response
    response = RegisterResponse(idPlayer = new_player.id, authToken = auth_token)
    return JSONResponse(
         status_code = HTTP_200_OK,
         content = jsonable_encoder(response)
    )

@router.post("/login")
async def login(request: AuthRequest):
    #check user exists
    login_query = select(Login).where(Login.email == request.email)
    results = session.exec(login_query)
    user = results.first()
    if user==None:
        return JSONResponse(
            status_code = HTTP_200_OK,
            content = {"message":"Wrong email or password"}
        )
    if hashpw(request.password.encode(),user.password) == user.password:
        try:
            auth_token = uuid7().urn.replace("urn:uuid:","")
            user.authToken = auth_token
            session.commit()
            response = AuthResponse(authToken=auth_token)
            return JSONResponse(
                status_code = HTTP_200_OK,
                content = jsonable_encoder(response)
            )
        except:
            return JSONResponse(
                status_code = HTTP_500_INTERNAL_SERVER_ERROR,
                content = {"message":"Something went wrong"}
            )
    #wrong password
    return JSONResponse(
        status_code = HTTP_200_OK,
        content = {"message":"Wrong email or password"}
    )



@router.post("/tokenLogin")
async def tokenLogin(request: AuthRequestWithToken):
    login_query = select(Login).where(Login.idPlayer == request.idPlayer)
    results = session.exec(login_query)
    user = results.first()
    if user==None:
        return JSONResponse(
            status_code = HTTP_400_BAD_REQUEST,
            content = {"message":"Player with given id does not exists"}
        )

    if user.authToken!=request.authToken:
        return JSONResponse(
            status_code = HTTP_400_BAD_REQUEST,
            content = {"message":"Authentication token does not match"}
        )
    
    validate_token = checkAuthTokenValidity(user.authToken)
    if validate_token[SUCCESS] == False:
        return JSONResponse(
            status_code = validate_token[HTTP_CODE],
            content = {"message":validate_token[ERROR]}
        )

    #everything is correct,return new authToken
    try:
        auth_token = uuid7().urn.replace("urn:uuid:","")
        user.authToken = auth_token
        session.commit()
        response = AuthResponse(authToken=auth_token)
        return JSONResponse(
            status_code = HTTP_200_OK,
            content = jsonable_encoder(response)
        )
    except:
        return JSONResponse(
            status_code = HTTP_500_INTERNAL_SERVER_ERROR,
            content = {"message":"Something went wrong"}
        )
