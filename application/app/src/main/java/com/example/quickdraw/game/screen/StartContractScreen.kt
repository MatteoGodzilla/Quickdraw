package com.example.quickdraw.game.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.quickdraw.game.GameNavigation
import com.example.quickdraw.game.components.RowDivider
import com.example.quickdraw.game.dataDisplayers.ActiveContract
import com.example.quickdraw.game.dataDisplayers.AssignableMercenary
import com.example.quickdraw.game.repo.GameRepository
import com.example.quickdraw.game.vm.ContractStartVM
import com.example.quickdraw.network.data.AvailableContract
import com.example.quickdraw.ui.theme.QuickdrawTheme
import com.example.quickdraw.ui.theme.Typography


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StartContractScreen(controller: NavHostController, repository: GameRepository,vm: ContractStartVM,callbacks: ContractsCallbacks){

    val selected = vm.selectedContractState.collectAsState()
    val selectedMercs = vm.selectedMercenariesState.collectAsState()
    val availableContracts = repository.contracts.available.collectAsState()
    val unassigned = repository.mercenaries.unAssigned.collectAsState()

    //calculations
    val selectedContract = availableContracts.value.filter { x->x.id == selected.value }
    val doesNotExist = selectedContract.isEmpty()
    val currentContract = if(doesNotExist) AvailableContract(0,"",0,0,0,0) else selectedContract.first()
    //to avoid the app crashing in case something weird happens
    val notTooMany = if(doesNotExist) false else selectedMercs.value.size<=currentContract.maxMercenaries
    val atLeastOne = if(doesNotExist) false else selectedMercs.value.isNotEmpty()
    val successRate = if(doesNotExist) 0 else vm.successChance(currentContract.requiredPower)

    QuickdrawTheme {
        Scaffold (
            topBar = {
                CenterAlignedTopAppBar(
                    title = { Text("Start Contract") },
                    modifier = Modifier.padding(0.dp),
                    navigationIcon = {
                        IconButton(onClick = { controller.navigate(GameNavigation.Contracts) }) {
                            Icon(Icons.AutoMirrored.Default.ArrowBack,"go back")
                        }
                    }
                )
            },
            bottomBar = {
                BottomAppBar(
                    modifier = Modifier
                        .windowInsetsPadding(WindowInsets.navigationBars)
                ){
                    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 10.dp)){
                        Spacer(modifier = Modifier.weight(0.5f).fillMaxWidth())
                        Button(enabled = notTooMany && atLeastOne,
                            modifier = Modifier.padding(horizontal = 10.dp),
                            onClick = {
                                vm.unselectContract()
                                controller.navigate(GameNavigation.Contracts)
                                callbacks.onStartContract(currentContract,selectedMercs.value.map{x->x.first})
                            }) {
                            Text("Start contract (${currentContract.startCost})", textAlign = TextAlign.Center,
                                modifier= Modifier.fillMaxWidth())
                        }
                    }
                }
            }
        ) { padding ->
            Column(
                modifier = Modifier.padding(padding).verticalScroll(rememberScrollState())
            ){
                if(doesNotExist){
                    vm.unselectContract()
                }
                else{
                    Column(modifier = Modifier.fillMaxWidth().padding(top=0.dp, bottom = 5.dp).background(color = MaterialTheme.colorScheme.surfaceContainer),
                        horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Completion time:${currentContract.requiredTime}", fontSize = Typography.bodyLarge.fontSize)
                        Text("Chance of success:${successRate}%", fontSize = Typography.bodyLarge.fontSize)
                        Text("Selected :${selectedMercs.value.size}/${currentContract.maxMercenaries} mercenaries"
                            , fontSize = Typography.bodyLarge.fontSize, color = if(notTooMany) Color.Black else Color.Red)
                    }

                    //display mercenaries
                    Spacer(modifier= Modifier.height(24.dp))
                    RowDivider()
                    for(merc in unassigned.value){
                        val checkBoxSelectable = selectedMercs.value.any{x->x.first==merc.idEmployment} || selectedMercs.value.size<currentContract.maxMercenaries
                        AssignableMercenary(merc,vm,checkBoxSelectable)
                    }
                }
            }
        }
    }
}