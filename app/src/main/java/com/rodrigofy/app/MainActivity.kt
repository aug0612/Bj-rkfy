package com.rodrigofy.app

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.rodrigofy.app.auth.AuthManager
import com.rodrigofy.app.auth.AuthState
import com.rodrigofy.app.data.SpotifyApiService
import com.rodrigofy.app.navigation.RodrigofyDestinations
import com.rodrigofy.app.navigation.RodrigofyNavGraph
import com.rodrigofy.app.ui.components.ExpressivePlayerBar
import com.rodrigofy.app.ui.theme.RodrigofyTheme
import com.rodrigofy.app.viewmodel.PlayerViewModel
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    private lateinit var authManager: AuthManager
    private val apiService = SpotifyApiService()
    private val playerViewModel: PlayerViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        authManager = AuthManager(applicationContext)

        setContent {
            RodrigofyTheme(dynamicColor = false) {
                RodrigofyApp(
                    authManager = authManager,
                    apiService = apiService,
                    playerViewModel = playerViewModel
                )
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        // Fires when the browser redirects back to spotifyclient://callback
        if (intent.data?.toString()?.startsWith(BuildConfig.SPOTIFY_REDIRECT_URI) == true) {
            lifecycleScope.launch { authManager.handleRedirect(intent) }
        }
    }
}

@Composable
private fun RodrigofyApp(
    authManager: AuthManager,
    apiService: SpotifyApiService,
    playerViewModel: PlayerViewModel
) {
    val authState by authManager.authState.collectAsState()
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route
    val context = LocalContext.current

    Scaffold { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            RodrigofyNavGraph(
                playerViewModel = playerViewModel,
                apiService = apiService,
                isLoggedIn = authState is AuthState.LoggedIn,
                onLoginClick = { context.startActivity(authManager.buildLoginIntent()) },
                navController = navController
            )

            val playbackState by playerViewModel.playbackState.collectAsState()
            if (currentRoute == RodrigofyDestinations.HOME && playbackState.track != null) {
                ExpressivePlayerBar(
                    playbackState = playbackState,
                    onTogglePlayPause = { playerViewModel.togglePlayPause() },
                    onSkipNext = { /* skip to next track — wire up to your queue */ },
                    onExpand = { navController.navigate(RodrigofyDestinations.PLAYER) },
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 24.dp)
                )
            }
        }
    }
}
