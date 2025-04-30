package com.example.quickdraw

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.viewmodel.compose.viewModel

import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

import androidx.navigation.toRoute
import com.example.quickdraw.login.LoginScreen
import com.example.quickdraw.login.LoginScreenVM
import com.example.quickdraw.register.RegisterScreen
import com.example.quickdraw.register.RegisterScreenVM
import kotlinx.serialization.Serializable

class NavDestination{
    @Serializable
    object Login
    @Serializable
    data class Register(val initialEmail: String, val initialPassword: String)
}


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val navigation = rememberNavController()
            NavHost(navController = navigation, startDestination = NavDestination.Login){
                composable<NavDestination.Login> {
                    val vm = viewModel { LoginScreenVM() }
                    LoginScreen(vm, navigation)
                }
                composable<NavDestination.Register> { backstackEntry ->
                    val initialValues = backstackEntry.toRoute<NavDestination.Register>()
                    val vm = viewModel { RegisterScreenVM() }
                    vm.email.value = initialValues.initialEmail
                    vm.password.value = initialValues.initialPassword
                    RegisterScreen(vm, navigation)
                }
            }
        }
    }
}