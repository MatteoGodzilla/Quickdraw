package com.example.quickdraw.duel.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
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
import com.example.quickdraw.game.components.infiniteRotation
import com.example.quickdraw.ui.theme.Typography

@Composable
fun DuelBar(opponent:String,color: Color=Color.Blue) {
    val rowHeight = 96.dp
    Surface(color = MaterialTheme.colorScheme.surfaceContainer,
        modifier= Modifier.fillMaxWidth().height(rowHeight)
    ){
        Box(modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center,
        ){
            Text(
                opponent,
                modifier = Modifier,
                textAlign = TextAlign.Center,
                fontSize = Typography.titleLarge.fontSize
            )
            Row(modifier=Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically){
                Icon(
                    imageVector = ImageVector.vectorResource(R.drawable.stars_2_24px),
                    "",
                    tint = color,
                    modifier = Modifier.size(36.dp)
                )
            }
        }
    }
}

