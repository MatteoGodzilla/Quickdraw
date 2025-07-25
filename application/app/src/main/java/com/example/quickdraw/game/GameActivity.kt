package com.example.quickdraw.game

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import com.example.quickdraw.game.components.BasicScreen
import com.example.quickdraw.game.shop.ShopScreen
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

class GameActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        //check for stored login info


        /*


         */

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