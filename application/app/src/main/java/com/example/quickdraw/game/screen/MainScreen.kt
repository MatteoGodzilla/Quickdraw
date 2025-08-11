package com.example.quickdraw.game.screen

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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.currentComposer
import androidx.compose.runtime.currentCompositionLocalContext
import androidx.compose.runtime.rememberCompositionContext
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.PreviewActivity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.quickdraw.R
import com.example.quickdraw.duel.PeerFinder
import com.example.quickdraw.game.repo.GameRepository
import com.example.quickdraw.game.components.BottomNavBar
import com.example.quickdraw.game.components.TopBar
import com.example.quickdraw.ui.theme.QuickdrawTheme
import com.example.quickdraw.ui.theme.Typography

@Composable
fun MainScreen(controller: NavHostController, repository: GameRepository, peerFinder: PeerFinder, onScan: ()->Unit){
    QuickdrawTheme {
        Scaffold(
            topBar = { TopBar(repository) },
            bottomBar = { BottomNavBar(controller) },
            modifier = Modifier.padding(
                top = WindowInsets.statusBars.asPaddingValues().calculateTopPadding(),
            )
        ) { padding ->
            val peers = repository.peer.peers.collectAsState()
            Column (
                modifier = Modifier.fillMaxSize().padding(padding).verticalScroll(rememberScrollState())
            ){
                for (p in peers.value) {
                    Row (
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ){
                        Text("${p.username} (Level: ${p.level})")
                        Button(onClick = {
                            peerFinder.startMatchWithPeer(p)
                        }) {
                            Text("Duel")
                        }
                    }
                }
            }
            Row (
                verticalAlignment = Alignment.Bottom,
                modifier = Modifier.fillMaxSize().padding(padding)
            ){
                Button(
                    onClick = onScan,
                    colors = ButtonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onSurface,
                        disabledContainerColor = MaterialTheme.colorScheme.primary,
                        disabledContentColor = MaterialTheme.colorScheme.onSurface
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    Icon(imageVector = ImageVector.vectorResource(R.drawable.radar_24px),"Scout")
                    if(repository.peer.scanning.collectAsState().value){
                        Text("Stop scouting", fontSize = Typography.titleLarge.fontSize)
                    } else {
                        Text("Start scouting", fontSize = Typography.titleLarge.fontSize)
                    }
                }
            }
        }
    }
}