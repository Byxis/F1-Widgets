package fr.byxis.f1w.utils

import androidx.compose.runtime.mutableStateListOf
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object DebugLogger {
    val logs = mutableStateListOf<String>()

    fun log(message: String) {
        val time = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date())
        logs.add(0, "[$time] $message")

        if (logs.size > 50) {
            logs.removeLast()
        }
    }
}