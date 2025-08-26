package com.example.quickdraw.duel.components

import androidx.compose.foundation.background
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.quickdraw.duel.DuelGameLogic
import com.example.quickdraw.duel.DuelNavigation
import com.example.quickdraw.duel.Peer
import com.example.quickdraw.duel.duelBandit.DuelBanditLogic
import com.example.quickdraw.duel.vms.WeaponSelectionViewModel
import com.example.quickdraw.game.components.RowDivider
import com.example.quickdraw.game.repo.GameRepository
import com.example.quickdraw.game.screen.StatsDisplayer
import com.example.quickdraw.network.data.InventoryBullet
import com.example.quickdraw.network.data.InventoryWeapon
import com.example.quickdraw.ui.theme.Typography
import com.example.quickdraw.ui.theme.primaryButtonColors
import com.example.quickdraw.ui.theme.secondaryButtonColors

@Composable
fun WeaponSelectionScreen(controller: NavHostController, self: Peer, other: Peer, gameLogic: DuelGameLogic, repo: GameRepository, vm: WeaponSelectionViewModel){
    val canDoRound = canSelfDoRound(repo)
    DuelContainer(self, other){
        val bullets = repo.inventory.bullets.collectAsState()
        Column(modifier = Modifier.fillMaxWidth()){
            if(canDoRound){
                Text("Select Weapon",modifier = Modifier.fillMaxWidth().padding(top=5.dp),
                    fontSize = Typography.titleLarge.fontSize,
                    textAlign = TextAlign.Center
                )
                Column(
                    modifier = Modifier.verticalScroll(rememberScrollState())
                ){
                    RowDivider()
                    for(w in repo.inventory.weapons.collectAsState().value){
                        WeaponOption(w,vm,bullets.value.first{x->x.type==w.bulletType})
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
                    gameLogic.setReady( vm.selectedWeapon.value)
                }, modifier = Modifier.fillMaxWidth(),
                    colors = primaryButtonColors
                ) {
                    Text("Start!")
                }
            } else {
                Text("No usable weapon found because of missing bullets",modifier = Modifier.fillMaxWidth().padding(top=5.dp),
                    fontSize = Typography.titleLarge.fontSize,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier= Modifier.weight(0.1f))
                Button(onClick = {
                    gameLogic.goodbye()
                }, modifier = Modifier.fillMaxWidth(),
                    colors = primaryButtonColors
                ) {
                    Text("Forfeit")
                }
            }
        }
    }
}

@Composable
fun WeaponSelectionScreen(controller: NavHostController, duelLogic: DuelBanditLogic, repo: GameRepository, vm: WeaponSelectionViewModel){
    DuelContainer(duelLogic,repo.player ){
        val bullets = repo.inventory.bullets.collectAsState()
        Column(modifier = Modifier.fillMaxWidth()){
            Text("Select Weapon",modifier = Modifier.fillMaxWidth().padding(top=5.dp),
                fontSize = Typography.titleLarge.fontSize,
                textAlign = TextAlign.Center
            )
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState())
            ){
                RowDivider()
                for(w in repo.inventory.weapons.collectAsState().value){
                    WeaponOption(w,vm,bullets.value.first{x->x.type==w.bulletType})
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
                controller.navigate(DuelNavigation.Play)
                duelLogic.setWeaponAndStart( vm.selectedWeapon.value)
            }, modifier = Modifier.fillMaxWidth(),
                colors = primaryButtonColors
            ) {
                Text("Start!")
            }
        }
    }
}

@Composable
fun WeaponOption(weapon: InventoryWeapon, vm: WeaponSelectionViewModel, bullet: InventoryBullet){
    val usable = bullet.amount >= weapon.bulletsShot
    Row (
        modifier = Modifier.fillMaxWidth().background(if(usable) Color.Transparent else Color.Red).padding(8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ){
        Column (modifier = Modifier.weight(1f)){
            Row (modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(weapon.name, fontSize = Typography.titleLarge.fontSize)
                Text("${bullet.amount} bullets")
            }
            Row (modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween){
                Text("Damage: ${weapon.damage}")
                Text("Bullets shot: ${weapon.bulletsShot}")
            }
        }
        RadioButton(enabled=usable, onClick = { vm.select(weapon) }, selected = vm.selectedWeapon.collectAsState().value == weapon)
    }
    RowDivider()
}

fun canSelfDoRound(repository: GameRepository): Boolean{
    //player can do a round if it has at least a weapon with enough bullets to shoot
    for(w in repository.inventory.weapons.value){
        val bulletsOwned = repository.inventory.bullets.value.first { bullet ->  bullet.type == w.bulletType }
        if(bulletsOwned.amount >= w.bulletsShot){
            return true
        }
    }
    return false
}