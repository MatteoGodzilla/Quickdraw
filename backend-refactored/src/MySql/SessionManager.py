from sqlmodel import Session,select,and_
from MySql import connection

engine = connection.create_db_connection()
global_session = Session(engine)