package com.example.quickdraw.duel.components

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.Checkbox
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.quickdraw.R
import com.example.quickdraw.TAG
import com.example.quickdraw.duel.DuelGameLogic
import com.example.quickdraw.duel.VMs.WeaponSelectionViewModel
import com.example.quickdraw.game.GameNavigation
import com.example.quickdraw.game.components.RowDivider
import com.example.quickdraw.game.repo.GameRepository
import com.example.quickdraw.network.data.InventoryWeapon
import com.example.quickdraw.ui.theme.QuickdrawTheme
import com.example.quickdraw.ui.theme.Typography

@Composable
fun WeaponSelectionScreen(controller: NavHostController, gameLogic: DuelGameLogic, repo: GameRepository, vm: WeaponSelectionViewModel){
    DuelContainer(controller,gameLogic,repo,{
        Column(modifier = Modifier.fillMaxWidth()){
            Text("Select Weapon",modifier = Modifier.fillMaxWidth().padding(top=5.dp),
                fontSize = Typography.titleLarge.fontSize,
                textAlign = TextAlign.Center
            )
            Column(
                modifier = Modifier.padding(vertical = 10.dp).verticalScroll(rememberScrollState())
            ){
                //display mercenaries
                Spacer(modifier= Modifier.height(24.dp))
                RowDivider()
                for(w in repo.inventory.weapons.collectAsState().value){
                    WeaponOption(w,vm)
                }
            }
            Row(modifier=Modifier.padding(horizontal = 10.dp).fillMaxWidth()){
                Button(onClick = {vm.selectMostDamage()},
                    colors = ButtonColors(
                        containerColor = MaterialTheme.colorScheme.secondary,
                        contentColor = MaterialTheme.colorScheme.onSurface,
                        disabledContainerColor = MaterialTheme.colorScheme.primary,
                        disabledContentColor = MaterialTheme.colorScheme.onSurface
                    )
                ) {
                    Text("Most Damage")
                }

                Button(onClick = {vm.selectMostDamage()},
                    colors = ButtonColors(
                        containerColor = MaterialTheme.colorScheme.secondary,
                        contentColor = MaterialTheme.colorScheme.onSurface,
                        disabledContainerColor = MaterialTheme.colorScheme.primary,
                        disabledContentColor = MaterialTheme.colorScheme.onSurface
                    )
                ) {
                    Text("Most Bullets")
                }
            }
            Button(onClick = {vm.selectMostDamage()}, modifier = Modifier.fillMaxWidth(),
                colors = ButtonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onSurface,
                    disabledContainerColor = MaterialTheme.colorScheme.primary,
                    disabledContentColor = MaterialTheme.colorScheme.onSurface
                )
            ) {
                Text("Start!")
            }
        }
    })
}

@Composable
fun WeaponOption(weapon: InventoryWeapon,vm: WeaponSelectionViewModel){
    var checked = vm.power.collectAsState().value == weapon.damage
    Row (
        modifier = Modifier.fillMaxWidth().padding(8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ){
        Column {
            Text(weapon.name, fontSize = Typography.titleLarge.fontSize)
            Text("Damage: ${weapon.damage}")
        }
        RadioButton(enabled=true, onClick =
            {
                if(checked){
                    vm.unselect()
                }
                else{
                    vm.select(weapon.id )
                }
                checked= !checked
            }
            , selected = checked
        )
    }
    RowDivider()
}