package fr.byxis.f1w.ui.activity

import android.appwidget.AppWidgetManager
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
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
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.appwidget.updateAll
import fr.byxis.f1w.ui.widget.large.NextGpWidget
import fr.byxis.f1w.ui.widget.large.NextGpWidgetReceiver
import fr.byxis.f1w.ui.widget.WidgetListScreen
import fr.byxis.f1w.ui.widget.WidgetConfigScreen
import fr.byxis.f1w.data.local.UserPreferences
import fr.byxis.f1w.data.model.EF1Team
import fr.byxis.f1w.utils.DebugLogger
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        DebugLogger.log("📱 Application ouverte")
        setContent {
            MainNavigation()
        }
    }
}

@Composable
fun MainNavigation() {
    var currentScreen by remember { mutableStateOf<Screen>(Screen.Dashboard) }

    androidx.activity.compose.BackHandler(enabled = currentScreen !is Screen.Dashboard) {
        currentScreen = when (currentScreen) {
            Screen.TeamSelection -> Screen.Dashboard
            Screen.WidgetList -> Screen.Dashboard
            is Screen.WidgetConfig -> Screen.WidgetList
            else -> Screen.Dashboard
        }
    }

    when (val screen = currentScreen) {
        Screen.Dashboard -> AppDashboard(
            onOpenSettings = { currentScreen = Screen.TeamSelection },
            onOpenWidgetList = { currentScreen = Screen.WidgetList }
        )
        Screen.TeamSelection -> TeamSelectionScreen(
            onBack = { currentScreen = Screen.Dashboard }
        )
        Screen.WidgetList -> WidgetListScreen(
            onBack = { currentScreen = Screen.Dashboard },
            onConfigureWidget = { widgetId -> currentScreen = Screen.WidgetConfig(widgetId) }
        )
        is Screen.WidgetConfig -> WidgetConfigScreen(
            widgetId = screen.widgetId,
            onBack = { currentScreen = Screen.WidgetList }
        )
    }
}

sealed class Screen {
    object Dashboard : Screen()
    object TeamSelection : Screen()
    object WidgetList : Screen()
    data class WidgetConfig(val widgetId: Int) : Screen()
}

