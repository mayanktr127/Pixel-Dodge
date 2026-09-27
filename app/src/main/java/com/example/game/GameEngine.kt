package com.example.game

import androidx.compose.ui.graphics.Color
import com.example.audio.RetroAudioPlayer
import kotlin.math.abs
import kotlin.random.Random

data class Obstacle(
    val id: Long,
    var x: Float, // 0.0 to 1.0 (normalized screen width)
    var y: Float, // 0.0 to 1.0 (normalized screen height)
    val speed: Float,
    val size: Float, // in dp/fraction
    val type: ObstacleType,
    var dodged: Boolean = false
)

data class Collectible(
    val id: Long,
    var x: Float,
    var y: Float,
    val speed: Float,
    val size: Float,
    val type: CollectibleType
)

data class PixelParticle(
    var x: Float,
    var y: Float,
    var vx: Float,
    var vy: Float,
    var size: Float,
    val color: Color,
    var alpha: Float = 1.0f,
    var life: Float = 1.0f
)

data class FloatingText(
    val id: Long,
    val text: String,
    var x: Float,
    var y: Float,
    val color: Color,
    var alpha: Float = 1.0f,
    var life: Float = 1.0f
)

data class GameState(
    val score: Int = 0,
    val highScore: Int = 0,
    val lives: Int = 3,
    val multiplier: Int = 1,
    val multiplierTimer: Float = 0f,
    val playerX: Float = 0.5f,
    val playerTargetX: Float = 0.5f,
    val isShieldActive: Boolean = false,
    val shieldHitsLeft: Int = 0,
    val isSlowMoActive: Boolean = false,
    val slowMoTimer: Float = 0f,
    val isInvulnerable: Boolean = false,
    val invulnerableTimer: Float = 0f,
    val dashCooldown: Float = 0f,
    val durationSeconds: Int = 0,
    val dodgedCount: Int = 0,
    val gemsCollectedThisRun: Int = 0,
    val comboCount: Int = 0,
    val isGameOver: Boolean = false,
    val isPaused: Boolean = false,
    val currentMode: String = "Classic",
    val selectedSkin: String = "ship_classic"
)

