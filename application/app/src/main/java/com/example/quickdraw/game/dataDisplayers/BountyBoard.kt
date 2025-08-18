package com.example.quickdraw.game.dataDisplayers

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.quickdraw.network.data.LeaderboardEntry
import com.example.quickdraw.ui.theme.MedalColors
import com.example.quickdraw.ui.theme.Typography

@Composable
fun PlacementText(placement: Int){
    var medalModifier = Modifier.size(38.dp).padding(5.dp)
    when(placement){
        1 -> medalModifier = medalModifier.background(color= MedalColors.BackGroundGold, shape = CircleShape)
        2 -> medalModifier = medalModifier.background(color= MedalColors.BackGroundSilver, shape = CircleShape)
        3 -> medalModifier = medalModifier.background(color= MedalColors.BackGroundBronze, shape = CircleShape)
        else -> {}
    }
    Text(
        placement.toString(),
        fontSize=Typography.titleLarge.fontSize,
        color = Color.Black,
        textAlign = TextAlign.Center,
        modifier = medalModifier
    )
}

@Composable
fun BountyEntry(entry: LeaderboardEntry, placement: Int, icon: ImageBitmap){
    val textModifier = Modifier.padding(5.dp)
    Row(modifier = Modifier.fillMaxWidth()){
        PlacementText(placement)
        Image(
            icon,
            "Placement $placement",
            contentScale = ContentScale.FillBounds,
            modifier = Modifier.size(32.dp).clip(CircleShape)
        )
        Text(entry.username, fontSize=Typography.titleLarge.fontSize,modifier = textModifier)
        Text(entry.bounty.toString(), fontSize=Typography.bodyLarge.fontSize, textAlign = TextAlign.Right,modifier = textModifier.fillMaxWidth())
    }
}