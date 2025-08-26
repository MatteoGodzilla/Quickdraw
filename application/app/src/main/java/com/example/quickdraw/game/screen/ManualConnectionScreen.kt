package com.example.quickdraw.game.screen

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
import com.example.quickdraw.game.components.QrCodeImage
import com.example.quickdraw.game.components.QrScanner
import com.example.quickdraw.game.vm.ManualConnectionVM
import com.example.quickdraw.ui.theme.QuickdrawTheme
import com.example.quickdraw.ui.theme.Typography


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ManualConnectionScreen(viewModel: ManualConnectionVM, onBack: () -> Unit ){
    QuickdrawTheme {
        Scaffold (
            topBar = {
                CenterAlignedTopAppBar(
                    title = { Text("Manual match") },
                    modifier = Modifier.padding(0.dp),
                    navigationIcon = {
                        IconButton(onClick = onBack ) {
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
                        Button(onClick = viewModel::startScanning, modifier = Modifier.fillMaxWidth()) {
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
                modifier = Modifier.padding(padding).fillMaxSize().verticalScroll(rememberScrollState())
            ){
                Spacer(modifier = Modifier.fillMaxHeight(0.1f))
                if(viewModel.scanning.collectAsState().value){
                    QrScanner(viewModel::onScan)
                }
                else{
                    QrCodeImage(viewModel.getQRData(), modifier = Modifier.align(Alignment.CenterHorizontally) )
                    val explaination = StringBuilder()
                    explaination.appendLine("If your device isn't finding your friends' device, you can make them scan your qr code and start a game manually!")
                    explaination.appendLine("Note: both devices must be connected to the same wifi in order to see each other")
                    Text(explaination.toString(), textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth().padding(15.dp))
                    val errorMessage = viewModel.messageError.collectAsState().value
                    if(errorMessage.isNotEmpty()){
                        Text(errorMessage, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth().padding(15.dp),color = Color.Red)
                    }
                }
            }
        }
    }
}