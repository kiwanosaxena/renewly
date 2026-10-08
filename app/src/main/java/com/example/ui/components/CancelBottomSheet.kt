package com.example.ui.components

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Subscription
import com.example.data.repository.RenewlyCalculations
import com.example.ui.theme.RenewlyTheme
import com.example.ui.theme.RenewlyTokens
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CancelBottomSheet(
  subscription: Subscription,
  currency: String,
  onDismiss: () -> Unit,
  onPauseThreeMonths: (resumeMonthName: String) -> Unit,
  onConfirmCancel: (annualSavingsStr: String) -> Unit
) {
  val colors = RenewlyTheme.colors
  val typography = RenewlyTheme.typography
  val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
  var step by remember { mutableIntStateOf(1) }

  val nextCharge = RenewlyCalculations.calculateNextChargeDate(subscription)
  val monD = nextCharge.format(DateTimeFormatter.ofPattern("MMM d", Locale.US))
  val resumeMonth = LocalDate.now().plusMonths(3).format(DateTimeFormatter.ofPattern("MMMM", Locale.US))
  val annualAmount = RenewlyCalculations.formatCurrency(RenewlyCalculations.normalizedMonthly(subscription) * 12, currency)

  BackHandler { onDismiss() }

  ModalBottomSheet(
    onDismissRequest = onDismiss,
    sheetState = sheetState,
    containerColor = colors.sheet,
    scrimColor = colors.scrim,
    shape = RoundedCornerShape(topStart = RenewlyTokens.RadiusBottomSheet, topEnd = RenewlyTokens.RadiusBottomSheet),
    dragHandle = {
      // 44 x 4 grab handle radius 999
      Box(
        modifier = Modifier
          .padding(top = 16.dp, bottom = 18.dp)
          .size(width = 44.dp, height = 4.dp)
          .clip(RoundedCornerShape(RenewlyTokens.RadiusSheetGrabHandle))
          .background(colors.track)
      )
    }
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 20.dp)
        .navigationBarsPadding()
        .padding(bottom = 24.dp)
    ) {
      if (step == 1) {
        // Step 1
        Text(
          text = "Cancel ${subscription.name}?",
          style = typography.screenHeadline,
          color = colors.ink
        )
        Spacer(modifier = Modifier.height(10.dp))
        Text(
          text = "You'd keep $annualAmount a year — and lose it from $monD.",
          style = typography.bodyLarge,
          color = colors.ink2
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Option 1: Pause it for 3 months
        val pauseSource = remember { MutableInteractionSource() }
        val isPausePressed by pauseSource.collectIsPressedAsState()
        val pauseBorder = if (isPausePressed) colors.accent else colors.line

        Box(
          modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(colors.card2)
            .border(1.dp, pauseBorder, RoundedCornerShape(20.dp))
            .clickable(
              interactionSource = pauseSource,
              indication = null,
              role = Role.Button
            ) {
              onPauseThreeMonths(resumeMonth)
            }
            .padding(horizontal = 18.dp, vertical = 16.dp)
        ) {
          Column {
            Text(
              text = "Pause it for 3 months",
              style = TextStyle(fontSize = 15.sp, fontWeight = FontWeight.W600, color = colors.ink)
            )
            Spacer(modifier = Modifier.height(3.dp))
            Text(
              text = "Comes out of your total, stays in your list. We'll ask again in $resumeMonth.",
              style = TextStyle(fontSize = 13.sp, color = colors.ink2)
            )
          }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Option 2: Cancel it for good
        val cancelSource = remember { MutableInteractionSource() }
        val isCancelPressed by cancelSource.collectIsPressedAsState()
        val cancelBg = if (isCancelPressed) colors.accentPress else colors.accent

        Box(
          modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(cancelBg)
            .clickable(
              interactionSource = cancelSource,
              indication = null,
              role = Role.Button
            ) {
              step = 2
            }
            .padding(horizontal = 18.dp, vertical = 16.dp)
        ) {
          Column {
            Text(
              text = "Cancel it for good",
              style = TextStyle(fontSize = 15.sp, fontWeight = FontWeight.W600, color = colors.onAccent)
            )
            Spacer(modifier = Modifier.height(3.dp))
            Text(
              text = "Two quick steps — one of them isn't in this app.",
              style = TextStyle(fontSize = 13.sp, color = colors.onAccent.copy(alpha = 0.85f))
            )
          }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Option 3: Never mind
        RenewlyTertiaryButton(
          text = "Never mind.",
          onClick = onDismiss,
          modifier = Modifier.align(Alignment.CenterHorizontally)
        )
      } else {
        // Step 2
        Text(
          text = "Two steps to finish.",
          style = typography.screenHeadline,
          color = colors.ink
        )

        Spacer(modifier = Modifier.height(18.dp))

        // Step 1 card
        Row(
          modifier = Modifier.fillMaxWidth(),
          verticalAlignment = Alignment.Top
        ) {
          Box(
            modifier = Modifier
              .size(28.dp)
              .clip(CircleShape)
              .background(colors.accentTint),
            contentAlignment = Alignment.Center
          ) {
            Text(
              text = "1",
              style = TextStyle(fontSize = 13.sp, fontWeight = FontWeight.W700, color = colors.accentDeep)
            )
          }
          Spacer(modifier = Modifier.width(13.dp))
          Column {
            Text(
              text = "Cancel with ${subscription.name} directly",
              style = TextStyle(fontSize = 15.sp, fontWeight = FontWeight.W600, color = colors.ink)
            )
            Spacer(modifier = Modifier.height(3.dp))
            Text(
              text = "Renewly can't do this for you. It only tracks what you tell it.",
              style = TextStyle(fontSize = 13.sp, lineHeight = (13 * 1.5).sp, color = colors.ink2)
            )
          }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Step 2 card
        Row(
          modifier = Modifier.fillMaxWidth(),
          verticalAlignment = Alignment.Top
        ) {
          Box(
            modifier = Modifier
              .size(28.dp)
              .clip(CircleShape)
              .background(colors.accentTint),
            contentAlignment = Alignment.Center
          ) {
            Text(
              text = "2",
              style = TextStyle(fontSize = 13.sp, fontWeight = FontWeight.W700, color = colors.accentDeep)
            )
          }
          Spacer(modifier = Modifier.width(13.dp))
          Column {
            Text(
              text = "Mark it cancelled here",
              style = TextStyle(fontSize = 15.sp, fontWeight = FontWeight.W600, color = colors.ink)
            )
            Spacer(modifier = Modifier.height(3.dp))
            Text(
              text = "We stop counting it and keep the history, so you can see what you saved.",
              style = TextStyle(fontSize = 13.sp, lineHeight = (13 * 1.5).sp, color = colors.ink2)
            )
          }
        }

        Spacer(modifier = Modifier.height(22.dp))

        RenewlyPrimaryButton(
          text = "I've cancelled it — mark it done",
          onClick = { onConfirmCancel(annualAmount) },
          showArrow = false
        )

        Spacer(modifier = Modifier.height(8.dp))

        RenewlyTertiaryButton(
          text = "I'll do it later.",
          onClick = onDismiss,
          modifier = Modifier.align(Alignment.CenterHorizontally)
        )
      }
    }
  }
}
