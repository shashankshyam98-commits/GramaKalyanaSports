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
import androidx.compose.material.icons.filled.Remove
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
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KabaddiScorecardScreen(
    tournamentName: String,
    isAdmin: Boolean = true,
    onBack: () -> Unit
) {
    val kabaddiStates by TournamentRepository.kabaddiMatchStates.collectAsState(initial = emptyMap())
    val matchState = kabaddiStates[tournamentName] ?: KabaddiMatchState()

    var minutesTFV by remember { mutableStateOf(TextFieldValue(matchState.minutes)) }
    var secondsTFV by remember { mutableStateOf(TextFieldValue(matchState.seconds)) }
    var teamANameTFV by remember { mutableStateOf(TextFieldValue(matchState.teamAName)) }
    var teamBNameTFV by remember { mutableStateOf(TextFieldValue(matchState.teamBName)) }
    var teamAScoreTFV by remember { mutableStateOf(TextFieldValue(matchState.teamAScore.toString())) }
    var teamBScoreTFV by remember { mutableStateOf(TextFieldValue(matchState.teamBScore.toString())) }

    LaunchedEffect(matchState) {
        if (matchState.minutes != minutesTFV.text) minutesTFV = minutesTFV.copy(text = matchState.minutes)
        if (matchState.seconds != secondsTFV.text) secondsTFV = secondsTFV.copy(text = matchState.seconds)
        if (matchState.teamAName != teamANameTFV.text) teamANameTFV = teamANameTFV.copy(text = matchState.teamAName)
        if (matchState.teamBName != teamBNameTFV.text) teamBNameTFV = teamBNameTFV.copy(text = matchState.teamBName)
        if (matchState.teamAScore.toString() != teamAScoreTFV.text) teamAScoreTFV = teamAScoreTFV.copy(text = matchState.teamAScore.toString())
        if (matchState.teamBScore.toString() != teamBScoreTFV.text) teamBScoreTFV = teamBScoreTFV.copy(text = matchState.teamBScore.toString())
    }

    var showPlayerDialog by remember { mutableStateOf<Pair<Boolean, KabaddiPlayerStats?>>(false to null) }
    var targetTeamForUpdate by remember { mutableStateOf("A") }
    
    var isCapturing by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    
    val isLocked = matchState.status == MatchStatus.FINISHED || matchState.status == MatchStatus.CANCELED
    val isEditable = isAdmin && !isLocked

    fun updateState(newState: KabaddiMatchState) {
        if (isAdmin) {
            TournamentRepository.updateKabaddiScore(tournamentName, newState)
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
            colors = CardDefaults.cardColors(containerColor = Color(0xFFD81B60)), // Magenta
            elevation = CardDefaults.cardElevation(4.dp)
        ) {
            Text(
                "KABADDI SCORE",
                fontSize = 24.sp,
                fontWeight = FontWeight.Black,
                textAlign = TextAlign.Center,
                color = Color.White,
                modifier = Modifier.fillMaxWidth().padding(16.dp)
            )
        }

        Card(
            modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF4527A0)), // Purple
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

        Spacer(modifier = Modifier.height(12.dp))

        // Timer Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFF57C00)), // Orange
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

        // TOTAL Card 
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF1976D2)), // Blue
            elevation = CardDefaults.cardElevation(4.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    "TOTAL", 
                    fontSize = 14.sp, 
                    fontWeight = FontWeight.Bold, 
                    color = Color.White,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxWidth()
                ) {
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
                        Text(" / ", color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 12.dp))
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
                        Text("${matchState.teamAScore} / ${matchState.teamBScore}", fontSize = 36.sp, fontWeight = FontWeight.Black, color = Color.White)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Team A Section
        KabaddiTeamSection(
            label = "TEAM A",
            teamNameTFV = teamANameTFV,
            teamNameString = matchState.teamAName,
            state = matchState,
            isAdmin = isAdmin,
            isEditable = isEditable,
            isTeamA = true,
            containerColor = Color(0xFFE91E63), // Pink
            onUpdate = { updateState(it) },
            onNameChange = { 
                teamANameTFV = it
                updateState(matchState.copy(teamAName = it.text))
            },
            onAddPlayer = {
                targetTeamForUpdate = "A"
                showPlayerDialog = true to KabaddiPlayerStats(id = UUID.randomUUID().toString())
            },
            onEditPlayer = { p ->
                targetTeamForUpdate = "A"
                showPlayerDialog = true to p
            }
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Team B Section
        KabaddiTeamSection(
            label = "TEAM B",
            teamNameTFV = teamBNameTFV,
            teamNameString = matchState.teamBName,
            state = matchState,
            isAdmin = isAdmin,
            isEditable = isEditable,
            isTeamA = false,
            containerColor = Color(0xFF03A9F4), // Light Blue
            onUpdate = { updateState(it) },
            onNameChange = { 
                teamBNameTFV = it
                updateState(matchState.copy(teamBName = it.text))
            },
            onAddPlayer = {
                targetTeamForUpdate = "B"
                showPlayerDialog = true to KabaddiPlayerStats(id = UUID.randomUUID().toString())
            },
            onEditPlayer = { p ->
                targetTeamForUpdate = "B"
                showPlayerDialog = true to p
            }
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
                        captureAndSaveScreenshot(context as Activity)
                        
                        val sb = StringBuilder()
                        sb.append("KABADDI SCORECARD - $tournamentName\n")
                        sb.append("Status: ${matchState.status}\n")
                        sb.append("Time: ${matchState.minutes}:${matchState.seconds}\n")
                        sb.append("Final Score: ${matchState.teamAScore} / ${matchState.teamBScore}\n\n")
                        
                        sb.append("TEAM A: ${matchState.teamAName} | Score: ${matchState.teamAScore}\n")
                        sb.append("Raid Points: ${matchState.teamARaidPoints} | Tackle Points: ${matchState.teamATacklePoints}\n")
                        sb.append("Players A:\n")
                        matchState.playersA.forEach { p -> sb.append("- ${p.name}: ${p.points} pts\n") }
                        sb.append("Man of the Match A: ${matchState.teamAManOfTheMatch}\n\n")
                        
                        sb.append("TEAM B: ${matchState.teamBName} | Score: ${matchState.teamBScore}\n")
                        sb.append("Raid Points: ${matchState.teamBRaidPoints} | Tackle Points: ${matchState.teamBTacklePoints}\n")
                        sb.append("Players B:\n")
                        matchState.playersB.forEach { p -> sb.append("- ${p.name}: ${p.points} pts\n") }
                        sb.append("Man of the Match B: ${matchState.teamBManOfTheMatch}\n")
                        
                        downloadScoreDataAsFile(context, "Kabaddi_${tournamentName.replace(" ","_")}", sb.toString())
                        
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
        var currentPoints by remember { mutableIntStateOf(playerToEdit?.points ?: 0) }

        AlertDialog(
            onDismissRequest = { showPlayerDialog = false to null },
            title = { Text(if (playerToEdit?.name?.isEmpty() == true) "New Player" else "Edit Player") },
            text = {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    OutlinedTextField(value = nameTFV, onValueChange = { nameTFV = it }, label = { Text("Player Name") })
                    Spacer(Modifier.height(16.dp))
                    Text("Points", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(onClick = { currentPoints = (currentPoints - 1).coerceAtLeast(0) }) {
                            Icon(Icons.Default.Remove, contentDescription = "Decrease")
                        }
                        Text(currentPoints.toString(), fontSize = 32.sp, fontWeight = FontWeight.Black, modifier = Modifier.padding(horizontal = 24.dp))
                        IconButton(onClick = { currentPoints++ }) {
                            Icon(Icons.Default.Add, contentDescription = "Increase")
                        }
                    }
                }
            },
            confirmButton = {
                Button(onClick = {
                    val isA = targetTeamForUpdate == "A"
                    val currentList = (if (isA) matchState.playersA else matchState.playersB).toMutableList()
                    val idx = currentList.indexOfFirst { it.id == playerToEdit?.id }
                    
                    val oldPoints = playerToEdit?.points ?: 0
                    val delta = currentPoints - oldPoints
                    
                    if (idx != -1) currentList[idx] = currentList[idx].copy(name = nameTFV.text, points = currentPoints)
                    else if (playerToEdit != null) currentList.add(playerToEdit.copy(name = nameTFV.text, points = currentPoints))
                    
                    val newState = if (isA) {
                        matchState.copy(
                            playersA = currentList, 
                            teamARaidPoints = (matchState.teamARaidPoints + delta).coerceAtLeast(0),
                            teamAScore = (matchState.teamAScore + delta).coerceAtLeast(0)
                        )
                    } else {
                        matchState.copy(
                            playersB = currentList, 
                            teamBRaidPoints = (matchState.teamBRaidPoints + delta).coerceAtLeast(0),
                            teamBScore = (matchState.teamBScore + delta).coerceAtLeast(0)
                        )
                    }
                    updateState(newState)
                    showPlayerDialog = false to null
                }) { Text("Save") }
            }
        )
    }
}

