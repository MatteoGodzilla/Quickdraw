package com.example.quickdraw.game.dataDisplayers

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import com.example.quickdraw.R
import com.example.quickdraw.network.data.EmployMercenary
import com.example.quickdraw.network.data.HireableMercenary
import com.example.quickdraw.network.data.ShopBullet
import com.example.quickdraw.network.data.ShopMedikit
import com.example.quickdraw.network.data.ShopUpgrade
import com.example.quickdraw.network.data.ShopWeapon
import com.example.quickdraw.ui.theme.Typography
import com.example.quickdraw.ui.theme.shopEntry
import java.nio.file.WatchEvent


@Composable
fun BasicShopEntry(canAfford: Boolean, action:()->Unit, populateShopEntry: @Composable ()->Unit){
    Row (
        modifier = Modifier.fillMaxWidth().padding(vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ){
        Column(modifier = Modifier.fillMaxWidth(0.65f).padding(horizontal = 20.dp)) {
            populateShopEntry()
        }
        Column(modifier = Modifier.padding(all = 20.dp).fillMaxWidth()) {
            Button(
                enabled = canAfford,
                onClick = action,
                modifier = Modifier.fillMaxWidth().height(72.dp)
            ) {
                Icon(
                    imageVector = ImageVector.vectorResource(R.drawable.money_bag_24px_1_),
                    "",
                    tint = Color.Black,
                )
            }
        }
    }
}

@Composable
fun BulletShopEntry(bullet: ShopBullet, onBuy: ()->Unit,canAfford:Boolean=true){
    BasicShopEntry(canAfford,onBuy) {
        Text(bullet.name, fontSize = Typography.titleLarge.fontSize)
        Text("Cost: ${bullet.cost} coins", color = if (canAfford) Color.Black  else Color.Red)
        Text("${bullet.quantity} pieces per purchase")
        Text("You can have a maximum of ${bullet.capacity} pieces")
    }
}

@Composable
fun MedikitEntryShop(medikit: ShopMedikit, onBuy: ()->Unit,canAfford:Boolean=true){
    BasicShopEntry(canAfford,onBuy) {
        Text(medikit.description, fontSize = Typography.titleLarge.fontSize)
        Text("Heals ${medikit.healthRecover} per use")
        Text("Cost: ${medikit.cost} coins", color = if (canAfford) Color.Black  else Color.Red)
        Text("${medikit.quantity} pieces per purchase")
        Text("You can use this a maximum of ${medikit.capacity} times")
    }
}

@Composable
fun WeaponEntryShop(weapon: ShopWeapon, onBuy: ()->Unit,canAfford:Boolean=true){
    BasicShopEntry(canAfford,onBuy) {
        Text(weapon.name, fontSize = Typography.titleLarge.fontSize)
        Text("Cost: ${weapon.cost} coins", color = if (canAfford) Color.Black  else Color.Red)
        Text("${weapon.damage} damage per hit")
    }
}

@Composable
fun UpgradeEntryShop(upgrade: ShopUpgrade, onBuy: ()->Unit,canAfford:Boolean=true){
    BasicShopEntry(canAfford,onBuy) {
        Text("Upgrade type:${upgrade.type}",fontSize = Typography.titleLarge.fontSize)
        Text("Cost: ${upgrade.cost} coins", color = if (canAfford) Color.Black  else Color.Red)
        Text("Level ${upgrade.level}")
        Text(upgrade.description)
    }
}

@Composable
fun MercenaryShopEntry(mercenary: HireableMercenary, onBuy: ()->Unit,canAfford:Boolean=true){
    BasicShopEntry(canAfford,onBuy) {
        Text(mercenary.name,fontSize = Typography.titleLarge.fontSize)
        Text("Cost: ${mercenary.cost} coins", color = if (canAfford) Color.Black  else Color.Red)
        Text("Power:${mercenary.power}")
    }
}


