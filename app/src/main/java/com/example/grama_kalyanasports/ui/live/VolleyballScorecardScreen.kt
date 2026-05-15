package com.example.grama_kalyanasports.ui.live

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import android.app.Activity
import com.example.grama_kalyanasports.data.*
import java.util.UUID
import java.util.Locale
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VolleyballScorecardScreen(
    tournamentName: String,
    isAdmin: Boolean = true,
    onBack: () -> Unit
) {
    val volleyballStates by TournamentRepository.volleyballMatchStates.collectAsState(initial = emptyMap())
    val matchState = volleyballStates[tournamentName] ?: VolleyballMatchState()

    var minutesTFV by remember { mutableStateOf(TextFieldValue(matchState.minutes)) }
    var secondsTFV by remember { mutableStateOf(TextFieldValue(matchState.seconds)) }
    var winningTeamTFV by remember { mutableStateOf(TextFieldValue(matchState.winningTeam)) }
    var teamANameTFV by remember { mutableStateOf(TextFieldValue(matchState.teamAName)) }
    var teamBNameTFV by remember { mutableStateOf(TextFieldValue(matchState.teamBName)) }
    var teamAScoreTFV by remember { mutableStateOf(TextFieldValue(matchState.teamAScore.toString())) }
    var teamBScoreTFV by remember { mutableStateOf(TextFieldValue(matchState.teamBScore.toString())) }

    LaunchedEffect(matchState) {
        if (matchState.minutes != minutesTFV.text) minutesTFV = minutesTFV.copy(text = matchState.minutes)
        if (matchState.seconds != secondsTFV.text) secondsTFV = secondsTFV.copy(text = matchState.seconds)
        if (matchState.winningTeam != winningTeamTFV.text) winningTeamTFV = winningTeamTFV.copy(text = matchState.winningTeam)
        if (matchState.teamAName != teamANameTFV.text) teamANameTFV = teamANameTFV.copy(text = matchState.teamAName)
        if (matchState.teamBName != teamBNameTFV.text) teamBNameTFV = teamBNameTFV.copy(text = matchState.teamBName)
        if (matchState.teamAScore.toString() != teamAScoreTFV.text) teamAScoreTFV = teamAScoreTFV.copy(text = matchState.teamAScore.toString())
        if (matchState.teamBScore.toString() != teamBScoreTFV.text) teamBScoreTFV = teamBScoreTFV.copy(text = matchState.teamBScore.toString())
    }

    var showPlayerDialog by remember { mutableStateOf<Pair<Boolean, VolleyballPlayerStats?>>(false to null) }
    var targetTeamForUpdate by remember { mutableStateOf("A") }
    
    var isCapturing by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    
    val isLocked = matchState.status == MatchStatus.FINISHED || matchState.status == MatchStatus.CANCELED
    val isEditable = isAdmin && !isLocked

    fun updateState(newState: VolleyballMatchState) {
        if (isAdmin) {
            TournamentRepository.updateVolleyballScore(tournamentName, newState)
        }
    }

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
            colors = CardDefaults.cardColors(containerColor = Color(0xFFAD1457)), // Saturated Pink
            elevation = CardDefaults.cardElevation(4.dp)
        ) {
            Text(
                "VOLLEYBALL SCORE",
                fontSize = 28.sp,
                fontWeight = FontWeight.Black,
                textAlign = TextAlign.Center,
                color = Color.White,
                modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp)
            )
        }

        Card(
            modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF6A1B9A)), // Vibrant Purple
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

        Row(
            modifier = Modifier.fillMaxWidth(),
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
                    onClick = { if (isAdmin) updateState(matchState.copy(status = status)) },
                    modifier = Modifier.weight(1f).height(48.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = containerColor,
                        contentColor = Color.White
                    ),
                    enabled = isAdmin && (!isLocked || isSelected),
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Text(status.name, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Timer Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFBF360C)), // Dark Orange
            elevation = CardDefaults.cardElevation(2.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Text("TIMER", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value = minutesTFV,
                        onValueChange = { 
                            if (isEditable && it.text.length <= 2) {
                                minutesTFV = it
                                updateState(matchState.copy(minutes = it.text)) 
                            }
                        },
                        label = { Text("Min", color = Color.White) },
                        modifier = Modifier.width(80.dp),
                        readOnly = !isEditable,
                        colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White, focusedBorderColor = Color.White, unfocusedBorderColor = Color.White),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )
                    Text(" : ", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    OutlinedTextField(
                        value = secondsTFV,
                        onValueChange = { 
                            if (isEditable && it.text.length <= 2) {
                                secondsTFV = it
                                updateState(matchState.copy(seconds = it.text))
                            }
                        },
                        label = { Text("Sec", color = Color.White) },
                        modifier = Modifier.width(80.dp),
                        readOnly = !isEditable,
                        colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White, focusedBorderColor = Color.White, unfocusedBorderColor = Color.White),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Winning Team Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF283593)), // Deep Blue/Indigo
            elevation = CardDefaults.cardElevation(2.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("WINNING TEAM", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
                OutlinedTextField(
                    value = winningTeamTFV,
                    onValueChange = { 
                        if (isEditable) {
                            winningTeamTFV = it
                            updateState(matchState.copy(winningTeam = it.text)) 
                        }
                    },
                    placeholder = { Text("Enter Winning Team", color = Color.LightGray) },
                    modifier = Modifier.fillMaxWidth(),
                    readOnly = !isEditable,
                    colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White, focusedBorderColor = Color.White, unfocusedBorderColor = Color.White)
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // TOTAL SCORE Card 
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF1565C0)), // Vibrant Blue
            elevation = CardDefaults.cardElevation(2.dp)
        ) {
            Column(Modifier.padding(16.dp).fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                Text("TOTAL SCORE", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.White)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (isEditable) {
                        OutlinedTextField(
                            value = teamAScoreTFV,
                            onValueChange = { 
                                if (it.text.all { c -> c.isDigit() } || it.text.isEmpty()) {
                                    teamAScoreTFV = it
                                    updateState(matchState.copy(teamAScore = it.text.toIntOrNull() ?: 0))
                                }
                            },
                            modifier = Modifier.width(100.dp),
                            label = { Text("Team A", color = Color.White, fontSize = 10.sp) },
                            colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White, focusedBorderColor = Color.White, unfocusedBorderColor = Color.White),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                        )
                        Text(" - ", fontSize = 48.sp, fontWeight = FontWeight.Black, color = Color.White, modifier = Modifier.padding(horizontal = 12.dp))
                        OutlinedTextField(
                            value = teamBScoreTFV,
                            onValueChange = { 
                                if (it.text.all { c -> c.isDigit() } || it.text.isEmpty()) {
                                    teamBScoreTFV = it
                                    updateState(matchState.copy(teamBScore = it.text.toIntOrNull() ?: 0))
                                }
                            },
                            modifier = Modifier.width(100.dp),
                            label = { Text("Team B", color = Color.White, fontSize = 10.sp) },
                            colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White, focusedBorderColor = Color.White, unfocusedBorderColor = Color.White),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                        )
                    } else {
                        Text("${matchState.teamAScore} - ${matchState.teamBScore}", fontSize = 48.sp, fontWeight = FontWeight.Black, color = Color.White)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Team A Section
        VolleyballTeamSection(
            label = "TEAM A",
            teamNameTFV = teamANameTFV,
            teamNameString = matchState.teamAName,
            score = matchState.teamAScore,
            players = matchState.playersA,
            mom = matchState.teamAManOfTheMatch,
            isEditable = isEditable,
            containerColor = Color(0xFFC62828), // Vibrant Red
            onUpdateName = { 
                teamANameTFV = it
                updateState(matchState.copy(teamAName = it.text))
            },
            onIncrementScore = { updateState(matchState.copy(teamAScore = matchState.teamAScore + 1)) },
            onDecrementScore = { updateState(matchState.copy(teamAScore = (matchState.teamAScore - 1).coerceAtLeast(0))) },
            onAddPlayer = {
                targetTeamForUpdate = "A"
                showPlayerDialog = true to VolleyballPlayerStats(id = UUID.randomUUID().toString())
            },
            onEditPlayer = { p ->
                targetTeamForUpdate = "A"
                showPlayerDialog = true to p
            },
            onSelectMoM = { player -> updateState(matchState.copy(teamAManOfTheMatch = player)) }
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Team B Section
        VolleyballTeamSection(
            label = "TEAM B",
            teamNameTFV = teamBNameTFV,
            teamNameString = matchState.teamBName,
            score = matchState.teamBScore,
            players = matchState.playersB,
            mom = matchState.teamBManOfTheMatch,
            isEditable = isEditable,
            containerColor = Color(0xFF00695C), // Vibrant Dark Teal
            onUpdateName = { 
                teamBNameTFV = it
                updateState(matchState.copy(teamBName = it.text))
            },
            onIncrementScore = { updateState(matchState.copy(teamBScore = matchState.teamBScore + 1)) },
            onDecrementScore = { updateState(matchState.copy(teamBScore = (matchState.teamBScore - 1).coerceAtLeast(0))) },
            onAddPlayer = {
                targetTeamForUpdate = "B"
                showPlayerDialog = true to VolleyballPlayerStats(id = UUID.randomUUID().toString())
            },
            onEditPlayer = { p ->
                targetTeamForUpdate = "B"
                showPlayerDialog = true to p
            },
            onSelectMoM = { player -> updateState(matchState.copy(teamBManOfTheMatch = player)) }
        )

        if (!isCapturing) {
            Spacer(modifier = Modifier.height(32.dp))
            Row(horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                Card(
                    modifier = Modifier.clickable { onBack() },
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(8.dp),
                    elevation = CardDefaults.cardElevation(2.dp)
                ) {
                    Text(
                        "Back", 
                        color = Color.Black, 
                        fontWeight = FontWeight.Bold, 
                        fontSize = 16.sp,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                    )
                }
                Button(onClick = { 
                    scope.launch {
                        isCapturing = true
                        delay(500)
                        
                        val sb = StringBuilder()
                        sb.append("VOLLEYBALL SCORECARD - $tournamentName\n")
                        sb.append("------------------------------------------\n")
                        sb.append("Match Status: ${matchState.status}\n")
                        sb.append("Time: ${matchState.minutes}:${matchState.seconds}\n")
                        sb.append("Winning Team: ${matchState.winningTeam.ifEmpty { "In Progress" }}\n")
                        sb.append("Final Score: ${matchState.teamAScore} - ${matchState.teamBScore}\n")
                        sb.append("------------------------------------------\n\n")
                        
                        sb.append("TEAM A: ${matchState.teamAName}\n")
                        sb.append("Score: ${matchState.teamAScore}\n")
                        sb.append("Players (A):\n")
                        sb.append(String.format(Locale.US, "%-20s %-5s\n", "NAME", "PTS"))
                        matchState.playersA.forEach { p ->
                            sb.append(String.format(Locale.US, "%-20s %-5d\n", p.name, p.points))
                        }
                        sb.append("Man of the Match (A): ${matchState.teamAManOfTheMatch.ifEmpty { "N/A" }}\n\n")

                        sb.append("TEAM B: ${matchState.teamBName}\n")
                        sb.append("Score: ${matchState.teamBScore}\n")
                        sb.append("Players (B):\n")
                        sb.append(String.format(Locale.US, "%-20s %-5s\n", "NAME", "PTS"))
                        matchState.playersB.forEach { p ->
                            sb.append(String.format(Locale.US, "%-20s %-5d\n", p.name, p.points))
                        }
                        sb.append("Man of the Match (B): ${matchState.teamBManOfTheMatch.ifEmpty { "N/A" }}\n")
                        
                        downloadScoreDataAsFile(context, "Volleyball_${tournamentName.replace(" ","_")}", sb.toString())

                        isCapturing = false
                    }
                }) { 
                    Text("Download Score") 
                }
            }
        }
        
        Spacer(modifier = Modifier.height(64.dp))
    }

    if (isAdmin && showPlayerDialog.first) {
        val playerToEdit = showPlayerDialog.second
        var nameTFV by remember { mutableStateOf(TextFieldValue(playerToEdit?.name ?: "")) }
        var pointsTFV by remember { mutableStateOf(TextFieldValue(playerToEdit?.points?.toString() ?: "0")) }

        AlertDialog(
            onDismissRequest = { showPlayerDialog = false to null },
            title = { Text(if (playerToEdit?.name?.isEmpty() == true) "New Player" else "Update Player Status") },
            text = {
                Column {
                    OutlinedTextField(value = nameTFV, onValueChange = { nameTFV = it }, label = { Text("Player Name") })
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = pointsTFV,
                        onValueChange = { pointsTFV = it },
                        label = { Text("Points") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )
                }
            },
            confirmButton = {
                Button(onClick = {
                    val isA = targetTeamForUpdate == "A"
                    val currentList = (if (isA) matchState.playersA else matchState.playersB).toMutableList()
                    val idx = currentList.indexOfFirst { it.id == playerToEdit?.id }
                    
                    val updatedPlayer = playerToEdit?.copy(
                        name = nameTFV.text,
                        points = pointsTFV.text.toIntOrNull() ?: 0
                    )
                    
                    if (idx != -1) {
                        if (updatedPlayer != null) currentList[idx] = updatedPlayer
                    } else if (updatedPlayer != null) {
                        currentList.add(updatedPlayer)
                    }
                    
                    val newState = if (isA) matchState.copy(playersA = currentList) else matchState.copy(playersB = currentList)
                    updateState(newState)
                    showPlayerDialog = false to null
                }) { Text("Save") }
            }
        )
    }
}

