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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.platform.LocalContext
import android.app.Activity
import com.example.grama_kalyanasports.data.*
import java.util.UUID
import java.util.Locale
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CricketScorecardScreen(
    tournamentName: String,
    isAdmin: Boolean = true,
    onBack: () -> Unit
) {
    val cricketStates by TournamentRepository.cricketMatchStates.collectAsState(initial = emptyMap())
    val matchState = cricketStates[tournamentName] ?: CricketMatchState()

    var totalOversTFV by remember { mutableStateOf(TextFieldValue(matchState.totalOvers)) }
    var teamANameTFV by remember { mutableStateOf(TextFieldValue(matchState.teamAName)) }
    var teamBNameTFV by remember { mutableStateOf(TextFieldValue(matchState.teamBName)) }
    var winningTeamTFV by remember { mutableStateOf(TextFieldValue(matchState.winningTeam)) }
    var targetChaseTFV by remember { mutableStateOf(TextFieldValue(matchState.targetChase)) }

    LaunchedEffect(matchState) {
        if (matchState.totalOvers != totalOversTFV.text) totalOversTFV = totalOversTFV.copy(text = matchState.totalOvers)
        if (matchState.teamAName != teamANameTFV.text) teamANameTFV = teamANameTFV.copy(text = matchState.teamAName)
        if (matchState.teamBName != teamBNameTFV.text) teamBNameTFV = teamBNameTFV.copy(text = matchState.teamBName)
        if (matchState.winningTeam != winningTeamTFV.text) winningTeamTFV = winningTeamTFV.copy(text = matchState.winningTeam)
        if (matchState.targetChase != targetChaseTFV.text) targetChaseTFV = targetChaseTFV.copy(text = matchState.targetChase)
    }

    var showPlayerDialog by remember { mutableStateOf<Pair<Boolean, CricketPlayerStats?>>(false to null) }
    var showPlusPopup by remember { mutableStateOf(false) }
    var activePlayerId by remember { mutableStateOf<String?>(null) }
    var isActivePlayerBowler by remember { mutableStateOf(false) }
    var showStatusSubMenu by remember { mutableStateOf(false) }
    var targetTeamForUpdate by remember { mutableStateOf("A") }
    
    var isCapturing by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    
    val isLocked = matchState.status == MatchStatus.FINISHED || matchState.status == MatchStatus.CANCELED
    val isEditable = isAdmin && !isLocked
    val context = LocalContext.current

    fun updateState(newState: CricketMatchState) {
        if (isAdmin) {
            TournamentRepository.updateCricketScore(tournamentName, newState)
        }
    }

    Scaffold(
        bottomBar = {
            if (!isCapturing) {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    tonalElevation = 8.dp,
                    shadowElevation = 8.dp,
                    color = Color.Black
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Card(
                            modifier = Modifier.weight(1f).height(48.dp).clickable { onBack() },
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                Text("Back", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                            }
                        }
                        Button(
                            onClick = { 
                                scope.launch {
                                    isCapturing = true
                                    delay(500)
                                    
                                    val sb = StringBuilder()
                                    sb.append("CRICKET SCORECARD - $tournamentName\n")
                                    sb.append("------------------------------------------\n")
                                    sb.append("Match Status: ${matchState.status}\n")
                                    sb.append("Winning Team: ${matchState.winningTeam.ifEmpty { "In Progress" }}\n")
                                    sb.append("Target/Chase: ${matchState.targetChase.ifEmpty { "Not Set" }}\n")
                                    sb.append("Total Overs Goal: ${matchState.totalOvers}\n")
                                    sb.append("Man of the Match (A): ${matchState.manOfTheMatchA.ifEmpty { "Not Decided" }}\n")
                                    sb.append("Man of the Match (B): ${matchState.manOfTheMatchB.ifEmpty { "Not Decided" }}\n")
                                    sb.append("------------------------------------------\n\n")

                                    sb.append("TEAM A: ${matchState.teamAName}\n")
                                    sb.append("Score: ${matchState.teamAScore}/${matchState.teamAWickets} (${matchState.teamAOversElapsed} overs)\n")
                                    sb.append("Player Performance (A):\n")
                                    sb.append(String.format(Locale.US, "| %-18s | %-3s | %-3s | %-3s | %-3s | %-5s |\n", "PLAYER NAME", "B", "4s", "6s", "W", "TOT"))
                                    sb.append("|--------------------|-----|-----|-----|-----|-------|\n")
                                    matchState.playersA.forEach { p ->
                                        sb.append(String.format(Locale.US, "| %-18s | %-3d | %-3d | %-3d | %-3d | %-5d |\n", p.name, p.balls, p.fours, p.sixes, p.wickets, (p.totalRuns + p.extras)))
                                    }
                                    sb.append("\n")

                                    sb.append("TEAM B: ${matchState.teamBName}\n")
                                    sb.append("Score: ${matchState.teamBScore}/${matchState.teamBWickets} (${matchState.teamBOversElapsed} overs)\n")
                                    sb.append("Player Performance (B):\n")
                                    sb.append(String.format(Locale.US, "| %-18s | %-3s | %-3s | %-3s | %-3s | %-5s |\n", "PLAYER NAME", "B", "4s", "6s", "W", "TOT"))
                                    sb.append("|--------------------|-----|-----|-----|-----|-------|\n")
                                    matchState.playersB.forEach { p ->
                                        sb.append(String.format(Locale.US, "| %-18s | %-3d | %-3d | %-3d | %-3d | %-5d |\n", p.name, p.balls, p.fours, p.sixes, p.wickets, (p.totalRuns + p.extras)))
                                    }

                                    downloadScoreDataAsFile(context, "Cricket_${tournamentName.replace(" ","_")}", sb.toString())
                                    isCapturing = false
                                }
                            },
                            modifier = Modifier.weight(1f).height(48.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1B5E20)),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("DOWNLOAD", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        }
                    }
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF121212)) 
                .padding(paddingValues)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Card(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFD81B60)), // Magenta
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
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
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
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
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
                            contentColor = Color.White,
                            disabledContainerColor = containerColor,
                            disabledContentColor = Color.White
                        ),
                        enabled = isAdmin && (!isLocked || isSelected),
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        Text(status.name, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Card(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF6A1B9A)), // Vibrant Purple
                elevation = CardDefaults.cardElevation(4.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("WINNING TEAM", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    if (isEditable) {
                        OutlinedTextField(
                            value = winningTeamTFV,
                            onValueChange = {
                                winningTeamTFV = it
                                updateState(matchState.copy(winningTeam = it.text))
                            },
                            placeholder = { Text("Which team won?", color = Color.LightGray) },
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White, focusedBorderColor = Color.White, unfocusedBorderColor = Color.LightGray)
                        )
                    } else {
                        Text(text = matchState.winningTeam.ifEmpty { "Match in progress" }, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Card(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1565C0)), // Vibrant Blue
                elevation = CardDefaults.cardElevation(4.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("TARGET / CHASE", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    if (isEditable) {
                        OutlinedTextField(
                            value = targetChaseTFV,
                            onValueChange = {
                                targetChaseTFV = it
                                updateState(matchState.copy(targetChase = it.text))
                            },
                            placeholder = { Text("target/chase", color = Color.LightGray) },
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White, focusedBorderColor = Color.White, unfocusedBorderColor = Color.LightGray)
                        )
                    } else {
                        Text(text = matchState.targetChase.ifEmpty { "Not set" }, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Card(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFBF360C)), // Vibrant Orange/Red
                elevation = CardDefaults.cardElevation(4.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("TOTAL OVERS GOAL", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    if (isEditable) {
                        OutlinedTextField(
                            value = totalOversTFV,
                            onValueChange = { 
                                totalOversTFV = it
                                updateState(matchState.copy(totalOvers = it.text)) 
                            },
                            placeholder = { Text("e.g. 20", color = Color.LightGray) },
                            modifier = Modifier.fillMaxWidth(),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White, focusedBorderColor = Color.White, unfocusedBorderColor = Color.LightGray)
                        )
                    } else {
                        Text(text = matchState.totalOvers.ifEmpty { "Not set" }, fontSize = 24.sp, fontWeight = FontWeight.ExtraBold, color = Color.White)
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Team Sections
            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                TeamManagementBlock(
                    label = "TEAM A",
                    nameTFV = teamANameTFV,
                    nameString = matchState.teamAName,
                    isAdmin = isAdmin,
                    isEditable = isEditable,
                    containerColor = Color(0xFFAD1457), // Magenta
                    onNameChange = { 
                        teamANameTFV = it
                        updateState(matchState.copy(teamAName = it.text)) 
                    },
                    batting = matchState.teamABattingSelected,
                    bowling = matchState.teamABowlingSelected,
                    onBattingClick = { updateState(matchState.copy(teamABattingSelected = true, teamABowlingSelected = false)) },
                    onBowlingClick = { updateState(matchState.copy(teamABattingSelected = false, teamABowlingSelected = true)) },
                    score = matchState.teamAScore,
                    oversElapsed = matchState.teamAOversElapsed,
                    wickets = matchState.teamAWickets,
                    onOversUpdate = { updateState(matchState.copy(teamAOversElapsed = it)) },
                    onWicketsUpdate = { updateState(matchState.copy(teamAWickets = it)) },
                    p1 = matchState.playersA.find { it.id == matchState.player1IdA },
                    p2 = matchState.playersA.find { it.id == matchState.player2IdA },
                    bowler = matchState.playersA.find { it.id == matchState.bowlerIdA },
                    allPlayers = matchState.playersA,
                    onP1Select = { p -> updateState(matchState.copy(player1IdA = p.id)) },
                    onP2Select = { p -> updateState(matchState.copy(player2IdA = p.id)) },
                    onBowlerSelect = { p -> updateState(matchState.copy(bowlerIdA = p.id)) },
                    onPlusClick = { pid, isBowler -> 
                        targetTeamForUpdate = "A"
                        activePlayerId = pid
                        isActivePlayerBowler = isBowler
                        showPlusPopup = true 
                    },
                    onTableAdd = {
                        targetTeamForUpdate = "A"
                        showPlayerDialog = true to CricketPlayerStats(id = UUID.randomUUID().toString())
                    },
                    onTableEdit = { p ->
                        targetTeamForUpdate = "A"
                        showPlayerDialog = true to p
                    },
                    manOfTheMatch = matchState.manOfTheMatchA,
                    onSelectMoM = { updateState(matchState.copy(manOfTheMatchA = it)) }
                )

                Spacer(modifier = Modifier.height(32.dp))

                TeamManagementBlock(
                    label = "TEAM B",
                    nameTFV = teamBNameTFV,
                    nameString = matchState.teamBName,
                    isAdmin = isAdmin,
                    isEditable = isEditable,
                    containerColor = Color(0xFFE65100), // Orange
                    onNameChange = { 
                        teamBNameTFV = it
                        updateState(matchState.copy(teamBName = it.text)) 
                    },
                    batting = matchState.teamBBattingSelected,
                    bowling = matchState.teamBBowlingSelected,
                    onBattingClick = { updateState(matchState.copy(teamBBattingSelected = true, teamBBowlingSelected = false)) },
                    onBowlingClick = { updateState(matchState.copy(teamBBattingSelected = false, teamBBowlingSelected = true)) },
                    score = matchState.teamBScore,
                    oversElapsed = matchState.teamBOversElapsed,
                    wickets = matchState.teamBWickets,
                    onOversUpdate = { updateState(matchState.copy(teamBOversElapsed = it)) },
                    onWicketsUpdate = { updateState(matchState.copy(teamBWickets = it)) },
                    p1 = matchState.playersB.find { it.id == matchState.player1IdB },
                    p2 = matchState.playersB.find { it.id == matchState.player2IdB },
                    bowler = matchState.playersB.find { it.id == matchState.bowlerIdB },
                    allPlayers = matchState.playersB,
                    onP1Select = { p -> updateState(matchState.copy(player1IdB = p.id)) },
                    onP2Select = { p -> updateState(matchState.copy(player2IdB = p.id)) },
                    onBowlerSelect = { p -> updateState(matchState.copy(bowlerIdB = p.id)) },
                    onPlusClick = { pid, isBowler -> 
                        targetTeamForUpdate = "B"
                        activePlayerId = pid
                        isActivePlayerBowler = isBowler
                        showPlusPopup = true 
                    },
                    onTableAdd = {
                        targetTeamForUpdate = "B"
                        showPlayerDialog = true to CricketPlayerStats(id = UUID.randomUUID().toString())
                    },
                    onTableEdit = { p ->
                        targetTeamForUpdate = "B"
                        showPlayerDialog = true to p
                    },
                    manOfTheMatch = matchState.manOfTheMatchB,
                    onSelectMoM = { updateState(matchState.copy(manOfTheMatchB = it)) }
                )
            }
            Spacer(modifier = Modifier.height(100.dp))
        }
    }

    if (isEditable && showPlayerDialog.first) {
        val playerToEdit = showPlayerDialog.second
        var nameTFV by remember { mutableStateOf(TextFieldValue(playerToEdit?.name ?: "")) }
        AlertDialog(
            onDismissRequest = { showPlayerDialog = false to null },
            title = { Text(if (playerToEdit?.name?.isEmpty() == true) "New Player" else "Edit Player Name") },
            text = { OutlinedTextField(value = nameTFV, onValueChange = { nameTFV = it }, label = { Text("Name") }, shape = RoundedCornerShape(8.dp)) },
            confirmButton = {
                Button(onClick = {
                    val isA = targetTeamForUpdate == "A"
                    val currentList = (if (isA) matchState.playersA else matchState.playersB).toMutableList()
                    val idx = currentList.indexOfFirst { it.id == playerToEdit?.id }
                    if (idx != -1) currentList[idx] = currentList[idx].copy(name = nameTFV.text)
                    else if (playerToEdit != null) currentList.add(playerToEdit.copy(name = nameTFV.text))
                    
                    if (isA) updateState(matchState.copy(playersA = currentList))
                    else updateState(matchState.copy(playersB = currentList))
                    showPlayerDialog = false to null
                }, colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32))) { Text("Save") }
            }
        )
    }

    if (isEditable && showPlusPopup) {
        val pid = activePlayerId!!
        val pool = if (targetTeamForUpdate == "A") matchState.playersA else matchState.playersB
        val currentPlayer = pool.find { it.id == pid }

        AlertDialog(
            onDismissRequest = { 
                showPlusPopup = false
                showStatusSubMenu = false
            },
            title = { Text(if (isActivePlayerBowler) "Update Bowler Wickets" else "Update Player Status") },
            text = {
                Column {
                    if (isActivePlayerBowler) {
                        var wicketTFV by remember { mutableStateOf(TextFieldValue(currentPlayer?.wickets?.toString() ?: "0")) }
                        OutlinedTextField(
                            value = wicketTFV,
                            onValueChange = { wicketTFV = it },
                            label = { Text("Number of Wickets Taken") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.Black, unfocusedTextColor = Color.Black)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Button(
                            onClick = {
                                val wicks = wicketTFV.text.toIntOrNull() ?: 0
                                updateTeamPlayerStat(matchState, targetTeamForUpdate, pid, "set_wickets", ::updateState, wicks)
                                showPlusPopup = false
                            },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1B5E20))
                        ) {
                            Text("Update Wickets")
                        }
                    } else {
                        if (!showStatusSubMenu) {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Button(onClick = { updateTeamPlayerStat(matchState, targetTeamForUpdate, pid, "1s", ::updateState) }, modifier = Modifier.weight(1f), colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50))) { Text("+1") }
                                Button(onClick = { updateTeamPlayerStat(matchState, targetTeamForUpdate, pid, "2s", ::updateState) }, modifier = Modifier.weight(1f), colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2196F3))) { Text("+2") }
                                Button(onClick = { updateTeamPlayerStat(matchState, targetTeamForUpdate, pid, "3s", ::updateState) }, modifier = Modifier.weight(1f), colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFC107))) { Text("+3") }
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Button(onClick = { updateTeamPlayerStat(matchState, targetTeamForUpdate, pid, "4s", ::updateState) }, modifier = Modifier.weight(1f), colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF689F38))) { Text("4'S") }
                                Button(onClick = { updateTeamPlayerStat(matchState, targetTeamForUpdate, pid, "6s", ::updateState) }, modifier = Modifier.weight(1f), colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF689F38))) { Text("6'S") }
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Button(onClick = { updateTeamPlayerStat(matchState, targetTeamForUpdate, pid, "0s", ::updateState) }, modifier = Modifier.fillMaxWidth(), colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF9800))) { Text("0'S") }
                            Spacer(modifier = Modifier.height(8.dp))
                            Button(onClick = { showStatusSubMenu = true }, modifier = Modifier.fillMaxWidth(), colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF455A64))) { Text("STATUS") }
                        } else {
                            Button(onClick = { updateTeamPlayerStat(matchState, targetTeamForUpdate, pid, "out", ::updateState); showPlusPopup = false; showStatusSubMenu = false }, modifier = Modifier.fillMaxWidth(), colors = ButtonDefaults.buttonColors(containerColor = Color.Red)) { Text("OUT", color = Color.White) }
                            Spacer(modifier = Modifier.height(8.dp))
                            Button(onClick = { updateTeamPlayerStat(matchState, targetTeamForUpdate, pid, "not_out", ::updateState); showPlusPopup = false; showStatusSubMenu = false }, modifier = Modifier.fillMaxWidth(), colors = ButtonDefaults.buttonColors(containerColor = Color.Green)) { Text("NOT OUT", color = Color.White) }
                        }
                    }
                }
            },
            confirmButton = {}
        )
    }
}

