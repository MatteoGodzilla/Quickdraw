package com.example.quickdraw.game.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.example.quickdraw.R
import com.example.quickdraw.game.vm.ContractStartVM
import com.example.quickdraw.network.data.ActiveContract
import com.example.quickdraw.network.data.AvailableContract
import com.example.quickdraw.network.data.EmployedMercenary
import com.example.quickdraw.network.data.LockedMercenary
import com.example.quickdraw.ui.theme.Typography

fun secondsToString(seconds:Long):String{
    //below a minute
    if(seconds<60){
        return "${seconds}s"
    }
    val minutes = seconds/60
    val leftSeconds = seconds%60
    //below a hour
    if(minutes<3600){
        return "${minutes}m${if(leftSeconds>0) " ${leftSeconds}s" else ""}"
    }
    val hours = minutes/60
    val leftMinutes = minutes%60
    return "${hours}h${if(leftMinutes>0) " ${leftMinutes}m" else ""}${if(leftSeconds>0) " ${leftSeconds}m" else ""}"
}

@Composable
fun ActiveContractUI(contract: ActiveContract, timeSeconds: Long, onRedeemClick: ()->Unit){
    Row (
        modifier = Modifier.fillMaxWidth().padding(8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ){
        val elapsed = timeSeconds - contract.startTime
        val timeRemaining = contract.requiredTime - elapsed
        Column(modifier = Modifier.fillMaxWidth(0.7f)) {
            Text(contract.name, fontSize = Typography.titleLarge.fontSize)
            if(timeRemaining > 0){
                Text("Time remaining: ${secondsToString(timeRemaining)}")
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
fun AvailableContractUI(contract: AvailableContract, onStartButton: ()->Unit,startable:Boolean=true){
    Row (
        modifier = Modifier.fillMaxWidth().padding(8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ){
        Column(modifier = Modifier.fillMaxWidth(0.7f).padding(5.dp)) {
            Text(contract.name, fontSize = Typography.titleLarge.fontSize)
            Text("Suggested power:${contract.requiredPower}")
            Text("Required time: ${secondsToString(contract.requiredTime)}")
            Text("Up to ${contract.maxMercenaries} mercenaries")
        }
        Button(
            onClick = onStartButton,
            contentPadding = PaddingValues(start=5.dp,end=15.dp),
            modifier = Modifier.weight(0.25f).height(48.dp),
            enabled = startable
        ) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center){
                Text("${contract.startCost}$", fontSize = Typography.bodyMedium.fontSize, textAlign = TextAlign.Center)
                Icon(
                    imageVector = ImageVector.vectorResource(R.drawable.play_arrow_24px),
                    "",
                    tint = Color.Black,
                        modifier = Modifier.size(24.dp).align(Alignment.CenterEnd)
                )
            }
        }
    }
    RowDivider()
}


@Composable
fun LockedMercenaryPost(mercenary: LockedMercenary,icon: ByteArray){
    LockedContainer(){
        AsyncImage(icon, "", modifier = Modifier.size(48.dp))
        Column(modifier = Modifier.fillMaxWidth()) {
            Text(mercenary.name, fontSize = Typography.titleLarge.fontSize)
            Text("Unlock at level ${mercenary.levelRequired}")
        }
    }
    RowDivider()
}

@Composable
fun EmployedMercenaryPost(mercenary: EmployedMercenary, icon: ByteArray, available: Boolean = true){
    Row (
        modifier = Modifier.fillMaxWidth().padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ){
        FadableAsyncImage(icon, mercenary.name, Modifier.size(48.dp))
        Column (modifier = Modifier.weight(1f)) {
            Text(mercenary.name, fontSize = Typography.titleLarge.fontSize)
            Text("Power: ${mercenary.power}")
        }
        if(available){
            Text("Available", textAlign = TextAlign.Center, color = MaterialTheme.colorScheme.primary)
        }
        else{
            Text("Occupied", textAlign = TextAlign.Center, color = Color.Gray)
        }
    }
    RowDivider()
}

@Composable
fun AssignableMercenary(mercenary: EmployedMercenary,vm: ContractStartVM,isCheckable: Boolean=true){
    val assigned = vm.selectedMercenaries.collectAsState().value

    Row (
        modifier = Modifier.fillMaxWidth().padding(8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ){
        Column {
            Text(mercenary.name, fontSize = Typography.titleLarge.fontSize)
            Text("Power: ${mercenary.power}")
        }
        Checkbox(
            enabled=isCheckable,
            checked=assigned.contains(mercenary),
            onCheckedChange = {
                if(it){
                    vm.selectMercenary(mercenary)
                }
                else{
                    vm.unselectMercenary(mercenary)
                }
            }
        )
    }
    RowDivider()
}