package com.example.quickdraw.duel.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.quickdraw.R
import com.example.quickdraw.game.components.ProgressBar
import com.example.quickdraw.game.components.infiniteRotation
import com.example.quickdraw.ui.theme.Typography

@Composable
fun DuelBar(opponent:String, ratio: Float, color: Color=Color.Blue) {
    val rowHeight = 64.dp
    Surface(color = MaterialTheme.colorScheme.surfaceContainer,
        modifier= Modifier.fillMaxWidth().height(rowHeight)
    ){
        Column (modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center){
            Box(modifier = Modifier.fillMaxWidth()){
                Icon(
                    imageVector = ImageVector.vectorResource(R.drawable.stars_2_24px),
                    "",
                    tint = color,
                    modifier = Modifier.size(36.dp)
                )
                Text(
                    opponent,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center,
                    fontSize = Typography.titleLarge.fontSize
                )
            }
            LinearProgressIndicator({ratio}, modifier = Modifier.fillMaxWidth())
        }
    }
}

