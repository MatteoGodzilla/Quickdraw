from pydantic import BaseModel


class BuyBulletResponse(BaseModel):
    id: int
    type: int
    name: str
    cost: int
    quantity: int
    capacity: int