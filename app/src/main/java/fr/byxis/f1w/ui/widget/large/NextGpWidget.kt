package fr.byxis.f1w.ui.widget.large

import android.content.Context
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.cornerRadius
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.layout.*
import androidx.glance.material3.ColorProviders
import androidx.glance.preview.ExperimentalGlancePreviewApi
import androidx.glance.preview.Preview
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import fr.byxis.f1w.data.local.UserPreferences
import fr.byxis.f1w.ui.widget.WidgetThemeHelper

class NextGpWidget : GlanceAppWidget() {
    companion object {
        var raceName = "Chargement..."
        var raceDate = "--/--"
        var raceCountry = "F1"
        var sessionName = "Prochain événement"
        var eventStartTime = 0L
        var eventEndTime = 0L
        var eventStatus = fr.byxis.f1w.data.model.EventStatus.NORMAL
        var countdownText = "--/--" // Pre-calculated countdown text
    }

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
                WidgetContent()
            }
        }
    }
}

@Composable
fun WidgetContent() {
    val gpName = if (NextGpWidget.raceName.contains("Grand Prix", ignoreCase = true)) {
        NextGpWidget.raceName
    } else {
        "Grand Prix de ${NextGpWidget.raceName}"
    }
    
    // Calculate emoji based on event status
    val emojiPrefix = when (NextGpWidget.eventStatus) {
        fr.byxis.f1w.data.model.EventStatus.SOON -> "⚠️ "
        fr.byxis.f1w.data.model.EventStatus.IN_PROGRESS -> "🟢 "
        fr.byxis.f1w.data.model.EventStatus.FINISHED -> "🚩 "
        else -> ""
    }
    
    Column(
        modifier = GlanceModifier
            .fillMaxSize()
            .background(GlanceTheme.colors.surface)
            .cornerRadius(16.dp)
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        
        Text(
            text = gpName,
            style = TextStyle(color = GlanceTheme.colors.primary, fontSize = 10.sp, fontWeight = FontWeight.Bold)
        )

        Spacer(modifier = GlanceModifier.height(4.dp))


        Text(
            text = "$emojiPrefix${NextGpWidget.sessionName.uppercase()}",
            style = TextStyle(
                color = GlanceTheme.colors.onSurface,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        )

        Text(
            text = NextGpWidget.raceCountry,
            style = TextStyle(color = GlanceTheme.colors.secondary, fontSize = 12.sp)
        )

        Spacer(modifier = GlanceModifier.height(8.dp))

        // Display countdown text (Glance doesn't support real-time updates)
        Box(
            modifier = GlanceModifier
                .background(GlanceTheme.colors.primaryContainer)
                .cornerRadius(8.dp)
                .padding(horizontal = 8.dp, vertical = 4.dp)
        ) {
            Text(
                text = NextGpWidget.countdownText,
                style = TextStyle(color = GlanceTheme.colors.onPrimaryContainer)
            )
        }
    }
}

@OptIn(ExperimentalGlancePreviewApi::class)
@Preview(widthDp = 200, heightDp = 150)
@Composable
fun PreviewWidget() {
    NextGpWidget.raceName = "Grand Prix de Monaco"
    NextGpWidget.raceDate = "26/05 • 15:00 - 17:00"
    NextGpWidget.raceCountry = "Monte Carlo"
    NextGpWidget.sessionName = "Course"

    GlanceTheme(colors = ColorProviders(
        light = lightColorScheme(
            primary = Color(0xFFFF1801),
            onPrimary = Color.White,
            surface = Color(0xFF1E1E1E),
            onSurface = Color.White,
            primaryContainer = Color(0xFFFF1801).copy(alpha = 0.2f),
            onPrimaryContainer = Color(0xFFFF1801)
        ),
        dark = darkColorScheme(
            primary = Color(0xFFFF1801),
            onPrimary = Color.White,
            surface = Color(0xFF1E1E1E),
            onSurface = Color.White,
            primaryContainer = Color(0xFFFF1801).copy(alpha = 0.2f),
            onPrimaryContainer = Color(0xFFFF1801)
        )
    )) {
        WidgetContent()
    }
}