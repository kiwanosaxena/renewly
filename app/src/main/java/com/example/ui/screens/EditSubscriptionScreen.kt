package com.example.ui.screens

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Subscription
import com.example.data.model.UserSettings
import com.example.ui.components.RenewlyBackHeader
import com.example.ui.components.RenewlyCategoryChips
import com.example.ui.components.RenewlyHeroCard
import com.example.ui.components.RenewlyNestedCard
import com.example.ui.components.RenewlyPrimaryButton
import com.example.ui.components.RenewlyScreenScaffold
import com.example.ui.components.RenewlySegmentedControl
import com.example.ui.components.RenewlyTertiaryButton
import com.example.ui.components.RenewlyTextField
import com.example.ui.components.RenewlyToggle
import com.example.ui.theme.RenewlyTheme
import com.example.ui.theme.RenewlyTokens

@Composable
fun EditSubscriptionScreen(
  subscriptionId: String,
  subscriptions: List<Subscription>,
  settings: UserSettings,
  onSave: (subId: String, name: String, amount: Double, cycle: String, chargeDay: Int, category: String, note: String, barelyUsed: Boolean) -> Unit,
  onDiscard: () -> Unit,
  modifier: Modifier = Modifier
) {
  val colors = RenewlyTheme.colors
  val typography = RenewlyTheme.typography

  val subscription = subscriptions.firstOrNull { it.id == subscriptionId }
  if (subscription == null) {
    onDiscard()
    return
  }

  var nameInput by remember { mutableStateOf(subscription.name) }
  var amountInput by remember { mutableStateOf(subscription.amount.toString()) }
  var cycleInput by remember { mutableStateOf(if (subscription.cycle.equals("yearly", ignoreCase = true)) "Yearly" else "Monthly") }
  var chargeDayInput by remember { mutableIntStateOf(subscription.chargeDay.coerceIn(1, 28)) }
  var selectedCategory by remember { mutableStateOf(subscription.category) }
  var noteInput by remember { mutableStateOf(subscription.note) }
  var barelyUsedToggle by remember { mutableStateOf(subscription.barelyUsed) }

  val categories = listOf("Streaming", "Music", "Software", "News", "Fitness", "Reading")

  RenewlyScreenScaffold(modifier = modifier) {
    Column(
      modifier = Modifier
        .fillMaxSize()
        .statusBarsPadding()
    ) {
      // Sticky header (Back form with close icon)
      RenewlyBackHeader(
        title = "Edit ${subscription.name}",
        iconName = "close",
        accessibilityLabel = "Discard changes",
        onBack = onDiscard
      )

      Column(
        modifier = Modifier
          .fillMaxSize()
          .verticalScroll(rememberScrollState())
          .padding(horizontal = RenewlyTokens.ScreenHorizontalGutter)
          .padding(top = 4.dp, bottom = 100.dp),
        verticalArrangement = Arrangement.spacedBy(RenewlyTokens.GapBetweenStackedCards)
      ) {
        // Form card
        RenewlyHeroCard {
          Column(verticalArrangement = Arrangement.spacedBy(RenewlyTokens.FormFieldGroupGap)) {
            // Service name
            RenewlyTextField(
              value = nameInput,
              onValueChange = { nameInput = it },
              label = "Service name",
              placeholder = "e.g. Netflix, Gym, Substack"
            )

            // Amount & Cycle
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.spacedBy(10.dp),
              verticalAlignment = Alignment.Top
            ) {
              Box(modifier = Modifier.weight(1.2f)) {
                RenewlyTextField(
                  value = amountInput,
                  onValueChange = {
                    if (it.count { c -> c == '.' } <= 1 && it.all { c -> c.isDigit() || c == '.' }) {
                      amountInput = it
                    }
                  },
                  label = "Amount",
                  isAmount = true,
                  currencySymbol = settings.currency,
                  keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
                )
              }

              Box(modifier = Modifier.weight(1f)) {
                Column {
                  Text(
                    text = "BILLING CYCLE",
                    style = typography.microLabel,
                    color = colors.ink3,
                    modifier = Modifier.padding(bottom = RenewlyTokens.LabelFieldGap)
                  )
                  RenewlySegmentedControl(
                    options = listOf("Monthly", "Yearly"),
                    selectedOption = cycleInput,
                    onOptionSelected = { cycleInput = it }
                  )
                }
              }
            }

            // Charge day (1-28)
            RenewlyTextField(
              value = chargeDayInput.toString(),
              onValueChange = {
                val parsed = it.filter { c -> c.isDigit() }.toIntOrNull()
                if (parsed != null) {
                  chargeDayInput = parsed.coerceIn(1, 28)
                }
              },
              label = "Charge day of month (1-28)",
              helperText = "Lands in every month.",
              keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )

            // Category
            Column {
              Text(
                text = "CATEGORY",
                style = typography.microLabel,
                color = colors.ink3,
                modifier = Modifier.padding(bottom = RenewlyTokens.LabelFieldGap)
              )

              Row(
                modifier = Modifier
                  .fillMaxWidth()
                  .horizontalScroll(rememberScrollState())
              ) {
                RenewlyCategoryChips(
                  categories = categories,
                  selectedCategory = selectedCategory,
                  onCategorySelected = { selectedCategory = it }
                )
              }
            }

            // Private note
            RenewlyTextField(
              value = noteInput,
              onValueChange = { noteInput = it },
              label = "Private note",
              placeholder = "Add a private note"
            )

            // Barely used toggle
            RenewlyNestedCard {
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Column(modifier = Modifier.weight(1f)) {
                  Text(
                    text = "Mark as barely used",
                    style = TextStyle(fontSize = 15.sp, fontWeight = FontWeight.W600, color = colors.ink)
                  )
                  Spacer(modifier = Modifier.height(2.dp))
                  Text(
                    text = "Flags this subscription for a second look on your Home screen.",
                    style = TextStyle(fontSize = 12.5.sp, color = colors.ink2)
                  )
                }

                RenewlyToggle(
                  checked = barelyUsedToggle,
                  onCheckedChange = { barelyUsedToggle = it }
                )
              }
            }
          }
        }

        // Save & Discard buttons
        val parsedAmount = amountInput.toDoubleOrNull()
        val isValid = nameInput.trim().isNotEmpty() && parsedAmount != null && parsedAmount > 0.0

        RenewlyPrimaryButton(
          text = if (isValid) "Save changes" else "Enter a valid name and amount",
          onClick = {
            if (isValid && parsedAmount != null) {
              onSave(
                subscription.id,
                nameInput,
                parsedAmount,
                cycleInput,
                chargeDayInput,
                selectedCategory,
                noteInput,
                barelyUsedToggle
              )
            }
          },
          enabled = isValid,
          isExtraLarge = true,
          showArrow = isValid
        )

        RenewlyTertiaryButton(
          text = "Discard changes",
          onClick = onDiscard,
          modifier = Modifier.align(Alignment.CenterHorizontally)
        )
      }
    }
  }
}
