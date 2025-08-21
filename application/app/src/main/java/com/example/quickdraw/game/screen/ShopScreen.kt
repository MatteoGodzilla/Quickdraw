package com.example.quickdraw.game.screen

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.navigation.NavHostController
import com.example.quickdraw.game.components.BasicScreen
import com.example.quickdraw.game.components.BulletShopEntry
import com.example.quickdraw.game.components.ContentTab
import com.example.quickdraw.game.components.LockedWeapon
import com.example.quickdraw.game.components.MedikitEntryShop
import com.example.quickdraw.game.components.RowDivider
import com.example.quickdraw.game.components.UpgradeEntryShop
import com.example.quickdraw.game.components.WeaponEntryShop
import com.example.quickdraw.game.vm.ShopScreenVM

@Composable
fun ShopScreen (viewModel: ShopScreenVM, controller: NavHostController) {
    //collectable states for shop
    val weapons = viewModel.weapons.collectAsState()
    val bullets = viewModel.bullets.collectAsState()
    val medikits = viewModel.medikits.collectAsState()
    val upgrades = viewModel.upgrades.collectAsState()

    val ownedBullets = viewModel.ownedBullets.collectAsState()
    val ownedMedikits = viewModel.ownedMedikits.collectAsState()

    //for money and level
    val player = viewModel.player.collectAsState()

    BasicScreen("Shop", controller, listOf(
        ContentTab("Weapons"){
            var nextUnlock = player.value.level
            if(weapons.value.any { x->x.level > player.value.level }){
                nextUnlock = weapons.value.filter { x->x.level>player.value.level }.minBy { x->x.level }.level
            }

            if(weapons.value.isNotEmpty()){
                for (w in weapons.value.sortedBy { x->x.level }.filter { x->x.level<=nextUnlock }){
                    val icon = viewModel.getWeaponIcon(w.id).collectAsState().value
                    if(w.level>player.value.level){
                        LockedWeapon(w, icon)
                        RowDivider()
                    }
                    else{
                        WeaponEntryShop(w, { viewModel.onBuyWeapon(w) }, icon,player.value.money>=w.cost)
                    }
                }
            }
        },
        ContentTab("Bullets"){
            var nextUnlock = player.value.level
            if(bullets.value.any { x->x.level > player.value.level }){
                nextUnlock = bullets.value.filter { x->x.level>player.value.level }.minBy { x->x.level }.level
            }
            if(bullets.value.isNotEmpty()){
                for (pair in bullets.value.filter { x->x.level<=nextUnlock }.sortedBy { x->x.level }.groupBy { it.name }){
                    if(pair.value.first().level> player.value.level ){
                        SmallHeader("${pair.key} (Unlock at level ${pair.value.first().level})",true)
                        RowDivider()
                    }
                    else{
                        SmallHeader(pair.key)
                        for (b in pair.value){
                            val possessed = ownedBullets.value.firstOrNull { x -> x.type == b.type }?.amount ?: 0
                            val purchasable = player.value.money >= b.cost && possessed<b.capacity
                            val icon = viewModel.getBulletIcon(b.id).collectAsState().value
                            BulletShopEntry(b,{viewModel.onBuyBullet(b)}, icon, purchasable,possessed)
                        }
                    }
                }
            }
        },
        ContentTab("Medikits"){
            var nextUnlock = player.value.level
            if(medikits.value.any { x->x.level > player.value.level }){
                nextUnlock = medikits.value.filter { x->x.level>player.value.level }.minBy { x->x.level }.level
            }

            if(medikits.value.isNotEmpty()){
                for (pair in medikits.value.filter { x->x.level<=nextUnlock }.sortedBy { x->x.level }.groupBy { it.description }){
                    if(pair.value.first().level> player.value.level){
                        SmallHeader("${pair.key} (Unlock at level ${pair.value.first().level})",true)
                        RowDivider()
                    }
                    else{
                        SmallHeader(pair.key)
                        for(m in pair.value){
                            val possessed = ownedMedikits.value.firstOrNull { x -> x.id == m.idMedikit }?.amount ?: 0
                            val purchasable = player.value.money >= m.cost && possessed < m.capacity
                            val icon = viewModel.getMedikitIcon(m.id).collectAsState().value
                            MedikitEntryShop(m,{viewModel.onBuyMedikit(m)}, icon, purchasable,possessed)
                        }
                    }
                }
            }
        },
        ContentTab("Upgrades"){
            if(upgrades.value.isNotEmpty()){
                for (w in upgrades.value){
                    val icon = viewModel.getUpgradeIcon(w.id).collectAsState().value
                    UpgradeEntryShop(w,{viewModel.onBuyUpgrade(w)}, icon,player.value.money>=w.cost)
                }
            }
        }
    ), money = player.value.money, showMoney = true)
}