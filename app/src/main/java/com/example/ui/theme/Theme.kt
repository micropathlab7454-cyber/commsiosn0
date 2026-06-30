package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = LabCyanSecondary,
    secondary = LabTealTertiary,
    tertiary = LabBluePrimary,
    background = DarkBackground,
    surface = DarkSurface,
    onPrimary = Color.Black,
    onSecondary = Color.Black,
    onTertiary = Color.White,
    onBackground = DarkOnBackground,
    onSurface = DarkOnBackground
)

private val LightColorScheme = lightColorScheme(
    primary = LabBluePrimary,
    secondary = LabCyanSecondary,
    tertiary = LabTealTertiary,
    background = LightBackground,
    surface = LightSurface,
    onPrimary = LightOnPrimary,
    onSecondary = Color.Black,
    onTertiary = Color.Black,
    onBackground = LightOnBackground,
    onSurface = LightOnBackground
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean,
    content: @Composable () -> Unit,
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
