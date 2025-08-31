package com.example.quickdraw.game.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.quickdraw.R
import com.example.quickdraw.game.components.BasicTabLayout
import com.example.quickdraw.game.components.BottomNavBar
import com.example.quickdraw.game.components.FadableAsyncImage
import com.example.quickdraw.game.components.RowDivider
import com.example.quickdraw.game.components.TopBar
import com.example.quickdraw.game.components.infiniteRotation
import com.example.quickdraw.game.vm.FightablePeer
import com.example.quickdraw.game.vm.MainScreenVM
import com.example.quickdraw.network.data.Bandit
import com.example.quickdraw.ui.theme.QuickdrawTheme
import com.example.quickdraw.ui.theme.Typography
import com.example.quickdraw.ui.theme.primaryButtonColors
import com.example.quickdraw.ui.theme.secondaryButtonColors
import kotlinx.coroutines.launch

interface DuelCallbacks{
    fun onScan()
    fun onScanBandits()
    fun onDuel()
    fun onDuelBandit(id:Int)
}

@Composable
fun MainScreen(viewModel: MainScreenVM, controller: NavHostController,callbacks: DuelCallbacks){
    QuickdrawTheme {
        Scaffold(
            topBar = { TopBar(viewModel.player.collectAsState().value, viewModel.stats.collectAsState().value, viewModel.levelProgress()) },
            bottomBar = { BottomNavBar(controller) },
            modifier = Modifier.padding(
                top = WindowInsets.statusBars.asPaddingValues().calculateTopPadding(),
            )
        ) { padding ->
            val content :List<@Composable ()->Unit> = listOf(
                {PvpSection(viewModel,callbacks)},
                {PveSection(viewModel,callbacks)}
            )
            val pagerState = rememberPagerState (initialPage = 0){ content.size }
            val scrollScope = rememberCoroutineScope()
            Column (modifier = Modifier.padding(padding)){
                HorizontalDivider(
                    modifier = Modifier.fillMaxWidth().padding(all=0.dp),
                    thickness = 2.dp,
                    color= MaterialTheme.colorScheme.background
                )
                BasicTabLayout(selectedIndex = pagerState.currentPage, listOf("Players", "Bandits")) {
                    scrollScope.launch { pagerState.animateScrollToPage(it) }
                }
                HorizontalPager(
                    state = pagerState,
                    modifier = Modifier.fillMaxSize()
                ) { page ->
                    content[page]()
                }
            }
        }
    }
}

