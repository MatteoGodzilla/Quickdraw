package com.example.quickdraw.duel.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue

@Composable
fun expandFromCenter():Float{
    var expanded by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        expanded = true
    }
    val fraction by animateFloatAsState(
        targetValue = if (expanded) 1f else 0f,
        animationSpec = tween(durationMillis = 800)
    )
    return fraction
}