import Express from "express";
import { eq, inArray, notInArray } from "drizzle-orm";

import db from "../db/db";
import { employedMercenary, assignedMercenary, activeContract, contract} from "../db/schema";
import { getPlayer } from "../middlewares/getPlayer";
const router = Express.Router();

function getEmployedMercenaries(idPlayer: number) {
    return db.select()
        .from(employedMercenary)
        .where(eq(employedMercenary.idPlayer, idPlayer))
        .execute()
        .then((mercenaries) => {
            if (mercenaries.length > 0) {
                return mercenaries
            } else {
                return null
            }
        })
}

function getAssignedMercenaries(idPlayer: number) {
    return getEmployedMercenaries(idPlayer).then((mercenaries) => {
        if (mercenaries) {
            return db.select()
                .from(assignedMercenary)
                .where(inArray(assignedMercenary.idEmployedMercenary, mercenaries.map((m) => m.idMercenary)))
                .execute()
                .then((assigned) => {
                    return assigned
                })
        } else {
            return null
        }
    })
}

function getActiveContracts(idPlayer:number) {
    return getAssignedMercenaries(idPlayer).then((mercenaries) => {
        if(mercenaries) {
            const contracts = mercenaries.map((mercenary) => {
                return db.select()
                .from(activeContract)
                .where(eq(activeContract.id, mercenary.idActiveContract))
                .execute()
                .then((assigned) => {
                    return assigned
                })
            })
            return Promise.all(contracts).then((active) => active.flat())
        } else {
            return null
        }
    })
}

router.use(getPlayer);

router.get("/available/", (req, res) => {
    // Get the available contracts from the database
    if(!req.player)
        return;
    
    getActiveContracts(req.player.idPlayer).then((activeContracts) => {
        const query = db.select().from(contract)
        if(activeContracts) {
            return query
                .where(notInArray(contract.id, activeContracts.map((c) => c.id)))
                .execute()
        } else {
            return query.execute()
        }
    }).then((a) => res.status(200).json(a));
})

export default router;
