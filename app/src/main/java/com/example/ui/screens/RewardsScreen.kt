package com.example.ui.screens

import androidx.compose.foundation.Canvas
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
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Quiz
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.UserStatsEntity
import com.example.data.repository.AVAILABLE_SKINS
import com.example.data.repository.Skin
import com.example.game.CollectibleType
import com.example.game.ObstacleType
import com.example.game.PixelArtRenderer
import com.example.game.PixelArtRenderer.drawCollectible
import com.example.game.PixelArtRenderer.drawObstacle
import com.example.game.PixelArtRenderer.drawPlayer
import com.example.ui.DailyQuizQuestion
import com.example.ui.MainViewModel
import com.example.ui.components.AppTopBar
import com.example.ui.theme.CardBorder
import com.example.ui.theme.CoralRed
import com.example.ui.theme.CreamBackground
import com.example.ui.theme.GemAmber
import com.example.ui.theme.GemAmberBg
import com.example.ui.theme.MintGreen
import com.example.ui.theme.MintGreenBg
import com.example.ui.theme.PixelFamily
import com.example.ui.theme.PixelHazardRed
import com.example.ui.theme.PixelShieldCyan
import com.example.ui.theme.PureWhite
import com.example.ui.theme.SkyBlue
import com.example.ui.theme.SkyBlueBg
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.TextTertiary

