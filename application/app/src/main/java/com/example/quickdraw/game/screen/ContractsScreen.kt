package com.example.quickdraw.game.screen


import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.currentCompositionLocalContext
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.asImageBitmap
import androidx.navigation.NavHostController
import com.example.quickdraw.ImageLoader
import com.example.quickdraw.game.GameNavigation
import com.example.quickdraw.game.components.ActiveContractUI
import com.example.quickdraw.game.components.AvailableContractUI
import com.example.quickdraw.game.components.BasicScreen
import com.example.quickdraw.game.components.ContentTab
import com.example.quickdraw.game.components.EmployedMercenaryPost
import com.example.quickdraw.game.components.LockedMercenaryPost
import com.example.quickdraw.game.components.MercenaryShopEntry
import com.example.quickdraw.game.components.TopScreenInfo
import com.example.quickdraw.game.repo.GameRepository
import com.example.quickdraw.game.vm.ContractsVM
import kotlinx.coroutines.delay

@Composable
fun ContractsScreen (vm: ContractsVM, controller: NavHostController, repository: GameRepository, imageLoader: ImageLoader) {
    //mutable states for mercenaries
    val unassigned by repository.mercenaries.unAssigned.collectAsState()
    val employedAll by repository.mercenaries.playerEmployed.collectAsState()
    val hireable by repository.mercenaries.hireable.collectAsState()
    val unlockable by repository.mercenaries.nextUnlockables.collectAsState()

    //mutable states for contracts
    val activeContracts = repository.contracts.active.collectAsState()
    val availableContracts = repository.contracts.available.collectAsState()

    //for mercenaries shop
    val player = repository.player.player.collectAsState()
    val stats = repository.player.stats.collectAsState()

    BasicScreen("Contracts", controller, listOf(
        ContentTab("Active"){
            TopScreenInfo("Executing ${activeContracts.value.size}/${stats.value.maxContracts} contracts")
            var timeSeconds by remember { mutableLongStateOf(0L) }
            for(contract in activeContracts.value){
                ActiveContractUI(contract, timeSeconds) {
                    vm.onRedeemContract(contract)
                }
            }
            LaunchedEffect(activeContracts.value) {
                while(true){
                    timeSeconds = System.currentTimeMillis() / 1000
                    delay(500)
                }
            }
        },
        ContentTab("Available"){
            if(stats.value.maxContracts<= activeContracts.value.size){
                TopScreenInfo("All your slots are being used,wait for contracts to finish and redeem them!")
            }
            for(contract in availableContracts.value){
                AvailableContractUI(contract, {vm.selectMercenariesForContract(contract)} ,
                    player.value.money >= contract.startCost && stats.value.maxContracts> activeContracts.value.size
                )
            }
        },
        ContentTab("Mercenary"){
            for(playerMercenary in employedAll){
                EmployedMercenaryPost(playerMercenary,
                    (unassigned.any{x->x.idEmployment == playerMercenary.idEmployment})
                )
            }
        },
        ContentTab("Employ"){
            for(mercenary in hireable){
                MercenaryShopEntry(mercenary,{vm.onHireMercenary(mercenary)}, imageLoader.notFound, player.value.money >= mercenary.cost)
            }
            for(mercenary in unlockable){
                LockedMercenaryPost(mercenary,imageLoader.notFound)
            }
        }
    ), money = player.value.money, showMoney = true)
}

