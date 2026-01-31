package fr.byxis.f1w.data.model

import androidx.compose.ui.graphics.Color

enum class EF1Team(
    val teamName: String,
    val primaryColor: Color,
    val contentColor: Color
) {
    FERRARI("Ferrari", Color(0xFFFF1801), Color.White),
    MCLAREN("McLaren", Color(0xFFFF8700), Color.Black),
    RED_BULL("Red Bull Racing", Color(0xFF061D41), Color.White),
    MERCEDES("Mercedes", Color(0xFF00D2BE), Color.Black),
    ASTON_MARTIN("Aston Martin", Color(0xFF006F62), Color.White),
    ALPINE("Alpine", Color(0xFF0090FF), Color.White),
    WILLIAMS("Williams", Color(0xFF005AFF), Color.White),
    RB("Visa Cash App RB", Color(0xFF1634CB), Color.White),
    KICK_SAUBER("Kick Sauber", Color(0xFF52E252), Color.Black),
    HAAS("Haas F1 Team", Color(0xFFB6BABD), Color.Black),

    DEFAULT("F1 Fan", Color(0xFF1E1E1E), Color.White);

    companion object {
        fun getByName(name: String): EF1Team {
            return entries.find { it.name == name } ?: DEFAULT
        }
    }
}