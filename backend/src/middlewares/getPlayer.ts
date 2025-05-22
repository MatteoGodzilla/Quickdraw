import { Response, NextFunction } from "express";
import { eq } from "drizzle-orm";
import { drizzle } from "drizzle-orm/mysql2";
import { login } from "../db/schema"; 
import dotenv from "dotenv";

dotenv.config();
const db = drizzle(process.env.DATABASE_URL!);

//This middleware assumes that body parser is set up for json

export function getPlayer(req: any, res: Response, next: NextFunction): void{
    if(req.body == null){
        res.status(400).send();
        return;
    }
    const token = req.body.authToken;
    if(!token){
        res.status(400).send();
        return;
    }
    //actually get player from token
    db.select().from(login).where(eq(login.authToken, token)).limit(1).execute()
        .then(player => {
            if(player.length > 0){
                req.player = player[0];
                next();
            } else {
                req.status(401).send();
            }
        })
        .catch(err => {
            console.log(err);
            res.status(500).send();
        })
}
