package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.audio.RetroAudioPlayer
import com.example.data.db.AppDatabase
import com.example.data.model.BadgeEntity
import com.example.data.model.GameRecordEntity
import com.example.data.model.UserStatsEntity
import com.example.data.repository.AVAILABLE_SKINS
import com.example.data.repository.GameRepository
import com.example.data.repository.Skin
import com.example.game.GameSession
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class AppTab {
    HOME,
    GAME,
    REWARDS,
    PROFILE
}

data class DailyQuizQuestion(
    val id: Int,
    val question: String,
    val options: List<String>,
    val correctIndex: Int,
    val rewardGems: Int = 15,
    val explanation: String
)

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: GameRepository
    val audioPlayer = RetroAudioPlayer(application)

    private val _currentTab = MutableStateFlow(AppTab.HOME)
    val currentTab: StateFlow<AppTab> = _currentTab.asStateFlow()

    private val _activeGameSession = MutableStateFlow<GameSession?>(null)
    val activeGameSession: StateFlow<GameSession?> = _activeGameSession.asStateFlow()

    private val _selectedMode = MutableStateFlow("Classic")
    val selectedMode: StateFlow<String> = _selectedMode.asStateFlow()

    // Daily quiz / challenge state (matching Screen 3 in uploaded screenshot!)
    private val _quizState = MutableStateFlow<QuizState>(QuizState.Ready)
    val quizState: StateFlow<QuizState> = _quizState.asStateFlow()

    val userStats: StateFlow<UserStatsEntity?>
    val topScores: StateFlow<List<GameRecordEntity>>
    val recentGames: StateFlow<List<GameRecordEntity>>
    val badges: StateFlow<List<BadgeEntity>>

    sealed class QuizState {
        object Ready : QuizState()
        data class Active(val question: DailyQuizQuestion, val selectedOption: Int? = null, val isSubmitted: Boolean = false, val isCorrect: Boolean = false) : QuizState()
        data class Completed(val earnedGems: Int) : QuizState()
    }

    private val quizQuestions = listOf(
        DailyQuizQuestion(
            id = 1,
            question = "Which power-up grants complete obstacle invulnerability?",
            options = listOf("Energy Shield", "Slow-Mo Clock", "Pixel Star", "1UP Heart"),
            correctIndex = 0,
            rewardGems = 20,
            explanation = "Energy Shield creates an 8-bit forcefield that destroys colliding obstacles!"
        ),
        DailyQuizQuestion(
            id = 2,
            question = "What bonus multiplier is activated by grabbing the golden Pixel Star?",
            options = listOf("1.5x Multiplier", "2x Score Multiplier", "5x Speed Burst", "Instant Win"),
            correctIndex = 1,
            rewardGems = 20,
            explanation = "Collecting Pixel Stars doubles all score and dodge points for 6 seconds!"
        ),
        DailyQuizQuestion(
            id = 3,
            question = "How do you achieve a 'CLOSE CALL' bonus in Pixel Dodge?",
            options = listOf("Pausing the game", "Touching the screen edge", "Dodging within hair-breadth of an obstacle", "Collecting 3 hearts"),
            correctIndex = 2,
            rewardGems = 25,
            explanation = "Slipping right next to falling hazard meteors awards +25 points and raises your combo!"
        )
    )
    private var currentQuizIndex = 0

    init {
        val db = AppDatabase.getDatabase(application)
        repository = GameRepository(db.gameDao())

        userStats = repository.userStats.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            null
        )

        topScores = repository.topScores.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            emptyList()
        )

        recentGames = repository.recentGames.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            emptyList()
        )

        badges = repository.badges.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            emptyList()
        )

        viewModelScope.launch {
            repository.initializeDefaultsIfNeeded()
        }
    }

    fun setTab(tab: AppTab) {
        audioPlayer.playBlip()
        _currentTab.value = tab
    }

    fun startNewGame(mode: String = _selectedMode.value) {
        audioPlayer.playPowerUp()
        _selectedMode.value = mode
        val currentHighScore = userStats.value?.highScore ?: 0
        val currentSkin = userStats.value?.selectedSkinId ?: "ship_classic"

        val session = GameSession(
            mode = mode,
            skinId = currentSkin,
            highScore = currentHighScore,
            audioPlayer = audioPlayer,
            onGameOver = { score, duration, dodged, gems ->
                viewModelScope.launch {
                    repository.recordGameRun(score, mode, duration, dodged, gems)
                }
            }
        )
        _activeGameSession.value = session
        _currentTab.value = AppTab.GAME
    }

    fun restartGame() {
        startNewGame(_selectedMode.value)
    }

    fun quitToHome() {
        audioPlayer.playBlip()
        _activeGameSession.value = null
        _currentTab.value = AppTab.HOME
    }

    fun selectSkin(skinId: String) {
        audioPlayer.playBlip()
        viewModelScope.launch {
            repository.selectSkin(skinId)
        }
    }

    fun buySkin(skin: Skin) {
        viewModelScope.launch {
            val success = repository.buySkin(skin)
            if (success) {
                audioPlayer.playCoin()
            } else {
                audioPlayer.playHit()
            }
        }
    }

    fun toggleSound() {
        viewModelScope.launch {
            repository.toggleSound()
            val current = userStats.value?.soundEnabled ?: true
            audioPlayer.soundEnabled = !current
        }
    }

    fun toggleHaptics() {
        viewModelScope.launch {
            repository.toggleHaptics()
            val current = userStats.value?.hapticsEnabled ?: true
            audioPlayer.hapticsEnabled = !current
        }
    }

    // Quiz / Daily Reflex Check (Screen 3 in image)
    fun startDailyQuiz() {
        audioPlayer.playBlip()
        val q = quizQuestions[currentQuizIndex % quizQuestions.size]
        _quizState.value = QuizState.Active(question = q)
    }

    fun selectQuizOption(index: Int) {
        audioPlayer.playBlip()
        val state = _quizState.value as? QuizState.Active ?: return
        if (state.isSubmitted) return
        _quizState.value = state.copy(selectedOption = index)
    }

    fun submitQuizAnswer() {
        val state = _quizState.value as? QuizState.Active ?: return
        val selected = state.selectedOption ?: return
        val isCorrect = selected == state.question.correctIndex
        if (isCorrect) {
            audioPlayer.playCoin()
            viewModelScope.launch {
                repository.addBonusGems(state.question.rewardGems)
            }
        } else {
            audioPlayer.playHit()
        }
        _quizState.value = state.copy(isSubmitted = true, isCorrect = isCorrect)
    }

    fun completeQuiz() {
        audioPlayer.playPowerUp()
        currentQuizIndex++
        _quizState.value = QuizState.Completed(earnedGems = 20)
    }

    fun resetQuiz() {
        _quizState.value = QuizState.Ready
    }
}
