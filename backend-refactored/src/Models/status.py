from pydantic import BaseModel

from MySql.tables import Player, PlayerStats

class GetPlayerResponse(BaseModel):
    player:Player
    stats:PlayerStats