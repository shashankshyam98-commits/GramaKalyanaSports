package com.example.grama_kalyanasports.ui.fan

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.grama_kalyanasports.data.Match
import com.example.grama_kalyanasports.data.SportType
import com.example.grama_kalyanasports.ui.genai.MatchSummaryGenerator
import com.example.grama_kalyanasports.ui.genai.ShareUtils
import kotlinx.coroutines.launch

@Composable
fun FanDashboard(viewModel: FanViewModel) {
    val liveMatches by viewModel.liveMatches.collectAsState()

    // Outdoor high contrast: Black text on white with thick borders
    Surface(
        color = Color.White,
        modifier = Modifier.fillMaxSize()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                "LIVE GRAMA KALYANA SPORTS", 
                fontSize = 26.sp, 
                fontWeight = FontWeight.ExtraBold,
                color = Color.Black
            )
            Spacer(modifier = Modifier.height(16.dp))
            
            if (liveMatches.isEmpty()) {
                Text(
                    "No matches currently live.", 
                    fontSize = 20.sp, 
                    color = Color.DarkGray
                )
            } else {
                LazyColumn {
                    items(liveMatches) { match ->
                        HighContrastMatchCard(match)
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun HighContrastMatchCard(match: Match) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .border(3.dp, Color.Black)
            .background(Color(0xFFFFFFE0)) // Light Neon Yellow for high contrast visibility outdoors
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "${match.sportType.name} - LIVE", 
            fontSize = 20.sp, 
            fontWeight = FontWeight.Bold,
            color = Color(0xFFD32F2F) // Deep Red
        )
        Spacer(modifier = Modifier.height(12.dp))
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(match.teamA, fontSize = 28.sp, fontWeight = FontWeight.ExtraBold, color = Color.Black)
            Text("VS", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.DarkGray)
            Text(match.teamB, fontSize = 28.sp, fontWeight = FontWeight.ExtraBold, color = Color.Black)
        }
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("${match.currentScoreA}", fontSize = 64.sp, fontWeight = FontWeight.Black, color = Color.Black)
            Text("-", fontSize = 48.sp, fontWeight = FontWeight.Black, color = Color.Black)
            Text("${match.currentScoreB}", fontSize = 64.sp, fontWeight = FontWeight.Black, color = Color.Black)
        }

        if (match.sportSpecificStats.isNotEmpty()) {
            Spacer(modifier = Modifier.height(12.dp))
            HorizontalDivider(color = Color.Black, thickness = 2.dp)
            Spacer(modifier = Modifier.height(8.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                // Team A specific stats
                Column {
                    when (match.sportType) {
                        SportType.KABADDI -> {
                            Text("Raid: ${match.sportSpecificStats["raidPointsA"] ?: 0}", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                            Text("Tackle: ${match.sportSpecificStats["tacklePointsA"] ?: 0}", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                        }
                        SportType.CRICKET -> {
                            Text("Wickets: ${match.sportSpecificStats["wicketsA"] ?: 0}", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                        }
                        else -> {}
                    }
                }

                // Team B specific stats
                Column(horizontalAlignment = Alignment.End) {
                    when (match.sportType) {
                        SportType.KABADDI -> {
                            Text("Raid: ${match.sportSpecificStats["raidPointsB"] ?: 0}", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                            Text("Tackle: ${match.sportSpecificStats["tacklePointsB"] ?: 0}", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                        }
                        SportType.CRICKET -> {
                            Text("Wickets: ${match.sportSpecificStats["wicketsB"] ?: 0}", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                        }
                        else -> {}
                    }
                }
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        val context = LocalContext.current
        val coroutineScope = rememberCoroutineScope()
        // Note: For production, do NOT hardcode API keys!
        val summaryGenerator = remember { MatchSummaryGenerator("YOUR_GEMINI_API_KEY") }
        
        Button(
            onClick = {
                coroutineScope.launch {
                    val summary = summaryGenerator.generateSummary(match)
                    ShareUtils.shareText(context, summary)
                }
            },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = Color.Black)
        ) {
            Text("Generate AI Summary & Share", color = Color.White)
        }
    }
}
