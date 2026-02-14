package com.example.isitai

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.example.isitai.ui.navigation.Route
import com.example.isitai.ui.screen.GameOverScreen
import com.example.isitai.ui.screen.GameScreen
import com.example.isitai.ui.screen.HomeScreen
import com.example.isitai.ui.theme.IsItAITheme
import com.example.isitai.viewmodel.GameViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            IsItAITheme {
                val viewModel: GameViewModel = viewModel(factory = GameViewModel.Factory)
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    val navController = rememberNavController()
                    NavHost(
                        navController = navController,
                        startDestination = Route.Home,
                        modifier = Modifier.padding(innerPadding)
                    ) {
                        composable<Route.Home> {
                            HomeScreen(
                                highScore = viewModel.highScore,
                                onPlayClick = {
                                    viewModel.startGame()
                                    navController.navigate(Route.Game)
                                }
                            )
                        }
                        composable<Route.Game> {
                            GameScreen(
                                viewModel = viewModel,
                                onContinueToGameOver = { streak, isNewRecord ->
                                    navController.navigate(Route.GameOver(streak, isNewRecord)) {
                                        popUpTo(Route.Home) { inclusive = false }
                                    }
                                },
                                onNavigateHome = {
                                    navController.popBackStack(Route.Home, inclusive = false)
                                }
                            )
                        }
                        composable<Route.GameOver> { backStackEntry ->
                            val route = backStackEntry.toRoute<Route.GameOver>()
                            GameOverScreen(
                                streak = route.streak,
                                isNewRecord = route.isNewRecord,
                                previousBest = viewModel.highScore,
                                onPlayAgain = {
                                    viewModel.startGame()
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
