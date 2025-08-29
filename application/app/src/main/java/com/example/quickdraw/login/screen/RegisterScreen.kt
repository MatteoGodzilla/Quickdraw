package com.example.quickdraw.login.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.quickdraw.R
import com.example.quickdraw.game.vm.GlobalPartsVM
import com.example.quickdraw.login.vm.RegisterScreenVM
import com.example.quickdraw.ui.theme.QuickdrawTheme
import com.example.quickdraw.ui.theme.primaryButtonColors

@Composable
fun RegisterScreen(
    registerScreenVM: RegisterScreenVM,
    globalsVM: GlobalPartsVM
){
    QuickdrawTheme {
        Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
            Column (
                modifier = Modifier.padding(innerPadding).fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ){
                Spacer(modifier = Modifier.height(64.dp))
                Text(
                    "QUICKDRAW",
                    fontSize = MaterialTheme.typography.headlineLarge.fontSize,
                    fontWeight = MaterialTheme.typography.headlineLarge.fontWeight
                )
                Spacer(modifier = Modifier.height(64.dp))
                val textFieldTheme = TextFieldDefaults.colors(
                    unfocusedContainerColor = MaterialTheme.colorScheme.surfaceContainer,
                    focusedContainerColor = MaterialTheme.colorScheme.surfaceContainer
                )
                TextField(
                    value=registerScreenVM.username.value,
                    onValueChange = {registerScreenVM.username.value = it},
                    modifier = Modifier.fillMaxWidth(),
                    colors = textFieldTheme,
                    placeholder = {
                        Text("Username")
                    }
                )
                Spacer(modifier = Modifier.height(8.dp))
                TextField(
                    value=registerScreenVM.email.value,
                    onValueChange = {registerScreenVM.email.value = it},
                    modifier = Modifier.fillMaxWidth(),
                    colors = textFieldTheme,
                    placeholder = {
                        Text("Email")
                    }
                )
                Spacer(modifier = Modifier.height(8.dp))
                TextField(
                    value=registerScreenVM.password.value,
                    onValueChange = {
                        registerScreenVM.password.value = it
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = textFieldTheme,
                    placeholder = {
                        Text("Password")
                    },
                    trailingIcon = {
                        IconButton(
                            onClick = {
                                registerScreenVM.showPassword.value = !registerScreenVM.showPassword.value
                            }
                        ) {
                            if(registerScreenVM.showPassword.value){
                                Icon(
                                    imageVector = ImageVector.vectorResource(R.drawable.outline_visibility_off_24),
                                    "Show Password"
                                )
                            } else {
                                Icon(
                                    imageVector = ImageVector.vectorResource(R.drawable.outline_visibility_24),
                                    "Show Password"
                                )
                            }

                        }
                    },
                    visualTransformation = if (registerScreenVM.showPassword.value) VisualTransformation.None else PasswordVisualTransformation()
                )
                Spacer(modifier = Modifier.height(8.dp))
                TextField(
                    value=registerScreenVM.passwordConfirm.value,
                    onValueChange = {registerScreenVM.passwordConfirm.value = it},
                    modifier = Modifier.fillMaxWidth(),
                    colors = textFieldTheme,
                    placeholder = {
                        Text("Confirm Password")
                    },
                    trailingIcon = {
                        if(registerScreenVM.doPasswordsMatch() && registerScreenVM.passwordConfirm.value != ""){
                            Icon(Icons.Outlined.Check, "Passwords match")
                        }
                    },
                    visualTransformation = PasswordVisualTransformation()
                )

                Spacer(modifier = Modifier.height(32.dp))

                Button(
                    onClick = {globalsVM.loadScreen.showLoading("Registering account");registerScreenVM.register()},
                    colors = primaryButtonColors,
                    enabled = registerScreenVM.canRegister()
                ) {
                    Text("Register")
                }
            }
        }
    }
}