package com.example.quickdraw.game.dataDisplayers

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import com.example.quickdraw.network.data.ShopBullet
import com.example.quickdraw.ui.theme.Typography
import com.example.quickdraw.ui.theme.shopEntry
import java.nio.file.WatchEvent

@Composable
fun BulletShopEntry(bullet: ShopBullet, onBuy: ()->Unit,canAfford:Boolean=true){
    Row (
        modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ){
        Column(modifier = Modifier.fillMaxWidth(0.7f).padding(horizontal = 15.dp)) {
            Text(bullet.name, fontSize = Typography.titleLarge.fontSize)
            Text("Cost: ${bullet.cost} coins", color = if (canAfford) Color.Black  else Color.Red)
            Text("${bullet.quantity} pieces per purchase")
            Text("You can have a maximum of ${bullet.capacity} pieces")
        }
        Column(modifier = Modifier.padding(horizontal = 20.dp)) {
            Button(
                enabled = canAfford,
                onClick = onBuy,
                modifier = Modifier.width(72.dp).height(72.dp)
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