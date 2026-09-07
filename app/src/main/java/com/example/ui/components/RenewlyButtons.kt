package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.example.ui.theme.RenewlyTheme
import com.example.ui.theme.RenewlyTokens

@Composable
fun RenewlyPrimaryButton(
  text: String,
  onClick: () -> Unit,
  modifier: Modifier = Modifier,
  enabled: Boolean = true,
  isExtraLarge: Boolean = false,
  showArrow: Boolean = true,
  testTag: String = "primary_button"
) {
  val colors = RenewlyTheme.colors
  val typography = RenewlyTheme.typography
  val interactionSource = remember { MutableInteractionSource() }
  val isPressed by interactionSource.collectIsPressedAsState()

  val height = if (isExtraLarge) 58.dp else 56.dp
  val horizPadding = if (isExtraLarge) 24.dp else 22.dp

  val bgColor = when {
    !enabled -> colors.track
    isPressed -> colors.accentPress
    else -> colors.accent
  }
  val textColor = if (enabled) colors.onAccent else colors.ink3

  Box(
    modifier = modifier
      .fillMaxWidth()
      .height(height)
      .then(if (enabled) Modifier.shadow(8.dp, RoundedCornerShape(RenewlyTokens.RadiusPill), ambientColor = Color.Black.copy(alpha = 0.15f)) else Modifier)
      .clip(RoundedCornerShape(RenewlyTokens.RadiusPill))
      .background(bgColor)
      .clickable(
        interactionSource = interactionSource,
        indication = null,
        enabled = true,
        role = Role.Button,
        onClick = onClick
      )
      .padding(horizontal = horizPadding)
      .testTag(testTag),
    contentAlignment = Alignment.CenterStart
  ) {
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Text(
        text = text,
        style = if (isExtraLarge) typography.buttonLarge else typography.buttonLarge,
        color = textColor
      )
      if (showArrow) {
        RenewlyIcon(
          name = "arrow-right",
          color = textColor,
          size = 18.dp
        )
      }
    }
  }
}

@Composable
fun RenewlySecondaryCardButton(
  onClick: () -> Unit,
  modifier: Modifier = Modifier,
  testTag: String = "secondary_card_button",
  content: @Composable RowScope.() -> Unit
) {
  val colors = RenewlyTheme.colors
  val interactionSource = remember { MutableInteractionSource() }
  val isPressed by interactionSource.collectIsPressedAsState()

  val borderColor = if (isPressed) colors.accent else colors.line

  Row(
    modifier = modifier
      .fillMaxWidth()
      .height(56.dp)
      .clip(RoundedCornerShape(20.dp))
      .background(colors.card)
      .border(1.dp, borderColor, RoundedCornerShape(20.dp))
      .clickable(
        interactionSource = interactionSource,
        indication = null,
        role = Role.Button,
        onClick = onClick
      )
      .padding(horizontal = 18.dp)
      .testTag(testTag),
    horizontalArrangement = Arrangement.SpaceBetween,
    verticalAlignment = Alignment.CenterVertically,
    content = content
  )
}

@Composable
fun RenewlyTertiaryButton(
  text: String,
  onClick: () -> Unit,
  modifier: Modifier = Modifier,
  testTag: String = "tertiary_button"
) {
  val colors = RenewlyTheme.colors
  val typography = RenewlyTheme.typography
  val interactionSource = remember { MutableInteractionSource() }
  val isPressed by interactionSource.collectIsPressedAsState()

  val bgColor = if (isPressed) colors.hover else Color.Transparent

  Box(
    modifier = modifier
      .height(48.dp)
      .clip(RoundedCornerShape(RenewlyTokens.RadiusPill))
      .background(bgColor)
      .clickable(
        interactionSource = interactionSource,
        indication = null,
        role = Role.Button,
        onClick = onClick
      )
      .padding(horizontal = 16.dp)
      .testTag(testTag),
    contentAlignment = Alignment.Center
  ) {
    Text(
      text = text,
      style = typography.buttonSmall,
      color = colors.ink2
    )
  }
}

@Composable
fun RenewlyHeaderIconButton(
  iconName: String,
  onClick: () -> Unit,
  contentDescription: String,
  modifier: Modifier = Modifier,
  testTag: String = "header_icon_button"
) {
  val colors = RenewlyTheme.colors
  val interactionSource = remember { MutableInteractionSource() }
  val isPressed by interactionSource.collectIsPressedAsState()

  val borderColor = if (isPressed) colors.accent else colors.line

  // 44x44 visual, 48x48 touch target minimum
  Box(
    modifier = modifier
      .size(48.dp)
      .testTag(testTag)
      .semantics { this.contentDescription = contentDescription },
    contentAlignment = Alignment.Center
  ) {
    Box(
      modifier = Modifier
        .size(44.dp)
        .clip(CircleShape)
        .background(colors.card)
        .border(1.dp, borderColor, CircleShape)
        .clickable(
          interactionSource = interactionSource,
          indication = null,
          role = Role.Button,
          onClick = onClick
        ),
      contentAlignment = Alignment.Center
    ) {
      RenewlyIcon(
        name = iconName,
        color = colors.ink,
        size = 17.dp
      )
    }
  }
}

@Composable
fun RenewlyInlineLink(
  text: String,
  onClick: () -> Unit,
  modifier: Modifier = Modifier,
  testTag: String = "inline_link"
) {
  val colors = RenewlyTheme.colors
  val typography = RenewlyTheme.typography
  val interactionSource = remember { MutableInteractionSource() }
  val isPressed by interactionSource.collectIsPressedAsState()

  val textColor = if (isPressed) colors.accent else colors.accentDeep

  Box(
    modifier = modifier
      .clickable(
        interactionSource = interactionSource,
        indication = null,
        role = Role.Button,
        onClick = onClick
      )
      .padding(vertical = 4.dp)
      .testTag(testTag)
  ) {
    Text(
      text = text,
      style = typography.secondarySupporting.copy(color = textColor)
    )
  }
}
