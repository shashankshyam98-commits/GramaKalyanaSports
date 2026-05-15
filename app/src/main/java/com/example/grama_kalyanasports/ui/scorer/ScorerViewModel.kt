package com.example.grama_kalyanasports.ui.scorer

import androidx.lifecycle.ViewModel
import com.example.grama_kalyanasports.data.FirebaseRepository
import com.example.grama_kalyanasports.data.Match
import com.example.grama_kalyanasports.data.SportType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class ScorerViewModel : ViewModel() {
    private val repository = FirebaseRepository()
    
    private val _currentMatch = MutableStateFlow<Match?>(null)
    val currentMatch: StateFlow<Match?> = _currentMatch.asStateFlow()

    fun createMatch(teamA: String, teamB: String, sportType: SportType) {
        val newMatch = Match(
            teamA = teamA,
            teamB = teamB,
            sportType = sportType
        )
        repository.createMatch(newMatch) { id ->
            if (id != null) {
                _currentMatch.value = newMatch.copy(id = id)
            }
        }
    }

    fun updateScore(teamA: Int, teamB: Int, specificStats: Map<String, Int> = emptyMap()) {
        val match = _currentMatch.value ?: return
        repository.updateMatchScore(match.id, teamA, teamB, specificStats)
        _currentMatch.value = match.copy(
            currentScoreA = teamA,
            currentScoreB = teamB,
            sportSpecificStats = specificStats
        )
    }

    fun addPointA() {
        val match = _currentMatch.value ?: return
        updateScore(match.currentScoreA + 1, match.currentScoreB, match.sportSpecificStats)
    }

    fun addPointB() {
        val match = _currentMatch.value ?: return
        updateScore(match.currentScoreA, match.currentScoreB + 1, match.sportSpecificStats)
    }

    fun updateSpecificStat(key: String, value: Int) {
        val match = _currentMatch.value ?: return
        val newStats = match.sportSpecificStats.toMutableMap()
        newStats[key] = value
        updateScore(match.currentScoreA, match.currentScoreB, newStats)
    }
}
