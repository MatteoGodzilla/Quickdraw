import Express from "express";
import { eq, notInArray } from "drizzle-orm";

import db from "../db/db";
import { employedMercenary, assignedMercenary, activeContract, contract, player} from "../db/schema";
import { getPlayer } from "../middlewares/getPlayer";
import { getActiveContracts, } from "../utils"; 

const router = Express.Router();

router.use(getPlayer);

router.post("/available/", async (req, res) => {
    // Get the available contracts from the database
    if(!req.player)
        return;
   
    const activeContracts = await getActiveContracts(req.player.idPlayer);
    const result = await db.select({
        name: contract.name,
        requiredTime: contract.requiredTime,
        maxMercenaries: contract.maxMercenaries,
        startCost: contract.startCost,
        requiredPower: contract.requiredPower
    }).from(contract)
        .where(activeContracts ? notInArray(contract.id, activeContracts.map(c => c.id)) : undefined)
        .execute()

    res.status(200).json(result);
})

router.post("/active/", async (req, res) => {
    if(!req.player)
        return;

    const activeContracts = await getActiveContracts(req.player.idPlayer);
    if(activeContracts){
        const promises = activeContracts.map(async (ac) => {
            const contractData = await db.select()
                .from(contract)
                .where(eq(contract.id, ac.idContract))
                .limit(1)
                .execute()
            return {
                id: ac.id,
                name: contractData[0].name,
                startTime: ac.startTime,
                requiredTime: contractData[0].requiredTime
            }
        });
        Promise.all(promises).then(list => res.status(200).json(list));
        return;
    }
    res.status(200).json([]);
})

router.post("/redeem/", async (req, res) => {
    if(!req.player || !req.body || !req.body.idContract){
        res.status(400).send();
        return;
    }

    const result = await db.select()
        .from(activeContract)
        .where(eq(activeContract.id, req.body.idContract))
        .limit(1)
        //.innerJoin(contract, eq(activeContract.idContract, contract.id))
        .execute();

    if(result.length == 0){
        res.status(200).json({});
        return;
    }

    const contract = result[0];

    if(Date.now() - Number(contract.startTime)){
        //Player can actually redeem because enough time has passed

        //Sanity check: get the players' id of the mercenaries assigned
        const result = await db.selectDistinct({idPlayer: employedMercenary.idPlayer})
            .from(assignedMercenary)
            .innerJoin(employedMercenary, eq(assignedMercenary.idEmployedMercenary, employedMercenary.id))
            .execute();

        if(result.length == 1 && result[0].idPlayer == req.player.idPlayer){
            //the player is correct
            //Actually give the reward now and reset employed mercenaries

            const playerObj = await db.select().from(player).where(eq(player.id, req.player.idPlayer)).execute();
            console.log(playerObj);
            console.log(contract);

            await db.update(player)
                .set({money: playerObj[0].money + contract.reward})
                .where(eq(player.id, req.player.idPlayer))
                .execute()

            await db.delete(assignedMercenary).where(eq(assignedMercenary.idActiveContract, contract.id))
            await db.delete(activeContract).where(eq(activeContract.id, contract.id))

            res.status(201).json({ reward: contract.reward });
            return;
        } else {
            //Id does not match -> 
            res.status(403).send();
            return;
        }
    }
    res.status(200).json({});
})

export default router;
