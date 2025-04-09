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

## Shop
### /shop/

## Duel
### /duel/

## Bounty board
### /bounty/friends/
### /bounty/leaderboard/