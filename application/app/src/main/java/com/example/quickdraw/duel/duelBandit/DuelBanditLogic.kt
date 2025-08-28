package com.example.quickdraw.duel.duelBandit

import android.content.Context
import android.content.Intent
import android.os.VibrationEffect
import android.os.Vibrator
import android.util.Log
import androidx.core.text.util.LocalePreferences
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.lifecycle.ViewModel
import com.example.quickdraw.PrefKeys
import com.example.quickdraw.TAG
import com.example.quickdraw.duel.DuelState
import com.example.quickdraw.duel.MAX_DELAY
import com.example.quickdraw.duel.MIN_DELAY
import com.example.quickdraw.duel.Message
import com.example.quickdraw.duel.MessageType
import com.example.quickdraw.duel.PeerState
import com.example.quickdraw.duel.vms.WeaponSelectionViewModel
import com.example.quickdraw.game.GameActivity
import com.example.quickdraw.game.repo.GameRepository
import com.example.quickdraw.music.AudioManager
import com.example.quickdraw.network.data.Bandit
import com.example.quickdraw.network.data.FightAttempt
import com.example.quickdraw.network.data.InventoryWeapon
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.math.round
import kotlin.random.Random
import kotlin.random.nextLong

enum class DuelBanditState{
    SELECT,STEADY,BANG
}

class DuelBanditLogic(
    val id:Int,
    val banditInfo: Bandit,
    val repo: GameRepository,
    val context: Context
){
    private val localScope = CoroutineScope(Dispatchers.IO)
    val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
    val botHP: MutableStateFlow<Int> = MutableStateFlow(banditInfo.hp)
    val selectedWeapon: MutableStateFlow<InventoryWeapon?> = MutableStateFlow(null)
    val gameState:MutableStateFlow<DuelBanditState> = MutableStateFlow(DuelBanditState.SELECT)
    var banditTimer= MutableStateFlow(0L)
    var shootTimer = MutableStateFlow(0L)
    var roundEnds  = MutableStateFlow(false)
    var canShoot = MutableStateFlow(false)
    var playerWon  = MutableStateFlow(false)
    var duelHistory: MutableStateFlow<List<FightAttempt>> = MutableStateFlow(listOf())
    var isGameEnded = MutableStateFlow(false)

    fun isDuelOver(): Boolean{
        // either someone is defeated or player is out of ammo
        return botHP.value<= 0 || repo.player.player.value.health <= 0 || repo.inventory.bullets.value.sumOf { x->x.amount } == 0
    }

    fun getEndGameMessage():String{
        if(botHP.value<= 0)return "You defeated the bandit!"
        if(repo.player.player.value.health <= 0) return "You were defeated"
        if(repo.inventory.bullets.value.sumOf { x->x.amount } == 0) return "You ran out of bullets"
        return "Unknown error"
    }

    private fun prepareSteady(){
        val rand = kotlin.random.Random
        banditTimer.update { rand.nextLong(banditInfo.minSpeed.toLong(),banditInfo.maxSpeed.toLong()) }
        shootTimer.update { Random.nextLong(5000L,10000L) }
    }

    fun setWeaponAndStart(w: InventoryWeapon){
        selectedWeapon.update { w }
        prepareSteady()
        gameState.update { DuelBanditState.STEADY }
    }

    fun bang(isPlayer: Boolean) = localScope.launch{
        val playerWinner = isPlayer && canShoot.value
        if(gameState.value == DuelBanditState.STEADY){
            gameState.update { DuelBanditState.BANG }
            repo.inventory.bullets.value = repo.inventory.bullets.value.map { b ->
                if(b.type == selectedWeapon.value!!.bulletType) b.copy(amount = b.amount - selectedWeapon.value!!.bulletsShot)
                else b
            }
            damageCalc(playerWinner)
            playerWon.update { playerWinner}
            roundEnds.update { true }
            AudioManager.startSFX()
        }
    }

    fun damageCalc(playerWins: Boolean){
        if(playerWins){
            botHP.update { x->x-selectedWeapon.value!!.damage }
            duelHistory.update { x->x+ FightAttempt(true,selectedWeapon.value!!.id,0) }
        }
        else{
            val rand = kotlin.random.Random
            val damage = rand.nextInt(banditInfo.minDamage,banditInfo.maxDamage+1)
            repo.player.player.update { x->x.copy(health = x.health-damage) }
            duelHistory.update { x->x+ FightAttempt(false,selectedWeapon.value!!.id,damage) }
        }
    }

    fun resetToSelect(){
        gameState.update { DuelBanditState.SELECT }
        roundEnds.update { false }
        playerWon.update { false }
        banditTimer.update { 0L }
        shootTimer.update { 0L }
        canShoot.update { false }
    }

    fun allowShooting(){
        vibrator.vibrate(VibrationEffect.createOneShot(1000, VibrationEffect.DEFAULT_AMPLITUDE))
        canShoot.update { true }
    }

    fun sendToServer(){
        localScope.launch {
            repo.bandits.fight(id,duelHistory.value)
        }
    }

    fun sendBackToMainGame(){
        localScope.launch {
            val intent = Intent(context, GameActivity::class.java)
            context.startActivity(intent)
            isGameEnded.update { true }
        }
    }

    suspend fun setFavourite(dataStore: DataStore<Preferences>,vm: WeaponSelectionViewModel){
        val favourite = dataStore.data.map { pref -> pref[PrefKeys.favouriteWeapon] }.firstOrNull()
        if(favourite!=null){
            val weapon = repo.inventory.weapons.value.firstOrNull{x->x.id==favourite}
            if(weapon!=null){
                if(repo.inventory.bullets.value.any{x->x.amount >= weapon.bulletsShot && x.type== weapon.bulletType}){
                    selectedWeapon.update { weapon }
                    vm.select(weapon)
                }
            }
        }
    }
}