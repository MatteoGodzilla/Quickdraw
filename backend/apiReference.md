# API Reference
tutti i dati vengono inviati in formato json

## Auth
### /auth/login
- Richesta POST
- Request: { email: String, password: String}
    - In caso di mancati email e password -> HTTP 400
- Response: { idPlayer: Number, authToken: String }

### /auth/tokenLogin
- Richesta POST
- Request: { idPlayer: Number, authToken: String }
    - In caso di idPlayer mancante -> HTTP 400
    - In caso di authToken scaduto -> HTTP 401
- Response: { idPlayer: Number, authToken: String }

### /auth/register
- Richiesta POST
- Request: {email: String, password: String, username: String}
  - In caso di dati mancanti -> HTTP 400
- Response: {idPlayer: int, authToken: String}

## Inventory
### /inventory/
- Richiesta POST
- Request: { idPlayer: Number, authToken: String }
  - In caso di dati mancanti -> HTTP 400
- Response: {bullets:{}, weapons:{}, medikits:{}, upgrades:{}}

## Contracts
### /contracts/mercenaries/unassigned/
- Richiesta POST
- Request: { idPlayer: Number, authToken: String }
  - In caso di dati mancanti -> HTTP 400
- Response: [{name:string, power:number}] Mercenari non ancora assegnati

### /contracts/mercenaries/available/
- Richiesta POST
- Request: { idPlayer: Number, authToken: String }
  - In caso di dati mancanti -> HTTP 400
- Response: [{name:string, power:number, cost:number}]

### /contracts/active/
- Richiesta POST
- Request: { idPlayer: Number, authToken: String }
  - In caso di dati mancanti -> HTTP 400
- Response: [{id:number, name:string, requiredTime:number, startTime:number}]

### /contracts/available/
- Richiesta POST
- Request: { idPlayer: Number, authToken: String }
  - In caso di dati mancanti -> HTTP 400
- Response: [{name:string, requiredTime:number, maxMercenaries:number, startCost:number, requiredPower:number}]

### /contracts/redeem/
- Richiesta POST
- Request: { idPlayer: Number, authToken: String, idContract:number }
  - In caso di dati mancanti -> HTTP 400
  - In caso di contratto non ancora terminato -> HTTP 400
- Response: {reward:number}

### /contracts/mercenaries/employ/:idMercenary/:idPlayer/:token
- Richiesta POST
- Request: { idPlayer: Number, authToken: String, idMercenary:number }
  - In caso di dati mancanti -> HTTP 400
  - In caso di contratto non ancora terminato -> HTTP 400
- Response: 200 OK

## Shop
### /shop/weapons/
- Richiesta POST
- Request: { idPlayer: Number, authToken: String }
  - In caso di dati mancanti -> HTTP 400
- Response: [{name:string, damage:number, cost:number, owned:boolean}]

### /shop/bullets/
- Richiesta POST
- Request: { idPlayer: Number, authToken: String }
  - In caso di dati mancanti -> HTTP 400
- Response: [{name:string, cost:number, quantity:number, amount:number, capacity:number}]

### /shop/upgrades/
- Richiesta POST
- Request: { idPlayer: Number, authToken: String }
  - In caso di dati mancanti -> HTTP 400
- Response: [{name:string, cost:number, value:number}] restituisce elenco upgrade del valore immediatamente successivo a quello già posseduto dal giocatore.

### /shop/medikit/
- Richiesta POST
- Request: { idPlayer: Number, authToken: String }
  - In caso di dati mancanti -> HTTP 400
- Response: [{description:string, healthRecover:number, cost:number, quantity:number, capacity:number, amount:number}]

## Duel
### /duel/

## Bounty board
### /bounty/friends/

### /bounty/leaderboard/