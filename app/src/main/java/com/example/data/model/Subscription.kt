package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "subscriptions")
data class Subscription(
  @PrimaryKey
  val id: String = UUID.randomUUID().toString(),
  val name: String,
  val amount: Double,
  val cycle: String, // 'monthly' | 'yearly'
  val chargeDay: Int, // 1-28
  val firstCharge: String, // YYYY-MM-DD
  val category: String,
  val paused: Boolean = false,
  val pausedUntil: String? = null, // YYYY-MM-DD
  val barelyUsed: Boolean = false,
  val remind: Boolean = true,
  val remindDays: Int = 2,
  val note: String = "",
  val createdAt: String, // ISO datetime
  val cancelledAt: String? = null // ISO datetime
)
