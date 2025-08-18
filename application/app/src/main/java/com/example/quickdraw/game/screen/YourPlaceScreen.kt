package com.example.quickdraw.game.screen

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.modifier.modifierLocalOf
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.quickdraw.R
import com.example.quickdraw.game.components.BasicScreen
import com.example.quickdraw.game.components.ContentTab
import com.example.quickdraw.game.components.ProgressBar
import com.example.quickdraw.game.vm.YourPlaceVM
import com.example.quickdraw.ui.theme.ProgressBarColors
import com.example.quickdraw.ui.theme.Typography


@Composable
fun YourPlaceScreen(viewModel: YourPlaceVM, controller: NavHostController){
    val weapons = viewModel.weapons.collectAsState()
    val medikits = viewModel.medikits.collectAsState()
    val upgrades = viewModel.upgrades.collectAsState()
    val bullets = viewModel.bullets.collectAsState()
    val player = viewModel.player.collectAsState()
    val stats = viewModel.stats.collectAsState()

    val playerImage = viewModel.playerImage.collectAsState()
    val choosePhoto = rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) {
        if(it != null){
            viewModel.updateImage(it)
        }
    }

    BasicScreen("Your Place", controller, listOf(
        ContentTab("Statistics") {
            Row(
                modifier = Modifier.fillMaxWidth().padding(top=10.dp),
                horizontalArrangement = Arrangement.Center
            ){
                Box (
                    modifier = Modifier.size(200.dp)
                        .clickable { choosePhoto.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)) }
                ){
                    Image(
                        playerImage.value,
                        "Player icon",
                        contentScale = ContentScale.FillBounds,
                        modifier = Modifier.fillMaxHeight().clip(CircleShape),
                    )
                    CircularProgressIndicator(viewModel::getProgressToNextLevel, modifier = Modifier.fillMaxSize() )
                }
            }
            //Player
            Text(
                viewModel.player.collectAsState().value.username,
                fontSize = Typography.titleLarge.fontSize,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )
            Text(
                "Level ${viewModel.player.collectAsState().value.level}",
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )
            Text(
                "Money: ${viewModel.player.collectAsState().value.money}",
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )
            Text(
                "Bounty: ${viewModel.player.collectAsState().value.bounty}",
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )

            SmallHeader("Upgrades")
            StatsDisplayer("Max health", stats.value.maxHealth.toString())
            StatsDisplayer("Max simultaneous contracts", stats.value.maxContracts.toString())
            StatsDisplayer("Exp boost", "${stats.value.expBoost-100}%")
            StatsDisplayer("Money boost", "${stats.value.moneyBoost-100}%")
            StatsDisplayer("Bounty boost", "${stats.value.bountyBoost-100}%")

            SmallHeader("Rounds")
            StatsDisplayer("Played", "#")
            StatsDisplayer("Won", "#")
            StatsDisplayer("Lost", "#")
            StatsDisplayer("Win Ratio", "## %")
            StatsDisplayer("Longest streak", "#")

            SmallHeader("Contracts")
            StatsDisplayer("Started", "##")
            StatsDisplayer("Completed", "##")
            StatsDisplayer("Failed", "##")
            StatsDisplayer("Completion ratio", "## %")

        },
        ContentTab("Memories") {
            Text("Memories")
        },
        ContentTab("Inventory") {
                //Weapons
                if(weapons.value.isNotEmpty()){
                    SmallHeader("Weapons")
                    for(weapon in weapons.value){
                        StatsDisplayer(weapon.name, "Damage: ${weapon.damage}")
                    }
                }
                //Bullets
                if(bullets.value.isNotEmpty()){
                    SmallHeader("Bullets")
                    for(bullet in bullets.value){
                        StatsDisplayer(bullet.description, "${bullet.amount}/${bullet.capacity}")
                    }
                }
                //Medikits
                if(medikits.value.isNotEmpty()){
                    SmallHeader("Medikits")
                    for(medikit in medikits.value){
                        Text(
                            medikit.description,
                            fontSize = Typography.titleLarge.fontSize,
                            modifier = Modifier.padding(8.dp)
                        )
                        StatsDisplayer("Health recover:${medikit.healthRecover}", "${medikit.amount}/${medikit.capacity}")
                    }
                }
                //Upgrades
                SmallHeader("Upgrades")
                if(upgrades.value.isNotEmpty()){
                    for (pair in upgrades.value.groupBy { it.type }){
                        val highest = pair.value.maxBy { x->x.level }
                        Row (
                            modifier = Modifier.fillMaxWidth().padding(8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ){
                            Text(highest.description, fontSize = Typography.titleLarge.fontSize)
                            Text("Level ${highest.level}")
                        }
                    }
                }
        },
        ContentTab("Settings") {
            Box(contentAlignment = Alignment.BottomCenter,
                    modifier = Modifier.fillMaxSize().padding(5.dp)){
                Button(
                    onClick = viewModel::logout,
                    colors = ButtonColors(
                        containerColor = MaterialTheme.colorScheme.secondary,
                        contentColor = MaterialTheme.colorScheme.onSurface,
                        disabledContainerColor = MaterialTheme.colorScheme.primary,
                        disabledContentColor = MaterialTheme.colorScheme.onSurface
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    Icon(Icons.AutoMirrored.Default.ArrowBack,"go back")
                    Text("Logout", fontSize = Typography.titleLarge.fontSize)
                }
            }
        }
    ), money = player.value.money)
}

@Composable
fun SmallHeader(title:String,locked:Boolean=false){
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = if(!locked) MaterialTheme.colorScheme.surfaceContainer else MaterialTheme.colorScheme.onErrorContainer,
    ) {
        Row (
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth().padding(8.dp)
        ){
            Text(title, fontSize = Typography.titleLarge.fontSize)
        }
    }
}

@Composable
fun StatsDisplayer(left: String, right: String){
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier.fillMaxWidth().padding(8.dp)
    ){
        Text(left)
        Text(right)
    }
}