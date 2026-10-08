package com.example

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.ui.RenewlyScreen
import com.example.ui.RenewlyViewModel
import com.example.ui.components.RenewlyTab
import com.example.ui.components.RenewlyTabBar
import com.example.ui.components.RenewlyToast
import com.example.ui.screens.AddSubscriptionScreen
import com.example.ui.screens.DatesScreen
import com.example.ui.screens.EditSubscriptionScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.InsightsScreen
import com.example.ui.screens.OnboardingScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.screens.SubscriptionDetailScreen
import com.example.ui.theme.RenewlyAppTheme

class MainActivity : ComponentActivity() {
  private val viewModel: RenewlyViewModel by viewModels()

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()

    handleIntent(intent)

    setContent {
      val settings by viewModel.settings.collectAsState()

      RenewlyAppTheme(themeSetting = settings.theme) {
        RenewlyMainContent(viewModel = viewModel)
      }
    }
  }

  override fun onNewIntent(intent: Intent) {
    super.onNewIntent(intent)
    setIntent(intent)
    handleIntent(intent)
  }

  private fun handleIntent(intent: Intent?) {
    val subId = intent?.getStringExtra("open_subscription_id")
    if (!subId.isNullOrEmpty()) {
      viewModel.navigateTo(RenewlyScreen.Detail(subId))
    }
  }
}

@Composable
fun RenewlyMainContent(viewModel: RenewlyViewModel) {
  val currentScreen by viewModel.currentScreen.collectAsState()
  val selectedTab by viewModel.selectedTab.collectAsState()
  val subscriptions by viewModel.subscriptions.collectAsState()
  val chargeRecords by viewModel.chargeRecords.collectAsState()
  val settings by viewModel.settings.collectAsState()
  val toastMessage by viewModel.toastMessage.collectAsState()

  val context = LocalContext.current
  var lastBackPressTime by remember { mutableLongStateOf(0L) }
  var onboardingStep by remember { mutableIntStateOf(1) }

  BackHandler {
    when {
      currentScreen == RenewlyScreen.Onboarding -> {
        if (onboardingStep > 1) {
          onboardingStep = 1
          lastBackPressTime = 0L
        } else {
          val now = System.currentTimeMillis()
          if (now - lastBackPressTime < 2000L) {
            (context as Activity).finish()
          } else {
            lastBackPressTime = now
            viewModel.showToast("Press back again to exit")
          }
        }
      }
      currentScreen == RenewlyScreen.Main && selectedTab == RenewlyTab.Home -> {
        val now = System.currentTimeMillis()
        if (now - lastBackPressTime < 2000L) {
          (context as Activity).finish()
        } else {
          lastBackPressTime = now
          viewModel.showToast("Press back again to exit")
        }
      }
      else -> {
        lastBackPressTime = 0L
        viewModel.navigateTo(RenewlyScreen.Main)
        viewModel.selectTab(RenewlyTab.Home)
      }
    }
  }

  Box(modifier = Modifier.fillMaxSize()) {
    when (val screen = currentScreen) {
      RenewlyScreen.Onboarding -> {
        OnboardingScreen(
          step = onboardingStep,
          onStepChange = { onboardingStep = it },
          onComplete = { currency ->
            viewModel.completeOnboarding(currency)
          }
        )
      }
      RenewlyScreen.Settings -> {
        SettingsScreen(
          settings = settings,
          onUpdateSettings = { viewModel.updateSettings(it) },
          onExportJson = { onReady -> viewModel.exportJson(onReady) },
          onExportCsv = { viewModel.exportCsv() },
          onImportJson = { viewModel.importJson(it) },
          onDeleteAllData = { viewModel.deleteAllData() },
          onBack = { viewModel.navigateTo(RenewlyScreen.Main) },
          showToast = { viewModel.showToast(it) }
        )
      }
      is RenewlyScreen.Detail -> {
        SubscriptionDetailScreen(
          subscriptionId = screen.subscriptionId,
          subscriptions = subscriptions,
          chargeRecords = chargeRecords,
          settings = settings,
          onNavigate = { viewModel.navigateTo(it) },
          onBack = { viewModel.navigateTo(RenewlyScreen.Main) },
          onToggleReminder = { viewModel.toggleReminder(it) },
          onPauseThreeMonths = { sub, monthName ->
            viewModel.pauseSubscription(sub, monthName)
          },
          onResumeSubscription = { viewModel.resumeSubscription(it) },
          onConfirmCancel = { sub, savings ->
            viewModel.cancelSubscriptionForGood(sub, savings)
          }
        )
      }
      is RenewlyScreen.Edit -> {
        EditSubscriptionScreen(
          subscriptionId = screen.subscriptionId,
          subscriptions = subscriptions,
          settings = settings,
          onSave = { subId, name, amount, cycle, day, cat, note, barelyUsed ->
            viewModel.updateSubscription(subId, name, amount, cycle, day, cat, note, barelyUsed)
          },
          onDiscard = { viewModel.navigateTo(RenewlyScreen.Detail(screen.subscriptionId)) }
        )
      }
      RenewlyScreen.Main -> {
        when (selectedTab) {
          com.example.ui.components.RenewlyTab.Home -> {
            HomeScreen(
              subscriptions = subscriptions,
              chargeRecords = chargeRecords,
              settings = settings,
              onNavigate = { viewModel.navigateTo(it) },
              onSelectTab = { viewModel.selectTab(it) }
            )
          }
          com.example.ui.components.RenewlyTab.Dates -> {
            DatesScreen(
              subscriptions = subscriptions,
              settings = settings,
              onNavigate = { viewModel.navigateTo(it) }
            )
          }
          com.example.ui.components.RenewlyTab.Insights -> {
            InsightsScreen(
              subscriptions = subscriptions,
              chargeRecords = chargeRecords,
              settings = settings,
              onNavigate = { viewModel.navigateTo(it) }
            )
          }
          com.example.ui.components.RenewlyTab.Add -> {
            AddSubscriptionScreen(
              settings = settings,
              onAddSingle = { name, amount, cycle, date, cat, remind, note ->
                viewModel.addSingleSubscription(name, amount, cycle, date, cat, remind, note)
              },
              onAddBatch = { subs ->
                viewModel.addBatchSubscriptions(subs)
              }
            )
          }
        }
      }
    }

    // Tab bar visible on Main and Detail screens per §4.8
    // "Hidden on: onboarding, edit, and the lock-screen preview. Visible everywhere else."
    val showTabBar = currentScreen is RenewlyScreen.Main

    if (showTabBar) {
      RenewlyTabBar(
        selectedTab = selectedTab,
        onTabSelected = { viewModel.selectTab(it) },
        modifier = Modifier.align(Alignment.BottomCenter)
      )
    }

    // Toast: bottom: 92dp
    RenewlyToast(
      message = toastMessage,
      onDismiss = { viewModel.dismissToast() },
      modifier = Modifier
        .align(Alignment.BottomCenter)
        .padding(bottom = if (showTabBar) 92.dp else 24.dp)
    )
  }
}
