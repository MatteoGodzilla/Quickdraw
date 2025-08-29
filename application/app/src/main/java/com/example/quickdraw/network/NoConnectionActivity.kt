package com.example.quickdraw.network

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.example.quickdraw.MainActivity
import com.example.quickdraw.game.ManualConnectionActivity
import com.example.quickdraw.ui.theme.QuickdrawTheme

class NoConnectionActivity: ComponentActivity()  {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val invalidVersion = intent.getBooleanExtra("invalidVersion", false)

        enableEdgeToEdge()
        setContent {
            QuickdrawTheme {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxSize().background(color= MaterialTheme.colorScheme.background)
                ){
                    Text("Could not connect to server")
                    if(invalidVersion) {
                        Text("Version mismatch between client and server")
                    } else {
                        Text("Server not found")
                    }
                    Button(onClick = {
                        val intent = Intent(this@NoConnectionActivity, MainActivity::class.java)
                        startActivity(intent)
                        return@Button
                    }) {
                        Text("Try again")
                    }
                    Button(onClick = {
                        val intent = Intent(this@NoConnectionActivity, ManualConnectionActivity::class.java)
                        startActivity(intent)
                        return@Button
                    }) {
                        Text("Play as Guest")
                    }
                }
            }
        }
    }
}