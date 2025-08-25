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
    wins:bool
    idWeapon:int
    banditDamage:int

class FightRequest(BasicAuthTokenRequest):
    idIstance:int
    fights:List[FightAttempt]

class FightRewards(BaseModel):
    money:int
    exp:int
