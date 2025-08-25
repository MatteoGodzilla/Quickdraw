package com.example.quickdraw.duel.vms

import androidx.lifecycle.ViewModel
import com.example.quickdraw.network.data.InventoryBullet
import com.example.quickdraw.network.data.InventoryWeapon
import kotlinx.coroutines.flow.MutableStateFlow

class WeaponSelectionViewModel(
    private val weapons: List<InventoryWeapon>,
    private val bullets: List<InventoryBullet>
): ViewModel() {
    val selectedWeapon = MutableStateFlow(weapons[0])

    fun select(weapon: InventoryWeapon){
        selectedWeapon.value = weapon
    }

    fun selectMostDamage() {
        selectedWeapon.value = weapons.maxBy { x -> x.damage }
    }

    fun selectMostBullets(){
        var best = 0
        var choice = weapons[0]

        for(w in weapons){
            if(bullets.any { x->x.type==w.id }){
                val amount = bullets.first{ x->x.type==w.bulletType}.amount
                if(best < amount){
                    best = amount
                    choice = w
                }
            }
        }
        select(choice)
    }
}