from sqlmodel import Field, SQLModel,Column,LargeBinary


class Login(SQLModel, table=True):
    __tablename__  = "Login"
    email : str = Field(primary_key=True)
    password : bytes = Field(sa_column=Column(LargeBinary))
    idPlayer : int = Field(index=True)
    authToken: str | None = None

class Player(SQLModel, table=True):
    __tablename__  = "Player"
    id : int = Field(primary_key=True,default=None)
    health : int = Field(default=100)
    maxHealth : int = Field(default=100)
    exp: int = Field(default=0)
    money: int = Field(default=0)
    bounty: int = Field(default=0)
    username: str 

class Mercenary(SQLModel, table=True):
    __tablename__  = "Mercenary"
    id:int = Field(primary_key=True)
    name:str
    power:int
    requiredLevel:int
    employmentCost:int

class Bullet(SQLModel,table=True):
    __tablename__  = "Bullet"
    type:int = Field(primary_key=True)
    description:str
    capacity:int

class Contract(SQLModel,table = True):
    __tablename__  = "Contract"
    id: int = Field(primary_key=True)
    requiredTime: int
    requiredPower: int = Field(default=0)
    maxMercenaries: int
    minReward: int
    maxReward: int
    startCost: int

class PlayerBullet(SQLModel, table=True):
    __tablename__  = "PlayerBullet"
    idPlayer : int=Field(primary_key=True,foreign_key="Player.id")
    idBullet : int=Field(primary_key=True,foreign_key="Bullet.type")
    amount: int

class BulletShop(SQLModel, table=True):
    __tablename__  = "BulletShop"
    id: int = Field(primary_key=True)
    idBullet: int = Field(foreign_key="Bullet.type")
    quantity: int
    cost: int

class ActiveContract(SQLModel, table=True):
    __tablename__  = "ActiveContract"
    id: int = Field(primary_key=True)
    idContract: int = Field(foreign_key="Contract.id")
    startTime: int
    reward: int

class EmployedMercenary(SQLModel, table=True):
    __tablename__  = "EmployedMercenary"
    id:int = Field(primary_key=True)
    idPlayer:int = Field(foreign_key="Player.id")
    idMercenary:int = Field(foreign_key="Mercenary.id")

class AssignedMercenary(SQLModel,table=True):
    __tablename__  = "AssignedMercenary"
    idActiveContract:int = Field(primary_key=True,foreign_key="ActiveContract.id")
    idEmployedMercenary:int = Field(primary_key=True,foreign_key="EmployedMercenary.idMercenary")

class Friendship(SQLModel,table=True):
    __tablename__  = "Friendship"
    idPlayerFrom:int = Field(primary_key=True,foreign_key="Player.id")
    idPlayerTo:int = Field(primary_key=True,foreign_key="Player.id")

class Medkit(SQLModel,table=True):
    __tablename__  = "Medkit"
    id:int = Field(primary_key=True)
    healthRecover:int
    description:str = Field(default=None)
    capacity:int

class MedkitShop(SQLModel,table=True):
    __tablename__  = "MedkitShop"
    id: int = Field(primary_key=True)
    idMedKit: int = Field(foreign_key="Medkit.id")
    quantity: int
    cost: int

class PlayerMedkit(SQLModel,table=True):
    __tablename__  = "PlayerMedkit"
    idPlayer : int=Field(primary_key=True,foreign_key="Player.id")
    idMedKit : int=Field(primary_key=True,foreign_key="Medkit.id")
    amount: int

class UpgradeTypes(SQLModel,table=True):
    __tablename__  = "UpgradeTypes"
    id:int = Field(primary_key=True)
    description:int = Field(default=None)

class PlayerUpgrade(SQLModel, table=True):
    __tablename__  = "PlayerUpgrade"
    idPlayer : int=Field(primary_key=True,foreign_key="Player.id")
    idUpgrade : int=Field(primary_key=True,foreign_key="UpgradeTypes.id")

class UpgradeShop(SQLModel,table=True):
    __tablename__  = "UpgradeShop"
    idUpgrade: int = Field(primary_key=True,foreign_key="UpgradeTypes.id")
    value: int
    cost: int
    type: int

class Weapon(SQLModel,table=True):
    __tablename__  = "Weapon"
    id:int = Field(primary_key=True)
    name:str
    damage:int
    cost:int = Field(default=0)
    bulletType: int = Field(foreign_key="Bullet.type")

class PlayerWeapon(SQLModel,table=True):
    __tablename__  = "PlayerWeapon"
    idPlayer:int = Field(primary_key=True,foreign_key="Player.id")
    idWeapon:int = Field(primary_key=True,foreign_key="Weapon.id")