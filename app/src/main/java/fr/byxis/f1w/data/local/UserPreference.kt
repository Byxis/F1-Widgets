package fr.byxis.f1w.data.local

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import fr.byxis.f1w.data.model.EF1Team
import fr.byxis.f1w.data.model.WidgetThemeMode
import kotlinx.coroutines.flow.first

val Context.dataStore by preferencesDataStore(name = "settings")

class UserPreferences(private val context: Context) {

    companion object {
        val TEAM_KEY = stringPreferencesKey("favorite_team")
        
        fun getThemeModeKey(widgetId: Int) = stringPreferencesKey("widget_${widgetId}_theme_mode")
        fun getTransparencyKey(widgetId: Int) = floatPreferencesKey("widget_${widgetId}_transparency")
    }

    suspend fun saveTeam(team: EF1Team) {
        context.dataStore.edit { preferences ->
            preferences[TEAM_KEY] = team.name
        }
    }

    suspend fun getSavedTeam(): EF1Team {
        val preferences = context.dataStore.data.first()
        val teamName = preferences[TEAM_KEY] ?: EF1Team.DEFAULT.name

        return try {
            EF1Team.valueOf(teamName)
        } catch (e: Exception) {
            EF1Team.DEFAULT
        }
    }

    suspend fun saveThemeMode(widgetId: Int, mode: WidgetThemeMode) {
        context.dataStore.edit { preferences ->
            preferences[getThemeModeKey(widgetId)] = mode.name
        }
    }

    suspend fun getSavedThemeMode(widgetId: Int): WidgetThemeMode {
        val preferences = context.dataStore.data.first()
        val modeName = preferences[getThemeModeKey(widgetId)] ?: WidgetThemeMode.DARK.name

        return try {
            WidgetThemeMode.valueOf(modeName)
        } catch (e: Exception) {
            WidgetThemeMode.DARK
        }
    }

    suspend fun saveTransparency(widgetId: Int, transparency: Float) {
        context.dataStore.edit { preferences ->
            preferences[getTransparencyKey(widgetId)] = transparency.coerceIn(0f, 1f)
        }
    }

    suspend fun getSavedTransparency(widgetId: Int): Float {
        val preferences = context.dataStore.data.first()
        return preferences[getTransparencyKey(widgetId)] ?: 0f
    }
}
