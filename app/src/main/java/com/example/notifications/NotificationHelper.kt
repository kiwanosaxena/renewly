package com.example.notifications

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.MainActivity
import com.example.R
import com.example.data.local.RenewlyDatabase
import com.example.data.model.ChargeRecord
import com.example.data.model.Subscription
import com.example.data.repository.RenewlyCalculations
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

object NotificationHelper {
  const val CHANNEL_ID = "renewal_reminders"
  const val CHANNEL_NAME = "Renewal reminders"

  fun createNotificationChannel(context: Context) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
      val channel = NotificationChannel(
        CHANNEL_ID,
        CHANNEL_NAME,
        NotificationManager.IMPORTANCE_DEFAULT
      ).apply {
        description = "Reminders before subscription renewals"
        setShowBadge(false)
        enableVibration(false)
      }
      val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
      notificationManager.createNotificationChannel(channel)
    }
  }

  fun scheduleAllReminders(context: Context) {
    CoroutineScope(Dispatchers.IO).launch {
      try {
        val db = RenewlyDatabase.getDatabase(context)
        val subs = db.subscriptionDao().getAllSubscriptions().firstOrNull() ?: emptyList()
        val settings = db.settingsDao().getSettingsDirect()
        val today = LocalDate.now()

        // Also record passed charges if any have passed today or earlier
        for (s in subs) {
          if (!s.paused && s.cancelledAt == null) {
            checkAndRecordPassedCharges(db, s, today)
            if (s.remind) {
              scheduleSubscriptionReminder(context, s, settings?.quietHoursEnabled == true, settings?.quietHoursStart ?: 22, settings?.quietHoursEnd ?: 8)
            }
          }
        }
      } catch (e: Exception) {
        // Safe fail-through
      }
    }
  }

  private suspend fun checkAndRecordPassedCharges(
    db: RenewlyDatabase,
    sub: Subscription,
    today: LocalDate
  ) {
    // Only record passed charge if today is the charge day or it passed since app run
    val todayIso = today.toString()
    if (today.dayOfMonth == sub.chargeDay) {
      val existing = db.chargeRecordDao().getRecordsForSubscription(sub.id).firstOrNull() ?: emptyList()
      if (existing.none { it.date == todayIso }) {
        db.chargeRecordDao().insertRecord(
          ChargeRecord(
            subscriptionId = sub.id,
            date = todayIso,
            amount = sub.amount,
            source = "passed"
          )
        )
      }
    }
  }

  fun scheduleSubscriptionReminder(
    context: Context,
    sub: Subscription,
    quietHoursEnabled: Boolean = false,
    quietHoursStart: Int = 22,
    quietHoursEnd: Int = 8
  ) {
    if (!sub.remind || sub.paused || sub.cancelledAt != null) {
      cancelReminder(context, sub.id)
      return
    }

    val today = LocalDate.now()
    val nextCharge = RenewlyCalculations.calculateNextChargeDate(sub, today)
    val reminderDate = nextCharge.minusDays(sub.remindDays.toLong())

    // Reminder time: 09:00 local time, or end of quiet hours if inside
    var targetHour = 9
    var targetMinute = 0
    if (quietHoursEnabled) {
      // If 9 AM falls within quiet hours (e.g., quiet 22 to 10), move to quietHoursEnd
      val isInQuiet = if (quietHoursStart > quietHoursEnd) {
        targetHour >= quietHoursStart || targetHour < quietHoursEnd
      } else {
        targetHour in quietHoursStart until quietHoursEnd
      }
      if (isInQuiet) {
        targetHour = quietHoursEnd
      }
    }

    val scheduledDateTime = LocalDateTime.of(
      if (reminderDate.isBefore(today)) today else reminderDate,
      LocalTime.of(targetHour, targetMinute)
    )

    val targetMillis = scheduledDateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
    if (targetMillis <= System.currentTimeMillis()) {
      // If past for today, don't trigger retroactively
      return
    }

    val intent = Intent(context, RenewlyAlarmReceiver::class.java).apply {
      putExtra("sub_id", sub.id)
      putExtra("sub_name", sub.name)
      putExtra("sub_amount", sub.amount)
      putExtra("sub_charge_date", nextCharge.toString())
      putExtra("sub_lead_days", sub.remindDays)
      putExtra("sub_barely_used", sub.barelyUsed)
    }

    val pendingIntent = PendingIntent.getBroadcast(
      context,
      sub.id.hashCode(),
      intent,
      PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )

    val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager
    alarmManager?.setAndAllowWhileIdle(
      AlarmManager.RTC_WAKEUP,
      targetMillis,
      pendingIntent
    )
  }

  fun cancelReminder(context: Context, subId: String) {
    val intent = Intent(context, RenewlyAlarmReceiver::class.java)
    val pendingIntent = PendingIntent.getBroadcast(
      context,
      subId.hashCode(),
      intent,
      PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE
    )
    if (pendingIntent != null) {
      val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager
      alarmManager?.cancel(pendingIntent)
      pendingIntent.cancel()
    }
  }

  fun showNotification(
    context: Context,
    subId: String,
    name: String,
    amount: Double,
    chargeDateIso: String,
    leadDays: Int,
    barelyUsed: Boolean,
    currency: String = "$"
  ) {
    createNotificationChannel(context)

    val parsedDate = try {
      LocalDate.parse(chargeDateIso)
    } catch (e: Exception) {
      LocalDate.now()
    }
    val monD = parsedDate.format(DateTimeFormatter.ofPattern("MMM d", Locale.US))

    // §5.8 Content rules:
    // Title: {name} renews in {n} days (or renews tomorrow / renews today)
    val timingStr = when (leadDays) {
      0 -> "today"
      1 -> "tomorrow"
      else -> "in $leadDays days"
    }
    val title = "$name renews $timingStr"

    // Body: {amount} leaves on {Mon} {D}.
    val formattedAmount = RenewlyCalculations.formatCurrency(amount, currency)
    var body = "$formattedAmount leaves on $monD."
    if (barelyUsed) {
      body += " You marked this as barely used."
    }

    val detailIntent = Intent(context, MainActivity::class.java).apply {
      flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
      putExtra("open_subscription_id", subId)
    }
    val contentPendingIntent = PendingIntent.getActivity(
      context,
      subId.hashCode(),
      detailIntent,
      PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )

    // Snooze action (re-ask on next cycle)
    val snoozeIntent = Intent(context, RenewlyAlarmReceiver::class.java).apply {
      action = "ACTION_SNOOZE"
      putExtra("sub_id", subId)
    }
    val snoozePending = PendingIntent.getBroadcast(
      context,
      subId.hashCode() + 100,
      snoozeIntent,
      PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )

    // Keep it action (silence this occurrence only)
    val keepIntent = Intent(context, RenewlyAlarmReceiver::class.java).apply {
      action = "ACTION_KEEP"
      putExtra("sub_id", subId)
    }
    val keepPending = PendingIntent.getBroadcast(
      context,
      subId.hashCode() + 200,
      keepIntent,
      PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )

    val notification = NotificationCompat.Builder(context, CHANNEL_ID)
      .setSmallIcon(R.mipmap.ic_launcher)
      .setContentTitle(title)
      .setContentText(body)
      .setColor(0xFFE8492C.toInt())
      .setContentIntent(contentPendingIntent)
      .setAutoCancel(true)
      .addAction(0, "Snooze", snoozePending)
      .addAction(0, "Keep it", keepPending)
      .build()

    val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    notificationManager.notify(subId.hashCode(), notification)
  }
}
