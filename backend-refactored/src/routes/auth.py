from fastapi import APIRouter

router = APIRouter(
    prefix="/auth",
    tags=["auth"]
)


@router.post("/register")
def register():
    return 0

@router.post("/login")
def login():
    return 0

@router.post("/tokenLogin")
def tokenLogin():
    return 0