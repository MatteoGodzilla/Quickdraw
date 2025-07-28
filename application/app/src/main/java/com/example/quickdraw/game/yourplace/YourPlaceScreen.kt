package com.example.quickdraw.game.yourplace

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavHostController
import com.example.quickdraw.game.components.BasicScreen
import com.example.quickdraw.game.components.ContentTab
import com.example.quickdraw.game.repo.GameRepository
import com.example.quickdraw.ui.theme.Typography


@Composable
fun YourPlaceScreen(controller: NavHostController, repository: GameRepository){
    BasicScreen("Your Place", controller, listOf(
        ContentTab("Main") {
            Surface(
                modifier = Modifier.fillMaxSize().padding(it),
                color = Color(255, 0, 0, 255)
            ) {
                Text("Main")
            }
        },
        ContentTab("Memories") {
            Text("Mamories", modifier = Modifier.padding(it))
        },
        ContentTab("Inventory") {
            Column(
                modifier = Modifier.padding(it)
            ) {
                //Weapons
                Text("Weapons", fontSize = Typography.titleLarge.fontSize )
                if(repository.weapons != null){
                    for (weapon in repository.weapons!!){
                        Text("${weapon.name} (Damage: ${weapon.damage})")
                    }
                }
                //Bullets
                Text("Bullets", fontSize = Typography.titleLarge.fontSize)
                if(repository.bullets != null){
                    for (bullet in repository.bullets!!){
                        Text("${bullet.description} ${bullet.capacity}")
                    }
                }
                //Medikits
                Text("Medikits", fontSize = Typography.titleLarge.fontSize)
                if(repository.medikits != null){
                    for (medikit in repository.medikits!!){
                        Text("${medikit.description} (Health recover: ${medikit.healthRecover}) ${medikit.capacity}")
                    }
                }
                //Upgrades
                //Medikits
                Text("Upgrades", fontSize = Typography.titleLarge.fontSize)
                if(repository.upgrades != null){
                    for (upgrade in repository.upgrades!!){
                        Text("${upgrade.description} LV.${upgrade.level}")
                    }
                }
            }
        },
    ))
}