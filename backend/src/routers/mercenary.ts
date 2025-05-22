import Express from "express";
import { eq, notInArray } from "drizzle-orm";
import { employedMercenary, mercenary, player} from "../db/schema";

import db from "../db/db";
import { getPlayer } from "../middlewares/getPlayer";
import { getAssignedMercenaries, getEmployedMercenaries } from "../utils";

const router = Express.Router();
router.use(getPlayer);

router.post("/mercenaries/unassigned", async (req, res) => {
    if(!req.player)
        return;

    const assignedMercenaries = await getAssignedMercenaries(req.player.idPlayer);
    const result = await db.select({
        name: mercenary.name,
        power: mercenary.power
    }).from(employedMercenary)
        .innerJoin(mercenary, eq(employedMercenary.idMercenary, mercenary.id))
        .where(assignedMercenaries ? notInArray(employedMercenary.id, assignedMercenaries.map(am => am.idEmployedMercenary)) : undefined)
        .execute()

    res.status(200).json(result);
})

router.post("/available", async (req, res) => {
    if(!req.player)
        return;

    const employedMercenaries = await getEmployedMercenaries(req.player.idPlayer);
    const result = await db.select().from(mercenary)
        .where(employedMercenaries ? notInArray(mercenary.id, employedMercenaries.map(e => e.idMercenary)) : undefined)
        .execute()
    
    res.status(200).json(result);
})

router.post("/employ", async (req, res) => {
    if(!req.player || !req.body || !req.body.idMercenary){
        res.status(400).send();
        return;
    }

    const playersInfo = await db.select().from(player).where(eq(player.id, req.player.idPlayer)).execute()
    const employees = await db.select().from(mercenary).where(eq(mercenary.id, req.body.idMercenary))

    if(playersInfo.length == 0 || employees.length == 0){
        res.status(400).send();
        return;
    }

    const playerInfo = playersInfo[0];
    const employee = employees[0];

    if(playerInfo.money >= employee.employmentCost){
        //player can buy mercenary

        await db.update(player).set({money: playerInfo.money - employee.employmentCost}).where(eq(player.id, req.player.idPlayer)).execute()
        const employeeId = await db.insert(employedMercenary).values({idPlayer: req.player.idPlayer, idMercenary: employee.id}).$returningId();

        res.status(200).json({ idEmployedMercenary: employeeId[0].id });
        return;
    }

    res.status(200).json({});
})

export default router;
