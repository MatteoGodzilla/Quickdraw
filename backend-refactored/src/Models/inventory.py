from pydantic import BaseModel

#objects
class InventoryResponseBullet(BaseModel):
    type:int
    description:str
    capacity:int

class InventoryResponseMedkit(BaseModel):
    healthRecover:int
    description:str
    capacity:int

class InventoryResponseUpgrade(BaseModel):
    idUpgrade:int
    description:str
    type: int
    level:int

class InventoryResponseWeapon(BaseModel):
    name:str
    damage:int
    cost:int
    bulletType:int

#requests
class InventoryRequest(BaseModel):
    authToken:str

#responses
class InventoryResponse(BaseModel):
    bullets:list
    weapons:list
    medikits:list
    upgrades:list