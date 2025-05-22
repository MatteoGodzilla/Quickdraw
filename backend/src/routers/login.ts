import Express from "express";
import bcrypt from "bcrypt"; 
import { v7 as uuidv7 } from "uuid";
import { eq } from "drizzle-orm";

import { login, player } from "../db/schema";
import db from "../db/db";
const SALT_ROUNDS = 12

const router = Express.Router();
router.use(Express.json());

async function checkUserPassword(email: string, password: string) {
    const user = await getUserByEmail(email);
    if(user){
        return bcrypt.compareSync(password, user.password);
    }
    return false;
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

async function getUserByToken(token: string) {
    const user = await db.select()
        .from(login)
        .where(eq(login.authToken, token))
        .limit(1)
        .execute()
       
    if (user.length > 0) {
        return user[0]
    } else {
        return null
    }
}

async function insertUser(email: string, password: string, username: string) {
    const token = uuidv7();
    const ids = await db.insert(player)
        .values({ username: username })
        .$returningId();
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

function saveToken(email: string, token: string) {
    // Save the token in the database
    db.update(login)
        .set({authToken: token})
        .where(eq(login.email, email))
        .execute()
}

router.post("/login/", (req, res) => {
    console.log("RECEIVED LOGIN REQUEST")

    if(req.body == null){
        res.status(400).send();
        return;
    }

    let { email, password } = req.body
    if (!email || !password) {
        res.status(400).send()
    }
    // Check if user exists in the database
    checkUserPassword(email, password).then((verified) => {
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
});

router.post("/register/", (req, res) => {
    console.log("REGISTER RECEIVED");

    if(req.body == null){
        res.status(400).send();
        return;
    }

    let { email, password, username } = req.body
    if (!email || !password || !username) {
        res.status(400).send()
        return;
    }

    getUserByEmail(email).then((user) => {
        if (user) {
            res.status(400).send()
        } else {
            const hashedPassword = bcrypt.hashSync(password, SALT_ROUNDS)
            insertUser(email, hashedPassword, username).then((response) => {
                if(response.inserted) {
                    res.status(200).json({ idPlayer: response.id, authToken: response.token })
                } else {
                    res.status(500).send()
                }
            })
        }
    })
});

router.post("/tokenLogin/", (req, res) => {
    let token = req.body
    if (!token) {
        res.status(400).send()
    }

    // Check if user exists in the database
    getUserByToken(token).then((tokenUser) => {
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

export default router;
