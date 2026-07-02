package com.soccerball.goalpop.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "soccer_ball_prefs")

class GamePreferences(private val context: Context) {
    private val highScoreKey = intPreferencesKey("high_score")
    private val currentLevelKey = intPreferencesKey("current_level")
    private val soundEnabledKey = booleanPreferencesKey("sound_enabled")
    private val musicEnabledKey = booleanPreferencesKey("music_enabled")
    private val coinsKey = intPreferencesKey("coins")

    val highScore: Flow<Int> = context.dataStore.data.map { it[highScoreKey] ?: 0 }
    val currentLevel: Flow<Int> = context.dataStore.data.map { it[currentLevelKey] ?: 1 }
    val soundEnabled: Flow<Boolean> = context.dataStore.data.map { it[soundEnabledKey] ?: true }
    val musicEnabled: Flow<Boolean> = context.dataStore.data.map { it[musicEnabledKey] ?: true }
    val coins: Flow<Int> = context.dataStore.data.map { it[coinsKey] ?: 0 }

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
        }
    }

    suspend fun setMusicEnabled(enabled: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[musicEnabledKey] = enabled
        }
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
    }
}
