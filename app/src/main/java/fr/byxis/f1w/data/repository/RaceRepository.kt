package fr.byxis.f1w.data.repository

import android.content.Context
import fr.byxis.f1w.data.api.NetworkClient
import fr.byxis.f1w.data.local.RaceStorage
import fr.byxis.f1w.data.local.WidgetData
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Calendar

object RaceRepository {

    suspend fun getNextRaceData(context: Context): WidgetData {
        try {
            val now = Instant.now()
            val currentYear = Calendar.getInstance().get(Calendar.YEAR)
            val sessions = NetworkClient.api.getSessionsByYear(currentYear)

            val nextSession = sessions
                .filter { Instant.parse(it.date_start).isAfter(now) }
                .minByOrNull { Instant.parse(it.date_start) }

            if (nextSession != null) {
                val startInstant = Instant.parse(nextSession.date_start)
                val endInstant = Instant.parse(nextSession.date_end)
                val zoneId = ZoneId.systemDefault()

                val localStartTime = LocalDateTime.ofInstant(startInstant, zoneId)
                val localEndTime = LocalDateTime.ofInstant(endInstant, zoneId)

                val dayFormatter = DateTimeFormatter.ofPattern("dd/MM")
                val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")

                val fullDateString = "${localStartTime.format(dayFormatter)} • ${localStartTime.format(timeFormatter)} - ${localEndTime.format(timeFormatter)}"

                val sessionDisplayName = if (nextSession.session_name.contains("Day")) {
                    "Test Hivernal - ${nextSession.session_name}"
                } else {
                    nextSession.session_name
                }

                val newData = WidgetData(
                    raceName = nextSession.location,
                    raceCountry = nextSession.country_name,
                    sessionName = sessionDisplayName,
                    raceDate = fullDateString,
                    lastUpdate = System.currentTimeMillis()
                )

                RaceStorage.save(context, newData)
                return newData
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return RaceStorage.load(context) ?: WidgetData(
            raceName = "Hors ligne",
            raceCountry = "F1",
            sessionName = "Pas de données",
            raceDate = "--/--"
        )
    }
}