package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable

private val RenewlyLightColors = RenewlyColors(
  canvas = LightCanvas,
  surface = LightSurface,
  card = LightCard,
  card2 = LightCard2,
  field = LightField,
  sticky = LightSticky,
  rowOnTint = LightRowOnTint,
  sheet = LightSheet,
  ink = LightInk,
  ink2 = LightInk2,
  ink3 = LightInk3,
  line = LightLine,
  hover = LightHover,
  track = LightTrack,
  barInactive = LightBarInactive,
  accent = LightAccent,
  accentPress = LightAccentPress,
  accentDeep = LightAccentDeep,
  accentTint = LightAccentTint,
  accentLine = LightAccentLine,
  onAccent = LightOnAccent,
  scrim = LightScrim,
  glow1 = LightGlow1,
  glow2 = LightGlow2,
  isDark = false
)

private val RenewlyDarkColors = RenewlyColors(
  canvas = DarkCanvas,
  surface = DarkSurface,
  card = DarkCard,
  card2 = DarkCard2,
  field = DarkField,
  sticky = DarkSticky,
  rowOnTint = DarkRowOnTint,
  sheet = DarkSheet,
  ink = DarkInk,
  ink2 = DarkInk2,
  ink3 = DarkInk3,
  line = DarkLine,
  hover = DarkHover,
  track = DarkTrack,
  barInactive = DarkBarInactive,
  accent = DarkAccent,
  accentPress = DarkAccentPress,
  accentDeep = DarkAccentDeep,
  accentTint = DarkAccentTint,
  accentLine = DarkAccentLine,
  onAccent = DarkOnAccent,
  scrim = DarkScrim,
  glow1 = DarkGlow1,
  glow2 = DarkGlow2,
  isDark = true
)

object RenewlyTheme {
  val colors: RenewlyColors
    @Composable
    @ReadOnlyComposable
    get() = LocalRenewlyColors.current

  val typography: RenewlyTypography
    @Composable
    @ReadOnlyComposable
    get() = LocalRenewlyTypography.current
}

@Composable
fun RenewlyAppTheme(
  themeSetting: String = "system",
  content: @Composable () -> Unit
) {
  val isDark = when (themeSetting.lowercase()) {
    "dark" -> true
    "light" -> false
    else -> isSystemInDarkTheme()
  }

  val renewlyColors = if (isDark) RenewlyDarkColors else RenewlyLightColors

  val materialColors = if (isDark) {
    darkColorScheme(
      primary = renewlyColors.accent,
      onPrimary = renewlyColors.onAccent,
      background = renewlyColors.surface,
      surface = renewlyColors.surface,
      onBackground = renewlyColors.ink,
      onSurface = renewlyColors.ink
    )
  } else {
    lightColorScheme(
      primary = renewlyColors.accent,
      onPrimary = renewlyColors.onAccent,
      background = renewlyColors.surface,
      surface = renewlyColors.surface,
      onBackground = renewlyColors.ink,
      onSurface = renewlyColors.ink
    )
  }

  CompositionLocalProvider(
    LocalRenewlyColors provides renewlyColors,
    LocalRenewlyTypography provides RenewlyType
  ) {
    MaterialTheme(
      colorScheme = materialColors,
      content = content
    )
  }
}
