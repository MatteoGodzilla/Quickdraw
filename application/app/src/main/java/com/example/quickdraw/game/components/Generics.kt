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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil3.compose.AsyncImage
import coil3.compose.rememberAsyncImagePainter
import coil3.request.CachePolicy
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.example.quickdraw.R
import com.example.quickdraw.game.vm.LoadingScreenVM
import com.example.quickdraw.game.vm.PopupVM
import com.example.quickdraw.network.data.LeaderboardEntry
import com.example.quickdraw.ui.theme.Typography
import kotlinx.coroutines.delay

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
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
                .offset(y=30.dp),
            contentAlignment = Alignment.TopCenter
        ){
            AnimatedVisibility(
                visible = isShowing.value,
                enter=fadeIn(initialAlpha = 0.2f),
                exit = fadeOut()
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
fun infiniteRotation(): Float{
    val infiniteTransition = rememberInfiniteTransition()
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1000, easing = LinearEasing)
        )
    )
    return rotation
}


@Composable
fun ScreenLoader(
    loadVM: LoadingScreenVM
) {
    val isLoading by loadVM.isLoading
    val msg by loadVM.message
    if (isLoading) {

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
                        .rotate(infiniteRotation())
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

@Composable
fun DropDownMenuForSettings(modifier:Modifier = Modifier,content:@Composable ()->Unit){
    var expanded by remember { mutableStateOf(false) }

    IconButton(onClick = { expanded = !expanded }) {
        Icon(Icons.Default.KeyboardArrowDown, contentDescription = "More options")
    }
    DropdownMenu(
        expanded = expanded,
        onDismissRequest = { expanded = false },
        modifier = modifier.padding(8.dp).background(MaterialTheme.colorScheme.background)
    ) {
        content()
    }
}

@Composable
fun FadableAsyncImage(icon: ByteArray,desc:String,modifier:Modifier = Modifier){
    val painter = rememberAsyncImagePainter(icon)
    AsyncImage(
        ImageRequest.Builder(LocalContext.current)
            .data(icon)
            .crossfade(true)
            .diskCachePolicy(CachePolicy.ENABLED)
            .memoryCachePolicy(CachePolicy.ENABLED)
            .build(),
        desc,
        contentScale = ContentScale.FillBounds,
        modifier = modifier.size(32.dp).clip(CircleShape)
    )
}
