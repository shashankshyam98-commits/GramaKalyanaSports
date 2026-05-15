package com.example.grama_kalyanasports.ui.genai

import com.example.grama_kalyanasports.data.Match
import com.google.ai.client.generativeai.GenerativeModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class MatchSummaryGenerator(private val apiKey: String) {
    
    suspend fun generateSummary(match: Match): String {
        return withContext(Dispatchers.IO) {
            try {
                // Ensure the API Key is valid before utilizing the model
                if (apiKey.isBlank() || apiKey == "YOUR_GEMINI_API_KEY") {
                    return@withContext "API Key not configured. Please add your Gemini API Key."
                }

                val generativeModel = GenerativeModel(
                    modelName = "gemini-1.5-flash",
                    apiKey = apiKey
                )
                
                val prompt = """
                    Write an exciting post-match summary for this ${match.sportType} game.
                    Team A: ${match.teamA} (Score: ${match.currentScoreA})
                    Team B: ${match.teamB} (Score: ${match.currentScoreB})
                    Special Stats: ${match.sportSpecificStats}
                    
                    Make it sound professional, energetic, and write it concisely (under 3 sentences).
                """.trimIndent()

                val response = generativeModel.generateContent(prompt)
                response.text ?: "Could not generate summary."
            } catch (e: Exception) {
                "Error generating AI summary: \${e.message}"
            }
        }
    }
}
