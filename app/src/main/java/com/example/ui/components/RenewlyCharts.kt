package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ChargeRecord
import com.example.data.repository.CategoryShare
import com.example.data.repository.MonthHistoryBarData
import com.example.data.repository.RenewlyCalculations
import com.example.ui.theme.CategoryColors
import com.example.ui.theme.RenewlyTheme
import com.example.ui.theme.RenewlyTokens

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun StackedCategoryBar(
  categoryShares: List<CategoryShare>,
  currency: String,
  modifier: Modifier = Modifier
) {
  val colors = RenewlyTheme.colors
  val typography = RenewlyTheme.typography

  if (categoryShares.isEmpty()) return

  Column(modifier = modifier.fillMaxWidth()) {
    // Height 14, full width, 3dp gap between segments, radius 999
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .height(14.dp),
      horizontalArrangement = Arrangement.spacedBy(3.dp)
    ) {
      categoryShares.forEach { share ->
        val catColor = CategoryColors.getCategoryColor(share.category, colors.isDark)
        val weight = share.sharePercentage.coerceAtLeast(0.01).toFloat()
        Box(
          modifier = Modifier
            .weight(weight)
            .fillMaxHeight()
            .clip(RoundedCornerShape(RenewlyTokens.RadiusProgressTrack))
            .background(catColor)
        )
      }
    }

    Spacer(modifier = Modifier.height(14.dp))

    // Wrapping legend at 8 horizontal / 14 vertical gaps
    FlowRow(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.spacedBy(14.dp),
      verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
      categoryShares.forEach { share ->
        val catColor = CategoryColors.getCategoryColor(share.category, colors.isDark)
        Row(
          verticalAlignment = Alignment.CenterVertically
        ) {
          Box(
            modifier = Modifier
              .size(9.dp)
              .clip(CircleShape)
              .background(catColor)
          )
          Spacer(modifier = Modifier.width(7.dp))
          Text(
            text = share.category,
            style = TextStyle(fontSize = 12.sp, fontWeight = FontWeight.W600, color = colors.ink)
          )
          Spacer(modifier = Modifier.width(4.dp))
          Text(
            text = RenewlyCalculations.formatCurrency(share.total, currency),
            style = TextStyle(fontSize = 12.sp, fontWeight = FontWeight.W500, color = colors.ink3)
          )
        }
      }
    }
  }
}

@Composable
fun CategoryProgressRows(
  categoryShares: List<CategoryShare>,
  currency: String,
  modifier: Modifier = Modifier
) {
  val colors = RenewlyTheme.colors
  if (categoryShares.isEmpty()) return

  val maxTotal = categoryShares.maxOfOrNull { it.total } ?: 1.0

  Column(modifier = modifier.fillMaxWidth()) {
    categoryShares.forEach { share ->
      val catColor = CategoryColors.getCategoryColor(share.category, colors.isDark)
      val percentText = "${(share.sharePercentage * 100).toInt()}%"

      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(vertical = 11.dp)
      ) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          // Left: 9x9 dot + category name
          Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
              modifier = Modifier
                .size(9.dp)
                .clip(CircleShape)
                .background(catColor)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
              text = share.category,
              style = TextStyle(fontSize = 14.sp, fontWeight = FontWeight.W600, color = colors.ink)
            )
          }

          // Right: amount + share %
          Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
              text = RenewlyCalculations.formatCurrency(share.total, currency),
              style = TextStyle(fontSize = 15.sp, fontWeight = FontWeight.W700, color = colors.ink)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
              text = percentText,
              style = TextStyle(fontSize = 11.5.sp, fontWeight = FontWeight.W500, color = colors.ink3),
              modifier = Modifier.width(34.dp),
              textAlign = TextAlign.End
            )
          }
        }

        Spacer(modifier = Modifier.height(9.dp))

        // Track height 7, radius 999
        val barFraction = if (maxTotal > 0) (share.total / maxTotal).coerceIn(0.02, 1.0).toFloat() else 0.02f
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .height(7.dp)
            .clip(RoundedCornerShape(RenewlyTokens.RadiusProgressTrack))
            .background(colors.track)
        ) {
          Box(
            modifier = Modifier
              .fillMaxWidth(barFraction)
              .fillMaxHeight()
              .clip(RoundedCornerShape(RenewlyTokens.RadiusProgressTrack))
              .background(catColor)
          )
        }
      }
    }
  }
}

