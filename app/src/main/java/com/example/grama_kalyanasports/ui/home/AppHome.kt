package com.example.grama_kalyanasports.ui.home

import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale

import com.example.grama_kalyanasports.ui.navigation.Screen
import com.example.grama_kalyanasports.data.TournamentRepository
import com.example.grama_kalyanasports.data.SportType
import com.example.grama_kalyanasports.data.MatchStatus
import com.example.grama_kalyanasports.data.Tournament
import com.example.grama_kalyanasports.R

fun isTournamentTimeReached(dateStr: String, timeStr: String): Boolean {
    return try {
        val format = SimpleDateFormat("d/M/yyyy h:mm a", Locale.getDefault())
        val date = format.parse("$dateStr $timeStr")
        date != null && System.currentTimeMillis() >= date.time
    } catch (e: Exception) {
        false
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppHome(
    username: String = "",
    email: String = "",
    onNavigate: (Screen) -> Unit
) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                drawerContainerColor = Color.White,
                drawerShape = RoundedCornerShape(0.dp),
                modifier = Modifier.width(320.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                ) {
                    Spacer(modifier = Modifier.height(48.dp))
                    
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFE91E63)),
                        shape = RoundedCornerShape(24.dp),
                        modifier = Modifier.fillMaxWidth(),
                        elevation = CardDefaults.cardElevation(8.dp)
                    ) {
                        Column(modifier = Modifier.padding(24.dp).fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "grama kalyana sports",
                                fontSize = 22.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color.White,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    Box(modifier = Modifier.fillMaxWidth().height(2.dp).background(Color.Black))
                    
                    if (username.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(16.dp))
                        Surface(
                            color = Color(0xFFFFF0F5),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.Star, contentDescription = null, tint = Color(0xFFE91E63), modifier = Modifier.size(24.dp))
                                Spacer(Modifier.width(8.dp))
                                Text(
                                    text = "Welcome, $username",
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Color(0xFFD81B60)
                                )
                            }
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(24.dp))
                    
                    NavigationDrawerItem(
                        label = { Text("Home", fontSize = 18.sp, fontWeight = FontWeight.Bold) },
                        selected = false,
                        onClick = { 
                            scope.launch { drawerState.close() }
                            onNavigate(Screen.Home(username, email)) 
                        },
                        colors = NavigationDrawerItemDefaults.colors(
                            unselectedContainerColor = Color(0xFF81D4FA),
                            unselectedTextColor = Color.Black
                        ),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.padding(vertical = 4.dp)
                    )

                    NavigationDrawerItem(
                        label = { Text("Register for Game", fontSize = 18.sp, fontWeight = FontWeight.Bold) },
                        selected = false,
                        onClick = { 
                            scope.launch { drawerState.close() }
                            onNavigate(Screen.RegisterTournament(username, email))
                        },
                        colors = NavigationDrawerItemDefaults.colors(
                            unselectedContainerColor = Color(0xFFA5D6A7),
                            unselectedTextColor = Color.Black
                        ),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.padding(vertical = 4.dp)
                    )
                }
            }
        }
    ) {
        Scaffold(
            containerColor = Color(0xFFF3E5F5), // Light Purple Background
            topBar = {
                TopAppBar(
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color(0xFFF3E5F5)
                    ),
                    title = { 
                        Text(
                            "Home", 
                            fontSize = 48.sp, 
                            fontWeight = FontWeight.Black, 
                            color = Color(0xFFE65100),
                            letterSpacing = 1.sp
                        ) 
                    },
                    navigationIcon = {
                        IconButton(
                            onClick = { scope.launch { drawerState.open() } },
                            modifier = Modifier.padding(start = 8.dp)
                        ) {
                            Column(
                                modifier = Modifier.size(40.dp),
                                verticalArrangement = Arrangement.Center,
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Box(modifier = Modifier.width(32.dp).height(5.dp).background(Color.Black, RoundedCornerShape(2.dp)))
                                Spacer(modifier = Modifier.height(6.dp))
                                Box(modifier = Modifier.width(32.dp).height(5.dp).background(Color.Black, RoundedCornerShape(2.dp)))
                                Spacer(modifier = Modifier.height(6.dp))
                                Box(modifier = Modifier.width(32.dp).height(5.dp).background(Color.Black, RoundedCornerShape(2.dp)))
                            }
                        }
                    },
                    actions = {
                        IconButton(onClick = { onNavigate(Screen.Profile) }) {
                            Icon(
                                Icons.Default.AccountCircle,
                                contentDescription = "Profile",
                                modifier = Modifier.size(40.dp),
                                tint = Color(0xFFE65100)
                            )
                        }
                    }
                )
            }
        ) { innerPadding ->
            HomeScreenContent(modifier = Modifier.padding(innerPadding), onNavigate = onNavigate)
        }
    }
}

