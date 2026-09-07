package com.example.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ChargeRecord
import com.example.data.model.Subscription
import com.example.data.model.UserSettings
import com.example.data.repository.RenewlyCalculations
import com.example.ui.RenewlyScreen
import com.example.ui.components.CategoryProgressRows
import com.example.ui.components.MonthlyHistoryBars
import com.example.ui.components.RenewlyHeroCard
import com.example.ui.components.RenewlyScreenScaffold
import com.example.ui.components.RenewlyStandardCard
import com.example.ui.components.RenewlySubscriptionRow
import com.example.ui.components.RenewlyTintedCard
import com.example.ui.components.RenewlyTitleHeader
import com.example.ui.theme.RenewlyTheme
import com.example.ui.theme.RenewlyTokens

@Composable
fun InsightsScreen(
  subscriptions: List<Subscription>,
  chargeRecords: List<ChargeRecord>,
  settings: UserSettings,
  onNavigate: (RenewlyScreen) -> Unit,
  modifier: Modifier = Modifier
) {
  val colors = RenewlyTheme.colors
  val typography = RenewlyTheme.typography

  val activeSubs = RenewlyCalculations.activeSubscriptions(subscriptions)
  val monthlyTotal = RenewlyCalculations.monthlyTotal(subscriptions)
  val annualProjection = RenewlyCalculations.annualProjection(subscriptions)
  val categoryShares = RenewlyCalculations.categoryShares(subscriptions)
  val monthlyBars = RenewlyCalculations.computeMonthlyHistoryBars(chargeRecords)

  // Find largest commitment
  val largestSub = activeSubs.maxByOrNull { RenewlyCalculations.normalizedMonthly(it) }

  // Observation heuristic
  val barelyUsedSub = activeSubs.firstOrNull { it.barelyUsed }
  val topCategory = categoryShares.firstOrNull()

  RenewlyScreenScaffold(modifier = modifier) {
    Column(
      modifier = Modifier
        .fillMaxSize()
        .statusBarsPadding()
    ) {
      // Sticky header
      RenewlyTitleHeader(
        eyebrow = "SPENDING BREAKDOWN",
        title = "Insights"
      )

      Column(
        modifier = Modifier
          .fillMaxSize()
          .verticalScroll(rememberScrollState())
          .padding(horizontal = RenewlyTokens.ScreenHorizontalGutter)
          .padding(top = 4.dp, bottom = 100.dp),
        verticalArrangement = Arrangement.spacedBy(RenewlyTokens.GapBetweenStackedCards)
      ) {
        if (activeSubs.isEmpty()) {
          RenewlyStandardCard {
            Text(
              text = "Add subscriptions to see spending insights, annual projections, and category breakdowns.",
              style = typography.bodyLarge,
              color = colors.ink2
            )
          }
        } else {
          // Card 1: ANNUAL PROJECTION
          RenewlyHeroCard {
            Column {
              Text(
                text = "ANNUAL PROJECTION",
                style = typography.microLabel,
                color = colors.ink3
              )

              Spacer(modifier = Modifier.height(8.dp))

              Text(
                text = RenewlyCalculations.formatIntegerCurrency(annualProjection, settings.currency),
                style = typography.heroAmount,
                color = colors.ink
              )

              Spacer(modifier = Modifier.height(8.dp))

              Text(
                text = "${RenewlyCalculations.formatCurrency(monthlyTotal, settings.currency)} a month · across ${activeSubs.size} active subscriptions",
                style = typography.secondarySupporting.copy(fontSize = 13.5.sp),
                color = colors.ink2
              )
            }
          }

          // Card 2: SPENDING BY CATEGORY
          if (categoryShares.isNotEmpty()) {
            RenewlyStandardCard {
              Column {
                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.SpaceBetween,
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Text(
                    text = "Spending by category",
                    style = typography.cardTitle,
                    color = colors.ink
                  )
                  Text(
                    text = "${categoryShares.size} categories",
                    style = TextStyle(fontSize = 12.sp, color = colors.ink3)
                  )
                }

                Spacer(modifier = Modifier.height(14.dp))

                CategoryProgressRows(
                  categoryShares = categoryShares,
                  currency = settings.currency
                )
              }
            }
          }

          // Card 3: MONTHLY HISTORY BARS (Rendered ONLY when >= 2 recorded months exist)
          if (monthlyBars != null && monthlyBars.size >= 2) {
            RenewlyStandardCard {
              Column {
                Text(
                  text = "Monthly spending history",
                  style = typography.cardTitle,
                  color = colors.ink
                )

                Spacer(modifier = Modifier.height(14.dp))

                MonthlyHistoryBars(
                  bars = monthlyBars,
                  currency = settings.currency
                )
              }
            }
          }

          // Card 4: LARGEST COMMITMENT
          if (largestSub != null) {
            RenewlyStandardCard {
              Column {
                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.SpaceBetween,
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Text(
                    text = "Largest commitment",
                    style = typography.cardTitle,
                    color = colors.ink
                  )
                  val share = if (monthlyTotal > 0) (RenewlyCalculations.normalizedMonthly(largestSub) / monthlyTotal * 100).toInt() else 0
                  Text(
                    text = "$share% of total",
                    style = TextStyle(fontSize = 12.sp, fontWeight = FontWeight.W600, color = colors.accentDeep)
                  )
                }

                Spacer(modifier = Modifier.height(8.dp))

                RenewlySubscriptionRow(
                  subscription = largestSub,
                  currency = settings.currency,
                  onClick = { onNavigate(RenewlyScreen.Detail(largestSub.id)) }
                )
              }
            }
          }

          // Card 5: OBSERVATION CARD
          if (barelyUsedSub != null) {
            val amt = RenewlyCalculations.formatCurrency(barelyUsedSub.amount, settings.currency)
            RenewlyTintedCard {
              Column {
                Text(
                  text = "OBSERVATION",
                  style = typography.eyebrow,
                  color = colors.accentDeep
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                  text = "You flagged ${barelyUsedSub.name} as barely used. That's $amt a month back if you cancel it.",
                  style = TextStyle(fontSize = 14.5.sp, fontWeight = FontWeight.W600, color = colors.ink)
                )
              }
            }
          } else if (topCategory != null) {
            val pct = (topCategory.sharePercentage * 100).toInt()
            RenewlyStandardCard {
              Column {
                Text(
                  text = "OBSERVATION",
                  style = typography.eyebrow,
                  color = colors.ink3
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                  text = "${topCategory.category} makes up $pct% of your regular subscription expenses.",
                  style = TextStyle(fontSize = 14.5.sp, fontWeight = FontWeight.W500, color = colors.ink2)
                )
              }
            }
          }
        }
      }
    }
  }
}
