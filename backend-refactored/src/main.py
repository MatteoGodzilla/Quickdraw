from fastapi import FastAPI
from fastapi.exceptions import RequestValidationError
from fastapi.responses import JSONResponse
from starlette.status import *
from routes import auth, contracts, inventory, status, shop
from MySql import connection

#this is the file to run for starting the backend service,it will incorporate all the routes defined in the route folder
app = FastAPI()
app.include_router(auth.router)
app.include_router(contracts.router)
app.include_router(inventory.router)
app.include_router(status.router)
app.include_router(shop.router)
connection.rebuild_tables()


#by default, fastApi returns code 422 for missing parameters,this overrides the default exception that returns error 422
@app.exception_handler(RequestValidationError)
async def missing_parameters_error(request, exc: RequestValidationError):
        return JSONResponse(
        status_code=HTTP_400_BAD_REQUEST,
        content={
            "error": "Missing or invalid fields."
        }
    )


