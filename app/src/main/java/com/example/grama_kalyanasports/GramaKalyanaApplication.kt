package com.example.grama_kalyanasports

import android.app.Application
import android.util.Log
import com.example.grama_kalyanasports.data.TournamentRepository
import com.google.firebase.FirebaseApp
import com.google.firebase.database.FirebaseDatabase

class GramaKalyanaApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        try {
            // Initialize Firebase only if not already initialized
            if (FirebaseApp.getApps(this).isEmpty()) {
                FirebaseApp.initializeApp(this)
                // Enable offline persistence for faster data retrieval
                FirebaseDatabase.getInstance().setPersistenceEnabled(true)
            }
            // Initialize repository listeners
            TournamentRepository.initialize()
        } catch (e: Exception) {
            Log.e("GramaKalyana", "App startup error: ${e.message}")
        }
    }
}
