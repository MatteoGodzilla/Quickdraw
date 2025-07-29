package com.example.quickdraw.game.yourplace

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.quickdraw.R
import com.example.quickdraw.game.components.BasicScreen
import com.example.quickdraw.game.components.ContentTab
import com.example.quickdraw.game.repo.GameRepository
import com.example.quickdraw.ui.theme.Typography


@Composable
fun YourPlaceScreen(controller: NavHostController, repository: GameRepository){
    // booleans for the collapsable categories
    BasicScreen("Your Place", controller, listOf(
        ContentTab("Stats") {
            Text("Main", modifier = Modifier.padding(it))
        },
        ContentTab("Memories") {
            Text("Memories", modifier = Modifier.padding(it))
        },
        ContentTab("Inventory") {
            Column(
                modifier = Modifier.padding(it)
            ) {
                //Weapons
                if(repository.weapons != null){
                    CollapsableList(true, "Weapons", repository.weapons!!) { weapon ->
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
                if(repository.bullets != null){
                    CollapsableList(true, "Bullets", repository.bullets!!) { bullet ->
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ){
                            Text(bullet.description)
                            Text("Capacity: ${bullet.capacity}")
                        }
                    }
                }
                //Medikits
                if(repository.medikits != null){
                    CollapsableList(true, "Medikits", repository.medikits!!) { medikit ->
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
                            Text("Capacity:${medikit.capacity}")
                        }
                    }
                }
                //Upgrades
                if(repository.upgrades != null){
                    CollapsableList(true, "Upgrades", repository.upgrades!!) { upgrade ->
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
            }
        },
        ContentTab("Settings") {

        }
    ))
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