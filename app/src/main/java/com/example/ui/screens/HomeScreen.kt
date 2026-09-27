package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.UserStatsEntity
import com.example.ui.components.AppTopBar
import com.example.ui.components.StreakCard
import com.example.ui.theme.CardBorder
import com.example.ui.theme.CoralRed
import com.example.ui.theme.CoralRedBg
import com.example.ui.theme.CreamBackground
import com.example.ui.theme.LavenderPurple
import com.example.ui.theme.LavenderPurpleBg
import com.example.ui.theme.MintGreen
import com.example.ui.theme.MintGreenBg
import com.example.ui.theme.PixelFamily
import com.example.ui.theme.PureWhite
import com.example.ui.theme.SkyBlue
import com.example.ui.theme.SkyBlueBg
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

data class GameModeCardData(
    val id: String,
    val title: String,
    val subtitle: String,
    val icon: ImageVector,
    val iconColor: Color,
    val bgColor: Color
)

@Composable
fun HomeScreen(
    stats: UserStatsEntity?,
    onStartGame: (mode: String) -> Unit,
    onNavigateToQuiz: () -> Unit,
    modifier: Modifier = Modifier
) {
    val streakDays = stats?.streakDays ?: 5
    val gems = stats?.gems ?: 25
    val highScore = stats?.highScore ?: 0

    val modes = listOf(
        GameModeCardData(
            id = "Classic",
            title = "Classic Dodge",
            subtitle = "Avoid falling hazards",
            icon = Icons.Default.SportsEsports,
            iconColor = MintGreen,
            bgColor = MintGreenBg
        ),
        GameModeCardData(
            id = "Fast Rush",
            title = "Turbo Rush",
            subtitle = "High speed reflexes",
            icon = Icons.Default.Bolt,
            iconColor = SkyBlue,
            bgColor = SkyBlueBg
        ),
        GameModeCardData(
            id = "Star Hunter",
            title = "Star Hunter",
            subtitle = "Collect 2x multipliers",
            icon = Icons.Default.Star,
            iconColor = CoralRed,
            bgColor = CoralRedBg
        ),
        GameModeCardData(
            id = "Shield Breaker",
            title = "Shield Break",
            subtitle = "Smash obstacles",
            icon = Icons.Default.Security,
            iconColor = LavenderPurple,
            bgColor = LavenderPurpleBg
        )
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(CreamBackground)
            .verticalScroll(rememberScrollState())
    ) {
        // Top Bar
        AppTopBar(streakDays = streakDays, gems = gems)

        // Player Greeting Section matching the image header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Hi, Dodger! 👋",
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "Ready to dodge falling obstacles?",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary
                )
            }

            // Player Retro Avatar
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(SkyBlueBg)
                    .border(2.dp, SkyBlue, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "👾",
                    fontSize = 28.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Streak Card
        Box(modifier = Modifier.padding(horizontal = 20.dp)) {
            StreakCard(streakDays = streakDays)
        }

        Spacer(modifier = Modifier.height(24.dp))

        // "Learn Categories" -> "Game Modes" Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Game Modes",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
            Text(
                text = "Best: $highScore",
                fontFamily = PixelFamily,
                fontWeight = FontWeight.Bold,
                color = SkyBlue,
                fontSize = 14.sp
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // 2x2 Grid of game modes matching the screenshot
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            for (row in 0..1) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    for (col in 0..1) {
                        val item = modes[row * 2 + col]
                        Surface(
                            shape = RoundedCornerShape(18.dp),
                            color = item.bgColor,
                            modifier = Modifier
                                .weight(1f)
                                .height(105.dp)
                                .clickable { onStartGame(item.id) }
                                .testTag("mode_${item.id.lowercase().replace(" ", "_")}")
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(14.dp),
                                verticalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(34.dp)
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(PureWhite),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = item.icon,
                                            contentDescription = item.title,
                                            tint = item.iconColor,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }
                                    Box(
                                        modifier = Modifier
                                            .size(22.dp)
                                            .clip(CircleShape)
                                            .background(item.iconColor),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
                                            contentDescription = "Start",
                                            tint = Color.White,
                                            modifier = Modifier.size(10.dp)
                                        )
                                    }
                                }

                                Column {
                                    Text(
                                        text = item.title,
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = TextPrimary
                                    )
                                    Text(
                                        text = item.subtitle,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = TextSecondary,
                                        maxLines = 1
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Daily Challenge Hero Card (similar to "Letter of the Day" in screenshot)
        Surface(
            shape = RoundedCornerShape(22.dp),
            color = PureWhite,
            border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .testTag("daily_challenge_card")
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Daily Reflex Quiz",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Answer retro trivia & earn +20 bonus gems",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSecondary
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Button(
                    onClick = onNavigateToQuiz,
                    colors = ButtonDefaults.buttonColors(containerColor = MintGreen),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.testTag("quiz_start_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = "Play Quiz",
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Quiz",
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Quick Launch Button
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 6.dp)
        ) {
            Button(
                onClick = { onStartGame("Classic") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp)
                    .testTag("play_now_button"),
                colors = ButtonDefaults.buttonColors(containerColor = SkyBlue),
                shape = RoundedCornerShape(16.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.PlayArrow,
                    contentDescription = "Play Now",
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "PLAY DODGE NOW",
                    fontFamily = PixelFamily,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}
