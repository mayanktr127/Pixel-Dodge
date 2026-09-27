package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "game_records")
data class GameRecordEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val score: Int,
    val timestamp: Long = System.currentTimeMillis(),
    val mode: String = "Classic",
    val secondsSurviving: Int = 0,
    val dodgedCount: Int = 0,
    val coinsCollected: Int = 0
)

@Entity(tableName = "user_stats")
data class UserStatsEntity(
    @PrimaryKey
    val id: Int = 1,
    val highScore: Int = 0,
    val totalGamesPlayed: Int = 0,
    val totalScore: Long = 0L,
    val totalXp: Int = 120,
    val gems: Int = 35,
    val streakDays: Int = 5,
    val lastStreakDate: String = "",
    val selectedSkinId: String = "ship_classic",
    val soundEnabled: Boolean = true,
    val hapticsEnabled: Boolean = true
)

@Entity(tableName = "badges")
data class BadgeEntity(
    @PrimaryKey
    val id: String,
    val title: String,
    val description: String,
    val category: String, // "STARTER", "SKILL", "COLLECTION", "CHAMPION"
    val isUnlocked: Boolean = false,
    val unlockedDate: Long? = null
)
