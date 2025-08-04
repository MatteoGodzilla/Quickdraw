from pydantic import BaseModel
from Models.commons import BasicAuthTokenRequest
from typing import List

#requests
class EmployRequest(BasicAuthTokenRequest):
    idMercenary:int

#objects
class BaseMercenaryInfo(BaseModel):
    power:int
    name:str

class BuyableMercenary(BaseMercenaryInfo):
    cost:int

#responses
class EmployResponse(BaseModel):
    idMercenary:int

class AvaliableResponse(BaseModel):
    mercenaries:List[BaseMercenaryInfo]