from datetime import datetime
from typing import List
from pydantic import BaseModel

from MySql.tables import Bandit
from Models.commons import BasicAuthTokenRequest

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

class BanditFreezeRequest(BasicAuthTokenRequest):
    idIstance:int

class BanditFreezeResponse(BaseModel):
    success:bool

class FightAttempt(BaseModel):
    wins:True
    damage:int

class FightRequest(BaseModel):
    idIstance:int
    fights:List[FightAttempt]

