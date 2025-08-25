package com.example.quickdraw.game.screen

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.quickdraw.R
import com.example.quickdraw.game.components.BottomNavBar
import com.example.quickdraw.game.components.TopBar
import com.example.quickdraw.game.vm.MainScreenVM
import com.example.quickdraw.ui.theme.QuickdrawTheme
import com.example.quickdraw.ui.theme.Typography

interface DuelCallbacks{
    fun onScan()
    fun onDuel()
    fun onDuelBandit(id:Int)
}

@Composable
fun MainScreen(viewModel: MainScreenVM, controller: NavHostController,callbacks: DuelCallbacks){
    val ok = viewModel.checkValidScan()
    val showPermissionDialog = remember { mutableStateOf(false) }
    val bandits = viewModel.bandits.collectAsState()

    QuickdrawTheme {
        //rotation if scouting
        val infiniteTransition = rememberInfiniteTransition()
        val rotation by infiniteTransition.animateFloat(
            initialValue = 0f,
            targetValue = 360f,
            animationSpec = infiniteRepeatable(
                animation = tween(durationMillis = 1000, easing = LinearEasing)
            )
        )
        Scaffold(
            topBar = { TopBar(viewModel.player.collectAsState().value, viewModel.stats.collectAsState().value, viewModel.levelProgress()) },
            bottomBar = { BottomNavBar(controller) },
            modifier = Modifier.padding(
                top = WindowInsets.statusBars.asPaddingValues().calculateTopPadding(),
            )
        ) { padding ->
            Column (
                modifier = Modifier.fillMaxSize().padding(padding).verticalScroll(rememberScrollState())
            ){
                Row (modifier = Modifier.fillMaxWidth()) {
                    //manual match
                    Button(
                        onClick = viewModel::goToManualMatch,
                        colors = ButtonColors(
                            containerColor = MaterialTheme.colorScheme.secondary,
                            contentColor = MaterialTheme.colorScheme.onSurface,
                            disabledContainerColor = MaterialTheme.colorScheme.primary,
                            disabledContentColor = MaterialTheme.colorScheme.onSurface
                        ),
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(imageVector = ImageVector.vectorResource(R.drawable.radar_24px),"Scout")
                        Text("Manual match")
                    }
                }
                //Test match
                for (p in viewModel.peers.collectAsState().value) {
                    Row (
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ){
                        Text("${p.username} (Level: ${p.level})")
                        Button( onClick = { viewModel.startMatchWithPeer(p) }, enabled = true) {
                            Text("Duel")
                        }
                    }
                }

                for(entry in bandits.value){
                    Row (
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ){
                        Text("${entry.value.name} (Hp: ${entry.value.name})")
                        Button( onClick = {callbacks.onDuelBandit(entry.key)}, enabled = true,
                            colors = ButtonColors(
                                containerColor = MaterialTheme.colorScheme.secondary,
                                contentColor = MaterialTheme.colorScheme.onSurface,
                                disabledContainerColor = MaterialTheme.colorScheme.primary,
                                disabledContentColor = MaterialTheme.colorScheme.onSurface
                            )) {
                            Text("Duel")
                        }
                    }
                }
            }
            Column (
                verticalArrangement = Arrangement.Bottom,
                modifier = Modifier.fillMaxSize().padding(padding)
            ){
                //Permissions not expanded
                Row (
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth().clickable { viewModel.expandedChecks.value = !viewModel.expandedChecks.value }
                ){
                    if(viewModel.permFineLocation){
                        Icon(Icons.Default.Done, "GRANTED", tint = Color.Green) //TODO: expand palette
                    } else {
                        Icon(Icons.Default.Close, "NOT GRANTED", tint = Color.Red) //TODO: expand palette
                    }
                    if(viewModel.permNearbyDevices){
                        Icon(Icons.Default.Done, "GRANTED", tint = Color.Green) //TODO: expand palette
                    } else {
                        Icon(Icons.Default.Close, "NOT GRANTED", tint = Color.Red) //TODO: expand palette
                    }
                    if(viewModel.wifiP2PActive){
                        Icon(Icons.Default.Done, "WIFI ACTIVE", tint = Color.Green) //TODO: expand palette
                    } else {
                        Icon(Icons.Default.Close, "WIFI NOT ACTIVE", tint = Color.Red) //TODO: expand palette
                    }
                    if(viewModel.wifiActive.collectAsState().value){
                        Icon(Icons.Default.Done, "WIFI ACTIVE", tint = Color.Green) //TODO: expand palette
                    } else {
                        Icon(Icons.Default.Close, "WIFI NOT ACTIVE", tint = Color.Red) //TODO: expand palette
                    }
                    if(viewModel.gpsActive.collectAsState().value){
                        Icon(Icons.Default.Done, "GPS ACTIVE", tint = Color.Green) //TODO: expand palette
                    } else {
                        Icon(Icons.Default.Close, "GPS NOT ACTIVE", tint = Color.Red) //TODO: expand palette
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
                            Icon(Icons.Default.Done, "GRANTED", tint = Color.Green) //TODO: expand palette
                        } else {
                            Icon(Icons.Default.Close, "NOT GRANTED", tint = Color.Red) //TODO: expand palette
                        }
                    }
                    Row (
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth().padding(8.dp).clickable { showPermissionDialog.value = true }
                    ) {
                        Text("Nearby devices permission (i)")
                        if(viewModel.permNearbyDevices){
                            Icon(Icons.Default.Done, "GRANTED", tint = Color.Green) //TODO: expand palette
                        } else {
                            Icon(Icons.Default.Close, "NOT GRANTED", tint = Color.Red) //TODO: expand palette
                        }
                    }
                    Row (
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth().padding(8.dp).clickable { viewModel.onWifiP2PSettings() }
                    ) {
                        Text("Wifi Direct supported?")
                        if(viewModel.wifiP2PActive){
                            Icon(Icons.Default.Done, "WIFI ACTIVE", tint = Color.Green) //TODO: expand palette
                        } else {
                            Icon(Icons.Default.Close, "WIFI NOT ACTIVE", tint = Color.Red) //TODO: expand palette
                        }
                    }
                    Row (
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth().padding(8.dp).clickable { viewModel.onWifiSettings() }
                    ) {
                        Text("Wifi active?")
                        if(viewModel.wifiActive.collectAsState().value){
                            Icon(Icons.Default.Done, "WIFI ACTIVE", tint = Color.Green) //TODO: expand palette
                        } else {
                            Icon(Icons.Default.Close, "WIFI NOT ACTIVE", tint = Color.Red) //TODO: expand palette
                        }
                    }
                    Row (
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth().padding(8.dp).clickable { viewModel.onLocationSettings() }
                    ) {
                        Text("Gps active?")
                        if(viewModel.gpsActive.collectAsState().value){
                            Icon(Icons.Default.Done, "GPS ACTIVE", tint = Color.Green) //TODO: expand palette
                        } else {
                            Icon(Icons.Default.Close, "GPS NOT ACTIVE", tint = Color.Red) //TODO: expand palette
                        }
                    }
                }
                Button(
                    onClick = callbacks::onScan,
                    colors = ButtonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onSurface,
                        disabledContainerColor = MaterialTheme.colorScheme.primary,
                        disabledContentColor = MaterialTheme.colorScheme.onSurface
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(imageVector = ImageVector.vectorResource(R.drawable.radar_24px),
                        "Scout",
                        modifier = Modifier.rotate( if(!viewModel.scanning.collectAsState().value) 0.0f else rotation )
                    )
                    if(viewModel.scanning.collectAsState().value){
                        Text("Stop scouting", fontSize = Typography.titleLarge.fontSize)
                    } else {
                        Text("Start scouting", fontSize = Typography.titleLarge.fontSize)
                    }
                }
            }

        }
    }
    if(showPermissionDialog.value){
        PermissionExplainationDialog{ showPermissionDialog.value = false }
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