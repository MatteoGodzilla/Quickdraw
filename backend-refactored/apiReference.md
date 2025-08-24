# API Reference
tutti i dati vengono inviati in formato json
QUESTE API SONO DESIGNATE PER CREARE MODIFICHE INCREMENTALI NELL'APPLICATIVO MOBILE

## Auth
### /auth/login X
- Richesta POST
- Request: { 
    email: String,
    password: String
}
    - In caso di mancati email e password -> HTTP 400
- Response: { 
    authToken: String 
}

### /auth/tokenLogin X
- Richesta POST
- Request: { 
    authToken: String 
}
    - In caso di idPlayer mancante -> HTTP 400
    - In caso di authToken scaduto -> HTTP 401
- Response: { 
    authToken: String 
}

### /auth/register X
- Richiesta POST
- Request: {
    email: String,
    password: String,
    username: String
}
  - In caso di dati mancanti -> HTTP 400
- Response: { 
    authToken: String 
}

## Inventory 
### /inventory/ X
- Richiesta POST
- Request: { 
    authToken: String 
}
  - In caso di dati mancanti -> HTTP 400
- Response: {
    bullets:{ vedere stuttura per shop/bullets },
    weapons:{ vedere struttura per shop/weapons },
    medikits:{ vedere struttura per shop/medikits },
    upgrades:{ vedere struttura per shop/upgrades }
}

## Contracts
### /contracts/active/ 
- Richiesta POST
- Request: { 
    authToken: String 
}
  - In caso di dati mancanti -> HTTP 400
- Response: [{
    id:number,
    name:string,
    requiredTime:number,
    startTime:number,
    mercenaries:[]
}]

### /contracts/available/
- Richiesta POST
- Request: { 
    authToken: String
}
  - In caso di dati mancanti -> HTTP 400
- Response: [{
     name:string
     requiredTime:number
     maxMercenaries:number
     startCost:number
     requiredPower:number
 }]

### /contracts/start
- Richiesta POST
- Request: {
    authToken: String,
    mercenaries: [number]
}
    - In caso di dati mancanti -> HTTP 400
    - In caso di soldi non sufficienti del giocatore -> HTTP 402
    - In caso di dati non validi -> HTTP 400
- Response: HTTP 200 Ok 

### /contracts/redeem/
- Richiesta POST
- Request: { 
    authToken: String,
    idContract:number 
}
  - In caso di dati mancanti -> HTTP 400
  - In caso di contratto non ancora terminato -> HTTP 400
- Response: {
    reward:number
}

## Mercenaries
### /mercenaries/player/all
- Richiesta POST
- Request: { 
    authToken: String
}
  - In caso di dati mancanti -> HTTP 400
- Response: [{name:string, power:number}] Mercenari posseduti dal player

### /mercenaries/player/unassigned
- Richiesta POST
- Request: { 
    authToken: String
}
  - In caso di dati mancanti -> HTTP 400
- Response: [{name:string, power:number}] Mercenari posseduti dal player che non sono assegnati a contratto


### /mercenaries/hirable/
- Richiesta POST
- Request: { 
    authToken: String
}
  - In caso di dati mancanti -> HTTP 400
- Response: [{name:string, power:number, cost:number}] Mercenari non posseduti dal player e che sono comprabili (livello dispondibile)

### /mercenaries/nextUnlockables
- Richiesta POST
- Request: { 
    authToken: String
}
  - In caso di dati mancanti -> HTTP 400
- Response: [{name:string, power:number, levelRequired:number}] Mercenari più vicini ad essere sbloccati,l'array può essere vuoto

### /mercenaries/employ/
- Richiesta POST
- Request: { 
    authToken: String,
    idMercenary:number 
}
  - In caso di dati mancanti -> HTTP 400
- Response: {idMercenary: number}

## Shop
### /shop/weapons/
- Richiesta POST
- Request: { 
    authToken: String 
}
  - In caso di dati mancanti -> HTTP 400
- Response: [{
    id:number,
    name:string,
    damage:number,
    cost:number,
}]
Il risultato è filtrato per gli oggetti che il giocatore non possiede

### /shop/weapons/buy
- Richiesta POST
- Request: {
    authToken: string,
    id: number
}
    - In caso di dati mancanti -> HTTP 400
- Response:
    - Se il giocatore non ha abbastanza denaro, HTTP 402 (si accettano opinioni)
    - Se il giocatore ha abbastanza denaro, HTTP 200 + body con lo stesso oggetto di /shop/weapons

### /shop/bullets/
- Richiesta POST
- Request: { 
    authToken: String 
}
  - In caso di dati mancanti -> HTTP 400
- Response: [{
    id:number,
    name:string,
    cost:number,
    quantity:number,
    capacity:number
}]
Per quelli posseduti dal giocatore, si fa riferimento alla quantità in inventory

### /shop/bullets/buy
- Richiesta POST
- Request: {
    authToken: string,
    id:number
}
    - In caso di dati mancanti -> HTTP 400
- Response:
    - Se il giocatore non ha abbastanza denaro, HTTP 402 (si accettano opinioni)
    - Se il giocatore ha abbastanza denaro, HTTP 200 + body con lo stesso oggetto di /shop/bullets

