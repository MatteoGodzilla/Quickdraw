package com.example.quickdraw.ui.theme

import android.app.Activity
import android.hardware.lights.Light
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val LightColorScheme = lightColorScheme(
    background = Color(0xFFFEEFDC),
    surfaceContainer = Color(0xFFF4CB89),
    onSurface = Color(0xFF2F0E02),
    primary = Color(0xFF65D73C),
    secondary = Color(0XFFF2721A),
    tertiary = Color(0XFFBC5813),
    onErrorContainer = Color(0xFF9A9A9A) //used for disabled stuff

    /* Other default colors to override
    background = Color(0xFFFFFBFE),
    surface = Color(0xFFFFFBFE),
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = Color(0xFF1C1B1F),
    onSurface = Color(0xFF1C1B1F),
    */
)

val primaryButtonColors = ButtonColors(
    containerColor = LightColorScheme.primary,
    contentColor = LightColorScheme.onSurface,
    disabledContainerColor = LightColorScheme.surfaceVariant,
    disabledContentColor = LightColorScheme.onSurface
)

@Composable
fun QuickdrawTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = LightColorScheme,
        typography = Typography,
        content = content
    )
}