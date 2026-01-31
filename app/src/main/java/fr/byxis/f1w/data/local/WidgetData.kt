package fr.byxis.f1w.data.local

data class WidgetData(
    val raceName: String,
    val raceCountry: String,
    val sessionName: String,
    val raceDate: String,
    val lastUpdate: Long = System.currentTimeMillis()
)