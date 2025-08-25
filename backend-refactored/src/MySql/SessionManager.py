from pymysql import OperationalError
from sqlmodel import Session,select,and_
from MySql import connection

engine = connection.create_db_connection()
global_session = Session(engine)

def safe_exec(stmt, retries=1):
    global global_session
    for i in range(retries + 1):
        try:
            return global_session.exec(stmt)
        except OperationalError as e:
            if e.args and e.args[0] == 2013:  # Lost connection
                global_session.rollback()
                global_session.close()
                global_session = Session(engine)
            else:
                raise
    raise RuntimeError("Failed after retrying due to connection loss")

# DO NOT USE!!!!!!!
def safe_commit(retries=1):
    global global_session
    for i in range(retries + 1):
        try:
            global_session.commit()
        except OperationalError as e:
            print(e)
            if e.args and e.args[0] == 2013:  # Lost connection
                global_session.rollback()
                global_session.close()
                global_session = Session(engine)
            else:
                raise
    raise RuntimeError("Failed after retrying due to connection loss")
