package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.RenewlyTheme
import kotlinx.coroutines.delay

@Composable
fun RenewlyToast(
  message: String?,
  onDismiss: () -> Unit,
  modifier: Modifier = Modifier
) {
  val colors = RenewlyTheme.colors

  LaunchedEffect(message) {
    if (message != null) {
      delay(2600)
      onDismiss()
    }
  }

  AnimatedVisibility(
    visible = message != null,
    enter = fadeIn() + slideInVertically { it / 2 },
    exit = fadeOut() + slideOutVertically { it / 2 },
    modifier = modifier
      .fillMaxWidth()
      .padding(horizontal = 16.dp)
  ) {
    if (message != null) {
      val shape = RoundedCornerShape(20.dp)
      // Inverted colors: fill ink, text surface/canvas
      val bgColor = colors.ink
      val textColor = if (colors.isDark) colors.canvas else colors.surface

      Box(
        modifier = Modifier
          .fillMaxWidth()
          .shadow(10.dp, shape, ambientColor = Color.Black.copy(alpha = 0.20f))
          .clip(shape)
          .background(bgColor)
          .padding(horizontal = 18.dp, vertical = 15.dp),
        contentAlignment = Alignment.CenterStart
      ) {
        Row(
          verticalAlignment = Alignment.CenterVertically
        ) {
          Box(
            modifier = Modifier
              .size(9.dp)
              .clip(CircleShape)
              .background(colors.accent)
          )
          Spacer(modifier = Modifier.width(12.dp))
          Text(
            text = message,
            style = TextStyle(fontSize = 13.5.sp, fontWeight = FontWeight.W500, color = textColor)
          )
        }
      }
    }
  }
}
