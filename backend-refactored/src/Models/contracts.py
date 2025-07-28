from pydantic import BaseModel

#RESPONSES
class ActiveContractsResponse(BaseModel):
    id:int
    name:str
    requiredTime:int
    startTime:int