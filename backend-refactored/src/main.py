from fastapi import FastAPI
from routes import auth 

#this is the file to run for starting the backend service,it will incorporate all the routes defined in the route folder
app = FastAPI()
app.include_router(auth.router)


