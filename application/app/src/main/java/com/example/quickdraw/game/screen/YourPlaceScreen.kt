package com.example.quickdraw.game.screen

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.quickdraw.ImageLoader
import com.example.quickdraw.R
import com.example.quickdraw.TAG
import com.example.quickdraw.game.GameNavigation
import com.example.quickdraw.game.components.BasicScreen
import com.example.quickdraw.game.components.ContentTab
import com.example.quickdraw.game.repo.GameRepository
import com.example.quickdraw.ui.theme.Typography
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch


@Composable
fun YourPlaceScreen(controller: NavHostController, repository: GameRepository, imageLoader: ImageLoader, onLogout:()-> Unit={}){

    //stateflows
    val weapons = repository.inventory.weapons.collectAsState()
    val medikits = repository.inventory.medikits.collectAsState()
    val upgrades = repository.inventory.upgrades.collectAsState()
    val bullets = repository.inventory.bullets.collectAsState()
    val player = repository.player.status.collectAsState()

    val coroutineScope = rememberCoroutineScope()
    val playerImage = MutableStateFlow(imageLoader.imageNotFound.asImageBitmap())
    LaunchedEffect(true) {
        coroutineScope.launch {
            playerImage.value = imageLoader.getPlayerImage(repository.player.status.value?.id ?: 0)
        }
    }

    BasicScreen("Your Place", controller, listOf(
        ContentTab("Stats") {
            Image(playerImage.collectAsState().value, "Player icon")
        },
        ContentTab("Memories") {
            Text("Memories")
        },
        ContentTab("Inventory") {
                //Weapons
                if(weapons.value.isNotEmpty()){
                    CollapsableList(true, "Weapons", weapons.value) { weapon ->
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ){
                            Text(weapon.name)
                            Text("Damage: ${weapon.damage}")
                        }
                    }
                }
                //Bullets
                if(bullets.value.isNotEmpty()){
                    CollapsableList(true, "Bullets", bullets.value) { bullet ->
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ){
                            Text(bullet.description)
                            Text("${bullet.amount}/${bullet.capacity}")
                        }
                    }
                }
                //Medikits
                if(medikits.value.isNotEmpty()){
                    CollapsableList(true, "Medikits", medikits.value) { medikit ->
                        Text(
                            medikit.description,
                            fontSize = Typography.titleLarge.fontSize,
                            modifier = Modifier.padding(8.dp)
                        )
                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth().padding(start = 8.dp, end = 8.dp)
                        ){
                            Text("Health recover:${medikit.healthRecover}")
                            Text("${medikit.amount}/${medikit.capacity}")
                        }
                    }
                }
                //Upgrades
                if(upgrades.value.isNotEmpty()){
                    CollapsableList(true, "Upgrades", upgrades.value) { upgrade ->
                        Row (
                            modifier = Modifier.fillMaxWidth().padding(8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ){
                            Text(upgrade.description, fontSize = Typography.titleLarge.fontSize)
                            Text("Level ${upgrade.level}")
                        }
                    }
                }
        },
        ContentTab("Settings") {
            Box(contentAlignment = Alignment.BottomCenter,
                    modifier = Modifier.fillMaxSize().padding(5.dp)){
                Button(
                    onClick = onLogout,
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
    ), money = player.value!!.money)
}


@Preview
@Composable
fun CollapsableListPreview(){
    CollapsableList(true, "Header", listOf("A", "B", "C")){
        Text(it)
    }
}

@Composable
fun <T> CollapsableList(defaultOpen: Boolean, title: String, items: List<T>, itemCompose: @Composable (T)->Unit){
    var open by remember { mutableStateOf(defaultOpen) }
    val icon = if(open) R.drawable.arrow_drop_up_24px else R.drawable.arrow_drop_down_24px
    Column (
        modifier = Modifier.fillMaxWidth()
    ) {
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = MaterialTheme.colorScheme.surfaceContainer,
            onClick = {open = !open}
        ) {
            Row (
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth().padding(8.dp)
            ){
                Text(title, fontSize = Typography.titleLarge.fontSize)
                Icon(
                    imageVector = ImageVector.vectorResource(icon),
                    if (open) "Close $title" else "Open $title"
                )
            }
        }

        if(open){
            for (item in items){
                itemCompose(item)
            }
        }
    }
}