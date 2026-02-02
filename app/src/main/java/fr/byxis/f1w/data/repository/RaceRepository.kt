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
                .filter { Instant.parse(it.dateStart).isAfter(now) }
                .minByOrNull { Instant.parse(it.dateStart) }

            if (nextSession != null) {
                val startInstant = Instant.parse(nextSession.dateStart)
                val endInstant = Instant.parse(nextSession.dateEnd)
                val zoneId = ZoneId.systemDefault()

                val localStartTime = LocalDateTime.ofInstant(startInstant, zoneId)
                val localEndTime = LocalDateTime.ofInstant(endInstant, zoneId)

                val dayFormatter = DateTimeFormatter.ofPattern("dd/MM")
                val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")

                val fullDateString = "${localStartTime.format(dayFormatter)} • ${localStartTime.format(timeFormatter)} - ${localEndTime.format(timeFormatter)}"

                val sessionDisplayName = if (nextSession.sessionName.contains("Day") && !nextSession.sessionName.contains("Test Hivernal")) {
                    "Test Hivernal - ${nextSession.sessionName}"
                } else {
                    nextSession.sessionName
                }

                val startTimeMillis = startInstant.toEpochMilli()
                val endTimeMillis = endInstant.toEpochMilli()
                val currentTimeMillis = System.currentTimeMillis()
                val thirtyMinutesInMillis = 30 * 60 * 1000L

                val eventStatus = when {
                    currentTimeMillis >= endTimeMillis -> fr.byxis.f1w.data.model.EventStatus.FINISHED
                    currentTimeMillis >= startTimeMillis -> fr.byxis.f1w.data.model.EventStatus.IN_PROGRESS
                    currentTimeMillis >= (startTimeMillis - thirtyMinutesInMillis) -> fr.byxis.f1w.data.model.EventStatus.SOON
                    else -> fr.byxis.f1w.data.model.EventStatus.NORMAL
                }

                val newData = WidgetData(
                    raceName = nextSession.location,
                    raceCountry = nextSession.countryName,
                    sessionName = sessionDisplayName,
                    raceDate = fullDateString,
                    eventStartTime = startTimeMillis,
                    eventEndTime = endTimeMillis,
                    eventStatus = eventStatus,
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