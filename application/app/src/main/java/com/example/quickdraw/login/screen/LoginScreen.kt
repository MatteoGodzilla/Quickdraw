package com.example.quickdraw.login.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.quickdraw.login.NavDestination
import com.example.quickdraw.R
import com.example.quickdraw.login.vm.LoginScreenVM
import com.example.quickdraw.ui.theme.QuickdrawTheme
import com.example.quickdraw.ui.theme.primaryButtonColors

@Composable
fun LoginScreen(
    loginScreenVM: LoginScreenVM,
    navHost: NavHostController
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
                    value=loginScreenVM.email.value,
                    onValueChange = {
                        loginScreenVM.email.value = it
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = textFieldTheme,
                    placeholder = {
                        Text("Email")
                    }
                )
                Spacer(modifier = Modifier.height(8.dp))
                TextField(
                    value=loginScreenVM.password.value,
                    onValueChange = {
                        loginScreenVM.password.value = it
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = textFieldTheme,
                    placeholder = {
                        Text("Password")
                    },
                    trailingIcon = {
                        IconButton(
                            onClick = {
                                loginScreenVM.showPassword.value = !loginScreenVM.showPassword.value
                            }
                        ) {
                            if(loginScreenVM.showPassword.value){
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
                    visualTransformation = if (loginScreenVM.showPassword.value) VisualTransformation.None else PasswordVisualTransformation()
                )
                Spacer(modifier = Modifier.height(32.dp))

                Button(
                    onClick = loginScreenVM::sendLogin,
                    colors = primaryButtonColors,
                    enabled = loginScreenVM.canSendLogin()
                ) {
                    Text("Login")
                }

                Spacer(modifier = Modifier.height(32.dp))
                val clickableString = "Register"
                Text(text = buildAnnotatedString {
                    withStyle(
                        style = SpanStyle(
                            textDecoration = TextDecoration.Underline,
                            fontSize = MaterialTheme.typography.labelLarge.fontSize,
                            fontWeight = MaterialTheme.typography.labelLarge.fontWeight,
                            color = MaterialTheme.colorScheme.secondary
                        )

                    ){
                        append(clickableString)
                    }
                    addLink(LinkAnnotation.Clickable(tag = "Go to Register"){
                        navHost.navigate(NavDestination.Register(loginScreenVM.email.value, loginScreenVM.password.value))
                    }, start = 0, end = clickableString.length)
                })

            }
        }
    }
}