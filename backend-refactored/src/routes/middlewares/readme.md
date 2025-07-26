This folder contains function for repeated procedures like token validation and player-id token association.

Every function must return a dictionary with a Success value.
-   If Success = True,then the dictionary will have additional values based on the context.
-   If Success = False,then the dictionary MUST ALWAYS CONTAIN a error string and a http code.