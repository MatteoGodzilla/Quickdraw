package com.example.quickdraw.game.screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import com.example.quickdraw.game.dataDisplayers.AssignableMercenary
import com.example.quickdraw.game.repo.GameRepository
import com.example.quickdraw.game.vm.ContractStartVM
import com.example.quickdraw.ui.theme.QuickdrawTheme
import com.example.quickdraw.ui.theme.Typography


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StartContractScreen(controller: NavHostController, repository: GameRepository,vm: ContractStartVM,callbacks: ContractsCallbacks){

    val selected = vm.selectedContractState.collectAsState()
    val selectedMercs = vm.selectedMercenariesState.collectAsState()
    val availableContracts = repository.contracts.available.collectAsState()
    val unassigned = repository.mercenaries.unAssigned.collectAsState()

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
            bottomBar = {}
        ) { padding ->
            Column(
                modifier = Modifier.padding(padding).verticalScroll(rememberScrollState())
            ){
                Box(modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ){
                    Text("Select mercenaries",
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 15.dp, vertical = 15.dp).align(Alignment.Center),
                        fontSize = Typography.titleLarge.fontSize,
                        textAlign = TextAlign.Center,
                    )
                }

                val selectedContract = availableContracts.value.filter { x->x.id == selected.value }
                if(selectedContract.isEmpty()){
                    vm.unselectContract()
                }
                else{
                    //Data
                    val currentContract = selectedContract.first()
                    val notTooMany = selectedMercs.value.size<=currentContract.maxMercenaries
                    val atLeastOne = selectedMercs.value.isNotEmpty()
                    val successRate = vm.successChance(currentContract.requiredPower)
                    Column(modifier = Modifier.fillMaxWidth().padding(vertical = 5.dp),
                        horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Completion time:${currentContract.requiredTime}", fontSize = Typography.bodyLarge.fontSize)
                        Text("Chance of success:${successRate}%", fontSize = Typography.bodyLarge.fontSize)
                        Text("Selected :${selectedMercs.value.size}/${currentContract.maxMercenaries} mercenaries"
                            , fontSize = Typography.bodyLarge.fontSize, color = if(notTooMany) Color.Black else Color.Red)
                    }

                    //display mercenaries
                    RowDivider()
                    for(merc in unassigned.value){
                        val checkBoxSelectable = selectedMercs.value.any{x->x.first==merc.idEmployment} || selectedMercs.value.size<currentContract.maxMercenaries
                        AssignableMercenary(merc,vm,checkBoxSelectable)
                    }

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
        }
    }
}