package com.example.grama_kalyanasports.ui.scorer

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.grama_kalyanasports.data.Match
import com.example.grama_kalyanasports.data.SportType

@Composable
fun ScorerDashboard(viewModel: ScorerViewModel) {
    val currentMatch by viewModel.currentMatch.collectAsState()

    if (currentMatch == null) {
        ScorerSetupScreen(
            onCreateMatch = { teamA, teamB, sport ->
                viewModel.createMatch(teamA, teamB, sport)
            }
        )
    } else {
        ScorerMatchScreen(
            viewModel = viewModel,
            match = currentMatch!!
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScorerSetupScreen(onCreateMatch: (String, String, SportType) -> Unit) {
    var teamA by remember { mutableStateOf("") }
    var teamB by remember { mutableStateOf("") }
    var selectedSport by remember { mutableStateOf(SportType.KABADDI) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Create New Match", fontSize = 24.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(24.dp))
        
        OutlinedTextField(
            value = teamA,
            onValueChange = { teamA = it },
            label = { Text("Team A Name") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        
        OutlinedTextField(
            value = teamB,
            onValueChange = { teamB = it },
            label = { Text("Team B Name") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))

        Text("Select Sport:")
        Row {
            SportType.entries.forEach { sport ->
                Row(verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(
                        selected = selectedSport == sport,
                        onClick = { selectedSport = sport }
                    )
                    Text(sport.name)
                }
            }
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        Button(
            onClick = { onCreateMatch(teamA, teamB, selectedSport) },
            modifier = Modifier.fillMaxWidth(),
            enabled = teamA.isNotBlank() && teamB.isNotBlank()
        ) {
            Text("Start Match")
        }
    }
}

@Composable
fun ScorerMatchScreen(viewModel: ScorerViewModel, match: Match) {
    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Scorer Dashboard - ${match.sportType.name}", fontSize = 20.sp, fontWeight = FontWeight.Bold)
        
        Spacer(modifier = Modifier.height(24.dp))
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            // Team A column
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(match.teamA, fontSize = 24.sp, fontWeight = FontWeight.Bold)
                Text("${match.currentScoreA}", fontSize = 48.sp, fontWeight = FontWeight.ExtraBold)
                Button(onClick = { viewModel.addPointA() }) {
                    Text("+1 Point")
                }
            }
            
            Text("VS", fontSize = 24.sp, modifier = Modifier.align(Alignment.CenterVertically))

            // Team B column
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(match.teamB, fontSize = 24.sp, fontWeight = FontWeight.Bold)
                Text("${match.currentScoreB}", fontSize = 48.sp, fontWeight = FontWeight.ExtraBold)
                Button(onClick = { viewModel.addPointB() }) {
                    Text("+1 Point")
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Sport Specific Buttons
        when (match.sportType) {
            SportType.KABADDI -> KabaddiControls(viewModel, match)
            SportType.CRICKET -> CricketControls(viewModel, match)
            SportType.VOLLEYBALL -> {
                Text("Volleyball standard scoring applies.", fontSize = 16.sp)
            }
        }
    }
}

@Composable
fun KabaddiControls(viewModel: ScorerViewModel, match: Match) {
    val raidPointsA = match.sportSpecificStats["raidPointsA"] ?: 0
    val tacklePointsA = match.sportSpecificStats["tacklePointsA"] ?: 0
    
    val raidPointsB = match.sportSpecificStats["raidPointsB"] ?: 0
    val tacklePointsB = match.sportSpecificStats["tacklePointsB"] ?: 0

    Column(Modifier.fillMaxWidth()) {
        Text("Kabaddi Specific Stats", fontWeight = FontWeight.Medium)
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Column {
                Text("Team A")
                OutlinedButton(onClick = {
                    viewModel.updateSpecificStat("raidPointsA", raidPointsA + 1)
                    viewModel.addPointA()
                }) { Text("Raid +1") }
                OutlinedButton(onClick = {
                    viewModel.updateSpecificStat("tacklePointsA", tacklePointsA + 1)
                    viewModel.addPointA()
                }) { Text("Tackle +1") }
            }
            Column {
                Text("Team B")
                OutlinedButton(onClick = {
                    viewModel.updateSpecificStat("raidPointsB", raidPointsB + 1)
                    viewModel.addPointB()
                }) { Text("Raid +1") }
                OutlinedButton(onClick = {
                    viewModel.updateSpecificStat("tacklePointsB", tacklePointsB + 1)
                    viewModel.addPointB()
                }) { Text("Tackle +1") }
            }
        }
    }
}

@Composable
fun CricketControls(viewModel: ScorerViewModel, match: Match) {
    val wicketsA = match.sportSpecificStats["wicketsA"] ?: 0
    val wicketsB = match.sportSpecificStats["wicketsB"] ?: 0
    
    Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
        Text("Cricket Specific Stats", fontWeight = FontWeight.Medium)
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("Team A Wickets: $wicketsA")
                OutlinedButton(onClick = {
                    viewModel.updateSpecificStat("wicketsA", wicketsA + 1)
                }) { Text("Wicket down!") }
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("Team B Wickets: $wicketsB")
                OutlinedButton(onClick = {
                    viewModel.updateSpecificStat("wicketsB", wicketsB + 1)
                }) { Text("Wicket down!") }
            }
        }
    }
}
