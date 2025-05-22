import { eq, inArray } from "drizzle-orm";
import db from "./db/db";
import { employedMercenary, assignedMercenary, activeContract} from "./db/schema";

export async function getEmployedMercenaries(idPlayer: number) {
    const mercenaries = await db.select()
        .from(employedMercenary)
        .where(eq(employedMercenary.idPlayer, idPlayer))
        .execute();
    if(mercenaries.length > 0){
        return mercenaries;
    }
    return null;
}

export async function getAssignedMercenaries(idPlayer: number) {
   const employedMercenaries = await getEmployedMercenaries(idPlayer);  
   if(employedMercenaries){
        return db.select()
            .from(assignedMercenary)
            .where(inArray(assignedMercenary.idEmployedMercenary, employedMercenaries.map((m) => m.idMercenary)))
            .execute()
   }
   return null;
}

export async function getActiveContracts(idPlayer:number) {
    const assignedMercenaries = await getAssignedMercenaries(idPlayer);

    if(assignedMercenaries){
        const contracts = assignedMercenaries.map((mercenary) => {
            return db.select()
                .from(activeContract)
                .where(eq(activeContract.id, mercenary.idActiveContract))
                .execute()
        })
        return await Promise.all(contracts).then((active) => active.flat())
    }
    return null;
}
