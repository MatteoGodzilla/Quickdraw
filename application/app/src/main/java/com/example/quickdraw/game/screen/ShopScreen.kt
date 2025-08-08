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

    BasicScreen("Shop", controller, listOf(
        ContentTab("Weapons"){
            if(repository.shopWeapons.isNotEmpty()){
                Column (modifier = Modifier.padding(it)){
                    for (w in repository.shopWeapons){
                        WeaponEntryShop(w,{},playerState.value!!.money>=w.cost)
                    }
                }
            }
        },
        ContentTab("Bullets"){
            if(repository.shopBullets.isNotEmpty()){
                Column (modifier = Modifier.padding(it)){
                    for (w in repository.shopBullets){
                        BulletShopEntry(w,{},playerState.value!!.money>=w.cost)
                    }
                }
            }
        },
        ContentTab("Medikits"){
            if(repository.shopMedikits.isNotEmpty()){
                Column (modifier = Modifier.padding(it)){
                    for (w in repository.shopMedikits){
                        MedikitEntryShop(w,{},playerState.value!!.money>=w.cost)
                    }
                }
            }
        },
        ContentTab("Upgrades"){
            if(repository.shopUpgrades.isNotEmpty()){
                Column (modifier = Modifier.padding(it)){
                    for (w in repository.shopUpgrades){
                        UpgradeEntryShop(w,{},playerState.value!!.money>=w.cost)
                    }
                }
            }
        }
    ))
}