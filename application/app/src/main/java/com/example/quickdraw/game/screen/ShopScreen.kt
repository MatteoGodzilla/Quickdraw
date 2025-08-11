package com.example.quickdraw.game.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.currentCompositionLocalContext
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.PreviewActivity
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.quickdraw.dataStore
import com.example.quickdraw.game.GameActivity
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
import com.example.quickdraw.network.data.ShopWeapon
import kotlinx.coroutines.delay

interface ShopCallbacks {
    fun onBuyBullet(toBuy: ShopBullet)
    fun onBuyMedikit(toBuy: ShopMedikit)
    fun onBuyWeapon(toBuy: ShopWeapon)
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
            if(weapons.value.isNotEmpty()){
                for (w in weapons.value){
                    WeaponEntryShop(w,{
                        callbacks.onBuyWeapon(w)
                    },playerState.value!!.money>=w.cost)
                }
            }
        },
        ContentTab("Bullets"){
            if(bullets.value.isNotEmpty()){
                for (w in bullets.value){
                    val possessed = if(!possessedBullets.value.any{x->x.type==w.type}) 0 else possessedBullets.value.first { x -> x.type == w.type }.amount
                    BulletShopEntry(w,{callbacks.onBuyBullet(w)},
                        playerState.value!!.money>=w.cost && possessed<w.capacity,possessed)
                }
            }
        },
        ContentTab("Medikits"){
            if(medikits.value.isNotEmpty()){
                for (w in medikits.value){
                    val possessed = if(!possessedMedikits.value.any{x->x.id==w.idMedikit}) 0 else possessedMedikits.value.first { x -> x.id == w.idMedikit }.amount
                    MedikitEntryShop(w,{callbacks.onBuyMedikit(w)},
                        playerState.value!!.money>=w.cost && possessed < w.capacity,possessed)
                }
            }
        },
        ContentTab("Upgrades"){
            if(upgrades.value.isNotEmpty()){
                for (w in upgrades.value){
                    UpgradeEntryShop(w,{},
                        playerState.value!!.money>=w.cost)
                }
            }
        }
    ), money = player.value!!.money, showMoney = true)
}