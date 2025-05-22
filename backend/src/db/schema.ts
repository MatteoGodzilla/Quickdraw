import { mysqlTable, mysqlSchema, AnyMySqlColumn, foreignKey, int, varchar, timestamp, binary, char, tinyint } from "drizzle-orm/mysql-core"
import { sql } from "drizzle-orm"

export const activeContract = mysqlTable("ActiveContract", {
	id: int().autoincrement().notNull(),
	idContract: int().notNull().references(() => contract.id, { onDelete: "restrict", onUpdate: "restrict" } ),
	startTime: int().notNull(),
	reward: int().notNull(),
});

export const assignedMercenary = mysqlTable("AssignedMercenary", {
	idEmployedMercenary: int().notNull().references(() => employedMercenary.id, { onDelete: "restrict", onUpdate: "restrict" } ),
	idActiveContract: int().notNull().references(() => activeContract.id, { onDelete: "restrict", onUpdate: "restrict" } ),
});

export const bullet = mysqlTable("Bullet", {
	type: int().autoincrement().notNull(),
	description: varchar({ length: 128 }).notNull(),
	capacity: int().notNull(),
});

export const bulletShop = mysqlTable("BulletShop", {
	id: int().autoincrement().notNull(),
	idBullet: int().notNull().references(() => bullet.type, { onDelete: "restrict", onUpdate: "restrict" } ),
	quantity: int().notNull(),
	cost: int().notNull(),
});

export const contract = mysqlTable("Contract", {
	name: varchar({ length: 100 }).notNull(),
	requiredTime: int().notNull(),
	requiredPower: int().default(0).notNull(),
	maxMercenaries: int().notNull(),
	minReward: int().notNull(),
	maxReward: int().notNull(),
	startCost: int().notNull(),
	id: int().autoincrement().notNull(),
});

export const duel = mysqlTable("Duel", {
	id: int().autoincrement().notNull(),
	idPlayerA: int().notNull(),
	idPlayerB: int().notNull(),
	status: tinyint().default(0).notNull(),
	timestamp: timestamp({ mode: 'string' }).default('current_timestamp()'),
});

export const employedMercenary = mysqlTable("EmployedMercenary", {
	idPlayer: int().notNull().references(() => player.id, { onDelete: "restrict", onUpdate: "restrict" } ),
	idMercenary: int().notNull().references(() => mercenary.id, { onDelete: "restrict", onUpdate: "restrict" } ),
	id: int().notNull(),
});

export const friendship = mysqlTable("Friendship", {
	idPlayerFrom: int().notNull().references(() => player.id, { onDelete: "restrict", onUpdate: "restrict" } ),
	idPlayerFriend: int().notNull().references(() => player.id, { onDelete: "restrict", onUpdate: "restrict" } ),
});

export const login = mysqlTable("Login", {
	email: varchar({ length: 254 }).notNull(),
	password: binary({ length: 60 }).notNull(),
	idPlayer: int().notNull().references(() => player.id, { onDelete: "restrict", onUpdate: "restrict" } ),
	authToken: char({ length: 36 }).default('NULL'),
});

export const medikit = mysqlTable("Medikit", {
	id: int().autoincrement().notNull(),
	healthRecover: int().notNull(),
	description: varchar({ length: 128 }).default('NULL'),
	capacity: int().notNull(),
});

export const medikitShop = mysqlTable("MedikitShop", {
	id: int().autoincrement().notNull(),
	idMedikit: int().notNull().references(() => medikit.id, { onDelete: "restrict", onUpdate: "restrict" } ),
	cost: int().notNull(),
	quantity: int().notNull(),
});

export const mercenary = mysqlTable("Mercenary", {
	id: int().notNull(),
	name: varchar({ length: 128 }).notNull(),
	power: int().notNull(),
	requiredLevel: int().notNull(),
	employmentCost: int().notNull(),
});

export const player = mysqlTable("Player", {
	id: int().autoincrement().notNull().primaryKey(),
	health: int().default(100).notNull(),
	maxHealth: int().default(100).notNull(),
	exp: int().default(0).notNull(),
	money: int().default(0).notNull(),
	bounty: int().default(0).notNull(),
	username: varchar({ length: 128 }).notNull(),
});

export const playerBullet = mysqlTable("PlayerBullet", {
	idPlayer: int().notNull().references(() => player.id, { onDelete: "restrict", onUpdate: "restrict" } ),
	idBullet: int().notNull().references(() => bullet.type, { onDelete: "restrict", onUpdate: "restrict" } ),
	amount: int().default(0).notNull(),
});

export const playerMedikit = mysqlTable("PlayerMedikit", {
	idPlayer: int().notNull().references(() => player.id, { onDelete: "restrict", onUpdate: "restrict" } ),
	idMedikit: int().notNull().references(() => medikit.id, { onDelete: "restrict", onUpdate: "restrict" } ),
	amount: int().default(0).notNull(),
});

export const playerUpgrade = mysqlTable("PlayerUpgrade", {
	idPlayer: int().notNull().references(() => player.id, { onDelete: "restrict", onUpdate: "restrict" } ),
	idUpgrade: int().notNull().references(() => upgradeShop.idUpgrade, { onDelete: "restrict", onUpdate: "restrict" } ),
});

export const playerWeapon = mysqlTable("PlayerWeapon", {
	idPlayer: int().notNull().references(() => player.id, { onDelete: "restrict", onUpdate: "restrict" } ),
	idWeapon: int().notNull().references(() => weapon.id, { onDelete: "restrict", onUpdate: "restrict" } ),
});

export const round = mysqlTable("Round", {
	idDuel: int().notNull().references(() => duel.id, { onDelete: "restrict", onUpdate: "restrict" } ),
	roundNumber: int().notNull(),
	idPlayer: int().notNull(),
	won: tinyint().notNull(),
	idWeaponUsed: int().notNull(),
	bulletsUsed: int().default(1).notNull(),
	damage: int().notNull(),
});

export const upgradeShop = mysqlTable("UpgradeShop", {
	idUpgrade: int().autoincrement().notNull(),
	type: int().notNull().references(() => upgradeTypes.id, { onDelete: "restrict", onUpdate: "restrict" } ),
	level: int().notNull(),
	cost: int().notNull(),
});

export const upgradeTypes = mysqlTable("UpgradeTypes", {
	id: int().autoincrement().notNull(),
	description: varchar("Description", { length: 100 }).notNull(),
});

export const weapon = mysqlTable("Weapon", {
	id: int().autoincrement().notNull(),
	bulletType: int().notNull().references(() => bullet.type, { onDelete: "restrict", onUpdate: "restrict" } ),
	name: varchar({ length: 100 }).notNull(),
	damage: int().notNull(),
	cost: int().default(0).notNull(),
	bulletsShot: int().default(1).notNull(),
});
