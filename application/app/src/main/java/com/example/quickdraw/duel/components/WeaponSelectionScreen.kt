package com.example.quickdraw.duel.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.quickdraw.duel.DuelGameLogic
import com.example.quickdraw.duel.Peer
import com.example.quickdraw.duel.vms.WeaponSelectionViewModel
import com.example.quickdraw.game.components.RowDivider
import com.example.quickdraw.game.repo.GameRepository
import com.example.quickdraw.network.data.InventoryBullet
import com.example.quickdraw.network.data.InventoryWeapon
import com.example.quickdraw.ui.theme.Typography
import com.example.quickdraw.ui.theme.primaryButtonColors
import com.example.quickdraw.ui.theme.secondaryButtonColors

@Composable
fun WeaponSelectionScreen(controller: NavHostController, self: Peer, other: Peer, gameLogic: DuelGameLogic, repo: GameRepository, vm: WeaponSelectionViewModel){
    DuelContainer(self, other){
        val bullets = repo.inventory.bullets.collectAsState()
        Column(modifier = Modifier.fillMaxWidth()){
            Text("Select Weapon",modifier = Modifier.fillMaxWidth().padding(top=5.dp),
                fontSize = Typography.titleLarge.fontSize,
                textAlign = TextAlign.Center
            )
            Column(
                modifier = Modifier.padding(vertical = 10.dp).verticalScroll(rememberScrollState())
            ){
                //display weapons
                Spacer(modifier= Modifier.height(24.dp))
                RowDivider()
                for(w in repo.inventory.weapons.collectAsState().value){
                    WeaponOption(w,vm,bullets.value.firstOrNull{x->x.type==w.bulletType})
                }
            }
            Spacer(modifier= Modifier.weight(0.1f))
            Row(modifier=Modifier.fillMaxWidth()){
                Button(onClick = {vm.selectMostDamage()}, colors = secondaryButtonColors, modifier = Modifier.weight(1f)) {
                    Text("Most Damage")
                }
                Button(onClick = {vm.selectMostBullets()}, colors = secondaryButtonColors, modifier = Modifier.weight(1f)) {
                    Text("Most Bullets")
                }
            }
            Button(onClick = {
                //controller.navigate(DuelNavigation.Play)
                gameLogic.setReady( vm.selectedWeapon.value)
            }, modifier = Modifier.fillMaxWidth(),
                colors = primaryButtonColors
            ) {
                Text("Start!")
            }
        }
    }
}

@Composable
fun WeaponOption(weapon: InventoryWeapon, vm: WeaponSelectionViewModel, bullet: InventoryBullet? = null){
    Row (
        modifier = Modifier.fillMaxWidth().padding(8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ){
        Column {
            Text("${weapon.name} (${bullet?.amount ?: 0} hits)", fontSize = Typography.titleLarge.fontSize)
            Text("Damage: ${weapon.damage}")
        }
        RadioButton(enabled=true, onClick = { vm.select(weapon) }, selected = vm.selectedWeapon.collectAsState().value == weapon)
    }
    RowDivider()
}