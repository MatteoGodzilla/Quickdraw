import Express from "express";
import { eq, desc } from "drizzle-orm";

import db from "../db/db";
import { player, friendship } from "../db/schema";
import { getPlayer } from "../middlewares/getPlayer";

const router = Express.Router();

router.get("/leaderboard/", (_, res) => {
    // Get the leaderboard from the database
    db.select({ username: player.username, bounty: player.bounty })
        .from(player)
        .orderBy(desc(player.bounty))
        .execute()
        .then((leaderboard) => {
            res.status(200).json(leaderboard)
        })
        .catch((error) => {
            console.error(error)
            res.status(500).send()
        })
})

router.use(getPlayer);

router.post("/friends/", (req, res) => {
    if(!req.player)
        return;
    db.select({ username: player.username, bounty: player.bounty })
        .from(friendship)
        .where(eq(friendship.idPlayerFrom, req.player.idPlayer)) 
        .innerJoin(player, eq(friendship.idPlayerFriend, player.id))
        .orderBy(desc(player.bounty))
        .execute()
        .then((friends) => {
            res.status(200).json(friends)
        })
        .catch((error) => {
            console.error(error)
            res.status(500).send()
        })
})

export default router;