@Composable
fun VolleyballTeamSection(
    label: String,
    teamNameTFV: TextFieldValue,
    teamNameString: String,
    score: Int,
    players: List<VolleyballPlayerStats>,
    mom: String,
    isEditable: Boolean,
    containerColor: Color,
    onUpdateName: (TextFieldValue) -> Unit,
    onIncrementScore: () -> Unit,
    onDecrementScore: () -> Unit,
    onAddPlayer: () -> Unit,
    onEditPlayer: (VolleyballPlayerStats) -> Unit,
    onSelectMoM: (String) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
        colors = CardDefaults.cardColors(containerColor = containerColor),
        elevation = CardDefaults.cardElevation(4.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("$label NAME", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
            OutlinedTextField(
                value = teamNameTFV,
                onValueChange = onUpdateName,
                modifier = Modifier.fillMaxWidth(),
                readOnly = !isEditable,
                colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White, focusedBorderColor = Color.White, unfocusedBorderColor = Color.White),
                placeholder = { Text("Enter Team Name", color = Color.LightGray) }
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text("SCORE", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.15f)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    modifier = Modifier.padding(12.dp).fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (isEditable) {
                        IconButton(onClick = onDecrementScore) {
                            Text("-", fontSize = 32.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        }
                    } else { Spacer(Modifier.size(48.dp)) }
                    
                    Text("$score", fontSize = 40.sp, fontWeight = FontWeight.Black, color = Color.White)
                    
                    if (isEditable) {
                        IconButton(onClick = onIncrementScore) {
                            Icon(Icons.Default.Add, contentDescription = "Add Score", modifier = Modifier.size(32.dp), tint = Color.White)
                        }
                    } else { Spacer(Modifier.size(48.dp)) }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text("PLAYERS TABLE", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
            Column(modifier = Modifier.fillMaxWidth().border(1.dp, Color.White)) {
                Row(modifier = Modifier.background(Color.DarkGray).padding(8.dp)) {
                    Text("Player Name", modifier = Modifier.weight(2f).then(if(isEditable) Modifier.clickable { onAddPlayer() } else Modifier), fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color.White)
                    Text("Points", modifier = Modifier.weight(1f), textAlign = TextAlign.Center, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color.White)
                }
                players.forEach { p ->
                    Row(modifier = Modifier.padding(8.dp).border(0.5.dp, Color.White).clickable(enabled = isEditable) { onEditPlayer(p) }) {
                        Text(p.name, modifier = Modifier.weight(2f), fontSize = 16.sp, color = Color.White)
                        Text("${p.points}", modifier = Modifier.weight(1f), textAlign = TextAlign.Center, fontSize = 16.sp, color = Color.White)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text("MAN OF THE MATCH", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
            var momExpanded by remember { mutableStateOf(false) }
            Box(modifier = Modifier.fillMaxWidth().padding(top = 4.dp)) {
                OutlinedTextField(
                    value = mom,
                    onValueChange = {},
                    readOnly = true,
                    enabled = false,
                    modifier = Modifier.fillMaxWidth().clickable { if (isEditable) momExpanded = true },
                    colors = OutlinedTextFieldDefaults.colors(disabledTextColor = Color.White, disabledBorderColor = Color.White),
                    placeholder = { Text("Select Player", color = Color.White) }
                )
                if (isEditable) {
                    DropdownMenu(expanded = momExpanded, onDismissRequest = { momExpanded = false }) {
                        players.forEach { p ->
                            DropdownMenuItem(
                                text = { Text(p.name) },
                                onClick = {
                                    onSelectMoM(p.name)
                                    momExpanded = false
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}
