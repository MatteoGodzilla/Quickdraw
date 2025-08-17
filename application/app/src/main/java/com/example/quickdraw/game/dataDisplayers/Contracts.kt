package com.example.quickdraw.game.dataDisplayers

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.quickdraw.R
import com.example.quickdraw.game.components.LockedContainer
import com.example.quickdraw.game.components.RowDivider
import com.example.quickdraw.game.vm.ContractStartVM
import com.example.quickdraw.network.data.ActiveContract
import com.example.quickdraw.network.data.AvailableContract
import com.example.quickdraw.network.data.EmployedMercenary
import com.example.quickdraw.network.data.LockedMercenary
import com.example.quickdraw.ui.theme.Typography
import com.example.quickdraw.ui.theme.lockedShopEntry
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlin.collections.plus

@Composable
fun ActiveContract(contract: ActiveContract, timeSeconds: Long, onRedeemClick: ()->Unit){
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
fun AvailableContract(contract: AvailableContract, onStartButton: ()->Unit,startable:Boolean=true){
    Row (
        modifier = Modifier.fillMaxWidth().padding(8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ){
        Column(modifier = Modifier.fillMaxWidth(0.7f)) {
            Text(contract.name, fontSize = Typography.titleLarge.fontSize)
            Text("Required time: ${contract.requiredTime}s")
            Text("Max mercenaries allowed: ${contract.maxMercenaries}")
            Text("Start cost: ${contract.startCost}")
        }
        Button(
            onClick = onStartButton,
            modifier = Modifier.width(72.dp).height(72.dp),
            enabled = startable
        ) {
            Icon(
                imageVector = ImageVector.vectorResource(R.drawable.play_arrow_24px),
                "",
                tint = Color.Black,
            )
        }
    }
    RowDivider()
}


@Composable
fun LockedMercenaryPost(mercenary: LockedMercenary,icon: ImageBitmap){
    LockedContainer(){
        Image(icon, "", modifier = Modifier.size(48.dp))
        Column(modifier = Modifier.fillMaxWidth()) {
            Text(mercenary.name, fontSize = Typography.titleLarge.fontSize)
            Text("Unlock at level ${mercenary.levelRequired}")
        }
    }
    RowDivider()
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
            Text("Available", textAlign = TextAlign.Center, color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.width(110.dp).height(60.dp))
        }
        else{
            Text("Occupied", textAlign = TextAlign.Center, color = Color.Gray,
                modifier = Modifier.width(110.dp).height(60.dp))
        }
    }
    RowDivider()
}

@Composable
fun AssignableMercenary(mercenary: EmployedMercenary,vm: ContractStartVM,isCheckable: Boolean=true){
    val mercs by vm.selectedMercenariesState.collectAsState()
    var checked = mercs.any{x->x.first==mercenary.idEmployment}
    Row (
        modifier = Modifier.fillMaxWidth().padding(8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ){
        Column {
            Text(mercenary.name, fontSize = Typography.titleLarge.fontSize)
            Text("Power: ${mercenary.power}")
        }
        Checkbox(enabled=isCheckable,onCheckedChange =
            {
                checked = it
                if(!checked){
                    vm.unselectMercenary(mercenary.idEmployment)
                }
                else{
                    vm.selectMercenary(mercenary)
                }
            }
            , checked = checked
        )
    }
    RowDivider()
}