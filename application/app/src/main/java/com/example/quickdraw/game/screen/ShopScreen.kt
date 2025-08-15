package com.example.quickdraw.game.screen

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.navigation.NavHostController
import com.example.quickdraw.game.repo.GameRepository
import com.example.quickdraw.game.components.BasicScreen
import com.example.quickdraw.game.components.ContentTab
import com.example.quickdraw.game.components.RowDivider
import com.example.quickdraw.game.dataDisplayers.BulletShopEntry
import com.example.quickdraw.game.dataDisplayers.LockedWeapon
import com.example.quickdraw.game.dataDisplayers.MedikitEntryShop
import com.example.quickdraw.game.dataDisplayers.UpgradeEntryShop
import com.example.quickdraw.game.dataDisplayers.WeaponEntryShop
import com.example.quickdraw.network.data.ShopBullet
import com.example.quickdraw.network.data.ShopMedikit
import com.example.quickdraw.network.data.ShopUpgrade
import com.example.quickdraw.network.data.ShopWeapon
import kotlinx.coroutines.flow.MutableStateFlow

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

    //for money and level
    val player = repository.player.status.collectAsState()
    val playerLevel = repository.player.level.collectAsState()

    BasicScreen("Shop", controller, listOf(
        ContentTab("Weapons"){
            var nextUnlock = playerLevel.value
            if(weapons.value.any { x->x.level > playerLevel.value }){
                nextUnlock = weapons.value.filter { x->x.level>playerLevel.value }.minBy { x->x.level }.level
            }

            if(weapons.value.isNotEmpty()){
                for (w in weapons.value.sortedBy { x->x.level }.filter { x->x.level<=nextUnlock }){
                    if(w.level>playerLevel.value){
                        LockedWeapon(w)
                        RowDivider()
                    }
                    else{
                        WeaponEntryShop(w,{
                            callbacks.onBuyWeapon(w)
                        },playerState.value!!.money>=w.cost)
                    }
                }
            }
        },
        ContentTab("Bullets"){
            var nextUnlock = playerLevel.value
            if(weapons.value.any { x->x.level > playerLevel.value }){
                nextUnlock = bullets.value.filter { x->x.level>playerLevel.value }.minBy { x->x.level }.level
            }

            if(bullets.value.isNotEmpty()){
                for (pair in bullets.value.filter { x->x.level<=nextUnlock }.sortedBy { x->x.level }.groupBy { it.name }){
                    if(pair.value.first().level> playerLevel.value){
                        SmallHeader("${pair.key} (Unlock at level ${pair.value.first().level})",true)
                        RowDivider()
                    }
                    else{
                        SmallHeader(pair.key)
                        for (b in pair.value){
                            val possessed = possessedBullets.value.firstOrNull { x -> x.type == b.type }?.amount ?: 0
                            val purchasable = playerState.value!!.money >= b.cost && possessed<b.capacity
                            BulletShopEntry(b,{callbacks.onBuyBullet(b)}, purchasable,possessed)
                        }
                    }
                }
            }
        },
        ContentTab("Medikits"){
            var nextUnlock = playerLevel.value
            if(weapons.value.any { x->x.level > playerLevel.value }){
                nextUnlock = bullets.value.filter { x->x.level>playerLevel.value }.minBy { x->x.level }.level
            }

            if(medikits.value.isNotEmpty()){
                for (pair in medikits.value.filter { x->x.level<=nextUnlock }.sortedBy { x->x.level }.groupBy { it.description }){
                    if(pair.value.first().level> playerLevel.value){
                        SmallHeader("${pair.key} (Unlock at level ${pair.value.first().level})",true)
                        RowDivider()
                    }
                    else{
                        SmallHeader(pair.key)
                        for(m in pair.value){
                            val possessed = possessedMedikits.value.firstOrNull { x -> x.id == m.id }?.amount ?: 0
                            val purchasable = playerState.value!!.money >= m.cost && possessed < m.capacity
                            MedikitEntryShop(m,{callbacks.onBuyMedikit(m)}, purchasable,possessed)
                        }
                    }
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