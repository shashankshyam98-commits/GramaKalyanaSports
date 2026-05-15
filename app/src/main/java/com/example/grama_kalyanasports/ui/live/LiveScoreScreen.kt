package com.example.grama_kalyanasports.ui.live

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.platform.LocalContext
import android.app.Activity
import com.example.grama_kalyanasports.data.TournamentRepository
import com.example.grama_kalyanasports.data.SportType
import com.example.grama_kalyanasports.data.MatchStatus
import com.example.grama_kalyanasports.data.captureAndSaveScreenshot

@Composable
fun LiveScoreScreen(
    tournamentName: String,
    isAdmin: Boolean,
    onBack: () -> Unit
) {
    val tournaments by TournamentRepository.tournaments.collectAsState()
    val cricketStates by TournamentRepository.cricketMatchStates.collectAsState()
    val kabaddiStates by TournamentRepository.kabaddiMatchStates.collectAsState()
    val volleyballStates by TournamentRepository.volleyballMatchStates.collectAsState()
    
    // Find tournament case-insensitively
    val tournament = tournaments.find { it.name.equals(tournamentName, ignoreCase = true) }
    
    // Check if it's a cricket match
    val hasCricketData = cricketStates.keys.any { it.equals(tournamentName, ignoreCase = true) }
    val isCricket = hasCricketData || 
                    tournament?.sportType == SportType.CRICKET || 
                    tournament?.type?.equals("Cricket", ignoreCase = true) == true

    // Check if it's a kabaddi match
    val hasKabaddiData = kabaddiStates.keys.any { it.equals(tournamentName, ignoreCase = true) }
    val isKabaddi = hasKabaddiData || 
                    tournament?.sportType == SportType.KABADDI || 
                    tournament?.type?.equals("Kabaddi", ignoreCase = true) == true

    // Check if it's a volleyball match
    val hasVolleyballData = volleyballStates.keys.any { it.equals(tournamentName, ignoreCase = true) }
    val isVolleyball = hasVolleyballData || 
                    tournament?.sportType == SportType.VOLLEYBALL || 
                    tournament?.type?.equals("Volleyball", ignoreCase = true) == true

    if (isCricket) {
        val actualCricketKey = cricketStates.keys.find { it.equals(tournamentName, ignoreCase = true) } ?: tournamentName
        CricketScorecardScreen(
            tournamentName = actualCricketKey,
            isAdmin = isAdmin,
            onBack = onBack
        )
    } else if (isKabaddi) {
        val actualKabaddiKey = kabaddiStates.keys.find { it.equals(tournamentName, ignoreCase = true) } ?: tournamentName
        KabaddiScorecardScreen(
            tournamentName = actualKabaddiKey,
            isAdmin = isAdmin,
            onBack = onBack
        )
    } else if (isVolleyball) {
        val actualVolleyballKey = volleyballStates.keys.find { it.equals(tournamentName, ignoreCase = true) } ?: tournamentName
        VolleyballScorecardScreen(
            tournamentName = actualVolleyballKey,
            isAdmin = isAdmin,
            onBack = onBack
        )
    } else {
        // Default UI for other sports
        val liveScores by TournamentRepository.liveScores.collectAsState()
        
        // Find the correct key for live scores case-insensitively
        val actualScoreKey = liveScores.keys.find { it.equals(tournamentName, ignoreCase = true) } ?: tournamentName
        val scores = liveScores[actualScoreKey] ?: Pair(0, 0)
        
        val teamAScore = scores.first
        val teamBScore = scores.second
        
        val status = tournament?.let { TournamentRepository.getTournamentStatus(it) } ?: MatchStatus.NONE
        val isEditable = isAdmin && status != MatchStatus.FINISHED && status != MatchStatus.CANCELED
        
        val context = LocalContext.current

        Column(
            modifier = Modifier.fillMaxSize().padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text("Live Score", fontSize = 28.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
            Text("Tournament: ${tournament?.name ?: tournamentName}", fontSize = 18.sp, color = MaterialTheme.colorScheme.primary)
            
            Spacer(modifier = Modifier.height(48.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Team A", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    Text(teamAScore.toString(), fontSize = 64.sp)
                    
                    if (isEditable) {
                        Row {
                            IconButton(onClick = { TournamentRepository.updateScore(actualScoreKey, 1, 0) }) {
                                Text("+", fontSize = 24.sp)
                            }
                            IconButton(onClick = { TournamentRepository.updateScore(actualScoreKey, -1, 0) }) {
                                Text("-", fontSize = 24.sp)
                            }
                        }
                    }
                }
                
                Text("vs", fontSize = 24.sp, modifier = Modifier.align(Alignment.CenterVertically))
                
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Team B", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    Text(teamBScore.toString(), fontSize = 64.sp)
                    
                    if (isEditable) {
                        Row {
                            IconButton(onClick = { TournamentRepository.updateScore(actualScoreKey, 0, 1) }) {
                                Text("+", fontSize = 24.sp)
                            }
                            IconButton(onClick = { TournamentRepository.updateScore(actualScoreKey, 0, -1) }) {
                                Text("-", fontSize = 24.sp)
                            }
                        }
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(64.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                Button(onClick = onBack) {
                    Text("Back")
                }
                Button(onClick = { captureAndSaveScreenshot(context as Activity) }) {
                    Text("Download Score")
                }
            }
        }
    }
}
