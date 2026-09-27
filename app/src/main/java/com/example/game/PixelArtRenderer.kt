package com.example.game

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import com.example.ui.theme.ElectricCyan
import com.example.ui.theme.GemAmber
import com.example.ui.theme.MintGreen
import com.example.ui.theme.PixelHazardRed
import com.example.ui.theme.PixelNeonYellow
import com.example.ui.theme.PixelObstaclePurple
import com.example.ui.theme.PixelShieldCyan

enum class ObstacleType {
    METEOR,
    SPIKE,
    MINE,
    LASER_GLITCH
}

enum class CollectibleType {
    STAR,
    GEM,
    SHIELD,
    SLOW_MO,
    HEART
}

object PixelArtRenderer {

    // Helper to draw a pixel grid from a string array where chars correspond to colors
    private fun DrawScope.drawPixelGrid(
        grid: Array<String>,
        colorMap: Map<Char, Color>,
        x: Float,
        y: Float,
        pixelSize: Float
    ) {
        val rows = grid.size
        val cols = grid[0].length
        val startX = x - (cols * pixelSize) / 2f
        val startY = y - (rows * pixelSize) / 2f

        for (r in 0 until rows) {
            val rowStr = grid[r]
            for (c in 0 until cols) {
                val char = rowStr[c]
                val color = colorMap[char]
                if (color != null && color != Color.Transparent) {
                    drawRect(
                        color = color,
                        topLeft = Offset(startX + c * pixelSize, startY + r * pixelSize),
                        size = Size(pixelSize, pixelSize)
                    )
                }
            }
        }
    }

    fun DrawScope.drawPlayer(
        skinId: String,
        x: Float,
        y: Float,
        scale: Float = 3.5f,
        hasShield: Boolean = false,
        isInvulnerable: Boolean = false,
        engineTick: Int = 0
    ) {
        if (isInvulnerable && (engineTick % 4 < 2)) {
            // Blinking invulnerability effect
            return
        }

        when (skinId) {
            "cyber_cat" -> drawPixelCat(x, y, scale)
            "knight_8bit" -> drawPixelKnight(x, y, scale)
            "pixel_ufo" -> drawPixelUfo(x, y, scale, engineTick)
            "glitch_phantom" -> drawPixelPhantom(x, y, scale, engineTick)
            else -> drawPixelShip(x, y, scale, engineTick)
        }

        if (hasShield) {
            // Draw glowing pixel shield ring around hero
            val shieldRadius = 28f * scale
            val shieldColor = PixelShieldCyan.copy(alpha = 0.65f)
            val ringPoints = 16
            for (i in 0 until ringPoints) {
                val angle = (i * (360f / ringPoints) + (engineTick * 5)) * (Math.PI / 180.0)
                val px = x + (Math.cos(angle) * shieldRadius).toFloat()
                val py = y + (Math.sin(angle) * shieldRadius).toFloat()
                drawRect(
                    color = shieldColor,
                    topLeft = Offset(px - scale * 1.5f, py - scale * 1.5f),
                    size = Size(scale * 3f, scale * 3f)
                )
            }
        }
    }

    private fun DrawScope.drawPixelShip(x: Float, y: Float, px: Float, tick: Int) {
        val grid = arrayOf(
            "..C..",
            ".CCC.",
            "CCCCC",
            "WWWWW",
            "CWCWC",
            "W.W.W",
            ".F.F."
        )
        val flameColor = if (tick % 2 == 0) PixelNeonYellow else GemAmber
        val colorMap = mapOf(
            'C' to ElectricCyan,
            'W' to Color.White,
            'F' to flameColor
        )
        drawPixelGrid(grid, colorMap, x, y, px)
    }

    private fun DrawScope.drawPixelCat(x: Float, y: Float, px: Float) {
        val grid = arrayOf(
            "P...P",
            "PP.PP",
            "WWWWW",
            "WEWEW",
            "WWPWW",
            ".WWW.",
            "W.W.W"
        )
        val colorMap = mapOf(
            'P' to Color(0xFFF43F5E),
            'W' to Color.White,
            'E' to Color(0xFF1E293B)
        )
        drawPixelGrid(grid, colorMap, x, y, px)
    }

    private fun DrawScope.drawPixelKnight(x: Float, y: Float, px: Float) {
        val grid = arrayOf(
            "..G..",
            ".GGG.",
            "SSSSS",
            "SERES",
            "SSSSS",
            ".SSS.",
            "G...G"
        )
        val colorMap = mapOf(
            'G' to GemAmber,
            'S' to Color(0xFF94A3B8),
            'E' to Color(0xFF0F172A),
            'R' to PixelHazardRed
        )
        drawPixelGrid(grid, colorMap, x, y, px)
    }

