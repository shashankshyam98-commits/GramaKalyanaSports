package com.example.grama_kalyanasports.ui.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.grama_kalyanasports.data.TournamentRepository
import com.example.grama_kalyanasports.data.MatchStatus

@Composable
fun AdminDashboardScreen(
    loggedInTournamentName: String,
    onTournamentClick: (String) -> Unit,
    onBack: () -> Unit
) {
    val tournaments by TournamentRepository.tournaments.collectAsState()
    val filteredTournaments = tournaments.filter { it.name == loggedInTournamentName }
    val tourney = filteredTournaments.firstOrNull()
    
    val status = tourney?.let { TournamentRepository.getTournamentStatus(it) } ?: MatchStatus.NONE
    val isFinishedOrCanceled = status == MatchStatus.FINISHED || status == MatchStatus.CANCELED
    
    var showDeleteDialog by remember { mutableStateOf(false) }

    if (showDeleteDialog && tourney != null) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Delete Tournament") },
            text = { Text("Are you sure you want to delete ${tourney.name}? This action cannot be undone.") },
            confirmButton = {
                Button(
                    onClick = {
                        TournamentRepository.deleteTournament(tourney.name) { success ->
                            if (success) {
                                showDeleteDialog = false
                                onBack() // Go back home
                            }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                ) {
                    Text("Delete", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF121212)), // Dark background
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                "Admin Dashboard", 
                fontSize = 28.sp, 
                fontWeight = FontWeight.Black,
                color = Color.White
            )
            
            Spacer(modifier = Modifier.height(32.dp))
            
            if (filteredTournaments.isEmpty()) {
                Text("No tournaments found.", color = Color.Gray)
            } else {
                val t = filteredTournaments.first()
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .clickable { onTournamentClick(t.name) },
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFF9800)), // Orange
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                    shape = RoundedCornerShape(24.dp)
                ) {
                    Column(
                        modifier = Modifier.fillMaxSize().padding(24.dp),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            t.name, 
                            fontSize = 24.sp, 
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.Black // Black text
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Type: ${t.type}", color = Color.Black.copy(alpha = 0.8f))
                        Text("Date: ${t.date} - ${t.time}", color = Color.Black.copy(alpha = 0.8f))
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("TAP TO MANAGE", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(40.dp))
            
            Button(
                onClick = onBack,
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Black) // Black button
            ) {
                Text("BACK TO PREVIOUS PAGE", fontWeight = FontWeight.Bold, color = Color.White) // White text
            }
        }
        
        Box(modifier = Modifier.fillMaxSize().padding(16.dp), contentAlignment = Alignment.BottomStart) {
            Button(
                onClick = { showDeleteDialog = true },
                enabled = !isFinishedOrCanceled,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Red,
                    disabledContainerColor = Color.Gray
                )
            ) {
                Text("Delete", color = Color.White, fontWeight = FontWeight.Bold)
            }
        }
    }
}
