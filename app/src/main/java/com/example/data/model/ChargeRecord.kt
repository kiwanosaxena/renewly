package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "charge_records")
data class ChargeRecord(
  @PrimaryKey(autoGenerate = true)
  val id: Long = 0,
  val subscriptionId: String,
  val date: String, // YYYY-MM-DD
  val amount: Double,
  val source: String = "passed"
)
