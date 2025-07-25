package com.example.quickdraw.login

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.example.quickdraw.common.TAG
import com.example.quickdraw.common.dataStore
import com.example.quickdraw.game.GameActivity
import com.example.quickdraw.login.screen.LoginScreen
import com.example.quickdraw.login.screen.RegisterScreen
import com.example.quickdraw.login.vm.LoginScreenVM
import com.example.quickdraw.login.vm.RegisterScreenVM

class LoginActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()


        setContent {
            val navigation = rememberNavController()

            NavHost(navController = navigation, startDestination = NavDestination.Login){
                composable<NavDestination.Login> {
                    val vm = viewModel { LoginScreenVM(this@LoginActivity.dataStore, {
                        val intent = Intent(this@LoginActivity, GameActivity::class.java)
                        startActivity(intent)
                        Log.i(TAG, "Sending from Login to Game activity")
                    }) }
                    LoginScreen(vm, navigation)
                }
                composable<NavDestination.Register> { backstackEntry ->
                    val initialValues = backstackEntry.toRoute<NavDestination.Register>()
                    val vm = viewModel { RegisterScreenVM(this@LoginActivity.dataStore) }
                    vm.email.value = initialValues.initialEmail
                    vm.password.value = initialValues.initialPassword
                    RegisterScreen(vm, navigation)
                }
            }
        }
    }
}