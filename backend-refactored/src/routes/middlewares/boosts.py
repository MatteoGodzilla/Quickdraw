import math
from sqlmodel import Session, asc, desc,select
from starlette.status import *
from bcrypt import *
from uuid_utils import *

from MySql.tables import *
from Models.auth import *
from routes.middlewares.checkAuthTokenExpiration import *
from routes.middlewares.key_names import *
from MySql.SessionManager import *


session = global_session
MONEY_BOOST = 3
EXP_BOUNTY=4
EXP_BOOST = 5

#returns a dictonary with a success value,if success is false a error message is added
def boostedMoney(money:int,player:Player):
    money_query = select(PlayerUpgrade,UpgradeShop).where(
        and_(
            PlayerUpgrade.idPlayer==player.id,
            PlayerUpgrade.idUpgrade == UpgradeShop.idUpgrade,
            UpgradeShop.type == MONEY_BOOST
        )
    )

    result = session.exec(money_query).fetchall()
    base_money = 100
    for x,y in result:
        base_money+=y.modifier
    print("Boost result:"+str(base_money))
    print("Boost:"+str(math.ceil(money*base_money/100)))
    return math.ceil(money*base_money/100)

    

def boostedExp(exp:int,player:Player):
    exp_query = select(PlayerUpgrade,UpgradeShop).where(
        and_(
            PlayerUpgrade.idPlayer==player.id,
            PlayerUpgrade.idUpgrade == UpgradeShop.idUpgrade,
            UpgradeShop.type == EXP_BOOST
        )
    )

    result = session.exec(exp_query).fetchall()
    exp_mul = 100
    for x,y in result:
        exp_mul+=y.modifier
    print("Boost result:"+str(exp_mul))
    print("Boost:"+str(math.ceil(exp*exp_mul/100)))
    return math.ceil(exp*exp_mul/100)