@Composable
fun RewardsScreen(
    stats: UserStatsEntity?,
    quizState: MainViewModel.QuizState,
    onStartQuiz: () -> Unit,
    onSelectQuizOption: (Int) -> Unit,
    onSubmitQuiz: () -> Unit,
    onCompleteQuiz: () -> Unit,
    onResetQuiz: () -> Unit,
    onSelectSkin: (String) -> Unit,
    onBuySkin: (Skin) -> Unit,
    modifier: Modifier = Modifier
) {
    val streakDays = stats?.streakDays ?: 5
    val gems = stats?.gems ?: 25
    val currentSkinId = stats?.selectedSkinId ?: "ship_classic"

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(CreamBackground)
    ) {
        // Top bar
        AppTopBar(streakDays = streakDays, gems = gems)

        // IF QUIZ IS ACTIVE (Matching Screen 3: "Animals Quiz / Which animal is this?" from screenshot)
        if (quizState is MainViewModel.QuizState.Active) {
            DailyQuizView(
                quiz = quizState.question,
                selectedOption = quizState.selectedOption,
                isSubmitted = quizState.isSubmitted,
                isCorrect = quizState.isCorrect,
                onSelectOption = onSelectQuizOption,
                onSubmit = onSubmitQuiz,
                onContinue = onCompleteQuiz,
                onClose = onResetQuiz,
                currentGems = gems
            )
        } else if (quizState is MainViewModel.QuizState.Completed) {
            QuizCompletedView(
                earnedGems = quizState.earnedGems,
                onClose = onResetQuiz
            )
        } else {
            // REGULAR REWARDS / SKINS / QUESTS VIEW
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Rewards & Skins",
                            style = MaterialTheme.typography.headlineLarge,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Text(
                            text = "Spend Pixel Gems to customize your dodger",
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextSecondary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Daily Quiz Banner (Screen 3 Entry point)
                Surface(
                    shape = RoundedCornerShape(22.dp),
                    color = MintGreenBg,
                    border = androidx.compose.foundation.BorderStroke(1.dp, MintGreen.copy(alpha = 0.3f)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onStartQuiz() }
                        .testTag("daily_quiz_banner")
                ) {
                    Row(
                        modifier = Modifier.padding(18.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(46.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(MintGreen),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Quiz,
                                    contentDescription = "Quiz",
                                    tint = Color.White,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(14.dp))
                            Column {
                                Text(
                                    text = "Daily Pixel Quiz",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                )
                                Text(
                                    text = "Earn +20 Gems & Boost Streak",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MintGreen,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }

                        Button(
                            onClick = onStartQuiz,
                            colors = ButtonDefaults.buttonColors(containerColor = MintGreen),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("Start", fontWeight = FontWeight.Bold)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Daily Quests Section
                Text(
                    text = "Daily Quests",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )

                Spacer(modifier = Modifier.height(12.dp))

                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    QuestCard(
                        title = "Dodge 50 Obstacles",
                        progress = (stats?.totalGamesPlayed?.times(15) ?: 0).coerceAtMost(50),
                        max = 50,
                        rewardGems = 10
                    )
                    QuestCard(
                        title = "Score 300+ in One Run",
                        progress = if ((stats?.highScore ?: 0) >= 300) 1 else 0,
                        max = 1,
                        rewardGems = 15
                    )
                    QuestCard(
                        title = "Maintain 5-Day Streak",
                        progress = stats?.streakDays?.coerceAtMost(5) ?: 5,
                        max = 5,
                        rewardGems = 25
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Hero Skin Locker Section
                Text(
                    text = "Pixel Hero Locker",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )

                Spacer(modifier = Modifier.height(12.dp))

                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    AVAILABLE_SKINS.forEach { skin ->
                        val isEquipped = currentSkinId == skin.id
                        // We check if unlocked (either default or purchased / gems logic)
                        val isUnlocked = skin.isUnlockedByDefault || (stats?.totalXp ?: 0) > skin.priceGems * 2

                        SkinCard(
                            skin = skin,
                            isEquipped = isEquipped,
                            isUnlocked = isUnlocked,
                            playerGems = gems,
                            onEquip = { onSelectSkin(skin.id) },
                            onBuy = { onBuySkin(skin) }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(30.dp))
            }
        }
    }
}

@Composable
private fun QuestCard(
    title: String,
    progress: Int,
    max: Int,
    rewardGems: Int
) {
    val isComplete = progress >= max
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = PureWhite,
        border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = TextPrimary
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Bolt,
                        contentDescription = "Gems",
                        tint = GemAmber,
                        modifier = Modifier.size(14.dp)
                    )
                    Text(
                        text = "+$rewardGems",
                        fontFamily = PixelFamily,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = GemAmber
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                LinearProgressIndicator(
                    progress = { (progress.toFloat() / max.toFloat()).coerceIn(0f, 1f) },
                    modifier = Modifier
                        .weight(1f)
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp)),
                    color = if (isComplete) MintGreen else SkyBlue,
                    trackColor = Color(0xFFF1F5F9),
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = "$progress / $max",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextSecondary
                )
            }
        }
    }
}

@Composable
private fun SkinCard(
    skin: Skin,
    isEquipped: Boolean,
    isUnlocked: Boolean,
    playerGems: Int,
    onEquip: () -> Unit,
    onBuy: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(18.dp),
        color = PureWhite,
        border = androidx.compose.foundation.BorderStroke(
            if (isEquipped) 2.dp else 1.dp,
            if (isEquipped) SkyBlue else CardBorder
        ),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Pixel Art Sprite Preview
            Box(
                modifier = Modifier
                    .size(54.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(Color(0xFF0F172A)),
                contentAlignment = Alignment.Center
            ) {
                Canvas(modifier = Modifier.size(40.dp)) {
                    drawPlayer(
                        skinId = skin.id,
                        x = size.width / 2f,
                        y = size.height / 2f,
                        scale = 3.5f
                    )
                }
            }

            Spacer(modifier = Modifier.width(14.dp))

            // Details
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = skin.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Text(
                    text = skin.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary
                )
            }

            Spacer(modifier = Modifier.width(10.dp))

            // Action Button
            when {
                isEquipped -> {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = SkyBlue.copy(alpha = 0.15f)
                    ) {
                        Text(
                            text = "EQUIPPED",
                            fontFamily = PixelFamily,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = SkyBlue,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                        )
                    }
                }
                isUnlocked -> {
                    Button(
                        onClick = onEquip,
                        colors = ButtonDefaults.buttonColors(containerColor = SkyBlue),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Equip", fontWeight = FontWeight.Bold)
                    }
                }
                else -> {
                    Button(
                        onClick = onBuy,
                        enabled = playerGems >= skin.priceGems,
                        colors = ButtonDefaults.buttonColors(containerColor = GemAmber),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Default.Bolt, contentDescription = null, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(2.dp))
                        Text("${skin.priceGems}", fontFamily = PixelFamily, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

// SCREEN 3: Interactive Quiz Mode (exactly modeled after image 3 in the screenshot)
@Composable
private fun DailyQuizView(
    quiz: DailyQuizQuestion,
    selectedOption: Int?,
    isSubmitted: Boolean,
    isCorrect: Boolean,
    onSelectOption: (Int) -> Unit,
    onSubmit: () -> Unit,
    onContinue: () -> Unit,
    onClose: () -> Unit,
    currentGems: Int
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(CreamBackground)
            .padding(horizontal = 20.dp, vertical = 10.dp)
            .verticalScroll(rememberScrollState())
    ) {
        // Header with Close and Energy Pill matching Screen 3
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onClose) {
                Icon(Icons.Default.Close, contentDescription = "Close", tint = TextPrimary)
            }

            // Progress bar in header
            LinearProgressIndicator(
                progress = { 0.75f },
                modifier = Modifier
                    .width(160.dp)
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp)),
                color = CoralRed,
                trackColor = Color(0xFFF1F5F9)
            )

            // Gems pill
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = GemAmberBg
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Bolt, contentDescription = null, tint = GemAmber, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(2.dp))
                    Text(text = "$currentGems", fontFamily = PixelFamily, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = GemAmber)
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Quiz subtitle and main question matching Screen 3 typography
        Text(
            text = "Retro Reflex Quiz",
            style = MaterialTheme.typography.labelLarge,
            color = TextSecondary
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = quiz.question,
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold,
            color = TextPrimary
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Large hero illustration center card (mirroring the giraffe illustration in image 3)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
                .clip(RoundedCornerShape(24.dp))
                .background(MintGreenBg),
            contentAlignment = Alignment.Center
        ) {
            // Retro 8-bit hero sprite center
            Canvas(modifier = Modifier.size(100.dp)) {
                drawPlayer(
                    skinId = "ship_classic",
                    x = size.width / 2f,
                    y = size.height / 2f,
                    scale = 8.0f,
                    hasShield = true
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // 2x2 Answer Option Buttons (matching Elephant, Giraffe, Zebra, Lion in Screen 3)
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            for (row in 0..1) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    for (col in 0..1) {
                        val index = row * 2 + col
                        if (index < quiz.options.size) {
                            val optionText = quiz.options[index]
                            val isSelected = selectedOption == index
                            val isTheCorrectOne = isSubmitted && index == quiz.correctIndex

                            val buttonBg = when {
                                isTheCorrectOne -> MintGreenBg
                                isSubmitted && isSelected && !isCorrect -> Color(0xFFFFE4E6)
                                isSelected -> SkyBlueBg
                                else -> PureWhite
                            }
                            val buttonBorder = when {
                                isTheCorrectOne -> MintGreen
                                isSubmitted && isSelected && !isCorrect -> CoralRed
                                isSelected -> SkyBlue
                                else -> CardBorder
                            }

                            Surface(
                                shape = RoundedCornerShape(16.dp),
                                color = buttonBg,
                                border = androidx.compose.foundation.BorderStroke(2.dp, buttonBorder),
                                modifier = Modifier
                                    .weight(1f)
                                    .height(58.dp)
                                    .clickable { if (!isSubmitted) onSelectOption(index) }
                            ) {
                                Box(
                                    modifier = Modifier.fillMaxSize(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = optionText,
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = when {
                                            isTheCorrectOne -> MintGreen
                                            isSubmitted && isSelected && !isCorrect -> CoralRed
                                            isSelected -> SkyBlue
                                            else -> TextPrimary
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (isSubmitted) {
            Text(
                text = if (isCorrect) "✓ Correct! ${quiz.explanation}" else "✗ Oops! ${quiz.explanation}",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
                color = if (isCorrect) MintGreen else CoralRed
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Continue Button matching the wide blue button in Screen 3
        Button(
            onClick = {
                if (isSubmitted) {
                    onContinue()
                } else if (selectedOption != null) {
                    onSubmit()
                }
            },
            enabled = selectedOption != null,
            colors = ButtonDefaults.buttonColors(containerColor = SkyBlue),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp)
                .testTag("quiz_continue_button")
        ) {
            Text(
                text = if (isSubmitted) "Continue" else "Check Answer",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun QuizCompletedView(
    earnedGems: Int,
    onClose: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(CreamBackground)
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = PureWhite,
            border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "QUIZ MASTER!",
                    fontFamily = PixelFamily,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = MintGreen
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "You earned +$earnedGems Pixel Gems & kept your streak on fire!",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary
                )
                Spacer(modifier = Modifier.height(20.dp))
                Button(
                    onClick = onClose,
                    colors = ButtonDefaults.buttonColors(containerColor = SkyBlue),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Collect & Return", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
