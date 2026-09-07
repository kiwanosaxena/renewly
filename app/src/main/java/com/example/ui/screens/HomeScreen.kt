package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ChargeRecord
import com.example.data.model.Subscription
import com.example.data.model.UserSettings
import com.example.data.repository.RenewlyCalculations
import com.example.ui.RenewlyScreen
import com.example.ui.components.RenewlyHeaderIconButton
import com.example.ui.components.RenewlyHeroCard
import com.example.ui.components.RenewlyIcon
import com.example.ui.components.RenewlyInlineLink
import com.example.ui.components.RenewlyNestedCard
import com.example.ui.components.RenewlyPrimaryButton
import com.example.ui.components.RenewlyScreenScaffold
import com.example.ui.components.RenewlyStandardCard
import com.example.ui.components.RenewlySubscriptionRow
import com.example.ui.components.RenewlyTab
import com.example.ui.components.RenewlyTertiaryButton
import com.example.ui.components.RenewlyTintedCard
import com.example.ui.components.StackedCategoryBar
import com.example.ui.theme.CategoryColors
import com.example.ui.theme.RenewlyTheme
import com.example.ui.theme.RenewlyTokens
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun HomeScreen(
  subscriptions: List<Subscription>,
  chargeRecords: List<ChargeRecord>,
  settings: UserSettings,
  onNavigate: (RenewlyScreen) -> Unit,
  onSelectTab: (RenewlyTab) -> Unit,
  modifier: Modifier = Modifier
) {
  val colors = RenewlyTheme.colors
  val typography = RenewlyTheme.typography
  val today = LocalDate.now()

  // Header Title calculation
  val currentMonthYear = today.format(DateTimeFormatter.ofPattern("MMMM yyyy", Locale.US))
  val greetingTitle = if (settings.userName.isNotBlank()) {
    val hour = LocalTime.now().hour
    val timeGreeting = when {
      hour in 5..11 -> "Morning"
      hour in 12..16 -> "Afternoon"
      else -> "Evening"
    }
    "$timeGreeting, ${settings.userName.trim()}"
  } else {
    "Your subscriptions"
  }

  // Check if any reminder is scheduled in next 7 days
  val activeSubs = RenewlyCalculations.activeSubscriptions(subscriptions)
  val hasUpcomingReminderIn7Days = activeSubs.any { sub ->
    if (!sub.remind) false
    else {
      val next = RenewlyCalculations.calculateNextChargeDate(sub, today)
      val days = java.time.temporal.ChronoUnit.DAYS.between(today, next)
      days in 0..7
    }
  }

  RenewlyScreenScaffold(modifier = modifier) {
    Column(
      modifier = Modifier
        .fillMaxSize()
        .statusBarsPadding()
    ) {
      // Sticky header (Title form)
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .background(colors.sticky)
          .padding(
            horizontal = RenewlyTokens.StickyHeaderHorizontal,
            vertical = RenewlyTokens.StickyHeaderVertical
          ),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column(modifier = Modifier.weight(1f)) {
          Text(
            text = currentMonthYear.uppercase(),
            style = typography.eyebrow,
            color = colors.ink3
          )
          Text(
            text = greetingTitle,
            style = typography.sectionHeader,
            color = colors.ink
          )
        }

        Row(
          horizontalArrangement = Arrangement.spacedBy(8.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          // Bell button 42x42 circle
          Box(
            modifier = Modifier.size(48.dp),
            contentAlignment = Alignment.Center
          ) {
            val bellSource = remember { MutableInteractionSource() }
            val isBellPressed by bellSource.collectIsPressedAsState()
            Box(
              modifier = Modifier
                .size(42.dp)
                .clip(CircleShape)
                .background(colors.card)
                .border(1.dp, if (isBellPressed) colors.accent else colors.line, CircleShape)
                .clickable(
                  interactionSource = bellSource,
                  indication = null,
                  role = Role.Button
                ) {
                  onSelectTab(RenewlyTab.Dates)
                },
              contentAlignment = Alignment.Center
            ) {
              RenewlyIcon(name = "bell", color = colors.ink, size = 18.dp)

              if (hasUpcomingReminderIn7Days) {
                Box(
                  modifier = Modifier
                    .align(Alignment.TopEnd)
                    .offset(x = (-2).dp, y = 2.dp)
                    .size(9.dp)
                    .clip(CircleShape)
                    .background(colors.accent)
                )
              }
            }
          }

          // Settings header button
          RenewlyHeaderIconButton(
            iconName = "pencil",
            onClick = { onNavigate(RenewlyScreen.Settings) },
            contentDescription = "Settings"
          )
        }
      }

      // Scroll container with 14dp horizontal padding and 14dp gap between cards
      Column(
        modifier = Modifier
          .fillMaxSize()
          .verticalScroll(rememberScrollState())
          .padding(horizontal = RenewlyTokens.ScreenHorizontalGutter)
          .padding(top = 4.dp, bottom = 100.dp),
        verticalArrangement = Arrangement.spacedBy(RenewlyTokens.GapBetweenStackedCards)
      ) {
        if (subscriptions.isEmpty()) {
          // EMPTY STATE (Section 5.1): Cards 1-4 are absent
          // Card A
          RenewlyStandardCard(padding = 22.dp) {
            Column {
              // 3 decorative placeholder tiles in a row (34x44, radius 12, opacity 1, 0.65, 0.35)
              Row(horizontalArrangement = Arrangement.spacedBy(7.dp)) {
                Box(
                  modifier = Modifier
                    .size(width = 34.dp, height = 44.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(colors.track.copy(alpha = 1f))
                )
                Box(
                  modifier = Modifier
                    .size(width = 34.dp, height = 44.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(colors.track.copy(alpha = 0.65f))
                )
                Box(
                  modifier = Modifier
                    .size(width = 34.dp, height = 44.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(colors.track.copy(alpha = 0.35f))
                )
              }

              Spacer(modifier = Modifier.height(20.dp))

              Text(
                text = "Nothing tracked yet — and that's the hard part.",
                style = TextStyle(
                  fontSize = 26.sp,
                  fontWeight = FontWeight.W700,
                  letterSpacing = (-0.03).sp,
                  lineHeight = (26 * 1.18).sp
                ),
                color = colors.ink
              )

              Spacer(modifier = Modifier.height(12.dp))

              Text(
                text = "Renewly only knows what you tell it. Start with the two or three you're sure about — you can add the rest as they charge you.",
                style = typography.bodyLarge,
                color = colors.ink2
              )

              Spacer(modifier = Modifier.height(20.dp))

              RenewlyPrimaryButton(
                text = "Add your first one",
                onClick = { onSelectTab(RenewlyTab.Add) },
                showArrow = true
              )

              Spacer(modifier = Modifier.height(8.dp))

              RenewlyTertiaryButton(
                text = "How it works, quickly ->",
                onClick = { onNavigate(RenewlyScreen.Onboarding) },
                modifier = Modifier.align(Alignment.CenterHorizontally)
              )
            }
          }

          // Card B
          RenewlyNestedCard(padding = 18.dp, radius = 24.dp) {
            Column {
              Text(
                text = "HOW THIS WORKS",
                style = typography.eyebrow,
                color = colors.ink3
              )
              Spacer(modifier = Modifier.height(8.dp))
              Text(
                text = "Renewly does no guessing. It shows the amounts you enter and the arithmetic on top of them — nothing estimated, nothing fetched, nothing sent anywhere.",
                style = typography.bodyLarge,
                color = colors.ink2
              )
            }
          }
        } else {
          // Card 1: HERO
          val monthlyTotal = RenewlyCalculations.monthlyTotal(subscriptions)
          val nextDueSub = activeSubs.minByOrNull {
            RenewlyCalculations.calculateNextChargeDate(it, today)
          }

          if (settings.homeHero == "renewal" && nextDueSub != null) {
            // Variant B ("next renewal" preference)
            val nextDate = RenewlyCalculations.calculateNextChargeDate(nextDueSub, today)
            val relTiming = RenewlyCalculations.formatRelativeTimingTitle(nextDate, today)
            val dueThisWeek = activeSubs.filter {
              val d = RenewlyCalculations.calculateNextChargeDate(it, today)
              java.time.temporal.ChronoUnit.DAYS.between(today, d) in 0..7
            }.sumOf { it.amount }

            RenewlyHeroCard {
              Column {
                Box(
                  modifier = Modifier
                    .clip(RoundedCornerShape(999.dp))
                    .background(colors.accentTint)
                    .padding(horizontal = 12.dp, vertical = 5.dp)
                ) {
                  Text(
                    text = relTiming.replaceFirstChar { it.uppercase() },
                    style = TextStyle(fontSize = 11.5.sp, fontWeight = FontWeight.W600, color = colors.accentDeep)
                  )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                  text = nextDueSub.name,
                  style = TextStyle(fontSize = 34.sp, fontWeight = FontWeight.W700, letterSpacing = (-0.03).sp),
                  color = colors.ink
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                  Text(
                    text = RenewlyCalculations.formatCurrency(nextDueSub.amount, settings.currency),
                    style = typography.heroSecondaryAmount,
                    color = colors.ink
                  )
                  Spacer(modifier = Modifier.width(10.dp))
                  Text(
                    text = "${RenewlyCalculations.formatCurrency(dueThisWeek, settings.currency)} due this week",
                    style = typography.secondarySupporting,
                    color = colors.ink2
                  )
                }

                Spacer(modifier = Modifier.height(18.dp))

                // 16dp padded divider-free block
                Box(
                  modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(RenewlyTokens.RadiusNestedCard))
                    .background(colors.card2)
                    .padding(16.dp)
                ) {
                  Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                  ) {
                    Text(
                      text = "MONTHLY TOTAL",
                      style = typography.microLabel,
                      color = colors.ink3
                    )
                    Text(
                      text = RenewlyCalculations.formatCurrency(monthlyTotal, settings.currency),
                      style = TextStyle(fontSize = 22.sp, fontWeight = FontWeight.W700),
                      color = colors.ink
                    )
                  }
                }
              }
            }
          } else {
            // Variant A (default)
            val nextDate = nextDueSub?.let { RenewlyCalculations.calculateNextChargeDate(it, today) }
            val nextDateStr = if (nextDate != null) {
              val days = java.time.temporal.ChronoUnit.DAYS.between(today, nextDate)
              if (days in 0..31) {
                "next charge ${RenewlyCalculations.formatRelativeTiming(nextDate, today)}"
              } else {
                "nothing due this month"
              }
            } else {
              "nothing due this month"
            }

            val momDiff = RenewlyCalculations.computeMonthOverMonth(chargeRecords, monthlyTotal)
            val categoryShares = RenewlyCalculations.categoryShares(subscriptions)

            RenewlyHeroCard {
              Column {
                Text(
                  text = "MONTHLY COMMITMENT",
                  style = typography.microLabel,
                  color = colors.ink3
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                  text = RenewlyCalculations.formatCurrency(monthlyTotal, settings.currency),
                  style = typography.heroAmount,
                  color = colors.ink
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                  text = "${activeSubs.size} active · $nextDateStr",
                  style = typography.secondarySupporting.copy(fontSize = 13.5.sp),
                  color = colors.ink2
                )

                // Month-over-month chip: rendered ONLY when two full months of records exist
                if (momDiff != null) {
                  Spacer(modifier = Modifier.height(8.dp))
                  val sign = if (momDiff >= 0) "+" else "-"
                  val absDiffStr = RenewlyCalculations.formatCurrency(kotlin.math.abs(momDiff), settings.currency)
                  val lastMonthName = today.minusMonths(1).format(DateTimeFormatter.ofPattern("MMM", Locale.US))
                  Box(
                    modifier = Modifier
                      .clip(RoundedCornerShape(999.dp))
                      .background(colors.accentTint)
                      .padding(horizontal = 10.dp, vertical = 4.dp)
                  ) {
                    Text(
                      text = "$sign$absDiffStr vs $lastMonthName",
                      style = TextStyle(fontSize = 12.sp, fontWeight = FontWeight.W600, color = colors.accentDeep)
                    )
                  }
                }

                if (categoryShares.isNotEmpty()) {
                  Spacer(modifier = Modifier.height(18.dp))
                  StackedCategoryBar(
                    categoryShares = categoryShares,
                    currency = settings.currency
                  )
                }
              }
            }
          }

          // Card 2: NEXT 7 DAYS
          val next7DaySubs = activeSubs.filter { sub ->
            val d = RenewlyCalculations.calculateNextChargeDate(sub, today)
            val diff = java.time.temporal.ChronoUnit.DAYS.between(today, d)
            diff in 0..7
          }.sortedBy { RenewlyCalculations.calculateNextChargeDate(it, today) }

          RenewlyStandardCard(padding = 16.dp) {
            Column {
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Text(
                  text = "Next 7 days",
                  style = typography.cardTitle,
                  color = colors.ink
                )
                RenewlyInlineLink(
                  text = "All dates",
                  onClick = { onSelectTab(RenewlyTab.Dates) }
                )
              }

              Spacer(modifier = Modifier.height(14.dp))

              if (next7DaySubs.isEmpty()) {
                Text(
                  text = "Nothing charges this week.",
                  style = typography.secondarySupporting.copy(fontSize = 13.5.sp),
                  color = colors.ink2
                )
              } else {
                Row(
                  modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                  horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                  next7DaySubs.forEach { sub ->
                    val nextDate = RenewlyCalculations.calculateNextChargeDate(sub, today)
                    val relTiming = RenewlyCalculations.formatRelativeTiming(nextDate, today)
                    val catColor = CategoryColors.getCategoryColor(sub.category, colors.isDark)

                    val cardSource = remember { MutableInteractionSource() }
                    val isCardPressed by cardSource.collectIsPressedAsState()

                    Box(
                      modifier = Modifier
                        .width(134.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(colors.card2)
                        .border(1.dp, if (isCardPressed) colors.accent else colors.line, RoundedCornerShape(16.dp))
                        .clickable(
                          interactionSource = cardSource,
                          indication = null,
                          role = Role.Button
                        ) {
                          onNavigate(RenewlyScreen.Detail(sub.id))
                        }
                        .padding(14.dp)
                    ) {
                      Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                          Box(
                            modifier = Modifier
                              .size(8.dp)
                              .clip(CircleShape)
                              .background(catColor)
                          )
                          Spacer(modifier = Modifier.width(7.dp))
                          Text(
                            text = relTiming,
                            style = TextStyle(fontSize = 11.sp, fontWeight = FontWeight.W600, color = colors.accentDeep)
                          )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                          text = sub.name,
                          style = TextStyle(fontSize = 14.5.sp, fontWeight = FontWeight.W600),
                          color = colors.ink,
                          maxLines = 1
                        )

                        Spacer(modifier = Modifier.height(3.dp))

                        Text(
                          text = RenewlyCalculations.formatCurrency(sub.amount, settings.currency),
                          style = TextStyle(fontSize = 19.sp, fontWeight = FontWeight.W700, letterSpacing = (-0.02).sp),
                          color = colors.ink
                        )
                      }
                    }
                  }
                }
              }
            }
          }

          // Card 3: WORTH A SECOND LOOK (Rendered ONLY when §6.3 produces entries!)
          val worthASecondLook = RenewlyCalculations.computeWorthASecondLook(subscriptions, settings.currency)
          if (worthASecondLook != null && worthASecondLook.items.isNotEmpty()) {
            val totalStr = RenewlyCalculations.formatCurrency(worthASecondLook.totalAmount, settings.currency)
            val countStr = if (worthASecondLook.items.size == 1) "1 subscription" else "${worthASecondLook.items.size} subscriptions"

            RenewlyTintedCard {
              Column {
                Text(
                  text = "WORTH A SECOND LOOK",
                  style = typography.eyebrow,
                  color = colors.accentDeep
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                  text = "$totalStr a month across $countStr worth a second look",
                  style = TextStyle(fontSize = 19.sp, fontWeight = FontWeight.W700, letterSpacing = (-0.025).sp),
                  color = colors.ink
                )

                Spacer(modifier = Modifier.height(14.dp))

                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                  worthASecondLook.items.forEach { item ->
                    val itemSource = remember { MutableInteractionSource() }
                    Box(
                      modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(colors.rowOnTint)
                        .clickable(
                          interactionSource = itemSource,
                          indication = null,
                          role = Role.Button
                        ) {
                          onNavigate(RenewlyScreen.Detail(item.subscription.id))
                        }
                        .padding(horizontal = 14.dp, vertical = 13.dp)
                    ) {
                      Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                      ) {
                        Column(modifier = Modifier.weight(1f)) {
                          Text(
                            text = item.subscription.name,
                            style = TextStyle(fontSize = 14.sp, fontWeight = FontWeight.W600, color = colors.ink)
                          )
                          Spacer(modifier = Modifier.height(2.dp))
                          Text(
                            text = item.reason,
                            style = TextStyle(fontSize = 12.sp, color = colors.ink2)
                          )
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                          Text(
                            text = RenewlyCalculations.formatCurrency(item.subscription.amount, settings.currency),
                            style = TextStyle(fontSize = 14.5.sp, fontWeight = FontWeight.W700, color = colors.ink)
                          )
                          Spacer(modifier = Modifier.width(8.dp))
                          RenewlyIcon(
                            name = "chevron-right",
                            color = colors.ink2,
                            size = 16.dp
                          )
                        }
                      }
                    }
                  }
                }
              }
            }
          }

          // Card 4: ALL SUBSCRIPTIONS
          val sortedSubs = subscriptions.sortedWith(
            compareBy<Subscription> { it.paused }
              .thenBy { RenewlyCalculations.calculateNextChargeDate(it, today) }
              .thenBy { it.name.lowercase() }
          )

          RenewlyStandardCard {
            Column {
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Text(
                  text = "All subscriptions",
                  style = typography.cardTitle,
                  color = colors.ink
                )
                Text(
                  text = "${activeSubs.size} active",
                  style = TextStyle(fontSize = 12.sp, color = colors.ink3)
                )
              }

              Spacer(modifier = Modifier.height(6.dp))

              Column(verticalArrangement = Arrangement.spacedBy(0.dp)) {
                sortedSubs.forEach { sub ->
                  RenewlySubscriptionRow(
                    subscription = sub,
                    currency = settings.currency,
                    onClick = { onNavigate(RenewlyScreen.Detail(sub.id)) }
                  )
                }
              }
            }
          }
        }
      }
    }
  }
}