class GameSession(
    val mode: String,
    val skinId: String,
    var highScore: Int,
    private val audioPlayer: RetroAudioPlayer,
    val onGameOver: (score: Int, duration: Int, dodged: Int, gems: Int) -> Unit
) {
    var state = GameState(
        currentMode = mode,
        selectedSkin = skinId,
        highScore = highScore
    )
        private set

    val obstacles = mutableListOf<Obstacle>()
    val collectibles = mutableListOf<Collectible>()
    val particles = mutableListOf<PixelParticle>()
    val floatingTexts = mutableListOf<FloatingText>()

    private var nextEntityId = 1L
    private var spawnTimer = 0f
    private var collectibleTimer = 0f
    private var survivalTimerAcc = 0f
    var engineTick = 0
        private set

    fun setPlayerTargetX(x: Float) {
        if (state.isGameOver || state.isPaused) return
        state = state.copy(playerTargetX = x.coerceIn(0.08f, 0.92f))
    }

    fun movePlayer(deltaX: Float) {
        if (state.isGameOver || state.isPaused) return
        val newTarget = (state.playerTargetX + deltaX).coerceIn(0.08f, 0.92f)
        state = state.copy(playerTargetX = newTarget)
    }

    fun triggerDash(direction: Float) { // -1f for left, +1f for right
        if (state.isGameOver || state.isPaused || state.dashCooldown > 0f) return
        val dashDistance = 0.22f * direction
        val newTarget = (state.playerX + dashDistance).coerceIn(0.08f, 0.92f)
        state = state.copy(
            playerX = newTarget,
            playerTargetX = newTarget,
            dashCooldown = 1.2f
        )
        audioPlayer.playDodge()
        // Dash particle burst
        spawnDashParticles(newTarget, 0.85f)
    }

    fun togglePause() {
        if (!state.isGameOver) {
            state = state.copy(isPaused = !state.isPaused)
        }
    }

    fun update(deltaSeconds: Float) {
        if (state.isGameOver || state.isPaused) return
        engineTick++

        // Smooth player interpolation
        val lerpFactor = 15f * deltaSeconds
        val newPlayerX = state.playerX + (state.playerTargetX - state.playerX) * lerpFactor.coerceAtMost(1f)

        // Timers
        val newDashCooldown = (state.dashCooldown - deltaSeconds).coerceAtLeast(0f)
        val newInvulnerableTimer = (state.invulnerableTimer - deltaSeconds).coerceAtLeast(0f)
        val isInvulnerable = newInvulnerableTimer > 0f

        var newSlowMoTimer = (state.slowMoTimer - deltaSeconds).coerceAtLeast(0f)
        val isSlowMo = newSlowMoTimer > 0f

        var newMultiplierTimer = (state.multiplierTimer - deltaSeconds).coerceAtLeast(0f)
        val multiplier = if (newMultiplierTimer > 0f) state.multiplier else 1

        survivalTimerAcc += deltaSeconds
        val duration = state.durationSeconds + (if (survivalTimerAcc >= 1f) {
            survivalTimerAcc -= 1f
            1
        } else 0)

        // Continuous score gain: +1 point per 0.2 seconds * multiplier
        val scoreIncrement = (deltaSeconds * 5 * multiplier).toInt()
        var currentScore = state.score + scoreIncrement

        // Speed multiplier based on mode & time
        val speedFactor = when (state.currentMode) {
            "Fast Rush" -> 1.45f
            "Star Hunter" -> 1.0f
            "Shield Breaker" -> 1.15f
            else -> 1.0f
        } * (if (isSlowMo) 0.45f else 1.0f) * (1.0f + (duration * 0.005f).coerceAtMost(0.8f))

        // Spawn obstacles
        spawnTimer += deltaSeconds
        val spawnInterval = when (state.currentMode) {
            "Fast Rush" -> 0.45f
            "Star Hunter" -> 0.75f
            else -> (0.85f - (duration * 0.004f)).coerceAtLeast(0.38f)
        }
        if (spawnTimer >= spawnInterval) {
            spawnTimer = 0f
            spawnObstacle(speedFactor)
        }

        // Spawn collectibles
        collectibleTimer += deltaSeconds
        val collectibleInterval = if (state.currentMode == "Star Hunter") 1.2f else 3.2f
        if (collectibleTimer >= collectibleInterval) {
            collectibleTimer = 0f
            spawnCollectible()
        }

        // Update obstacles
        val obstacleIterator = obstacles.iterator()
        val playerY = 0.84f
        val playerHitboxRadius = 0.045f

        var dodgedIncrement = 0
        var newLives = state.lives
        var isShieldActive = state.isShieldActive
        var shieldHits = state.shieldHitsLeft
        var combo = state.comboCount

        while (obstacleIterator.hasNext()) {
            val obs = obstacleIterator.next()
            obs.y += obs.speed * speedFactor * deltaSeconds

            // Check collision with player
            val dx = abs(obs.x - newPlayerX)
            val dy = abs(obs.y - playerY)
            val isColliding = dx < (playerHitboxRadius + obs.size * 0.5f) && dy < 0.045f

            if (isColliding) {
                if (isShieldActive) {
                    // Shield destroys obstacle
                    shieldHits--
                    if (shieldHits <= 0) {
                        isShieldActive = false
                    }
                    audioPlayer.playPowerUp()
                    spawnExplosion(obs.x, obs.y, Color(0xFF38BDF8))
                    addFloatingText("SHIELD BLOCKED!", obs.x, obs.y, Color(0xFF38BDF8))
                    obstacleIterator.remove()
                    continue
                } else if (!isInvulnerable) {
                    // Player took hit
                    newLives--
                    audioPlayer.playHit()
                    spawnExplosion(newPlayerX, playerY, Color(0xFFEF4444))
                    combo = 0
                    if (newLives <= 0) {
                        // Game Over!
                        audioPlayer.playGameOver()
                        state = state.copy(
                            isGameOver = true,
                            lives = 0,
                            playerX = newPlayerX,
                            score = currentScore
                        )
                        onGameOver(currentScore, duration, state.dodgedCount, state.gemsCollectedThisRun)
                        return
                    } else {
                        // Reset invulnerability for 2 seconds
                        state = state.copy(
                            lives = newLives,
                            isInvulnerable = true,
                            invulnerableTimer = 2.0f
                        )
                    }
                    obstacleIterator.remove()
                    continue
                }
            }

            // Close call check
            if (!obs.dodged && obs.y > playerY && dy < 0.08f && dx < 0.09f) {
                obs.dodged = true
                dodgedIncrement++
                combo++
                val bonus = 25 * multiplier
                currentScore += bonus
                audioPlayer.playDodge()
                addFloatingText("CLOSE CALL! +$bonus", obs.x, obs.y - 0.02f, Color(0xFFFACC15))
            } else if (!obs.dodged && obs.y > 0.98f) {
                obs.dodged = true
                dodgedIncrement++
                val bonus = 10 * multiplier
                currentScore += bonus
            }

            // Remove off-screen
            if (obs.y > 1.1f) {
                obstacleIterator.remove()
            }
        }

        // Update collectibles
        val collectibleIterator = collectibles.iterator()
        var gemsCollected = state.gemsCollectedThisRun

        while (collectibleIterator.hasNext()) {
            val item = collectibleIterator.next()
            item.y += item.speed * speedFactor * deltaSeconds

            val dx = abs(item.x - newPlayerX)
            val dy = abs(item.y - playerY)
            if (dx < 0.06f && dy < 0.05f) {
                // Collected!
                when (item.type) {
                    CollectibleType.STAR -> {
                        audioPlayer.playCoin()
                        val bonus = 50 * multiplier
                        currentScore += bonus
                        newMultiplierTimer = 6f
                        state = state.copy(multiplier = 2)
                        addFloatingText("STAR 2X! +$bonus", item.x, item.y, Color(0xFFFACC15))
                        spawnExplosion(item.x, item.y, Color(0xFFFACC15))
                    }
                    CollectibleType.GEM -> {
                        audioPlayer.playCoin()
                        gemsCollected++
                        currentScore += 20 * multiplier
                        addFloatingText("+1 GEM!", item.x, item.y, Color(0xFF10B981))
                        spawnExplosion(item.x, item.y, Color(0xFF10B981))
                    }
                    CollectibleType.SHIELD -> {
                        audioPlayer.playPowerUp()
                        isShieldActive = true
                        shieldHits = 2
                        addFloatingText("SHIELD READY!", item.x, item.y, Color(0xFF38BDF8))
                        spawnExplosion(item.x, item.y, Color(0xFF38BDF8))
                    }
                    CollectibleType.SLOW_MO -> {
                        audioPlayer.playPowerUp()
                        newSlowMoTimer = 6f
                        addFloatingText("SLOW MOTION!", item.x, item.y, Color(0xFFF59E0B))
                        spawnExplosion(item.x, item.y, Color(0xFFF59E0B))
                    }
                    CollectibleType.HEART -> {
                        audioPlayer.playPowerUp()
                        if (newLives < 3) newLives++
                        addFloatingText("+1 LIFE!", item.x, item.y, Color(0xFFF43F5E))
                        spawnExplosion(item.x, item.y, Color(0xFFF43F5E))
                    }
                }
                collectibleIterator.remove()
                continue
            }

            if (item.y > 1.1f) {
                collectibleIterator.remove()
            }
        }

        // Update particles
        val particleIterator = particles.iterator()
        while (particleIterator.hasNext()) {
            val p = particleIterator.next()
            p.x += p.vx * deltaSeconds
            p.y += p.vy * deltaSeconds
            p.life -= deltaSeconds * 2.2f
            p.alpha = p.life.coerceIn(0f, 1f)
            if (p.life <= 0f) particleIterator.remove()
        }

        // Update floating texts
        val textIterator = floatingTexts.iterator()
        while (textIterator.hasNext()) {
            val t = textIterator.next()
            t.y -= 0.05f * deltaSeconds
            t.life -= deltaSeconds * 1.5f
            t.alpha = t.life.coerceIn(0f, 1f)
            if (t.life <= 0f) textIterator.remove()
        }

        val updatedHighScore = if (currentScore > state.highScore) currentScore else state.highScore

        state = state.copy(
            score = currentScore,
            highScore = updatedHighScore,
            lives = newLives,
            multiplier = if (newMultiplierTimer > 0f) 2 else 1,
            multiplierTimer = newMultiplierTimer,
            playerX = newPlayerX,
            isShieldActive = isShieldActive,
            shieldHitsLeft = shieldHits,
            isSlowMoActive = isSlowMo,
            slowMoTimer = newSlowMoTimer,
            isInvulnerable = isInvulnerable,
            invulnerableTimer = newInvulnerableTimer,
            dashCooldown = newDashCooldown,
            durationSeconds = duration,
            dodgedCount = state.dodgedCount + dodgedIncrement,
            gemsCollectedThisRun = gemsCollected,
            comboCount = combo
        )
    }

    private fun spawnObstacle(speedFactor: Float) {
        val type = when (Random.nextInt(10)) {
            0, 1 -> ObstacleType.SPIKE
            2 -> ObstacleType.MINE
            3 -> ObstacleType.LASER_GLITCH
            else -> ObstacleType.METEOR
        }
        val size = when (type) {
            ObstacleType.MINE -> 0.055f
            ObstacleType.LASER_GLITCH -> 0.08f
            else -> 0.045f
        }
        val baseSpeed = when (type) {
            ObstacleType.LASER_GLITCH -> 0.42f
            ObstacleType.SPIKE -> 0.35f
            ObstacleType.MINE -> 0.24f
            else -> 0.30f
        }
        obstacles.add(
            Obstacle(
                id = nextEntityId++,
                x = Random.nextFloat() * 0.84f + 0.08f,
                y = -0.05f,
                speed = baseSpeed * (0.9f + Random.nextFloat() * 0.2f),
                size = size,
                type = type
            )
        )
    }

    private fun spawnCollectible() {
        val roll = Random.nextInt(100)
        val type = when {
            roll < 45 -> CollectibleType.STAR
            roll < 75 -> CollectibleType.GEM
            roll < 85 -> CollectibleType.SHIELD
            roll < 95 -> CollectibleType.SLOW_MO
            else -> CollectibleType.HEART
        }
        collectibles.add(
            Collectible(
                id = nextEntityId++,
                x = Random.nextFloat() * 0.82f + 0.09f,
                y = -0.05f,
                speed = 0.22f,
                size = 0.045f,
                type = type
            )
        )
    }

    private fun spawnExplosion(x: Float, y: Float, color: Color) {
        for (i in 0 until 14) {
            val angle = Random.nextFloat() * Math.PI.toFloat() * 2f
            val speed = Random.nextFloat() * 0.25f + 0.05f
            particles.add(
                PixelParticle(
                    x = x,
                    y = y,
                    vx = (Math.cos(angle.toDouble()) * speed).toFloat(),
                    vy = (Math.sin(angle.toDouble()) * speed).toFloat(),
                    size = 10f + Random.nextFloat() * 8f,
                    color = color
                )
            )
        }
    }

    private fun spawnDashParticles(x: Float, y: Float) {
        for (i in 0 until 8) {
            particles.add(
                PixelParticle(
                    x = x + (Random.nextFloat() - 0.5f) * 0.05f,
                    y = y + (Random.nextFloat() - 0.5f) * 0.02f,
                    vx = (Random.nextFloat() - 0.5f) * 0.1f,
                    vy = Random.nextFloat() * 0.1f + 0.05f,
                    size = 8f,
                    color = Color(0xFF38BDF8)
                )
            )
        }
    }

    private fun addFloatingText(text: String, x: Float, y: Float, color: Color) {
        floatingTexts.add(
            FloatingText(
                id = nextEntityId++,
                text = text,
                x = x.coerceIn(0.1f, 0.9f),
                y = y,
                color = color
            )
        )
    }
}