@Composable
fun PvpSection(viewModel: MainScreenVM, callbacks: DuelCallbacks){
    val hasWeapon = viewModel.checkInventoryForWeapon()
    val hasEnoughBullets = viewModel.checkInventoryForShoot()
    viewModel.checkValidScan()
    LaunchedEffect(
        viewModel.peerFinder.rawDevices.collectAsState().value,
        viewModel.serviceFinder.rawDeviceToPeer.collectAsState().value
    ) {
        viewModel.getPeers()
    }
    val showPermissionDialog = remember { mutableStateOf(false) }
    Column (
        modifier = Modifier.fillMaxSize()
    ){
        if(viewModel.peers.collectAsState().value.isNotEmpty()){
            Column (modifier = Modifier.weight(1f).verticalScroll(rememberScrollState())){
                //Test match
                for (p in viewModel.peers.collectAsState().value) {
                    FightablePlayer(p,viewModel,hasWeapon && hasEnoughBullets)
                }
            }
        } else {
            Box(contentAlignment = Alignment.Center, modifier = Modifier.weight(1f)){
                var str = "Press Start scouting in order to find other players nearby!\n\n(Note: if you're already connected to a device using Wifi-Direct, that device will not show in the list)"
                if(!hasWeapon){
                    str = "You don't have a weapon yet!\nPick up the Colt Navy Revolver from the Shop!"
                } else if(!hasEnoughBullets){
                    str = "You don't have enough bullets for a match! Refill yourself from the Shop!"
                }
                Text(
                    str,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
        Column (
            verticalArrangement = Arrangement.Bottom,
            modifier = Modifier.fillMaxWidth()
        ){
            //Permissions not expanded
            Row (
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth().background(MaterialTheme.colorScheme.surfaceContainer)
                    .clickable { viewModel.expandedChecks.value = !viewModel.expandedChecks.value }
            ){
                if(viewModel.permFineLocation){
                    Icon(Icons.Default.Done, "GRANTED", tint = MaterialTheme.colorScheme.primary)
                } else {
                    Icon(Icons.Default.Close, "NOT GRANTED", tint = Color.Red)
                }
                if(viewModel.permNearbyDevices){
                    Icon(Icons.Default.Done, "GRANTED", tint = MaterialTheme.colorScheme.primary)
                } else {
                    Icon(Icons.Default.Close, "NOT GRANTED", tint = Color.Red)
                }
                if(viewModel.wifiP2PActive){
                    Icon(Icons.Default.Done, "WIFI P2P ACTIVE", tint = MaterialTheme.colorScheme.primary)
                } else {
                    Icon(Icons.Default.Close, "WIFI P2P NOT ACTIVE", tint = Color.Red)
                }
                if(viewModel.wifiActive.collectAsState().value){
                    Icon(Icons.Default.Done, "WIFI ACTIVE", tint = MaterialTheme.colorScheme.primary)
                } else {
                    Icon(Icons.Default.Close, "WIFI NOT ACTIVE", tint = Color.Red)
                }
                if(viewModel.gpsActive.collectAsState().value){
                    Icon(Icons.Default.Done, "GPS ACTIVE", tint = MaterialTheme.colorScheme.primary)
                } else {
                    Icon(Icons.Default.Close, "GPS NOT ACTIVE", tint = Color.Red)
                }
                Icon(if(viewModel.expandedChecks.value) Icons.Default.KeyboardArrowDown else Icons.Default.KeyboardArrowUp, "Expand")
            }

            if(viewModel.expandedChecks.value){
                //scouting
                Row (
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth().padding(8.dp).clickable { showPermissionDialog.value = true }
                ) {
                    Text("Fine location permission (i)")
                    if(viewModel.permFineLocation){
                        Icon(Icons.Default.Done, "GRANTED", tint = Color.Green)
                    } else {
                        Icon(Icons.Default.Close, "NOT GRANTED", tint = Color.Red)
                    }
                }
                Row (
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth().padding(8.dp).clickable { showPermissionDialog.value = true }
                ) {
                    Text("Nearby devices permission (i)")
                    if(viewModel.permNearbyDevices){
                        Icon(Icons.Default.Done, "GRANTED", tint = Color.Green)
                    } else {
                        Icon(Icons.Default.Close, "NOT GRANTED", tint = Color.Red)
                    }
                }
                Row (
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth().padding(8.dp).clickable { viewModel.onWifiP2PSettings() }
                ) {
                    Text("Wifi Direct supported?")
                    if(viewModel.wifiP2PActive){
                        Icon(Icons.Default.Done, "WIFI ACTIVE", tint = Color.Green)
                    } else {
                        Icon(Icons.Default.Close, "WIFI NOT ACTIVE", tint = Color.Red)
                    }
                }
                Row (
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth().padding(8.dp).clickable { viewModel.onWifiSettings() }
                ) {
                    Text("Wifi active?")
                    if(viewModel.wifiActive.collectAsState().value){
                        Icon(Icons.Default.Done, "WIFI ACTIVE", tint = Color.Green)
                    } else {
                        Icon(Icons.Default.Close, "WIFI NOT ACTIVE", tint = Color.Red)
                    }
                }
                Row (
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth().padding(8.dp).clickable { viewModel.onLocationSettings() }
                ) {
                    Text("Gps active?")
                    if(viewModel.gpsActive.collectAsState().value){
                        Icon(Icons.Default.Done, "GPS ACTIVE", tint = Color.Green)
                    } else {
                        Icon(Icons.Default.Close, "GPS NOT ACTIVE", tint = Color.Red)
                    }
                }
            }
            Button(
                onClick = {callbacks.onScan()},
                colors = primaryButtonColors,
                modifier = Modifier.fillMaxWidth().background(MaterialTheme.colorScheme.surfaceContainer),
                enabled = hasWeapon && hasEnoughBullets
            ) {
                Icon(imageVector = ImageVector.vectorResource(R.drawable.radar_24px),
                    "Scout",
                    modifier = Modifier.rotate( if(!viewModel.scanning.collectAsState().value) 0.0f else infiniteRotation())
                )
                if(viewModel.scanning.collectAsState().value){
                    Text("Stop scouting", fontSize = Typography.titleLarge.fontSize)
                } else {
                    Text("Start scouting", fontSize = Typography.titleLarge.fontSize)
                }
            }
            //manual match
            Button(
                onClick = viewModel::goToManualMatch,
                colors = secondaryButtonColors,
                modifier = Modifier.fillMaxWidth().background(color = MaterialTheme.colorScheme.surfaceContainer),
                enabled = hasWeapon && hasEnoughBullets
            ) {
                Icon(imageVector = ImageVector.vectorResource(R.drawable.radar_24px),"Scout")
                Text("Manual match")
            }
        }
    }
    if(showPermissionDialog.value){
        PermissionExplainationDialog{ showPermissionDialog.value = false }
    }
}

@Composable
fun PveSection(viewModel: MainScreenVM, callbacks: DuelCallbacks){
    val bandits = viewModel.bandits.collectAsState()
    val hasWeapon = viewModel.checkInventoryForWeapon()
    val hasEnoughBullets = viewModel.checkInventoryForShoot()
    Column (
        modifier = Modifier.fillMaxSize()
    ){
        if(bandits.value.isNotEmpty()){
            Column (modifier = Modifier.weight(1f).verticalScroll(rememberScrollState())){
                for(entry in bandits.value){
                    FightableBandit(entry.value,viewModel,hasWeapon && hasEnoughBullets,{callbacks.onDuelBandit(entry.key)})
                }
            }
        } else {
            Box(contentAlignment = Alignment.Center, modifier = Modifier.weight(1f)){
                var str = "Press Locate bandits in order to look for bandits to fight!"
                if(!hasWeapon){
                    str = "You don't have a weapon yet!\nPick up the Colt Navy Revolver from the Shop!"
                } else if(!hasEnoughBullets){
                    str = "You don't have enough bullets for a match! Refill yourself from the Shop!"
                }
                Text(
                    str,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
        Button(
            onClick = {callbacks.onScanBandits()},
            colors = primaryButtonColors,
            modifier = Modifier.fillMaxWidth().background(MaterialTheme.colorScheme.surfaceContainer),
            enabled = hasWeapon && hasEnoughBullets
        ) {
            Icon(imageVector = ImageVector.vectorResource(R.drawable.radar_24px),
                "Scout"
            )
            Text("Locate bandits", fontSize = Typography.titleLarge.fontSize)
        }
    }
}

fun powerToHint(power:Int):String{
    if(power<=20) return "Very Low"
    if(power<=30) return "Low"
    if(power<=40) return "Moderate"
    if(power<=50) return "High"
    if(power<=60) return "Very High"
    return "Extreme"
}

fun speedToHint(speed:Int):String{
    if(speed>=1000) return "Very Slow"
    if(speed>=900) return "Slow"
    if(speed>=800) return "Moderate"
    if(speed>=700) return "Fast"
    if(speed>=500) return "Very Fast"
    return "Extreme"
}

@Composable
fun FightableEntity(content:@Composable ()->Unit){
    Row (
         modifier = Modifier.fillMaxWidth().padding(horizontal = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
       horizontalArrangement = Arrangement.SpaceBetween
    ){
        content()
    }
    RowDivider()
}
@Composable
fun FightablePlayer(p: FightablePeer, viewModel: MainScreenVM, canFight:Boolean){
    if(p.playerInfo != null){
        FightableEntity {
            val playerImage = viewModel.imageLoader.getPlayerFlow(p.playerInfo.id).collectAsState().value
            FadableAsyncImage(playerImage,"player icon",Modifier.size(48.dp).clip(CircleShape), contentScale = ContentScale.FillBounds)
            Column(){
                val textModifier = Modifier.fillMaxWidth(0.7f).padding(horizontal=8.dp).align(Alignment.Start)
                Text(p.playerInfo.username,fontSize = Typography.titleLarge.fontSize,modifier = textModifier)
                Text("Level:${p.playerInfo.level}",modifier = textModifier)
                Text("Bounty:${p.playerInfo.bounty}",modifier = textModifier)

            }
            Button( onClick = { viewModel.startMatchWithPeer(p) }, enabled = canFight) {
                Text("Duel")
            }
        }
    } else {
        FightableEntity {
            Text(p.rawDevice.deviceName, fontSize = Typography.titleLarge.fontSize)
            Button( onClick = { viewModel.startMatchWithPeer(p) }, enabled = canFight) {
                Text("Duel")
            }
        }
    }
}

@Composable
fun FightableBandit(b: Bandit, viewModel: MainScreenVM, canFight:Boolean,onDuel:()->Unit){
    FightableEntity {
        val banditImage = viewModel.imageLoader.getBanditFlow(b.id).collectAsState().value
        FadableAsyncImage(banditImage,"bandit icon",Modifier.size(48.dp).clip(CircleShape), contentScale = ContentScale.FillBounds)
        Column(){
            val textModifier = Modifier.fillMaxWidth(0.7f).padding(horizontal=8.dp).align(Alignment.Start)
            Text(b.name,fontSize = Typography.titleLarge.fontSize,modifier = textModifier)
            Text("Speed:${powerToHint(b.maxDamage)}",modifier = textModifier)
            Text("Strength:${speedToHint(b.minSpeed)}",modifier = textModifier)
        }
        Button( onClick = { onDuel() }, enabled = canFight,colors = secondaryButtonColors) {
            Text("Duel")
        }
    }
}

@Composable
fun PermissionExplainationDialog(onclick: () -> Unit){
    val explaination = StringBuilder()
    explaination.appendLine("In order to scout other players, Quickdraw uses Wifi Direct to scan for nearby devices.")
    explaination.appendLine("Since this technology can in theory be used to get the precise location, the Android team has decided to also require location permission to the user.")
    explaination.appendLine()
    explaination.appendLine("NO LOCATION DATA IS EVER SENT TO THE SERVER, it is required only to scan for nearby devices.")

    AlertDialog(
        onDismissRequest = onclick,
        confirmButton = { Button(onClick = onclick) { Text("Got it")}},
        title = { Text("Why does Quickdraw need my location?")},
        text = { Text(explaination.toString())},
    )
}