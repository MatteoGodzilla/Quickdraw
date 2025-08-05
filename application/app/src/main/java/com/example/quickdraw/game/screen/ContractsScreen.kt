package com.example.quickdraw.game.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
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
import com.example.quickdraw.network.data.MercenaryHireable
import com.example.quickdraw.ui.theme.Typography
import com.example.quickdraw.ui.theme.availableMercenaryStatusColor
import com.example.quickdraw.ui.theme.lockedElementColor
import com.example.quickdraw.ui.theme.unavailableMercenaryStatusColor
import kotlinx.coroutines.delay
import okio.Lock

interface ContractsCallbacks {
    fun onRedeemContract(activeContract: ActiveContract)
    fun onStartContract(availableContract: AvailableContract)
    fun onHireMercenary(hireable: HireableMercenary)
}

@Composable
fun ContractsScreen (controller: NavHostController, repository: GameRepository, callbacks: ContractsCallbacks) {
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
                if(repository.availableContracts != null){
                    for(contract in repository.availableContracts!!){
                        AvailableContract(contract){
                            callbacks.onStartContract(contract)
                        }
                    }
                }
            }
        },
        ContentTab("Mercenary"){

            Column(
                modifier = Modifier.padding(it)
            ){

                if(repository.playerEmployedMercenaries != null){

                    val unAssignedExist = repository.unAssignedMercenaries!= null

                    for(playerMercenary in repository.playerEmployedMercenaries!!){
                        EmployedMercenaryPost(playerMercenary,
                            (repository.unAssignedMercenaries.filter { x -> x.idEmployment == playerMercenary.idEmployment }).size > 0
                        )
                    }
                }
            }
        },
        ContentTab("Employ"){
            Column (
                modifier = Modifier.padding(it)
            ){
                if(repository.hireableMercenaries != null){
                    for(hireableMercenary in repository.hireableMercenaries!!){
                        HireableMercenaryPost(hireableMercenary){
                            callbacks.onHireMercenary(hireableMercenary)
                        }
                    }
                }

                if(repository.nextUnlockablesMercenaries != null){
                    for(lockedMercenary in repository.nextUnlockablesMercenaries!!){
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