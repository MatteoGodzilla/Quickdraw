package com.example.quickdraw.game

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.quickdraw.Game2Duel
import com.example.quickdraw.QuickdrawApplication
import com.example.quickdraw.duel.DuelActivity
import com.example.quickdraw.game.screen.ManualConnectionScreen
import com.example.quickdraw.game.vm.ManualConnectionVM

class ManualConnectionActivity: ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val qdapp = application as QuickdrawApplication

        enableEdgeToEdge()
        setContent {
            val vm = viewModel {
                ManualConnectionVM(qdapp.repository, this@ManualConnectionActivity ) { isServer, serverAddress, serverPort ->
                    goToDuel(isServer, serverAddress, serverPort)
                }
            }
            ManualConnectionScreen(vm){
                vm.closeServer()
                finish()
            }
         }
    }

    private fun goToDuel(isServer: Boolean, address: String, port:Int){
        val intent = Intent(this, DuelActivity::class.java)
        intent.putExtra(Game2Duel.IS_SERVER_KEY, isServer)
        intent.putExtra(Game2Duel.SERVER_ADDRESS_KEY, address)
        intent.putExtra(Game2Duel.SERVER_PORT_KEY, port)
        intent.putExtra(Game2Duel.USING_WIFI_P2P, false)
        startActivity(intent)
        this.finish()
    }
}