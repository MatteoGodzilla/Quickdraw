from datetime import datetime
import enum

from sqlmodel import Field, SQLModel,Column,LargeBinary,TIMESTAMP


class Evaluation(enum.Enum):
    INCREMENT = 1,
    MULTIPLIER = 2

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
    requiredLevel:int

class Contract(SQLModel,table = True):
    __tablename__  = "Contract"
    id: int = Field(primary_key=True)
    requiredTime: int
    requiredPower: int = Field(default=0)
    maxMercenaries: int
    minReward: int
    maxReward: int
    startCost: int
    name:str

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

class Medikit(SQLModel,table=True):
    __tablename__  = "Medikit"
    id:int = Field(primary_key=True)
    healthRecover:int
    description:str = Field(default=None)
    capacity:int
    requiredLevel:int

class MedikitShop(SQLModel,table=True):
    __tablename__  = "MedikitShop"
    id: int = Field(primary_key=True)
    idMedikit: int = Field(foreign_key="Medikit.id")
    quantity: int
    cost: int

class PlayerMedikit(SQLModel,table=True):
    __tablename__  = "PlayerMedikit"
    idPlayer : int=Field(primary_key=True,foreign_key="Player.id")
    idMediKit : int=Field(primary_key=True,foreign_key="Medikit.id")
    amount: int

class UpgradeTypes(SQLModel,table=True):
    __tablename__  = "UpgradeTypes"
    id:int = Field(primary_key=True)
    description:str = Field(default=None)

class PlayerUpgrade(SQLModel, table=True):
    __tablename__  = "PlayerUpgrade"
    idPlayer : int=Field(primary_key=True,foreign_key="Player.id")
    idUpgrade : int=Field(primary_key=True,foreign_key="UpgradeShop.idUpgrade")

class UpgradeShop(SQLModel,table=True):
    __tablename__  = "UpgradeShop"
    idUpgrade: int = Field(primary_key=True,foreign_key="UpgradeTypes.id")
    level: int
    cost: int
    type: int
    modifier:int

class Weapon(SQLModel,table=True):
    __tablename__  = "Weapon"
    id:int = Field(primary_key=True)
    name:str
    damage:int
    cost:int = Field(default=0)
    bulletType: int = Field(foreign_key="Bullet.type")
    requiredLevel:int

class PlayerWeapon(SQLModel,table=True):
    __tablename__  = "PlayerWeapon"
    idPlayer:int = Field(primary_key=True,foreign_key="Player.id")
    idWeapon:int = Field(primary_key=True,foreign_key="Weapon.id")

class Level(SQLModel, table=True):
    __tablename__ = "Level"
    level:int = Field(primary_key=True)
    expRequired: int

class BaseStats(SQLModel, table=True):
    __tablename__ = "BaseStats"
    upgradeType:int = Field(primary_key=True,foreign_key="UpgradeTypes.id")
    baseValue: int = Field(default=1)
    evaluation: str = Field(default=str(Evaluation.INCREMENT))

class Bandit(SQLModel, table=True):
    __tablename__ = "Bandit"
    id:int = Field(primary_key=True)
    name: str = Field(default="Bandit")
    hp:int = Field(default=100)
    minDamage:int = Field(default=1)
    maxDamage:int = Field(default=10)
    minExp:int = Field(default=1)
    maxExp:int = Field(default=10)
    minSpeed:int = Field(default=500)
    maxSpeed:int = Field(default=1000)
    minMoney:int = Field(default=1)
    maxMoney:int = Field(default=100)


class PoolRequest(SQLModel, table=True):
    __tablename__ = "PoolRequest"
    id:int = Field(primary_key=True)
    idPlayer:int = Field(foreign_key="Player.id")
    expireTime:datetime  = Field()

class BanditIstance(SQLModel,table=True):
    __tablename__ = "BanditIstance"
    id:int = Field(primary_key=True)
    idBandit:int = Field(foreign_key="Bandit.id")
    idRequest:int = Field(foreign_key="PoolRequest.id")
    defeated:bool = Field(default=False)
    frozen:bool = Field(default=False)

class BanditPool(SQLModel,table = True):
    __tablename__ = "BanditPool"
    id:int = Field(primary_key=True)
    banditId:int = Field(foreign_key="Bandit.id")
    levelRequired:int = Field(default=1)
    spawnChance:int = Field(default=100)
    minSpawn:int = Field(default=1)
    maxSpawn:int = Field(default=1)