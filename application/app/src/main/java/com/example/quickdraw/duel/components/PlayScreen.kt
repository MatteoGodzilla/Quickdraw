package com.example.quickdraw.duel.components

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.DurationBasedAnimationSpec
import androidx.compose.animation.core.Easing
import androidx.compose.animation.core.FiniteAnimationSpec
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.TweenSpec
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.quickdraw.R
import com.example.quickdraw.duel.DuelGameLogic
import com.example.quickdraw.duel.DuelNavigation
import com.example.quickdraw.duel.MatchResult
import com.example.quickdraw.duel.PeerState
import com.example.quickdraw.duel.duelBandit.DuelBanditLogic
import com.example.quickdraw.game.components.infiniteRotation
import com.example.quickdraw.game.repo.GameRepository
import com.example.quickdraw.ui.theme.QuickdrawTheme
import com.example.quickdraw.ui.theme.Typography
import com.google.android.material.progressindicator.LinearProgressIndicatorSpec
import kotlinx.coroutines.delay

@Composable
fun PlayScreen(controller: NavHostController, gameLogic: DuelGameLogic){
    val shouldShoot = gameLogic.shouldShoot.collectAsState().value
    val roundEnded = (gameLogic.selfState.collectAsState().value == PeerState.BANG && gameLogic.otherState.collectAsState().value == PeerState.BANG)
    val lostRound = gameLogic.didSelfWin() == MatchResult.LOST
    val duration = 2000 //milliseconds
    val bloodAnimation = animateFloatAsState(
        targetValue = if(roundEnded && lostRound) 1.0f else 0.0f,
        animationSpec = TweenSpec(duration, 0, LinearEasing)
    )
    LaunchedEffect(roundEnded) {
        if(roundEnded){
            delay(duration.toLong())
            controller.navigate(DuelNavigation.Results)
        }
    }
    QuickdrawTheme {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.fillMaxSize().clickable(onClick = gameLogic::bang).background(
                if(shouldShoot) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary
            )
        ){
            if(roundEnded && lostRound){
                Box(modifier = Modifier.animateContentSize().fillMaxSize().scale(1.0f, bloodAnimation.value).background(Color.Red) )
            }
            Row(){
                if(shouldShoot){
                    Text("SHOOT!", fontSize = Typography.titleLarge.fontSize)
                } else {
                    Text("Steady",fontSize = Typography.titleLarge.fontSize)
                }
            }
        }
    }
}

@Composable
fun PlayScreen(controller: NavHostController, gameLogic: DuelBanditLogic){
    val shouldShoot = gameLogic.canShoot.collectAsState()
    val shootTime = gameLogic.shootTimer.collectAsState()
    val roundEnded = (gameLogic.roundEnds.collectAsState())
    val lostRound = !gameLogic.playerWon.collectAsState().value
    val banditTimer = gameLogic.banditTimer.collectAsState().value
    val duration = 2000 //milliseconds
    val bloodAnimation = animateFloatAsState(
        targetValue = if(roundEnded.value && lostRound) 1.0f else 0.0f,
        animationSpec = TweenSpec(duration, 0, LinearEasing)
    )

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
    QuickdrawTheme {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.fillMaxSize().clickable(onClick = {gameLogic.bang(true)}).background(
                if(shouldShoot.value) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary
            )
        ){
            if(roundEnded.value && lostRound){
                Box(modifier = Modifier.animateContentSize().fillMaxSize().scale(1.0f, bloodAnimation.value).background(Color.Red) )
            }
            Row(modifier=Modifier.fillMaxWidth().fillMaxHeight(0.03f).background(Color.Black)){
                val message = if(shouldShoot.value) "SHOOT!" else "Steady..."
                Text(message, fontSize = Typography.titleLarge.fontSize,
                    modifier = Modifier.fillMaxWidth(expandFromCenter()).align(Alignment.CenterVertically),color = Color.White, textAlign = TextAlign.Center)
            }
        }
    }
}

