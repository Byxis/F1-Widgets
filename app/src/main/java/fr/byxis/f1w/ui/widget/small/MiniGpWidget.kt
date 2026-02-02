package fr.byxis.f1w.ui.widget.small

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.provideContent
import androidx.glance.layout.*
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import androidx.glance.text.FontWeight
import fr.byxis.f1w.ui.widget.large.NextGpWidget
import fr.byxis.f1w.data.local.UserPreferences
import fr.byxis.f1w.ui.widget.WidgetThemeHelper

class MiniGpWidget : GlanceAppWidget() {

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val glanceAppWidgetManager = GlanceAppWidgetManager(context)
        val appWidgetId = glanceAppWidgetManager.getAppWidgetId(id)
        
        val userPrefs = UserPreferences(context)
        val favoriteTeam = userPrefs.getSavedTeam()
        val themeMode = userPrefs.getSavedThemeMode(appWidgetId)
        val transparency = userPrefs.getSavedTransparency(appWidgetId)

        provideContent {
            GlanceTheme(
                colors = WidgetThemeHelper.getColorProviders(
                    mode = themeMode,
                    transparency = transparency,
                    teamColor = favoriteTeam.primaryColor
                )
            ) {
                MiniWidgetContent()
            }
        }
    }
}

@Composable
fun MiniWidgetContent() {
    val emojiPrefix = when (NextGpWidget.eventStatus) {
        fr.byxis.f1w.data.model.EventStatus.SOON -> "⚠️ "
        fr.byxis.f1w.data.model.EventStatus.IN_PROGRESS -> "🟢 "
        fr.byxis.f1w.data.model.EventStatus.FINISHED -> "🚩 "
        else -> ""
    }
    
    Column(
        modifier = GlanceModifier
            .fillMaxSize()
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = NextGpWidget.countdownText,
            style = TextStyle(
                color = GlanceTheme.colors.primary,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold
            )
        )

        Spacer(modifier = GlanceModifier.height(2.dp))

        Text(
            text = "$emojiPrefix${NextGpWidget.sessionName.uppercase()}",
            style = TextStyle(
                color = GlanceTheme.colors.onSurface,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold
            ),
            maxLines = 1
        )

        Text(
            text = NextGpWidget.raceCountry,
            style = TextStyle(
                color = GlanceTheme.colors.secondary,
                fontSize = 9.sp
            ),
            maxLines = 1
        )
    }
}