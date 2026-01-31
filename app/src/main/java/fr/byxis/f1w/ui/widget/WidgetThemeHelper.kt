package fr.byxis.f1w.ui.widget

import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color
import androidx.glance.color.ColorProviders
import androidx.glance.material3.ColorProviders
import fr.byxis.f1w.data.model.WidgetThemeMode

object WidgetThemeHelper {

    fun getColorProviders(
        mode: WidgetThemeMode,
        transparency: Float,
        teamColor: Color
    ): ColorProviders {
        val surfaceColor = when (mode) {
            WidgetThemeMode.LIGHT -> Color.White
            WidgetThemeMode.DARK -> Color(0xFF1E1E1E)
        }

        val onSurfaceColor = when (mode) {
            WidgetThemeMode.LIGHT -> Color.Black
            WidgetThemeMode.DARK -> Color.White
        }

        val transparentSurface = surfaceColor.copy(alpha = 1f - transparency)

        return ColorProviders(
            light = lightColorScheme(
                primary = teamColor,
                onPrimary = Color.White,
                secondary = Color.Gray,
                surface = transparentSurface,
                onSurface = onSurfaceColor,
                primaryContainer = teamColor.copy(alpha = 0.2f),
                onPrimaryContainer = teamColor
            ),
            dark = darkColorScheme(
                primary = teamColor,
                onPrimary = Color.White,
                secondary = Color.LightGray,
                surface = transparentSurface,
                onSurface = onSurfaceColor,
                primaryContainer = teamColor.copy(alpha = 0.2f),
                onPrimaryContainer = teamColor
            )
        )
    }
}
