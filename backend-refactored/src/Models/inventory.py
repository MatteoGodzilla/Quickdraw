from pydantic import BaseModel

#objects
class InventoryResponseBullet:
    type:int
    description:str
    capacity:int

class InventoryResponseMedkit:
    healthRecover:int
    description:str
    capacity:int

class InventoryResponseUpgrade:
    idUpgrade:int
    description:str
    value: int
    type: int

class InventoryResponseWeapon:
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