package com.example.grama_kalyanasports.ui.navigation

sealed class Screen {
    object Login : Screen()
    data class Home(val username: String = "", val email: String = "") : Screen()
    data class RegisterTournament(val username: String = "", val email: String = "") : Screen()
    object LeaderLogin : Screen()
    object Profile : Screen()
    data class AdminDashboard(val tournamentName: String) : Screen()
    data class LiveScore(val tournamentName: String, val isAdmin: Boolean) : Screen()
    data class GameSelection(val tournamentName: String) : Screen()
    data class CricketScorecard(val tournamentName: String, val isAdmin: Boolean) : Screen()
    data class KabaddiScorecard(val tournamentName: String, val isAdmin: Boolean) : Screen()
    data class VolleyballScorecard(val tournamentName: String, val isAdmin: Boolean) : Screen()
}
