# API Reference
tutti i dati vengono inviati in formato json

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
    startTime:number
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
### /mercenaries/unassigned/
- Richiesta POST
- Request: { 
    authToken: String
}
  - In caso di dati mancanti -> HTTP 400
- Response: [{name:string, power:number}] Mercenari non ancora assegnati

### /mercenaries/available/
- Richiesta POST
- Request: { 
    authToken: String
}
  - In caso di dati mancanti -> HTTP 400
- Response: [{name:string, power:number, cost:number}]

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
    owned:boolean
}]

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
    id:number
    name:string
    cost:number
    quantity:number
    amount:number
    capacity:number
}]

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
    amount:number
}]


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

## Duel
### /duel/
TODO: finish this

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
