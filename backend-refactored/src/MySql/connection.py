from dotenv import load_dotenv
from sqlmodel import SQLModel, create_engine
import os

def create_db_connection():
    #load .env file
    load_dotenv()
    hostname = os.getenv("hostname")
    username = os.getenv("username")
    password = os.getenv("password")
    database_name = os.getenv("database")
    #create database
    connection_url = f"postgresql://{username}:{password}@{hostname}:{3306}/{database_name}"
    engine = create_engine(connection_url,echo=True)
    SQLModel.metadata.create_all(engine)
    return engine