package com.example.ui.theme

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

// Section 3.2 Light Theme
val LightCanvas = Color(0xFFECEAE7)
val LightSurface = Color(0xFFF6F4F2)
val LightCard = Color(0xB8FFFFFF)       // rgba(255, 255, 255, 0.72)
val LightCard2 = Color(0x80FFFFFF)      // rgba(255, 255, 255, 0.50)
val LightField = Color(0x99FFFFFF)      // rgba(255, 255, 255, 0.60)
val LightSticky = Color(0xC7F6F4F2)     // rgba(246, 244, 242, 0.78)
val LightRowOnTint = Color(0xA8FFFFFF)  // rgba(255, 255, 255, 0.66)
val LightSheet = Color(0xFFFBFAF9)
val LightInk = Color(0xFF24211F)
val LightInk2 = Color(0xFF6B6663)
val LightInk3 = Color(0xFF6F6A66)
val LightLine = Color(0x1724211F)       // rgba(36, 33, 31, 0.09)
val LightHover = Color(0x0924211F)      // rgba(36, 33, 31, 0.035)
val LightTrack = Color(0x1224211F)      // rgba(36, 33, 31, 0.07)
val LightBarInactive = Color(0x1C24211F) // rgba(36, 33, 31, 0.11)
val LightAccent = Color(0xFFE8492C)
val LightAccentPress = Color(0xFFC93A20)
val LightAccentDeep = Color(0xFFA83218)
val LightAccentTint = Color(0xFFFDECE7)
val LightAccentLine = Color(0x38E8492C) // rgba(232, 73, 44, 0.22)
val LightOnAccent = Color(0xFFFFFFFF)
val LightScrim = Color(0x6B14110F)      // rgba(20, 17, 15, 0.42)
val LightGlow1 = Color(0x33E8492C)      // 20%
val LightGlow2 = Color(0x1FE8492C)      // 12%

// Section 3.3 Dark Theme
val DarkCanvas = Color(0xFF100F0E)
val DarkSurface = Color(0xFF191716)
val DarkCard = Color(0x10FAF7F5)        // rgba(250, 247, 245, 0.065)
val DarkCard2 = Color(0x0AFAF7F5)       // rgba(250, 247, 245, 0.040)
val DarkField = Color(0x0DFAF7F5)       // rgba(250, 247, 245, 0.050)
val DarkSticky = Color(0xD1191716)      // rgba(25, 23, 22, 0.82)
val DarkRowOnTint = Color(0x12FAF7F5)   // rgba(250, 247, 245, 0.070)
val DarkSheet = Color(0xFF221F1D)
val DarkInk = Color(0xFFF7F4F2)
val DarkInk2 = Color(0xFFB5AFAB)
val DarkInk3 = Color(0xFF98918C)
val DarkLine = Color(0x1CFAF7F5)        // rgba(250, 247, 245, 0.11)
val DarkHover = Color(0x0DFAF7F5)       // rgba(250, 247, 245, 0.05)
val DarkTrack = Color(0x1AFAF7F5)       // rgba(250, 247, 245, 0.10)
val DarkBarInactive = Color(0x29FAF7F5) // rgba(250, 247, 245, 0.16)
val DarkAccent = Color(0xFFFF7A63)
val DarkAccentPress = Color(0xFFFFA48B)
val DarkAccentDeep = Color(0xFFFFA48B)
val DarkAccentTint = Color(0x21FF7A63)  // rgba(255, 122, 99, 0.13)
val DarkAccentLine = Color(0x47FFA48B)  // rgba(255, 164, 139, 0.28)
val DarkOnAccent = Color(0xFF231F1D)
val DarkScrim = Color(0x6B14110F)       // rgba(20, 17, 15, 0.42)
val DarkGlow1 = Color(0x38FF7A63)       // 22%
val DarkGlow2 = Color(0x1FFF7A63)       // 12%

// Section 3.4 Category Colors
object CategoryColors {
  val Streaming = Pair(Color(0xFFE8492C), Color(0xFFFF7A63))
  val Software = Pair(Color(0xFF4A4441), Color(0xFFF0EBE8))
  val Music = Pair(Color(0xFFC96A4B), Color(0xFFFFA48B))
  val Fitness = Pair(Color(0xFF8D8681), Color(0xFFA7A09B))
  val News = Pair(Color(0xFFEDA488), Color(0xFFFFC9B5))
  val Reading = Pair(Color(0xFF6B6663), Color(0xFFCFC8C3))

  val List = listOf(
    "Streaming" to Streaming,
    "Software" to Software,
    "Music" to Music,
    "Fitness" to Fitness,
    "News" to News,
    "Reading" to Reading
  )

  fun getCategoryColor(category: String, isDark: Boolean): Color {
    val found = List.firstOrNull { it.first.equals(category, ignoreCase = true) }
    if (found != null) {
      return if (isDark) found.second.second else found.second.first
    }
    // User-created categories take the next unused colour from this list, cycling in order
    val hash = kotlin.math.abs(category.hashCode())
    val pair = List[hash % List.size].second
    return if (isDark) pair.second else pair.first
  }
}

@Immutable
data class RenewlyColors(
  val canvas: Color,
  val surface: Color,
  val card: Color,
  val card2: Color,
  val field: Color,
  val sticky: Color,
  val rowOnTint: Color,
  val sheet: Color,
  val ink: Color,
  val ink2: Color,
  val ink3: Color,
  val line: Color,
  val hover: Color,
  val track: Color,
  val barInactive: Color,
  val accent: Color,
  val accentPress: Color,
  val accentDeep: Color,
  val accentTint: Color,
  val accentLine: Color,
  val onAccent: Color,
  val scrim: Color,
  val glow1: Color,
  val glow2: Color,
  val isDark: Boolean
)

val LocalRenewlyColors = staticCompositionLocalOf {
  RenewlyColors(
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
}
