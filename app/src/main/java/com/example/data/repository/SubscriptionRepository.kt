package com.example.data.repository

import com.example.data.local.ChargeRecordDao
import com.example.data.local.SettingsDao
import com.example.data.local.SubscriptionDao
import com.example.data.model.ChargeRecord
import com.example.data.model.Subscription
import com.example.data.model.UserSettings
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.json.JSONArray
import org.json.JSONObject
import java.time.LocalDate

class SubscriptionRepository(
  private val subscriptionDao: SubscriptionDao,
  private val chargeRecordDao: ChargeRecordDao,
  private val settingsDao: SettingsDao
) {
  val subscriptions: Flow<List<Subscription>> = subscriptionDao.getAllSubscriptions()
  val chargeRecords: Flow<List<ChargeRecord>> = chargeRecordDao.getAllChargeRecords()
  val settings: Flow<UserSettings> = settingsDao.getSettings().map { it ?: UserSettings() }

  suspend fun getSettingsDirect(): UserSettings {
    return settingsDao.getSettingsDirect() ?: UserSettings()
  }

  suspend fun updateSettings(userSettings: UserSettings) {
    settingsDao.insertOrUpdate(userSettings)
  }

  suspend fun addSubscription(subscription: Subscription) {
    subscriptionDao.insertSubscription(subscription)
  }

  suspend fun addSubscriptions(list: List<Subscription>) {
    subscriptionDao.insertAll(list)
  }

  suspend fun updateSubscription(subscription: Subscription) {
    subscriptionDao.updateSubscription(subscription)
  }

  suspend fun deleteSubscription(id: String) {
    subscriptionDao.deleteSubscription(id)
    chargeRecordDao.deleteForSubscription(id)
  }

  fun getSubscriptionFlow(id: String): Flow<Subscription?> {
    return subscriptionDao.getSubscriptionFlow(id)
  }

  fun getRecordsForSubscription(id: String): Flow<List<ChargeRecord>> {
    return chargeRecordDao.getRecordsForSubscription(id)
  }

  suspend fun addChargeRecord(record: ChargeRecord) {
    chargeRecordDao.insertRecord(record)
  }

  suspend fun deleteAllData() {
    subscriptionDao.deleteAll()
    chargeRecordDao.deleteAll()
    settingsDao.deleteAll()
  }

  // Export JSON
  suspend fun exportJson(subs: List<Subscription>): String {
    val root = JSONObject()
    root.put("version", 1)
    root.put("exportedAt", LocalDate.now().toString())
    val arr = JSONArray()
    for (s in subs) {
      val obj = JSONObject()
      obj.put("id", s.id)
      obj.put("name", s.name)
      obj.put("amount", s.amount)
      obj.put("cycle", s.cycle)
      obj.put("chargeDay", s.chargeDay)
      obj.put("firstCharge", s.firstCharge)
      obj.put("category", s.category)
      obj.put("paused", s.paused)
      obj.put("pausedUntil", s.pausedUntil ?: JSONObject.NULL)
      obj.put("barelyUsed", s.barelyUsed)
      obj.put("remind", s.remind)
      obj.put("remindDays", s.remindDays)
      obj.put("note", s.note)
      obj.put("createdAt", s.createdAt)
      obj.put("cancelledAt", s.cancelledAt ?: JSONObject.NULL)
      arr.put(obj)
    }
    root.put("subscriptions", arr)
    return root.toString(2)
  }

  // Export CSV
  fun exportCsv(subs: List<Subscription>): String {
    val sb = StringBuilder()
    sb.append("Name,Amount,Cycle,Charge Day,First Charge,Category,Status,Barely Used,Remind,Note\n")
    for (s in subs) {
      val status = if (s.cancelledAt != null) "Cancelled" else if (s.paused) "Paused" else "Active"
      val escapedName = "\"${s.name.replace("\"", "\"\"")}\""
      val escapedNote = "\"${s.note.replace("\"", "\"\"")}\""
      sb.append("$escapedName,${s.amount},${s.cycle},${s.chargeDay},${s.firstCharge},${s.category},$status,${s.barelyUsed},${s.remind},$escapedNote\n")
    }
    return sb.toString()
  }

  // Import JSON: returns count of imported subscriptions
  suspend fun importJson(jsonStr: String): Int {
    return try {
      val root = JSONObject(jsonStr)
      val arr = root.optJSONArray("subscriptions") ?: JSONArray()
      val list = mutableListOf<Subscription>()
      for (i in 0 until arr.length()) {
        val obj = arr.getJSONObject(i)
        val name = obj.getString("name").trim()
        val amount = obj.getDouble("amount")
        val cycle = obj.optString("cycle", "monthly")
        val chargeDay = obj.optInt("chargeDay", 1)
        val firstCharge = obj.optString("firstCharge", LocalDate.now().toString())
        val category = obj.optString("category", "Software")
        val paused = obj.optBoolean("paused", false)
        val pausedUntil = if (obj.isNull("pausedUntil")) null else obj.optString("pausedUntil")
        val barelyUsed = obj.optBoolean("barelyUsed", false)
        val remind = obj.optBoolean("remind", true)
        val remindDays = obj.optInt("remindDays", 2)
        val note = obj.optString("note", "")
        val createdAt = obj.optString("createdAt", LocalDate.now().toString())
        val cancelledAt = if (obj.isNull("cancelledAt")) null else obj.optString("cancelledAt")

        list.add(
          Subscription(
            id = obj.optString("id", java.util.UUID.randomUUID().toString()),
            name = name,
            amount = amount,
            cycle = cycle,
            chargeDay = chargeDay.coerceIn(1, 28),
            firstCharge = firstCharge,
            category = category,
            paused = paused,
            pausedUntil = pausedUntil,
            barelyUsed = barelyUsed,
            remind = remind,
            remindDays = remindDays,
            note = note,
            createdAt = createdAt,
            cancelledAt = cancelledAt
          )
        )
      }
      if (list.isNotEmpty()) {
        subscriptionDao.insertAll(list)
      }
      list.size
    } catch (e: Exception) {
      0
    }
  }
}
