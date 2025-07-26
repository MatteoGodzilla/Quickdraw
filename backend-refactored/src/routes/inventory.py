import time
from fastapi import APIRouter
from fastapi.exceptions import RequestValidationError
from fastapi.responses import JSONResponse

from sqlmodel import Session,select
from starlette.status import *
from bcrypt import *
from uuid_utils import *

from MySql import connection
from MySql.tables import *
from Models.auth import *
import json

router = APIRouter(
    prefix="/inventory",
    tags=["inventory"]
)

engine = connection.create_db_connection()
session = Session(engine)

#routes
@router.post("/inventory")
async def get_inventory(request: RegisterRequest):
    return 0