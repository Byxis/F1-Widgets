package fr.byxis.f1w.ui.widget.large

import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.updateAll
import fr.byxis.f1w.data.local.WidgetData
import fr.byxis.f1w.data.repository.RaceRepository
import fr.byxis.f1w.utils.DebugLogger
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

class NextGpWidgetReceiver : GlanceAppWidgetReceiver() {

    override val glanceAppWidget: GlanceAppWidget = NextGpWidget()

    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        super.onUpdate(context, appWidgetManager, appWidgetIds)
        refreshData(context)
    }

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)
        if (intent.action == AppWidgetManager.ACTION_APPWIDGET_UPDATE) {
            refreshData(context)
        }
    }

    private fun refreshData(context: Context) {
        MainScope().launch {
            val data = RaceRepository.getNextRaceData(context)
            if (data.raceName != "Hors ligne") {
                DebugLogger.log("✅ Données reçues: ${data.raceName}")
            } else {
                DebugLogger.log("❌ ERREUR: Impossible de récupérer les données (Hors ligne)")
            }

            NextGpWidget.raceName = data.raceName
            NextGpWidget.raceCountry = data.raceCountry
            NextGpWidget.sessionName = data.sessionName
            NextGpWidget.raceDate = data.raceDate
            NextGpWidget.eventStartTime = data.eventStartTime
            NextGpWidget.eventEndTime = data.eventEndTime
            NextGpWidget.eventStatus = data.eventStatus
            
            // Calculate initial countdown text
            val currentTime = System.currentTimeMillis()
            NextGpWidget.countdownText = when (data.eventStatus) {
                fr.byxis.f1w.data.model.EventStatus.SOON -> {
                    val timeUntilStart = data.eventStartTime - currentTime
                    val minutes = (timeUntilStart / 60000).toInt()
                    val seconds = ((timeUntilStart % 60000) / 1000).toInt()
                    if (minutes > 0) "Dans ${minutes}min ${seconds}s" else "Dans ${seconds}s"
                }
                fr.byxis.f1w.data.model.EventStatus.IN_PROGRESS -> "En cours"
                fr.byxis.f1w.data.model.EventStatus.FINISHED -> "Terminé"
                else -> data.raceDate
            }

            NextGpWidget().updateAll(context)
        }
    }

    private suspend fun updateWidgetState(context: Context, data: WidgetData) {
        NextGpWidget.raceName = data.raceName
        NextGpWidget.raceCountry = data.raceCountry
        NextGpWidget.sessionName = data.sessionName
        NextGpWidget.raceDate = data.raceDate
        NextGpWidget.eventStartTime = data.eventStartTime
        NextGpWidget.eventEndTime = data.eventEndTime
        NextGpWidget.eventStatus = data.eventStatus
        NextGpWidget().updateAll(context)
    }
}