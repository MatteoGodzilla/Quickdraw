package com.example.quickdraw.game.screen

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
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
import com.example.quickdraw.game.components.AssignableMercenary
import com.example.quickdraw.game.components.RowDivider
import com.example.quickdraw.game.repo.GameRepository
import com.example.quickdraw.game.vm.ContractStartVM
import com.example.quickdraw.network.data.AvailableContract
import com.example.quickdraw.ui.theme.QuickdrawTheme
import com.example.quickdraw.ui.theme.Typography
import kotlinx.coroutines.selects.select


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StartContractScreen(
    vm: ContractStartVM,
    controller: NavHostController,
){
    val unassigned = vm.availableMercenaries.collectAsState().value
    val selectedMercs = vm.selectedMercenaries.collectAsState().value
    val tooLittle = selectedMercs.isEmpty()
    val tooMany = selectedMercs.size > vm.contract.maxMercenaries

    //go back both for arrow on top left and phone button
    val goBack:()->Unit = {
        controller.navigate(GameNavigation.Contracts)
    }
    BackHandler {
        goBack()
    }

    QuickdrawTheme {
        Scaffold (
            topBar = {
                CenterAlignedTopAppBar(
                    title = { Text("Start Contract") },
                    modifier = Modifier.padding(0.dp),
                    navigationIcon = {
                        IconButton(onClick = { goBack()  }) {
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
                        Button(
                            enabled = !tooMany && !tooLittle,
                            modifier = Modifier.padding(horizontal = 10.dp),
                            onClick = vm::startContract) {
                            Text("Start contract (costs ${vm.contract.startCost})", textAlign = TextAlign.Center,
                                modifier= Modifier.fillMaxWidth())
                        }
                    }
                }
            }
        ) { padding ->
            Column(
                modifier = Modifier.padding(padding).verticalScroll(rememberScrollState())
            ){
                Column(modifier = Modifier.fillMaxWidth().padding(top=0.dp, bottom = 5.dp).background(color = MaterialTheme.colorScheme.surfaceContainer),
                    horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Completion time:${vm.contract.requiredTime}", fontSize = Typography.bodyLarge.fontSize)
                    Text("Chance of success:${vm.successChance()}%", fontSize = Typography.bodyLarge.fontSize)
                    Text("Selected :${selectedMercs.size}/${vm.contract.maxMercenaries} mercenaries"
                        , fontSize = Typography.bodyLarge.fontSize, color = if(tooMany) Color.Red else Color.Black)
                }

                //display mercenaries
                Spacer(modifier= Modifier.height(24.dp))
                RowDivider()
                for(m in unassigned){
                    val checkBoxSelectable = selectedMercs.contains(m) || selectedMercs.size < vm.contract.maxMercenaries
                    AssignableMercenary(m,vm,checkBoxSelectable)
                }
            }
        }
    }
}