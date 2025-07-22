from fastapi import APIRouter

router = APIRouter(
    prefix="/auth",
    tags=["auth"]
)


@router.post("/register")
def register():
    return 0