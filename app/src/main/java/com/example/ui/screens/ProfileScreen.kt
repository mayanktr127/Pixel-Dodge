package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Vibration
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
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
import com.example.data.model.BadgeEntity
import com.example.data.model.UserStatsEntity
import com.example.ui.components.AppTopBar
import com.example.ui.components.StreakCard
import com.example.ui.theme.CardBorder
import com.example.ui.theme.CoralRed
import com.example.ui.theme.CoralRedBg
import com.example.ui.theme.CreamBackground
import com.example.ui.theme.GemAmber
import com.example.ui.theme.GemAmberBg
import com.example.ui.theme.LavenderPurple
import com.example.ui.theme.LavenderPurpleBg
import com.example.ui.theme.MintGreen
import com.example.ui.theme.MintGreenBg
import com.example.ui.theme.PixelFamily
import com.example.ui.theme.PureWhite
import com.example.ui.theme.SkyBlue
import com.example.ui.theme.SkyBlueBg
import com.example.ui.theme.StreakOrange
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.TextTertiary

@Composable
fun ProfileScreen(
    stats: UserStatsEntity?,
    badges: List<BadgeEntity>,
    onToggleSound: () -> Unit,
    onToggleHaptics: () -> Unit,
    modifier: Modifier = Modifier
) {
    val streakDays = stats?.streakDays ?: 5
    val gems = stats?.gems ?: 25
    val totalXp = stats?.totalXp ?: 120
    val totalGames = stats?.totalGamesPlayed ?: 0
    val highScore = stats?.highScore ?: 0
    val soundEnabled = stats?.soundEnabled ?: true
    val hapticsEnabled = stats?.hapticsEnabled ?: true

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(CreamBackground)
            .verticalScroll(rememberScrollState())
    ) {
        // Top bar
        AppTopBar(streakDays = streakDays, gems = gems)

        // Profile Greeting Header matching Screen 4 of uploaded image
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Great job, Dodger! 🌟",
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "Keep dodging and earn amazing rewards.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Avatar circle with badge
            Box(
                modifier = Modifier
                    .size(62.dp)
                    .clip(CircleShape)
                    .background(GemAmberBg)
                    .border(2.dp, GemAmber, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "🚀",
                    fontSize = 30.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Streak Card matching Screen 4
        Box(modifier = Modifier.padding(horizontal = 20.dp)) {
            StreakCard(streakDays = streakDays)
        }

        Spacer(modifier = Modifier.height(20.dp))

        // 4 Key Stats in a row matching Screen 4
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            StatCard(
                icon = Icons.Default.Bolt,
                iconColor = GemAmber,
                label = "Total XP",
                value = "$totalXp",
                modifier = Modifier.weight(1f)
            )
            StatCard(
                icon = Icons.Default.MenuBook,
                iconColor = MintGreen,
                label = "Dodges",
                value = "${totalGames * 12 + 15}",
                modifier = Modifier.weight(1f)
            )
            StatCard(
                icon = Icons.Default.SportsEsports,
                iconColor = LavenderPurple,
                label = "Games",
                value = "$totalGames",
                modifier = Modifier.weight(1f)
            )
            StatCard(
                icon = Icons.Default.EmojiEvents,
                iconColor = CoralRed,
                label = "Best",
                value = "$highScore",
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Badges Section matching Screen 4
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Badges",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
            Text(
                text = "${badges.count { it.isUnlocked }} / ${badges.size}",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
                color = SkyBlue
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Horizontal Badges Shelf matching Screen 4
        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 20.dp),
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            items(badges) { badge ->
                BadgeItem(badge = badge)
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Recent Achievements matching Screen 4
        Text(
            text = "Recent Achievements",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = TextPrimary,
            modifier = Modifier.padding(horizontal = 20.dp)
        )

        Spacer(modifier = Modifier.height(10.dp))

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            AchievementTile(
                title = "Dodger Master",
                description = "You completed your daily reflex challenge!",
                time = "Just now",
                iconColor = LavenderPurple
            )
            AchievementTile(
                title = "Streak Champion",
                description = "Kept your 5-day streak alive!",
                time = "Today",
                iconColor = StreakOrange
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Settings (Audio & Haptics)
        Text(
            text = "Settings",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = TextPrimary,
            modifier = Modifier.padding(horizontal = 20.dp)
        )

        Spacer(modifier = Modifier.height(10.dp))

        Surface(
            shape = RoundedCornerShape(18.dp),
            color = PureWhite,
            border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.VolumeUp, contentDescription = null, tint = SkyBlue, modifier = Modifier.size(22.dp))
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(text = "8-Bit Retro Sound FX", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
                    }
                    Switch(
                        checked = soundEnabled,
                        onCheckedChange = { onToggleSound() },
                        colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = SkyBlue)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Vibration, contentDescription = null, tint = StreakOrange, modifier = Modifier.size(22.dp))
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(text = "Haptic Vibration", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
                    }
                    Switch(
                        checked = hapticsEnabled,
                        onCheckedChange = { onToggleHaptics() },
                        colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = StreakOrange)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(30.dp))
    }
}

@Composable
private fun StatCard(
    icon: ImageVector,
    iconColor: Color,
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(18.dp),
        color = PureWhite,
        border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = iconColor,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = TextSecondary,
                maxLines = 1
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = value,
                fontFamily = PixelFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = TextPrimary
            )
        }
    }
}

@Composable
private fun BadgeItem(badge: BadgeEntity) {
    val badgeColor = when (badge.id) {
        "starter" -> GemAmber
        "quick_learner" -> StreakOrange
        "top_scorer" -> LavenderPurple
        "helper" -> MintGreen
        "champion" -> CoralRed
        else -> SkyBlue
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.width(68.dp)
    ) {
        Box(
            modifier = Modifier
                .size(56.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(if (badge.isUnlocked) badgeColor.copy(alpha = 0.15f) else Color(0xFFF1F5F9))
                .border(
                    width = 2.dp,
                    color = if (badge.isUnlocked) badgeColor else CardBorder,
                    shape = RoundedCornerShape(16.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            if (badge.isUnlocked) {
                Icon(
                    imageVector = Icons.Default.EmojiEvents,
                    contentDescription = badge.title,
                    tint = badgeColor,
                    modifier = Modifier.size(28.dp)
                )
            } else {
                Icon(
                    imageVector = Icons.Default.Lock,
                    contentDescription = "Locked",
                    tint = TextTertiary,
                    modifier = Modifier.size(22.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = badge.title,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = if (badge.isUnlocked) FontWeight.Bold else FontWeight.Normal,
            color = if (badge.isUnlocked) TextPrimary else TextTertiary,
            maxLines = 1
        )
    }
}

@Composable
private fun AchievementTile(
    title: String,
    description: String,
    time: String,
    iconColor: Color
) {
    Surface(
        shape = RoundedCornerShape(18.dp),
        color = PureWhite,
        border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(iconColor.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = null,
                    tint = iconColor,
                    modifier = Modifier.size(22.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary
                )
            }

            Text(
                text = time,
                style = MaterialTheme.typography.labelSmall,
                color = TextTertiary
            )
        }
    }
}
