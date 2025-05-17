import { login } from "../db/schema"
import { InferSelectModel } from "drizzle-orm"

export {}

//Hack to make the request module appear to have a player field as well
declare module "express-serve-static-core" {
    export interface Request {
        player?: InferSelectModel<typeof login>;
    }
}
