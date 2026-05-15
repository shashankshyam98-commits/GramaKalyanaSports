package com.example.grama_kalyanasports.ui.genai

import android.content.Context
import android.content.Intent

object ShareUtils {
    fun shareText(context: Context, text: String, title: String = "Share Match Score") {
        val sendIntent: Intent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, text)
            type = "text/plain"
        }
        val shareIntent = Intent.createChooser(sendIntent, title)
        context.startActivity(shareIntent)
    }
}
