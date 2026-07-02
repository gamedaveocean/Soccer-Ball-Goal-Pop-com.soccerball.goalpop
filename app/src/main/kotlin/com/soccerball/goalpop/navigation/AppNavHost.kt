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
import com.soccerball.goalpop.ui.gameover.GameOverScreen
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
    const val GAME_OVER = "game_over/{score}/{won}/{level}"
    const val SETTINGS = "settings"
    const val LEGAL = "legal/{document}"
    const val SHOP = "shop"
    const val LUCKY_WHEEL = "lucky_wheel"

    fun game(level: Int) = "game/$level"
    fun gameOver(score: Int, won: Boolean, level: Int) =
        "game_over/$score/$won/$level"

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
                onWin = { score ->
                    navController.navigate(Routes.gameOver(score, true, level)) {
                        popUpTo(Routes.MENU)
                    }
                },
                onLose = { score ->
                    navController.navigate(Routes.gameOver(score, false, level)) {
                        popUpTo(Routes.MENU)
                    }
                },
                onMainMenu = {
                    navController.navigate(Routes.MENU) {
                        popUpTo(Routes.MENU) { inclusive = true }
                    }
                },
            )
        }

        composable(
            route = Routes.GAME_OVER,
            arguments = listOf(
                navArgument("score") { type = NavType.IntType },
                navArgument("won") { type = NavType.BoolType },
                navArgument("level") { type = NavType.IntType },
            ),
        ) { backStackEntry ->
            val score = backStackEntry.arguments?.getInt("score") ?: 0
            val won = backStackEntry.arguments?.getBoolean("won") ?: false
            val level = backStackEntry.arguments?.getInt("level") ?: 1
            GameOverScreen(
                preferences = preferences,
                score = score,
                won = won,
                level = level,
                onRetry = {
                    val retryLevel = if (won) level + 1 else level
                    navController.navigate(Routes.game(retryLevel)) {
                        popUpTo(Routes.MENU)
                    }
                },
                onMenu = {
                    navController.navigate(Routes.MENU) {
                        popUpTo(Routes.MENU) { inclusive = true }
                    }
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
