# API Reference
tutti i dati vengono inviati in formato json

## Auth
### /auth/login
- Richesta POST
- Request: { email: String, password: String}
    - In caso di mancati email e password -> HTTP 400
- Response: { authToken: String }

### /auth/tokenLogin
- Richesta POST
- Request: { authToken: String }
    - In caso di idPlayer mancante -> HTTP 400
    - In caso di authToken scaduto -> HTTP 401
- Response: { authToken: String }

### /auth/register
- Richiesta POST
- Request: {email: String, password: String, username: String}
  - In caso di dati mancanti -> HTTP 400
- Response: {idPlayer: int, authToken: String}

## Inventory
### /inventory/
- Richiesta POST
- Request: { authToken: String }
  - In caso di dati mancanti -> HTTP 400
- Response: {bullets:{}, weapons:{}, medikits:{}, upgrades:{}}

## Contracts
### /contracts/active/
- Richiesta POST
- Request: { authToken: String }
  - In caso di dati mancanti -> HTTP 400
- Response: [{id:number, name:string, requiredTime:number, startTime:number}]

### /contracts/available/
- Richiesta POST
- Request: { authToken: String }
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
- Request: { authToken: String, idContract:number }
  - In caso di dati mancanti -> HTTP 400
  - In caso di contratto non ancora terminato -> HTTP 400
- Response: {reward:number}

## Mercenaries
### /mercenaries/unassigned/
- Richiesta POST
- Request: { authToken: String }
  - In caso di dati mancanti -> HTTP 400
- Response: [{name:string, power:number}] Mercenari non ancora assegnati

### /mercenaries/available/
- Richiesta POST
- Request: { authToken: String }
  - In caso di dati mancanti -> HTTP 400
- Response: [{name:string, power:number, cost:number}]

### /mercenaries/employ/
- Richiesta POST
- Request: { authToken: String, idMercenary:number }
  - In caso di dati mancanti -> HTTP 400
- Response: {idMercenary: number}

## Shop
### /shop/weapons/
- Richiesta POST
- Request: { authToken: String }
  - In caso di dati mancanti -> HTTP 400
- Response: [{name:string, damage:number, cost:number, owned:boolean}]

### /shop/bullets/
- Richiesta POST
- Request: { authToken: String }
  - In caso di dati mancanti -> HTTP 400
- Response: [{name:string
     cost:number
     quantity:number
     amount:number
     capacity:number}]

### /shop/upgrades/
- Richiesta POST
- Request: { authToken: String }
  - In caso di dati mancanti -> HTTP 400
- Response: [{
    name:string,
    cost:number, 
    level:number
}] restituisce elenco upgrade del valore immediatamente successivo a quello già posseduto dal giocatore.

### /shop/medikits/
- Richiesta POST
- Request: { authToken: String }
  - In caso di dati mancanti -> HTTP 400
- Response: [{
    description:string,
    healthRecover:number,
    cost:number,
    quantity:number,
    capacity:number,
    amount:number}]

## Duel
### /duel/

## Bounty board
### /bounty/friends/
- Richiesta POST
- Request: { authToken: String }
  - In caso di dati mancanti -> HTTP 400
- Response: [{username: string, bounty: number}]


### /bounty/leaderboard/
- Richiesta GET
- Response: [{username: string, bounty: number}]

