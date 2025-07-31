from pydantic import BaseModel

#requests
class RegisterRequest(BaseModel):
    email: str
    password: str
    username: str

class AuthRequest(BaseModel):
    email: str
    password: str

class AuthRequestWithToken(BaseModel):
    authToken : str
    #idPlayer : int

#responses
# Maybe merge these into a single class for multiple purposes?
class RegisterResponse(BaseModel):
    authToken:str

class AuthResponse(BaseModel):
    authToken:str

class AuthTokenResponse(BaseModel):
    authToken:str
