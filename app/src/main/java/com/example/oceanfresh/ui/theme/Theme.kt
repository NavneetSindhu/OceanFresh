package com.example.oceanfresh.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

// ── PREMIUM DARK PALETTE ────────────────────────────────────────────
private val DarkBackground = Color(0xFF0C0C0C) // Deepest grey, better than pure black
private val DarkSurface = Color(0xFF161618)    // Slightly lighter for cards
private val DarkSurfaceVariant = Color(0xFF242426) // For text fields/chips
private val DarkOnSurface = Color(0xFFF2F2F2)  // Soft white to reduce glare
private val DarkOnSurfaceVariant = Color(0xFFAAAAAA) // Muted grey for secondary text

private val OceanFreshDarkColorScheme = darkColorScheme(
    primary = GreenPrimary,
    onPrimary = Color.Black,
    primaryContainer = Color(0xFF00391C),      // Deep forest green
    onPrimaryContainer = Color(0xFFB4F3BE),    // Light green text for badges
    secondary = OrangePrimary,
    onSecondary = Color.Black,
    background = DarkBackground,
    onBackground = DarkOnSurface,
    surface = DarkSurface,
    onSurface = DarkOnSurface,
    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = DarkOnSurfaceVariant,
    outline = Color(0xFF3F3F42),               // Thin borders for cards
    error = Color(0xFFFFB4AB)                  // Soft red for dark mode
)

// ── LIGHT PALETTE (Existing) ────────────────────────────────────────
private val OceanFreshLightColorScheme = lightColorScheme(
    primary = GreenPrimary,
    onPrimary = Color.White,
    primaryContainer = GreenContainer,
    onPrimaryContainer = OnGreenContainer,
    background = Color(0xFFF8F9FA),
    surface = Color.White,
    onSurface = OnSurface,
    onSurfaceVariant = OnSurfaceVariant,
    outline = Color(0xFFEEEEEE)
)

val LocalThemeIsDark = staticCompositionLocalOf { false }

@Composable
fun OceanFreshTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) OceanFreshDarkColorScheme else OceanFreshLightColorScheme

    CompositionLocalProvider(LocalThemeIsDark provides darkTheme) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = AppTypography,
            content = content
        )
    }
}