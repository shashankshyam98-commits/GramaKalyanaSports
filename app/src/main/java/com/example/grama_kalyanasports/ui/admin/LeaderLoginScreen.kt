package com.example.grama_kalyanasports.ui.admin

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.grama_kalyanasports.data.TournamentRepository

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LeaderLoginScreen(
    onLoginSuccess: (String) -> Unit,
    onBack: () -> Unit
) {
    var tournamentName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8F9FA))
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            "Admin Login", 
            fontSize = 32.sp, 
            fontWeight = FontWeight.Black,
            color = Color(0xFF212529)
        )
        Spacer(modifier = Modifier.height(40.dp))
        
        // 1. Tournament Name Card (Light Blue)
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFE3F2FD)),
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(4.dp)
        ) {
            OutlinedTextField(
                value = tournamentName,
                onValueChange = { tournamentName = it },
                label = { Text("Tournament Name") },
                modifier = Modifier.fillMaxWidth().padding(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    focusedTextColor = Color.Black,
                    unfocusedTextColor = Color.Black
                )
            )
        }
        Spacer(modifier = Modifier.height(16.dp))

        // 2. Email Card (Light Pink)
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFFCE4EC)),
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(4.dp)
        ) {
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Admin Email") },
                modifier = Modifier.fillMaxWidth().padding(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    focusedTextColor = Color.Black,
                    unfocusedTextColor = Color.Black
                )
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        
        // 3. Password Card (Light Orange/Amber)
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF3E0)),
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(4.dp)
        ) {
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Admin Password") },
                modifier = Modifier.fillMaxWidth().padding(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    focusedTextColor = Color.Black,
                    unfocusedTextColor = Color.Black
                )
            )
        }
        
        Spacer(modifier = Modifier.height(48.dp))
        
        if (isLoading) {
            CircularProgressIndicator(color = Color(0xFF1B5E20))
        } else {
            Button(
                onClick = {
                    isLoading = true
                    TournamentRepository.verifyTournamentLeader(tournamentName, email, password) { success ->
                        isLoading = false
                        if (success) {
                            onLoginSuccess(tournamentName)
                        } else {
                            Toast.makeText(context, "Invalid Name, Email or Password", Toast.LENGTH_SHORT).show()
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1B5E20))
            ) {
                Text("LOGIN", fontSize = 18.sp, fontWeight = FontWeight.Bold)
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        TextButton(onClick = onBack) {
            Text("Back to Home", color = Color.Gray)
        }
    }
}
