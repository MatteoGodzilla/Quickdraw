package com.example.quickdraw.duel.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.quickdraw.duel.DuelGameLogic
import com.example.quickdraw.duel.DuelNavigation
import com.example.quickdraw.duel.Peer
import com.example.quickdraw.duel.PeerState
import com.example.quickdraw.duel.duelBandit.DuelBanditLogic
import com.example.quickdraw.duel.vms.WeaponSelectionViewModel
import com.example.quickdraw.game.components.FadableAsyncImage
import com.example.quickdraw.game.components.RowDivider
import com.example.quickdraw.game.repo.GameRepository
import com.example.quickdraw.network.data.InventoryBullet
import com.example.quickdraw.network.data.InventoryWeapon
import com.example.quickdraw.ui.theme.Typography
import com.example.quickdraw.ui.theme.primaryButtonColors
import com.example.quickdraw.ui.theme.secondaryButtonColors

@Composable
fun WeaponSelectionScreen(self: Peer, other: Peer, gameLogic: DuelGameLogic, repo: GameRepository, vm: WeaponSelectionViewModel){
    val waitingForOpponent = gameLogic.selfState.collectAsState().value == PeerState.READY && gameLogic.otherState.collectAsState().value == PeerState.CAN_PLAY
    val canDoRound = canSelfDoRound(repo)
    WeaponSelectionUI(self, other, waitingForOpponent, canDoRound, repo, vm,
        onSetWeapon = {
            gameLogic.setReady( vm.selectedWeapon.value)
        }, onForfeit = {
            gameLogic.goodbye()
        }
    )
}

@Composable
fun WeaponSelectionScreen(self: Peer, other: Peer, duelLogic: DuelBanditLogic, repo: GameRepository, vm: WeaponSelectionViewModel, controller: NavHostController){
    val canDoRound = canSelfDoRound(repo)
    WeaponSelectionUI(self, other, false, canDoRound, repo, vm,
        onSetWeapon = {
            controller.navigate(DuelNavigation.Play)
            duelLogic.setWeaponAndStart( vm.selectedWeapon.value)
        },
        onForfeit = {}
    )
}

@Composable
fun WeaponSelectionUI(self: Peer, other: Peer, waitingForOpponent: Boolean, canDoRound: Boolean, repo: GameRepository, vm: WeaponSelectionViewModel, onSetWeapon: ()->Unit, onForfeit: () -> Unit){
    DuelContainer(self, other){
        if(waitingForOpponent){
            Box(modifier=Modifier.padding(5.dp).fillMaxSize()){
                Column(modifier = Modifier.fillMaxWidth().align(alignment = Alignment.Center)) {
                    LoadMessage("Waiting for opponent...",Modifier.align(alignment = Alignment.CenterHorizontally))
                }
            }
        }
        else{
            val bullets = repo.inventory.bullets.collectAsState()
            Column(modifier = Modifier.fillMaxWidth()){
                if(canDoRound){
                    Text("Select Weapon",modifier = Modifier.fillMaxWidth().padding(top=5.dp),
                        fontSize = Typography.titleLarge.fontSize,
                        textAlign = TextAlign.Center
                    )
                    Column(
                        modifier = Modifier.weight(1f).verticalScroll(rememberScrollState())
                    ){
                        RowDivider()
                        for(w in repo.inventory.weapons.collectAsState().value){
                            WeaponOption(w,vm,bullets.value.first{x->x.type==w.bulletType}, vm.getWeaponImage(w.id).collectAsState().value)
                        }
                    }
                    Row(modifier=Modifier.fillMaxWidth()){
                        Button(onClick = {vm.selectMostDamage()}, colors = secondaryButtonColors, modifier = Modifier.weight(1f)) {
                            Text("Most Damage")
                        }
                        Button(onClick = {vm.selectMostBullets()}, colors = secondaryButtonColors, modifier = Modifier.weight(1f)) {
                            Text("Most Bullets")
                        }
                    }
                    Button(onClick = onSetWeapon , modifier = Modifier.fillMaxWidth(),
                        colors = primaryButtonColors
                    ) {
                        Text("Start!")
                    }
                } else {
                    Text("No usable weapon found because of missing bullets",modifier = Modifier.fillMaxWidth().padding(top=5.dp),
                        fontSize = Typography.titleLarge.fontSize,
                        textAlign = TextAlign.Center
                    )
                    Button(onClick = onForfeit, modifier = Modifier.fillMaxWidth(),
                        colors = primaryButtonColors
                    ) {
                        Text("Forfeit")
                    }
                }
            }
        }
    }
}

@Composable
fun WeaponOption(weapon: InventoryWeapon, vm: WeaponSelectionViewModel, bullet: InventoryBullet, icon: ByteArray){
    val usable = bullet.amount >= weapon.bulletsShot
    Column (modifier = Modifier.fillMaxWidth().padding(8.dp).clickable { vm.select(weapon) }){
        Row (modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically){
            FadableAsyncImage(icon, weapon.name, modifier = Modifier.size(48.dp))
            Text(weapon.name, fontSize = Typography.titleLarge.fontSize, modifier = Modifier.weight(1f), textAlign = TextAlign.Center)
            RadioButton(enabled=usable, onClick = { vm.select(weapon) }, selected = vm.selectedWeapon.collectAsState().value == weapon)
        }
        Box (modifier = Modifier.fillMaxWidth()){
            Text("Damage: ${weapon.damage}", modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Left)
            Text("${bullet.amount} bullets", modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center)
            Text("Bullets shot: ${weapon.bulletsShot}", modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Right)
        }
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