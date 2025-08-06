package com.example.quickdraw.game.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
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
import com.example.quickdraw.network.data.EmployedMercenary
import com.example.quickdraw.network.data.HireableMercenary
import com.example.quickdraw.network.data.LockedMercenary
import com.example.quickdraw.ui.theme.Typography
import com.example.quickdraw.ui.theme.availableMercenaryStatusColor
import com.example.quickdraw.ui.theme.lockedElementColor
import com.example.quickdraw.ui.theme.unavailableMercenaryStatusColor
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

interface ContractsCallbacks {
    fun onRedeemContract(activeContract: ActiveContract)
    fun onStartContract(availableContract: AvailableContract)
    fun onHireMercenary(hireable: HireableMercenary)
}


@Composable
fun ContractsScreen (controller: NavHostController, repository: GameRepository, callbacks: ContractsCallbacks) {
    //mutable states for mercenaries
    val unassigned by repository.unAssignedMercenaries.collectAsState()
    val employedAll by repository.playerEmployedMercenaries.collectAsState()
    val hireable by repository.hireableMercenaries.collectAsState()
    val unlockable by repository.nextUnlockablesMercenaries.collectAsState()

    //mutable states for contracts (selectedMercenariesState pair is id and power
    val selectedContractState = MutableStateFlow(-1)
    val selectedMercenariesState = MutableStateFlow<List<Pair<Int,Int>>>(listOf())
    val selectedContract = selectedContractState.collectAsState()
    val selectedMercenaries = selectedMercenariesState.collectAsState()

    BasicScreen("Contracts", controller, listOf(
        ContentTab("Active"){
            Column (
                modifier = Modifier.padding(it)
            ){
                var timeSeconds by remember { mutableLongStateOf(0L) }
                if(repository.activeContracts != null){
                    for(contract in repository.activeContracts!!){
                        ActiveContract(contract, timeSeconds) {
                            callbacks.onRedeemContract(contract)
                        }
                    }
                }
                LaunchedEffect(repository.activeContracts) {
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
                    if(repository.availableContracts != null){
                        for(contract in repository.availableContracts!!){
                            AvailableContract(contract){
                                selectedContractState.update { x->contract.id }
                            }
                        }
                    }
                }
                else{
                    Row(modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ){
                        Text("Select mercenaries",
                            modifier = Modifier.fillMaxWidth().padding(horizontal = 15.dp, vertical = 15.dp),
                            fontSize = Typography.titleLarge.fontSize,
                            textAlign = TextAlign.Center
                        )
                    }
                    //Display available mercenaries
                    for(merc in employedAll){
                        AssignableMercenary(merc,selectedMercenariesState)
                    }
                    val selected = repository.availableContracts.filter { x->x.id == selectedContract.value }
                    if(selected.isEmpty()){
                        selectedContractState.update { x->-1 }
                    }
                    else{
                        val currentContract = selected.first()
                        Row(modifier = Modifier.fillMaxWidth().padding(vertical = 10.dp)){
                            Button(modifier = Modifier.padding(horizontal = 10.dp),
                                onClick = {selectedContractState.update { x->-1 }
                                    selectedMercenariesState.update { x->listOf() }
                                }) {
                                Text("Cancel")
                            }
                            Spacer(modifier = Modifier.weight(0.5f))
                            Button(modifier = Modifier.padding(horizontal = 10.dp),
                                onClick = {}) {
                                Text("Start contract")
                            }
                        }
                        Column(modifier = Modifier.fillMaxWidth().padding(vertical = 5.dp),
                            horizontalAlignment = Alignment.CenterHorizontally) {
                            var successRate = 100.0
                            var tooMany = selectedMercenaries.value.size>currentContract.maxMercenaries
                            if(currentContract.requiredPower>0){
                                successRate = kotlin.math.round((selectedMercenaries.value.sumOf { x-> x.second }.toDouble() / (currentContract.requiredPower).toDouble()) * 100)
                            }
                            Text("Start cost:${currentContract.startCost}", fontSize = Typography.bodyLarge.fontSize )
                            Text("Completion time:${currentContract.requiredTime}", fontSize = Typography.bodyLarge.fontSize)
                            Text("Chance of success:${successRate}%", fontSize = Typography.bodyLarge.fontSize)
                            Text("Selected :${selectedMercenaries.value.size}/${currentContract.maxMercenaries} mercenaries"
                                , fontSize = Typography.bodyLarge.fontSize)
                        }

                    }

                }
            }
        },
        ContentTab("Mercenary"){

            Column(
                modifier = Modifier.padding(it)
            ){
                if(employedAll != null){
                    for(playerMercenary in employedAll!!){
                        EmployedMercenaryPost(playerMercenary,
                            (unassigned.any{x->x.idEmployment == playerMercenary.idEmployment})
                        )
                    }
                }
            }
        },
        ContentTab("Employ"){
            Column (
                modifier = Modifier.padding(it)
            ){
                if(hireable != null){
                    for(hireableMercenary in hireable!!){
                        HireableMercenaryPost(hireableMercenary){
                            callbacks.onHireMercenary(hireableMercenary)
                        }
                    }
                }

                if(unlockable != null){
                    for(lockedMercenary in unlockable!!){
                        LockedMercenaryPost(lockedMercenary)
                    }
                }
            }
        }
    ))
}

