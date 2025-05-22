import Express from "express"; 
import { eq } from "drizzle-orm";

import db from "../db/db";
import { playerBullet, playerMedikit, playerWeapon, playerUpgrade } from "../db/schema";
import { getPlayer } from "../middlewares/getPlayer";

const router = Express.Router();

router.use(getPlayer);

router.get("/", (req, res) => {
    if(!req.player)
        return;
    req.player.idPlayer
    const bullets = getPlayerBullets(req.player.idPlayer)
    const medikits = getPlayerMedikits(req.player.idPlayer)
    const weapons = getPlayerWeapons(req.player.idPlayer)
    const upgrades = getPlayerUpgrades(req.player.idPlayer)
    Promise.all([bullets, medikits, weapons, upgrades]).then((results) => {
        res.status(200).json({
            bullets: results[0],
            medikits: results[1],
            weapons: results[2],
            upgrades: results[3]
        })
    }).catch((error) => {
        console.error(error)
        res.status(500).send()
    })
})

function getPlayerBullets(idPlayer: number) {
    return db.select()
        .from(playerBullet)
        .where(eq(playerBullet.idPlayer, idPlayer))
        .execute()
}

function getPlayerMedikits(idPlayer: number) {
    return db.select()
        .from(playerMedikit)
        .where(eq(playerMedikit.idPlayer, idPlayer))
        .execute()
}

function getPlayerWeapons(idPlayer: number) {
    return db.select()
        .from(playerWeapon)
        .where(eq(playerWeapon.idPlayer, idPlayer))
        .execute()
}

function getPlayerUpgrades(idPlayer: number) {
    return db.select()
        .from(playerUpgrade)
        .where(eq(playerUpgrade.idPlayer, idPlayer))
        .execute()
}

export default router;
