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
import com.example.quickdraw.game.components.HorizontalSeparator
import com.example.quickdraw.game.dataDisplayers.ActiveContract
import com.example.quickdraw.game.dataDisplayers.BulletShopEntry
import com.example.quickdraw.game.dataDisplayers.MedikitEntryShop
import com.example.quickdraw.game.dataDisplayers.UpgradeEntryShop
import com.example.quickdraw.game.dataDisplayers.WeaponEntryShop
import com.example.quickdraw.network.data.ActiveContract
import com.example.quickdraw.network.data.AvailableContract
import com.example.quickdraw.network.data.HireableMercenary
import com.example.quickdraw.network.data.ShopBullet
import com.example.quickdraw.network.data.ShopMedikit
import kotlinx.coroutines.delay

interface ShopCallbacks {
    fun onBuyBullet(toBuy: ShopBullet)
    fun onBuyMedikit(toBuy: ShopMedikit)
}

@Composable
fun ShopScreen (controller: NavHostController, repository: GameRepository,callbacks: ShopCallbacks) {

    //collectable states for shop
    val playerState = repository.player.collectAsState()
    val bullets = repository.shopBullets.collectAsState()
    val weapons = repository.shopWeapons.collectAsState()
    val medikits = repository.shopMedikits.collectAsState()
    val upgrades = repository.shopUpgrades.collectAsState()

    //collectable states for inventory (needed for showing possessed vs max capacity
    val possessedBullets = repository.bullets.collectAsState()
    val possessedWeapons = repository.weapons.collectAsState()
    val possessedMedikits = repository.medikits.collectAsState()
    val possessedUpgrades = repository.upgrades.collectAsState()

    //for money
    val player = repository.player.collectAsState()

    BasicScreen("Shop", controller, listOf(
        ContentTab("Weapons"){
            HorizontalSeparator()
            if(weapons.value.isNotEmpty()){
                Column (modifier = Modifier.padding(it)){
                    for (w in weapons.value){
                        WeaponEntryShop(w,{},playerState.value!!.money>=w.cost)
                    }
                }
            }
        },
        ContentTab("Bullets"){
            HorizontalSeparator()
            if(bullets.value.isNotEmpty()){
                Column (modifier = Modifier.padding(it)){
                    for (w in bullets.value){
                        val possessed = if(!possessedBullets.value.any{x->x.type==w.type}) 0 else possessedBullets.value.first { x -> x.type == w.type }.amount
                        BulletShopEntry(w,{callbacks.onBuyBullet(w)},
                            playerState.value!!.money>=w.cost && possessed<w.capacity,possessed)
                    }
                }
            }
        },
        ContentTab("Medikits"){
            HorizontalSeparator()
            if(medikits.value.isNotEmpty()){
                Column (modifier = Modifier.padding(it)){
                    for (w in medikits.value){
                        val possessed = if(!possessedMedikits.value.any{x->x.id==w.idMedikit}) 0 else possessedMedikits.value.first { x -> x.id == w.idMedikit }.amount
                        MedikitEntryShop(w,{callbacks.onBuyMedikit(w)},
                            playerState.value!!.money>=w.cost && possessed < w.capacity,possessed)
                    }
                }
            }
        },
        ContentTab("Upgrades"){
            HorizontalSeparator()
            if(upgrades.value.isNotEmpty()){
                Column (modifier = Modifier.padding(it)){
                    for (w in upgrades.value){
                        UpgradeEntryShop(w,{},
                            playerState.value!!.money>=w.cost)
                    }
                }
            }
        }
    ), money = player.value!!.money)
}