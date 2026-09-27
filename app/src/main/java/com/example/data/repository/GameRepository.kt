package com.example.data.repository

import com.example.data.db.GameDao
import com.example.data.model.BadgeEntity
import com.example.data.model.GameRecordEntity
import com.example.data.model.UserStatsEntity
import kotlinx.coroutines.flow.Flow
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class Skin(
    val id: String,
    val name: String,
    val description: String,
    val priceGems: Int,
    val colorHex: Long,
    val isUnlockedByDefault: Boolean = false
)

val AVAILABLE_SKINS = listOf(
    Skin("ship_classic", "Pixel Dodger", "Classic nimble retro vessel", 0, 0xFF38BDF8, true),
    Skin("cyber_cat", "Cyber Kitty", "Agile 9-lived digital feline", 30, 0xFFF43F5E, false),
    Skin("knight_8bit", "8-Bit Paladin", "Armored shield-bearing hero", 50, 0xFFF59E0B, false),
    Skin("pixel_ufo", "Retro Saucer", "Futuristic anti-gravity glider", 75, 0xFF10B981, false),
    Skin("glitch_phantom", "Neon Ghost", "Ethereal phase-shifting entity", 100, 0xFF8B5CF6, false)
)

class GameRepository(private val gameDao: GameDao) {

    val userStats: Flow<UserStatsEntity?> = gameDao.getUserStatsFlow()
    val topScores: Flow<List<GameRecordEntity>> = gameDao.getTopScoresFlow()
    val recentGames: Flow<List<GameRecordEntity>> = gameDao.getRecentGamesFlow()
    val badges: Flow<List<BadgeEntity>> = gameDao.getAllBadgesFlow()

    suspend fun initializeDefaultsIfNeeded() {
        val existingStats = gameDao.getUserStats()
        val todayStr = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        if (existingStats == null) {
            gameDao.insertOrUpdateStats(
                UserStatsEntity(
                    id = 1,
                    highScore = 0,
                    totalGamesPlayed = 0,
                    totalScore = 0L,
                    totalXp = 0,
                    gems = 25,
                    streakDays = 5,
                    lastStreakDate = todayStr,
                    selectedSkinId = "ship_classic"
                )
            )
        }

        val existingBadges = gameDao.getAllBadges()
        if (existingBadges.isEmpty()) {
            val initialBadges = listOf(
                BadgeEntity("starter", "Starter", "Play your very first dodge run", "STARTER", true, System.currentTimeMillis()),
                BadgeEntity("quick_learner", "Quick Learner", "Reach 100 score in any mode", "SKILL", false),
                BadgeEntity("top_scorer", "Top Scorer", "Break 500 points in a single run", "SKILL", false),
                BadgeEntity("helper", "Star Collector", "Collect 20 glowing pixel stars", "COLLECTION", false),
                BadgeEntity("champion", "Dodger Master", "Survive over 60 seconds", "CHAMPION", false),
                BadgeEntity("streak_fire", "On Fire", "Maintain a 5-day dodge streak", "STREAK", true, System.currentTimeMillis())
            )
            gameDao.insertBadges(initialBadges)
        }
    }

    suspend fun recordGameRun(
        score: Int,
        mode: String,
        durationSeconds: Int,
        dodgedCount: Int,
        coinsCollected: Int
    ): UserStatsEntity {
        val currentStats = gameDao.getUserStats() ?: UserStatsEntity()
        val isNewHighScore = score > currentStats.highScore
        val newHighScore = if (isNewHighScore) score else currentStats.highScore
        val newGems = currentStats.gems + coinsCollected
        val gainedXp = (score * 1.5).toInt() + (durationSeconds * 2) + (coinsCollected * 5)
        val newTotalXp = currentStats.totalXp + gainedXp
        val newTotalGames = currentStats.totalGamesPlayed + 1
        val newTotalScore = currentStats.totalScore + score

        // Daily streak update
        val todayStr = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        val streak = if (currentStats.lastStreakDate == todayStr) {
            currentStats.streakDays
        } else {
            currentStats.streakDays + 1
        }

        val updatedStats = currentStats.copy(
            highScore = newHighScore,
            totalGamesPlayed = newTotalGames,
            totalScore = newTotalScore,
            totalXp = newTotalXp,
            gems = newGems,
            streakDays = streak,
            lastStreakDate = todayStr
        )
        gameDao.insertOrUpdateStats(updatedStats)

        // Record run history
        gameDao.insertRecord(
            GameRecordEntity(
                score = score,
                timestamp = System.currentTimeMillis(),
                mode = mode,
                secondsSurviving = durationSeconds,
                dodgedCount = dodgedCount,
                coinsCollected = coinsCollected
            )
        )

        // Evaluate badge unlocks
        checkAndUnlockBadges(score, durationSeconds, coinsCollected, newTotalGames)

        return updatedStats
    }

    private suspend fun checkAndUnlockBadges(
        score: Int,
        durationSeconds: Int,
        coinsCollected: Int,
        totalGames: Int
    ) {
        val badges = gameDao.getAllBadges()
        for (b in badges) {
            if (b.isUnlocked) continue
            var unlock = false
            when (b.id) {
                "starter" -> if (totalGames >= 1) unlock = true
                "quick_learner" -> if (score >= 100) unlock = true
                "top_scorer" -> if (score >= 500) unlock = true
                "helper" -> if (coinsCollected >= 10) unlock = true
                "champion" -> if (durationSeconds >= 60) unlock = true
            }
            if (unlock) {
                gameDao.updateBadge(b.copy(isUnlocked = true, unlockedDate = System.currentTimeMillis()))
            }
        }
    }

    suspend fun selectSkin(skinId: String) {
        val stats = gameDao.getUserStats() ?: return
        gameDao.insertOrUpdateStats(stats.copy(selectedSkinId = skinId))
    }

    suspend fun buySkin(skin: Skin): Boolean {
        val stats = gameDao.getUserStats() ?: return false
        if (stats.gems >= skin.priceGems) {
            gameDao.insertOrUpdateStats(stats.copy(gems = stats.gems - skin.priceGems, selectedSkinId = skin.id))
            return true
        }
        return false
    }

    suspend fun addBonusGems(amount: Int) {
        val stats = gameDao.getUserStats() ?: return
        gameDao.insertOrUpdateStats(stats.copy(gems = stats.gems + amount, totalXp = stats.totalXp + amount * 3))
    }

    suspend fun toggleSound() {
        val stats = gameDao.getUserStats() ?: return
        gameDao.insertOrUpdateStats(stats.copy(soundEnabled = !stats.soundEnabled))
    }

    suspend fun toggleHaptics() {
        val stats = gameDao.getUserStats() ?: return
        gameDao.insertOrUpdateStats(stats.copy(hapticsEnabled = !stats.hapticsEnabled))
    }
}
