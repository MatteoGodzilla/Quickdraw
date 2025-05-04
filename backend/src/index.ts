import Express from "express";
import dotenv from "dotenv";
import { drizzle } from "drizzle-orm/mysql2"
import { eq, desc } from "drizzle-orm";
import { upgradeTypes, login, player, friendship } from "./db/schema";
import bcrypt from "bcrypt" // bcrypt for hashing passwords
import { v7 as uuidv7 } from 'uuid'; // uuidv7 for generating unique tokens

const app = Express();
dotenv.config()
const db = drizzle(process.env.DATABASE_URL!)
const SALT_ROUNDS = 12

app.post("/auth/tokenLogin", (req, res) => {
    let { idPlayer, token } = req.body
    if (!idPlayer || !token) {
        res.status(400).send()
    }
    // Check if user exists in the database
    db.select()
        .from(login)
        .where(eq(login.idPlayer, idPlayer))
        .limit(1)
        .execute()
        .then((user) => {
            if (user.length > 0) {
                // Check if the token is valid
                if (user[0].authToken !== token) {
                    res.status(401).send()
                } else {
                    const newToken = uuidv7()
                    // Save the new token in the database
                    saveToken(user[0].email, newToken)
                    // Send the new token back to the user
                    res.status(200).json({ idPlayer: user[0].idPlayer, authToken: newToken })
                }
            } else {
                res.status(400).send()
            }
        })
        .catch((error) => {
            console.error(error)
            res.status(500).send()
        })
})

app.post("/auth/login", (req, res) => {
    let { email, password } = req.body
    if (!email || !password) {
        res.status(400).send()
    }
    // Check if user exists in the database
    verifyUser(email, password).then((verified) => {
        if (verified) {
            // Generate a token and send it back to the user
            const token = uuidv7()
            // Store the token in the database or in memory
            saveToken(email, token)
            getUserByEmail(email).then((user) => {
                if (user) {
                    res.status(200).json({ idPlayer: user.idPlayer, authToken: token })
                } else {
                    res.status(500).send()
                }
            })
        } else {
            res.status(400).send()
        }
    })
})

function saveToken(email: string, token: string) {
    // Save the token in the database
    db.update(login)
        .set({authToken: token})
        .where(eq(login.email, email))
        .execute()
}

function passwordHash(password: string) {
    return bcrypt.hashSync(password, SALT_ROUNDS)
}

function verifyUser(email: string, password: string) {
    return getUserByEmail(email).then((user) => {
        if (user) {
            const hashedPassword = user.password
            return bcrypt.compareSync(password, hashedPassword)
        } else {
            return false
        }
    })
}

async function getUserByEmail(email: string) {
    const user = await db.select()
        .from(login)
        .where(eq(login.email, email))
        .limit(1)
        .execute()
    if (user.length > 0) {
        return user[0]
    } else {
        return null
    }
}

app.post("/auth/register", (req, res) => {
    let { email, password, username } = req.body
    if (!email || !password) {
        res.status(400).send()
    }
    getUserByEmail(email).then((user) => {
        if (user) {
            res.status(400).send()
        } else {
            const hashedPassword = passwordHash(password)
            insertUser(email, hashedPassword, username).then((response) => {
                if(response.inserted) {
                    res.status(200).json({ idPlayer: response.id, authToken: response.token })
                } else {
                    res.status(500).send()
                }
            })
        }
    })
})

async function insertUser(email: string, password: string, username: string) {
    const token = uuidv7()
    const ids = await db.insert(player)
        .values({ username: username })
        .$returningId()
    const result = await db.insert(login)
        .values({
            email: email,
            password: password,
            idPlayer: ids[0].id,
            authToken: token
        })
        .execute()
    return { inserted: result, id: ids[0].id, token: token }
}

app.get("bounty/friends", (req, res) => {
    // Get the friends of the player from the database
    const { idPlayer, token } = req.body
    if (!idPlayer || !token) {
        res.status(400).send()
    } else {
        verifyWithToken(idPlayer, token).then((verified) => {
            if (verified) {
                db.select()
                    .from(friendship)
                    .where(eq(friendship.idPlayerFrom, idPlayer))
                    .fullJoin(player, eq(friendship.idPlayerFriend, player.id))
                    .orderBy(desc(player.bounty))
                    .execute()
                    .then((friends) => {
                        res.status(200).json(friends)
                    })
                    .catch((error) => {
                        console.error(error)
                        res.status(500).send()
                    })
            } else {
                res.status(401).send()
            }
        })
    }
})

function verifyWithToken(idPlayer: number, token: string) {
    // Check if the user exists in the database
    return db.select()
        .from(login)
        .where(eq(login.idPlayer, idPlayer))
        .limit(1)
        .execute()
        .then((user) => {
            if (user.length > 0) {
                // Check if the token is valid
                return user[0].authToken === token
            } else {
                return false
            }
        })
        .catch((error) => {
            console.error(error)
            return false
        })
}

app.get("/bounty/leaderboard", (req, res) => {
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

app.listen(process.env.PORT)
/*
//dotenv.configDotenv()
//const db = drizzle(process.env.DATABASE_URL!)
async function getUpgradeTypes(){
    const types = await db.select().from(upgradeTypes);
    console.log(types)
}

getUpgradeTypes()
*/
