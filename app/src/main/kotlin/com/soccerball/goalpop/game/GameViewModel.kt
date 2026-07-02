package com.soccerball.goalpop.game

import androidx.compose.ui.geometry.Offset
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.soccerball.goalpop.analytics.Analytics
import com.soccerball.goalpop.data.GamePreferences
import com.soccerball.goalpop.game.model.GamePhase
import com.soccerball.goalpop.game.model.GameUiState
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class GameViewModel(
    private val preferences: GamePreferences,
) : ViewModel() {
    private val engine = BubbleShooterEngine()
    private val _uiState = MutableStateFlow(GameUiState())
    val uiState: StateFlow<GameUiState> = _uiState.asStateFlow()

    private var gameLoopJob: Job? = null
    private var layoutWidth = 0f
    private var playfieldHeight = 0f

    private val _isPaused = MutableStateFlow(false)
    val isPaused: StateFlow<Boolean> = _isPaused.asStateFlow()

    fun configureLayout(width: Float, playfieldH: Float) {
        if (width <= 0f || playfieldH <= 0f) return
        layoutWidth = width
        playfieldHeight = playfieldH
        engine.configureLayout(width, playfieldH)
    }

    fun startGame(level: Int) {
        gameLoopJob?.cancel()
        _isPaused.value = false
        _uiState.value = engine.startLevel(level)
        Analytics.reportGameStart(level)
        if (layoutWidth > 0f && playfieldHeight > 0f) {
            engine.configureLayout(layoutWidth, playfieldHeight)
        }
        startGameLoop()
    }

    fun onAimStart(touch: Offset) {
        _uiState.value = engine.beginAim(_uiState.value, touch)
    }

    fun onAim(touch: Offset) {
        _uiState.value = engine.updateAim(_uiState.value, touch)
    }

    fun onAimCancel() {
        _uiState.value = engine.cancelAim(_uiState.value)
    }

    fun onShoot() {
        val current = _uiState.value
        if (current.phase != GamePhase.Aiming || _isPaused.value) return
        _uiState.value = engine.shoot(current)
    }

    fun pause() {
        _isPaused.value = true
        _uiState.value = engine.cancelAim(_uiState.value)
    }

    fun resume() {
        _isPaused.value = false
    }

    fun isTouchOnShooter(touch: Offset): Boolean = engine.isTouchOnShooter(touch)

    fun canStartAim(touch: Offset): Boolean = engine.canStartAim(touch)

    fun launchPoint(): Offset = engine.launchPoint()

    private fun onGameEndHandled() {
        val state = _uiState.value
        viewModelScope.launch {
            preferences.updateHighScore(state.score)
            when (state.phase) {
                GamePhase.Won -> {
                    Analytics.reportGameWin(state.level, state.score)
                    preferences.setCurrentLevel(state.level + 1)
                }
                GamePhase.Lost -> Analytics.reportGameLoss(state.level, state.score)
                else -> Unit
            }
        }
    }

    private fun startGameLoop() {
        gameLoopJob = viewModelScope.launch {
            while (true) {
                if (_isPaused.value) {
                    delay(50)
                    continue
                }

                val dt = 0.016f
                var state = _uiState.value

                when (state.phase) {
                    GamePhase.Shooting -> state = engine.tickShooting(state, dt)
                    GamePhase.Resolving -> {
                        if (state.poppingCells.isNotEmpty()) {
                            delay(200)
                            state = engine.tickPopping(state)
                        }
                        state = engine.tickFalling(state, dt)
                    }
                    GamePhase.Won, GamePhase.Lost -> break
                    else -> Unit
                }

                _uiState.value = state

                if (state.phase == GamePhase.Won || state.phase == GamePhase.Lost) {
                    onGameEndHandled()
                    break
                }

                delay(16)
            }
        }
    }

    fun shooterPosition(): Offset = engine.shooterPosition()

    fun dangerLineY(): Float = engine.dangerLineY()

    fun ballRadius(): Float = engine.ballRadius()

    fun grid() = engine.grid()

    override fun onCleared() {
        gameLoopJob?.cancel()
        super.onCleared()
    }
}
