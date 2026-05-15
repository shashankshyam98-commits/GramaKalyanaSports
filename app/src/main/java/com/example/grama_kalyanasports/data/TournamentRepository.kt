package com.example.grama_kalyanasports.data

import android.util.Log
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

object TournamentRepository {
    private const val TAG = "TournamentRepo"
    private const val databaseUrl = "https://grama-kalyana-sports-app-default-rtdb.asia-southeast1.firebasedatabase.app/"
    
    private val database by lazy { 
        FirebaseDatabase.getInstance(databaseUrl).apply {
            try {
                setPersistenceEnabled(true)
            } catch (e: Exception) {
                Log.d(TAG, "Persistence already enabled")
            }
        }
    }
    
    private val tournamentsRef by lazy { database.getReference("tournaments") }
    private val passwordsRef by lazy { database.getReference("tournament_passwords") }
    private val scoresRef by lazy { database.getReference("live_scores") }
    private val cricketScoresRef by lazy { database.getReference("cricket_scores") }
    private val kabaddiScoresRef by lazy { database.getReference("kabaddi_scores") }
    private val volleyballScoresRef by lazy { database.getReference("volleyball_scores") }

    private val _tournaments = MutableStateFlow<List<Tournament>>(emptyList())
    val tournaments: StateFlow<List<Tournament>> = _tournaments.asStateFlow()

    private val _liveScores = MutableStateFlow<Map<String, Pair<Int, Int>>>(emptyMap())
    val liveScores: StateFlow<Map<String, Pair<Int, Int>>> = _liveScores.asStateFlow()

    private val _cricketMatchStates = MutableStateFlow<Map<String, CricketMatchState>>(emptyMap())
    val cricketMatchStates: StateFlow<Map<String, CricketMatchState>> = _cricketMatchStates.asStateFlow()

    private val _kabaddiMatchStates = MutableStateFlow<Map<String, KabaddiMatchState>>(emptyMap())
    val kabaddiMatchStates: StateFlow<Map<String, KabaddiMatchState>> = _kabaddiMatchStates.asStateFlow()

    private val _volleyballMatchStates = MutableStateFlow<Map<String, VolleyballMatchState>>(emptyMap())
    val volleyballMatchStates: StateFlow<Map<String, VolleyballMatchState>> = _volleyballMatchStates.asStateFlow()

    private var isInitialized = false

