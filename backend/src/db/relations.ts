import { relations } from "drizzle-orm/relations";
import { contract, activeContract, assignedMercenary, employedMercenary, bullet, bulletShop, mercenary, player, friendship, login, medikit, medikitShop, playerBullet, playerMedikit, playerUpgrade, upgradeShop, playerWeapon, weapon, duel, round, upgradeTypes } from "./schema";

export const activeContractRelations = relations(activeContract, ({one, many}) => ({
	contract: one(contract, {
		fields: [activeContract.idContract],
		references: [contract.id]
	}),
	assignedMercenaries: many(assignedMercenary),
}));

export const contractRelations = relations(contract, ({many}) => ({
	activeContracts: many(activeContract),
}));

export const assignedMercenaryRelations = relations(assignedMercenary, ({one}) => ({
	activeContract: one(activeContract, {
		fields: [assignedMercenary.idActiveContract],
		references: [activeContract.id]
	}),
	employedMercenary: one(employedMercenary, {
		fields: [assignedMercenary.idEmployedMercenary],
		references: [employedMercenary.id]
	}),
}));

export const employedMercenaryRelations = relations(employedMercenary, ({one, many}) => ({
	assignedMercenaries: many(assignedMercenary),
	mercenary: one(mercenary, {
		fields: [employedMercenary.idMercenary],
		references: [mercenary.id]
	}),
	player: one(player, {
		fields: [employedMercenary.idPlayer],
		references: [player.id]
	}),
}));

export const bulletShopRelations = relations(bulletShop, ({one}) => ({
	bullet: one(bullet, {
		fields: [bulletShop.idBullet],
		references: [bullet.type]
	}),
}));

export const bulletRelations = relations(bullet, ({many}) => ({
	bulletShops: many(bulletShop),
	playerBullets: many(playerBullet),
	weapons: many(weapon),
}));

export const mercenaryRelations = relations(mercenary, ({many}) => ({
	employedMercenaries: many(employedMercenary),
}));

export const playerRelations = relations(player, ({many}) => ({
	employedMercenaries: many(employedMercenary),
	friendships_idPlayerFrom: many(friendship, {
		relationName: "friendship_idPlayerFrom_player_id"
	}),
	friendships_idPlayerFriend: many(friendship, {
		relationName: "friendship_idPlayerFriend_player_id"
	}),
	logins: many(login),
	playerBullets: many(playerBullet),
	playerMedikits: many(playerMedikit),
	playerUpgrades: many(playerUpgrade),
	playerWeapons: many(playerWeapon),
}));

export const friendshipRelations = relations(friendship, ({one}) => ({
	player_idPlayerFrom: one(player, {
		fields: [friendship.idPlayerFrom],
		references: [player.id],
		relationName: "friendship_idPlayerFrom_player_id"
	}),
	player_idPlayerFriend: one(player, {
		fields: [friendship.idPlayerFriend],
		references: [player.id],
		relationName: "friendship_idPlayerFriend_player_id"
	}),
}));

export const loginRelations = relations(login, ({one}) => ({
	player: one(player, {
		fields: [login.idPlayer],
		references: [player.id]
	}),
}));

export const medikitShopRelations = relations(medikitShop, ({one}) => ({
	medikit: one(medikit, {
		fields: [medikitShop.idMedikit],
		references: [medikit.id]
	}),
}));

export const medikitRelations = relations(medikit, ({many}) => ({
	medikitShops: many(medikitShop),
	playerMedikits: many(playerMedikit),
}));

export const playerBulletRelations = relations(playerBullet, ({one}) => ({
	bullet: one(bullet, {
		fields: [playerBullet.idBullet],
		references: [bullet.type]
	}),
	player: one(player, {
		fields: [playerBullet.idPlayer],
		references: [player.id]
	}),
}));

export const playerMedikitRelations = relations(playerMedikit, ({one}) => ({
	medikit: one(medikit, {
		fields: [playerMedikit.idMedikit],
		references: [medikit.id]
	}),
	player: one(player, {
		fields: [playerMedikit.idPlayer],
		references: [player.id]
	}),
}));

export const playerUpgradeRelations = relations(playerUpgrade, ({one}) => ({
	player: one(player, {
		fields: [playerUpgrade.idPlayer],
		references: [player.id]
	}),
	upgradeShop: one(upgradeShop, {
		fields: [playerUpgrade.idUpgrade],
		references: [upgradeShop.idUpgrade]
	}),
}));

export const upgradeShopRelations = relations(upgradeShop, ({one, many}) => ({
	playerUpgrades: many(playerUpgrade),
	upgradeType: one(upgradeTypes, {
		fields: [upgradeShop.type],
		references: [upgradeTypes.id]
	}),
}));

export const playerWeaponRelations = relations(playerWeapon, ({one}) => ({
	player: one(player, {
		fields: [playerWeapon.idPlayer],
		references: [player.id]
	}),
	weapon: one(weapon, {
		fields: [playerWeapon.idWeapon],
		references: [weapon.id]
	}),
}));

export const weaponRelations = relations(weapon, ({one, many}) => ({
	playerWeapons: many(playerWeapon),
	bullet: one(bullet, {
		fields: [weapon.bulletType],
		references: [bullet.type]
	}),
}));

export const roundRelations = relations(round, ({one}) => ({
	duel: one(duel, {
		fields: [round.idDuel],
		references: [duel.id]
	}),
}));

export const duelRelations = relations(duel, ({many}) => ({
	rounds: many(round),
}));

export const upgradeTypesRelations = relations(upgradeTypes, ({many}) => ({
	upgradeShops: many(upgradeShop),
}));