### /shop/upgrades/
- Richiesta POST
- Request: { 
    authToken: String
}
  - In caso di dati mancanti -> HTTP 400
- Response: [{
    id:number
    name:string,
    cost:number, 
    level:number
}] 
restituisce elenco upgrade del valore immediatamente successivo a quello già posseduto dal giocatore.

### /shop/upgrades/buy
- Richiesta POST
- Request: {
    authToken: string,
    id: number
}
    - In caso di dati mancanti -> HTTP 400
- Response:
    - Se il giocatore non ha abbastanza denaro, HTTP 402 (si accettano opinioni)
    - Se il giocatore ha abbastanza denaro, HTTP 200 + body con lo stesso oggetto di /shop/upgrades
    

### /shop/medikits/
- Richiesta POST
- Request: {
    authToken: String 
}
  - In caso di dati mancanti -> HTTP 400
- Response: [{
    id:number
    description:string,
    healthRecover:number,
    cost:number,
    quantity:number,
    capacity:number,
}]
Per la quantità dei medikit, fare riferimento all'inventario

### /shop/medikits/buy
- Richiesta POST
- Request: {
    authToken: string,
    id: number
}
    - In caso di dati mancanti -> HTTP 400
- Response:
    - Se il giocatore non ha abbastanza denaro, HTTP 402 (si accettano opinioni)
    - Se il giocatore ha abbastanza denaro, HTTP 200 + body con lo stesso oggetto di /shop/medikits

## Player
### /status/
- Richiesta POST
- Request: {
    authToken: string
}
    - In caso di dati mancanti -> HTTP 400
- Response: {
    health: number,
    maxHealth: number,
    exp: number,
    money: number,
    bounty: number
}

### /status/levels
- Richiesta GET
- Response: [number]
Lista di punti exp richiesti, in ordine crescente, dal livello 1 in poi

### /use/medikit
- Richiesta POST
- Request: {
    authToken: string,
    id: number
}
- Response: {
    newHealth: number,
    amountLeft: number
}

## Duel
### /duel/
TODO: da migliorare una volta implementato il loop vero di gioco
- Richiesta POST 
- Request: {
    authToken: string,
    rounds:[{
        won: boolean,
        idWeaponUsed: number,
        bulletsUsed: number,
        damage: number
    }]
}
    - In caso di dati mancanti -> HTTP 400
- Response: HTTP 200 Ok


## Bounty board
### /bounty/friends/
- Richiesta POST
- Request: {
    authToken: String 
}
  - In caso di dati mancanti -> HTTP 400
- Response: [{
    username: string,
    bounty: number
}] 
lista ordinata dall'utente con il bounty più alto a quello più basso

### /bounty/leaderboard/
- Richiesta GET
- Response: [{
    username: string,
    bounty: number
}]
lista ordinata dall'utente con il bounty più alto a quello più basso

## Images
### /image/weapon
- Richiesta POST
- Request: { id:number }
    - In caso di dati mancanti -> HTTP 404 Not found
- Response: { image: blob as base64 } 

### /image/bullet
- Richiesta POST
- Request: { id:number }
    - In caso di dati mancanti -> HTTP 404 Not found
- Response: { image: blob as base64 } 

### /image/medikit
- Richiesta POST
- Request: { id:number }
    - In caso di dati mancanti -> HTTP 404 Not found
- Response: { image: blob as base64 } 

### /image/upgrade
- Richiesta POST
- Request: { id:number }
    - In caso di dati mancanti -> HTTP 404 Not found
- Response: { image: blob as base64 } 

### /image/player
- Richiesta POST
- Request: { id:number }
    - In caso di dati mancanti -> HTTP 404 Not found
- Response: { image: blob as base64 } 

### /image/updatePic
- Richiesta POST
- Request: {
    authToken: string,
    image: blob as base64
}
    - In caso di dati mancanti -> HTTP 400
- Response: 
    - Se non viene mandata un'immagine -> HTTP 400
    - Successo -> HTTP 200 Ok

### /bandits/pool
- Richiesta POST
- Request: {
    authToken: string
}
    - In caso di dati mancanti -> HTTP 400
-Response:
    - se il player ha già una pool request attiva, si restituiscono le banditIstance presenti
    - se il player ha una pool request scaduta, si aggiorna la timestamp di scadenza e si creano nuove banditIstance
{idIstance: int , expires:timestamp, stats:bandit}
dove bandit è una riga della table Bandit

### /bandit/freeze
- Richiesta POST
- Request: {
    authToken: string,
    idIstance: string
}

Per evitare che un player affronti un bandit prima della scadenza della request,per poi finire la partita dopo la scadenza, si freeza l'istance.
Solo un istance alla volta di bandit per player può essere frozen

### /bandit/fight
- Richiesta POST
- Request: {
    authToken: string,
    idIstance: string,
    rounds: [pair(playerWinsRound:bool,damage)]
}
    - In caso di dati mancanti -> HTTP 400
    - In caso di istance non valida -> HTTP 403 
    - In caso il bandit non fosse frozen -> HTTP 403

(assoulatamente cheattabile da un player ma non è il momento di preocuparsi di ciò)

- se i rounds indicano vittoria del player: restituire prize in base al bandit-> {money:int,exp:int}
- se i rounds indicano vittoria del bot: restituire array vuoto