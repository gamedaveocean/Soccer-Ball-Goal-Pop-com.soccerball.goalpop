package com.soccerball.goalpop.analytics

import io.appmetrica.analytics.AppMetrica

object Analytics {
    const val NAMESPACE = "soccer_ball_goal_pop"
    const val GAME_NAME = "soccer_ball_goal_pop"

    const val SOURCE_SHOP = "shop"
    const val TYPE_COIN = "coin"

    const val ITEM_FREE_COINS_1000 = "free_coins_1000"

    fun itemLuckyWheel(prize: Int) = "lucky_wheel_$prize"
    fun itemWinBoost(multiplier: Int) = "win_boost_x$multiplier"

    private fun reportEventWithMap(event: String, payload: Map<String, Any>) {
        AppMetrica.reportEvent(event, mapOf(NAMESPACE to payload))
    }

    private fun gamePayload(extra: Map<String, Any> = emptyMap()): Map<String, Any> {
        return mapOf("game_name" to GAME_NAME) + extra
    }

    fun reportGameStart(level: Int) {
        reportEventWithMap("game_start", gamePayload(mapOf("level" to level)))
    }

    fun reportGameWin(level: Int, score: Int) {
        reportEventWithMap(
            "game_win",
            gamePayload(mapOf("level" to level, "score" to score)),
        )
    }

    fun reportGameLoss(level: Int, score: Int) {
        reportEventWithMap(
            "game_loss",
            gamePayload(mapOf("level" to level, "score" to score)),
        )
    }

    fun reportBetChange(bet: Int) {
        reportEventWithMap("bet_change", gamePayload(mapOf("bet" to bet)))
    }

    fun reportPaywallView(source: String) {
        reportEventWithMap("paywall_view", mapOf("source" to source))
    }

    fun reportPaywallClose(source: String) {
        reportEventWithMap("paywall_close", mapOf("source" to source))
    }

    fun reportPurchaseClick(itemId: String, type: String) {
        reportEventWithMap(
            "purchase_click",
            mapOf("item_id" to itemId, "type" to type),
        )
    }

    fun reportPurchaseSuccess(itemId: String, price: Double, type: String) {
        reportEventWithMap(
            "purchase_success",
            mapOf("item_id" to itemId, "price" to price, "type" to type),
        )
    }

    fun reportPurchaseError(itemId: String, type: String) {
        reportEventWithMap(
            "purchase_error",
            mapOf("item_id" to itemId, "type" to type),
        )
    }

    fun reportSettingsOpen() {
        reportEventWithMap("settings_open", emptyMap())
    }

    fun reportAppClose() {
        reportEventWithMap("app_close", emptyMap())
    }
}
