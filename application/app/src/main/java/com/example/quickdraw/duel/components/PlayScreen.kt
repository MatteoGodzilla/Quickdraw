package com.example.quickdraw.duel.components

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.TweenSpec
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.style.TextAlign
import androidx.navigation.NavHostController
import com.example.quickdraw.duel.DuelGameLogic
import com.example.quickdraw.duel.DuelNavigation
import com.example.quickdraw.duel.MatchResult
import com.example.quickdraw.duel.PeerState
import com.example.quickdraw.duel.duelBandit.DuelBanditLogic
import com.example.quickdraw.ui.theme.QuickdrawTheme
import com.example.quickdraw.ui.theme.Typography
import kotlinx.coroutines.delay

@Composable
fun PlayScreen(controller: NavHostController, gameLogic: DuelGameLogic){
    val shouldShoot = gameLogic.shouldShoot.collectAsState().value
    val roundEnded = (gameLogic.selfState.collectAsState().value == PeerState.BANG && gameLogic.otherState.collectAsState().value == PeerState.BANG)
    val lostRound = gameLogic.didSelfWin() == MatchResult.LOST
    val duration = 2000 //milliseconds
    LaunchedEffect(roundEnded) {
        if(roundEnded){
            delay(duration.toLong())
            controller.navigate(DuelNavigation.Results)
        }
    }
    PlayScreenUI(shouldShoot, roundEnded, lostRound, duration) { gameLogic.bang() }
}

@Composable
fun PlayScreen(controller: NavHostController, gameLogic: DuelBanditLogic){
    val shouldShoot = gameLogic.canShoot.collectAsState()
    val shootTime = gameLogic.shootTimer.collectAsState()
    val roundEnded = (gameLogic.roundEnds.collectAsState())
    val lostRound = !gameLogic.playerWon.collectAsState().value
    val banditTimer = gameLogic.banditTimer.collectAsState().value
    val duration = 2000 //milliseconds

    LaunchedEffect(true) {
        delay(shootTime.value)
        gameLogic.allowShooting()
    }

    if(shouldShoot.value){
        LaunchedEffect(true) {
            delay(banditTimer)
            gameLogic.bang(false)
        }
    }
    LaunchedEffect(roundEnded.value) {
        if(roundEnded.value){
            delay(duration.toLong())
            controller.navigate(DuelNavigation.Results)
        }
    }
    PlayScreenUI(shouldShoot.value, roundEnded.value, lostRound, duration) { gameLogic.bang(true)}
}

@Composable
fun PlayScreenUI(shouldShoot: Boolean, roundEnded: Boolean, lostRound: Boolean, duration:Int, onBang:()->Unit){
    val bloodAnimation = animateFloatAsState(
        targetValue = if(roundEnded && lostRound) 1.0f else 0.0f,
        animationSpec = TweenSpec(duration, 0, LinearEasing)
    )
    QuickdrawTheme {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.fillMaxSize().background(
                if(shouldShoot) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary
            ).pointerInput(Unit) { detectTapGestures(onPress = { onBang() }) }
        ){
            if(roundEnded && lostRound){
                Box(modifier = Modifier.animateContentSize().fillMaxSize().scale(1.0f, bloodAnimation.value).background(Color.Red) )
            }
            Row(modifier=Modifier.fillMaxWidth().fillMaxHeight(0.03f).background(Color.Black)){
                val message = if(shouldShoot) "SHOOT!" else "Steady..."
                Text(message, fontSize = Typography.titleLarge.fontSize,
                    modifier = Modifier.fillMaxWidth(expandFromCenter()).align(Alignment.CenterVertically),color = Color.White, textAlign = TextAlign.Center)
            }
        }
    }
}
