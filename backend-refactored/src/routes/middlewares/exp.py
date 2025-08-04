from sqlmodel import Session, desc,select
from starlette.status import *
from bcrypt import *
from uuid_utils import *

from MySql.tables import *
from Models.auth import *
from routes.middlewares.checkAuthTokenExpiration import *
from routes.middlewares.key_names import *

#returns a dictonary with a success value,if success is false a error message is added
def getLevel(exp:int,session:Session):
    level_query = select(Level).where(Level.expRequired >= exp).order_by(desc(Level.id))
    result = session.exec(level_query)
    level = result.first()
    return level.level