package com.example.quickdraw.game.screen

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.navigation.NavHostController
import com.example.quickdraw.R
import com.example.quickdraw.game.GameNavigation
import com.example.quickdraw.game.components.BottomNavBar
import com.example.quickdraw.game.components.TopBar
import com.example.quickdraw.game.vm.MainScreenVM
import com.example.quickdraw.ui.theme.QuickdrawTheme
import com.example.quickdraw.ui.theme.Typography

@Composable
fun MainScreen(viewModel: MainScreenVM, controller: NavHostController){
    val ok = viewModel.checkValidScan()

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
                        onClick = {controller.navigate(GameNavigation.ManualMatch)},
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
                    //connection settings
                    Button(
                        onClick = viewModel::onSettings,
                        colors = ButtonColors(
                            containerColor = MaterialTheme.colorScheme.secondary,
                            contentColor = MaterialTheme.colorScheme.onSurface,
                            disabledContainerColor = MaterialTheme.colorScheme.primary,
                            disabledContentColor = MaterialTheme.colorScheme.onSurface
                        ),
                        modifier = Modifier.fillMaxWidth().weight(1f)
                    ) {
                        Icon(imageVector = ImageVector.vectorResource(R.drawable.settings_24px),"Scout")
                        Text("Open settings")
                    }
                }
                for (p in viewModel.peers.collectAsState().value) {
                    Row (
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ){
                        Text("${p.username} (Level: ${p.level})")
                        Button(onClick = { viewModel.startMatchWithPeer(p) }, enabled = ok) {
                            Text("Duel")
                        }
                    }
                }
            }
            Column (
                verticalArrangement = Arrangement.Bottom,
                modifier = Modifier.fillMaxSize().padding(padding)
            ){
                //scouting
                Row (
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth().padding(8.dp)
                ) {
                    Text("Fine location permission")
                    if(viewModel.permFineLocation){
                        Icon(Icons.Default.Done, "GRANTED", tint = Color.Green) //TODO: expand palette
                    } else {
                        Icon(Icons.Default.Close, "NOT GRANTED", tint = Color.Red) //TODO: expand palette
                    }
                }
                Row (
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth().padding(8.dp)
                ) {
                    Text("Nearby devices permission")
                    if(viewModel.permNearbyDevices){
                        Icon(Icons.Default.Done, "GRANTED", tint = Color.Green) //TODO: expand palette
                    } else {
                        Icon(Icons.Default.Close, "NOT GRANTED", tint = Color.Red) //TODO: expand palette
                    }
                }
                Row (
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth().padding(8.dp)
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
                    modifier = Modifier.fillMaxWidth().padding(8.dp)
                ) {
                    Text("Gps active?")
                    if(viewModel.gpsActive.collectAsState().value){
                        Icon(Icons.Default.Done, "GPS ACTIVE", tint = Color.Green) //TODO: expand palette
                    } else {
                        Icon(Icons.Default.Close, "GPS NOT ACTIVE", tint = Color.Red) //TODO: expand palette
                    }
                }
                Button(
                    onClick = viewModel::onScan,
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
}