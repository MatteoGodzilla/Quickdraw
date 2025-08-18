from pydantic import BaseModel
from Models.commons import BasicAuthTokenRequest

class ImageRequest(BaseModel):
    id: int 

class ImageUpdateRequest(BasicAuthTokenRequest):
    image: str
    
