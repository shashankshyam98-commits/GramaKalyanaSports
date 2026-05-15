package com.example.grama_kalyanasports.ui.live

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.grama_kalyanasports.data.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CricketViewerScreen(
    tournamentName: String,
    onBack: () -> Unit
) {
    val cricketStates by TournamentRepository.cricketMatchStates.collectAsState(initial = emptyMap())
    val matchState = cricketStates[tournamentName] ?: CricketMatchState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF121212)) 
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Card(
            modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFD81B60)),
            elevation = CardDefaults.cardElevation(4.dp)
        ) {
            Text(
                "CRICKET SCORE",
                fontSize = 32.sp,
                fontWeight = FontWeight.Black,
                textAlign = TextAlign.Center,
                color = Color.White,
                modifier = Modifier.fillMaxWidth().padding(16.dp)
            )
        }

        Card(
            modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF4527A0)),
            elevation = CardDefaults.cardElevation(2.dp)
        ) {
            Text(
                tournamentName,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth().padding(12.dp)
            )
        }

        // Status Buttons (Read-only for viewer)
        Row(
            modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            listOf(MatchStatus.LIVE, MatchStatus.BREAK, MatchStatus.FINISHED, MatchStatus.CANCELED).forEach { status ->
                val isSelected = matchState.status == status
                val containerColor = if (isSelected) {
                    when (status) {
                        MatchStatus.LIVE -> Color.Green
                        MatchStatus.BREAK -> Color.Blue
                        MatchStatus.FINISHED -> Color.Red
                        MatchStatus.CANCELED -> Color.Black
                        else -> Color.Gray
                    }
                } else Color.DarkGray

                Button(
                    onClick = { },
                    modifier = Modifier.weight(1f).height(48.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = containerColor, 
                        contentColor = Color.White,
                        disabledContainerColor = containerColor,
                        disabledContentColor = Color.White
                    ),
                    enabled = false,
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Text(status.name, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        // Team Displays
        TeamViewerDisplay(
            label = "TEAM A",
            name = matchState.teamAName,
            battingSelected = matchState.teamABattingSelected,
            bowlingSelected = matchState.teamABowlingSelected,
            score = matchState.teamAScore,
            oversWickets = "${matchState.teamAOversElapsed} / ${matchState.teamAWickets}",
            p1 = matchState.playersA.find { it.id == matchState.player1IdA },
            p2 = matchState.playersA.find { it.id == matchState.player2IdA },
            players = matchState.playersA,
            mom = matchState.manOfTheMatchA,
            containerColor = Color(0xFFAD1457)
        )

        Spacer(modifier = Modifier.height(24.dp))

        TeamViewerDisplay(
            label = "TEAM B",
            name = matchState.teamBName,
            battingSelected = matchState.teamBBattingSelected,
            bowlingSelected = matchState.teamBBowlingSelected,
            score = matchState.teamBScore,
            oversWickets = "${matchState.teamBOversElapsed} / ${matchState.teamBWickets}",
            p1 = matchState.playersB.find { it.id == matchState.player1IdB },
            p2 = matchState.playersB.find { it.id == matchState.player2IdB },
            players = matchState.playersB,
            mom = matchState.manOfTheMatchB,
            containerColor = Color(0xFFE65100)
        )

        Spacer(modifier = Modifier.height(32.dp))
        Button(onClick = onBack, colors = ButtonDefaults.buttonColors(containerColor = Color.White, contentColor = Color.Black)) { 
            Text("Back to Home", fontWeight = FontWeight.Bold) 
        }
        Spacer(modifier = Modifier.height(64.dp))
    }
}

@Composable
fun TeamViewerDisplay(
    label: String,
    name: String,
    battingSelected: Boolean,
    bowlingSelected: Boolean,
    score: Int,
    oversWickets: String,
    p1: CricketPlayerStats?,
    p2: CricketPlayerStats?,
    players: List<CricketPlayerStats>,
    mom: String,
    containerColor: Color
) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
        colors = CardDefaults.cardColors(containerColor = containerColor),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("$label NAME", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
            Text(text = name.ifEmpty { "TBD" }, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.White, modifier = Modifier.padding(vertical = 4.dp))
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Row(modifier = Modifier.fillMaxWidth()) {
                Surface(modifier = Modifier.weight(1f).height(40.dp), color = if (battingSelected) Color.Green else Color.DarkGray, shape = RoundedCornerShape(8.dp)) {
                    Box(contentAlignment = Alignment.Center) { Text("BATTING", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp) }
                }
                Spacer(modifier = Modifier.width(8.dp))
                Surface(modifier = Modifier.weight(1f).height(40.dp), color = if (bowlingSelected) Color.Blue else Color.DarkGray, shape = RoundedCornerShape(8.dp)) {
                    Box(contentAlignment = Alignment.Center) { Text("BOWLING", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp) }
                }
            }

            if (battingSelected || bowlingSelected) {
                Spacer(modifier = Modifier.height(16.dp))
                Row(modifier = Modifier.fillMaxWidth()) {
                    Card(modifier = Modifier.weight(1f).height(80.dp), colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.15f))) {
                        Column(Modifier.fillMaxSize(), Arrangement.Center, Alignment.CenterHorizontally) {
                            Text("SCORE", fontSize = 10.sp, color = Color.White); Text("$score", fontSize = 32.sp, fontWeight = FontWeight.Black, color = Color.White)
                        }
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Card(modifier = Modifier.weight(1f).height(80.dp), colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.15f))) {
                        Column(Modifier.fillMaxSize(), Arrangement.Center, Alignment.CenterHorizontally) {
                            Text("OVERS / WICKETS", fontSize = 10.sp, color = Color.White); Text(oversWickets, fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                ReadOnlyViewerPlayerRow("Player 1", p1?.name ?: "No selection")
                Spacer(Modifier.height(8.dp))
                ReadOnlyViewerPlayerRow("Player 2", p2?.name ?: "No selection")
                
                Spacer(modifier = Modifier.height(24.dp))
                Text("PLAYER PERFORMANCE", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
                
                Card(modifier = Modifier.fillMaxWidth().padding(top = 8.dp), colors = CardDefaults.cardColors(containerColor = Color.Black.copy(alpha = 0.3f))) {
                    Column {
                        Row(modifier = Modifier.background(Color.DarkGray).padding(8.dp)) {
                            Text("Players", modifier = Modifier.weight(2.5f), fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Color.White)
                            listOf("B", "4s", "6s", "W", "TOT").forEach { Text(it, modifier = Modifier.weight(1f), textAlign = TextAlign.Center, fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Color.White) }
                        }
                        players.forEach { p ->
                            val statusColor = when(p.status) {
                                PlayerStatus.OUT -> Color.Red
                                PlayerStatus.NOT_OUT -> Color.Green
                                else -> Color.White
                            }
                            HorizontalDivider(thickness = 0.5.dp, color = Color.Gray)
                            Row(modifier = Modifier.padding(8.dp)) {
                                Text(p.name, modifier = Modifier.weight(2.5f), color = statusColor, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                                Text("${p.balls}", modifier = Modifier.weight(1f), textAlign = TextAlign.Center, fontSize = 14.sp, color = Color.White)
                                Text("${p.fours}", modifier = Modifier.weight(1f), textAlign = TextAlign.Center, fontSize = 14.sp, color = Color.White)
                                Text("${p.sixes}", modifier = Modifier.weight(1f), textAlign = TextAlign.Center, fontSize = 14.sp, color = Color.White)
                                Text("${p.wickets}", modifier = Modifier.weight(1f), textAlign = TextAlign.Center, fontSize = 14.sp, color = Color.Yellow)
                                Text("${p.totalRuns + p.extras}", modifier = Modifier.weight(1f), textAlign = TextAlign.Center, fontSize = 14.sp, fontWeight = FontWeight.Black, color = Color.White)
                            }
                        }
                    }
                }

                if (mom.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(24.dp))
                    Text("MAN OF THE MATCH", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    Surface(
                        modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                        color = Color.White.copy(alpha = 0.2f),
                        shape = RoundedCornerShape(8.dp),
                        border = BorderStroke(1.dp, Color.White)
                    ) {
                        Text(
                            text = "🌟 $mom",
                            modifier = Modifier.padding(16.dp),
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center,
                            color = Color.White
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ReadOnlyViewerPlayerRow(label: String, playerName: String) {
    Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(8.dp), colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.1f))) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(label.uppercase(), fontSize = 10.sp, color = Color.LightGray, fontWeight = FontWeight.Bold)
            Text(playerName, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.White)
        }
    }
}
