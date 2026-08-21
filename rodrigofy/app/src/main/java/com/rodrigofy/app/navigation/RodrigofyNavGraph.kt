package com.rodrigofy.app.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.rodrigofy.app.data.SpotifyApiService
import com.rodrigofy.app.data.SpotifyTrack
import com.rodrigofy.app.ui.screens.HomeScreen
import com.rodrigofy.app.ui.screens.LoginScreen
import com.rodrigofy.app.ui.screens.PlayerScreen
import com.rodrigofy.app.viewmodel.PlayerViewModel

object RodrigofyDestinations {
    const val LOGIN = "login"
    const val HOME = "home"
    const val PLAYER = "player"
}

@Composable
fun RodrigofyNavGraph(
    playerViewModel: PlayerViewModel,
    apiService: SpotifyApiService,
    isLoggedIn: Boolean,
    onLoginClick: () -> Unit,
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        navController = navController,
        startDestination = if (isLoggedIn) RodrigofyDestinations.HOME else RodrigofyDestinations.LOGIN
    ) {
        composable(RodrigofyDestinations.LOGIN) {
            LoginScreen(onLoginClick = onLoginClick)
        }
        composable(RodrigofyDestinations.HOME) {
            HomeScreen(
                apiService = apiService,
                playerViewModel = playerViewModel,
                onTrackSelected = { track: SpotifyTrack ->
                    playerViewModel.play(track)
                    navController.navigate(RodrigofyDestinations.PLAYER)
                }
            )
        }
        composable(RodrigofyDestinations.PLAYER) {
            PlayerScreen(
                playerViewModel = playerViewModel,
                onBack = { navController.popBackStack() }
            )
        }
    }
}
