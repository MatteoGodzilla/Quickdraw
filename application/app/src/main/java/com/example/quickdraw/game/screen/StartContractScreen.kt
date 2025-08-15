package com.example.quickdraw.game.screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
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
import com.example.quickdraw.game.components.BasicScreen
import com.example.quickdraw.game.components.ContentTab
import com.example.quickdraw.game.components.RowDivider
import com.example.quickdraw.game.dataDisplayers.AssignableMercenary
import com.example.quickdraw.game.repo.GameRepository
import com.example.quickdraw.game.vm.ContractStartVM
import com.example.quickdraw.ui.theme.Typography
import kotlinx.coroutines.flow.update


@Composable
fun StartContractScreen(controller: NavHostController, repository: GameRepository,vm: ContractStartVM,callbacks: ContractsCallbacks){

    val selected = vm.selectedContractState.collectAsState()
    val selectedMercs = vm.selectedMercenariesState.collectAsState()
    val availableContracts = repository.contracts.available.collectAsState()
    val unassigned = repository.mercenaries.unAssigned.collectAsState()

    BasicScreen("Start Contract",controller,listOf(ContentTab("Start"){
        Box(modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
        ){
            /**Button(
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
            }**/
            Text("Select mercenaries",
                modifier = Modifier.fillMaxWidth().padding(horizontal = 15.dp, vertical = 15.dp).align(Alignment.Center),
                fontSize = Typography.titleLarge.fontSize,
                textAlign = TextAlign.Center,
            )
        }

        val selected = availableContracts.value.filter { x->x.id == selected.value }
        if(selected.isEmpty()){
            vm.unselectContract()
        }
        else{
            //Data
            val currentContract = selected.first()
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
                        callbacks.onStartContract(currentContract,selectedMercs.value.map{x->x.first})
                        vm.unselectContract()
                        controller.navigate(GameNavigation.Contracts)
                    }) {
                    Text("Start contract (${currentContract.startCost})", textAlign = TextAlign.Center,
                        modifier= Modifier.fillMaxWidth())
                }
            }
        }
    }))
}