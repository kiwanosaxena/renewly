package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_settings")
data class UserSettings(
  @PrimaryKey
  val id: Int = 1,
  val currency: String = "$",
  val theme: String = "system", // 'system', 'light', 'dark'
  val userName: String = "",
  val weekStart: String = "mon", // 'mon', 'sun'
  val homeHero: String = "total", // 'total', 'renewal'
  val datesLayout: String = "grid", // 'grid', 'timeline'
  val quietHoursEnabled: Boolean = false,
  val quietHoursStart: Int = 22,
  val quietHoursEnd: Int = 8,
  val defaultRemindDays: Int = 2,
  val onboardingCompleted: Boolean = false
)
