package fr.byxis.f1w.ui.widget

import android.appwidget.AppWidgetManager
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.appwidget.updateAll
import fr.byxis.f1w.data.local.UserPreferences
import fr.byxis.f1w.data.model.EF1Team
import fr.byxis.f1w.data.model.WidgetThemeMode
import fr.byxis.f1w.ui.widget.large.NextGpWidget
import fr.byxis.f1w.ui.widget.large.NextGpWidgetReceiver
import kotlinx.coroutines.launch

@Composable
fun WidgetListScreen(onBack: () -> Unit, onConfigureWidget: (Int) -> Unit) {
    val context = LocalContext.current
    val appWidgetManager = AppWidgetManager.getInstance(context)
    
    val nextGpWidgetIds = appWidgetManager.getAppWidgetIds(
        android.content.ComponentName(context, NextGpWidgetReceiver::class.java)
    )
    
    val miniGpWidgetIds = appWidgetManager.getAppWidgetIds(
        android.content.ComponentName(context, fr.byxis.f1w.ui.widget.small.MiniGpWidgetReceiver::class.java)
    )
    
    LaunchedEffect(Unit) {
        fr.byxis.f1w.utils.DebugLogger.log("📊 NextGP Widget IDs: ${nextGpWidgetIds.joinToString()}")
        fr.byxis.f1w.utils.DebugLogger.log("📊 Mini Widget IDs: ${miniGpWidgetIds.joinToString()}")
    }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF121212))
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("←", color = Color.White, fontSize = 24.sp, modifier = Modifier.clickable { onBack() })
            Spacer(modifier = Modifier.width(16.dp))
            Text("MES WIDGETS", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 20.sp)
        }

        Spacer(modifier = Modifier.height(20.dp))

        if (nextGpWidgetIds.isEmpty() && miniGpWidgetIds.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "Aucun widget placé\n\nAjoutez un widget F1 sur votre écran d'accueil",
                    color = Color.Gray,
                    fontSize = 16.sp,
                    textAlign = TextAlign.Center
                )
            }
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                if (nextGpWidgetIds.isNotEmpty()) {
                    item {
                        Text("Widgets Grands", color = Color.Gray, fontSize = 14.sp)
                    }
                    items(nextGpWidgetIds.toList()) { widgetId ->
                        WidgetCard(
                            widgetName = "Next GP Widget",
                            widgetId = widgetId,
                            onConfigure = { onConfigureWidget(widgetId) }
                        )
                    }
                }
                
                if (miniGpWidgetIds.isNotEmpty()) {
                    item {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Widgets Mini", color = Color.Gray, fontSize = 14.sp)
                    }
                    items(miniGpWidgetIds.toList()) { widgetId ->
                        WidgetCard(
                            widgetName = "Mini GP Widget",
                            widgetId = widgetId,
                            onConfigure = { onConfigureWidget(widgetId) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun WidgetCard(widgetName: String, widgetId: Int, onConfigure: () -> Unit) {
    val context = LocalContext.current
    val userPrefs = remember { UserPreferences(context) }
    
    var themeMode by remember { mutableStateOf(WidgetThemeMode.DARK) }
    var transparency by remember { mutableFloatStateOf(0f) }
    
    LaunchedEffect(widgetId) {
        themeMode = userPrefs.getSavedThemeMode(widgetId)
        transparency = userPrefs.getSavedTransparency(widgetId)
    }
    
    Card(
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E1E)),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(widgetName, color = Color.White, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    "ID: $widgetId",
                    color = Color.Gray,
                    fontSize = 12.sp
                )
                Text(
                    "Thème: ${if (themeMode == WidgetThemeMode.LIGHT) "Clair" else "Sombre"} • ${(transparency * 100).toInt()}% transparent",
                    color = Color.Gray,
                    fontSize = 12.sp
                )
            }
            
            Button(
                onClick = onConfigure,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF333333))
            ) {
                Text("MODIFIER")
            }
        }
    }
}

@Composable
fun WidgetConfigScreen(widgetId: Int, onBack: () -> Unit) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val userPrefs = remember { UserPreferences(context) }

    var selectedThemeMode by remember { mutableStateOf(WidgetThemeMode.DARK) }
    var transparency by remember { mutableFloatStateOf(0f) }
    var favoriteTeam by remember { mutableStateOf(EF1Team.FERRARI) }

    LaunchedEffect(widgetId) {
        selectedThemeMode = userPrefs.getSavedThemeMode(widgetId)
        transparency = userPrefs.getSavedTransparency(widgetId)
        favoriteTeam = userPrefs.getSavedTeam()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF121212))
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            "Configuration Widget #$widgetId",
            color = Color.White,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(32.dp))

        val previewBackgroundColor = when (selectedThemeMode) {
            WidgetThemeMode.LIGHT -> Color.White.copy(alpha = 1f - transparency)
            WidgetThemeMode.DARK -> Color(0xFF1E1E1E).copy(alpha = 1f - transparency)
        }

        val previewTextColor = when (selectedThemeMode) {
            WidgetThemeMode.LIGHT -> Color.Black
            WidgetThemeMode.DARK -> Color.White
        }

        Card(
            colors = CardDefaults.cardColors(containerColor = previewBackgroundColor),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(12.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "PROCHAIN ÉVÉNEMENT",
                    color = favoriteTeam.primaryColor,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Grand Prix",
                    color = previewTextColor,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Circuit",
                    color = previewTextColor.copy(alpha = 0.7f),
                    fontSize = 12.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        Text("Thème", color = Color.Gray, fontSize = 14.sp)
        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Button(
                onClick = { selectedThemeMode = WidgetThemeMode.LIGHT },
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (selectedThemeMode == WidgetThemeMode.LIGHT)
                        Color.White else Color(0xFF333333)
                ),
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    "☀️ CLAIR",
                    color = if (selectedThemeMode == WidgetThemeMode.LIGHT)
                        Color.Black else Color.White
                )
            }

            Button(
                onClick = { selectedThemeMode = WidgetThemeMode.DARK },
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (selectedThemeMode == WidgetThemeMode.DARK)
                        Color(0xFF1E1E1E) else Color(0xFF333333)
                ),
                modifier = Modifier.weight(1f)
            ) {
                Text("🌙 SOMBRE")
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            "Transparence: ${(transparency * 100).toInt()}%",
            color = Color.Gray,
            fontSize = 14.sp
        )
        Spacer(modifier = Modifier.height(8.dp))

        Slider(
            value = transparency,
            onValueChange = { transparency = it },
            colors = SliderDefaults.colors(
                thumbColor = favoriteTeam.primaryColor,
                activeTrackColor = favoriteTeam.primaryColor
            ),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(32.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedButton(
                onClick = onBack,
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = Color.White
                )
            ) {
                Text("ANNULER")
            }

            Button(
                onClick = {
                    scope.launch {
                        userPrefs.saveThemeMode(widgetId, selectedThemeMode)
                        userPrefs.saveTransparency(widgetId, transparency)
                        NextGpWidget().updateAll(context)
                        Toast.makeText(context, "Widget mis à jour", Toast.LENGTH_SHORT).show()
                        onBack()
                    }
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = favoriteTeam.primaryColor
                ),
                modifier = Modifier.weight(1f)
            ) {
                Text("ENREGISTRER")
            }
        }
    }
}
