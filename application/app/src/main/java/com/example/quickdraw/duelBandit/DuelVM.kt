package com.example.quickdraw.duelBandit

import androidx.lifecycle.ViewModel
import com.example.quickdraw.game.repo.GameRepository
import com.example.quickdraw.network.data.Bandit
import com.example.quickdraw.network.data.InventoryWeapon
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

class DuelVM(
    val banditInfo: Bandit,
    val repo: GameRepository
) : ViewModel(){

    val botHP: MutableStateFlow<Int> = MutableStateFlow(banditInfo.hp)
    val playerHP = repo.player.player.value.health
    val selectedWeapon: MutableStateFlow<InventoryWeapon?> = MutableStateFlow(null)

    fun getName():String{
        return banditInfo.name
    }

    fun isDuelOver(): Boolean{
        // either someone is defeated or player is out of ammo
        return botHP.value<= 0 || playerHP <= 0 || repo.inventory.bullets.value.sumOf { x->x.amount } == 0
    }

    fun duelIsOverMessage():String{
        if(playerHP <= 0) return "You lost! (You died)"
        if(botHP.value<=0) return "You won! (Bandit was defeated)"
        if(repo.inventory.bullets.value.sumOf { x->x.amount } == 0) return "You lost! (You are auto of ammos)"
        return "Something went wrong (returning to main menu)"
    }

    fun damageCalc(playerWins: Boolean){
        if(playerWins){
            botHP.update { x->x-selectedWeapon.value!!.damage }
        }
        else{
            
        }
    }

}