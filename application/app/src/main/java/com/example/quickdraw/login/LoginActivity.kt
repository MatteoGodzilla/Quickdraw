package com.example.quickdraw.login

import android.app.Activity
import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.example.quickdraw.TAG
import com.example.quickdraw.dataStore
import com.example.quickdraw.game.GameActivity
import com.example.quickdraw.game.components.Popup
import com.example.quickdraw.game.components.ScreenLoader
import com.example.quickdraw.game.vm.GlobalPartsVM
import com.example.quickdraw.game.vm.LoadingScreenVM
import com.example.quickdraw.login.screen.LoginScreen
import com.example.quickdraw.login.screen.RegisterScreen
import com.example.quickdraw.login.vm.LoginScreenVM
import com.example.quickdraw.login.vm.RegisterScreenVM
import com.example.quickdraw.network.ConnectionManager
import com.example.quickdraw.network.NoConnectionActivity
import com.example.quickdraw.ui.theme.QuickdrawTheme
import kotlinx.serialization.Serializable

class LoginNavigation{
    @Serializable
    object Login
    @Serializable
    data class Register(val initialEmail: String, val initialPassword: String)
}

class LoginActivity : ComponentActivity() {
    val globalsVM = GlobalPartsVM()

    private fun onLoginFailed(){
        globalsVM.loadScreen.hideLoading()
        if(ConnectionManager.errorMessage.isNotEmpty()){
            globalsVM.popup.showLoading(ConnectionManager.errorMessage,false)
        }
        else{
            val intent = Intent(this@LoginActivity, NoConnectionActivity::class.java)
            startActivity(intent)
            Log.i(TAG, "Sending from Login to No connection")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            //force portait
            val context = LocalContext.current
            (context as? Activity)?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

            //navigation
            val navigation = rememberNavController()
            NavHost(navController = navigation, startDestination = LoginNavigation.Login){
                composable<LoginNavigation.Login> {
                    val vm = viewModel {
                        LoginScreenVM(this@LoginActivity.dataStore,{onLoginFailed()}) {
                            //On Login success
                            globalsVM.loadScreen.hideLoading()
                            val intent = Intent(this@LoginActivity, GameActivity::class.java)
                            startActivity(intent)
                            Log.i(TAG, "Sending from Login to Game activity")
                        }
                    }
                    LoginScreen(vm, navigation, globalsVM )
                }
                composable<LoginNavigation.Register> { backstackEntry ->
                    val initialValues = backstackEntry.toRoute<LoginNavigation.Register>()
                    val vm = viewModel {
                        RegisterScreenVM(this@LoginActivity.dataStore){
                            //On Register success
                            globalsVM.loadScreen.hideLoading()
                            val intent = Intent(this@LoginActivity, GameActivity::class.java)
                            startActivity(intent)
                            Log.i(TAG, "Sending from Register to Game activity")
                        }
                    }

                    vm.email.value = initialValues.initialEmail
                    vm.password.value = initialValues.initialPassword
                    RegisterScreen(vm, navigation,globalsVM)
                }
            }

            //global parts
            QuickdrawTheme {
                ScreenLoader(globalsVM.loadScreen)
                Popup(3000,globalsVM.popup) { globalsVM.popup.hide() }
            }
        }
    }
}