package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.RenewlyTheme
import com.example.ui.theme.RenewlyTokens

@Composable
fun RenewlyTitleHeader(
  eyebrow: String,
  title: String,
  modifier: Modifier = Modifier,
  rightContent: (@Composable () -> Unit)? = null
) {
  val colors = RenewlyTheme.colors
  val typography = RenewlyTheme.typography

  Row(
    modifier = modifier
      .fillMaxWidth()
      .background(colors.sticky)
      .padding(
        horizontal = RenewlyTokens.StickyHeaderHorizontal,
        vertical = RenewlyTokens.StickyHeaderVertical
      ),
    horizontalArrangement = Arrangement.SpaceBetween,
    verticalAlignment = Alignment.CenterVertically
  ) {
    Column(modifier = Modifier.weight(1f, fill = false)) {
      Text(
        text = eyebrow.uppercase(),
        style = typography.eyebrow,
        color = colors.ink3
      )
      Text(
        text = title,
        style = typography.sectionHeader,
        color = colors.ink
      )
    }

    if (rightContent != null) {
      Box(modifier = Modifier.padding(start = 12.dp)) {
        rightContent()
      }
    }
  }
}

@Composable
fun RenewlyBackHeader(
  title: String,
  onBack: () -> Unit,
  modifier: Modifier = Modifier,
  isCategoryName: Boolean = false,
  iconName: String = "chevron-left",
  accessibilityLabel: String = "Back",
  rightContent: (@Composable () -> Unit)? = null
) {
  val colors = RenewlyTheme.colors
  val typography = RenewlyTheme.typography

  Row(
    modifier = modifier
      .fillMaxWidth()
      .background(colors.sticky)
      .padding(horizontal = 14.dp, vertical = 12.dp),
    verticalAlignment = Alignment.CenterVertically
  ) {
    RenewlyHeaderIconButton(
      iconName = iconName,
      onClick = onBack,
      contentDescription = accessibilityLabel
    )
    Spacer(modifier = Modifier.width(12.dp))

    Text(
      text = title,
      style = if (isCategoryName) {
        TextStyle(fontSize = 12.5.sp, fontWeight = FontWeight.W600, color = colors.ink2)
      } else {
        TextStyle(fontSize = 14.sp, fontWeight = FontWeight.W600, color = colors.ink)
      },
      modifier = Modifier.weight(1f)
    )

    if (rightContent != null) {
      rightContent()
    }
  }
}
