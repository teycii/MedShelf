package com.example.medshelf.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val LightColorScheme = lightColorScheme(
    primary = Teal,
    secondary = Mint,
    background = BgLight,
    surface = White,
    onPrimary = White,
    onSecondary = White,
    onBackground = Navy,
    onSurface = Navy,
    onSurfaceVariant = Grey
)

@Composable
fun MedShelfTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = LightColorScheme,
        typography = Typography,
        content = content
    )
}
