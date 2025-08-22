package com.example.quickdraw.duel.VMs

import androidx.lifecycle.ViewModel
import com.example.quickdraw.game.repo.InventoryRepository
import com.example.quickdraw.network.data.InventoryWeapon
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.any
import kotlinx.coroutines.flow.update

class WeaponSelectionViewModel(
    val weapons: MutableStateFlow<List<InventoryWeapon>>
): ViewModel() {
    val selectedWeapon = MutableStateFlow(-1)
    val power = MutableStateFlow(0)

    fun select(id:Int){
        if(weapons.value.any{x->x.id==id}){
            selectedWeapon.update { id }
            power.update { weapons.value.first{x->x.id==id}.damage }
        }
    }

    fun selectMostDamage(){
        val selected = weapons.value.maxBy { x->x.damage }
        select(selected.id)
    }

    fun selectMostBullets(invetory: InventoryRepository){
        var best = 0
        var choice = weapons.value.first().id

        for(w in weapons.value){
            if(invetory.bullets.value.any { x->x.type==w.id }){
                val amount = invetory.bullets.value.first{x->x.type==w.id}.amount
               if(best < amount){
                    best = amount
                    choice = w.id
               }
            }
        }
        select(choice)
    }

    fun unselect(){
        selectedWeapon.update { -1 }
    }
}