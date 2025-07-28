from pydantic import BaseModel


class basicAuthTokenRequest(BaseModel):
    authToken:str