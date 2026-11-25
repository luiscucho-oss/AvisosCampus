package com.tecsup.avisoscampus.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = Azul80,
    secondary = AzulGris80,
    tertiary = Celeste80
)

private val LightColorScheme = lightColorScheme(
    primary = Indigo,
    onPrimary = Color.White,
    secondary = Violeta,
    tertiary = Fucsia,
    background = FondoClaro,
    surface = Color.White,
    onSurface = TextoFuerte,
    onSurfaceVariant = TextoSuave
)

@Composable
fun AvisosCampusTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme,
        typography = Typography,
        content = content
    )
}
