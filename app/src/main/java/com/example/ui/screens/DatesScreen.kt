package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Subscription
import com.example.data.model.UserSettings
import com.example.data.repository.RenewlyCalculations
import com.example.ui.RenewlyScreen
import com.example.ui.components.RenewlyHeaderIconButton
import com.example.ui.components.RenewlyScreenScaffold
import com.example.ui.components.RenewlySegmentedControl
import com.example.ui.components.RenewlyStandardCard
import com.example.ui.components.RenewlySubscriptionRow
import com.example.ui.theme.CategoryColors
import com.example.ui.theme.RenewlyTheme
import com.example.ui.theme.RenewlyTokens
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun DatesScreen(
  subscriptions: List<Subscription>,
  settings: UserSettings,
  onNavigate: (RenewlyScreen) -> Unit,
  modifier: Modifier = Modifier
) {
  val colors = RenewlyTheme.colors
  val typography = RenewlyTheme.typography
  val today = LocalDate.now()

  var currentYearMonth by remember { mutableStateOf(YearMonth.now()) }
  var selectedDate by remember { mutableStateOf(today) }
  var selectedView by remember { mutableStateOf(if (settings.datesLayout == "timeline") "Timeline" else "Calendar") }

  val monthTitle = currentYearMonth.format(DateTimeFormatter.ofPattern("MMMM yyyy", Locale.US))
  val activeSubs = RenewlyCalculations.activeSubscriptions(subscriptions)

  // Subscriptions falling in this month
  val monthChargeMap = remember(currentYearMonth, activeSubs) {
    val map = mutableMapOf<Int, MutableList<Subscription>>()
    for (day in 1..currentYearMonth.lengthOfMonth()) {
      map[day] = mutableListOf()
    }
    for (sub in activeSubs) {
      if (sub.cycle.equals("yearly", ignoreCase = true)) {
        try {
          val first = LocalDate.parse(sub.firstCharge)
          if (first.month == currentYearMonth.month) {
            val day = sub.chargeDay.coerceAtMost(currentYearMonth.lengthOfMonth())
            map[day]?.add(sub)
          }
        } catch (e: Exception) {}
      } else {
        val day = sub.chargeDay.coerceAtMost(currentYearMonth.lengthOfMonth())
        map[day]?.add(sub)
      }
    }
    map
  }

  RenewlyScreenScaffold(modifier = modifier) {
    Column(
      modifier = Modifier
        .fillMaxSize()
        .statusBarsPadding()
    ) {
      // Sticky header (Title form with month navigation chevrons)
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
            text = "RENEWAL CALENDAR",
            style = typography.eyebrow,
            color = colors.ink3
          )
          Text(
            text = monthTitle,
            style = typography.sectionHeader,
            color = colors.ink
          )
        }

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
          RenewlyHeaderIconButton(
            iconName = "chevron-left",
            onClick = { currentYearMonth = currentYearMonth.minusMonths(1) },
            contentDescription = "Previous Month"
          )
          RenewlyHeaderIconButton(
            iconName = "chevron-right",
            onClick = { currentYearMonth = currentYearMonth.plusMonths(1) },
            contentDescription = "Next Month"
          )
        }
      }

      Column(
        modifier = Modifier
          .fillMaxSize()
          .verticalScroll(rememberScrollState())
          .padding(horizontal = RenewlyTokens.ScreenHorizontalGutter)
          .padding(top = 4.dp, bottom = 100.dp),
        verticalArrangement = Arrangement.spacedBy(RenewlyTokens.GapBetweenStackedCards)
      ) {
        // View switch: Calendar vs Timeline
        RenewlySegmentedControl(
          options = listOf("Calendar", "Timeline"),
          selectedOption = selectedView,
          onOptionSelected = { selectedView = it },
          isAddModeStyle = true
        )

        if (selectedView == "Calendar") {
          // CALENDAR GRID
          RenewlyStandardCard {
            Column {
              // Weekday headers
              val weekStartsOnMonday = settings.weekStart != "sun"
              val weekdayLabels = if (weekStartsOnMonday) {
                listOf("M", "T", "W", "T", "F", "S", "S")
              } else {
                listOf("S", "M", "T", "W", "T", "F", "S")
              }

              Row(modifier = Modifier.fillMaxWidth()) {
                weekdayLabels.forEach { label ->
                  Text(
                    text = label,
                    style = TextStyle(fontSize = 11.sp, fontWeight = FontWeight.W600, color = colors.ink3),
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center
                  )
                }
              }

              Spacer(modifier = Modifier.height(10.dp))

              // Calendar month grid
              val daysInMonth = currentYearMonth.lengthOfMonth()
              val firstDayOfMonth = currentYearMonth.atDay(1)
              val firstDayOfWeek = firstDayOfMonth.dayOfWeek // Monday is 1, Sunday is 7

              val leadingEmptyDays = if (weekStartsOnMonday) {
                firstDayOfWeek.value - 1
              } else {
                firstDayOfWeek.value % 7
              }

              val totalCells = leadingEmptyDays + daysInMonth
              val totalRows = (totalCells + 6) / 7

              Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                for (row in 0 until totalRows) {
                  Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                  ) {
                    for (col in 0 until 7) {
                      val cellIndex = row * 7 + col
                      val dayNumber = cellIndex - leadingEmptyDays + 1

                      if (dayNumber in 1..daysInMonth) {
                        val cellDate = currentYearMonth.atDay(dayNumber)
                        val isToday = cellDate == today
                        val isSelected = cellDate == selectedDate
                        val isPast = cellDate.isBefore(today)
                        val chargingSubs = monthChargeMap[dayNumber] ?: emptyList()
                        val hasCharges = chargingSubs.isNotEmpty()

                        val cellShape = RoundedCornerShape(RenewlyTokens.RadiusDayCell)
                        val cellBg = when {
                          isToday -> colors.track
                          hasCharges -> colors.card2
                          else -> Color.Transparent
                        }

                        val cellBorder = when {
                          isSelected -> 1.dp
                          hasCharges -> 1.dp
                          else -> 0.dp
                        }
                        val borderColor = when {
                          isSelected -> colors.accent
                          hasCharges -> colors.line
                          else -> Color.Transparent
                        }

                        val textColor = when {
                          isPast && !isToday -> colors.ink3
                          isToday -> colors.ink
                          hasCharges -> colors.ink
                          else -> colors.ink2
                        }

                        val fontWeight = when {
                          isSelected || isToday -> FontWeight.W700
                          hasCharges -> FontWeight.W600
                          else -> FontWeight.W400
                        }

                        Box(
                          modifier = Modifier
                            .weight(1f)
                            .aspectRatio(1f)
                            .clip(cellShape)
                            .background(cellBg)
                            .then(if (cellBorder > 0.dp) Modifier.border(cellBorder, borderColor, cellShape) else Modifier)
                            .clickable(role = Role.Button) {
                              selectedDate = cellDate
                            }
                            .padding(2.dp),
                          contentAlignment = Alignment.Center
                        ) {
                          Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                          ) {
                            Text(
                              text = dayNumber.toString(),
                              style = TextStyle(fontSize = 13.sp, fontWeight = fontWeight, color = textColor),
                              textAlign = TextAlign.Center
                            )

                            if (hasCharges) {
                              Spacer(modifier = Modifier.height(2.dp))
                              Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                                chargingSubs.take(3).forEach { sub ->
                                  val dotColor = CategoryColors.getCategoryColor(sub.category, colors.isDark)
                                  Box(
                                    modifier = Modifier
                                      .size(5.dp)
                                      .clip(CircleShape)
                                      .background(dotColor)
                                  )
                                }
                                if (chargingSubs.size > 3) {
                                  Text(
                                    text = "+",
                                    style = TextStyle(fontSize = 7.sp, fontWeight = FontWeight.W700, color = colors.ink3)
                                  )
                                }
                              }
                            }
                          }
                        }
                      } else {
                        // Empty cell
                        Box(
                          modifier = Modifier
                            .weight(1f)
                            .aspectRatio(1f)
                        )
                      }
                    }
                  }
                }
              }
            }
          }

          // Card 2: Day detail (Selected date)
          val dayName = selectedDate.format(DateTimeFormatter.ofPattern("EEEE", Locale.US))
          val monD = selectedDate.format(DateTimeFormatter.ofPattern("MMM d", Locale.US))
          val daySubs = monthChargeMap[selectedDate.dayOfMonth] ?: emptyList()

          RenewlyStandardCard {
            Column {
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Text(
                  text = "$dayName, $monD",
                  style = typography.cardTitle,
                  color = colors.ink
                )
                Text(
                  text = if (daySubs.isNotEmpty()) "${daySubs.size} charging" else "None due",
                  style = TextStyle(fontSize = 12.sp, color = colors.ink3)
                )
              }

              Spacer(modifier = Modifier.height(8.dp))

              if (daySubs.isEmpty()) {
                Text(
                  text = "Nothing scheduled on this day.",
                  style = typography.secondarySupporting,
                  color = colors.ink2
                )
              } else {
                Column {
                  daySubs.forEach { sub ->
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
        } else {
          // TIMELINE VIEW
          val timelineDates = (1..currentYearMonth.lengthOfMonth())
            .filter { (monthChargeMap[it] ?: emptyList()).isNotEmpty() }

          if (timelineDates.isEmpty()) {
            RenewlyStandardCard {
              Text(
                text = "No subscriptions renew during ${currentYearMonth.format(DateTimeFormatter.ofPattern("MMMM yyyy", Locale.US))}.",
                style = typography.bodyLarge,
                color = colors.ink2
              )
            }
          } else {
            timelineDates.forEach { dayNum ->
              val date = currentYearMonth.atDay(dayNum)
              val subs = monthChargeMap[dayNum] ?: emptyList()
              val rel = RenewlyCalculations.formatRelativeTiming(date, today)

              RenewlyStandardCard {
                Column {
                  Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                  ) {
                    Text(
                      text = date.format(DateTimeFormatter.ofPattern("EEE, MMM d", Locale.US)),
                      style = typography.cardTitle,
                      color = colors.ink
                    )
                    Text(
                      text = rel,
                      style = TextStyle(fontSize = 12.sp, fontWeight = FontWeight.W600, color = colors.accentDeep)
                    )
                  }

                  Spacer(modifier = Modifier.height(8.dp))

                  Column {
                    subs.forEach { sub ->
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
  }
}
