package com.example.quickdraw.game.repo

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.example.quickdraw.network.api.buyBulletsAPI
import com.example.quickdraw.network.api.buyMedikitAPI
import com.example.quickdraw.network.api.buyUpgradeAPI
import com.example.quickdraw.network.api.buyWeaponAPI
import com.example.quickdraw.network.api.getShopBulletsAPI
import com.example.quickdraw.network.api.getShopMedikitsAPI
import com.example.quickdraw.network.api.getShopUpgradesAPI
import com.example.quickdraw.network.api.getShopWeaponsAPI
import com.example.quickdraw.network.data.BuyRequest
import com.example.quickdraw.network.data.InventoryBullet
import com.example.quickdraw.network.data.InventoryMedikit
import com.example.quickdraw.network.data.InventoryUpgrade
import com.example.quickdraw.network.data.InventoryWeapon
import com.example.quickdraw.network.data.ShopBullet
import com.example.quickdraw.network.data.ShopMedikit
import com.example.quickdraw.network.data.ShopUpgrade
import com.example.quickdraw.network.data.ShopWeapon
import com.example.quickdraw.runIfAuthenticated
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

class ShopRepository(
    private val dataStore: DataStore<Preferences>,
    private val playerRepository: PlayerRepository,
    private val inventoryRepository: InventoryRepository
) {
    //Shop
    var weapons: MutableStateFlow<List<ShopWeapon>> = MutableStateFlow(listOf())
        private set
    var bullets: MutableStateFlow<List<ShopBullet>> = MutableStateFlow(listOf())
        private set
    var medikits: MutableStateFlow<List<ShopMedikit>> = MutableStateFlow(listOf())
        private set
    var upgrades: MutableStateFlow<List<ShopUpgrade>> = MutableStateFlow(listOf())
        private set

    suspend fun firstLoad() {
        getWeapons()
        getBullets()
        getMedikits()
        getUpgrades()
    }

    suspend fun getWeapons() = runIfAuthenticated( dataStore ) { auth ->
        weapons.update { getShopWeaponsAPI(auth) }
    }

    suspend fun getBullets() = runIfAuthenticated( dataStore ) { auth ->
        bullets.update { getShopBulletsAPI(auth) }
    }

    suspend fun getMedikits() = runIfAuthenticated( dataStore ) { auth ->
        medikits.update { getShopMedikitsAPI(auth) }
    }

    suspend fun getUpgrades() = runIfAuthenticated( dataStore ) { auth ->
        upgrades.update { getShopUpgradesAPI(auth) }
    }

    suspend fun buyBullet(bullet: ShopBullet) = runIfAuthenticated( dataStore ) { auth ->
        val response = buyBulletsAPI(BuyRequest(id = bullet.id, authToken = auth))
        if (response != null) {
            playerRepository.player.update { player -> player.copy(money = player.money - bullet.cost) }
            if (inventoryRepository.bullets.value.any { it.type == bullet.type }) {
                inventoryRepository.bullets.update {
                    it.map { y ->
                        if (y.type != bullet.type) y.copy()
                        else y.copy(
                            amount = kotlin.math.min(
                                y.amount + bullet.quantity,
                                bullet.capacity
                            )
                        )
                    }
                }
            } else {
                inventoryRepository.bullets.update {
                    it + InventoryBullet(
                        type = bullet.type,
                        description = bullet.name,
                        amount = bullet.quantity,
                        capacity = bullet.capacity
                    )
                }
            }
        }
    }

    suspend fun buyMedikit(medikit: ShopMedikit) = runIfAuthenticated( dataStore ) { auth ->
        val response = buyMedikitAPI(BuyRequest(id = medikit.id, authToken = auth))
        if (response != null) {
            playerRepository.player.update { p -> p.copy(money = p.money - medikit.cost) }
            if (inventoryRepository.medikits.value.any { it.id == medikit.idMedikit }) {
                inventoryRepository.medikits.update {
                    it.map { y ->
                        if (y.id != medikit.idMedikit) y.copy()
                        else y.copy(
                            amount = kotlin.math.min(
                                y.amount + medikit.quantity,
                                medikit.capacity
                            )
                        )
                    }
                }
            } else {
                inventoryRepository.medikits.update { x ->
                    x + InventoryMedikit(
                        healthRecover = medikit.healthRecover,
                        description = medikit.description,
                        amount = medikit.quantity,
                        capacity = medikit.capacity,
                        id = medikit.idMedikit
                    )
                }
            }
        }
    }

    suspend fun buyWeapon(weapon: ShopWeapon) = runIfAuthenticated( dataStore ) { auth ->
        val response = buyWeaponAPI(BuyRequest(id = weapon.id, authToken = auth))
        if (response != null) {
            playerRepository.player.update { x -> x.copy(money = x.money - weapon.cost) }
            weapons.update { it.filter { w -> w.id != weapon.id } }
            inventoryRepository.weapons.update { x -> x + InventoryWeapon(weapon.name, weapon.damage, weapon.cost, 1) }
        }
    }

    suspend fun buyUpgrade(upgrade: ShopUpgrade) = runIfAuthenticated( dataStore ) { auth ->
        val response = buyUpgradeAPI(BuyRequest(id = upgrade.id, authToken = auth))
        if(response!=null){
            playerRepository.player.update { x -> x.copy(money = x.money - upgrade.cost) }
            upgrades.update { x-> x.filter { y -> y.id != upgrade.id } }
            inventoryRepository.upgrades.update{it.filter{u->u.type!=upgrade.type}}
            inventoryRepository.upgrades.update { x -> x + InventoryUpgrade(upgrade.id, upgrade.description, upgrade.type, upgrade.level,upgrade.modifier) }
            upgrades.update { x-> if(response.nextUp.isNotEmpty()) x + response.nextUp.first() else x }
            upgrades.update{it.sortedBy { x->x.id }}
            updateOnSingle(upgrade)
        }
    }

    private fun updateOnSingle(up: ShopUpgrade){
        when (up.type) {
            UpgradeIds.MAX_HEALTH.ordinal -> {
                playerRepository.stats.update { x->x.copy(maxHealth = x.maxHealth + up.modifier) }
            }
            UpgradeIds.MAX_CONTRACTS.ordinal -> {
                playerRepository.stats.update { x->x.copy(maxContracts = x.maxContracts + up.modifier) }
            }
            UpgradeIds.MONEY_BOOST.ordinal -> {
                playerRepository.stats.update { x->x.copy(moneyBoost = x.moneyBoost + up.modifier) }
            }
            UpgradeIds.EXP_BOOST.ordinal -> {
                playerRepository.stats.update { x->x.copy(expBoost = x.expBoost + up.modifier) }
            }
            UpgradeIds.BOUNTY_BOOST.ordinal -> {
                playerRepository.stats.update { x->x.copy(bountyBoost = x.bountyBoost + up.modifier) }
            }
        }
    }
}