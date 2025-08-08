package com.example.quickdraw.game.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import com.example.quickdraw.game.GameRepository
import com.example.quickdraw.game.components.BasicScreen
import com.example.quickdraw.game.components.ContentTab
import com.example.quickdraw.game.dataDisplayers.ActiveContract
import com.example.quickdraw.game.dataDisplayers.BulletShopEntry
import com.example.quickdraw.game.dataDisplayers.MedikitEntryShop
import com.example.quickdraw.game.dataDisplayers.UpgradeEntryShop
import com.example.quickdraw.game.dataDisplayers.WeaponEntryShop
import kotlinx.coroutines.delay

@Composable
fun ShopScreen (controller: NavHostController, repository: GameRepository) {

    //collectable states
    val playerState = repository.player.collectAsState()
    val bullets = repository.shopBullets.collectAsState()
    val weapons = repository.shopWeapons.collectAsState()
    val medikits = repository.shopMedikits.collectAsState()
    val upgrades = repository.shopUpgrades.collectAsState()

    BasicScreen("Shop", controller, listOf(
        ContentTab("Weapons"){
            if(weapons.value.isNotEmpty()){
                Column (modifier = Modifier.padding(it)){
                    for (w in weapons.value){
                        WeaponEntryShop(w,{},playerState.value!!.money>=w.cost)
                    }
                }
            }
        },
        ContentTab("Bullets"){
            if(bullets.value.isNotEmpty()){
                Column (modifier = Modifier.padding(it)){
                    for (w in bullets.value){
                        BulletShopEntry(w,{},playerState.value!!.money>=w.cost)
                    }
                }
            }
        },
        ContentTab("Medikits"){
            if(medikits.value.isNotEmpty()){
                Column (modifier = Modifier.padding(it)){
                    for (w in medikits.value){
                        MedikitEntryShop(w,{},playerState.value!!.money>=w.cost)
                    }
                }
            }
        },
        ContentTab("Upgrades"){
            if(upgrades.value.isNotEmpty()){
                Column (modifier = Modifier.padding(it)){
                    for (w in upgrades.value){
                        UpgradeEntryShop(w,{},playerState.value!!.money>=w.cost)
                    }
                }
            }
        }
    ))
}