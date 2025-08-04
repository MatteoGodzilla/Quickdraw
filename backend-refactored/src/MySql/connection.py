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
    print(hostname)
    #create database
    connection_url = f"mysql+pymysql://{username}:{password}@{hostname}/{database_name}"
    engine = create_engine(connection_url,echo=True,query_cache_size=0)
    return engine

def rebuild_tables():
    #load .env file
    load_dotenv()
    hostname = os.getenv("hostname")
    username = os.getenv("db_username")
    password = os.getenv("password")
    database_name = os.getenv("database")
    #create database and clear metadata
    connection_url = f"mysql+pymysql://{username}:{password}@{hostname}/{database_name}"
    engine = create_engine(connection_url,echo=True,query_cache_size=0)
    SQLModel.metadata.clear()
    SQLModel.metadata.create_all(engine)
