package com.rodrigofy.app.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val GutsDarkColorScheme = darkColorScheme(
    primary = SourPurple80,
    onPrimary = SourPurple10,
    primaryContainer = SourPurple40,
    onPrimaryContainer = SourLavender95,
    secondary = DejaVuPink,
    onSecondary = SourPurple10,
    secondaryContainer = GutsMaroon,
    onSecondaryContainer = SourLavender95,
    tertiary = TeenageDreamGold,
    onTertiary = SourPurple10,
    background = GutsBlack,
    onBackground = OnDark,
    surface = SourPurple20,
    onSurface = OnDark,
    surfaceVariant = SourPurple40,
    onSurfaceVariant = SourLavender90,
    error = VampireRed,
    onError = SourLavender95
)

private val SourLightColorScheme = lightColorScheme(
    primary = SourPurple60,
    onPrimary = SourLavender95,
    primaryContainer = SourLavender90,
    onPrimaryContainer = SourPurple10,
    secondary = DejaVuPink,
    onSecondary = SourLavender95,
    secondaryContainer = SourLavender90,
    onSecondaryContainer = SourPurple20,
    tertiary = SourPurple40,
    background = SourLavender95,
    onBackground = SourPurple10,
    surface = SourLavender95,
    onSurface = SourPurple10,
    surfaceVariant = SourLavender90,
    onSurfaceVariant = SourPurple40,
    error = VampireRed,
    onError = SourLavender95
)

/**
 * rodrigofy's theme. Defaults to the GUTS-inspired dark palette; honors
 * Dynamic Color on Android 12+ when [dynamicColor] is enabled, and always
 * falls back to the hand-tuned SOUR/GUTS palettes otherwise.
 *
 * Note: this uses the stable androidx.compose.material3 tonal color
 * system (surface/container roles, expressive type scale, large shapes)
 * to express the Material 3 Expressive language. If you want to pull in
 * the newer material3-expressive alpha artifact once it's generally
 * available, swap the dependency in libs.versions.toml — the color
 * scheme and typography below are already structured to drop in.
 */
@Composable
fun RodrigofyTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val context = LocalContext.current
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> GutsDarkColorScheme
        else -> SourLightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = RodrigofyTypography,
        content = content
    )
}
