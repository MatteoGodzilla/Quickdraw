package com.example.quickdraw.game.screen

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.paddingFrom
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import coil3.compose.AsyncImage
import com.example.quickdraw.R
import com.example.quickdraw.game.components.BasicScreen
import com.example.quickdraw.game.components.ContentTab
import com.example.quickdraw.game.components.DropDownMenuForSettings
import com.example.quickdraw.game.components.RowDivider
import com.example.quickdraw.game.vm.YourPlaceVM
import com.example.quickdraw.music.AudioManager
import com.example.quickdraw.ui.theme.Typography
import com.example.quickdraw.ui.theme.secondaryButtonColors
import kotlin.math.floor


@Composable
fun YourPlaceScreen(viewModel: YourPlaceVM, controller: NavHostController){
    val weapons = viewModel.weapons.collectAsState()
    val medikits = viewModel.medikits.collectAsState()
    val upgrades = viewModel.upgrades.collectAsState()
    val bullets = viewModel.bullets.collectAsState()
    val player = viewModel.player.player.collectAsState()
    val stats = viewModel.stats.collectAsState()
    val favouriteWeapon = viewModel.favouriteWeapon.collectAsState()

    val playerImage = viewModel.playerImage.collectAsState()
    val choosePhoto = rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) {
        if(it != null){
            viewModel.updateImage(it)
        }
    }

    BasicScreen("Your Place", controller, listOf(
        ContentTab("Inventory") {
            //Weapons
            if(weapons.value.isNotEmpty()){
                SmallHeader("Weapons")
                for(weapon in weapons.value){
                    Row (verticalAlignment = Alignment.CenterVertically){
                        AsyncImage(
                            viewModel.loadWeaponImage(weapon.id).collectAsState().value,
                            weapon.name,
                            modifier = Modifier.size(48.dp)
                        )
                        Column {
                            Text(weapon.name, fontSize = Typography.titleLarge.fontSize, modifier = Modifier.padding(8.dp))
                            val bulletUsed = bullets.value.first { b -> b.type == weapon.bulletType }
                            StatsDisplayer("Damage: ${weapon.damage}", "Bullet used: ${bulletUsed.description}")
                            Box(modifier = Modifier.fillMaxWidth().padding(end = 20.dp)){
                                Text("Bullets shot: ${weapon.bulletsShot}", modifier = Modifier.padding(8.dp))
                                FavouriteButton(Modifier.size(36.dp).align(Alignment.BottomEnd),favouriteWeapon.value==weapon.id){
                                    viewModel.setOrUnsetFavourite(weapon.id)
                                }
                            }
                        }
                    }
                    RowDivider()
                }
            }
            //Bullets
            if(bullets.value.isNotEmpty()){
                SmallHeader("Bullets")
                for(bullet in bullets.value){
                    Row(verticalAlignment = Alignment.CenterVertically){
                        AsyncImage(viewModel.loadBulletImage(bullet.type).collectAsState().value, bullet.description, modifier = Modifier.size(48.dp))
                        StatsDisplayer(bullet.description, "Owned: ${bullet.amount}/${bullet.capacity}")
                    }
                }
            }
            //Medikits
            if(medikits.value.isNotEmpty()){
                SmallHeader("Medikits")
                val ratio = player.value.health.toFloat() / stats.value.maxHealth
                StatsDisplayer("Your health:", "${player.value.health}/${stats.value.maxHealth}")
                LinearProgressIndicator({ ratio }, modifier = Modifier.fillMaxWidth())
                for(medikit in medikits.value){
                    Row(verticalAlignment = Alignment.CenterVertically){
                        AsyncImage(viewModel.loadMedikitImage(medikit.id).collectAsState().value, medikit.description, modifier = Modifier.size(48.dp))
                        Column {
                            Text(
                                medikit.description,
                                fontSize = Typography.titleLarge.fontSize,
                                modifier = Modifier.padding(8.dp)
                            )
                            Row (modifier = Modifier.fillMaxWidth().padding(8.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                                Column {
                                    Text("Health recover:${medikit.healthRecover}")
                                    Text("Owned: ${medikit.amount}/${medikit.capacity}")
                                }
                                Button(
                                    onClick = {viewModel.useMedikit(medikit.id)},
                                    enabled = ratio < 1
                                ) {
                                    Text("Heal")
                                }
                            }
                        }
                    }
                }
            }
            //Upgrades
            SmallHeader("Upgrades")
            if(upgrades.value.isNotEmpty()){
                for (pair in upgrades.value.groupBy { it.type }){
                    val highest = pair.value.maxBy { x->x.level }
                    Row(verticalAlignment = Alignment.CenterVertically){
                        AsyncImage(viewModel.loadUpgradeImage(pair.value.maxBy{x->x.level}.idUpgrade).collectAsState().value, highest.description, modifier = Modifier.size(48.dp))
                        Row (
                            modifier = Modifier.fillMaxWidth().padding(8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ){
                            Text(highest.description, fontSize = Typography.titleLarge.fontSize)
                            Text("Level ${highest.level}")
                        }
                    }
                }
            }
        },
        ContentTab("Statistics") {
            Row(
                modifier = Modifier.fillMaxWidth().padding(top=10.dp),
                horizontalArrangement = Arrangement.Center
            ){
                Box (
                    modifier = Modifier.size(200.dp)
                        .clickable { choosePhoto.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)) }
                ){
                    AsyncImage(
                        playerImage.value,
                        "Player icon",
                        contentScale = ContentScale.FillBounds,
                        modifier = Modifier.fillMaxHeight().clip(CircleShape).align(Alignment.Center),

                    )
                    CircularProgressIndicator(viewModel::getProgressToNextLevel, modifier = Modifier.fillMaxSize() )
                }
            }
            //Player
            Text(
                player.value.username,
                fontSize = Typography.titleLarge.fontSize,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )
            Text(
                "Level ${player.value.level} (${player.value.exp}/${viewModel.player.getExpForNextLevel()})",
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )
            Text(
                "Money: ${player.value.money}",
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )
            Text(
                "Bounty: ${player.value.bounty}",
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )

            SmallHeader("Upgrades")
            StatsDisplayer("Max health", stats.value.maxHealth.toString())
            StatsDisplayer("Max simultaneous contracts", stats.value.maxContracts.toString())
            StatsDisplayer("Exp boost", "${stats.value.expBoost-100}%")
            StatsDisplayer("Money boost", "${stats.value.moneyBoost-100}%")
            StatsDisplayer("Bounty boost", "${stats.value.bountyBoost-100}%")

            val roundStats = viewModel.otherStatistics.rounds.collectAsState().value

            SmallHeader("Rounds")
            StatsDisplayer("Played", roundStats.played.toString())
            StatsDisplayer("Won", roundStats.won.toString())
            StatsDisplayer("Lost", roundStats.lost.toString())
            StatsDisplayer("Win Ratio", "${if (roundStats.played > 0) floor(roundStats.won.toFloat() * 100 / roundStats.played) else 0} %")
            StatsDisplayer("Bullets shot", roundStats.bulletsShot.toString())
            StatsDisplayer("Damage dealt", roundStats.damageDealt.toString())
            StatsDisplayer("Damage received", roundStats.damageReceived.toString())

            val contractStats = viewModel.otherStatistics.contracts.collectAsState().value

            SmallHeader("Contracts")
            StatsDisplayer("Started", contractStats.started.toString())
            StatsDisplayer("Completed", contractStats.completed.toString())
            StatsDisplayer("Failed", contractStats.successful.toString())
            StatsDisplayer("Completion ratio", "${if (contractStats.completed > 0) floor(contractStats.successful.toFloat() * 100 / contractStats.completed) else 0} %")
        },
        ContentTab("Settings") {
            SmallHeader("Audio")
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth().padding(8.dp)
            ) {
                Text("Mute audio")
                Checkbox(viewModel.muteAudio.collectAsState().value, onCheckedChange = viewModel::onMuteToggle)
            }
            StatsDisplayer("Music", "${floor(viewModel.musicVolumeSlider.floatValue * 100).toInt()}%")
            Slider(
                value = viewModel.musicVolumeSlider.floatValue,
                onValueChange = { viewModel.setMusicVolume(it) },
                modifier = Modifier.padding(start = 16.dp, end = 16.dp)
            )
            StatsDisplayer("Sound effects", "${floor(viewModel.sfxVolumeSlider.floatValue * 100).toInt()}%")
            Slider(
                value = viewModel.sfxVolumeSlider.floatValue,
                onValueChange = { viewModel.setSFXVolume(it) },
                modifier = Modifier.padding(start = 16.dp, end = 16.dp)
            )

            Row(modifier = Modifier.fillMaxWidth()){
                val options = AudioManager.getSettingsSongs()
                Row(modifier = Modifier.fillMaxWidth().padding(8.dp), horizontalArrangement = Arrangement.SpaceBetween){
                    Text("Current song")
                    val selected = options[viewModel.favouriteSong.collectAsState().value].name
                    DropDownMenuForSettings(selected, modifier = Modifier.align(Alignment.CenterVertically).fillMaxWidth( )){
                        for(i in 0 .. options.size-1){
                            val song = options[i]
                            DropdownMenuItem(
                                modifier = Modifier.fillMaxWidth(),
                                text = {Text(song.name)},
                                onClick = {viewModel.changeAudioManagerSong(i)}
                            )
                        }
                    }
                }
            }
            SmallHeader("Device")
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth().padding(8.dp)
            ) {
                Text("Enable vibration")
                Checkbox(viewModel.enableVibration.collectAsState().value, onCheckedChange = viewModel::onEnableVibration)
            }
            //SmallHeader("Notifications")
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth().padding(8.dp)
            ) {
                Text("Enable notifications")
                Checkbox(viewModel.enableNotifications.collectAsState().value, onCheckedChange = viewModel::onEnableNotifications)
            }
            //SmallHeader("Account")
            Button(
                onClick = viewModel::logout,
                colors = secondaryButtonColors,
                modifier = Modifier .fillMaxWidth().padding(8.dp)
            ) {
                Icon(Icons.AutoMirrored.Default.ArrowBack,"go back")
                Text("Logout", fontSize = Typography.titleLarge.fontSize)
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

@Composable
fun FavouriteButton(modifier: Modifier, selected: Boolean=false, onClick:()->Unit){
    val color = if(selected) Color.Red else Color.Black
    Icon(imageVector = ImageVector.vectorResource(R.drawable.favorite_24px), "Favourite",tint = color,
        modifier = modifier.clickable(onClick=onClick))
}