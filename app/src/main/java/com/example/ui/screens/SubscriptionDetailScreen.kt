package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ChargeRecord
import com.example.data.model.Subscription
import com.example.data.model.UserSettings
import com.example.data.repository.RenewlyCalculations
import com.example.ui.RenewlyScreen
import com.example.ui.components.CancelBottomSheet
import com.example.ui.components.DetailHistoryBars
import com.example.ui.components.RenewlyBackHeader
import com.example.ui.components.RenewlyHeroCard
import com.example.ui.components.RenewlyIcon
import com.example.ui.components.RenewlyInlineLink
import com.example.ui.components.RenewlyScreenScaffold
import com.example.ui.components.RenewlySecondaryCardButton
import com.example.ui.components.RenewlyStandardCard
import com.example.ui.components.RenewlyToggle
import com.example.ui.theme.CategoryColors
import com.example.ui.theme.RenewlyTheme
import com.example.ui.theme.RenewlyTokens
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun SubscriptionDetailScreen(
  subscriptionId: String,
  subscriptions: List<Subscription>,
  chargeRecords: List<ChargeRecord>,
  settings: UserSettings,
  onNavigate: (RenewlyScreen) -> Unit,
  onBack: () -> Unit,
  onToggleReminder: (Subscription) -> Unit,
  onPauseThreeMonths: (Subscription, String) -> Unit,
  onResumeSubscription: (Subscription) -> Unit,
  onConfirmCancel: (Subscription, String) -> Unit,
  modifier: Modifier = Modifier
) {
  val colors = RenewlyTheme.colors
  val typography = RenewlyTheme.typography
  val today = LocalDate.now()

  val subscription = subscriptions.firstOrNull { it.id == subscriptionId }
  if (subscription == null) {
    onBack()
    return
  }

  var showCancelSheet by remember { mutableStateOf(false) }

  val isPaused = subscription.paused
  val isCancelled = subscription.cancelledAt != null
  val nextCharge = RenewlyCalculations.calculateNextChargeDate(subscription, today)
  val nextChargeFormatted = nextCharge.format(DateTimeFormatter.ofPattern("EEE, MMM d", Locale.US))
  val relTiming = RenewlyCalculations.formatRelativeTiming(nextCharge, today)

  val annualAmountStr = RenewlyCalculations.formatCurrency(
    RenewlyCalculations.normalizedMonthly(subscription) * 12,
    settings.currency
  )

  val categoryColor = if (isPaused) {
    colors.barInactive
  } else {
    CategoryColors.getCategoryColor(subscription.category, colors.isDark)
  }

  // Filter charge records for this subscription
  val subRecords = chargeRecords.filter { it.subscriptionId == subscription.id }

  RenewlyScreenScaffold(modifier = modifier) {
    Column(
      modifier = Modifier
        .fillMaxSize()
        .statusBarsPadding()
    ) {
      // Sticky header (Back form)
      RenewlyBackHeader(
        title = subscription.category.uppercase(),
        isCategoryName = true,
        onBack = onBack,
        rightContent = {
          RenewlyInlineLink(
            text = "Edit",
            onClick = { onNavigate(RenewlyScreen.Edit(subscription.id)) }
          )
        }
      )

      Column(
        modifier = Modifier
          .fillMaxSize()
          .verticalScroll(rememberScrollState())
          .padding(horizontal = RenewlyTokens.ScreenHorizontalGutter)
          .padding(top = 4.dp, bottom = 100.dp),
        verticalArrangement = Arrangement.spacedBy(RenewlyTokens.GapBetweenStackedCards)
      ) {
        // Card 1: HERO
        RenewlyHeroCard {
          Column {
            Row(
              modifier = Modifier.fillMaxWidth(),
              verticalAlignment = Alignment.CenterVertically
            ) {
              // 58 x 58 letter tile, radius 20
              Box(
                modifier = Modifier
                  .size(58.dp)
                  .clip(RoundedCornerShape(RenewlyTokens.RadiusLetterTile58))
                  .background(categoryColor),
                contentAlignment = Alignment.Center
              ) {
                Text(
                  text = subscription.name.firstOrNull()?.uppercase() ?: "?",
                  style = TextStyle(fontSize = 24.sp, fontWeight = FontWeight.W700),
                  color = colors.onAccent
                )
              }

              Spacer(modifier = Modifier.width(14.dp))

              Column(modifier = Modifier.weight(1f)) {
                Text(
                  text = subscription.name,
                  style = typography.screenHeadline,
                  color = if (isPaused) colors.ink3 else colors.ink
                )
                Spacer(modifier = Modifier.height(4.dp))
                val cyclePillText = when {
                  isCancelled -> "Cancelled"
                  isPaused -> "Paused"
                  subscription.cycle.equals("yearly", ignoreCase = true) -> "Yearly"
                  else -> "Monthly"
                }
                Box(
                  modifier = Modifier
                    .clip(RoundedCornerShape(999.dp))
                    .background(colors.track)
                    .padding(horizontal = 9.dp, vertical = 3.dp)
                ) {
                  Text(
                    text = cyclePillText,
                    style = TextStyle(fontSize = 11.sp, fontWeight = FontWeight.W600, color = colors.ink3)
                  )
                }
              }
            }

            Spacer(modifier = Modifier.height(18.dp))

            Text(
              text = RenewlyCalculations.formatCurrency(subscription.amount, settings.currency),
              style = typography.heroAmount,
              color = if (isPaused) colors.ink3 else colors.ink
            )

            Spacer(modifier = Modifier.height(6.dp))

            val projectionText = when {
              isCancelled -> "Cancelled on ${subscription.cancelledAt}"
              isPaused -> "Paused · resumes ${subscription.pausedUntil ?: "later"}"
              else -> "$annualAmountStr a year at today's rate"
            }
            Text(
              text = projectionText,
              style = typography.secondarySupporting,
              color = colors.ink2
            )
          }
        }

        // Card 2: SCHEDULE & REMINDERS
        RenewlyStandardCard {
          Column {
            // Row 1: Next charge date
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Text(
                text = "Next charge date",
                style = TextStyle(fontSize = 14.sp, fontWeight = FontWeight.W500, color = colors.ink2)
              )
              Text(
                text = "$nextChargeFormatted ($relTiming)",
                style = TextStyle(fontSize = 14.sp, fontWeight = FontWeight.W600, color = colors.ink)
              )
            }

            Spacer(modifier = Modifier.height(12.dp))
            HorizontalDivider(color = colors.line, thickness = 1.dp)
            Spacer(modifier = Modifier.height(12.dp))

            // Row 2: Reminder toggle row
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Column(modifier = Modifier.weight(1f)) {
                Text(
                  text = "Reminder alert",
                  style = TextStyle(fontSize = 15.sp, fontWeight = FontWeight.W600, color = colors.ink)
                )
                Spacer(modifier = Modifier.height(2.dp))
                val leadStr = if (subscription.remindDays == 1) "1 day" else "${subscription.remindDays} days"
                var helperMsg = "At 09:00, $leadStr before renewal"
                if (settings.quietHoursEnabled) {
                  helperMsg += " · delivered at ${settings.quietHoursEnd}:00 (quiet hours)"
                }
                Text(
                  text = helperMsg,
                  style = TextStyle(fontSize = 12.5.sp, color = colors.ink2)
                )
              }

              RenewlyToggle(
                checked = subscription.remind,
                onCheckedChange = { onToggleReminder(subscription) }
              )
            }

            // Row 3: Note (if present)
            if (subscription.note.isNotBlank()) {
              Spacer(modifier = Modifier.height(12.dp))
              HorizontalDivider(color = colors.line, thickness = 1.dp)
              Spacer(modifier = Modifier.height(12.dp))

              Column {
                Text(
                  text = "NOTE",
                  style = typography.microLabel,
                  color = colors.ink3
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                  text = subscription.note,
                  style = TextStyle(fontSize = 13.5.sp, color = colors.ink2)
                )
              }
            }
          }
        }

        // Card 3: PAID SO FAR (Rendered ONLY when >= 2 ChargeRecords exist)
        if (subRecords.size >= 2) {
          val totalPaid = subRecords.sumOf { it.amount }
          RenewlyStandardCard {
            Column {
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Text(
                  text = "Paid so far",
                  style = typography.cardTitle,
                  color = colors.ink
                )
                Text(
                  text = RenewlyCalculations.formatCurrency(totalPaid, settings.currency),
                  style = TextStyle(fontSize = 16.sp, fontWeight = FontWeight.W700, color = colors.ink)
                )
              }

              Spacer(modifier = Modifier.height(14.dp))

              DetailHistoryBars(
                records = subRecords,
                categoryColor = categoryColor,
                currency = settings.currency
              )
            }
          }
        }

        // ACTION BUTTONS
        // Button 1: Edit name, amount or date
        RenewlySecondaryCardButton(
          onClick = { onNavigate(RenewlyScreen.Edit(subscription.id)) }
        ) {
          Text(
            text = "Edit name, amount or date",
            style = typography.buttonMedium,
            color = colors.ink
          )
          RenewlyIcon(name = "chevron-right", color = colors.ink2, size = 16.dp)
        }

        // Button 2: Pause / Resume subscription
        if (isPaused) {
          RenewlySecondaryCardButton(
            onClick = { onResumeSubscription(subscription) }
          ) {
            Text(
              text = "Resume subscription",
              style = typography.buttonMedium,
              color = colors.accentDeep
            )
            RenewlyIcon(name = "chevron-right", color = colors.accentDeep, size = 16.dp)
          }
        } else if (!isCancelled) {
          RenewlySecondaryCardButton(
            onClick = {
              val resumeMonth = LocalDate.now().plusMonths(3).format(DateTimeFormatter.ofPattern("MMMM", Locale.US))
              onPauseThreeMonths(subscription, resumeMonth)
            }
          ) {
            Text(
              text = "Pause subscription (3 months)",
              style = typography.buttonMedium,
              color = colors.ink
            )
            RenewlyIcon(name = "chevron-right", color = colors.ink2, size = 16.dp)
          }
        }

        // Button 3: Cancel subscription (Destructive: accent-deep)
        if (!isCancelled) {
          RenewlySecondaryCardButton(
            onClick = { showCancelSheet = true }
          ) {
            Text(
              text = "Cancel subscription",
              style = typography.buttonMedium,
              color = colors.accentDeep
            )
            RenewlyIcon(name = "chevron-right", color = colors.accentDeep, size = 16.dp)
          }
        }
      }
    }

    if (showCancelSheet) {
      CancelBottomSheet(
        subscription = subscription,
        currency = settings.currency,
        onDismiss = { showCancelSheet = false },
        onPauseThreeMonths = { monthName ->
          showCancelSheet = false
          onPauseThreeMonths(subscription, monthName)
        },
        onConfirmCancel = { annualSavings ->
          showCancelSheet = false
          onConfirmCancel(subscription, annualSavings)
        }
      )
    }
  }
}
