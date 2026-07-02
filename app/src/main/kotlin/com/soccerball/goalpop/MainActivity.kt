package com.soccerball.goalpop

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.view.WindowCompat
import com.soccerball.goalpop.navigation.AppNavHost
import com.soccerball.goalpop.ui.theme.SoccerBallTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        WindowCompat.setDecorFitsSystemWindows(window, false)

        val preferences = (application as SoccerBallApp).preferences

        setContent {
            SoccerBallTheme {
                AppNavHost(preferences = preferences)
            }
        }
    }
}
