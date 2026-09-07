package com.example.notifications

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class RenewlyBootReceiver : BroadcastReceiver() {
  override fun onReceive(context: Context, intent: Intent) {
    // Reschedule everything on device boot, timezone change, time set, or package replaced
    NotificationHelper.scheduleAllReminders(context)
  }
}
