package com.example.notifications

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.data.local.RenewlyDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class RenewlyAlarmReceiver : BroadcastReceiver() {
  override fun onReceive(context: Context, intent: Intent) {
    val action = intent.action
    val subId = intent.getStringExtra("sub_id") ?: return

    val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    if (action == "ACTION_KEEP") {
      notificationManager.cancel(subId.hashCode())
      return
    }

    if (action == "ACTION_SNOOZE") {
      notificationManager.cancel(subId.hashCode())
      // Reschedule for next day
      return
    }

    val name = intent.getStringExtra("sub_name") ?: "Subscription"
    val amount = intent.getDoubleExtra("sub_amount", 0.0)
    val chargeDate = intent.getStringExtra("sub_charge_date") ?: ""
    val leadDays = intent.getIntExtra("sub_lead_days", 2)
    val barelyUsed = intent.getBooleanExtra("sub_barely_used", false)

    CoroutineScope(Dispatchers.IO).launch {
      val db = RenewlyDatabase.getDatabase(context)
      val settings = db.settingsDao().getSettingsDirect()
      val currency = settings?.currency ?: "$"

      NotificationHelper.showNotification(
        context = context,
        subId = subId,
        name = name,
        amount = amount,
        chargeDateIso = chargeDate,
        leadDays = leadDays,
        barelyUsed = barelyUsed,
        currency = currency
      )
    }
  }
}
