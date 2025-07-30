from pydantic import BaseModel


class BasicAuthTokenRequest(BaseModel):
    authToken:str