package com.example.quickdraw.game.components

import android.media.Image
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.graphics.createBitmap
import com.example.quickdraw.R
import com.example.quickdraw.game.components.LockedContainer
import com.example.quickdraw.game.components.ShoppingContainer
import com.example.quickdraw.game.components.RowDivider
import com.example.quickdraw.network.data.HireableMercenary
import com.example.quickdraw.network.data.ShopBullet
import com.example.quickdraw.network.data.ShopMedikit
import com.example.quickdraw.network.data.ShopUpgrade
import com.example.quickdraw.network.data.ShopWeapon
import com.example.quickdraw.ui.theme.Typography
import com.example.quickdraw.ui.theme.fulledEntry


@Composable
fun BasicShopEntry(price:String, purchasable: Boolean, action:()->Unit, icon: ImageBitmap, populateShopEntry: @Composable ()->Unit){
    val content = @Composable {
        Image(icon, "", modifier = Modifier.size(48.dp))
        Column {
            populateShopEntry()
        }
    }

    ShoppingContainer(content){
        Button(
            enabled = purchasable,
            onClick = action,
            //modifier = Modifier.fillMaxWidth().height(64.dp)
        ) {
            Text(
                price,
                //fontSize = Typography.titleLarge.fontSize,
                //modifier = Modifier.weight(1.0f),
                textAlign = TextAlign.Center
            )
            Icon(
                imageVector = ImageVector.vectorResource(R.drawable.money_bag_24px_1_),
                "",
                tint = Color.Black,
                modifier = Modifier.size(24.dp)
            )
        }
    }
    RowDivider()
}

@Composable
fun BulletShopEntry(bullet: ShopBullet, onBuy: ()->Unit, icon:ImageBitmap, purchasable:Boolean=true, possessedAmount:Int = 0){
    val isFull = possessedAmount==bullet.capacity
    BasicShopEntry(if(isFull) "Full" else bullet.cost.toString(),purchasable,onBuy, icon) {
        Text(bullet.name, fontSize = Typography.titleLarge.fontSize)
        if(bullet.quantity > 1){
            Text("Bundle of ${bullet.quantity} bullets")
        }
        Text("Owned: ${possessedAmount}/${bullet.capacity}",color = if(isFull) fulledEntry else Color.Black)
    }
}

@Composable
fun MedikitEntryShop(medikit: ShopMedikit, onBuy: ()->Unit, icon:ImageBitmap, purchasable:Boolean=true, possessedAmount:Int = 0){
    val isFull = possessedAmount==medikit.capacity
    BasicShopEntry(if(isFull) "Full" else medikit.cost.toString(),purchasable,onBuy, icon) {
        Text(medikit.description, fontSize = Typography.titleLarge.fontSize)
        if(medikit.quantity > 1){
            Text("Bundle of ${medikit.quantity} medikits")
        }
        Text("Heals ${medikit.healthRecover} per use")
        Text("Owned: ${possessedAmount}/${medikit.capacity}",color = if(isFull) fulledEntry else Color.Black)
    }
}

@Composable
fun WeaponEntryShop(weapon: ShopWeapon, onBuy: ()->Unit, icon: ImageBitmap, purchasable:Boolean=true){
    BasicShopEntry(weapon.cost.toString(),purchasable,onBuy, icon) {
        Text(weapon.name, fontSize = Typography.titleLarge.fontSize)
        Text("${weapon.damage} damage per hit")
    }
}

@Composable
fun LockedWeapon(weapon:ShopWeapon, icon:ImageBitmap){
    LockedContainer(){
        Image(icon, "Icon", modifier = Modifier.size(48.dp))
        Column {
            Text(weapon.name, fontSize = Typography.titleLarge.fontSize)
            Text("Unlock at level ${weapon.level}")
        }
    }
}

@Composable
fun UpgradeEntryShop(upgrade: ShopUpgrade, onBuy: ()->Unit, icon:ImageBitmap, purchasable:Boolean=true){
    BasicShopEntry(upgrade.cost.toString(),purchasable,onBuy, icon) {
        Text(upgrade.description,fontSize = Typography.titleLarge.fontSize)
        Text("Level ${upgrade.level}")
    }
}

@Composable
fun MercenaryShopEntry(mercenary: HireableMercenary, onBuy: ()->Unit,icon:ImageBitmap, canAfford:Boolean=true){
    BasicShopEntry(mercenary.cost.toString(),canAfford,onBuy, icon) {
        Text(mercenary.name,fontSize = Typography.titleLarge.fontSize)
        Text("Power:${mercenary.power}")
    }
}

@Preview
@Composable
fun previewBasic(){
    BasicShopEntry("69000", true, action = {}, createBitmap(10,10).asImageBitmap()) {
        Text("Winchester", fontSize = Typography.titleLarge.fontSize)
        Text("200 damage per hit")
        Text("200 damage per hit")
        Text("200 damage per hit")
        Text("200 damage per hit")
        Text("200 damage per hit")
        Text("200 damage per hit   g  sg  gl js lh sghl gshlgshlgshlgshlsglhsglh")
    }
}




