package com.example.quickdraw.game.screen

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.navigation.NavHostController
import com.example.quickdraw.game.repo.GameRepository
import com.example.quickdraw.game.components.BasicScreen
import com.example.quickdraw.game.components.ContentTab
import com.example.quickdraw.game.dataDisplayers.BulletShopEntry
import com.example.quickdraw.game.dataDisplayers.MedikitEntryShop
import com.example.quickdraw.game.dataDisplayers.UpgradeEntryShop
import com.example.quickdraw.game.dataDisplayers.WeaponEntryShop
import com.example.quickdraw.network.data.ShopBullet
import com.example.quickdraw.network.data.ShopMedikit
import com.example.quickdraw.network.data.ShopUpgrade
import com.example.quickdraw.network.data.ShopWeapon

interface ShopCallbacks {
    fun onBuyBullet(toBuy: ShopBullet)
    fun onBuyMedikit(toBuy: ShopMedikit)
    fun onBuyWeapon(toBuy: ShopWeapon)
    fun onBuyUpgrade(toBuy: ShopUpgrade)
}

@Composable
fun ShopScreen (controller: NavHostController, repository: GameRepository, callbacks: ShopCallbacks) {

    //collectable states for shop
    val playerState = repository.player.status.collectAsState()
    val bullets = repository.shop.bullets.collectAsState()
    val weapons = repository.shop.weapons.collectAsState()
    val medikits = repository.shop.medikits.collectAsState()
    val upgrades = repository.shop.upgrades.collectAsState()

    //collectable states for inventory (needed for showing possessed vs max capacity
    val possessedBullets = repository.inventory.bullets.collectAsState()
    val possessedWeapons = repository.inventory.weapons.collectAsState()
    val possessedMedikits = repository.inventory.medikits.collectAsState()
    val possessedUpgrades = repository.inventory.upgrades.collectAsState()

    //for money
    val player = repository.player.status.collectAsState()

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
                    UpgradeEntryShop(w,{callbacks.onBuyUpgrade(w)},
                        playerState.value!!.money>=w.cost)
                }
            }
        }
    ), money = player.value!!.money, showMoney = true)
}