from pydantic import BaseModel

#objects
class InventoryResponseBullet:
    idBullet:int

class InventoryResponseMedkit:
    idMedkit:int

class InventoryResponseUpgrade:
    upgradeType:int

class InventoryResponseWeapon:
    idWeapon:int

#requests
class InventoryRequest(BaseModel):
    authToken:str

#responses
class InventoryResponse(BaseModel):
    bullets:list
    weapons:list
    medikits:list
    upgrades:list