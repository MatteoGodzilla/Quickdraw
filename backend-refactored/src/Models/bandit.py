from datetime import datetime
from pydantic import BaseModel

from MySql.tables import Bandit

class BanditData(BaseModel):
    name:str
    hp:int
    minDamage:int
    maxDamage:int
    minSpeed:int
    maxSpeed:int

class BanditRepsonse(BaseModel):
    expires:datetime
    idIstance:int
    stats:BanditData

