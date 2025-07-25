import Express from "express";
import dotenv from "dotenv";

import loginRouter from "./routers/login";
import shopRouter from "./routers/shop";
import inventoryRouter from "./routers/inventory";
import contractsRouter from "./routers/contracts";
import bountyRouter from "./routers/bounty";
import mercenaryRouter from "./routers/mercenary";

dotenv.config();

const app = Express();
app.use(Express.json())

app.use("/api/auth/", loginRouter);
app.use("/api/bounty/", bountyRouter);
app.use("/api/contracts/", contractsRouter);
app.use("/api/inventory/", inventoryRouter);
app.use("/api/mercenary/", mercenaryRouter);
app.use("/api/shop/", shopRouter);

app.listen(process.env.PORT, ()=>console.log(`Listening on port ${process.env.PORT}` ))
