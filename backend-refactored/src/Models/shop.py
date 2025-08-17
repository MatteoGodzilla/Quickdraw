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


class BuyUpgradeResponse(BaseModel):
    id: int
    type: int
    description: str
    level: int
    cost: int
    modifier:int