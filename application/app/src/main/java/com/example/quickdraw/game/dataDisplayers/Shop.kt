package com.example.quickdraw.game.dataDisplayers

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import com.example.quickdraw.game.components.RowDevider
import com.example.quickdraw.network.data.HireableMercenary
import com.example.quickdraw.network.data.ShopBullet
import com.example.quickdraw.network.data.ShopMedikit
import com.example.quickdraw.network.data.ShopUpgrade
import com.example.quickdraw.network.data.ShopWeapon
import com.example.quickdraw.ui.theme.Typography
import com.example.quickdraw.ui.theme.fulledEntry


@Composable
fun BasicShopEntry(price:String, purchasable: Boolean, action:()->Unit, populateShopEntry: @Composable ()->Unit){
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
                enabled = purchasable,
                onClick = action,
                modifier = Modifier.fillMaxWidth().height(72.dp)
            ) {
                Text(price,fontSize = Typography.titleLarge.fontSize)
                Icon(
                    imageVector = ImageVector.vectorResource(R.drawable.money_bag_24px_1_),
                    "",
                    tint = Color.Black,
                )
            }
        }
    }
    RowDevider()
}

@Composable
fun BulletShopEntry(bullet: ShopBullet, onBuy: ()->Unit, purchasable:Boolean=true, possessedAmount:Int = 0){
    val isFull = possessedAmount==bullet.capacity
    BasicShopEntry(if(isFull) "Full" else bullet.cost.toString(),purchasable,onBuy) {
        Text("${bullet.name} (${bullet.quantity}x)", fontSize = Typography.titleLarge.fontSize)
        Text("Your possession: ${possessedAmount}/${bullet.capacity}",color = if(isFull) fulledEntry else Color.Black)
    }
}

@Composable
fun MedikitEntryShop(medikit: ShopMedikit, onBuy: ()->Unit, purchasable:Boolean=true, possessedAmount:Int = 0){
    val isFull = possessedAmount==medikit.capacity
    BasicShopEntry(if(isFull) "Full" else medikit.cost.toString(),purchasable,onBuy) {
        Text("${medikit.description} (${medikit.quantity}x)", fontSize = Typography.titleLarge.fontSize)
        Text("Heals ${medikit.healthRecover} per use")
        Text("Your possession: ${possessedAmount}/${medikit.capacity}",color = if(isFull) fulledEntry else Color.Black)
    }
}

@Composable
fun WeaponEntryShop(weapon: ShopWeapon, onBuy: ()->Unit, purchasable:Boolean=true){
    BasicShopEntry(weapon.cost.toString(),purchasable,onBuy) {
        Text(weapon.name, fontSize = Typography.titleLarge.fontSize)
        Text("${weapon.damage} damage per hit")
    }
}

@Composable
fun UpgradeEntryShop(upgrade: ShopUpgrade, onBuy: ()->Unit, purchasable:Boolean=true){
    BasicShopEntry(upgrade.cost.toString(),purchasable,onBuy) {
        Text(upgrade.description,fontSize = Typography.titleLarge.fontSize)
        Text("Level ${upgrade.level}")
    }
}

@Composable
fun MercenaryShopEntry(mercenary: HireableMercenary, onBuy: ()->Unit,canAfford:Boolean=true){
    BasicShopEntry(mercenary.cost.toString(),canAfford,onBuy) {
        Text(mercenary.name,fontSize = Typography.titleLarge.fontSize)
        Text("Power:${mercenary.power}")
    }
}





