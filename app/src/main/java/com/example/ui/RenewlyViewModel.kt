package com.example.ui

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.RenewlyApp
import com.example.data.model.ChargeRecord
import com.example.data.model.Subscription
import com.example.data.model.UserSettings
import com.example.data.repository.RenewlyCalculations
import com.example.notifications.NotificationHelper
import com.example.ui.components.RenewlyTab
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale
import java.util.UUID

sealed interface RenewlyScreen {
  object Main : RenewlyScreen
  object Settings : RenewlyScreen
  data class Detail(val subscriptionId: String) : RenewlyScreen
  data class Edit(val subscriptionId: String) : RenewlyScreen
  object Onboarding : RenewlyScreen
}

class RenewlyViewModel(application: Application) : AndroidViewModel(application) {
  private val repository = (application as RenewlyApp).repository

  val subscriptions: StateFlow<List<Subscription>> = repository.subscriptions
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  val chargeRecords: StateFlow<List<ChargeRecord>> = repository.chargeRecords
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  val settings: StateFlow<UserSettings> = repository.settings
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), UserSettings())

  private val _currentScreen = MutableStateFlow<RenewlyScreen>(RenewlyScreen.Main)
  val currentScreen: StateFlow<RenewlyScreen> = _currentScreen.asStateFlow()

  private val _selectedTab = MutableStateFlow(RenewlyTab.Home)
  val selectedTab: StateFlow<RenewlyTab> = _selectedTab.asStateFlow()

  private val _toastMessage = MutableStateFlow<String?>(null)
  val toastMessage: StateFlow<String?> = _toastMessage.asStateFlow()

  private val _selectedDate = MutableStateFlow(LocalDate.now())
  val selectedDate: StateFlow<LocalDate> = _selectedDate.asStateFlow()

  init {
    viewModelScope.launch {
      val direct = repository.getSettingsDirect()
      if (!direct.onboardingCompleted) {
        _currentScreen.value = RenewlyScreen.Onboarding
      }
    }
  }

  fun navigateTo(screen: RenewlyScreen) {
    _currentScreen.value = screen
  }

  fun selectTab(tab: RenewlyTab) {
    _selectedTab.value = tab
    _currentScreen.value = RenewlyScreen.Main
  }

  fun showToast(message: String) {
    _toastMessage.value = message
  }

  fun dismissToast() {
    _toastMessage.value = null
  }

  fun setSelectedDate(date: LocalDate) {
    _selectedDate.value = date
  }

  fun completeOnboarding(currency: String) {
    viewModelScope.launch {
      val current = settings.value
      repository.updateSettings(
        current.copy(
          currency = currency,
          onboardingCompleted = true
        )
      )
      _currentScreen.value = RenewlyScreen.Main
      _selectedTab.value = RenewlyTab.Add
    }
  }

  fun addSingleSubscription(
    name: String,
    amount: Double,
    cycle: String,
    firstCharge: LocalDate,
    category: String,
    remind: Boolean,
    note: String
  ) {
    viewModelScope.launch {
      val cleanName = name.trim()
      val sub = Subscription(
        id = UUID.randomUUID().toString(),
        name = cleanName,
        amount = amount,
        cycle = cycle.lowercase(),
        chargeDay = firstCharge.dayOfMonth.coerceIn(1, 28),
        firstCharge = firstCharge.toString(),
        category = category,
        remind = remind,
        remindDays = settings.value.defaultRemindDays,
        note = note.trim(),
        createdAt = LocalDate.now().toString()
      )
      repository.addSubscription(sub)
      NotificationHelper.scheduleSubscriptionReminder(
        context = getApplication(),
        sub = sub,
        quietHoursEnabled = settings.value.quietHoursEnabled,
        quietHoursStart = settings.value.quietHoursStart,
        quietHoursEnd = settings.value.quietHoursEnd
      )
      val newTotal = RenewlyCalculations.formatCurrency(
        RenewlyCalculations.monthlyTotal(subscriptions.value + sub),
        settings.value.currency
      )
      showToast("1 subscription tracked. $newTotal a month now.")
      _currentScreen.value = RenewlyScreen.Main
      _selectedTab.value = RenewlyTab.Home
    }
  }

  fun addBatchSubscriptions(subs: List<Subscription>) {
    viewModelScope.launch {
      repository.addSubscriptions(subs)
      for (s in subs) {
        NotificationHelper.scheduleSubscriptionReminder(
          context = getApplication(),
          sub = s,
          quietHoursEnabled = settings.value.quietHoursEnabled,
          quietHoursStart = settings.value.quietHoursStart,
          quietHoursEnd = settings.value.quietHoursEnd
        )
      }
      val newTotal = RenewlyCalculations.formatCurrency(
        RenewlyCalculations.monthlyTotal(subscriptions.value + subs),
        settings.value.currency
      )
      val countStr = if (subs.size == 1) "1 subscription" else "${subs.size} subscriptions"
      showToast("$countStr tracked. $newTotal a month now.")
      _currentScreen.value = RenewlyScreen.Main
      _selectedTab.value = RenewlyTab.Home
    }
  }

  fun updateSubscription(
    subId: String,
    name: String,
    amount: Double,
    cycle: String,
    chargeDay: Int,
    category: String,
    note: String,
    barelyUsed: Boolean
  ) {
    viewModelScope.launch {
      val existing = subscriptions.value.firstOrNull { it.id == subId } ?: return@launch
      val updated = existing.copy(
        name = name.trim(),
        amount = amount,
        cycle = cycle.lowercase(),
        chargeDay = chargeDay.coerceIn(1, 28),
        category = category,
        note = note.trim(),
        barelyUsed = barelyUsed
      )
      repository.updateSubscription(updated)
      NotificationHelper.scheduleSubscriptionReminder(
        context = getApplication(),
        sub = updated,
        quietHoursEnabled = settings.value.quietHoursEnabled,
        quietHoursStart = settings.value.quietHoursStart,
        quietHoursEnd = settings.value.quietHoursEnd
      )
      showToast("${updated.name} updated.")
      _currentScreen.value = RenewlyScreen.Detail(subId)
    }
  }

  fun toggleReminder(sub: Subscription) {
    viewModelScope.launch {
      val updated = sub.copy(remind = !sub.remind)
      repository.updateSubscription(updated)
      if (updated.remind) {
        NotificationHelper.scheduleSubscriptionReminder(
          context = getApplication(),
          sub = updated,
          quietHoursEnabled = settings.value.quietHoursEnabled,
          quietHoursStart = settings.value.quietHoursStart,
          quietHoursEnd = settings.value.quietHoursEnd
        )
      } else {
        NotificationHelper.cancelReminder(getApplication(), updated.id)
      }
    }
  }

  fun pauseSubscription(sub: Subscription, resumeMonthName: String) {
    viewModelScope.launch {
      val threeMonthsOut = LocalDate.now().plusMonths(3).toString()
      val updated = sub.copy(
        paused = true,
        pausedUntil = threeMonthsOut
      )
      repository.updateSubscription(updated)
      NotificationHelper.cancelReminder(getApplication(), updated.id)
      showToast("${sub.name} paused for 3 months. Out of your total until $resumeMonthName.")
      _currentScreen.value = RenewlyScreen.Main
      _selectedTab.value = RenewlyTab.Home
    }
  }

  fun resumeSubscription(sub: Subscription) {
    viewModelScope.launch {
      val updated = sub.copy(
        paused = false,
        pausedUntil = null
      )
      repository.updateSubscription(updated)
      NotificationHelper.scheduleSubscriptionReminder(
        context = getApplication(),
        sub = updated,
        quietHoursEnabled = settings.value.quietHoursEnabled,
        quietHoursStart = settings.value.quietHoursStart,
        quietHoursEnd = settings.value.quietHoursEnd
      )
      showToast("${sub.name} resumed.")
      _currentScreen.value = RenewlyScreen.Detail(sub.id)
    }
  }

  fun cancelSubscriptionForGood(sub: Subscription, annualSavingsStr: String) {
    viewModelScope.launch {
      val updated = sub.copy(cancelledAt = LocalDate.now().toString())
      repository.updateSubscription(updated)
      NotificationHelper.cancelReminder(getApplication(), updated.id)
      showToast("${sub.name} cancelled — $annualSavingsStr a year back in your pocket.")
      _currentScreen.value = RenewlyScreen.Main
      _selectedTab.value = RenewlyTab.Home
    }
  }

  fun updateSettings(newSettings: UserSettings) {
    viewModelScope.launch {
      repository.updateSettings(newSettings)
      NotificationHelper.scheduleAllReminders(getApplication())
    }
  }

  fun exportJson(onReady: (String) -> Unit) {
    viewModelScope.launch {
      val json = repository.exportJson(subscriptions.value)
      onReady(json)
    }
  }

  fun exportCsv(): String {
    return repository.exportCsv(subscriptions.value)
  }

  fun importJson(jsonStr: String) {
    viewModelScope.launch {
      val count = repository.importJson(jsonStr)
      if (count > 0) {
        showToast("Imported $count subscriptions.")
        NotificationHelper.scheduleAllReminders(getApplication())
      } else {
        showToast("No valid subscriptions found in import.")
      }
    }
  }

  fun deleteAllData() {
    viewModelScope.launch {
      for (s in subscriptions.value) {
        NotificationHelper.cancelReminder(getApplication(), s.id)
      }
      repository.deleteAllData()
      showToast("All data deleted.")
      _currentScreen.value = RenewlyScreen.Main
      _selectedTab.value = RenewlyTab.Home
    }
  }
}
