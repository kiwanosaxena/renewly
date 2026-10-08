package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.ui.theme.RenewlyTheme
import com.example.ui.theme.RenewlyTokens

@Composable
fun RenewlyStandardCard(
  modifier: Modifier = Modifier,
  padding: Dp = RenewlyTokens.CardInnerPadding,
  testTag: String = "standard_card",
  content: @Composable BoxScope.() -> Unit
) {
  val colors = RenewlyTheme.colors
  val shape = RoundedCornerShape(RenewlyTokens.RadiusCard)

  Box(
    modifier = modifier
      .fillMaxWidth()
      .then(
        if (!colors.isDark) {
          Modifier.shadow(
            elevation = 2.dp,
            shape = shape,
            ambientColor = Color.Black.copy(alpha = 0.06f),
            spotColor = Color.Black.copy(alpha = 0.06f)
          )
        } else Modifier
      )
      .clip(shape)
      .background(colors.card)
      .border(1.dp, colors.line, shape)
      .padding(padding)
      .testTag(testTag),
    content = content
  )
}

@Composable
fun RenewlyHeroCard(
  modifier: Modifier = Modifier,
  testTag: String = "hero_card",
  content: @Composable BoxScope.() -> Unit
) {
  val colors = RenewlyTheme.colors
  val shape = RoundedCornerShape(RenewlyTokens.RadiusCard)

  Box(
    modifier = modifier
      .fillMaxWidth()
      .then(
        if (!colors.isDark) {
          Modifier.shadow(
            elevation = 3.dp,
            shape = shape,
            ambientColor = Color.Black.copy(alpha = 0.06f),
            spotColor = Color.Black.copy(alpha = 0.06f)
          )
        } else Modifier
      )
      .clip(shape)
      .background(colors.card)
      .border(1.dp, colors.line, shape)
      .padding(RenewlyTokens.HeroCardInnerPadding)
      .testTag(testTag),
    content = content
  )
}

@Composable
fun RenewlyTintedCard(
  modifier: Modifier = Modifier,
  testTag: String = "tinted_card",
  content: @Composable BoxScope.() -> Unit
) {
  val colors = RenewlyTheme.colors
  val shape = RoundedCornerShape(RenewlyTokens.RadiusCard)

  Box(
    modifier = modifier
      .fillMaxWidth()
      .clip(shape)
      .background(colors.accentTint)
      .border(1.dp, colors.accentLine, shape)
      .padding(RenewlyTokens.CardInnerPadding)
      .testTag(testTag),
    content = content
  )
}

@Composable
fun RenewlyNestedCard(
  modifier: Modifier = Modifier,
  padding: Dp = 14.dp,
  radius: Dp = RenewlyTokens.RadiusNestedCard,
  testTag: String = "nested_card",
  content: @Composable BoxScope.() -> Unit
) {
  val colors = RenewlyTheme.colors
  val shape = RoundedCornerShape(radius)

  Box(
    modifier = modifier
      .fillMaxWidth()
      .clip(shape)
      .background(colors.card2)
      .border(1.dp, colors.line, shape)
      .padding(padding)
      .testTag(testTag),
    content = content
  )
}
