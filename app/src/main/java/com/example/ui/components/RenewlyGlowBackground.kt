package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.ui.theme.RenewlyTheme

@Composable
fun RenewlyScreenScaffold(
  modifier: Modifier = Modifier,
  content: @Composable BoxScope.() -> Unit
) {
  val colors = RenewlyTheme.colors
  Box(
    modifier = modifier
      .fillMaxSize()
      .background(colors.surface)
  ) {
    // Decorative glows behind all content (Section 3.7)
    // Glow 1: 340 x 340, top: -120dp, right: -90dp
    Box(
      modifier = Modifier
        .align(Alignment.TopEnd)
        .offset(x = 90.dp, y = (-120).dp)
        .size(340.dp)
    ) {
      Canvas(modifier = Modifier.fillMaxSize()) {
        drawCircle(
          brush = Brush.radialGradient(
            colors = listOf(colors.glow1, Color.Transparent),
            center = Offset(size.width / 2f, size.height / 2f),
            radius = size.width / 2f
          )
        )
      }
    }

    // Glow 2: 320 x 320, bottom: 60dp, left: -130dp
    Box(
      modifier = Modifier
        .align(Alignment.BottomStart)
        .offset(x = (-130).dp, y = (-60).dp)
        .size(320.dp)
    ) {
      Canvas(modifier = Modifier.fillMaxSize()) {
        drawCircle(
          brush = Brush.radialGradient(
            colors = listOf(colors.glow2, Color.Transparent),
            center = Offset(size.width / 2f, size.height / 2f),
            radius = size.width / 2f
          )
        )
      }
    }

    content()
  }
}
