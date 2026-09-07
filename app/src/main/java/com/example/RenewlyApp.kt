package com.example

import android.app.Application
import com.example.data.local.RenewlyDatabase
import com.example.data.repository.SubscriptionRepository
import com.example.notifications.NotificationHelper

class RenewlyApp : Application() {
  lateinit var repository: SubscriptionRepository
    private set

  override fun onCreate() {
    super.onCreate()
    val database = RenewlyDatabase.getDatabase(this)
    repository = SubscriptionRepository(
      database.subscriptionDao(),
      database.chargeRecordDao(),
      database.settingsDao()
    )
    NotificationHelper.createNotificationChannel(this)
    NotificationHelper.scheduleAllReminders(this)
  }
}
