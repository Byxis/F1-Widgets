package fr.byxis.f1w.data.local

import fr.byxis.f1w.data.model.EventStatus

data class WidgetData(
    val raceName: String,
    val raceCountry: String,
    val sessionName: String,
    val raceDate: String,
    val eventStartTime: Long = 0L,
    val eventEndTime: Long = 0L,
    val eventStatus: EventStatus = EventStatus.NORMAL,
    val lastUpdate: Long = System.currentTimeMillis()
)