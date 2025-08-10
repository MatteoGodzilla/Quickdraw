package com.example.quickdraw.game.screen


import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.quickdraw.R
import com.example.quickdraw.game.components.BasicScreen
import com.example.quickdraw.game.components.ContentTab
import com.example.quickdraw.network.data.ActiveContract
import com.example.quickdraw.network.data.AvailableContract
import com.example.quickdraw.game.GameRepository
import com.example.quickdraw.game.dataDisplayers.*
import com.example.quickdraw.network.data.HireableMercenary
import com.example.quickdraw.ui.theme.Typography
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

interface ContractsCallbacks {
    fun onRedeemContract(activeContract: ActiveContract)
    fun onStartContract(availableContract: AvailableContract,mercenaries:List<Int>)
    fun onHireMercenary(hireable: HireableMercenary)
}


@Composable
fun ContractsScreen (controller: NavHostController, repository: GameRepository, callbacks: ContractsCallbacks) {
    //mutable states for mercenaries
    val unassigned by repository.unAssignedMercenaries.collectAsState()
    val employedAll by repository.playerEmployedMercenaries.collectAsState()
    val hireable by repository.hireableMercenaries.collectAsState()
    val unlockable by repository.nextUnlockablesMercenaries.collectAsState()

    //mutable states for contracts starting (selectedMercenariesState pair is id and power)
    val selectedContractState = MutableStateFlow(-1)
    val selectedMercenariesState = MutableStateFlow<List<Pair<Int,Int>>>(listOf())
    val selectedContract = selectedContractState.collectAsState()
    val selectedMercenaries = selectedMercenariesState.collectAsState()

    //mutable states for contracts
    val activeContracts = repository.activeContracts.collectAsState()
    val availableContracts = repository.availableContracts.collectAsState()

    //for mercenaries shop
    val player = repository.player.collectAsState()

    BasicScreen("Contracts", controller, listOf(
        ContentTab("Active"){
            Column (
                modifier = Modifier.padding(it)
            ){
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
            }
        },
        ContentTab("Available"){
            Column (
                modifier = Modifier.padding(it)
            ){
                if(selectedContract.value == -1){
                    for(contract in availableContracts.value){
                        AvailableContract(contract,
                            {selectedContractState.update { x->contract.id }},
                            player.value!!.money >= contract.startCost
                        )
                    }
                }
                else{
                        Box(modifier = Modifier.fillMaxWidth(),
                            contentAlignment = Alignment.Center
                        ){
                            Button(
                                modifier = Modifier.padding(horizontal = 10.dp)
                                    .align(Alignment.CenterStart),
                                onClick = {selectedContractState.update { x->-1 }
                                    selectedMercenariesState.update { x->listOf() }
                                }) {
                                Icon(
                                    imageVector = ImageVector.vectorResource(R.drawable.home_24px),
                                    "",
                                    tint = Color.Black,
                                )
                            }
                            Text("Select mercenaries",
                                modifier = Modifier.fillMaxWidth().padding(horizontal = 15.dp, vertical = 15.dp).align(Alignment.Center),
                                fontSize = Typography.titleLarge.fontSize,
                                textAlign = TextAlign.Center,
                            )
                        }

                    val selected = availableContracts.value.filter { x->x.id == selectedContract.value }
                    if(selected.isEmpty()){
                        selectedContractState.update { x->-1 }
                    }
                    else{
                        //Data
                        val currentContract = selected.first()
                        val notTooMany = selectedMercenaries.value.size<=currentContract.maxMercenaries
                        val atLeastOne = selectedMercenaries.value.isNotEmpty()
                        var successRate = 100.0
                        if(currentContract.requiredPower>0){
                            successRate =
                                kotlin.math.round((selectedMercenaries.value.sumOf { x -> x.second }
                                    .toDouble() / (currentContract.requiredPower).toDouble()) * 100)
                                    .coerceAtMost(100.0)
                        }

                        Column(modifier = Modifier.fillMaxWidth().padding(vertical = 5.dp),
                            horizontalAlignment = Alignment.CenterHorizontally) {

                            Text("Start cost:${currentContract.startCost}", fontSize = Typography.bodyLarge.fontSize )
                            Text("Completion time:${currentContract.requiredTime}", fontSize = Typography.bodyLarge.fontSize)
                            Text("Chance of success:${successRate}%", fontSize = Typography.bodyLarge.fontSize)
                            Text("Selected :${selectedMercenaries.value.size}/${currentContract.maxMercenaries} mercenaries"
                                , fontSize = Typography.bodyLarge.fontSize, color = if(notTooMany) Color.Black else Color.Red)
                        }

                        //display mercenaries
                        HorizontalDivider()
                        for(merc in unassigned){
                            val checkBoxSelectable = selectedMercenaries.value.any{x->x.first==merc.idEmployment} || selectedMercenaries.value.size<currentContract.maxMercenaries
                            AssignableMercenary(merc,selectedMercenariesState,checkBoxSelectable)
                        }

                        Row(modifier = Modifier.fillMaxWidth().padding(vertical = 10.dp)){

                            Spacer(modifier = Modifier.weight(0.5f).fillMaxWidth())
                            Button(enabled = notTooMany && atLeastOne,
                                modifier = Modifier.padding(horizontal = 10.dp),
                                onClick = {
                                    callbacks.onStartContract(currentContract,selectedMercenaries.value.map{x->x.first})
                                    selectedMercenariesState.update { x->listOf() }
                                    selectedContractState.update { x->-1 }
                                }) {
                                Text("Start contract", textAlign = TextAlign.Center,
                                    modifier= Modifier.fillMaxWidth())
                            }
                        }

                    }

                }
            }
        },
        ContentTab("Mercenary"){
            Column(
                modifier = Modifier.padding(it)
            ){
                for(playerMercenary in employedAll){
                    EmployedMercenaryPost(playerMercenary,
                        (unassigned.any{x->x.idEmployment == playerMercenary.idEmployment})
                    )
                }
            }
        },
        ContentTab("Employ"){
            Column (
                modifier = Modifier.padding(it)
            ){
                for(mercenary in hireable){
                    MercenaryShopEntry(mercenary,{callbacks.onHireMercenary(mercenary)},player.value!!.money>=mercenary.cost)
                }
                for(mercenary in unlockable){
                    LockedMercenaryPost(mercenary)
                }
            }
        }
    ), money = player.value!!.money)
}