    fun initialize() {
        if (isInitialized) return
        isInitialized = true

        // REMOVED clearAllTournaments() to persist data between runs.

        tournamentsRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val list = mutableListOf<Tournament>()
                for (child in snapshot.children) {
                    try {
                        val tournament = child.getValue(Tournament::class.java)
                        if (tournament != null) list.add(tournament)
                    } catch (e: Exception) { e.printStackTrace() }
                }
                _tournaments.value = list
            }
            override fun onCancelled(error: DatabaseError) {
                Log.e(TAG, "Tournaments listen cancelled: ${error.message}")
            }
        })

        scoresRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val scoresMap = mutableMapOf<String, Pair<Int, Int>>()
                for (child in snapshot.children) {
                    val scoreA = child.child("first").getValue(Int::class.java) ?: 0
                    val scoreB = child.child("second").getValue(Int::class.java) ?: 0
                    scoresMap[child.key ?: ""] = Pair(scoreA, scoreB)
                }
                _liveScores.value = scoresMap
            }
            override fun onCancelled(error: DatabaseError) {}
        })

        cricketScoresRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val cricketMap = mutableMapOf<String, CricketMatchState>()
                for (child in snapshot.children) {
                    try {
                        val state = parseCricketMatchState(child)
                        cricketMap[child.key ?: ""] = state
                    } catch (e: Exception) { e.printStackTrace() }
                }
                _cricketMatchStates.value = cricketMap
            }
            override fun onCancelled(error: DatabaseError) {}
        })

        kabaddiScoresRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val kabaddiMap = mutableMapOf<String, KabaddiMatchState>()
                for (child in snapshot.children) {
                    try {
                        val state = parseKabaddiMatchState(child)
                        kabaddiMap[child.key ?: ""] = state
                    } catch (e: Exception) { e.printStackTrace() }
                }
                _kabaddiMatchStates.value = kabaddiMap
            }
            override fun onCancelled(error: DatabaseError) {}
        })

        volleyballScoresRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val volleyballMap = mutableMapOf<String, VolleyballMatchState>()
                for (child in snapshot.children) {
                    try {
                        val state = parseVolleyballMatchState(child)
                        volleyballMap[child.key ?: ""] = state
                    } catch (e: Exception) { e.printStackTrace() }
                }
                _volleyballMatchStates.value = volleyballMap
            }
            override fun onCancelled(error: DatabaseError) {}
        })
    }

    private fun parseCricketMatchState(snapshot: DataSnapshot): CricketMatchState {
        val statusStr = snapshot.child("status").getValue(String::class.java) ?: "NONE"
        val status = try { MatchStatus.valueOf(statusStr.uppercase()) } catch (e: Exception) { MatchStatus.NONE }
        return CricketMatchState(
            status = status,
            totalOvers = snapshot.child("totalOvers").getValue()?.toString() ?: "",
            teamAName = snapshot.child("teamAName").getValue(String::class.java) ?: "",
            teamABattingSelected = snapshot.child("teamABattingSelected").getValue(Boolean::class.java) ?: false,
            teamABowlingSelected = snapshot.child("teamABowlingSelected").getValue(Boolean::class.java) ?: false,
            teamAScore = snapshot.child("teamAScore").getValue(Int::class.java) ?: 0,
            teamAWickets = snapshot.child("teamAWickets").getValue(Int::class.java) ?: 0,
            teamAOversElapsed = snapshot.child("teamAOversElapsed").getValue()?.toString() ?: "0.0",
            player1IdA = snapshot.child("player1IdA").getValue(String::class.java) ?: "",
            player2IdA = snapshot.child("player2IdA").getValue(String::class.java) ?: "",
            bowlerIdA = snapshot.child("bowlerIdA").getValue(String::class.java) ?: "",
            playersA = snapshot.child("playersA").children.mapNotNull { it.getValue(CricketPlayerStats::class.java) },
            manOfTheMatchA = snapshot.child("manOfTheMatchA").getValue(String::class.java) ?: "",
            teamBName = snapshot.child("teamBName").getValue(String::class.java) ?: "",
            teamBBattingSelected = snapshot.child("teamBBattingSelected").getValue(Boolean::class.java) ?: false,
            teamBBowlingSelected = snapshot.child("teamBBowlingSelected").getValue(Boolean::class.java) ?: false,
            teamBScore = snapshot.child("teamBScore").getValue(Int::class.java) ?: 0,
            teamBWickets = snapshot.child("teamBWickets").getValue(Int::class.java) ?: 0,
            teamBOversElapsed = snapshot.child("teamBOversElapsed").getValue()?.toString() ?: "0.0",
            player1IdB = snapshot.child("player1IdB").getValue(String::class.java) ?: "",
            player2IdB = snapshot.child("player2IdB").getValue(String::class.java) ?: "",
            bowlerIdB = snapshot.child("bowlerIdB").getValue(String::class.java) ?: "",
            playersB = snapshot.child("playersB").children.mapNotNull { it.getValue(CricketPlayerStats::class.java) },
            manOfTheMatchB = snapshot.child("manOfTheMatchB").getValue(String::class.java) ?: "",
            winningTeam = snapshot.child("winningTeam").getValue(String::class.java) ?: "",
            targetChase = snapshot.child("targetChase").getValue(String::class.java) ?: ""
        )
    }

    private fun parseKabaddiMatchState(snapshot: DataSnapshot): KabaddiMatchState {
        val statusStr = snapshot.child("status").getValue(String::class.java) ?: "NONE"
        val status = try { MatchStatus.valueOf(statusStr.uppercase()) } catch (e: Exception) { MatchStatus.NONE }
        return KabaddiMatchState(
            status = status,
            minutes = snapshot.child("minutes").getValue()?.toString() ?: "00",
            seconds = snapshot.child("seconds").getValue()?.toString() ?: "00",
            target = snapshot.child("target").getValue()?.toString() ?: "",
            teamAName = snapshot.child("teamAName").getValue(String::class.java) ?: "",
            teamARaidPoints = snapshot.child("teamARaidPoints").getValue(Int::class.java) ?: 0,
            teamATacklePoints = snapshot.child("teamATacklePoints").getValue(Int::class.java) ?: 0,
            teamAScore = snapshot.child("teamAScore").getValue(Int::class.java) ?: 0,
            playersA = snapshot.child("playersA").children.mapNotNull { it.getValue(KabaddiPlayerStats::class.java) },
            teamAManOfTheMatch = snapshot.child("teamAManOfTheMatch").getValue(String::class.java) ?: "",
            teamBName = snapshot.child("teamBName").getValue(String::class.java) ?: "",
            teamBRaidPoints = snapshot.child("teamBRaidPoints").getValue(Int::class.java) ?: 0,
            teamBTacklePoints = snapshot.child("teamBTacklePoints").getValue(Int::class.java) ?: 0,
            teamBScore = snapshot.child("teamBScore").getValue(Int::class.java) ?: 0,
            playersB = snapshot.child("playersB").children.mapNotNull { it.getValue(KabaddiPlayerStats::class.java) },
            teamBManOfTheMatch = snapshot.child("teamBManOfTheMatch").getValue(String::class.java) ?: ""
        )
    }

    private fun parseVolleyballMatchState(snapshot: DataSnapshot): VolleyballMatchState {
        val statusStr = snapshot.child("status").getValue(String::class.java) ?: "NONE"
        val status = try { MatchStatus.valueOf(statusStr.uppercase()) } catch (e: Exception) { MatchStatus.NONE }
        return VolleyballMatchState(
            status = status,
            minutes = snapshot.child("minutes").getValue()?.toString() ?: "00",
            seconds = snapshot.child("seconds").getValue()?.toString() ?: "00",
            winningTeam = snapshot.child("winningTeam").getValue(String::class.java) ?: "",
            teamAName = snapshot.child("teamAName").getValue(String::class.java) ?: "",
            teamAScore = snapshot.child("teamAScore").getValue(Int::class.java) ?: 0,
            playersA = snapshot.child("playersA").children.mapNotNull { it.getValue(VolleyballPlayerStats::class.java) },
            teamAManOfTheMatch = snapshot.child("teamAManOfTheMatch").getValue(String::class.java) ?: "",
            teamBName = snapshot.child("teamBName").getValue(String::class.java) ?: "",
            teamBScore = snapshot.child("teamBScore").getValue(Int::class.java) ?: 0,
            playersB = snapshot.child("playersB").children.mapNotNull { it.getValue(VolleyballPlayerStats::class.java) },
            teamBManOfTheMatch = snapshot.child("teamBManOfTheMatch").getValue(String::class.java) ?: ""
        )
    }

    fun registerTournament(tournament: Tournament, password: String, onResult: (Boolean) -> Unit) {
        val safeName = tournament.name.trim().replace(Regex("[.#$\\[\\]/]"), "_")
        val sanitizedTournament = tournament.copy(name = safeName)

        if (_tournaments.value.any { it.name.equals(safeName, ignoreCase = true) }) {
            onResult(false)
            return
        }

        val updates = hashMapOf<String, Any?>(
            "tournaments/$safeName" to sanitizedTournament,
            "tournament_passwords/$safeName" to password,
            "live_scores/$safeName" to mapOf("first" to 0, "second" to 0)
        )

        database.reference.updateChildren(updates).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Log.d(TAG, "Registration successful")
                onResult(true)
            } else {
                Log.e(TAG, "Registration failed", task.exception)
                onResult(false)
            }
        }
    }

    fun verifyTournamentLeader(tournamentName: String, email: String, password: String, onResult: (Boolean) -> Unit) {
        tournamentsRef.child(tournamentName).get().addOnSuccessListener { tSnapshot ->
            val tournament = tSnapshot.getValue(Tournament::class.java)
            if (tournament?.leaderEmail == email) {
                passwordsRef.child(tournamentName).get().addOnSuccessListener { pSnapshot ->
                    val storedPassword = pSnapshot.getValue(String::class.java)
                    onResult(storedPassword == password)
                }.addOnFailureListener { onResult(false) }
            } else { onResult(false) }
        }.addOnFailureListener { onResult(false) }
    }

    fun updateScore(tournamentName: String, teamAIncrement: Int, teamBIncrement: Int) {
        val current = _liveScores.value[tournamentName] ?: Pair(0, 0)
        val newScore = mapOf(
            "first" to (current.first + teamAIncrement).coerceAtLeast(0),
            "second" to (current.second + teamBIncrement).coerceAtLeast(0)
        )
        scoresRef.child(tournamentName).setValue(newScore)
    }

    fun updateCricketScore(tournamentName: String, state: CricketMatchState) {
        cricketScoresRef.child(tournamentName).setValue(state)
    }

    fun updateKabaddiScore(tournamentName: String, state: KabaddiMatchState) {
        kabaddiScoresRef.child(tournamentName).setValue(state)
    }

    fun updateVolleyballScore(tournamentName: String, state: VolleyballMatchState) {
        volleyballScoresRef.child(tournamentName).setValue(state)
    }

    fun getTournamentStatus(tournament: Tournament): MatchStatus {
        return when {
            tournament.sportType == SportType.CRICKET || tournament.type.equals("Cricket", ignoreCase = true) -> {
                _cricketMatchStates.value[tournament.name]?.status ?: MatchStatus.NONE
            }
            tournament.sportType == SportType.KABADDI || tournament.type.equals("Kabaddi", ignoreCase = true) -> {
                _kabaddiMatchStates.value[tournament.name]?.status ?: MatchStatus.NONE
            }
            tournament.sportType == SportType.VOLLEYBALL || tournament.type.equals("Volleyball", ignoreCase = true) -> {
                _volleyballMatchStates.value[tournament.name]?.status ?: MatchStatus.NONE
            }
            else -> MatchStatus.NONE
        }
    }

    fun deleteTournament(tournamentName: String, onComplete: (Boolean) -> Unit) {
        val updates = hashMapOf<String, Any?>(
            "tournaments/$tournamentName" to null,
            "tournament_passwords/$tournamentName" to null,
            "live_scores/$tournamentName" to null,
            "cricket_scores/$tournamentName" to null,
            "kabaddi_scores/$tournamentName" to null,
            "volleyball_scores/$tournamentName" to null
        )

        database.reference.updateChildren(updates).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Log.d(TAG, "Tournament deletion successful")
                onComplete(true)
            } else {
                Log.e(TAG, "Tournament deletion failed", task.exception)
                onComplete(false)
            }
        }
    }

    fun clearAllTournaments() {
        val updates = hashMapOf<String, Any?>(
            "tournaments" to null,
            "tournament_passwords" to null,
            "live_scores" to null,
            "cricket_scores" to null,
            "kabaddi_scores" to null,
            "volleyball_scores" to null
        )
        database.reference.updateChildren(updates).addOnSuccessListener {
            Log.d(TAG, "Database cleared successfully")
        }
    }
}
