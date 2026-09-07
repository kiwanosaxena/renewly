package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.CatalogData
import com.example.data.model.CatalogService
import com.example.data.model.Subscription
import com.example.data.model.UserSettings
import com.example.ui.components.RenewlyCategoryChips
import com.example.ui.components.RenewlyHeroCard
import com.example.ui.components.RenewlyIcon
import com.example.ui.components.RenewlyNestedCard
import com.example.ui.components.RenewlyPrimaryButton
import com.example.ui.components.RenewlyScreenScaffold
import com.example.ui.components.RenewlySearchField
import com.example.ui.components.RenewlySecondaryCardButton
import com.example.ui.components.RenewlySegmentedControl
import com.example.ui.components.RenewlyStandardCard
import com.example.ui.components.RenewlyTextField
import com.example.ui.components.RenewlyToggle
import com.example.ui.theme.CategoryColors
import com.example.ui.theme.RenewlyTheme
import com.example.ui.theme.RenewlyTokens
import java.time.LocalDate
import java.util.UUID

data class StagedSubscription(
  val name: String,
  val category: String,
  var amountStr: String = "",
  var cycle: String = "monthly",
  var chargeDay: Int = LocalDate.now().dayOfMonth.coerceIn(1, 28)
)

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AddSubscriptionScreen(
  settings: UserSettings,
  onAddSingle: (name: String, amount: Double, cycle: String, firstCharge: LocalDate, category: String, remind: Boolean, note: String) -> Unit,
  onAddBatch: (List<Subscription>) -> Unit,
  modifier: Modifier = Modifier
) {
  val colors = RenewlyTheme.colors
  val typography = RenewlyTheme.typography
  val today = LocalDate.now()

  var mode by remember { mutableStateOf("Full details") } // "Quick pick" vs "Full details"

  // Mode B state
  var nameInput by remember { mutableStateOf("") }
  var amountInput by remember { mutableStateOf("") }
  var cycleInput by remember { mutableStateOf("Monthly") }
  var chargeDayInput by remember { mutableIntStateOf(today.dayOfMonth.coerceIn(1, 28)) }
  var selectedCategory by remember { mutableStateOf("Software") }
  var remindToggle by remember { mutableStateOf(true) }
  var noteInput by remember { mutableStateOf("") }

  // Mode A state
  var searchQuery by remember { mutableStateOf("") }
  var filterCategory by remember { mutableStateOf("All") }
  val stagedQueue = remember { mutableStateListOf<StagedSubscription>() }

  val categories = listOf("Streaming", "Music", "Software", "News", "Fitness", "Reading")

  RenewlyScreenScaffold(modifier = modifier) {
    Column(
      modifier = Modifier
        .fillMaxSize()
        .statusBarsPadding()
    ) {
      // Sticky header (Title form with mode switch)
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
            text = "NEW SUBSCRIPTION",
            style = typography.eyebrow,
            color = colors.ink3
          )
          Text(
            text = "Track another",
            style = typography.sectionHeader,
            color = colors.ink
          )
        }

        Box(modifier = Modifier.width(190.dp)) {
          RenewlySegmentedControl(
            options = listOf("Quick pick", "Full details"),
            selectedOption = mode,
            onOptionSelected = { mode = it },
            isAddModeStyle = true
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
        if (mode == "Full details") {
          // MODE B: FULL DETAILS FORM
          RenewlyHeroCard {
            Column(verticalArrangement = Arrangement.spacedBy(RenewlyTokens.FormFieldGroupGap)) {
              // 1. Service name
              RenewlyTextField(
                value = nameInput,
                onValueChange = { nameInput = it },
                label = "Service name",
                placeholder = "e.g. Netflix, Gym, Substack",
                helperText = "What appears on your bank statement.",
                testTag = "input_sub_name"
              )

              // 2. Amount & Cycle
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
                    placeholder = "0.00",
                    isAmount = true,
                    currencySymbol = settings.currency,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    testTag = "input_sub_amount"
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

              // 3. Charge day (1-28)
              Column {
                Text(
                  text = "CHARGE DAY OF MONTH (1-28)",
                  style = typography.microLabel,
                  color = colors.ink3,
                  modifier = Modifier.padding(bottom = RenewlyTokens.LabelFieldGap)
                )

                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.spacedBy(6.dp),
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  listOf(1, 5, 10, 15, 20, 25, 28).forEach { day ->
                    val isSelected = day == chargeDayInput
                    Box(
                      modifier = Modifier
                        .weight(1f)
                        .height(44.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (isSelected) colors.accent else colors.field)
                        .border(1.dp, if (isSelected) colors.accent else colors.line, RoundedCornerShape(12.dp))
                        .clickable(role = Role.Button) { chargeDayInput = day },
                      contentAlignment = Alignment.Center
                    ) {
                      Text(
                        text = day.toString(),
                        style = TextStyle(
                          fontSize = 14.sp,
                          fontWeight = FontWeight.W600,
                          color = if (isSelected) colors.onAccent else colors.ink
                        )
                      )
                    }
                  }
                }
              }

              // 4. Category
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

              // 5. Reminder toggle
              RenewlyNestedCard {
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
                    Text(
                      text = "Alert ${settings.defaultRemindDays} days before renewal at 09:00",
                      style = TextStyle(fontSize = 12.5.sp, color = colors.ink2)
                    )
                  }

                  RenewlyToggle(
                    checked = remindToggle,
                    onCheckedChange = { remindToggle = it }
                  )
                }
              }

              // 6. Private note (optional)
              RenewlyTextField(
                value = noteInput,
                onValueChange = { noteInput = it },
                label = "Private note (optional)",
                placeholder = "e.g. Student plan, shared with family",
                testTag = "input_sub_note"
              )
            }
          }

          // Validation & Confirm Button
          val parsedAmount = amountInput.toDoubleOrNull()
          val isFormValid = nameInput.trim().isNotEmpty() && parsedAmount != null && parsedAmount > 0.0

          val buttonText = if (isFormValid) "Track subscription" else "Enter a name and amount to continue"

          RenewlyPrimaryButton(
            text = buttonText,
            onClick = {
              if (isFormValid && parsedAmount != null) {
                val firstChargeDate = LocalDate.of(today.year, today.month, chargeDayInput.coerceAtMost(today.lengthOfMonth()))
                onAddSingle(
                  nameInput,
                  parsedAmount,
                  cycleInput,
                  firstChargeDate,
                  selectedCategory,
                  remindToggle,
                  noteInput
                )
              }
            },
            enabled = isFormValid,
            isExtraLarge = true,
            showArrow = isFormValid,
            testTag = "submit_add_subscription"
          )
        } else {
          // MODE A: QUICK PICK CATALOGUE
          RenewlySearchField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = "Search services."
          )

          // Category filter pills
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            (listOf("All") + categories).forEach { cat ->
              val isSelected = cat == filterCategory
              Box(
                modifier = Modifier
                  .height(38.dp)
                  .clip(RoundedCornerShape(999.dp))
                  .background(if (isSelected) colors.accentTint else colors.card)
                  .border(1.dp, if (isSelected) colors.accent else colors.line, RoundedCornerShape(999.dp))
                  .clickable(role = Role.Button) { filterCategory = cat }
                  .padding(horizontal = 14.dp),
                contentAlignment = Alignment.Center
              ) {
                Text(
                  text = cat,
                  style = TextStyle(
                    fontSize = 13.sp,
                    fontWeight = if (isSelected) FontWeight.W600 else FontWeight.W500,
                    color = if (isSelected) colors.accentDeep else colors.ink
                  )
                )
              }
            }
          }

          // Staged queue items (if any are selected)
          if (stagedQueue.isNotEmpty()) {
            RenewlyStandardCard {
              Column {
                Text(
                  text = "Selected to track (${stagedQueue.size})",
                  style = typography.cardTitle,
                  color = colors.ink
                )

                Spacer(modifier = Modifier.height(10.dp))

                stagedQueue.forEachIndexed { index, item ->
                  RenewlyNestedCard(modifier = Modifier.padding(bottom = 8.dp)) {
                    Column {
                      Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                      ) {
                        Text(
                          text = item.name,
                          style = TextStyle(fontSize = 15.sp, fontWeight = FontWeight.W600, color = colors.ink)
                        )
                        Box(
                          modifier = Modifier
                            .size(24.dp)
                            .clip(RoundedCornerShape(999.dp))
                            .background(colors.track)
                            .clickable(role = Role.Button) { stagedQueue.removeAt(index) },
                          contentAlignment = Alignment.Center
                        ) {
                          RenewlyIcon(name = "close", color = colors.ink2, size = 12.dp)
                        }
                      }

                      Spacer(modifier = Modifier.height(8.dp))

                      Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                      ) {
                        Box(modifier = Modifier.weight(1.2f)) {
                          RenewlyTextField(
                            value = item.amountStr,
                            onValueChange = { newAmt ->
                              if (newAmt.count { c -> c == '.' } <= 1 && newAmt.all { c -> c.isDigit() || c == '.' }) {
                                stagedQueue[index] = item.copy(amountStr = newAmt)
                              }
                            },
                            placeholder = "0.00",
                            isAmount = true,
                            currencySymbol = settings.currency,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
                          )
                        }

                        Box(modifier = Modifier.weight(1f)) {
                          RenewlySegmentedControl(
                            options = listOf("monthly", "yearly"),
                            selectedOption = item.cycle,
                            onOptionSelected = { stagedQueue[index] = item.copy(cycle = it) },
                            isAddModeStyle = true
                          )
                        }
                      }
                    }
                  }
                }
              }
            }
          }

          // Filter catalogue
          val filteredServices = CatalogData.defaultServices.filter { service ->
            val matchesCategory = (filterCategory == "All" || service.defaultCategory == filterCategory)
            val matchesSearch = searchQuery.isBlank() || service.name.contains(searchQuery, ignoreCase = true)
            matchesCategory && matchesSearch
          }

          // 2-column virtualised service grid
          FlowRow(
            modifier = Modifier.fillMaxWidth(),
            maxItemsInEachRow = 2,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            filteredServices.forEach { service ->
              val catColor = CategoryColors.getCategoryColor(service.defaultCategory, colors.isDark)
              val isAlreadyStaged = stagedQueue.any { it.name.equals(service.name, ignoreCase = true) }

              Box(
                modifier = Modifier
                  .weight(1f)
                  .height(68.dp)
                  .clip(RoundedCornerShape(RenewlyTokens.RadiusTile))
                  .background(if (isAlreadyStaged) colors.accentTint else colors.card)
                  .border(1.dp, if (isAlreadyStaged) colors.accent else colors.line, RoundedCornerShape(RenewlyTokens.RadiusTile))
                  .clickable(role = Role.Button) {
                    if (!isAlreadyStaged) {
                      stagedQueue.add(StagedSubscription(name = service.name, category = service.defaultCategory))
                    }
                  }
                  .padding(horizontal = 12.dp, vertical = 10.dp),
                contentAlignment = Alignment.CenterStart
              ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                  // 34 x 34 letter tile
                  Box(
                    modifier = Modifier
                      .size(34.dp)
                      .clip(RoundedCornerShape(RenewlyTokens.RadiusLetterTile34))
                      .background(catColor),
                    contentAlignment = Alignment.Center
                  ) {
                    Text(
                      text = service.name.firstOrNull()?.uppercase() ?: "?",
                      style = TextStyle(fontSize = 14.sp, fontWeight = FontWeight.W700, color = colors.onAccent)
                    )
                  }

                  Spacer(modifier = Modifier.width(10.dp))

                  Column(modifier = Modifier.weight(1f)) {
                    Text(
                      text = service.name,
                      style = TextStyle(fontSize = 13.5.sp, fontWeight = FontWeight.W600, color = colors.ink),
                      maxLines = 1,
                      overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                      text = service.defaultCategory,
                      style = TextStyle(fontSize = 11.sp, color = colors.ink3),
                      maxLines = 1,
                      overflow = TextOverflow.Ellipsis
                    )
                  }
                }
              }
            }
          }

          // If custom name search has no exact match, offer to add as custom
          if (searchQuery.isNotBlank() && filteredServices.none { it.name.equals(searchQuery.trim(), ignoreCase = true) }) {
            RenewlySecondaryCardButton(
              onClick = {
                stagedQueue.add(StagedSubscription(name = searchQuery.trim(), category = "Software"))
                searchQuery = ""
              }
            ) {
              Text(
                text = "Add custom: \"${searchQuery.trim()}\"",
                style = typography.buttonMedium,
                color = colors.accentDeep
              )
              RenewlyIcon(name = "plus", color = colors.accentDeep, size = 16.dp)
            }
          }

          if (stagedQueue.isNotEmpty()) {
            val allHaveValidAmounts = stagedQueue.all { it.amountStr.toDoubleOrNull() != null && (it.amountStr.toDoubleOrNull() ?: 0.0) > 0.0 }
            val countStr = if (stagedQueue.size == 1) "1 subscription" else "${stagedQueue.size} subscriptions"

            RenewlyPrimaryButton(
              text = if (allHaveValidAmounts) "Track $countStr" else "Fill amounts above to finish",
              onClick = {
                if (allHaveValidAmounts) {
                  val subs = stagedQueue.map { item ->
                    Subscription(
                      id = UUID.randomUUID().toString(),
                      name = item.name.trim(),
                      amount = item.amountStr.toDouble(),
                      cycle = item.cycle,
                      chargeDay = item.chargeDay,
                      firstCharge = today.toString(),
                      category = item.category,
                      remind = true,
                      remindDays = settings.defaultRemindDays,
                      createdAt = today.toString()
                    )
                  }
                  onAddBatch(subs)
                }
              },
              enabled = allHaveValidAmounts,
              showArrow = allHaveValidAmounts
            )
          }
        }
      }
    }
  }
}
