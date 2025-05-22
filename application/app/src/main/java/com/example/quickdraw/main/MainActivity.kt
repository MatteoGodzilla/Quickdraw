package com.example.quickdraw.main

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import com.example.quickdraw.common.PrefKeys
import com.example.quickdraw.common.dataStore
import com.example.quickdraw.login.LoginActivity
import com.example.quickdraw.main.components.BasicScreen
import com.example.quickdraw.main.components.BasicTabLayout
import com.example.quickdraw.main.components.BottomNavBar
import com.example.quickdraw.main.shop.ShopScreen
import com.example.quickdraw.ui.theme.QuickdrawTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable


class Navigation {
    @Serializable
    object YourPlace {
        @Serializable
        object Main
        @Serializable
        object Memories
        @Serializable
        object Inventory
    }
    @Serializable
    object Shop
    @Serializable
    object Map
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        //check for stored login info

        lifecycleScope.launch(Dispatchers.Main) {
            val storedId = this@MainActivity.dataStore.data.map { pref -> pref[PrefKeys.playerId] }.firstOrNull()
            val tokenId = this@MainActivity.dataStore.data.map { pref -> pref[PrefKeys.authToken] }.firstOrNull()
            if(storedId == null || tokenId == null){
                //send request to tokenLogin
                //if incorrect, go to login screen
                val intent = Intent(this@MainActivity, LoginActivity::class.java)
                startActivity(intent)
            }
        }


        setContent {
            Graph()
        }
    }
}

@Preview
@Composable
fun Graph(){
    val controller = rememberNavController()
    NavHost(navController = controller, startDestination = Navigation.Shop) {

        navigation<Navigation.YourPlace>(startDestination = Navigation.YourPlace.Inventory){
            composable<Navigation.YourPlace.Main> {
                BasicScreen("Your Place/Main")
            }
            composable<Navigation.YourPlace.Memories> {
                BasicScreen("Your Place/Memories")
            }
            composable<Navigation.YourPlace.Inventory> {
                BasicScreen("Your Place/Inventory")
            }
        }
        composable<Navigation.Shop> { ShopScreen() }
        composable<Navigation.Map> { MainScreen() }
    }
    controller.navigate(Navigation.YourPlace)
}