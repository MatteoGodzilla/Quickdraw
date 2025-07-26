from sqlmodel import Session,select
from starlette.status import *
from bcrypt import *
from uuid_utils import *

from MySql.tables import *
from Models.auth import *
from routes.middlewares.checkAuthTokenExpiration import *
from routes.middlewares.key_names import *

#returns a dictonary with a success value,if success is false a error message is added
def getPlayer(authToken:str,session:Session):
    player_query = select(Login).where(Login.authToken == authToken).limit(1)
    results = session.exec(player_query)
    user = results.first()

    validate_token = checkAuthTokenValidity(authToken)
    if validate_token["success"] == False:
        return validate_token

    if user == None:
        return {SUCCESS:False,ERROR:"Provided authentication token is not associated with any Player",HTTP_CODE:HTTP_400_BAD_REQUEST}

    return {SUCCESS:True,PLAYER:user}