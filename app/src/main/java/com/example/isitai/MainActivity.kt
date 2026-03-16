package com.example.isitai

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.example.isitai.ui.navigation.BottomNavBar
import com.example.isitai.ui.navigation.Route
import com.example.isitai.ui.screen.AllCompleteScreen
import com.example.isitai.ui.screen.GameOverScreen
import com.example.isitai.ui.screen.GameScreen
import com.example.isitai.ui.screen.HomeScreen
import com.example.isitai.ui.screen.PacksScreen
import com.example.isitai.ui.screen.SettingsScreen
import com.example.isitai.ui.theme.IsItAITheme
import com.example.isitai.viewmodel.GameViewModel
import com.example.isitai.viewmodel.PackViewModel
import com.example.isitai.viewmodel.SettingsViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val settingsViewModel: SettingsViewModel = viewModel(factory = SettingsViewModel.Factory)

            IsItAITheme(darkTheme = settingsViewModel.isDarkMode) {
                val navController = rememberNavController()
                val gameViewModel: GameViewModel = viewModel(factory = GameViewModel.Factory)

                val backStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = backStackEntry?.destination?.route ?: ""
                val showBottomBar = listOf(Route.Home, Route.Packs, Route.Settings).any { route ->
                    currentRoute.contains(route::class.qualifiedName ?: "")
                }

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    bottomBar = {
                        if (showBottomBar) {
                            BottomNavBar(navController)
                        }
                    }
                ) { innerPadding ->
                    NavHost(
                        navController = navController,
                        startDestination = Route.Home
                    ) {
                        composable<Route.Home> {
                            HomeScreen(
                                highScore = gameViewModel.highScore,
                                onPlayClick = {
                                    gameViewModel.startGame()
                                    navController.navigate(Route.Game)
                                },
                                modifier = Modifier.padding(innerPadding)
                            )
                        }
                        composable<Route.Game> {
                            GameScreen(
                                viewModel = gameViewModel,
                                isDarkMode = settingsViewModel.isDarkMode,
                                onToggleDarkMode = { settingsViewModel.toggleDarkMode() },
                                onContinueToGameOver = { streak, isNewRecord ->
                                    navController.navigate(Route.GameOver(streak, isNewRecord)) {
                                        popUpTo(Route.Home) { inclusive = false }
                                    }
                                },
                                onAllComplete = { streak, isNewRecord ->
                                    navController.navigate(Route.AllComplete(streak, isNewRecord)) {
                                        popUpTo(Route.Home) { inclusive = false }
                                    }
                                },
                                onNavigateHome = {
                                    navController.popBackStack(Route.Home, inclusive = false)
                                },
                                modifier = Modifier.padding(innerPadding)
                            )
                        }
                        composable<Route.GameOver> { backStackEntry ->
                            val route = backStackEntry.toRoute<Route.GameOver>()
                            GameOverScreen(
                                streak = route.streak,
                                isNewRecord = route.isNewRecord,
                                previousBest = gameViewModel.highScore,
                                onPlayAgain = {
                                    gameViewModel.startGame()
                                    navController.navigate(Route.Game) {
                                        popUpTo(Route.Home) { inclusive = false }
                                    }
                                },
                                onHome = {
                                    navController.popBackStack(Route.Home, inclusive = false)
                                },
                                modifier = Modifier.padding(innerPadding)
                            )
                        }
                        composable<Route.AllComplete> { backStackEntry ->
                            val route = backStackEntry.toRoute<Route.AllComplete>()
                            AllCompleteScreen(
                                streak = route.streak,
                                isNewRecord = route.isNewRecord,
                                previousBest = gameViewModel.highScore,
                                onPlayAgain = {
                                    gameViewModel.startGame()
                                    navController.navigate(Route.Game) {
                                        popUpTo(Route.Home) { inclusive = false }
                                    }
                                },
                                onHome = {
                                    navController.popBackStack(Route.Home, inclusive = false)
                                },
                                modifier = Modifier.padding(innerPadding)
                            )
                        }
                        composable<Route.Packs> {
                            val packViewModel: PackViewModel = viewModel(
                                factory = PackViewModel.Factory
                            )
                            PacksScreen(
                                viewModel = packViewModel,
                                modifier = Modifier.padding(innerPadding)
                            )
                        }
                        composable<Route.Settings> {
                            SettingsScreen(
                                viewModel = settingsViewModel,
                                modifier = Modifier.padding(innerPadding)
                            )
                        }
                    }
                }
            }
        }
    }
}
