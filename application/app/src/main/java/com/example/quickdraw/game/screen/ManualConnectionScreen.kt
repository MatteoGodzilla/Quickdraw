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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.quickdraw.game.GameNavigation
import com.example.quickdraw.game.components.QrCodeImage
import com.example.quickdraw.game.repo.GameRepository
import com.example.quickdraw.ui.theme.QuickdrawTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ManualConnectionScreen(controller: NavHostController, repository: GameRepository,ip:String){
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
                    }
                }
            }
        ) { padding ->
            Column(
                modifier = Modifier.padding(padding).fillMaxSize()
            ){
                Spacer(modifier = Modifier.fillMaxHeight(0.1f))
                QrCodeImage(ip, modifier = Modifier.align(Alignment.CenterHorizontally) )
                Text("Your server IP", textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())
            }
        }
    }
}