    private fun DrawScope.drawPixelUfo(x: Float, y: Float, px: Float, tick: Int) {
        val grid = arrayOf(
            "..G..",
            ".GGG.",
            "MMMMM",
            "L.L.L",
            ".MMM."
        )
        val lightColor = if (tick % 4 < 2) PixelNeonYellow else ElectricCyan
        val colorMap = mapOf(
            'G' to MintGreen,
            'M' to Color(0xFF64748B),
            'L' to lightColor
        )
        drawPixelGrid(grid, colorMap, x, y, px)
    }

    private fun DrawScope.drawPixelPhantom(x: Float, y: Float, px: Float, tick: Int) {
        val grid = arrayOf(
            ".PPP.",
            "PPPPP",
            "PEPEP",
            "PPPPP",
            "P.P.P",
            (if (tick % 2 == 0) ".P.P." else "P.P.P")
        )
        val colorMap = mapOf(
            'P' to Color(0xFFA855F7),
            'E' to Color(0xFFFDE047)
        )
        drawPixelGrid(grid, colorMap, x, y, px)
    }

    fun DrawScope.drawObstacle(type: ObstacleType, x: Float, y: Float, size: Float, tick: Int) {
        val px = size / 6f
        when (type) {
            ObstacleType.METEOR -> {
                val grid = arrayOf(
                    ".OOO.",
                    "OOMOO",
                    "OMMMO",
                    "OOMOO",
                    ".OOO."
                )
                val colorMap = mapOf(
                    'O' to PixelHazardRed,
                    'M' to Color(0xFF7F1D1D)
                )
                drawPixelGrid(grid, colorMap, x, y, px)
            }
            ObstacleType.SPIKE -> {
                val grid = arrayOf(
                    "..S..",
                    ".SSS.",
                    "SSSSS",
                    ".SSS.",
                    "..S.."
                )
                val colorMap = mapOf(
                    'S' to PixelObstaclePurple
                )
                drawPixelGrid(grid, colorMap, x, y, px)
            }
            ObstacleType.MINE -> {
                val grid = arrayOf(
                    "..R..",
                    ".ROR.",
                    "ROOOR",
                    ".ROR.",
                    "..R.."
                )
                val colorMap = mapOf(
                    'R' to GemAmber,
                    'O' to Color(0xFF1E293B)
                )
                drawPixelGrid(grid, colorMap, x, y, px)
            }
            ObstacleType.LASER_GLITCH -> {
                val grid = arrayOf(
                    "LLLL",
                    "WLLW",
                    "LLLL"
                )
                val colorMap = mapOf(
                    'L' to if (tick % 2 == 0) Color(0xFFF43F5E) else Color(0xFF38BDF8),
                    'W' to Color.White
                )
                drawPixelGrid(grid, colorMap, x, y, px * 1.5f)
            }
        }
    }

    fun DrawScope.drawCollectible(type: CollectibleType, x: Float, y: Float, size: Float, tick: Int) {
        val px = size / 5f
        when (type) {
            CollectibleType.STAR -> {
                val grid = arrayOf(
                    "..Y..",
                    ".YYY.",
                    "YYYYY",
                    ".YYY.",
                    "Y.Y.Y"
                )
                val colorMap = mapOf('Y' to PixelNeonYellow)
                drawPixelGrid(grid, colorMap, x, y, px)
            }
            CollectibleType.GEM -> {
                val grid = arrayOf(
                    ".GGG.",
                    "GGWGG",
                    "GGGGG",
                    ".GGG.",
                    "..G.."
                )
                val colorMap = mapOf('G' to MintGreen, 'W' to Color.White)
                drawPixelGrid(grid, colorMap, x, y, px)
            }
            CollectibleType.SHIELD -> {
                val grid = arrayOf(
                    ".CCC.",
                    "CWWWC",
                    "CCCCC",
                    ".CCC.",
                    "..C.."
                )
                val colorMap = mapOf('C' to PixelShieldCyan, 'W' to Color.White)
                drawPixelGrid(grid, colorMap, x, y, px)
            }
            CollectibleType.SLOW_MO -> {
                val grid = arrayOf(
                    "AAAAA",
                    ".ACA.",
                    "..A..",
                    ".ACA.",
                    "AAAAA"
                )
                val colorMap = mapOf('A' to GemAmber, 'C' to Color.White)
                drawPixelGrid(grid, colorMap, x, y, px)
            }
            CollectibleType.HEART -> {
                val grid = arrayOf(
                    ".H.H.",
                    "HHHHH",
                    "HHHHH",
                    ".HHH.",
                    "..H.."
                )
                val colorMap = mapOf('H' to Color(0xFFF43F5E))
                drawPixelGrid(grid, colorMap, x, y, px)
            }
        }
    }

    fun DrawScope.drawPixelParticle(x: Float, y: Float, size: Float, color: Color) {
        drawRect(
            color = color,
            topLeft = Offset(x - size / 2, y - size / 2),
            size = Size(size, size)
        )
    }
}
