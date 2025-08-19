package com.example.quickdraw.game.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.quickdraw.R
import com.example.quickdraw.game.vm.LoadingScreenVM
import com.example.quickdraw.game.vm.PopupVM
import com.example.quickdraw.ui.theme.Typography
import kotlinx.coroutines.delay

enum class PopupType{
    SUCCESS,
    FAILURE,
    WARNING
}

@Composable
fun RowDivider(){
    HorizontalDivider(
        modifier = Modifier.fillMaxWidth().padding(all=0.dp),
        thickness = 2.dp,
        color=Color.Black
    )
}

@Composable
fun Popup(duration:Long, popupVm: PopupVM, onEnd:()->Unit){
        val message = popupVm.message.collectAsState()
        val isShowing = popupVm.isShowing.collectAsState()
        val success = popupVm.isPositive.collectAsState()
        val backColor = if(success.value) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary
        LaunchedEffect(isShowing.value) {
            if (isShowing.value) {
                delay(duration)
                popupVm.hide()
            }
        }
        val density = LocalDensity.current
        AnimatedVisibility(
            visible = isShowing.value,
            enter=fadeIn(initialAlpha = 0.2f),
            exit = fadeOut()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
                    .offset(y=10.dp),
                contentAlignment = Alignment.TopCenter
            ){
                Surface(color = backColor,
                    shape = RoundedCornerShape(8.dp),
                    shadowElevation = 4.dp,
                    modifier = Modifier.wrapContentSize()) {
                    Row(
                        modifier = Modifier
                            .padding(horizontal = 16.dp, vertical = 12.dp)
                            .fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ){
                        Text(
                            text = message.value,
                            color = Color.White
                        )
                    }
                }
            }
        }
}



@Composable
fun ScreenLoader(
    loadVM: LoadingScreenVM
) {
    val isLoading by loadVM.isLoading
    val msg by loadVM.message
    if (isLoading) {
        val infiniteTransition = rememberInfiniteTransition()
        val rotation by infiniteTransition.animateFloat(
            initialValue = 0f,
            targetValue = 360f,
            animationSpec = infiniteRepeatable(
                animation = tween(durationMillis = 1000, easing = LinearEasing)
            )
        )
        Dialog(
            onDismissRequest = {},
            properties = DialogProperties(
                usePlatformDefaultWidth = false,
                dismissOnBackPress = false,
                dismissOnClickOutside = false,
                decorFitsSystemWindows = false,
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.radar_24px),
                    contentDescription = "Loading icon",
                    modifier = Modifier
                        .size(64.dp)
                        .rotate(rotation)
                )
                Text(msg)
            }
        }
    }
}

@Composable
fun ShoppingContainer(rowContent: @Composable ()->Unit, columnContent: @Composable ()->Unit){
    Row (
        modifier = Modifier.fillMaxWidth().padding(vertical = 0.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ){
        Row (
            modifier = Modifier.padding(start = 10.dp, top = 10.dp, bottom = 10.dp).weight(1f),
            verticalAlignment = Alignment.CenterVertically
        ) {
            rowContent()
        }
        Column(modifier = Modifier.padding(top = 10.dp, bottom = 10.dp, end = 10.dp)) {
            columnContent()
        }
    }
}

@Composable
fun LockedContainer(content:@Composable ()->Unit){
    Row (
        modifier = Modifier.fillMaxWidth().padding(vertical = 0.dp).background(color= MaterialTheme.colorScheme.onErrorContainer),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically

    ){
        Row (
            modifier = Modifier.padding(start = 10.dp, top = 10.dp, bottom = 10.dp).weight(1f),
            verticalAlignment = Alignment.CenterVertically
        ) {
            content()
        }
    }
}

@Composable
fun TopScreenInfo(text:String){
    Text(text,
        textAlign = TextAlign.Center,
        fontSize =Typography.bodyLarge.fontSize,
        modifier = Modifier.fillMaxWidth().padding(all = 4.dp))
    RowDivider()
}



