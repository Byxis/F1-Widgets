package fr.byxis.f1w.data.model

enum class EventStatus {
    NORMAL,        // More than 30 minutes before start
    SOON,          // Within 30 minutes of start
    IN_PROGRESS,   // Event is currently happening
    FINISHED       // Event has ended
}
