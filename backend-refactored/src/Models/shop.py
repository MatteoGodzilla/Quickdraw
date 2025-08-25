from typing import List
from pydantic import BaseModel


class BuyBulletResponse(BaseModel):
    id: int
    type: int
    name: str
    cost: int
    quantity: int
    capacity: int
    level:int

class BuyMedikitResponse(BaseModel):
    id: int
    idMedikit: int
    description: str
    healthRecover: int
    cost: int
    quantity: int
    capacity: int
    level:int


class BuyWeaponResponse(BaseModel):
    id: int
    name: str
    damage: int
    cost: int
    level:int
    bulletType: int
    bulletsShot: int

class BuyableUpgrade(BaseModel):
    id: int
    type: int
    description: str
    level: int
    cost: int
    modifier:int

class BuyUpgradeResponse(BaseModel):
    buyed:BuyableUpgrade
    nextUp:List[BuyableUpgrade]
