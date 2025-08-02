package com.example.quickdraw.game.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.quickdraw.R
import com.example.quickdraw.game.components.BasicScreen
import com.example.quickdraw.game.components.ContentTab
import com.example.quickdraw.network.data.ActiveContract
import com.example.quickdraw.network.data.AvailableContract
import com.example.quickdraw.game.GameRepository
import com.example.quickdraw.ui.theme.Typography
import kotlinx.coroutines.delay

interface ContractsCallbacks {
    fun onRedeemContract(activeContract: ActiveContract)
    fun onStartContract(availableContract: AvailableContract)
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
        ContentTab("Mercenaries"){}
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