package com.example.quickdraw.game.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsPadding
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.quickdraw.game.GameNavigation
import com.example.quickdraw.game.components.QrCodeImage
import com.example.quickdraw.game.components.QrScanner
import com.example.quickdraw.game.repo.GameRepository
import com.example.quickdraw.ui.theme.QuickdrawTheme
import com.example.quickdraw.ui.theme.Typography
import qrscanner.CameraLens
import qrscanner.OverlayShape
import qrscanner.QrCodeScanner

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ManualConnectionScreen(controller: NavHostController, repository: GameRepository,ip:String,onScan:()->Unit){
    //TODO: use a viewmodel
    var scanOn by remember { mutableStateOf(false) }
    var text by remember { mutableStateOf("none") }
    QuickdrawTheme {
        Scaffold (
            topBar = {
                CenterAlignedTopAppBar(
                    title = { Text("Manual match") },
                    modifier = Modifier.padding(0.dp),
                    navigationIcon = {
                        IconButton(onClick = { controller.navigate(GameNavigation.Map) }) {
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
                        Button({
                            scanOn = true
                        }, modifier = Modifier.fillMaxWidth()) {
                            Text("Scan your friends code",
                                modifier = Modifier.fillMaxWidth(),
                                fontSize = Typography.bodyLarge.fontSize,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }
        ) { padding ->
            Column(
                modifier = Modifier.padding(padding).fillMaxSize()
            ){
                Row(modifier = Modifier.fillMaxWidth().background(MaterialTheme.colorScheme.surfaceContainer).padding(10.dp)){
                    Text("Your IP Address", modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center)
                }
                Spacer(modifier = Modifier.fillMaxHeight(0.1f))
                if(scanOn){
                    QrScanner({res->
                        text = res
                        scanOn = false
                    })
                }
                else{
                    QrCodeImage(ip, modifier = Modifier.align(Alignment.CenterHorizontally) )
                    Text("If your device isn't finding your friends device,you can make them scan your qr code and start a game manually!", textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth().padding(15.dp))
                    Text("Scanned server:$text",color=Color.Red)
                }

            }
        }
    }
}