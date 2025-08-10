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
import androidx.compose.material3.Divider
import androidx.compose.material3.HorizontalDivider
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
fun BasicShopEntry(price:Int,canAfford: Boolean, action:()->Unit, populateShopEntry: @Composable ()->Unit){
    Row (
        modifier = Modifier.fillMaxWidth().padding(vertical = 5.dp),
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
                Text(price.toString(),fontSize = Typography.titleLarge.fontSize)
                Icon(

                    imageVector = ImageVector.vectorResource(R.drawable.money_bag_24px_1_),
                    "",
                    tint = Color.Black,
                )
            }
        }
    }
    HorizontalDivider(
        modifier = Modifier.fillMaxWidth().padding(all=0.dp),
        thickness = 2.dp,
        color=Color.Black
    )
}

@Composable
fun BulletShopEntry(bullet: ShopBullet, onBuy: ()->Unit,canAfford:Boolean=true,possessedAmount:Int = 0){
    BasicShopEntry(bullet.cost,canAfford,onBuy) {
        Text(bullet.name, fontSize = Typography.titleLarge.fontSize)
        Text("${bullet.quantity} pieces per purchase")
        Text("Your possession: ${possessedAmount}/${bullet.capacity}",color = if(possessedAmount==bullet.capacity) Color.Red else Color.Black)
    }
}

@Composable
fun MedikitEntryShop(medikit: ShopMedikit, onBuy: ()->Unit,canAfford:Boolean=true,possessedAmount:Int = 0){
    BasicShopEntry(medikit.cost,canAfford,onBuy) {
        Text(medikit.description, fontSize = Typography.titleLarge.fontSize)
        Text("Heals ${medikit.healthRecover} per use")
        Text("${medikit.quantity} pieces per purchase")
        Text("Your possession: ${possessedAmount}/${medikit.capacity}",color = if(possessedAmount==medikit.capacity) Color.Red else Color.Black)
    }
}

@Composable
fun WeaponEntryShop(weapon: ShopWeapon, onBuy: ()->Unit,canAfford:Boolean=true){
    BasicShopEntry(weapon.cost,canAfford,onBuy) {
        Text(weapon.name, fontSize = Typography.titleLarge.fontSize)
        Text("${weapon.damage} damage per hit")
    }
}

@Composable
fun UpgradeEntryShop(upgrade: ShopUpgrade, onBuy: ()->Unit,canAfford:Boolean=true){
    BasicShopEntry(upgrade.cost,canAfford,onBuy) {
        Text(upgrade.description,fontSize = Typography.titleLarge.fontSize)
        Text("Level ${upgrade.level}")
    }
}

@Composable
fun MercenaryShopEntry(mercenary: HireableMercenary, onBuy: ()->Unit,canAfford:Boolean=true){
    BasicShopEntry(mercenary.cost,canAfford,onBuy) {
        Text(mercenary.name,fontSize = Typography.titleLarge.fontSize)
        Text("Power:${mercenary.power}")
    }
}





