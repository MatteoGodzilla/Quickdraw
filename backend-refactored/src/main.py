from fastapi import FastAPI, APIRouter
from fastapi.exceptions import RequestValidationError
from fastapi.responses import JSONResponse
from sqlmodel import Session
from starlette.status import *
from routes import auth, contracts, inventory, status, shop, bountyboard,mercenaries, images, use, bandits, duel
from MySql import connection
from pymysql.err import *

VERSION = 2

#this is the file to run for starting the backend service,it will incorporate all the routes defined in the route folder
app = FastAPI()
app.include_router(auth.router)
app.include_router(contracts.router)
app.include_router(inventory.router)
app.include_router(status.router)
app.include_router(mercenaries.router)
app.include_router(shop.router)
app.include_router(bountyboard.router)
app.include_router(images.router)
app.include_router(use.router)
app.include_router(bandits.router)
app.include_router(duel.router)
connection.rebuild_tables()

router = APIRouter()

@router.get("/version")
async def version():
    return VERSION 

app.include_router(router)

#by default, fastApi returns code 422 for missing parameters,this overrides the default exception that returns error 422
@app.exception_handler(RequestValidationError)
async def missing_parameters_error(request, exc: RequestValidationError):
    return JSONResponse(
        status_code=HTTP_400_BAD_REQUEST,
        content={
            "error": "Missing or invalid fields."
        }
    )

