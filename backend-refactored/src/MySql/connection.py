from dotenv import load_dotenv
from sqlmodel import SQLModel, create_engine
import os

def create_db_connection():
    #load .env file
    load_dotenv()
    hostname = os.getenv("hostname")
    username = os.getenv("db_username")
    password = os.getenv("password")
    database_name = os.getenv("database")
    print(database_name,hostname,username,password)
    #create database
    connection_url = f"mysql+pymysql://{username}:{password}@{hostname}/{database_name}"
    engine = create_engine(connection_url,echo=True)
    return engine

#WARNING DO THIS ONLY IN TEST DATABASE: THIS WILL DROP ALL TABLES AND CREATE THEM AGAIN WITH NO DATA
def rebuild_tables():
    #load .env file
    load_dotenv()
    hostname = os.getenv("hostname")
    username = os.getenv("db_username")
    password = os.getenv("password")
    database_name = os.getenv("database")
    print(database_name,hostname,username,password)
    #create database and clear metadata
    connection_url = f"mysql+pymysql://{username}:{password}@{hostname}/{database_name}"
    engine = create_engine(connection_url,echo=True)
    SQLModel.metadata.clear()
    SQLModel.metadata.create_all(engine)
