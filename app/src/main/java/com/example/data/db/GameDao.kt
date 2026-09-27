package com.example.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.BadgeEntity
import com.example.data.model.GameRecordEntity
import com.example.data.model.UserStatsEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface GameDao {
    @Query("SELECT * FROM user_stats WHERE id = 1")
    fun getUserStatsFlow(): Flow<UserStatsEntity?>

    @Query("SELECT * FROM user_stats WHERE id = 1")
    suspend fun getUserStats(): UserStatsEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateStats(stats: UserStatsEntity)

    @Insert
    suspend fun insertRecord(record: GameRecordEntity)

    @Query("SELECT * FROM game_records ORDER BY score DESC LIMIT 10")
    fun getTopScoresFlow(): Flow<List<GameRecordEntity>>

    @Query("SELECT * FROM game_records ORDER BY timestamp DESC LIMIT 20")
    fun getRecentGamesFlow(): Flow<List<GameRecordEntity>>

    @Query("SELECT * FROM badges")
    fun getAllBadgesFlow(): Flow<List<BadgeEntity>>

    @Query("SELECT * FROM badges")
    suspend fun getAllBadges(): List<BadgeEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBadges(badges: List<BadgeEntity>)

    @Update
    suspend fun updateBadge(badge: BadgeEntity)
}
