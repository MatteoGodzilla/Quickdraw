import Express from "express";
import dotenv from "dotenv";
import { drizzle } from "drizzle-orm/mysql2"
import { upgradeTypes } from "./db/schema";

dotenv.configDotenv()
const db = drizzle(process.env.DATABASE_URL!)

async function getUpgradeTypes(){
    const types = await db.select().from(upgradeTypes);
    for (const t of types) {
        console.log(t.description)
    }
}

getUpgradeTypes()


/*
const app = Express();

app.get("/", (req, res) => {
    let obj : {
        id:number,
        description:string
    } = {
        id:69,
        description: "inyextynoiwxgnyixwt"
    };
    res.send(obj);
})

app.listen(3000);
*/
