import Express from "express";
import BodyParser from "body-parser";
import dotenv from "dotenv";
import { drizzle } from "drizzle-orm/mysql2"
import { eq, desc, notInArray, inArray } from "drizzle-orm";
import { upgradeTypes, login, player, friendship, playerBullet, playerMedikit, playerWeapon, playerUpgrade, employedMercenary, assignedMercenary, activeContract, contract } from "./db/schema";
import bcrypt from "bcrypt" // bcrypt for hashing passwords
import { v7 as uuidv7 } from 'uuid'; // uuidv7 for generating unique tokens

dotenv.config()
const app = Express();
app.use(BodyParser.json())
const db = drizzle(process.env.DATABASE_URL!)
const SALT_ROUNDS = 12

app.post("/auth/tokenLogin", (req, res) => {
    let token = req.body
    if (!token) {
        res.status(400).send()
    }
    // Check if user exists in the database
    getPlayerByToken(token).then((tokenUser) => {
        if (tokenUser) {
            db.select()
            .from(login)
            .where(eq(login.idPlayer, tokenUser.idPlayer))
            .limit(1)
            .execute()
            .then((user) => {
                if (user.length > 0) {
                    const newToken = uuidv7()
                    // Save the new token in the database
                    saveToken(user[0].email, newToken)
                    // Send the new token back to the user
                    res.status(200).json({ authToken: newToken })
                } else {
                    res.status(400).send()
                }
            })
            .catch((error) => {
                console.error(error)
                res.status(500).send()
            })
        } else {
            res.status(400).send()
        }
    })
})

function getPlayerByToken(token: string) {
    return db.select()
        .from(login)
        .where(eq(login.authToken, token))
        .limit(1)
        .execute()
        .then((user) => {
            if (user.length > 0) {
                return user[0]
            } else {
                return null
            }
        })
}

app.post("/auth/login", (req, res) => {
    console.log("RECEIVED LOGIN REQUEST")
    console.log(req.body)
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

app.get("bounty/friends/", (req, res) => {
    // Get the friends of the player from the database
    const token = req.body.authToken
    if (!token) {
        res.status(400).send()
    } else {
        verifyToken(token).then((verified) => {
            if (verified) {
                getPlayerByToken(token).then((tokenUser) => {
                    if(tokenUser) {
                        // Get the friends of the player from the database
                        db.select()
                        .from(friendship)
                        .where(eq(friendship.idPlayerFrom, tokenUser.idPlayer))
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
                        res.status(400).send()
                    }
                })
            } else {
                res.status(401).send()
            }
        })
    }
})

function verifyToken(token: string) {
    return db.select()
        .from(login)
        .where(eq(login.authToken, token))
        .limit(1)
        .execute()
        .then((user) => {
            return user.length > 0
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

app.get("/inventory/", (req, res) => {
    // Get the inventory of the player from the database")
    const token = req.body.authToken
    if (!token) {
        res.status(400).send()
    }
    verifyToken(token).then((verified) => {
        if (verified) {
            getPlayerByToken(token).then((tokenUser) => {
                if(tokenUser) {
                    const bullets = getPlayerBullets(tokenUser.idPlayer)
                    const medikits = getPlayerMedikits(tokenUser.idPlayer)
                    const weapons = getPlayerWeapons(tokenUser.idPlayer)
                    const upgrades = getPlayerUpgrades(tokenUser.idPlayer)
                    Promise.all([bullets, medikits, weapons, upgrades]).then((results) => {
                        res.status(200).json({
                            bullets: results[0],
                            medikits: results[1],
                            weapons: results[2],
                            upgrades: results[3]
                        })
                    }).catch((error) => {
                        console.error(error)
                        res.status(500).send()
                    }).catch((error) => {
                        console.error(error)
                        res.status(500).send()
                    })
                } else {
                    res.status(400).send()
                }
            })
        } else {
            res.status(401).send()
        }
    })
})

app.get("/contracts/available", (req, res) => {
    // Get the available contracts from the database
    const token = req.body.authToken
    if (!token) {
        res.status(400).send()
    }
    verifyToken(token).then((verified) => {
        if (verified) {
            getPlayerByToken(token).then((tokenUser) => {
                if(tokenUser) {
                    const available = getActiveContracts(tokenUser.idPlayer).then((activeContracts) => {
                        const query = db.select().from(contract)
                        if(activeContracts) {
                            return query
                                .where(notInArray(contract.id, activeContracts.map((c) => c.id)))
                                .execute()
                        } else {
                            return query.execute()
                        }
                    })
                    available.then((a) => res.status(200).json(a)
                )
                } else {
                    res.status(400).send()
                }
            })
        } else {
            res.status(401).send()
        }
    })
})

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

function getPlayerBullets(idPlayer: number) {
    return db.select()
        .from(playerBullet)
        .where(eq(playerBullet.idPlayer, idPlayer))
        .execute()
}

function getPlayerMedikits(idPlayer: number) {
    return db.select()
        .from(playerMedikit)
        .where(eq(playerMedikit.idPlayer, idPlayer))
        .execute()
}

function getPlayerWeapons(idPlayer: number) {
    return db.select()
        .from(playerWeapon)
        .where(eq(playerWeapon.idPlayer, idPlayer))
        .execute()
}

function getPlayerUpgrades(idPlayer: number) {
    return db.select()
        .from(playerUpgrade)
        .where(eq(playerUpgrade.idPlayer, idPlayer))
        .execute()
}

app.listen(process.env.PORT)