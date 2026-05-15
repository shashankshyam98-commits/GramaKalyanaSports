package com.example.grama_kalyanasports.data

import android.app.Activity
import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.os.Environment
import android.provider.MediaStore
import android.view.PixelCopy
import android.widget.Toast
import android.os.Handler
import android.os.Looper
import java.io.File
import java.io.FileOutputStream

data class Tournament(
    val id: String = "",
    val name: String = "",
    val type: String = "",
    val date: String = "",
    val time: String = "",
    val leaderUsername: String = "",
    val leaderEmail: String = "",
    val sportTypeName: String = SportType.KABADDI.name
) {
    val sportType: SportType
        get() = try { SportType.valueOf(sportTypeName) } catch (e: Exception) { SportType.KABADDI }
}

enum class SportType {
    KABADDI, VOLLEYBALL, CRICKET
}

enum class MatchStatus {
    LIVE, BREAK, CANCELED, FINISHED, NONE
}

data class Match(
    val id: String = "",
    val teamA: String = "",
    val teamB: String = "",
    val currentScoreA: Int = 0,
    val currentScoreB: Int = 0,
    val sportType: SportType = SportType.KABADDI,
    val sportSpecificStats: Map<String, Int> = emptyMap(),
    val status: MatchStatus = MatchStatus.LIVE
)

enum class PlayerStatus {
    ACTIVE, OUT, NOT_OUT, BENCH
}

data class CricketPlayerStats(
    val id: String = "",
    val name: String = "",
    val extras: Int = 0,
    val fours: Int = 0,
    val sixes: Int = 0,
    val totalRuns: Int = 0,
    val wickets: Int = 0,
    val balls: Int = 0,
    val status: PlayerStatus = PlayerStatus.BENCH
)

data class CricketMatchState(
    val status: MatchStatus = MatchStatus.NONE,
    val totalOvers: String = "",
    
    // Team A
    val teamAName: String = "",
    val teamABattingSelected: Boolean = false,
    val teamABowlingSelected: Boolean = false,
    val teamAScore: Int = 0,
    val teamAWickets: Int = 0,
    val teamAOversElapsed: String = "0.0",
    val player1IdA: String = "",
    val player2IdA: String = "",
    val bowlerIdA: String = "",
    val playersA: List<CricketPlayerStats> = emptyList(),
    val manOfTheMatchA: String = "",
    
    // Team B
    val teamBName: String = "",
    val teamBBattingSelected: Boolean = false,
    val teamBBowlingSelected: Boolean = false,
    val teamBScore: Int = 0,
    val teamBWickets: Int = 0,
    val teamBOversElapsed: String = "0.0",
    val player1IdB: String = "",
    val player2IdB: String = "",
    val bowlerIdB: String = "",
    val playersB: List<CricketPlayerStats> = emptyList(),
    val manOfTheMatchB: String = "",

    val winningTeam: String = "",
    val targetChase: String = ""
)

data class KabaddiPlayerStats(
    val id: String = "",
    val name: String = "",
    val points: Int = 0
)

data class KabaddiMatchState(
    val status: MatchStatus = MatchStatus.NONE,
    val minutes: String = "00",
    val seconds: String = "00",
    val target: String = "",
    
    // Team A
    val teamAName: String = "",
    val teamARaidPoints: Int = 0,
    val teamATacklePoints: Int = 0,
    val teamAScore: Int = 0,
    val playersA: List<KabaddiPlayerStats> = emptyList(),
    val teamAManOfTheMatch: String = "",
    
    // Team B
    val teamBName: String = "",
    val teamBRaidPoints: Int = 0,
    val teamBTacklePoints: Int = 0,
    val teamBScore: Int = 0,
    val playersB: List<KabaddiPlayerStats> = emptyList(),
    val teamBManOfTheMatch: String = ""
)

data class VolleyballPlayerStats(
    val id: String = "",
    val name: String = "",
    val points: Int = 0
)

data class VolleyballMatchState(
    val status: MatchStatus = MatchStatus.NONE,
    val minutes: String = "00",
    val seconds: String = "00",
    val winningTeam: String = "",
    
    // Team A
    val teamAName: String = "",
    val teamAScore: Int = 0,
    val playersA: List<VolleyballPlayerStats> = emptyList(),
    val teamAManOfTheMatch: String = "",
    
    // Team B
    val teamBName: String = "",
    val teamBScore: Int = 0,
    val playersB: List<VolleyballPlayerStats> = emptyList(),
    val teamBManOfTheMatch: String = ""
)

fun captureAndSaveScreenshot(activity: Activity) {
    try {
        val window = activity.window
        val view = window.decorView
        if (view.width == 0 || view.height == 0) return
        val bitmap = Bitmap.createBitmap(view.width, view.height, Bitmap.Config.ARGB_8888)
        
        PixelCopy.request(window, bitmap, { copyResult ->
            if (copyResult == PixelCopy.SUCCESS) {
                try {
                    val contentValues = ContentValues().apply {
                        put(MediaStore.Images.Media.DISPLAY_NAME, "Scorecard_${System.currentTimeMillis()}.png")
                        put(MediaStore.Images.Media.MIME_TYPE, "image/png")
                        put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + "/GramaKalyanaSports")
                    }
                    
                    val uri = activity.contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
                    if (uri != null) {
                        val outputStream = activity.contentResolver.openOutputStream(uri)
                        outputStream?.use {
                            bitmap.compress(Bitmap.CompressFormat.PNG, 100, it)
                        }
                        activity.runOnUiThread {
                            Toast.makeText(activity, "Saved Screenshot to Gallery", Toast.LENGTH_SHORT).show()
                        }
                    }
                } catch (e: Exception) {
                    activity.runOnUiThread {
                        Toast.makeText(activity, "Error saving screenshot: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                activity.runOnUiThread {
                    Toast.makeText(activity, "Screenshot failed", Toast.LENGTH_SHORT).show()
                }
            }
        }, Handler(Looper.getMainLooper()))
    } catch (e: Exception) {
        activity.runOnUiThread {
            Toast.makeText(activity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }
}

fun downloadScoreDataAsFile(context: Context, fileName: String, data: String) {
    try {
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, "$fileName.txt")
            put(MediaStore.MediaColumns.MIME_TYPE, "text/plain")
            put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOCUMENTS + "/GramaKalyanaSports")
        }

        val resolver = context.contentResolver
        val uri = resolver.insert(MediaStore.Files.getContentUri("external"), contentValues)

        if (uri != null) {
            resolver.openOutputStream(uri)?.use { outputStream ->
                outputStream.write(data.toByteArray())
            }
            Toast.makeText(context, "Data file saved to Documents/GramaKalyanaSports", Toast.LENGTH_LONG).show()
        } else {
            Toast.makeText(context, "Failed to create data file", Toast.LENGTH_SHORT).show()
        }
    } catch (e: Exception) {
        Toast.makeText(context, "Error saving data file: ${e.message}", Toast.LENGTH_SHORT).show()
    }
}