@Composable
fun MovingSportsRow() {
    val sports = listOf("Cricket", "Volleyball", "Kabaddi", "Cricket", "Volleyball", "Kabaddi")
    val scrollState = rememberScrollState()
    
    LaunchedEffect(Unit) {
        while (true) {
            scrollState.animateScrollTo(
                value = scrollState.maxValue,
                animationSpec = tween(durationMillis = 20000, easing = LinearEasing)
            )
            scrollState.scrollTo(0)
        }
    }

    Column {
        Text("Our Sports", fontSize = 22.sp, fontWeight = FontWeight.ExtraBold, color = Color.DarkGray)
        Spacer(modifier = Modifier.height(12.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(scrollState, enabled = false)
        ) {
            sports.forEach { sport ->
                Card(
                    modifier = Modifier
                        .width(280.dp)
                        .height(180.dp)
                        .padding(end = 16.dp),
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(10.dp)
                ) {
                    Box(modifier = Modifier.fillMaxSize()) {
                        val imageResource = when(sport) {
                            "Cricket" -> R.drawable.cricket
                            "Volleyball" -> R.drawable.volley_ball
                            "Kabaddi" -> R.drawable.kabaddi
                            else -> R.drawable.ic_launcher_background
                        }

                        Image(
                            painter = painterResource(id = imageResource),
                            contentDescription = sport,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop,
                            alpha = 0.7f
                        )
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(Color.Black.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = when(sport) {
                                        "Cricket" -> "🏏"
                                        "Volleyball" -> "🏐"
                                        "Kabaddi" -> "🏃"
                                        else -> "🏆"
                                    },
                                    fontSize = 52.sp
                                )
                                Text(
                                    sport.uppercase(), 
                                    fontWeight = FontWeight.Black, 
                                    color = Color.White, 
                                    fontSize = 28.sp
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun HomeScreenContent(modifier: Modifier = Modifier, onNavigate: (Screen) -> Unit) {
    val registeredTournaments by TournamentRepository.tournaments.collectAsState()
    var showRoleDialog by remember { mutableStateOf(false) }
    var selectedTournament by remember { mutableStateOf<Tournament?>(null) }
    var searchQuery by remember { mutableStateOf("") }

    val recentTournaments = registeredTournaments.filter { 
        val status = TournamentRepository.getTournamentStatus(it)
        status == MatchStatus.FINISHED || status == MatchStatus.CANCELED
    }

    val liveTournaments = registeredTournaments.filter { 
        val status = TournamentRepository.getTournamentStatus(it)
        status != MatchStatus.FINISHED && status != MatchStatus.CANCELED && isTournamentTimeReached(it.date, it.time)
    }

    val upcomingTournaments = registeredTournaments.filter { 
        val status = TournamentRepository.getTournamentStatus(it)
        status != MatchStatus.FINISHED && status != MatchStatus.CANCELED && !isTournamentTimeReached(it.date, it.time)
    }
    
    val searchResults = if (searchQuery.isNotEmpty()) {
        registeredTournaments.filter { it.name.startsWith(searchQuery, ignoreCase = true) }
    } else {
        emptyList()
    }

    if (showRoleDialog && selectedTournament != null) {
        Dialog(onDismissRequest = { showRoleDialog = false }) {
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                elevation = CardDefaults.cardElevation(8.dp)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("Select Role", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        "How do you want to continue for ${selectedTournament!!.name}?",
                        textAlign = TextAlign.Center,
                        color = Color.DarkGray
                    )
                    Spacer(modifier = Modifier.height(32.dp))
                    
                    // Big Green Button ABOVE Login
                    Button(
                        onClick = {
                            showRoleDialog = false
                            val tourney = selectedTournament!!
                            when {
                                tourney.sportType == SportType.CRICKET || tourney.type.equals("Cricket", ignoreCase = true) -> {
                                    onNavigate(Screen.CricketScorecard(tourney.name, isAdmin = false))
                                }
                                tourney.sportType == SportType.KABADDI || tourney.type.equals("Kabaddi", ignoreCase = true) -> {
                                    onNavigate(Screen.KabaddiScorecard(tourney.name, isAdmin = false))
                                }
                                else -> {
                                    onNavigate(Screen.VolleyballScorecard(tourney.name, isAdmin = false))
                                }
                            }
                        },
                        modifier = Modifier.fillMaxWidth().height(64.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Green),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Text("Continue as Guest", color = Color.Black, fontSize = 20.sp, fontWeight = FontWeight.ExtraBold)
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // Smaller Grey Button BELOW Guest
                    Button(
                        onClick = {
                            showRoleDialog = false
                            onNavigate(Screen.LeaderLogin)
                        },
                        modifier = Modifier.width(200.dp).height(44.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Gray),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Login as Admin", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Medium)
                    }
                }
            }
        }
    }

    LazyColumn(modifier = modifier.fillMaxSize().padding(16.dp)) {
        item {
            TextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Search Tournaments...") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp)
                    .height(64.dp),
                shape = RoundedCornerShape(32.dp),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    disabledContainerColor = Color.White,
                    cursorColor = Color(0xFFE65100),
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    focusedTextColor = Color.Black,
                    unfocusedTextColor = Color.Black
                ),
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = Color(0xFFE65100)) },
                singleLine = true
            )
            
            if (searchQuery.isNotEmpty()) {
                Text("Search Results", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.DarkGray)
                Spacer(modifier = Modifier.height(8.dp))
                if (searchResults.isEmpty()) {
                    Text("No tournaments found matching '$searchQuery'", color = Color.Gray, modifier = Modifier.padding(bottom = 16.dp))
                } else {
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(16.dp), modifier = Modifier.padding(bottom = 16.dp)) {
                        items(searchResults) { tourney ->
                            val status = TournamentRepository.getTournamentStatus(tourney)
                            val isLive = status != MatchStatus.FINISHED && status != MatchStatus.CANCELED && isTournamentTimeReached(tourney.date, tourney.time)
                            
                            val bgColor = when {
                                isLive -> Color(0xFF4CAF50)
                                status == MatchStatus.FINISHED -> Color.Red
                                status == MatchStatus.CANCELED -> Color.Black
                                else -> Color(0xFF81D4FA)
                            }
                            
                            val statusText = when {
                                isLive -> "LIVE NOW"
                                status == MatchStatus.FINISHED -> "FINISHED"
                                status == MatchStatus.CANCELED -> "CANCELLED"
                                else -> "UPCOMING"
                            }

                            Card(
                                modifier = Modifier.width(300.dp).height(140.dp),
                                onClick = { 
                                    selectedTournament = tourney
                                    showRoleDialog = true 
                                },
                                colors = CardDefaults.cardColors(containerColor = bgColor, contentColor = if(status == MatchStatus.CANCELED) Color.White else Color.Black),
                                elevation = CardDefaults.cardElevation(6.dp)
                            ) {
                                Column(modifier = Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.Center) {
                                    Text(tourney.name, fontSize = 26.sp, fontWeight = FontWeight.Black)
                                    Text("${tourney.type.uppercase()} - $statusText", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }
        }

        item {
            MovingSportsRow()
            Spacer(modifier = Modifier.height(32.dp))
        }

        item {
            Text("Live Matches", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Color.DarkGray)
            Spacer(modifier = Modifier.height(8.dp))
            if (liveTournaments.isEmpty()) {
                Text("No live matches at the moment", modifier = Modifier.padding(vertical = 16.dp), color = Color.Gray)
            } else {
                LazyRow(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    items(liveTournaments) { tourney ->
                        Card(
                            modifier = Modifier.width(300.dp).height(140.dp),
                            onClick = { selectedTournament = tourney; showRoleDialog = true },
                            colors = CardDefaults.cardColors(
                                containerColor = Color(0xFF4CAF50), 
                                contentColor = Color.Black
                            ),
                            elevation = CardDefaults.cardElevation(6.dp)
                        ) {
                            Column(modifier = Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.Center) {
                                Text(tourney.name, fontSize = 26.sp, fontWeight = FontWeight.Black)
                                Text("${tourney.type.uppercase()} - LIVE NOW", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(32.dp))
        }

        item {
            Text("Upcoming Tournaments", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Color.DarkGray)
            Spacer(modifier = Modifier.height(8.dp))
            if (upcomingTournaments.isEmpty()) {
                Text("No upcoming tournaments", modifier = Modifier.padding(vertical = 16.dp), color = Color.Gray)
            } else {
                LazyRow(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    items(upcomingTournaments) { tourney ->
                        Card(
                            modifier = Modifier.width(300.dp),
                            onClick = { selectedTournament = tourney; showRoleDialog = true },
                            colors = CardDefaults.cardColors(
                                containerColor = Color(0xFF81D4FA),
                                contentColor = Color.Black
                            ),
                            elevation = CardDefaults.cardElevation(6.dp)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(tourney.name, fontSize = 22.sp, fontWeight = FontWeight.Black)
                                Text("Game: ${tourney.type}", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                                Text("Date: ${tourney.date}", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                                Text("Time: ${tourney.time}", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(32.dp))
        }
        
        item {
            Text("Recent Matches", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Color.DarkGray)
            Spacer(modifier = Modifier.height(8.dp))
            if (recentTournaments.isEmpty()) {
                Text("No recent matches", modifier = Modifier.padding(vertical = 16.dp), color = Color.Gray)
            } else {
                LazyRow(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    items(recentTournaments) { tourney -> 
                        val status = TournamentRepository.getTournamentStatus(tourney)
                        val bgColor = if (status == MatchStatus.CANCELED) Color.Black else Color.Red
                        val textColor = if (status == MatchStatus.CANCELED) Color.White else Color.Black
                        val statusText = if (status == MatchStatus.CANCELED) "CANCELLED" else "FINISHED"
                        
                        Card(
                            modifier = Modifier.width(300.dp),
                            onClick = { selectedTournament = tourney; showRoleDialog = true },
                            colors = CardDefaults.cardColors(
                                containerColor = bgColor,
                                contentColor = textColor
                            ),
                            elevation = CardDefaults.cardElevation(6.dp)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(tourney.name, fontSize = 22.sp, fontWeight = FontWeight.Black)
                                Text("Game: ${tourney.type}", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                                Text("Status: $statusText", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}
