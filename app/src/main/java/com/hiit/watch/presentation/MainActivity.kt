package com.hiit.watch.presentation

import android.content.Context
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            HiitWatchApp()
        }
    }
}

@Composable
fun HiitWatchApp() {
    val context = LocalContext.current
    val vibrator = remember { context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator }

    var workTime by remember { mutableStateOf(30) }
    var restTime by remember { mutableStateOf(10) }
    var totalRounds by remember { mutableStateOf(8) }
    var isConfiguring by remember { mutableStateOf(true) }

    var timeRemaining by remember { mutableStateOf(workTime) }
    var currentRound by remember { mutableStateOf(1) }
    var phase by remember { mutableStateOf("work") } // Fases: "work", "rest", "finished"
    var isRunning by remember { mutableStateOf(false) }

    fun triggerVibration() {
        try {
            vibrator?.vibrate(VibrationEffect.createOneShot(300, VibrationEffect.DEFAULT_AMPLITUDE))
        } catch (e: Exception) {}
    }

    // Lógica del temporizador
    LaunchedEffect(isRunning, timeRemaining, phase) {
        if (isRunning) {
            delay(1000L)
            if (timeRemaining > 1) {
                timeRemaining -= 1
                if (timeRemaining <= 3) triggerVibration()
            } else {
                if (phase == "work") {
                    if (currentRound < totalRounds) {
                        phase = "rest"
                        timeRemaining = restTime
                        triggerVibration()
                    } else {
                        phase = "finished"
                        isRunning = false
                        triggerVibration()
                    }
                } else if (phase == "rest") {
                    currentRound += 1
                    phase = "work"
                    timeRemaining = workTime
                    triggerVibration()
                }
            }
        }
    }

    // Color de fondo dependiendo de la fase
    val bgColor = when {
        isConfiguring -> Color.Black
        phase == "work" -> Color(0xFF4A0E0E) // Rojo oscuro
        phase == "rest" -> Color(0xFF0E4A1E) // Verde oscuro
        else -> Color(0xFF4A3E0E) // Amarillo oscuro
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .size(190.dp)
                .clip(CircleShape)
                .background(bgColor),
            contentAlignment = Alignment.Center
        ) {
            if (isConfiguring) {
                ConfigScreen(
                    workTime = workTime,
                    restTime = restTime,
                    onWorkChange = { workTime = it },
                    onRestChange = { restTime = it },
                    onStart = {
                        timeRemaining = workTime
                        currentRound = 1
                        phase = "work"
                        isConfiguring = false
                        isRunning = true
                        triggerVibration()
                    }
                )
            } else {
                TimerScreen(
                    currentRound = currentRound,
                    totalRounds = totalRounds,
                    timeRemaining = timeRemaining,
                    phase = phase,
                    isRunning = isRunning,
                    onTogglePause = { isRunning = !isRunning },
                    onStop = {
                        isConfiguring = true
                        isRunning = false
                    }
                )
            }
        }
    }
}

@Composable
fun ConfigScreen(
    workTime: Int,
    restTime: Int,
    onWorkChange: (Int) -> Unit,
    onRestChange: (Int) -> Unit,
    onStart: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxSize()
    ) {
        BasicText("HIIT TIMER", style = TextStyle(color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold))
        Spacer(modifier = Modifier.height(8.dp))

        // Controles de Trabajo (Work)
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.clickable { if (workTime > 5) onWorkChange(workTime - 5) }.padding(8.dp)) {
                BasicText("-", style = TextStyle(color = Color.White, fontSize = 18.sp))
            }
            BasicText("${workTime}s TRAB", style = TextStyle(color = Color.Red, fontSize = 12.sp, fontWeight = FontWeight.Bold))
            Box(modifier = Modifier.clickable { onWorkChange(workTime + 5) }.padding(8.dp)) {
                BasicText("+", style = TextStyle(color = Color.White, fontSize = 18.sp))
            }
        }

        // Controles de Descanso (Rest)
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.clickable { if (restTime > 5) onRestChange(restTime - 5) }.padding(8.dp)) {
                BasicText("-", style = TextStyle(color = Color.White, fontSize = 18.sp))
            }
            BasicText("${restTime}s DESC", style = TextStyle(color = Color.Green, fontSize = 12.sp, fontWeight = FontWeight.Bold))
            Box(modifier = Modifier.clickable { onRestChange(restTime + 5) }.padding(8.dp)) {
                BasicText("+", style = TextStyle(color = Color.White, fontSize = 18.sp))
            }
        }

        Spacer(modifier = Modifier.height(8.dp))
        Box(
            modifier = Modifier
                .clip(CircleShape)
                .background(Color.White)
                .clickable { onStart() }
                .padding(horizontal = 16.dp, vertical = 6.dp)
        ) {
            BasicText("INICIAR", style = TextStyle(color = Color.Black, fontSize = 12.sp, fontWeight = FontWeight.Bold))
        }
    }
}

@Composable
fun TimerScreen(
    currentRound: Int,
    totalRounds: Int,
    timeRemaining: Int,
    phase: String,
    isRunning: Boolean,
    onTogglePause: () -> Unit,
    onStop: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxSize()
    ) {
        BasicText("RONDA $currentRound / $totalRounds", style = TextStyle(color = Color.LightGray, fontSize = 10.sp))
        Spacer(modifier = Modifier.height(4.dp))
        BasicText(
            text = "${timeRemaining}s",
            style = TextStyle(color = Color.White, fontSize = 42.sp, fontWeight = FontWeight.Black)
        )
        Spacer(modifier = Modifier.height(4.dp))
        BasicText(
            text = if (phase == "work") "¡DALE!" else if (phase == "rest") "RESPIRA" else "¡FIN!",
            style = TextStyle(
                color = if (phase == "work") Color.Red else if (phase == "rest") Color.Green else Color.Yellow,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
            )
        )
        Spacer(modifier = Modifier.height(12.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            Box(
                modifier = Modifier
                    .clip(CircleShape)
                    .background(Color.DarkGray)
                    .clickable { onTogglePause() }
                    .padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
                BasicText(if (isRunning) "Pausa" else "Seguir", style = TextStyle(color = Color.White, fontSize = 10.sp))
            }
            Box(
                modifier = Modifier
                    .clip(CircleShape)
                    .background(Color.DarkGray)
                    .clickable { onStop() }
                    .padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
                BasicText("Salir", style = TextStyle(color = Color.White, fontSize = 10.sp))
            }
        }
    }
}