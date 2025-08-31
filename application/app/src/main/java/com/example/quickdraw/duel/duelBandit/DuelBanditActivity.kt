package com.example.quickdraw.duel.duelBandit

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.quickdraw.Game2Bandit
import com.example.quickdraw.QuickdrawApplication
import com.example.quickdraw.dataStore
import com.example.quickdraw.duel.DuelNavigation
import com.example.quickdraw.duel.Peer
import com.example.quickdraw.duel.components.PlayScreen
import com.example.quickdraw.duel.components.PresentationScreen
import com.example.quickdraw.duel.components.ResultsScreen
import com.example.quickdraw.duel.components.WeaponSelectionScreen
import com.example.quickdraw.duel.vms.WeaponSelectionViewModel
import kotlinx.coroutines.launch

class DuelBanditActivity: ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val id = intent.getIntExtra(Game2Bandit.BANDIT_ID, -1)
        val qdapp = application as QuickdrawApplication
        val gameRepo = qdapp.repository
        val duelState = DuelBanditLogic(id,gameRepo.bandits.bandits.value[id]!!, gameRepo,this)

        val player = gameRepo.player.player.value
        val stats = gameRepo.player.stats.value
        val playerAsPeer = Peer(player.id, player.username, player.level, player.health, stats.maxHealth,player.bounty)

        setContent {
            val banditAsPeer = Peer(duelState.banditInfo.id, duelState.banditInfo.name, 0, duelState.botHP.collectAsState().value, duelState.banditInfo.hp, 0)
            val controller = rememberNavController()
            NavHost(navController = controller, startDestination = DuelNavigation.Presentation){
                composable<DuelNavigation.Presentation>{
                    PresentationScreen(controller,playerAsPeer, banditAsPeer)
                }
                composable<DuelNavigation.WeaponSelect>{
                    val vm = viewModel {
                        WeaponSelectionViewModel(qdapp.repository.inventory.weapons.value, qdapp.repository.inventory.bullets.value, qdapp.imageLoader)
                    }
                    LaunchedEffect(true) {
                        lifecycleScope.launch {
                            duelState.setFavourite(this@DuelBanditActivity.dataStore,vm)
                        }
                    }
                    WeaponSelectionScreen(playerAsPeer, banditAsPeer,duelState, gameRepo, vm, controller)
                }
                composable<DuelNavigation.Play>{
                    PlayScreen(controller, duelState)
                }
                composable<DuelNavigation.Results>{
                    ResultsScreen(controller, playerAsPeer, banditAsPeer, duelState, gameRepo)
                }
            }
        }
    }
}