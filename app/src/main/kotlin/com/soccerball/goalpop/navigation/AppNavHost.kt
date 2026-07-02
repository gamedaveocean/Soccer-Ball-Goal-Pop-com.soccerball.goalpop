package com.soccerball.goalpop.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.soccerball.goalpop.data.GamePreferences
import com.soccerball.goalpop.game.GameViewModel
import com.soccerball.goalpop.ui.game.GameScreen
import com.soccerball.goalpop.ui.leaderboard.LeaderboardScreen
import com.soccerball.goalpop.ui.menu.MainMenuScreen
import com.soccerball.goalpop.ui.settings.LegalDocument
import com.soccerball.goalpop.ui.settings.LegalDocumentScreen
import com.soccerball.goalpop.ui.settings.SettingsScreen
import com.soccerball.goalpop.ui.shop.ShopScreen
import com.soccerball.goalpop.ui.splash.SplashScreen
import com.soccerball.goalpop.ui.wheel.LuckWheelScreen

object Routes {
    const val SPLASH = "splash"
    const val MENU = "menu"
    const val GAME = "game/{level}"
    const val SETTINGS = "settings"
    const val LEGAL = "legal/{document}"
    const val SHOP = "shop"
    const val LUCKY_WHEEL = "lucky_wheel"
    const val LEADERBOARD = "leaderboard"

    fun game(level: Int) = "game/$level"

    fun legal(document: LegalDocument) = when (document) {
        LegalDocument.PrivacyPolicy -> "legal/privacy"
        LegalDocument.TermsOfUse -> "legal/terms"
    }
}

@Composable
fun AppNavHost(preferences: GamePreferences) {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = Routes.SPLASH) {
        composable(Routes.SPLASH) {
            SplashScreen(
                onFinished = {
                    navController.navigate(Routes.MENU) {
                        popUpTo(Routes.SPLASH) { inclusive = true }
                    }
                },
                onPrivacyPolicy = {
                    navController.navigate(Routes.legal(LegalDocument.PrivacyPolicy))
                },
                onTermsOfUse = {
                    navController.navigate(Routes.legal(LegalDocument.TermsOfUse))
                },
            )
        }

        composable(Routes.MENU) {
            MainMenuScreen(
                preferences = preferences,
                onPlay = { level ->
                    navController.navigate(Routes.game(level))
                },
                onShop = {
                    navController.navigate(Routes.SHOP)
                },
                onLuckyWheel = {
                    navController.navigate(Routes.LUCKY_WHEEL)
                },
                onSettings = {
                    navController.navigate(Routes.SETTINGS)
                },
                onLeaderboard = {
                    navController.navigate(Routes.LEADERBOARD)
                },
                onPrivacyPolicy = {
                    navController.navigate(Routes.legal(LegalDocument.PrivacyPolicy))
                },
                onTermsOfUse = {
                    navController.navigate(Routes.legal(LegalDocument.TermsOfUse))
                },
            )
        }

        composable(Routes.SHOP) {
            ShopScreen(
                preferences = preferences,
                onBack = { navController.popBackStack() },
            )
        }

        composable(Routes.LUCKY_WHEEL) {
            LuckWheelScreen(
                preferences = preferences,
                onBack = { navController.popBackStack() },
            )
        }

        composable(Routes.LEADERBOARD) {
            LeaderboardScreen(
                preferences = preferences,
                onBack = { navController.popBackStack() },
            )
        }

        composable(
            route = Routes.GAME,
            arguments = listOf(navArgument("level") { type = NavType.IntType }),
        ) { backStackEntry ->
            val level = backStackEntry.arguments?.getInt("level") ?: 1
            val gameViewModel: GameViewModel = viewModel(
                factory = remember {
                    GameViewModelFactory(preferences)
                },
            )
            GameScreen(
                viewModel = gameViewModel,
                level = level,
                onNextLevel = { nextLevel ->
                    navController.navigate(Routes.game(nextLevel)) {
                        popUpTo(Routes.game(level)) { inclusive = true }
                    }
                },
                onRetry = { retryLevel ->
                    navController.navigate(Routes.game(retryLevel)) {
                        popUpTo(Routes.game(level)) { inclusive = true }
                    }
                },
                onHome = {
                    navController.navigate(Routes.MENU) {
                        popUpTo(Routes.MENU) { inclusive = true }
                    }
                },
                onSettings = {
                    navController.navigate(Routes.SETTINGS)
                },
                onLuckyWheel = {
                    navController.navigate(Routes.LUCKY_WHEEL)
                },
                onLeaderboard = {
                    navController.navigate(Routes.LEADERBOARD)
                },
            )
        }

        composable(Routes.SETTINGS) {
            SettingsScreen(
                preferences = preferences,
                onBack = { navController.popBackStack() },
                onPrivacyPolicy = {
                    navController.navigate(Routes.legal(LegalDocument.PrivacyPolicy))
                },
                onTermsOfUse = {
                    navController.navigate(Routes.legal(LegalDocument.TermsOfUse))
                },
                onStore = {
                    navController.navigate(Routes.SHOP)
                },
            )
        }

        composable(
            route = Routes.LEGAL,
            arguments = listOf(navArgument("document") { type = NavType.StringType }),
        ) { backStackEntry ->
            val document = when (backStackEntry.arguments?.getString("document")) {
                "privacy" -> LegalDocument.PrivacyPolicy
                "terms" -> LegalDocument.TermsOfUse
                else -> LegalDocument.PrivacyPolicy
            }
            LegalDocumentScreen(
                document = document,
                onBack = { navController.popBackStack() },
            )
        }
    }
}
