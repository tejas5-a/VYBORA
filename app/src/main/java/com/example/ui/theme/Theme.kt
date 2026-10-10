package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val VyboraDarkColorScheme = darkColorScheme(
    primary = EmeraldGreen,
    onPrimary = Color(0xFF08090B),
    primaryContainer = Color(0xFF143E26),
    onPrimaryContainer = EmeraldGreen,
    secondary = SoftWhite,
    onSecondary = VyboraBackground,
    secondaryContainer = VyboraSurfaceElevated,
    onSecondaryContainer = SoftWhite,
    tertiary = EmeraldGreenDark,
    background = VyboraBackground,
    onBackground = SoftWhite,
    surface = VyboraBackgroundSecondary,
    onSurface = SoftWhite,
    surfaceVariant = VyboraSurfaceElevated,
    onSurfaceVariant = MutedGrey,
    outline = GlassBorder,
    outlineVariant = Color(0x1AFFFFFF)
)

@Composable
fun VyboraTheme(
    darkTheme: Boolean = true, // VYBORA is designed with dark mode as default
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            dynamicDarkColorScheme(context)
        }
        else -> VyboraDarkColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