@Composable
fun AppDashboard(onOpenSettings: () -> Unit, onOpenWidgetList: () -> Unit) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val userPrefs = remember { fr.byxis.f1w.data.local.UserPreferences(context) }
    
    var widgetData by remember { 
        mutableStateOf(
            fr.byxis.f1w.data.local.RaceStorage.load(context) ?: fr.byxis.f1w.data.local.WidgetData(
                raceName = "Chargement...",
                raceCountry = "F1",
                sessionName = "Prochain événement",
                raceDate = "--/--"
            )
        )
    }
    
    var isRefreshing by remember { mutableStateOf(false) }
    var favoriteTeam by remember { mutableStateOf(EF1Team.FERRARI) }
    
    LaunchedEffect(Unit) {
        favoriteTeam = userPrefs.getSavedTeam()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF121212))
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text("F1 WIDGET DASHBOARD", color = Color.White, fontWeight = FontWeight.Bold)

        Spacer(modifier = Modifier.height(20.dp))

        Text("Aperçu (Design)", color = Color.Gray, fontSize = 12.sp)
        Spacer(modifier = Modifier.height(8.dp))

        val previewBackgroundColor = Color(0xFF1E1E1E)
        val previewTextColor = Color.White

        Card(
            colors = CardDefaults.cardColors(containerColor = previewBackgroundColor),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth().height(140.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxSize().padding(12.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = widgetData.sessionName.uppercase(), 
                    color = favoriteTeam.primaryColor, 
                    fontSize = 10.sp, 
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                
                Text(
                    text = widgetData.raceName, 
                    color = previewTextColor, 
                    fontSize = 20.sp, 
                    fontWeight = FontWeight.Bold
                )
                
                Text(
                    text = widgetData.raceCountry, 
                    color = previewTextColor.copy(alpha = 0.7f), 
                    fontSize = 14.sp
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Box(
                    modifier = Modifier
                        .background(
                            favoriteTeam.primaryColor.copy(alpha = 0.2f),
                            RoundedCornerShape(8.dp)
                        )
                        .padding(horizontal = 12.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = widgetData.raceDate,
                        color = favoriteTeam.primaryColor,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = { onOpenWidgetList() },
            colors = ButtonDefaults.buttonColors(containerColor = favoriteTeam.primaryColor),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("⚙️ GÉRER MES WIDGETS")
        }

        Spacer(modifier = Modifier.height(12.dp))

        Button(
            onClick = { onOpenSettings() },
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF333333)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("🎨 CHOISIR MON ÉCURIE")
        }

        Spacer(modifier = Modifier.height(12.dp))

        Button(
            onClick = {
                scope.launch {
                    isRefreshing = true
                    DebugLogger.log("🔄 Début du rafraîchissement des données...")
                    
                    try {
                        val newData = fr.byxis.f1w.data.repository.RaceRepository.getNextRaceData(context)
                        widgetData = newData
                        
                        NextGpWidget.raceName = newData.raceName
                        NextGpWidget.raceCountry = newData.raceCountry
                        NextGpWidget.sessionName = newData.sessionName
                        NextGpWidget.raceDate = newData.raceDate
                        NextGpWidget.eventStartTime = newData.eventStartTime
                        NextGpWidget.eventEndTime = newData.eventEndTime
                        NextGpWidget.eventStatus = newData.eventStatus
                        
                        // Calculate countdown text
                        val currentTime = System.currentTimeMillis()
                        NextGpWidget.countdownText = when (newData.eventStatus) {
                            fr.byxis.f1w.data.model.EventStatus.SOON -> {
                                val timeUntilStart = newData.eventStartTime - currentTime
                                val minutes = (timeUntilStart / 60000).toInt()
                                val seconds = ((timeUntilStart % 60000) / 1000).toInt()
                                if (minutes > 0) "Dans ${minutes}min ${seconds}s" else "Dans ${seconds}s"
                            }
                            fr.byxis.f1w.data.model.EventStatus.IN_PROGRESS -> "En cours"
                            fr.byxis.f1w.data.model.EventStatus.FINISHED -> "Terminé"
                            else -> newData.raceDate
                        }
                        
                        NextGpWidget().updateAll(context)
                        fr.byxis.f1w.ui.widget.small.MiniGpWidget().updateAll(context)
                        
                        DebugLogger.log("✅ Données rafraîchies avec succès")
                        Toast.makeText(context, "Données mises à jour !", Toast.LENGTH_SHORT).show()
                    } catch (e: Exception) {
                        DebugLogger.log("❌ ERREUR lors du rafraîchissement: ${e.message}")
                        Toast.makeText(context, "Erreur de mise à jour", Toast.LENGTH_SHORT).show()
                    } finally {
                        isRefreshing = false
                    }
                }
            },
            colors = ButtonDefaults.buttonColors(
                containerColor = if (isRefreshing) Color.Gray else Color(0xFFFF1801)
            ),
            modifier = Modifier.fillMaxWidth(),
            enabled = !isRefreshing
        ) {
            Text(if (isRefreshing) "🔄 RAFRAÎCHISSEMENT..." else "🔄 RAFRAÎCHIR LES DONNÉES")
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text("Logs système", color = Color.Gray, fontSize = 12.sp, modifier = Modifier.align(Alignment.Start))

        Card(
            modifier = Modifier.fillMaxWidth().weight(1f),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF000000))
        ) {
            LazyColumn(contentPadding = PaddingValues(12.dp)) {
                items(DebugLogger.logs) { logMessage ->
                    Text(
                        text = logMessage,
                        color = if (logMessage.contains("ERREUR")) Color.Red else Color(0xFF00FF00),
                        fontSize = 12.sp,
                        fontFamily = FontFamily.Monospace,
                        modifier = Modifier.padding(vertical = 2.dp)
                    )
                    Divider(color = Color.DarkGray, thickness = 0.5.dp)
                }
            }
        }
    }
}

@Composable
fun TeamSelectionScreen(onBack: () -> Unit) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val userPrefs = UserPreferences(context)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF121212))
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        // Header avec bouton Retour
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Button(onClick = { onBack() }, colors = ButtonDefaults.buttonColors(containerColor = Color.Gray)) {
                Text("<")
            }
            Spacer(modifier = Modifier.width(16.dp))
            Text("CHOISIS TON ÉCURIE", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 20.sp)
        }

        Spacer(modifier = Modifier.height(20.dp))

        LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            items(EF1Team.entries) { team ->
                TeamItem(team = team) {
                    scope.launch {
                        userPrefs.saveTeam(team)
                        Toast.makeText(context, "${team.teamName} sauvegardé", Toast.LENGTH_SHORT).show()
                        NextGpWidget().updateAll(context)
                        fr.byxis.f1w.ui.widget.small.MiniGpWidget().updateAll(context)
                        
                        onBack()
                    }
                }
            }
        }
    }
}

@Composable
fun TeamItem(team: EF1Team, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(60.dp)
            .background(team.primaryColor, RoundedCornerShape(12.dp))
            .clickable { onClick() }
            .padding(horizontal = 20.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = team.teamName,
            color = team.contentColor,
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp
        )
    }
}