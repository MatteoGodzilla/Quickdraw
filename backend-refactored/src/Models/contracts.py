from pydantic import BaseModel
from typing import List

from Models.commons import basicAuthTokenRequest

#objects
class AvailableContractResponseElement(BaseModel):
    id:int
    name:str
    requiredTime:int
    requiredPower:int
    maxMercenaries:int
    startCost:int

class ActiveContractResponseElement(BaseModel):
    id:int
    name:str
    requiredTime:int
    startTime:int

#requests
class RedeemContractRequest(basicAuthTokenRequest):
    idContract:int

class StartContractRequest(basicAuthTokenRequest):
    contract:int
    mercenaries: list[int]

#responses
class ContractRedeemedResponse(BaseModel):
    success:bool
    reward:int

class ContractStartResponse(BaseModel):
    success:bool
