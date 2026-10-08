package com.example.ui.screens

import android.content.ActivityNotFoundException
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import com.example.BuildConfig
import androidx.activity.compose.BackHandler
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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.UserSettings
import com.example.ui.components.RenewlyBackHeader
import com.example.ui.components.RenewlyIcon
import com.example.ui.components.RenewlyInlineLink
import com.example.ui.components.RenewlyNestedCard
import com.example.ui.components.RenewlyScreenScaffold
import com.example.ui.components.RenewlySecondaryCardButton
import com.example.ui.components.RenewlySegmentedControl
import com.example.ui.components.RenewlyStandardCard
import com.example.ui.components.RenewlyTextField
import com.example.ui.components.RenewlyToggle
import com.example.ui.theme.RenewlyTheme
import com.example.ui.theme.RenewlyTokens

@Composable
fun SettingsScreen(
  settings: UserSettings,
  onUpdateSettings: (UserSettings) -> Unit,
  onExportJson: ((String) -> Unit) -> Unit,
  onExportCsv: () -> String,
  onImportJson: (String) -> Unit,
  onDeleteAllData: () -> Unit,
  onBack: () -> Unit,
  showToast: (String) -> Unit,
  modifier: Modifier = Modifier
) {
  val colors = RenewlyTheme.colors
  val typography = RenewlyTheme.typography
  val context = LocalContext.current

  var showDeleteConfirmDialog by remember { mutableStateOf(false) }
  var showImportDialog by remember { mutableStateOf(false) }
  var importInputText by remember { mutableStateOf("") }

  RenewlyScreenScaffold(modifier = modifier) {
    Column(
      modifier = Modifier
        .fillMaxSize()
        .statusBarsPadding()
    ) {
      // Sticky header
      RenewlyBackHeader(
        title = "Settings",
        onBack = onBack
      )

      Column(
        modifier = Modifier
          .fillMaxSize()
          .verticalScroll(rememberScrollState())
          .padding(horizontal = RenewlyTokens.ScreenHorizontalGutter)
          .padding(top = 4.dp, bottom = 100.dp),
        verticalArrangement = Arrangement.spacedBy(RenewlyTokens.GapBetweenStackedCards)
      ) {
        // PREFERENCES
        RenewlyStandardCard {
          Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Text(
              text = "PREFERENCES",
              style = typography.eyebrow,
              color = colors.ink3
            )

            // Preferred name
            RenewlyTextField(
              value = settings.userName,
              onValueChange = { onUpdateSettings(settings.copy(userName = it)) },
              label = "Your name (optional)",
              placeholder = "Used for morning greeting"
            )

            // Currency
            Column {
              Text(
                text = "CURRENCY",
                style = typography.microLabel,
                color = colors.ink3,
                modifier = Modifier.padding(bottom = RenewlyTokens.LabelFieldGap)
              )
              RenewlySegmentedControl(
                options = listOf("$", "€", "£", "₹", "¥"),
                selectedOption = settings.currency,
                onOptionSelected = { onUpdateSettings(settings.copy(currency = it)) }
              )
            }

            // Theme
            Column {
              Text(
                text = "THEME",
                style = typography.microLabel,
                color = colors.ink3,
                modifier = Modifier.padding(bottom = RenewlyTokens.LabelFieldGap)
              )
              RenewlySegmentedControl(
                options = listOf("system", "light", "dark"),
                selectedOption = settings.theme,
                onOptionSelected = { onUpdateSettings(settings.copy(theme = it)) },
                labelProvider = { it.replaceFirstChar { c -> c.uppercase() } }
              )
            }

            // Week starts on
            Column {
              Text(
                text = "FIRST DAY OF WEEK",
                style = typography.microLabel,
                color = colors.ink3,
                modifier = Modifier.padding(bottom = RenewlyTokens.LabelFieldGap)
              )
              RenewlySegmentedControl(
                options = listOf("mon", "sun"),
                selectedOption = settings.weekStart,
                onOptionSelected = { onUpdateSettings(settings.copy(weekStart = it)) },
                labelProvider = { if (it == "mon") "Monday" else "Sunday" }
              )
            }

            // Home Hero style
            Column {
              Text(
                text = "HOME SCREEN HERO",
                style = typography.microLabel,
                color = colors.ink3,
                modifier = Modifier.padding(bottom = RenewlyTokens.LabelFieldGap)
              )
              RenewlySegmentedControl(
                options = listOf("total", "renewal"),
                selectedOption = settings.homeHero,
                onOptionSelected = { onUpdateSettings(settings.copy(homeHero = it)) },
                labelProvider = { if (it == "total") "Monthly total" else "Next renewal" }
              )
            }
          }
        }

        // NOTIFICATIONS
        RenewlyStandardCard {
          Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Text(
              text = "NOTIFICATIONS & QUIET HOURS",
              style = typography.eyebrow,
              color = colors.ink3
            )

            // Default lead days
            Column {
              Text(
                text = "DEFAULT ALERT LEAD TIME",
                style = typography.microLabel,
                color = colors.ink3,
                modifier = Modifier.padding(bottom = RenewlyTokens.LabelFieldGap)
              )
              RenewlySegmentedControl(
                options = listOf(1, 2, 3, 7),
                selectedOption = settings.defaultRemindDays,
                onOptionSelected = { onUpdateSettings(settings.copy(defaultRemindDays = it)) },
                labelProvider = { "$it days" }
              )
            }

            // Quiet hours toggle
            RenewlyNestedCard {
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Column(modifier = Modifier.weight(1f)) {
                  Text(
                    text = "Quiet hours",
                    style = TextStyle(fontSize = 15.sp, fontWeight = FontWeight.W600, color = colors.ink)
                  )
                  Spacer(modifier = Modifier.height(2.dp))
                  Text(
                    text = "Delays morning reminders until 08:00 if quiet",
                    style = TextStyle(fontSize = 12.5.sp, color = colors.ink2)
                  )
                }

                RenewlyToggle(
                  checked = settings.quietHoursEnabled,
                  onCheckedChange = { onUpdateSettings(settings.copy(quietHoursEnabled = it)) }
                )
              }
            }
          }
        }

        // DATA & BACKUP
        RenewlyStandardCard {
          Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text(
              text = "DATA MANAGEMENT",
              style = typography.eyebrow,
              color = colors.ink3
            )

            // Export JSON
            RenewlySecondaryCardButton(
              onClick = {
                onExportJson { json ->
                  val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                  val clip = ClipData.newPlainText("Renewly Subscriptions JSON", json)
                  clipboard.setPrimaryClip(clip)
                  showToast("JSON copied to clipboard.")
                }
              }
            ) {
              Text(
                text = "Export subscriptions (JSON)",
                style = typography.buttonMedium,
                color = colors.ink
              )
              RenewlyIcon(name = "chevron-right", color = colors.ink2, size = 16.dp)
            }

            // Export CSV
            RenewlySecondaryCardButton(
              onClick = {
                val csv = onExportCsv()
                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                val clip = ClipData.newPlainText("Renewly Subscriptions CSV", csv)
                clipboard.setPrimaryClip(clip)
                showToast("CSV copied to clipboard.")
              }
            ) {
              Text(
                text = "Export spreadsheet (CSV)",
                style = typography.buttonMedium,
                color = colors.ink
              )
              RenewlyIcon(name = "chevron-right", color = colors.ink2, size = 16.dp)
            }

            // Import JSON
            RenewlySecondaryCardButton(
              onClick = { showImportDialog = true }
            ) {
              Text(
                text = "Import subscriptions (JSON)",
                style = typography.buttonMedium,
                color = colors.ink
              )
              RenewlyIcon(name = "chevron-right", color = colors.ink2, size = 16.dp)
            }

            // Delete all data
            RenewlySecondaryCardButton(
              onClick = { showDeleteConfirmDialog = true }
            ) {
              Text(
                text = "Delete all data",
                style = typography.buttonMedium,
                color = colors.accentDeep
              )
              RenewlyIcon(name = "chevron-right", color = colors.accentDeep, size = 16.dp)
            }
          }
        }

        // ABOUT
        RenewlyNestedCard {
          Column {
            Text(
              text = "Renewly " + BuildConfig.VERSION_NAME,
              style = TextStyle(fontSize = 14.sp, fontWeight = FontWeight.W600, color = colors.ink)
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
              text = "by Avikalabs",
              style = TextStyle(fontSize = 12.5.sp, color = colors.ink3)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
              text = "Zero tracking. Zero bank connections. All data on-device.",
              style = TextStyle(fontSize = 12.5.sp, color = colors.ink2)
            )
            Spacer(modifier = Modifier.height(8.dp))
            RenewlyInlineLink(
              text = "Privacy policy",
              onClick = {
                try {
                  val intent = Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("https://github.com/kiwanosaxena/renewly/blob/main/privacy.md")
                  )
                  context.startActivity(intent)
                } catch (e: ActivityNotFoundException) {
                  showToast("No browser found.")
                }
              }
            )
          }
        }
      }
    }

    // Delete confirmation dialog
    if (showDeleteConfirmDialog) {
      BackHandler { showDeleteConfirmDialog = false }
      AlertDialog(
        onDismissRequest = { showDeleteConfirmDialog = false },
        title = { Text("Delete all data?", color = colors.ink) },
        text = {
          Text(
            "This will permanently delete all subscriptions, scheduled reminders, and charge history. This cannot be undone.",
            color = colors.ink2
          )
        },
        confirmButton = {
          Button(
            onClick = {
              showDeleteConfirmDialog = false
              onDeleteAllData()
            },
            colors = ButtonDefaults.buttonColors(containerColor = colors.accent)
          ) {
            Text("Delete everything", color = colors.onAccent)
          }
        },
        dismissButton = {
          TextButton(onClick = { showDeleteConfirmDialog = false }) {
            Text("Cancel", color = colors.ink2)
          }
        },
        containerColor = colors.sheet
      )
    }

    // Import JSON dialog
    if (showImportDialog) {
      BackHandler { showImportDialog = false }
      AlertDialog(
        onDismissRequest = { showImportDialog = false },
        title = { Text("Import JSON", color = colors.ink) },
        text = {
          Column {
            Text(
              "Paste exported Renewly JSON below:",
              color = colors.ink2,
              modifier = Modifier.padding(bottom = 8.dp)
            )
            RenewlyTextField(
              value = importInputText,
              onValueChange = { importInputText = it },
              placeholder = "{\"subscriptions\": [...] }",
              singleLine = false
            )
          }
        },
        confirmButton = {
          Button(
            onClick = {
              showImportDialog = false
              onImportJson(importInputText)
              importInputText = ""
            },
            colors = ButtonDefaults.buttonColors(containerColor = colors.accent)
          ) {
            Text("Import", color = colors.onAccent)
          }
        },
        dismissButton = {
          TextButton(onClick = { showImportDialog = false }) {
            Text("Cancel", color = colors.ink2)
          }
        },
        containerColor = colors.sheet
      )
    }
  }
}
