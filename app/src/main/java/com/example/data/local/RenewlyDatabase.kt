package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.data.model.ChargeRecord
import com.example.data.model.Subscription
import com.example.data.model.UserSettings

@Database(
  entities = [Subscription::class, ChargeRecord::class, UserSettings::class],
  version = 1,
  exportSchema = false
)
abstract class RenewlyDatabase : RoomDatabase() {
  abstract fun subscriptionDao(): SubscriptionDao
  abstract fun chargeRecordDao(): ChargeRecordDao
  abstract fun settingsDao(): SettingsDao

  companion object {
    @Volatile
    private var INSTANCE: RenewlyDatabase? = null

    fun getDatabase(context: Context): RenewlyDatabase {
      return INSTANCE ?: synchronized(this) {
        val instance = Room.databaseBuilder(
          context.applicationContext,
          RenewlyDatabase::class.java,
          "renewly.db"
        ).fallbackToDestructiveMigration().build()
        INSTANCE = instance
        instance
      }
    }
  }
}
