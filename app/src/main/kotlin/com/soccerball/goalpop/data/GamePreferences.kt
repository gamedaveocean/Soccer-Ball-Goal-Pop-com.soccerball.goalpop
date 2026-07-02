package com.soccerball.goalpop.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import java.util.Calendar

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "soccer_ball_prefs")

class GamePreferences(private val context: Context) {
    private val highScoreKey = intPreferencesKey("high_score")
    private val currentLevelKey = intPreferencesKey("current_level")
    private val soundEnabledKey = booleanPreferencesKey("sound_enabled")
    private val musicEnabledKey = booleanPreferencesKey("music_enabled")
    private val coinsKey = intPreferencesKey("coins")
    private val vibrationEnabledKey = booleanPreferencesKey("vibration_enabled")
    private val soundVolumeKey = intPreferencesKey("sound_volume")
    private val musicVolumeKey = intPreferencesKey("music_volume")
    private val lastDailyBonusKey = longPreferencesKey("last_daily_bonus")
    private val lastWheelSpinKey = longPreferencesKey("last_wheel_spin")
    private val freeSpinsKey = intPreferencesKey("free_spins")
    private val playerNameKey = stringPreferencesKey("player_name")

    val highScore: Flow<Int> = context.dataStore.data.map { it[highScoreKey] ?: 0 }
    val currentLevel: Flow<Int> = context.dataStore.data.map { it[currentLevelKey] ?: 1 }
    val soundEnabled: Flow<Boolean> = context.dataStore.data.map { (it[soundVolumeKey] ?: 100) > 0 }
    val musicEnabled: Flow<Boolean> = context.dataStore.data.map { (it[musicVolumeKey] ?: 100) > 0 }
    val soundVolume: Flow<Int> = context.dataStore.data.map { it[soundVolumeKey] ?: 100 }
    val musicVolume: Flow<Int> = context.dataStore.data.map { it[musicVolumeKey] ?: 100 }
    val vibrationEnabled: Flow<Boolean> = context.dataStore.data.map { it[vibrationEnabledKey] ?: true }
    val coins: Flow<Int> = context.dataStore.data.map { it[coinsKey] ?: 0 }
    val freeSpins: Flow<Int> = context.dataStore.data.map { it[freeSpinsKey] ?: 0 }
    val playerName: Flow<String> = context.dataStore.data.map { it[playerNameKey] ?: "Player" }

    suspend fun updateHighScore(score: Int) {
        context.dataStore.edit { prefs ->
            val current = prefs[highScoreKey] ?: 0
            if (score > current) {
                prefs[highScoreKey] = score
            }
        }
    }

    suspend fun setCurrentLevel(level: Int) {
        context.dataStore.edit { prefs ->
            prefs[currentLevelKey] = level
        }
    }

    suspend fun setSoundEnabled(enabled: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[soundEnabledKey] = enabled
            prefs[soundVolumeKey] = if (enabled) 100 else 0
        }
    }

    suspend fun setMusicEnabled(enabled: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[musicEnabledKey] = enabled
            prefs[musicVolumeKey] = if (enabled) 100 else 0
        }
    }

    suspend fun setSoundVolume(volume: Int) {
        val clamped = volume.coerceIn(0, 100)
        context.dataStore.edit { prefs ->
            prefs[soundVolumeKey] = clamped
            prefs[soundEnabledKey] = clamped > 0
        }
    }

    suspend fun setMusicVolume(volume: Int) {
        val clamped = volume.coerceIn(0, 100)
        context.dataStore.edit { prefs ->
            prefs[musicVolumeKey] = clamped
            prefs[musicEnabledKey] = clamped > 0
        }
    }

    suspend fun setVibrationEnabled(enabled: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[vibrationEnabledKey] = enabled
        }
    }

    suspend fun canClaimDailyBonus(): Boolean {
        val last = context.dataStore.data.first()[lastDailyBonusKey] ?: 0L
        return !isSameDay(last, System.currentTimeMillis())
    }

    suspend fun claimDailyBonus(): Int {
        val reward = DAILY_BONUS_COINS
        context.dataStore.edit { prefs ->
            val current = prefs[coinsKey] ?: 0
            prefs[coinsKey] = current + reward
            prefs[lastDailyBonusKey] = System.currentTimeMillis()
        }
        return reward
    }

    suspend fun canFreeWheelSpin(): Boolean {
        val last = context.dataStore.data.first()[lastWheelSpinKey] ?: 0L
        return System.currentTimeMillis() - last >= WHEEL_FREE_SPIN_COOLDOWN_MS
    }

    suspend fun markWheelSpun() {
        context.dataStore.edit { prefs ->
            prefs[lastWheelSpinKey] = System.currentTimeMillis()
        }
    }

    suspend fun useFreeSpin(): Boolean {
        var used = false
        context.dataStore.edit { prefs ->
            val spins = prefs[freeSpinsKey] ?: 0
            if (spins > 0) {
                prefs[freeSpinsKey] = spins - 1
                used = true
            }
        }
        return used
    }

    suspend fun addFreeSpins(count: Int) {
        if (count <= 0) return
        context.dataStore.edit { prefs ->
            val current = prefs[freeSpinsKey] ?: 0
            prefs[freeSpinsKey] = current + count
        }
    }

    fun leaderboardEntries(highScore: Int, playerName: String): List<LeaderboardEntry> {
        val mock = listOf(
            LeaderboardEntry(1, "SoccerKing", 98500),
            LeaderboardEntry(2, "GoalMaster", 87200),
            LeaderboardEntry(3, "BallPro", 75400),
            LeaderboardEntry(4, "Striker99", 62100),
            LeaderboardEntry(5, "NetBuster", 58900),
            LeaderboardEntry(6, "KickStar", 45200),
            LeaderboardEntry(7, "PopChamp", 38700),
            LeaderboardEntry(8, "FieldHero", 29400),
            LeaderboardEntry(9, "LuckyShot", 22100),
            LeaderboardEntry(10, "Rookie", 15800),
        )
        val withPlayer = (mock + LeaderboardEntry(-1, playerName, highScore))
            .sortedByDescending { it.score }
            .take(10)
            .mapIndexed { index, entry -> entry.copy(rank = index + 1) }
        return withPlayer
    }

    private fun isSameDay(a: Long, b: Long): Boolean {
        if (a == 0L) return false
        val calA = Calendar.getInstance().apply { timeInMillis = a }
        val calB = Calendar.getInstance().apply { timeInMillis = b }
        return calA.get(Calendar.YEAR) == calB.get(Calendar.YEAR) &&
            calA.get(Calendar.DAY_OF_YEAR) == calB.get(Calendar.DAY_OF_YEAR)
    }

    suspend fun addCoins(amount: Int) {
        if (amount <= 0) return
        context.dataStore.edit { prefs ->
            val current = prefs[coinsKey] ?: 0
            prefs[coinsKey] = current + amount
        }
    }

    companion object {
        const val SHOP_VIDEO_REWARD = 1000
        const val DAILY_BONUS_COINS = 1000
        const val WHEEL_FREE_SPIN_COOLDOWN_MS = 24 * 60 * 60 * 1000L
    }
}

data class LeaderboardEntry(
    val rank: Int,
    val name: String,
    val score: Int,
)
