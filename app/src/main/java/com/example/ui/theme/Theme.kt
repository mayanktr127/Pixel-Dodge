package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    primary = SkyBlue,
    onPrimary = Color.White,
    primaryContainer = SkyBlueBg,
    onPrimaryContainer = SkyBlue,
    secondary = StreakOrange,
    onSecondary = Color.White,
    secondaryContainer = StreakOrangeBg,
    onSecondaryContainer = StreakOrange,
    tertiary = MintGreen,
    onTertiary = Color.White,
    tertiaryContainer = MintGreenBg,
    onTertiaryContainer = MintGreen,
    background = CreamBackground,
    onBackground = TextPrimary,
    surface = PureWhite,
    onSurface = TextPrimary,
    surfaceVariant = Color(0xFFF1F5F9),
    onSurfaceVariant = TextSecondary,
    outline = CardBorder
)

private val DarkColorScheme = darkColorScheme(
    primary = ElectricCyan,
    onPrimary = DarkSurface,
    primaryContainer = Color(0xFF164E63),
    onPrimaryContainer = ElectricCyan,
    secondary = StreakOrange,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFF7C2D12),
    onSecondaryContainer = Color(0xFFFFEDD5),
    tertiary = MintGreen,
    onTertiary = DarkSurface,
    tertiaryContainer = Color(0xFF064E3B),
    onTertiaryContainer = MintGreen,
    background = DarkSurface,
    onBackground = Color(0xFFF8FAFC),
    surface = Color(0xFF1E293B),
    onSurface = Color(0xFFF8FAFC),
    surfaceVariant = Color(0xFF334155),
    onSurfaceVariant = Color(0xFF94A3B8),
    outline = Color(0xFF475569)
)

@Composable
fun PixelDodgeTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = AppTypography,
        content = content
    )
}