@Composable
fun ActiveContract(contract: ActiveContract, timeSeconds: Long, onRedeemClick: ()->Unit){
    Row (
        modifier = Modifier.fillMaxWidth().padding(8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ){
        val elapsed = timeSeconds - contract.startTime
        val timeRemaining = contract.requiredTime - elapsed
        Column {
            Text("${contract.activeId} ${contract.name}", fontSize = Typography.titleLarge.fontSize)
            if(timeRemaining > 0){
                Text("Time remaining: ${timeRemaining}s")
            } else {
                Text("Contract finished")
            }
        }
        if(timeRemaining > 0){
            CircularProgressIndicator(
                progress = { (elapsed).toFloat() / contract.requiredTime },
                color = MaterialTheme.colorScheme.primary
            )
        } else {
            Button(onClick = onRedeemClick) {
                Text("Redeem")
            }
        }
    }
}

@Composable
fun AvailableContract(contract: AvailableContract, onStartButton: ()->Unit){
    Row (
        modifier = Modifier.fillMaxWidth().padding(8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ){
        Column {
            Text(contract.name, fontSize = Typography.titleLarge.fontSize)
            Text("Required time: ${contract.requiredTime}s")
            Text("Max mercenaries allowed: ${contract.maxMercenaries}")
            Text("Start cost: ${contract.startCost}")
        }
        Button(
            onClick = onStartButton,
            modifier = Modifier.width(72.dp).height(72.dp)
        ) {
            Icon(
                imageVector = ImageVector.vectorResource(R.drawable.play_arrow_24px),
                "",
                tint = Color.Black,
            )
        }
    }
}

@Composable
fun HireableMercenaryPost(mercenary: HireableMercenary, onHireClick: ()->Unit){
    Row (
        modifier = Modifier.fillMaxWidth().padding(12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ){
        Column {
            Text(mercenary.name, fontSize = Typography.titleLarge.fontSize)
            Text("Cost: ${mercenary.cost}")
            Text("Power: ${mercenary.power}")
        }
        Button(
            onClick = onHireClick,
            modifier = Modifier.width(72.dp).height(72.dp)
        ) {
            Icon(
                imageVector = ImageVector.vectorResource(R.drawable.money_bag_24px_1_),
                "",
                tint = Color.Black,
            )
        }
    }
}

@Composable
fun LockedMercenaryPost(mercenary: LockedMercenary){
    Row (
        modifier = Modifier.fillMaxWidth().background(lockedElementColor).padding(8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,

    ){
        Column {
            Text(mercenary.name, fontSize = Typography.titleLarge.fontSize)
            Text("Power: ${mercenary.power}")
            Text("Unlock at level ${mercenary.levelRequired}")
        }
    }
}

@Composable
fun EmployedMercenaryPost(mercenary: EmployedMercenary,available: Boolean = true){
    Row (
        modifier = Modifier.fillMaxWidth().padding(8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ){
        Column {
            Text(mercenary.name, fontSize = Typography.titleLarge.fontSize)
            Text("Power: ${mercenary.power}")
        }

        if(available){
            Text("Available for contract", textAlign = TextAlign.Center,
                modifier = Modifier.width(110.dp).height(60.dp)
                    .background(availableMercenaryStatusColor,shape = RectangleShape))
        }
        else{
            Text("Already assigned", textAlign = TextAlign.Center,
                modifier = Modifier.width(110.dp).height(60.dp)
                    .background(unavailableMercenaryStatusColor,shape = RectangleShape))
        }


    }
}

@Composable
fun AssignableMercenary(mercenary: EmployedMercenary,stateArray: MutableStateFlow<List<Pair<Int,Int>>>){
    var checked by remember { mutableStateOf(false) }
    Row (
        modifier = Modifier.fillMaxWidth().padding(8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ){
        Column {
            Text(mercenary.name, fontSize = Typography.titleLarge.fontSize)
            Text("Power: ${mercenary.power}")
        }
        Checkbox(onCheckedChange =
            {
                checked = it
                if(checked){
                    stateArray.update { x->x+Pair<Int,Int>(mercenary.idEmployment,mercenary.power) }
                }
                else{
                    stateArray.update { x->x.filter { y->y.first!=mercenary.idEmployment } }
                }
            }
            , checked = checked
        )
    }
}