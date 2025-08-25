package com.example.quickdraw.game.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.quickdraw.R

@Composable
fun MoneyDisplayer(money:Int=0){
    Row(modifier = Modifier
        //.background(brush = Brush.horizontalGradient(listOf(Color(0xFFF6E68A),Color.Yellow)))
        .padding(horizontal = 20.dp, vertical = 5.dp))
    {
        Text("$money", textAlign = TextAlign.Right, modifier = Modifier.padding(horizontal = 5.dp))
        Icon(
            imageVector = ImageVector.vectorResource(R.drawable.money_bag_24px_1_),
            "Your balance",
            tint = Color.Green,
        )
    }
}