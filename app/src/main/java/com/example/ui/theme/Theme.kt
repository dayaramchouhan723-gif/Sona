package com.example.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val SonaEmpireColorScheme = darkColorScheme(
    primary = SonaGold,
    onPrimary = EmpireDarkBg,
    primaryContainer = EmpireDarkCard,
    onPrimaryContainer = SonaGoldGlow,
    secondary = SonaRed,
    onSecondary = TextPrimary,
    secondaryContainer = EmpireDarkCard,
    onSecondaryContainer = SonaRedGlow,
    tertiary = MyraPink,
    background = EmpireDarkBg,
    onBackground = TextPrimary,
    surface = EmpireDarkSurface,
    onSurface = TextPrimary,
    surfaceVariant = EmpireDarkCard,
    onSurfaceVariant = TextSecondary
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = EmpireDarkBg.toArgb()
            window.navigationBarColor = EmpireDarkBg.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
            WindowCompat.getInsetsController(window, view).isAppearanceLightNavigationBars = false
        }
    }

    MaterialTheme(
        colorScheme = SonaEmpireColorScheme,
        typography = Typography,
        content = content
    )
}
