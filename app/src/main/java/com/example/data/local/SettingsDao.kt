package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.UserSettings
import kotlinx.coroutines.flow.Flow

@Dao
interface SettingsDao {
  @Query("SELECT * FROM user_settings WHERE id = 1")
  fun getSettings(): Flow<UserSettings?>

  @Query("SELECT * FROM user_settings WHERE id = 1")
  suspend fun getSettingsDirect(): UserSettings?

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertOrUpdate(settings: UserSettings)

  @Update
  suspend fun update(settings: UserSettings)

  @Query("DELETE FROM user_settings")
  suspend fun deleteAll()
}