@Composable
fun MonthlyHistoryBars(
  bars: List<MonthHistoryBarData>,
  currency: String,
  modifier: Modifier = Modifier
) {
  val colors = RenewlyTheme.colors
  if (bars.size < 2) return

  val maxAmount = bars.maxOfOrNull { it.amount }?.coerceAtLeast(1.0) ?: 1.0

  Column(modifier = modifier.fillMaxWidth()) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .height(126.dp),
      horizontalArrangement = Arrangement.spacedBy(10.dp),
      verticalAlignment = Alignment.Bottom
    ) {
      bars.forEach { bar ->
        val heightFraction = (bar.amount / maxAmount).coerceIn(0.08, 1.0).toFloat()
        val barColor = if (bar.isCurrentMonth) colors.accent else colors.barInactive

        Column(
          modifier = Modifier
            .weight(1f)
            .fillMaxHeight(),
          horizontalAlignment = Alignment.CenterHorizontally,
          verticalArrangement = Arrangement.Bottom
        ) {
          Text(
            text = RenewlyCalculations.formatIntegerCurrency(bar.amount, currency),
            style = TextStyle(fontSize = 10.5.sp, fontWeight = FontWeight.W600, color = colors.ink2),
            textAlign = TextAlign.Center
          )
          Spacer(modifier = Modifier.height(7.dp))
          Box(
            modifier = Modifier
              .fillMaxWidth()
              .fillMaxHeight(heightFraction)
              .clip(RoundedCornerShape(RenewlyTokens.RadiusChartBarInsights))
              .background(barColor)
          )
        }
      }
    }

    Spacer(modifier = Modifier.height(8.dp))

    // Month labels below container
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
      bars.forEach { bar ->
        Text(
          text = bar.displayMonth,
          style = TextStyle(fontSize = 10.5.sp, fontWeight = FontWeight.W600, color = colors.ink3),
          modifier = Modifier.weight(1f),
          textAlign = TextAlign.Center
        )
      }
    }
  }
}

@Composable
fun DetailHistoryBars(
  records: List<ChargeRecord>,
  categoryColor: Color,
  currency: String,
  modifier: Modifier = Modifier
) {
  val colors = RenewlyTheme.colors
  if (records.size < 2) return

  val sorted = records.sortedBy { it.date }
  val maxAmount = sorted.maxOfOrNull { it.amount }?.coerceAtLeast(1.0) ?: 1.0

  Column(modifier = modifier.fillMaxWidth()) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .height(92.dp),
      horizontalArrangement = Arrangement.spacedBy(7.dp),
      verticalAlignment = Alignment.Bottom
    ) {
      sorted.forEach { record ->
        val heightFraction = (record.amount / maxAmount).coerceIn(0.1, 1.0).toFloat()
        Column(
          modifier = Modifier
            .weight(1f)
            .fillMaxHeight(),
          horizontalAlignment = Alignment.CenterHorizontally,
          verticalArrangement = Arrangement.Bottom
        ) {
          Box(
            modifier = Modifier
              .fillMaxWidth()
              .fillMaxHeight(heightFraction)
              .clip(RoundedCornerShape(RenewlyTokens.RadiusChartBarDetail))
              .background(categoryColor)
          )
        }
      }
    }

    Spacer(modifier = Modifier.height(8.dp))

    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween
    ) {
      Text(
        text = sorted.first().date,
        style = TextStyle(fontSize = 11.sp, fontWeight = FontWeight.W500, color = colors.ink3)
      )
      Text(
        text = sorted.last().date,
        style = TextStyle(fontSize = 11.sp, fontWeight = FontWeight.W500, color = colors.ink3)
      )
    }
  }
}
