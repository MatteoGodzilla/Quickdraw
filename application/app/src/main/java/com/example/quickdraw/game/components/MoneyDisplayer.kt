package com.example.quickdraw.game.components

import android.graphics.drawable.shapes.OvalShape
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination.Companion.hasRoute
import com.example.quickdraw.R
import com.example.quickdraw.game.GameNavigation.Contracts
import com.example.quickdraw.ui.theme.bottomBarButtonColors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MoneyDisplayer(money:Int=0){
    Row(modifier = Modifier.background(color = Color(0XFFF2721A)).padding(horizontal = 20.dp, vertical = 5.dp)){
        Text("$money", textAlign = TextAlign.Right, modifier = Modifier.padding(end=20.dp))
        Icon(
            imageVector = ImageVector.vectorResource(R.drawable.money_bag_24px_1_),
            "Your balance",
            tint = Color.Yellow,
        )
    }
}