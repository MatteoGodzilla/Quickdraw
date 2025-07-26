from time import time
from starlette.status import *
from routes.middlewares.key_names import *

TOKEN_PERIOD_OF_VALIDITY = 3600 * 24 * 1000 * 2 #48 hours

#token must be uuidv7
def checkAuthTokenValidity(authToken:str):
    formatted_token = authToken.replace("urn:uuid:","")
    if len(formatted_token) != 36:
        return {SUCCESS:False,ERROR:"Invalid format for authentication token",HTTP_CODE:HTTP_400_BAD_REQUEST}

    token_parts = formatted_token.split("-")
    millis_timestamp = int(token_parts[0]+token_parts[1],16)
    treshold = round(time() * 1000) + (TOKEN_PERIOD_OF_VALIDITY)

    if millis_timestamp > treshold:
        return {SUCCESS:False,ERROR:"Token is expired",HTTP_CODE:HTTP_401_UNAUTHORIZED}

    return {SUCCESS:True}