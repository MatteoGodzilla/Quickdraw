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
    id: int

class LockedMercenary(BaseMercenaryInfo):
    levelRequired:int
    id:int

class EmployedMercenaryInfo(BaseMercenaryInfo):
    idEmployment:int
    idMercenary:int

#responses
class EmployResponse(BaseModel):
    idEmployment:int

class AvaliableResponse(BaseModel):
    mercenaries:List[BuyableMercenary]

class PlayerAllResponse(BaseModel):
    mercenaries:List[EmployedMercenaryInfo]

class UnassignedResponse(BaseModel):
    mercenaries:List[EmployedMercenaryInfo]

class NextUnlockRespone(BaseModel):
    mercenaries:List[LockedMercenary]
