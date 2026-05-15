package com.example.grama_kalyanasports.ui.fan

import androidx.lifecycle.ViewModel
import com.example.grama_kalyanasports.data.Match
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class FanViewModel : ViewModel() {
    private val database = FirebaseDatabase.getInstance()
    private val matchesRef = database.getReference("matches")

    private val _liveMatches = MutableStateFlow<List<Match>>(emptyList())
    val liveMatches: StateFlow<List<Match>> = _liveMatches.asStateFlow()

    init {
        matchesRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val matches = mutableListOf<Match>()
                for (child in snapshot.children) {
                    try {
                        val match = child.getValue(Match::class.java)
                        if (match != null) {
                            matches.add(match)
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
                _liveMatches.value = matches.reversed() // Put newest first
            }

            override fun onCancelled(error: DatabaseError) {
                // handle error
            }
        })
    }
}
