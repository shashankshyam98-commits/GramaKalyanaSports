package com.example.grama_kalyanasports.ui.admin

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun GameSelectionScreen(
    tournamentName: String,
    onGameSelected: (String) -> Unit,
    onBack: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Select Sport for",
            fontSize = 20.sp,
            color = MaterialTheme.colorScheme.secondary
        )
        Text(
            text = tournamentName,
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(bottom = 48.dp)
        )

        // Stacked Buttons
        Button(
            onClick = { onGameSelected("Cricket") },
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp)
        ) {
            Text("CRICKET", fontSize = 18.sp, fontWeight = FontWeight.Bold)
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Button(
            onClick = { onGameSelected("Volleyball") },
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp)
        ) {
            Text("VOLLEYBALL", fontSize = 18.sp, fontWeight = FontWeight.Bold)
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Button(
            onClick = { onGameSelected("Kabaddi") },
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp)
        ) {
            Text("KABADDI", fontSize = 18.sp, fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(48.dp))

        TextButton(onClick = onBack) {
            Text("Back to Home")
        }
    }
}
