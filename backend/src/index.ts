import Express from "express";
import dotenv from "dotenv";
import { drizzle } from "drizzle-orm/mysql2"
import { upgradeTypes } from "./db/schema";
import bcrypt from "bcrypt"
import { v7 as uuidv7 } from 'uuid';

console.log(bcrypt.hashSync("password", 12))
console.log(uuidv7())
/*
//dotenv.configDotenv()
//const db = drizzle(process.env.DATABASE_URL!)
async function getUpgradeTypes(){
    const types = await db.select().from(upgradeTypes);
    console.log(types)
}

getUpgradeTypes()
*/
