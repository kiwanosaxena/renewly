package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.data.model.Subscription
import com.example.data.repository.RenewlyCalculations
import com.example.ui.theme.CategoryColors
import com.example.ui.theme.RenewlyTheme
import com.example.ui.theme.RenewlyTokens
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun RenewlySubscriptionRow(
  subscription: Subscription,
  currency: String,
  onClick: () -> Unit,
  modifier: Modifier = Modifier,
  testTag: String = "subscription_row"
) {
  val colors = RenewlyTheme.colors
  val typography = RenewlyTheme.typography
  val interactionSource = remember { MutableInteractionSource() }
  val isPressed by interactionSource.collectIsPressedAsState()

  val isPaused = subscription.paused
  val shape = RoundedCornerShape(RenewlyTokens.RadiusTile)
  val bgColor = if (isPressed) colors.hover else Color.Transparent

  val letterColor = if (isPaused) {
    colors.barInactive
  } else {
    CategoryColors.getCategoryColor(subscription.category, colors.isDark)
  }

  val firstChar = subscription.name.firstOrNull()?.uppercase() ?: "?"
  val nextChargeDate = RenewlyCalculations.calculateNextChargeDate(subscription)
  val monD = nextChargeDate.format(DateTimeFormatter.ofPattern("MMM d", Locale.US))

  val subtitleText = if (isPaused) {
    val resumeDate = subscription.pausedUntil ?: monD
    "Paused · resumes $resumeDate"
  } else {
    "${subscription.category} · renews $monD"
  }

  val cycleWord = if (isPaused) {
    "paused"
  } else {
    subscription.cycle.lowercase()
  }

  val nameColor = if (isPaused) colors.ink3 else colors.ink
  val amountColor = if (isPaused) colors.ink3 else colors.ink

  Row(
    modifier = modifier
      .fillMaxWidth()
      .clip(shape)
      .background(bgColor)
      .clickable(
        interactionSource = interactionSource,
        indication = null,
        role = Role.Button,
        onClick = onClick
      )
      .padding(horizontal = 10.dp, vertical = 12.dp)
      .testTag(testTag),
    verticalAlignment = Alignment.CenterVertically
  ) {
    // 1. Letter tile 40 x 40, radius 14
    Box(
      modifier = Modifier
        .size(40.dp)
        .clip(RoundedCornerShape(RenewlyTokens.RadiusLetterTile40))
        .background(letterColor),
      contentAlignment = Alignment.Center
    ) {
      Text(
        text = firstChar,
        style = typography.buttonLarge,
        color = colors.onAccent
      )
    }

    Spacer(modifier = Modifier.size(13.dp))

    // 2. Middle block (flex 1)
    Column(
      modifier = Modifier.weight(1f),
      verticalArrangement = Arrangement.Center
    ) {
      Text(
        text = subscription.name,
        style = typography.listRowName,
        color = nameColor,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis
      )
      Spacer(modifier = Modifier.height(2.dp))
      Text(
        text = subtitleText,
        style = typography.rowSubtitle,
        color = colors.ink2,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis
      )
    }

    Spacer(modifier = Modifier.size(8.dp))

    // 3. Right block
    Column(
      horizontalAlignment = Alignment.End,
      verticalArrangement = Arrangement.Center
    ) {
      Text(
        text = RenewlyCalculations.formatCurrency(subscription.amount, currency),
        style = typography.listRowAmount,
        color = amountColor
      )
      Spacer(modifier = Modifier.height(2.dp))
      Text(
        text = cycleWord,
        style = typography.fieldLabel.copy(color = colors.ink3),
      )
    }
  }
}
