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

    /*
    private val weaponIcons: MutableMap<Int, MutableStateFlow<ImageBitmap>> = mutableMapOf()
    private val bulletIcons: MutableMap<Int, MutableStateFlow<ImageBitmap>> = mutableMapOf()
    private val medikitIcons: MutableMap<Int, MutableStateFlow<ImageBitmap>> = mutableMapOf()
    private val upgradeIcons: MutableMap<Int, MutableStateFlow<ImageBitmap>> = mutableMapOf()

     */

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
    fun getBulletIcon(id:Int) = imageLoader.getBulletFlow(id)
    fun getMedikitIcon(id:Int) = imageLoader.getMedikitFlow(id)
    fun getUpgradeIcon(id:Int) = imageLoader.getUpgradeFlow(id)
    /*
    fun getWeaponIcon(id: Int): MutableStateFlow<ImageBitmap> {
        if(weaponIcons.containsKey(id)){
            return weaponIcons[id]!!
        }
        val res = MutableStateFlow(imageLoader.imageNotFound.asImageBitmap())
        weaponIcons[id] = res
        viewModelScope.launch { res.value = imageLoader.getWeaponBitmap(id) }
        return res
    }

    fun getBulletIcon(id: Int): MutableStateFlow<ImageBitmap> {
        if(bulletIcons.containsKey(id)){
            return bulletIcons[id]!!
        }
        val res = MutableStateFlow(imageLoader.imageNotFound.asImageBitmap())
        bulletIcons[id] = res
        viewModelScope.launch { res.value = imageLoader.getBulletBitmap(id) }
        return res
    }

    fun getMedikitIcon(id: Int): MutableStateFlow<ImageBitmap> {
        if(medikitIcons.containsKey(id)){
            return medikitIcons[id]!!
        }
        val res = MutableStateFlow(imageLoader.imageNotFound.asImageBitmap())
        medikitIcons[id] = res
        viewModelScope.launch { res.value = imageLoader.getMedikitBitmap(id) }
        return res
    }

    fun getUpgradeIcon(id: Int): MutableStateFlow<ImageBitmap> {
        if(upgradeIcons.containsKey(id)){
            return upgradeIcons[id]!!
        }
        val res = MutableStateFlow(imageLoader.imageNotFound.asImageBitmap())
        upgradeIcons[id] = res
        viewModelScope.launch { res.value = imageLoader.getUpgradeBitmap(id) }
        return res
    }

     */

}