@Composable
fun KabaddiTeamSection(
    label: String,
    teamNameTFV: TextFieldValue,
    teamNameString: String,
    state: KabaddiMatchState,
    isAdmin: Boolean,
    isEditable: Boolean,
    isTeamA: Boolean,
    containerColor: Color,
    onUpdate: (KabaddiMatchState) -> Unit,
    onNameChange: (TextFieldValue) -> Unit,
    onAddPlayer: () -> Unit,
    onEditPlayer: (KabaddiPlayerStats) -> Unit
) {
    val raidPoints = if (isTeamA) state.teamARaidPoints else state.teamBRaidPoints
    val tacklePoints = if (isTeamA) state.teamATacklePoints else state.teamBTacklePoints
    val score = if (isTeamA) state.teamAScore else state.teamBScore
    val players = if (isTeamA) state.playersA else state.playersB
    val mom = if (isTeamA) state.teamAManOfTheMatch else state.teamBManOfTheMatch

    Column(modifier = Modifier.padding(vertical = 8.dp)) {
        Card(
            modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
            colors = CardDefaults.cardColors(containerColor = containerColor),
            elevation = CardDefaults.cardElevation(4.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("$label NAME", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
                OutlinedTextField(
                    value = teamNameTFV,
                    onValueChange = onNameChange,
                    modifier = Modifier.fillMaxWidth(),
                    readOnly = !isEditable,
                    colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White, focusedBorderColor = Color.White, unfocusedBorderColor = Color.White),
                    placeholder = { Text("Enter Team Name", color = Color.White.copy(alpha = 0.6f)) }
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
                            IconButton(onClick = { 
                                if (isTeamA) onUpdate(state.copy(teamAScore = (score - 1).coerceAtLeast(0))) 
                                else onUpdate(state.copy(teamBScore = (score - 1).coerceAtLeast(0))) 
                            }) { Text("-", fontSize = 32.sp, fontWeight = FontWeight.Bold, color = Color.White) }
                        } else { Spacer(Modifier.size(48.dp)) }
                        
                        Text("$score", fontSize = 40.sp, fontWeight = FontWeight.Black, color = Color.White)
                        
                        if (isEditable) {
                            IconButton(onClick = { 
                                if (isTeamA) onUpdate(state.copy(teamAScore = score + 1)) 
                                else onUpdate(state.copy(teamBScore = score + 1)) 
                            }) { Icon(Icons.Default.Add, contentDescription = "Add Score", modifier = Modifier.size(32.dp), tint = Color.White) }
                        } else { Spacer(Modifier.size(48.dp)) }
                    }
                }
            }
        }

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Card(modifier = Modifier.weight(1f), colors = CardDefaults.cardColors(containerColor = Color(0xFF1B5E20))) { // Very Dark Green
                Column(Modifier.padding(12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("RAID POINTS", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    if (isEditable) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            IconButton(onClick = { 
                                if (isTeamA) onUpdate(state.copy(teamARaidPoints = (raidPoints - 1).coerceAtLeast(0), teamAScore = (score - 1).coerceAtLeast(0))) 
                                else onUpdate(state.copy(teamBRaidPoints = (raidPoints - 1).coerceAtLeast(0), teamBScore = (score - 1).coerceAtLeast(0))) 
                            }) { Text("-", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color.White) }
                            Text("$raidPoints", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color.White)
                            IconButton(onClick = { 
                                if (isTeamA) onUpdate(state.copy(teamARaidPoints = raidPoints + 1, teamAScore = score + 1)) 
                                else onUpdate(state.copy(teamBRaidPoints = raidPoints + 1, teamBScore = score + 1)) 
                            }) { Text("+", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color.White) }
                        }
                    } else {
                        Text("$raidPoints", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    }
                }
            }
            Card(modifier = Modifier.weight(1f), colors = CardDefaults.cardColors(containerColor = Color(0xFF01579B))) { // Very Dark Blue
                Column(Modifier.padding(12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("TACKLE POINTS", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    if (isEditable) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            IconButton(onClick = { 
                                if (isTeamA) onUpdate(state.copy(teamATacklePoints = (tacklePoints - 1).coerceAtLeast(0), teamAScore = (score - 1).coerceAtLeast(0))) 
                                else onUpdate(state.copy(teamBTacklePoints = (tacklePoints - 1).coerceAtLeast(0), teamBScore = (score - 1).coerceAtLeast(0))) 
                            }) { Text("-", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color.White) }
                            Text("$tacklePoints", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color.White)
                            IconButton(onClick = { 
                                if (isTeamA) onUpdate(state.copy(teamATacklePoints = tacklePoints + 1, teamAScore = score + 1)) 
                                else onUpdate(state.copy(teamBTacklePoints = tacklePoints + 1, teamBScore = score + 1)) 
                            }) { Text("+", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color.White) }
                        }
                    } else {
                        Text("$tacklePoints", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text("PLAYERS TABLE", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
        Column(modifier = Modifier.fillMaxWidth().border(1.dp, Color.Gray)) {
            Row(modifier = Modifier.background(Color.DarkGray).padding(4.dp)) {
                Text("Player Name", modifier = Modifier.weight(2f).then(if(isEditable) Modifier.clickable { onAddPlayer() } else Modifier), fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Color.White)
                Text("Points", modifier = Modifier.weight(1f), textAlign = TextAlign.Center, fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Color.White)
            }
            players.forEach { p ->
                Row(modifier = Modifier.padding(4.dp).border(0.5.dp, Color.Gray).clickable(enabled = isEditable) { onEditPlayer(p) }) {
                    Text(p.name, modifier = Modifier.weight(2f), fontSize = 14.sp, color = Color.White)
                    Text("${p.points}", modifier = Modifier.weight(1f), textAlign = TextAlign.Center, fontSize = 14.sp, color = Color.White)
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
                placeholder = { Text("select player", color = Color.White) }
            )
            if (isEditable) {
                DropdownMenu(expanded = momExpanded, onDismissRequest = { momExpanded = false }) {
                    players.forEach { p ->
                        DropdownMenuItem(
                            text = { Text(p.name) },
                            onClick = {
                                if (isTeamA) onUpdate(state.copy(teamAManOfTheMatch = p.name))
                                else onUpdate(state.copy(teamBManOfTheMatch = p.name))
                                momExpanded = false
                            }
                        )
                    }
                }
            }
        }
    }
}
