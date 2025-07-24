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