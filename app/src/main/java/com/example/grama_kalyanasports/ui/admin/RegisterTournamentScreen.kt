package com.example.grama_kalyanasports.ui.admin

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.grama_kalyanasports.data.SportType
import com.example.grama_kalyanasports.data.Tournament
import com.example.grama_kalyanasports.data.TournamentRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterTournamentScreen(
    initialUsername: String = "",
    initialEmail: String = "",
    onRegistrationSuccess: (String) -> Unit,
    onBack: () -> Unit
) {
    var username by remember { mutableStateOf(initialUsername) }
    var email by remember { mutableStateOf(initialEmail) }
    var tournamentName by remember { mutableStateOf("") }
    var gameType by remember { mutableStateOf("Kabaddi") }
    
    val now = Calendar.getInstance()
    var date by remember { mutableStateOf("${now.get(Calendar.DAY_OF_MONTH)}/${now.get(Calendar.MONTH) + 1}/${now.get(Calendar.YEAR)}") }
    
    val currentHour = now.get(Calendar.HOUR_OF_DAY)
    val amPmInit = if (currentHour < 12) "AM" else "PM"
    val hourInit = if (currentHour % 12 == 0) 12 else currentHour % 12
    var time by remember { mutableStateOf(String.format(Locale.getDefault(), "%02d:%02d %s", hourInit, now.get(Calendar.MINUTE), amPmInit)) }
    
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    
    val scope = rememberCoroutineScope()
    val tournamentNameFocusRequester = remember { FocusRequester() }
    val context = LocalContext.current
    val calendar = Calendar.getInstance()

    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }
    var dropdownExpanded by remember { mutableStateOf(false) }

    val datePickerDialog = DatePickerDialog(
        context, { _, y, m, d -> date = "$d/${m + 1}/$y" },
        calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)
    )

    val timePickerDialog = TimePickerDialog(
        context, { _, h, min ->
            val ap = if (h < 12) "AM" else "PM"
            val hh = if (h % 12 == 0) 12 else h % 12
            time = String.format(Locale.getDefault(), "%02d:%02d %s", hh, min, ap)
        },
        calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), false
    )

    LaunchedEffect(Unit) {
        if (username.isNotEmpty()) tournamentNameFocusRequester.requestFocus()
    }

    Box(modifier = Modifier.fillMaxSize().background(Color(0xFFFFF9C4))) {
        Column(
            modifier = Modifier.fillMaxSize().padding(16.dp).verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(24.dp))
            
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFFFF9800)),
                shape = RoundedCornerShape(12.dp),
                elevation = CardDefaults.cardElevation(4.dp)
            ) {
                Text(
                    "Register Tournament", 
                    fontSize = 32.sp, 
                    fontWeight = FontWeight.Black, 
                    color = Color.Black,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(16.dp)
                )
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF9EDBE3)),
                elevation = CardDefaults.cardElevation(8.dp)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    val fieldColors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color.Black,
                        unfocusedBorderColor = Color.Black.copy(alpha = 0.5f),
                        focusedTextColor = Color.Black,
                        unfocusedTextColor = Color.Black,
                        focusedLabelColor = Color.Black,
                        unfocusedLabelColor = Color.Black.copy(alpha = 0.7f),
                        cursorColor = Color.Black
                    )

                    OutlinedTextField(
                        value = username, 
                        onValueChange = { username = it }, 
                        label = { Text("Username") }, 
                        modifier = Modifier.fillMaxWidth(),
                        colors = fieldColors,
                        shape = RoundedCornerShape(12.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    OutlinedTextField(
                        value = email, 
                        onValueChange = { input ->
                            email = input.lowercase().filter { it.isLowerCase() || it.isDigit() || it == '@' || it == '.' }
                        }, 
                        label = { Text("Email") }, 
                        modifier = Modifier.fillMaxWidth(),
                        colors = fieldColors,
                        shape = RoundedCornerShape(12.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    OutlinedTextField(
                        value = tournamentName, 
                        onValueChange = { tournamentName = it }, 
                        label = { Text("Tournament Name") }, 
                        modifier = Modifier.fillMaxWidth().focusRequester(tournamentNameFocusRequester),
                        colors = fieldColors,
                        shape = RoundedCornerShape(12.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    ExposedDropdownMenuBox(
                        expanded = dropdownExpanded,
                        onExpandedChange = { dropdownExpanded = !dropdownExpanded },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        OutlinedTextField(
                            value = gameType, onValueChange = {}, readOnly = true,
                            label = { Text("Type of Game") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = dropdownExpanded) },
                            modifier = Modifier.menuAnchor(MenuAnchorType.PrimaryNotEditable).fillMaxWidth(),
                            colors = fieldColors,
                            shape = RoundedCornerShape(12.dp)
                        )
                        ExposedDropdownMenu(expanded = dropdownExpanded, onDismissRequest = { dropdownExpanded = false }) {
                            listOf("Kabaddi", "Cricket", "Volleyball").forEach { opt ->
                                DropdownMenuItem(text = { Text(opt) }, onClick = { gameType = opt; dropdownExpanded = false })
                            }
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    Row(modifier = Modifier.fillMaxWidth()) {
                        OutlinedTextField(
                            value = date, onValueChange = {}, label = { Text("Date") },
                            modifier = Modifier.weight(1f).clickable { datePickerDialog.show() },
                            readOnly = true, enabled = false,
                            colors = OutlinedTextFieldDefaults.colors(
                                disabledTextColor = Color.Black, 
                                disabledBorderColor = Color.Black.copy(alpha = 0.5f),
                                disabledLabelColor = Color.Black.copy(alpha = 0.7f)
                            ),
                            shape = RoundedCornerShape(12.dp),
                            trailingIcon = { IconButton(onClick = { datePickerDialog.show() }) { Icon(Icons.Default.DateRange, null, tint = Color.Black) } }
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        OutlinedTextField(
                            value = time, onValueChange = {}, label = { Text("Time") },
                            modifier = Modifier.weight(1f).clickable { timePickerDialog.show() },
                            readOnly = true, enabled = false,
                            colors = OutlinedTextFieldDefaults.colors(
                                disabledTextColor = Color.Black, 
                                disabledBorderColor = Color.Black.copy(alpha = 0.5f),
                                disabledLabelColor = Color.Black.copy(alpha = 0.7f)
                            ),
                            shape = RoundedCornerShape(12.dp),
                            trailingIcon = { IconButton(onClick = { timePickerDialog.show() }) { Text("🕒", fontSize = 20.sp) } }
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    OutlinedTextField(
                        value = password, onValueChange = { password = it }, label = { Text("Create Password") },
                        modifier = Modifier.fillMaxWidth(),
                        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        colors = fieldColors,
                        shape = RoundedCornerShape(12.dp),
                        trailingIcon = { IconButton(onClick = { passwordVisible = !passwordVisible }) { Icon(if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff, null, tint = Color.Black) } }
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    OutlinedTextField(
                        value = confirmPassword, onValueChange = { confirmPassword = it }, label = { Text("Confirm Password") },
                        modifier = Modifier.fillMaxWidth(),
                        visualTransformation = if (confirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        colors = fieldColors,
                        shape = RoundedCornerShape(12.dp),
                        trailingIcon = { IconButton(onClick = { confirmPasswordVisible = !confirmPasswordVisible }) { Icon(if (confirmPasswordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff, null, tint = Color.Black) } }
                    )
                    
                    Spacer(modifier = Modifier.height(32.dp))
                    
                    if (isLoading) {
                        CircularProgressIndicator(color = Color.Black)
                    } else {
                        Button(
                            modifier = Modifier.fillMaxWidth().height(56.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color.Black),
                            shape = RoundedCornerShape(16.dp),
                            onClick = {
                                if (tournamentName.isBlank() || password.isBlank()) {
                                    Toast.makeText(context, "Please fill all fields", Toast.LENGTH_SHORT).show()
                                } else if (!email.endsWith("@gmail.com")) {
                                    Toast.makeText(context, "Invalid email. Must end with @gmail.com", Toast.LENGTH_SHORT).show()
                                } else if (password != confirmPassword) {
                                    Toast.makeText(context, "Passwords do not match", Toast.LENGTH_SHORT).show()
                                } else {
                                    isLoading = true
                                    scope.launch {
                                        delay(10000)
                                        if (isLoading) {
                                            isLoading = false
                                            Toast.makeText(context, "Connection timeout. Check network.", Toast.LENGTH_SHORT).show()
                                        }
                                    }
                                    val tournament = Tournament(
                                        name = tournamentName, 
                                        type = gameType, 
                                        date = date, 
                                        time = time,
                                        leaderUsername = username, 
                                        leaderEmail = email,
                                        sportTypeName = when(gameType) {
                                            "Cricket" -> SportType.CRICKET.name
                                            "Volleyball" -> SportType.VOLLEYBALL.name
                                            else -> SportType.KABADDI.name
                                        }
                                    )
                                    TournamentRepository.registerTournament(tournament, password) { success ->
                                        isLoading = false
                                        if (success) {
                                            Toast.makeText(context, "Registered successfully!", Toast.LENGTH_SHORT).show()
                                            val safeName = tournamentName.trim().replace(Regex("[.#$\\[\\]/]"), "_")
                                            onRegistrationSuccess(safeName)
                                        } else {
                                            Toast.makeText(context, "Name already taken or database error", Toast.LENGTH_SHORT).show()
                                        }
                                    }
                                }
                            }
                        ) {
                            Text("Submit", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        }
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Card(
                modifier = Modifier.clickable { onBack() },
                colors = CardDefaults.cardColors(containerColor = Color.Black),
                shape = RoundedCornerShape(8.dp),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Text(
                    "Back", 
                    color = Color.White,
                    fontWeight = FontWeight.Bold, 
                    fontSize = 16.sp,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )
            }
            
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}
