package com.example.ui.screens

import android.Manifest
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.RenewlyHeroCard
import com.example.ui.components.RenewlyIcon
import com.example.ui.components.RenewlyNestedCard
import com.example.ui.components.RenewlyPrimaryButton
import com.example.ui.components.RenewlyScreenScaffold
import com.example.ui.components.RenewlyTertiaryButton
import com.example.ui.theme.RenewlyTheme
import com.example.ui.theme.RenewlyTokens

@Composable
fun OnboardingScreen(
  onComplete: (currency: String) -> Unit,
  modifier: Modifier = Modifier
) {
  val colors = RenewlyTheme.colors
  val typography = RenewlyTheme.typography

  var step by remember { mutableIntStateOf(1) }
  var selectedCurrency by remember { mutableStateOf("$") }

  // Permission launcher for Android 13+
  val permissionLauncher = rememberLauncherForActivityResult(
    contract = ActivityResultContracts.RequestPermission()
  ) { isGranted ->
    step = 2
  }

  RenewlyScreenScaffold(modifier = modifier) {
    Column(
      modifier = Modifier
        .fillMaxSize()
        .statusBarsPadding()
        .padding(horizontal = RenewlyTokens.ScreenHorizontalGutter)
        .padding(top = 20.dp, bottom = 32.dp)
        .verticalScroll(rememberScrollState()),
      verticalArrangement = Arrangement.SpaceBetween
    ) {
      Column {
        // Progress track: height 4, radius 999
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .height(4.dp)
            .clip(RoundedCornerShape(RenewlyTokens.RadiusProgressTrack))
            .background(colors.track)
        ) {
          val progressFraction = if (step == 1) 0.5f else 1.0f
          Box(
            modifier = Modifier
              .fillMaxWidth(progressFraction)
              .height(4.dp)
              .clip(RoundedCornerShape(RenewlyTokens.RadiusProgressTrack))
              .background(colors.accent)
          )
        }

        Spacer(modifier = Modifier.height(32.dp))

        if (step == 1) {
          // STEP 1: CURRENCY & PROMISE
          Text(
            text = "RENEWLY",
            style = typography.eyebrow,
            color = colors.ink3
          )

          Spacer(modifier = Modifier.height(8.dp))

          Text(
            text = "What you spend, without the guesswork.",
            style = typography.screenHeadline,
            color = colors.ink
          )

          Spacer(modifier = Modifier.height(14.dp))

          Text(
            text = "Renewly doesn't connect to your bank. It has no account, no server, and no cloud. Everything you enter stays on this device — and every total is arithmetic on top of what you typed.",
            style = typography.bodyLarge,
            color = colors.ink2
          )

          Spacer(modifier = Modifier.height(28.dp))

          RenewlyHeroCard {
            Column {
              Text(
                text = "CHOOSE YOUR CURRENCY",
                style = typography.microLabel,
                color = colors.ink3
              )

              Spacer(modifier = Modifier.height(12.dp))

              val currencies = listOf(
                Pair("$", "USD / CAD / AUD"),
                Pair("€", "EUR"),
                Pair("£", "GBP"),
                Pair("¥", "JPY")
              )

              Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                currencies.forEach { (sym, desc) ->
                  val isSelected = sym == selectedCurrency
                  Box(
                    modifier = Modifier
                      .fillMaxWidth()
                      .height(52.dp)
                      .clip(RoundedCornerShape(RenewlyTokens.RadiusTile))
                      .background(if (isSelected) colors.accentTint else colors.card2)
                      .border(1.dp, if (isSelected) colors.accent else colors.line, RoundedCornerShape(RenewlyTokens.RadiusTile))
                      .clickable(role = Role.RadioButton) { selectedCurrency = sym }
                      .padding(horizontal = 16.dp),
                    contentAlignment = Alignment.CenterStart
                  ) {
                    Row(
                      modifier = Modifier.fillMaxWidth(),
                      horizontalArrangement = Arrangement.SpaceBetween,
                      verticalAlignment = Alignment.CenterVertically
                    ) {
                      Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                          text = sym,
                          style = TextStyle(fontSize = 18.sp, fontWeight = FontWeight.W700, color = colors.ink)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                          text = desc,
                          style = TextStyle(fontSize = 14.sp, fontWeight = FontWeight.W500, color = colors.ink2)
                        )
                      }
                      if (isSelected) {
                        RenewlyIcon(name = "check", color = colors.accentDeep, size = 18.dp)
                      }
                    }
                  }
                }
              }
            }
          }
        } else {
          // STEP 2: REMINDERS & ALERTS
          Text(
            text = "NOTIFICATIONS",
            style = typography.eyebrow,
            color = colors.ink3
          )

          Spacer(modifier = Modifier.height(8.dp))

          Text(
            text = "Alerts before the money leaves.",
            style = typography.screenHeadline,
            color = colors.ink
          )

          Spacer(modifier = Modifier.height(14.dp))

          Text(
            text = "A morning reminder two days before renewal gives you time to cancel before you are charged. All reminders are inexact and battery-friendly, running locally on your phone.",
            style = typography.bodyLarge,
            color = colors.ink2
          )

          Spacer(modifier = Modifier.height(28.dp))

          RenewlyNestedCard {
            Row(
              modifier = Modifier.fillMaxWidth(),
              verticalAlignment = Alignment.CenterVertically
            ) {
              Box(
                modifier = Modifier
                  .size(40.dp)
                  .clip(CircleShape)
                  .background(colors.accentTint),
                contentAlignment = Alignment.Center
              ) {
                RenewlyIcon(name = "bell", color = colors.accentDeep, size = 20.dp)
              }
              Spacer(modifier = Modifier.width(14.dp))
              Column {
                Text(
                  text = "09:00 morning alert",
                  style = TextStyle(fontSize = 15.sp, fontWeight = FontWeight.W600, color = colors.ink)
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                  text = "2 days before each scheduled charge date",
                  style = TextStyle(fontSize = 12.5.sp, color = colors.ink2)
                )
              }
            }
          }
        }
      }

      // Bottom buttons
      Column(
        modifier = Modifier.padding(top = 24.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        if (step == 1) {
          RenewlyPrimaryButton(
            text = "Set up reminders",
            onClick = {
              if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
              } else {
                step = 2
              }
            }
          )
        } else {
          RenewlyPrimaryButton(
            text = "Track your first subscription",
            onClick = { onComplete(selectedCurrency) }
          )
          RenewlyTertiaryButton(
            text = "Skip for now",
            onClick = { onComplete(selectedCurrency) },
            modifier = Modifier.align(Alignment.CenterHorizontally)
          )
        }
      }
    }
  }
}
