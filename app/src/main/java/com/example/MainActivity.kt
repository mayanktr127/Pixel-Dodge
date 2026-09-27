package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.example.ui.AppTab
import com.example.ui.MainViewModel
import com.example.ui.components.AppBottomNav
import com.example.ui.screens.GameScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.ProfileScreen
import com.example.ui.screens.RewardsScreen
import com.example.ui.theme.CreamBackground
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.PixelDodgeTheme

class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PixelDodgeTheme {
                PixelDodgeApp(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun PixelDodgeApp(viewModel: MainViewModel) {
    val currentTab by viewModel.currentTab.collectAsState()
    val activeSession by viewModel.activeGameSession.collectAsState()
    val userStats by viewModel.userStats.collectAsState()
    val badges by viewModel.badges.collectAsState()
    val quizState by viewModel.quizState.collectAsState()

    val isPlayingGame = currentTab == AppTab.GAME && activeSession != null

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            if (!isPlayingGame) {
                AppBottomNav(
                    currentTab = currentTab,
                    onTabSelected = { tab ->
                        if (tab == AppTab.GAME && activeSession == null) {
                            viewModel.startNewGame()
                        } else {
                            viewModel.setTab(tab)
                        }
                    }
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(if (isPlayingGame) DarkSurface else CreamBackground)
                .padding(innerPadding)
        ) {
            when {
                isPlayingGame -> {
                    activeSession?.let { session ->
                        GameScreen(
                            session = session,
                            onBackToHome = { viewModel.quitToHome() },
                            onRestart = { viewModel.restartGame() }
                        )
                    }
                }
                currentTab == AppTab.HOME -> {
                    HomeScreen(
                        stats = userStats,
                        onStartGame = { mode -> viewModel.startNewGame(mode) },
                        onNavigateToQuiz = {
                            viewModel.setTab(AppTab.REWARDS)
                            viewModel.startDailyQuiz()
                        }
                    )
                }
                currentTab == AppTab.GAME -> {
                    // Fallback if on game tab with no active session
                    HomeScreen(
                        stats = userStats,
                        onStartGame = { mode -> viewModel.startNewGame(mode) },
                        onNavigateToQuiz = {
                            viewModel.setTab(AppTab.REWARDS)
                            viewModel.startDailyQuiz()
                        }
                    )
                }
                currentTab == AppTab.REWARDS -> {
                    RewardsScreen(
                        stats = userStats,
                        quizState = quizState,
                        onStartQuiz = { viewModel.startDailyQuiz() },
                        onSelectQuizOption = { viewModel.selectQuizOption(it) },
                        onSubmitQuiz = { viewModel.submitQuizAnswer() },
                        onCompleteQuiz = { viewModel.completeQuiz() },
                        onResetQuiz = { viewModel.resetQuiz() },
                        onSelectSkin = { viewModel.selectSkin(it) },
                        onBuySkin = { viewModel.buySkin(it) }
                    )
                }
                currentTab == AppTab.PROFILE -> {
                    ProfileScreen(
                        stats = userStats,
                        badges = badges,
                        onToggleSound = { viewModel.toggleSound() },
                        onToggleHaptics = { viewModel.toggleHaptics() }
                    )
                }
            }
        }
    }
}