@Composable
fun TeamManagementBlock(
    label: String,
    nameTFV: TextFieldValue,
    nameString: String,
    isAdmin: Boolean,
    isEditable: Boolean,
    containerColor: Color,
    onNameChange: (TextFieldValue) -> Unit,
    batting: Boolean,
    bowling: Boolean,
    onBattingClick: () -> Unit,
    onBowlingClick: () -> Unit,
    score: Int,
    oversElapsed: String,
    wickets: Int,
    onOversUpdate: (String) -> Unit,
    onWicketsUpdate: (Int) -> Unit,
    p1: CricketPlayerStats?,
    p2: CricketPlayerStats?,
    bowler: CricketPlayerStats?,
    allPlayers: List<CricketPlayerStats>,
    onP1Select: (CricketPlayerStats) -> Unit,
    onP2Select: (CricketPlayerStats) -> Unit,
    onBowlerSelect: (CricketPlayerStats) -> Unit,
    onPlusClick: (String, Boolean) -> Unit,
    onTableAdd: () -> Unit,
    onTableEdit: (CricketPlayerStats) -> Unit,
    manOfTheMatch: String,
    onSelectMoM: (String) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
        colors = CardDefaults.cardColors(containerColor = containerColor),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("$label NAME", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
            OutlinedTextField(
                value = nameTFV, 
                onValueChange = onNameChange, 
                modifier = Modifier.fillMaxWidth(), 
                readOnly = !isEditable,
                placeholder = { Text("Enter Team Name", color = Color.LightGray) },
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White, focusedBorderColor = Color.White, unfocusedBorderColor = Color.White)
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Row(modifier = Modifier.fillMaxWidth()) {
                Button(onClick = { if (isEditable) onBattingClick() }, modifier = Modifier.weight(1f), shape = RoundedCornerShape(12.dp), colors = ButtonDefaults.buttonColors(containerColor = if(batting) Color.Green else Color.DarkGray)) { Text("BATTING", color = Color.White) }
                Spacer(modifier = Modifier.width(12.dp))
                Button(onClick = { if (isEditable) onBowlingClick() }, modifier = Modifier.weight(1f), shape = RoundedCornerShape(12.dp), colors = ButtonDefaults.buttonColors(containerColor = if(bowling) Color.Blue else Color.DarkGray)) { Text("BOWLING", color = Color.White) }
            }

            if (batting || bowling) {
                Spacer(modifier = Modifier.height(20.dp))
                Column(modifier = Modifier.fillMaxWidth()) {
                    // SCORE CARD
                    Card(modifier = Modifier.fillMaxWidth().height(120.dp), colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.15f)), elevation = CardDefaults.cardElevation(4.dp), shape = RoundedCornerShape(16.dp)) {
                        Column(Modifier.fillMaxSize(), Arrangement.Center, Alignment.CenterHorizontally) {
                            Text("SCORE", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
                            Text("$score", fontSize = 40.sp, fontWeight = FontWeight.Black, color = Color.White)
                        }
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    // OVERS / WICKETS CARD - NOW BELOW SCORE
                    Card(modifier = Modifier.fillMaxWidth().height(120.dp), colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.15f)), elevation = CardDefaults.cardElevation(4.dp), shape = RoundedCornerShape(16.dp)) {
                        Column(Modifier.fillMaxSize().padding(8.dp), Arrangement.Center, Alignment.CenterHorizontally) {
                            Text("OVERS / WICKETS", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.White)
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                if (isEditable) {
                                    IconButton(onClick = { onOversUpdate(incrementOver(oversElapsed)) }, modifier = Modifier.size(28.dp)) { Icon(Icons.Default.Add, contentDescription = null, tint = Color.Green) }
                                    IconButton(onClick = { onOversUpdate(decrementOver(oversElapsed)) }, modifier = Modifier.size(28.dp)) { Icon(Icons.Default.Remove, contentDescription = null, tint = Color.Red) }
                                }
                                Text(oversElapsed, fontSize = 20.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 2.dp), color = Color.White)
                                Text("/", fontSize = 20.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 4.dp), color = Color.White)
                                if (isEditable) {
                                    IconButton(onClick = { onWicketsUpdate(wickets + 1) }, modifier = Modifier.size(28.dp)) { Icon(Icons.Default.Add, contentDescription = null, tint = Color.Green) }
                                    IconButton(onClick = { if (wickets > 0) onWicketsUpdate(wickets - 1) }, modifier = Modifier.size(28.dp)) { Icon(Icons.Default.Remove, contentDescription = null, tint = Color.Red) }
                                }
                                Text("$wickets", fontSize = 20.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 2.dp), color = Color.White)
                            }
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(20.dp))
                
                if (batting) {
                    AdminPlayerRow("Player 1", p1, allPlayers, isEditable, onP1Select) { if(p1 != null) onPlusClick(p1.id, false) }
                    Spacer(Modifier.height(8.dp))
                    AdminPlayerRow("Player 2", p2, allPlayers, isEditable, onP2Select) { if(p2 != null) onPlusClick(p2.id, false) }
                }
                
                if (bowling) {
                    Spacer(Modifier.height(8.dp))
                    AdminPlayerRow("Current Bowler", bowler, allPlayers, isEditable, onBowlerSelect) { if(bowler != null) onPlusClick(bowler.id, true) }
                }
                
                Spacer(modifier = Modifier.height(24.dp))
                Text("PLAYERS PERFORMANCE TABLE", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
                TableAdminUI(players = allPlayers, isEditable = isEditable, onHeaderClick = onTableAdd, onNameClick = onTableEdit, onSelectAsSlot = { player -> if (bowling) onBowlerSelect(player) else onP1Select(player) })

                Spacer(modifier = Modifier.height(16.dp))
                Text("MAN OF THE MATCH", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
                var expanded by remember { mutableStateOf(false) }
                Box(modifier = Modifier.fillMaxWidth().padding(top = 4.dp)) {
                    OutlinedTextField(
                        value = manOfTheMatch,
                        onValueChange = {},
                        readOnly = true,
                        enabled = false,
                        modifier = Modifier.fillMaxWidth().clickable { if (isEditable) expanded = true },
                        colors = OutlinedTextFieldDefaults.colors(disabledTextColor = Color.White, disabledBorderColor = Color.White),
                        placeholder = { Text("Select Player", color = Color.White) }
                    )
                    if (isEditable) {
                        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                            allPlayers.forEach { p ->
                                DropdownMenuItem(text = { Text(p.name) }, onClick = { onSelectMoM(p.name); expanded = false })
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AdminPlayerRow(label: String, p: CricketPlayerStats?, pool: List<CricketPlayerStats>, isEditable: Boolean, onSelect: (CricketPlayerStats) -> Unit, onPlus: () -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    Card(modifier = Modifier.fillMaxWidth().clickable(enabled = isEditable) { expanded = true }, shape = RoundedCornerShape(12.dp), colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.1f)), elevation = CardDefaults.cardElevation(2.dp)) {
        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f)) {
                Text(label.uppercase(), fontSize = 10.sp, color = Color.LightGray, fontWeight = FontWeight.Bold)
                Text(text = p?.name ?: if (isEditable) "Select $label from table below" else "Not Selected", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = if (p == null) Color.Gray else Color.White)
                if (isEditable) {
                    DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                        pool.forEach { opt -> DropdownMenuItem(text = { Text(opt.name) }, onClick = { onSelect(opt); expanded = false }) }
                    }
                }
            }
            if (isEditable) {
                IconButton(onClick = { onPlus() }, modifier = Modifier.background(Color.White.copy(alpha = 0.2f), RoundedCornerShape(8.dp))) { Icon(Icons.Default.Add, contentDescription = "Add Stats", tint = Color.Green) }
            }
        }
    }
}

