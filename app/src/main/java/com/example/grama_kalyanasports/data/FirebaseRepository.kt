package com.example.grama_kalyanasports.data

import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class FirebaseRepository {
    private val databaseUrl = "https://grama-kalyana-sports-app-default-rtdb.asia-southeast1.firebasedatabase.app/"
    
    // Use lazy initialization to prevent crashes during early app startup
    private val database: FirebaseDatabase by lazy { 
        FirebaseDatabase.getInstance(databaseUrl) 
    }
    
    private val matchesRef: DatabaseReference by lazy { database.getReference("matches") }
    private val playersRef: DatabaseReference by lazy { database.getReference("players") }

    fun createMatch(match: Match, onResult: (String?) -> Unit) {
        val id = matchesRef.push().key
        if (id != null) {
            val newMatch = match.copy(id = id)
            matchesRef.child(id).setValue(newMatch)
                .addOnSuccessListener { onResult(id) }
                .addOnFailureListener { onResult(null) }
        } else {
            onResult(null)
        }
    }

    fun updateMatchScore(
        matchId: String, 
        teamA: Int, 
        teamB: Int, 
        specificStats: Map<String, Int> = emptyMap()
    ) {
        val updates = mutableMapOf<String, Any>(
            "currentScoreA" to teamA,
            "currentScoreB" to teamB
        )
        if (specificStats.isNotEmpty()) {
            updates["sportSpecificStats"] = specificStats
        }
        matchesRef.child(matchId).updateChildren(updates)
    }
}
