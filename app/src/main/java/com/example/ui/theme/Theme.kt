package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.graphics.Color
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme =
  darkColorScheme(
    primary = GoldPrimary,
    onPrimary = ObsidianBlack,
    primaryContainer = GoldDark,
    onPrimaryContainer = TextWhite,
    secondary = WaterCyan,
    onSecondary = ObsidianBlack,
    secondaryContainer = WaterCyanDark,
    onSecondaryContainer = TextWhite,
    tertiary = DragonCrimson,
    onTertiary = TextWhite,
    background = ObsidianBlack,
    onBackground = TextWhite,
    surface = ObsidianSurface,
    onSurface = TextWhite,
    surfaceVariant = ObsidianSurfaceVariant,
    onSurfaceVariant = TextMuted,
    outline = ObsidianCardBorder
  )

private val LightColorScheme =
  lightColorScheme(
    primary = GoldDark,
    onPrimary = TextWhite,
    primaryContainer = GoldLight,
    onPrimaryContainer = ObsidianBlack,
    secondary = WaterCyanDark,
    onSecondary = TextWhite,
    secondaryContainer = WaterCyanLight,
    onSecondaryContainer = ObsidianBlack,
    tertiary = DragonCrimson,
    onTertiary = TextWhite,
    background = Color(0xFFF8FAFC),
    onBackground = TextDark,
    surface = Color(0xFFFFFFFF),
    onSurface = TextDark,
    surfaceVariant = Color(0xFFE2E8F0),
    onSurfaceVariant = Color(0xFF475569),
    outline = Color(0xFFCBD5E1)
  )

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = true, // Default to immersive dark dojo mode
  dynamicColor: Boolean = false, // Keep brand aesthetic consistent
  content: @Composable () -> Unit,
) {
  val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

  MaterialTheme(colorScheme = colorScheme, typography = Typography, content = content)
}
