from Models.commons import BasicAuthTokenRequest 
from pydantic import BaseModel

# Match Result enum
WON = 0
LOST = 1
DRAW = 2

class RoundSubmitData(BaseModel):
    won: int
    idWeaponUsed: int
    bulletsUsed: int
    damage: int

class DuelSubmitData(BasicAuthTokenRequest):
    idOpponent: int
    rounds: list[RoundSubmitData]
