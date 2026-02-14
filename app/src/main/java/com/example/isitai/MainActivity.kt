package com.example.isitai

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.example.isitai.ui.navigation.Route
import com.example.isitai.ui.screen.GameOverScreen
import com.example.isitai.ui.screen.GameScreen
import com.example.isitai.ui.screen.HomeScreen
import com.example.isitai.ui.theme.IsItAITheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            IsItAITheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    val navController = rememberNavController()
                    NavHost(
                        navController = navController,
                        startDestination = Route.Home,
                        modifier = Modifier.padding(innerPadding)
                    ) {
                        composable<Route.Home> {
                            HomeScreen(
                                highScore = 0,
                                onPlayClick = {
                                    navController.navigate(Route.Game)
                                }
                            )
                        }
                        composable<Route.Game> {
                            GameScreen(
                                onEndGame = {
                                    navController.navigate(Route.GameOver(streak = 0, isNewRecord = false)) {
                                        popUpTo(Route.Home) { inclusive = false }
                                    }
                                }
                            )
                        }
                        composable<Route.GameOver> { backStackEntry ->
                            val route = backStackEntry.toRoute<Route.GameOver>()
                            GameOverScreen(
                                streak = route.streak,
                                isNewRecord = route.isNewRecord,
                                onPlayAgain = {
                                    navController.navigate(Route.Game) {
                                        popUpTo(Route.Home) { inclusive = false }
                                    }
                                },
                                onHome = {
                                    navController.popBackStack(Route.Home, inclusive = false)
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}
