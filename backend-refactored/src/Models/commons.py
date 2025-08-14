from pydantic import BaseModel


class BasicAuthTokenRequest(BaseModel):
    authToken:str

class BuyRequest(BasicAuthTokenRequest):
    id:int

class ImageRequest(BaseModel):
    id: int
