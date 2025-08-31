package com.example.quickdraw.game.vm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.quickdraw.ImageLoader
import com.example.quickdraw.game.repo.GameRepository
import com.example.quickdraw.network.data.ShopBullet
import com.example.quickdraw.network.data.ShopMedikit
import com.example.quickdraw.network.data.ShopUpgrade
import com.example.quickdraw.network.data.ShopWeapon
import kotlinx.coroutines.launch

class ShopScreenVM(
    private val repository: GameRepository,
    private val imageLoader: ImageLoader,
) : ViewModel() {

    val player = repository.player.player
    val bullets = repository.shop.bullets
    val weapons = repository.shop.weapons
    val medikits = repository.shop.medikits
    val upgrades = repository.shop.upgrades

    //collectable states for inventory (needed for showing possessed vs max capacity
    val ownedBullets = repository.inventory.bullets
    val ownedMedikits = repository.inventory.medikits

    fun onBuyBullet(toBuy: ShopBullet) {
        viewModelScope.launch { repository.shop.buyBullet(toBuy) }
    }

    fun onBuyMedikit(toBuy: ShopMedikit) {
        viewModelScope.launch { repository.shop.buyMedikit(toBuy) }
    }

    fun onBuyWeapon(toBuy: ShopWeapon) {
        viewModelScope.launch { repository.shop.buyWeapon(toBuy) }
    }

    fun onBuyUpgrade(toBuy: ShopUpgrade) {
        viewModelScope.launch {
            repository.shop.buyUpgrade(toBuy)
        }
    }

    //Icon stuff
    fun getWeaponIcon(id:Int) = imageLoader.getWeaponFlow(id)
    fun getBulletIcon(id:Int) = imageLoader.getBulletShopFlow(id)
    fun getMedikitIcon(id:Int) = imageLoader.getMedikitShopFlow(id)
    fun getUpgradeIcon(id:Int) = imageLoader.getUpgradeFlow(id)
}