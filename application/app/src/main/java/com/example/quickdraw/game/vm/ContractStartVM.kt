package com.example.quickdraw.game.vm

import android.app.Activity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import com.example.quickdraw.PrefKeys
import com.example.quickdraw.dataStore
import com.example.quickdraw.game.GameNavigation
import com.example.quickdraw.game.repo.GameRepository
import com.example.quickdraw.network.data.ActiveContract
import com.example.quickdraw.network.data.AvailableContract
import com.example.quickdraw.network.data.EmployedMercenary
import com.example.quickdraw.notifications.QDNotifManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ContractStartVM(
    contractId: Int,
    private val controller: NavHostController,
    private val globalsVM: GlobalPartsVM,
    private val repository: GameRepository,
    private val activity: Activity
): ViewModel() {
    val contract = repository.contracts.available.value.firstOrNull { c -> c.id == contractId }
        ?: AvailableContract(0, "", 0,0,0,0)
    val availableMercenaries = repository.mercenaries.unAssigned
    val selectedMercenaries = MutableStateFlow<List<EmployedMercenary>>(listOf())

    fun startContract(){
        controller.navigate(GameNavigation.Contracts)
        globalsVM.loadScreen.showLoading("Starting...")

        val mercenaries = selectedMercenaries.value.map {x->x.idEmployment}

        viewModelScope.launch {
            repository.contracts.start(contract, mercenaries)
            val sendNotification = activity.dataStore.data.map { preferences -> preferences[PrefKeys.enableNotifications] }.firstOrNull() ?: true
            if(sendNotification){
                QDNotifManager.scheduleContractNotification(activity, ActiveContract(
                    contract.id,
                    contract.name,
                    contract.requiredTime,
                    0,
                    listOf()
                ))
            }
        }
        globalsVM.loadScreen.hideLoading()
    }

    fun selectMercenary(m: EmployedMercenary){
        if(!selectedMercenaries.value.contains(m)){
            selectedMercenaries.update { it + m }
        }
    }

    fun unselectMercenary(m: EmployedMercenary){
        if(selectedMercenaries.value.contains(m)){
            selectedMercenaries.update { it - m }
        }
    }

    fun successChance(): Float{
        var successRate = 100.0f
        if(contract.requiredPower > 0){
            successRate =
                kotlin.math.round((selectedMercenaries.value.sumOf { x -> x.power }
                    .toDouble() / (contract.requiredPower).toDouble()) * 100).toFloat()
                    .coerceAtMost(100.0f)
        }
        return successRate
    }
}