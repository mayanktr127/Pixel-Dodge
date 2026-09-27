package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
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
import com.example.ui.AppTab
import com.example.ui.theme.CardBorder
import com.example.ui.theme.GemAmber
import com.example.ui.theme.GemAmberBg
import com.example.ui.theme.PixelFamily
import com.example.ui.theme.PureWhite
import com.example.ui.theme.SkyBlue
import com.example.ui.theme.StreakOrange
import com.example.ui.theme.StreakOrangeBg
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.TextTertiary

@Composable
fun AppTopBar(
    streakDays: Int,
    gems: Int,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // App title with retro pixel font accent
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(SkyBlue),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "PD",
                    fontFamily = PixelFamily,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Pixel Dodge",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
        }

        // Streak pill and Gem/Energy pill matching the screenshot
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Streak Pill
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = StreakOrangeBg,
                modifier = Modifier.testTag("streak_pill")
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.LocalFireDepartment,
                        contentDescription = "Streak",
                        tint = StreakOrange,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = streakDays.toString(),
                        fontFamily = PixelFamily,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = StreakOrange
                    )
                }
            }

            // Energy / Gem Pill
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = GemAmberBg,
                modifier = Modifier.testTag("gems_pill")
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Bolt,
                        contentDescription = "Gems",
                        tint = GemAmber,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = gems.toString(),
                        fontFamily = PixelFamily,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = GemAmber
                    )
                }
            }
        }
    }
}

@Composable
fun StreakCard(
    streakDays: Int,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
) {
    val days = listOf("Su", "Mo", "Tu", "We", "Th", "Fr", "Sa")
    // Assuming active streak covers the first 5 days for visual fidelity matching screenshot
    val activeCount = streakDays.coerceIn(1, 7)

    Surface(
        shape = RoundedCornerShape(20.dp),
        color = PureWhite,
        border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder),
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .testTag("streak_card")
    ) {
        Column(
            modifier = Modifier.padding(18.dp)
        ) {
            // Header with flame and count
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.LocalFireDepartment,
                        contentDescription = "Streak Fire",
                        tint = StreakOrange,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "$streakDays",
                        fontFamily = PixelFamily,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = StreakOrange
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "day streak",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = StreakOrange
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Day circles row matching screenshot
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                days.forEachIndexed { index, dayName ->
                    val isCompleted = index < activeCount - 1
                    val isToday = index == activeCount - 1

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = dayName,
                            style = MaterialTheme.typography.labelMedium,
                            color = TextSecondary,
                            fontWeight = FontWeight.Medium
                        )
                        Spacer(modifier = Modifier.height(6.dp))

                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .clip(CircleShape)
                                .background(
                                    when {
                                        isCompleted -> StreakOrange
                                        isToday -> PureWhite
                                        else -> Color(0xFFF1F5F9)
                                    }
                                )
                                .then(
                                    if (isToday) Modifier.border(2.dp, StreakOrange, CircleShape)
                                    else Modifier
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            if (isCompleted) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = "Done",
                                    tint = Color.White,
                                    modifier = Modifier.size(18.dp)
                                )
                            } else if (isToday) {
                                Text(
                                    text = "$streakDays",
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = StreakOrange
                                )
                            } else {
                                Box(
                                    modifier = Modifier
                                        .size(6.dp)
                                        .clip(CircleShape)
                                        .background(TextTertiary)
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Subtitle footer
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "You're on fire! 🔥 Keep going!",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                    color = TextSecondary
                )
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
                    contentDescription = "Details",
                    tint = TextTertiary,
                    modifier = Modifier.size(12.dp)
                )
            }
        }
    }
}

@Composable
fun AppBottomNav(
    currentTab: AppTab,
    onTabSelected: (AppTab) -> Unit,
    modifier: Modifier = Modifier
) {
    NavigationBar(
        modifier = modifier.testTag("app_bottom_nav"),
        containerColor = PureWhite,
        tonalElevation = 8.dp
    ) {
        val items = listOf(
            Triple(AppTab.HOME, "Home", Icons.Default.Home),
            Triple(AppTab.GAME, "Games", Icons.Default.SportsEsports),
            Triple(AppTab.REWARDS, "Reward", Icons.Default.EmojiEvents),
            Triple(AppTab.PROFILE, "Profile", Icons.Default.Person)
        )

        items.forEach { (tab, label, icon) ->
            val selected = currentTab == tab
            NavigationBarItem(
                selected = selected,
                onClick = { onTabSelected(tab) },
                icon = {
                    Icon(
                        imageVector = icon,
                        contentDescription = label,
                        modifier = Modifier.size(24.dp)
                    )
                },
                label = {
                    Text(
                        text = label,
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = SkyBlue,
                    selectedTextColor = SkyBlue,
                    indicatorColor = SkyBlue.copy(alpha = 0.12f),
                    unselectedIconColor = TextTertiary,
                    unselectedTextColor = TextTertiary
                )
            )
        }
    }
}