@Composable
fun TableAdminUI(players: List<CricketPlayerStats>, isEditable: Boolean, onHeaderClick: () -> Unit, onNameClick: (CricketPlayerStats) -> Unit, onSelectAsSlot: (CricketPlayerStats) -> Unit) {
    Card(modifier = Modifier.fillMaxWidth().padding(top = 8.dp), shape = RoundedCornerShape(12.dp), elevation = CardDefaults.cardElevation(4.dp), colors = CardDefaults.cardColors(containerColor = Color.Black.copy(alpha = 0.3f))) {
        Column {
            Row(modifier = Modifier.background(Color.DarkGray).padding(10.dp)) {
                Text("PLAYERS", modifier = Modifier.weight(2.5f).then(if (isEditable) Modifier.clickable { onHeaderClick() } else Modifier), fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Color.White)
                listOf("B", "4s", "6s", "W", "TOT").forEach { Text(it, modifier = Modifier.weight(1f), textAlign = TextAlign.Center, fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Color.White) }
            }
            players.forEach { p ->
                val statusColor = when(p.status) {
                    PlayerStatus.OUT -> Color.Red
                    PlayerStatus.NOT_OUT -> Color.Green
                    else -> Color.White
                }
                HorizontalDivider(thickness = 0.5.dp, color = Color.Gray)
                Row(modifier = Modifier.padding(vertical = 12.dp, horizontal = 10.dp).clickable(enabled = isEditable) { onNameClick(p) }) {
                    Text(p.name, modifier = Modifier.weight(2.5f).clickable(enabled = isEditable) { onSelectAsSlot(p) }, color = statusColor, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    Text("${p.balls}", modifier = Modifier.weight(1f), textAlign = TextAlign.Center, fontSize = 14.sp, color = Color.White)
                    Text("${p.fours}", modifier = Modifier.weight(1f), textAlign = TextAlign.Center, fontSize = 14.sp, color = Color.White)
                    Text("${p.sixes}", modifier = Modifier.weight(1f), textAlign = TextAlign.Center, fontSize = 14.sp, color = Color.White)
                    Text("${p.wickets}", modifier = Modifier.weight(1f), textAlign = TextAlign.Center, fontSize = 14.sp, color = Color.Yellow, fontWeight = FontWeight.Bold)
                    Text("${p.totalRuns + p.extras}", modifier = Modifier.weight(1f), textAlign = TextAlign.Center, fontSize = 14.sp, fontWeight = FontWeight.Black, color = Color.White)
                }
            }
        }
    }
}

fun incrementOver(current: String): String {
    val parts = current.split(".")
    var overs = parts[0].toIntOrNull() ?: 0
    var balls = if (parts.size > 1) parts[1].toIntOrNull() ?: 0 else 0
    balls++
    if (balls > 6) { overs++; balls = 1 }
    return "$overs.$balls"
}

fun decrementOver(current: String): String {
    val parts = current.split(".")
    var overs = parts[0].toIntOrNull() ?: 0
    var balls = if (parts.size > 1) parts[1].toIntOrNull() ?: 0 else 0
    if (balls <= 1) { if (overs > 0) { overs--; balls = 6 } else { overs = 0; balls = 0 } } else { balls-- }
    return "$overs.$balls"
}

fun updateTeamPlayerStat(state: CricketMatchState, team: String, pid: String, type: String, update: (CricketMatchState) -> Unit, value: Int = 0) {
    val isTeamA = team == "A"; val pool = if (isTeamA) state.playersA else state.playersB; val oppPool = if (isTeamA) state.playersB else state.playersA
    val inMainPool = pool.any { it.id == pid }
    if (inMainPool) {
        var incrementWickets = false
        var incrementBalls = false
        val newList = pool.map { p ->
            if (p.id == pid) {
                when(type) {
                    "1s" -> { incrementBalls = true; p.copy(totalRuns = p.totalRuns + 1, balls = p.balls + 1) }
                    "2s" -> { incrementBalls = true; p.copy(totalRuns = p.totalRuns + 2, balls = p.balls + 1) }
                    "3s" -> { incrementBalls = true; p.copy(totalRuns = p.totalRuns + 3, balls = p.balls + 1) }
                    "4s" -> { incrementBalls = true; p.copy(fours = p.fours + 1, totalRuns = p.totalRuns + 4, balls = p.balls + 1) }
                    "6s" -> { incrementBalls = true; p.copy(sixes = p.sixes + 1, totalRuns = p.totalRuns + 6, balls = p.balls + 1) }
                    "0s" -> { incrementBalls = true; p.copy(balls = p.balls + 1) }
                    "set_wickets" -> p.copy(wickets = value)
                    "out" -> {
                        if (p.status != PlayerStatus.OUT) {
                            incrementWickets = true
                            incrementBalls = true
                        }
                        p.copy(status = PlayerStatus.OUT, balls = p.balls + 1)
                    }
                    "not_out" -> p.copy(status = PlayerStatus.NOT_OUT)
                    else -> p
                }
            } else p
        }
        var s = 0; newList.forEach { s += (it.totalRuns + it.extras) }
        
        var nextState = state
        if (isTeamA) {
            nextState = nextState.copy(playersA = newList, teamAScore = s)
            if (incrementWickets && state.teamABattingSelected) nextState = nextState.copy(teamAWickets = state.teamAWickets + 1)
            if (incrementBalls && state.teamABattingSelected) nextState = nextState.copy(teamAOversElapsed = incrementOver(state.teamAOversElapsed))
        } else {
            nextState = nextState.copy(playersB = newList, teamBScore = s)
            if (incrementWickets && state.teamBBattingSelected) nextState = nextState.copy(teamBWickets = state.teamBWickets + 1)
            if (incrementBalls && state.teamBBattingSelected) nextState = nextState.copy(teamBOversElapsed = incrementOver(state.teamBOversElapsed))
        }
        update(nextState)
    } else {
        val newList = oppPool.map { p -> if (p.id == pid && type == "set_wickets") p.copy(wickets = value) else p }
        if (isTeamA) update(state.copy(playersB = newList)) else update(state.copy(playersA = newList))
    }
}
