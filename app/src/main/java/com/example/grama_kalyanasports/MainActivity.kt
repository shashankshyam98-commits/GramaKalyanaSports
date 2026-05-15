package com.example.grama_kalyanasports

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.edit
import com.example.grama_kalyanasports.ui.theme.GramaKalyanaSportsTheme
import com.example.grama_kalyanasports.ui.navigation.Screen
import com.example.grama_kalyanasports.ui.home.AppHome
import com.example.grama_kalyanasports.ui.login.LoginScreen
import com.example.grama_kalyanasports.ui.admin.*
import com.example.grama_kalyanasports.ui.live.*
import com.example.grama_kalyanasports.ui.profile.ProfileScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            GramaKalyanaSportsTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MainAppNavigation()
                }
            }
        }
    }
}

@Composable
fun MainAppNavigation() {
    val context = LocalContext.current
    val sharedPrefs = remember { context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE) }
    
    var savedUsername by remember { mutableStateOf(sharedPrefs.getString("username", null)) }
    var savedEmail by remember { mutableStateOf(sharedPrefs.getString("email", null)) }

    var currentScreen by remember { 
        mutableStateOf<Screen>(
            if (savedUsername != null && savedEmail != null) {
                Screen.Home(savedUsername!!, savedEmail!!)
            } else {
                Screen.Login
            }
        )
    }

    when (val screen = currentScreen) {
        is Screen.Login -> LoginScreen(onLoginSuccess = { username, email -> 
            sharedPrefs.edit {
                putString("username", username)
                putString("email", email)
            }
            savedUsername = username
            savedEmail = email
            currentScreen = Screen.Home(username, email) 
        })
        
        is Screen.Home -> AppHome(
            username = screen.username,
            email = screen.email,
            onNavigate = { nextScreen -> 
                if (nextScreen is Screen.RegisterTournament) {
                    currentScreen = nextScreen.copy(username = screen.username, email = screen.email)
                } else {
                    currentScreen = nextScreen
                }
            }
        )

        is Screen.RegisterTournament -> RegisterTournamentScreen(
            initialUsername = screen.username,
            initialEmail = screen.email,
            onRegistrationSuccess = { _ ->
                currentScreen = Screen.Home(screen.username, screen.email)
            },
            onBack = {
                currentScreen = Screen.Home(screen.username, screen.email)
            }
        )

        is Screen.LeaderLogin -> LeaderLoginScreen(
            onLoginSuccess = { tournamentName ->
                currentScreen = Screen.AdminDashboard(tournamentName)
            },
            onBack = {
                currentScreen = Screen.Home(savedUsername ?: "", savedEmail ?: "")
            }
        )

        is Screen.AdminDashboard -> AdminDashboardScreen(
            loggedInTournamentName = screen.tournamentName,
            onTournamentClick = { name ->
                currentScreen = Screen.GameSelection(name)
            },
            onBack = { currentScreen = Screen.Home(savedUsername ?: "", savedEmail ?: "") }
        )

        is Screen.GameSelection -> GameSelectionScreen(
            tournamentName = screen.tournamentName,
            onGameSelected = { sport ->
                if (sport == "Cricket") {
                    currentScreen = Screen.CricketScorecard(screen.tournamentName, isAdmin = true)
                } else if (sport == "Kabaddi") {
                    currentScreen = Screen.KabaddiScorecard(screen.tournamentName, isAdmin = true)
                } else if (sport == "Volleyball") {
                    currentScreen = Screen.VolleyballScorecard(screen.tournamentName, isAdmin = true)
                } else {
                    currentScreen = Screen.LiveScore(screen.tournamentName, isAdmin = true)
                }
            },
            onBack = { currentScreen = Screen.Home(savedUsername ?: "", savedEmail ?: "") }
        )

        is Screen.CricketScorecard -> CricketScorecardScreen(
            tournamentName = screen.tournamentName,
            isAdmin = screen.isAdmin,
            onBack = { currentScreen = Screen.Home(savedUsername ?: "", savedEmail ?: "") }
        )

        is Screen.KabaddiScorecard -> KabaddiScorecardScreen(
            tournamentName = screen.tournamentName,
            isAdmin = screen.isAdmin,
            onBack = { currentScreen = Screen.Home(savedUsername ?: "", savedEmail ?: "") }
        )

        is Screen.VolleyballScorecard -> VolleyballScorecardScreen(
            tournamentName = screen.tournamentName,
            isAdmin = screen.isAdmin,
            onBack = { currentScreen = Screen.Home(savedUsername ?: "", savedEmail ?: "") }
        )

        is Screen.LiveScore -> LiveScoreScreen(
            tournamentName = screen.tournamentName,
            isAdmin = screen.isAdmin,
            onBack = { currentScreen = Screen.Home(savedUsername ?: "", savedEmail ?: "") }
        )

        is Screen.Profile -> ProfileScreen(
            onLogout = { 
                sharedPrefs.edit { clear() }
                savedUsername = null
                savedEmail = null
                currentScreen = Screen.Login 
            },
            onBack = { currentScreen = Screen.Home(savedUsername ?: "", savedEmail ?: "") }
        )
    }
}
