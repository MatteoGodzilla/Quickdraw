package com.example.quickdraw.duel.duelBandit

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.quickdraw.Game2Bandit
import com.example.quickdraw.QuickdrawApplication
import com.example.quickdraw.dataStore
import com.example.quickdraw.duel.DuelNavigation
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

        setContent {
            val controller = rememberNavController()
            NavHost(navController = controller, startDestination = DuelNavigation.Presentation){
                composable<DuelNavigation.Presentation>{
                    PresentationScreen(controller,duelState, gameRepo.player)
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
                    WeaponSelectionScreen(controller,duelState, gameRepo, vm)
                }
                composable<DuelNavigation.Play>{
                    PlayScreen(controller, duelState)
                }
                composable<DuelNavigation.Results>{
                    ResultsScreen(controller, duelState, gameRepo)
                }
            }
        }
    }
}