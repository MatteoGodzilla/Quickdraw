package com.example.quickdraw.game.vm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import com.example.quickdraw.game.GameNavigation
import com.example.quickdraw.game.repo.GameRepository
import com.example.quickdraw.network.data.ActiveContract
import com.example.quickdraw.network.data.AvailableContract
import com.example.quickdraw.network.data.HireableMercenary
import kotlinx.coroutines.launch

class ContractsVM(
    private val repository: GameRepository,
    private val globalsVM: GlobalPartsVM,
    private val controller: NavHostController
) : ViewModel() {
    fun onRedeemContract(activeContract: ActiveContract) = viewModelScope.launch{
        repository.contracts.redeem(activeContract)
        val redeemedCoins = repository.contracts.lastRedeemed.value
        if(redeemedCoins>0) globalsVM.popup.showLoading("Contract completed! You gained $redeemedCoins coins")
        else globalsVM.popup.showLoading("Yor mercenaries failed the contract :(",false)
    }

    fun selectMercenariesForContract(contract: AvailableContract) {
        controller.navigate(GameNavigation.StartContract(contract.id))
    }

    fun onHireMercenary(hireable: HireableMercenary) = viewModelScope.launch{
        repository.mercenaries.employ(hireable)
        globalsVM.popup.showLoading("Mercenary hired!",true)
    }
}