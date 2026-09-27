package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.game.GameSession
import com.example.game.PixelArtRenderer
import com.example.game.PixelArtRenderer.drawCollectible
import com.example.game.PixelArtRenderer.drawObstacle
import com.example.game.PixelArtRenderer.drawPixelParticle
import com.example.game.PixelArtRenderer.drawPlayer
import com.example.ui.theme.CardBorder
import com.example.ui.theme.CoralRed
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.ElectricCyan
import com.example.ui.theme.GemAmber
import com.example.ui.theme.MintGreen
import com.example.ui.theme.PixelFamily
import com.example.ui.theme.PixelNeonYellow
import com.example.ui.theme.PixelShieldCyan
import com.example.ui.theme.PureWhite
import com.example.ui.theme.SkyBlue
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.TextTertiary

@Composable
fun GameScreen(
    session: GameSession,
    onBackToHome: () -> Unit,
    onRestart: () -> Unit,
    modifier: Modifier = Modifier
) {
    BackHandler {
        if (!session.state.isGameOver) {
            session.togglePause()
        } else {
            onBackToHome()
        }
    }

    var lastFrameNanos by remember { mutableLongStateOf(0L) }

    // 60FPS Game loop using withFrameNanos
    LaunchedEffect(session) {
        lastFrameNanos = 0L
        while (true) {
            withFrameNanos { frameTimeNanos ->
                if (lastFrameNanos != 0L) {
                    val deltaSeconds = ((frameTimeNanos - lastFrameNanos) / 1_000_000_000f).coerceIn(0f, 0.05f)
                    session.update(deltaSeconds)
                }
                lastFrameNanos = frameTimeNanos
            }
        }
    }

    val state = session.state

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(DarkSurface)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // TOP HUD
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Pause button & Mode label
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = { session.togglePause() },
                        modifier = Modifier
                            .size(36.dp)
                            .background(Color(0xFF1E293B), CircleShape)
                            .testTag("pause_button")
                    ) {
                        Icon(
                            imageVector = if (state.isPaused) Icons.Default.PlayArrow else Icons.Default.Pause,
                            contentDescription = "Pause",
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Text(
                        text = state.currentMode.uppercase(),
                        fontFamily = PixelFamily,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = ElectricCyan
                    )
                }

                // Score HUD
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "${state.score}",
                        fontFamily = PixelFamily,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = "BEST: ${state.highScore}",
                        fontFamily = PixelFamily,
                        fontSize = 10.sp,
                        color = TextTertiary
                    )
                }

                // Lives Hearts
                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    for (i in 1..3) {
                        val hasLife = i <= state.lives
                        Icon(
                            imageVector = if (hasLife) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                            contentDescription = "Heart $i",
                            tint = if (hasLife) CoralRed else Color(0xFF475569),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }

            // Status badges row (Multiplier, Shield, Slow-mo)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (state.multiplier > 1) {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = PixelNeonYellow.copy(alpha = 0.2f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, PixelNeonYellow)
                    ) {
                        Text(
                            text = "2X MULTIPLIER!",
                            fontFamily = PixelFamily,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = PixelNeonYellow,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                        )
                    }
                }

                if (state.isShieldActive) {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = PixelShieldCyan.copy(alpha = 0.2f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, PixelShieldCyan)
                    ) {
                        Text(
                            text = "SHIELD (${state.shieldHitsLeft})",
                            fontFamily = PixelFamily,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = PixelShieldCyan,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                        )
                    }
                }

                if (state.isSlowMoActive) {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = GemAmber.copy(alpha = 0.2f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, GemAmber)
                    ) {
                        Text(
                            text = "SLOW-MO",
                            fontFamily = PixelFamily,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = GemAmber,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                        )
                    }
                }
            }

            // RETRO PIXEL ART ARENA CANVAS
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .pointerInput(session) {
                        detectDragGestures { change, _ ->
                            change.consume()
                            val normalizedX = (change.position.x / size.width).coerceIn(0.08f, 0.92f)
                            session.setPlayerTargetX(normalizedX)
                        }
                    }
                    .pointerInput(session) {
                        detectTapGestures { offset ->
                            val normalizedX = (offset.x / size.width).coerceIn(0.08f, 0.92f)
                            session.setPlayerTargetX(normalizedX)
                        }
                    }
                    .testTag("game_canvas")
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val canvasWidth = size.width
                    val canvasHeight = size.height

                    // 1. Subtle retro pixel starfield
                    val starCols = 8
                    val starRows = 12
                    for (c in 0 until starCols) {
                        for (r in 0 until starRows) {
                            val sx = (c + 0.5f) * (canvasWidth / starCols) + ((r * 29) % 31)
                            val sy = (r + 0.5f) * (canvasHeight / starRows)
                            val starTick = (session.engineTick + c * 3 + r * 7) % 60
                            if (starTick < 40) {
                                drawRect(
                                    color = Color(0xFF1E293B).copy(alpha = 0.5f),
                                    topLeft = Offset(sx % canvasWidth, sy),
                                    size = androidx.compose.ui.geometry.Size(3f, 3f)
                                )
                            }
                        }
                    }

                    // 2. Obstacles
                    for (obs in session.obstacles) {
                        val px = obs.x * canvasWidth
                        val py = obs.y * canvasHeight
                        val sz = obs.size * canvasWidth
                        drawObstacle(obs.type, px, py, sz, session.engineTick)
                    }

                    // 3. Collectibles
                    for (item in session.collectibles) {
                        val px = item.x * canvasWidth
                        val py = item.y * canvasHeight
                        val sz = item.size * canvasWidth
                        drawCollectible(item.type, px, py, sz, session.engineTick)
                    }

                    // 4. Particles
                    for (p in session.particles) {
                        val px = p.x * canvasWidth
                        val py = p.y * canvasHeight
                        drawPixelParticle(px, py, p.size, p.color.copy(alpha = p.alpha))
                    }

                    // 5. Player Hero Sprite
                    val playerPixelX = state.playerX * canvasWidth
                    val playerPixelY = 0.84f * canvasHeight
                    drawPlayer(
                        skinId = state.selectedSkin,
                        x = playerPixelX,
                        y = playerPixelY,
                        scale = 5.0f,
                        hasShield = state.isShieldActive,
                        isInvulnerable = state.isInvulnerable,
                        engineTick = session.engineTick
                    )

                    // 6. Floating bonus texts
                    val paint = android.graphics.Paint().apply {
                        color = android.graphics.Color.WHITE
                        textSize = 28f
                        isFakeBoldText = true
                    }
                    for (ft in session.floatingTexts) {
                        paint.color = android.graphics.Color.argb(
                            (ft.alpha * 255).toInt(),
                            (ft.color.red * 255).toInt(),
                            (ft.color.green * 255).toInt(),
                            (ft.color.blue * 255).toInt()
                        )
                        drawContext.canvas.nativeCanvas.drawText(
                            ft.text,
                            ft.x * canvasWidth,
                            ft.y * canvasHeight,
                            paint
                        )
                    }
                }
            }

            // ARCADE ON-SCREEN TACTILE CONTROLS
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = Color(0xFF0F172A),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF1E293B))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Left Arrow Button
                    Button(
                        onClick = { session.movePlayer(-0.10f) },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E293B)),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier
                            .size(68.dp, 56.dp)
                            .testTag("arcade_left_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.KeyboardArrowLeft,
                            contentDescription = "Move Left",
                            tint = Color.White,
                            modifier = Modifier.size(32.dp)
                        )
                    }

                    // DASH / JUMP BUTTON
                    Button(
                        onClick = { session.triggerDash(if (state.playerX < 0.5f) 1f else -1f) },
                        enabled = state.dashCooldown <= 0f,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = ElectricCyan,
                            disabledContainerColor = Color(0xFF1E293B)
                        ),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier
                            .height(56.dp)
                            .testTag("arcade_dash_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Bolt,
                            contentDescription = "Dash",
                            tint = if (state.dashCooldown <= 0f) Color.White else TextTertiary,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (state.dashCooldown <= 0f) "DASH" else "READY...",
                            fontFamily = PixelFamily,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (state.dashCooldown <= 0f) Color.White else TextTertiary
                        )
                    }

                    // Right Arrow Button
                    Button(
                        onClick = { session.movePlayer(0.10f) },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E293B)),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier
                            .size(68.dp, 56.dp)
                            .testTag("arcade_right_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.KeyboardArrowRight,
                            contentDescription = "Move Right",
                            tint = Color.White,
                            modifier = Modifier.size(32.dp)
                        )
                    }
                }
            }
        }

        // PAUSE DIALOG
        if (state.isPaused) {
            Dialog(onDismissRequest = { session.togglePause() }) {
                Surface(
                    shape = RoundedCornerShape(24.dp),
                    color = PureWhite,
                    border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .testTag("pause_dialog")
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "PAUSED",
                            fontFamily = PixelFamily,
                            fontSize = 26.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = "Current Score: ${state.score}",
                            fontFamily = PixelFamily,
                            fontSize = 14.sp,
                            color = SkyBlue
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        Button(
                            onClick = { session.togglePause() },
                            colors = ButtonDefaults.buttonColors(containerColor = SkyBlue),
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                                .testTag("resume_button")
                        ) {
                            Text("Resume", fontWeight = FontWeight.Bold)
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        OutlinedButton(
                            onClick = onRestart,
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                        ) {
                            Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Restart Run")
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        OutlinedButton(
                            onClick = onBackToHome,
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                        ) {
                            Text("Quit to Home")
                        }
                    }
                }
            }
        }

        // GAME OVER DIALOG
        if (state.isGameOver) {
            val isNewBest = state.score >= state.highScore && state.score > 0
            Dialog(onDismissRequest = onRestart) {
                Surface(
                    shape = RoundedCornerShape(26.dp),
                    color = PureWhite,
                    border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .testTag("game_over_dialog")
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "GAME OVER",
                            fontFamily = PixelFamily,
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Bold,
                            color = CoralRed
                        )

                        if (isNewBest) {
                            Spacer(modifier = Modifier.height(6.dp))
                            Surface(
                                shape = RoundedCornerShape(20.dp),
                                color = MintGreen.copy(alpha = 0.15f)
                            ) {
                                Text(
                                    text = "★ NEW HIGH SCORE! ★",
                                    fontFamily = PixelFamily,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MintGreen,
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(18.dp))

                        // Score & Stats Container
                        Surface(
                            shape = RoundedCornerShape(18.dp),
                            color = Color(0xFFF8FAFC),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "FINAL SCORE",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = TextSecondary
                                )
                                Text(
                                    text = "${state.score}",
                                    fontFamily = PixelFamily,
                                    fontSize = 36.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = SkyBlue
                                )

                                Spacer(modifier = Modifier.height(10.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceAround
                                ) {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Text(text = "SURVIVED", style = MaterialTheme.typography.labelSmall, color = TextTertiary)
                                        Text(text = "${state.durationSeconds}s", fontFamily = PixelFamily, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                    }
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Text(text = "DODGED", style = MaterialTheme.typography.labelSmall, color = TextTertiary)
                                        Text(text = "${state.dodgedCount}", fontFamily = PixelFamily, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                    }
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Text(text = "GEMS", style = MaterialTheme.typography.labelSmall, color = TextTertiary)
                                        Text(text = "+${state.gemsCollectedThisRun}", fontFamily = PixelFamily, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = GemAmber)
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        Button(
                            onClick = onRestart,
                            colors = ButtonDefaults.buttonColors(containerColor = SkyBlue),
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(52.dp)
                                .testTag("try_again_button")
                        ) {
                            Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("TRY AGAIN", fontFamily = PixelFamily, fontWeight = FontWeight.Bold)
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        OutlinedButton(
                            onClick = onBackToHome,
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                                .testTag("home_button")
                        ) {
                            Text("BACK TO HOME", fontWeight = FontWeight.SemiBold)
                        }
                    }
                }
            }
        }
    }
}
