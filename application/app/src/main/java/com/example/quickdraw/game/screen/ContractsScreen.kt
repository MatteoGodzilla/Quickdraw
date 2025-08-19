package com.example.quickdraw.game.screen


import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.quickdraw.ImageLoader
import com.example.quickdraw.game.GameNavigation
import com.example.quickdraw.game.components.BasicScreen
import com.example.quickdraw.game.components.ContentTab
import com.example.quickdraw.game.components.RowDivider
import com.example.quickdraw.game.components.TopScreenInfo
import com.example.quickdraw.network.data.ActiveContract
import com.example.quickdraw.network.data.AvailableContract
import com.example.quickdraw.game.repo.GameRepository
import com.example.quickdraw.game.dataDisplayers.*
import com.example.quickdraw.network.data.HireableMercenary
import kotlinx.coroutines.delay

interface ContractsCallbacks {
    fun onRedeemContract(activeContract: ActiveContract)
    fun onStartContract(availableContract: AvailableContract,mercenaries:List<Int>)
    fun onHireMercenary(hireable: HireableMercenary)
}
@Composable
fun ContractsScreen (controller: NavHostController, repository: GameRepository, imageLoader: ImageLoader, callbacks: ContractsCallbacks) {
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
                ActiveContract(contract, timeSeconds) {
                    callbacks.onRedeemContract(contract)
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
                AvailableContract(contract,
                    {
                        controller.navigate(GameNavigation.StartContract(contract.id))
                    },
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
                MercenaryShopEntry(mercenary,{callbacks.onHireMercenary(mercenary)}, imageLoader.imageNotFound.asImageBitmap(), player.value.money >= mercenary.cost)
            }
            for(mercenary in unlockable){
                LockedMercenaryPost(mercenary,imageLoader.imageNotFound.asImageBitmap())
            }
        }
    ), money = player.value.money, showMoney = true)
}

