package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.model.ChargeRecord
import kotlinx.coroutines.flow.Flow

@Dao
interface ChargeRecordDao {
  @Query("SELECT * FROM charge_records ORDER BY date DESC")
  fun getAllChargeRecords(): Flow<List<ChargeRecord>>

  @Query("SELECT * FROM charge_records WHERE subscriptionId = :subscriptionId ORDER BY date DESC")
  fun getRecordsForSubscription(subscriptionId: String): Flow<List<ChargeRecord>>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertRecord(record: ChargeRecord)

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertAll(records: List<ChargeRecord>)

  @Query("DELETE FROM charge_records WHERE subscriptionId = :subscriptionId")
  suspend fun deleteForSubscription(subscriptionId: String)

  @Query("DELETE FROM charge_records")
  suspend fun deleteAll()
}
