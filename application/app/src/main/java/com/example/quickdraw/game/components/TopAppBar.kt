package com.example.quickdraw.game.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.quickdraw.R
import com.example.quickdraw.game.repo.GameRepository
import com.example.quickdraw.ui.theme.ProgressBarColors
import com.example.quickdraw.ui.theme.Typography

@Composable
fun TopBar(repository: GameRepository) {
    val player = repository.player.status.collectAsState()
    Surface(color = MaterialTheme.colorScheme.surfaceContainer){
        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            verticalArrangement = Arrangement.Bottom,
            userScrollEnabled = false
        ) {
            val rowHeight = Typography.bodyLarge.lineHeight.value.dp
            item ( span = { GridItemSpan(2) } ) {
                val ratio = if(player.value != null) player.value!!.health.toFloat() / player.value!!.maxHealth else 0.0f
                ProgressBar(ratio, ProgressBarColors.health, rowHeight, ImageVector.vectorResource(R.drawable.favorite_24px), "Health")
            }
            item { CenteredText("${player.value?.health}/${player.value?.maxHealth} HP", rowHeight ) }
            item ( span = { GridItemSpan(2) } ) {
                //TODO: use levels for this
                var progress = 0f
                if(repository.player.levels.isNotEmpty()){
                    val playerLevel = repository.player.level.collectAsState().value
                    val levelIndex = playerLevel - 1
                    if(levelIndex < repository.player.levels.size){
                        //playerLevel -1 is a valid index
                        progress = (player.value!!.exp - repository.player.levels[levelIndex]).toFloat() /
                                (repository.player.levels[levelIndex + 1] - repository.player.levels[levelIndex])
                    }
                }
                ProgressBar(progress, ProgressBarColors.experience, rowHeight, ImageVector.vectorResource(R.drawable.stars_2_24px), "Experience")
            }
            item { CenteredText("Lv. ${-repository.player.level.collectAsState().value}", rowHeight) }
            item { TopBarRow(image = ImageVector.vectorResource(R.drawable.money_bag_24px_1_), rowHeight, text = "${player.value?.money}") }
            item { TopBarRow(image = ImageVector.vectorResource(R.drawable.local_police_24px), rowHeight, text = "${player.value?.bounty}") }
        }
    }
}

@Composable
fun TopBarRow(image: ImageVector, height: Dp,  text: String) {
    Row (
        verticalAlignment = Alignment.CenterVertically
    ){
        Image(image, "")
        CenteredText(text, height)
    }
}

@Composable
fun ProgressBar(progress: Float, color: Color, height: Dp, image: ImageVector, imageDescription: String) {
    Row(
        modifier = Modifier.heightIn(height).fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(image, imageDescription)
        LinearProgressIndicator(
            progress = {progress},
            color = color,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
fun CenteredText(text: String, height: Dp) {
    Text(
        text,
        textAlign = TextAlign.Center,
        modifier = Modifier.fillMaxWidth().height(height)
            .wrapContentHeight(Alignment.CenterVertically)
    )
}