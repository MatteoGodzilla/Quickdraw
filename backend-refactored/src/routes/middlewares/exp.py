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

#returns a dictonary with a success value,if success is false a error message is added
def getLevel(exp:int):
    level_query = select(Level).where(Level.expRequired <= exp).order_by(desc(Level.level))
    result = session.exec(level_query)
    level = result.first()
    return level.level

def getNextUnlockableBulletLevel(level:int):
    return 0
    
def getNextUnlockableMedikitLevel(level:int):
    return 0

def getNextUnlockableUpgradeLevel(level:int):
    return 0

def getNextUnlockableMercenariesLevel(level:int):
    level_query = select(Mercenary.requiredLevel).where(Mercenary.requiredLevel > level).order_by(asc(Mercenary.requiredLevel))
    result = session.exec(level_query)
    return result.first()