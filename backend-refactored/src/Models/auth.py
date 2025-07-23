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

#responses
class RegisterResponse(BaseModel):
    idPlayer : int
    authToken:str

class AuthResponse(BaseModel):
    authToken:str

class AuthTokenResponse(BaseModel):
    authToken:str