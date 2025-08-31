from pydantic import BaseModel
from typing import List
from Models.commons import BasicAuthTokenRequest
from Models.mercenaries import EmployedMercenaryInfo

#objects
class AvailableContractResponseElement(BaseModel):
    id:int
    name:str
    requiredTime:int
    requiredPower:int
    maxMercenaries:int
    startCost:int

class ActiveContractResponseElement(BaseModel):
    activeId:int
    name:str
    requiredTime:int
    startTime:int
    mercenaries:List[EmployedMercenaryInfo]

class StartedContractResponse(BaseModel):
    startTime:int
    idActiveContract:int

#requests
class RedeemContractRequest(BasicAuthTokenRequest):
    idContract:int

class StartContractRequest(BasicAuthTokenRequest):
    contract:int
    mercenaries: list[int]

#responses
class ContractRedeemedResponse(BaseModel):
    success:bool
    reward:int
    returnableContract:AvailableContractResponseElement

class ContractStartResponse(BaseModel):
    success:bool
    contractInfo:StartedContractResponse



