package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.Subscription
import kotlinx.coroutines.flow.Flow

@Dao
interface SubscriptionDao {
  @Query("SELECT * FROM subscriptions ORDER BY chargeDay ASC, name ASC")
  fun getAllSubscriptions(): Flow<List<Subscription>>

  @Query("SELECT * FROM subscriptions WHERE id = :id")
  suspend fun getSubscriptionById(id: String): Subscription?

  @Query("SELECT * FROM subscriptions WHERE id = :id")
  fun getSubscriptionFlow(id: String): Flow<Subscription?>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertSubscription(subscription: Subscription)

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertAll(subscriptions: List<Subscription>)

  @Update
  suspend fun updateSubscription(subscription: Subscription)

  @Query("DELETE FROM subscriptions WHERE id = :id")
  suspend fun deleteSubscription(id: String)

  @Query("DELETE FROM subscriptions")
  suspend fun deleteAll()
}
