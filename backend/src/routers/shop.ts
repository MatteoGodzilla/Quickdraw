import Express from "express";
import dotenv from "dotenv"; 
import { eq, sql, and, max } from "drizzle-orm";
import { drizzle } from "drizzle-orm/mysql2";
import { 
    medikitShop, medikit, playerMedikit, 
    bulletShop, bullet, playerBullet,
    upgradeShop, playerUpgrade,
    weapon, playerWeapon
} from "../db/schema";

import { getPlayer } from "../middlewares/getPlayer";

dotenv.config();
const db = drizzle(process.env.DATABASE_URL!);

const router = Express.Router();

router.use(getPlayer);

router.post("/weapons/", (req, res) => {
    db.select({
        name: weapon.name,
        damage: weapon.damage,
        cost: weapon.cost,
        bulletsShot: weapon.bulletsShot,
        ownedTemp: sql<number>`${playerWeapon.idPlayer} IS NOT NULL`
    }).from(weapon)
        .leftJoin(playerWeapon, and(
            eq(weapon.id, playerWeapon.idWeapon),
            eq(playerWeapon.idPlayer, req.player!.idPlayer)
        ))
        .execute()
        .then(rows => {
            //adjust because sql handles booleans as tinyint,
            //when the json should really send actual true and false
            //i would've just added owned to the object and removed ownedTemp, but typescript disagrees
            const result = rows.map(row => {
                return {
                    name: row.name,
                    damage: row.damage,
                    cost: row.cost,
                    bulletsShot: row.bulletsShot,
                    owned: (row.ownedTemp > 0)
                };
            });
            res.status(200).json(result);
        });
})

router.post("/bullets/", (req, res) => {
    db.select({
       name: bullet.description, 
       cost: bulletShop.cost,
       quantity: bulletShop.quantity,
       amount: sql<Number>`COALESCE(${playerBullet.amount}, 0)`
    }).from(bulletShop)
        .innerJoin(bullet, eq(bulletShop.idBullet, bullet.type))
        .leftJoin(playerBullet, and(
            eq(bulletShop.idBullet, playerBullet.idBullet),
            eq(playerBullet.idPlayer, req.player!.idPlayer)
        ))
        .execute()
        .then(rows => res.status(200).json(rows));
})

router.post("/upgrades/", (req, res) => {
    const playerMaxUpgrades = db.$with("playerMax").as(
        db.select({
            type: upgradeShop.type,
            level: max(upgradeShop.level).as("maxLevel")
        })
        .from(playerUpgrade)
        .innerJoin(upgradeShop, and(
            eq(upgradeShop.idUpgrade, playerUpgrade.idUpgrade),
            eq(playerUpgrade.idPlayer, req.player!.idPlayer)
        ))
        .groupBy(upgradeShop.type)
   );

    db.with(playerMaxUpgrades).select().from(upgradeShop)
        .leftJoin(playerMaxUpgrades, eq(playerMaxUpgrades.type, upgradeShop.type))
        .where(sql`coalesce(${playerMaxUpgrades.level},0) + 1 = ${upgradeShop.level}`)
        .execute()
        .then(rows => res.status(200).json(rows))

})

router.post("/medikits/", (req, res) => {
    db.select({
       description: medikit.description, 
       healthRecover: medikit.healthRecover,
       cost: medikitShop.cost,
       quantity: medikitShop.quantity,
       amount: sql<Number>`COALESCE(${playerMedikit.amount}, 0)`
    }).from(medikitShop)
        .innerJoin(medikit, eq(medikitShop.idMedikit, medikit.id))
        .leftJoin(playerMedikit, and(
            eq(medikitShop.idMedikit, playerMedikit.idMedikit),
            eq(playerMedikit.idPlayer, req.player!.idPlayer)
        ))
        .execute()
        .then(rows => res.status(200).json(rows));
})

export default router;
