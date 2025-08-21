package com.example.quickdraw.duel.VMs

import androidx.lifecycle.ViewModel
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

    fun unselect(){
        selectedWeapon.update { -1 }
    }
}