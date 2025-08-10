from pydantic import BaseModel


class BuyBulletResponse(BaseModel):
    id: int
    type: int
    name: str
    cost: int
    quantity: int
    capacity: int

class BuyMedikitResponse(BaseModel):
    id: int
    idMedikit: int
    description: str
    healthRecover: int
    cost: int
    quantity: int
    capacity: int