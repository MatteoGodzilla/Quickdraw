from pydantic import BaseModel

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
#responses
