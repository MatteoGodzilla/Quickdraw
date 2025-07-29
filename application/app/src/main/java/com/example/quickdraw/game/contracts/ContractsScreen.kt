package com.example.quickdraw.game.contracts

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import com.example.quickdraw.game.components.BasicScreen
import com.example.quickdraw.game.components.ContentTab
import com.example.quickdraw.game.repo.GameRepository
import com.example.quickdraw.ui.theme.Typography

@Composable
fun ContractsScreen (controller: NavHostController, repository: GameRepository) {
    BasicScreen("Contracts", controller, listOf(
        ContentTab("Active"){
            Column (
                modifier = Modifier.padding(it)
            ){
                if(repository.activeContracts != null){
                    for(contract in repository.activeContracts!!){
                        Text(contract.toString())
                        //Text(contract.name, fontSize = Typography.titleLarge.fontSize)
                        //Text("Required time: ${contract.requiredTime}")
                        //Text("Max mercenaries allowed: ${contract.requiredTime}")
                        //Text("Start cost: ${contract.startCost}")
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
                        Text(contract.name, fontSize = Typography.titleLarge.fontSize)
                        Text("Required time: ${contract.requiredTime}")
                        Text("Max mercenaries allowed: ${contract.requiredTime}")
                        Text("Start cost: ${contract.startCost}")
                    }
                }
            }
        },
        ContentTab("Mercenaries"){}
    ))
}