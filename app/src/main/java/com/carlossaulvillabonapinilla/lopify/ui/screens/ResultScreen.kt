package com.carlossaulvillabonapinilla.lopify.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.carlossaulvillabonapinilla.lopify.ui.model.AppState
import com.carlossaulvillabonapinilla.lopify.ui.model.WeightResult
import android.widget.Toast
import androidx.compose.ui.platform.LocalContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import com.carlossaulvillabonapinilla.lopify.ui.model.Solicitud

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResultScreen(
    onNewCapture: () -> Unit,
    onGoToOrders: () -> Unit
) {

    val result: WeightResult = AppState.lastResult ?: run {
        Box(Modifier.fillMaxSize().background(Color(0xFF0D1117)),
            contentAlignment = Alignment.Center) {
            Text("No hay resultado", color = Color.White)
        }
        return
    }

    val animatedWeight by animateFloatAsState(
        targetValue = result.weightKg.toFloat(),
        animationSpec = tween(durationMillis = 1500, easing = FastOutSlowInEasing),
        label = "weight"
    )

    val confidenceColor = if (result.usedFallback) Color(0xFFFF9F43) else Color(0xFF00D4AA)
    val confidenceText  = if (result.usedFallback) "⚠️ Confianza ~60%" else "✅ Confianza ~88%"

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Resultado del Análisis", color = Color.White, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNewCapture) {
                        Icon(Icons.Default.ArrowBack, "Atrás", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF1E2A3A))
            )
        },
        containerColor = Color(0xFF0D1117)
    ) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            // ── Tarjeta principal de peso ──────────────────────
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(24.dp))
                    .background(Color(0xFF1E2A3A))
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Peso estimado", color = Color(0xFF8899AA), fontSize = 14.sp)
                    Spacer(Modifier.height(8.dp))

                    val displayText = if (animatedWeight >= 1f)
                        "${"%.2f".format(animatedWeight)} kg"
                    else
                        "${"%.0f".format(animatedWeight * 1000)} g"

                    Text(displayText, color = Color(0xFF00D4AA),
                        fontSize = 56.sp, fontWeight = FontWeight.ExtraBold)

                    Spacer(Modifier.height(8.dp))
                    Text(confidenceText, color = confidenceColor, fontSize = 13.sp)
                }
            }

            // ── Detalles ───────────────────────────────────────
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(0xFF1E2A3A))
                    .padding(16.dp)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("📊 Detalles del cálculo", color = Color.White,
                        fontSize = 14.sp, fontWeight = FontWeight.SemiBold)

                    DetailRow("Material",    "${result.material.emoji} ${result.material.name}")
                    DetailRow("Densidad",    "%.0f g/L".format(result.material.densityGramsPerLiter))
                    DetailRow("Volumen est.","%.2f L".format(result.volumeLiters))
                    DetailRow("Referencia",  "📐 ${result.reference.name}")
                }
            }

            // ── Dimensiones ────────────────────────────────────
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(0xFF1E2A3A))
                    .padding(16.dp)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("📐 Dimensiones estimadas", color = Color.White,
                        fontSize = 14.sp, fontWeight = FontWeight.SemiBold)

                    DetailRow("Ancho",      "%.1f cm".format(result.bagWidthCm))
                    DetailRow("Alto",       "%.1f cm".format(result.bagHeightCm))
                    DetailRow("Prof. est.", "%.1f cm".format(result.bagDepthCm))
                }
            }

            // ── Fórmula ────────────────────────────────────────
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(0xFF0A1628))
                    .padding(16.dp)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text("🔬 Fórmula aplicada", color = Color(0xFF4A9EFF),
                        fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                    Text(
                        "Peso = Volumen × Densidad\n" +
                                "%.2f kg = %.2f L × %.0f g/L ÷ 1000".format(
                                    result.weightKg,
                                    result.volumeLiters,
                                    result.material.densityGramsPerLiter
                                ),
                        color = Color(0xFF8899AA),
                        fontSize = 13.sp,
                        lineHeight = 20.sp
                    )
                }
            }

            // ── Botón nueva solicitud o propuesta de reciclaje ────────────────────────────
            val context = LocalContext.current
            var alreadyCreated by remember { mutableStateOf(false) }

            Button(
                onClick = {
                    if (alreadyCreated) return@Button

                    val now = SimpleDateFormat("dd/MM HH:mm", Locale.getDefault()).format(Date())
                    AppState.solicitudes.add(
                        Solicitud(
                            material = "${result.material.emoji} ${result.material.name}",
                            peso = result.displayWeight(),
                            fecha = now
                        )
                    )
                    alreadyCreated = true
                    Toast.makeText(context, "✅ Solicitud creada", Toast.LENGTH_SHORT).show()
                    onGoToOrders()
                },
                modifier = Modifier.fillMaxWidth().height(52.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF9C74F)),
                shape = RoundedCornerShape(26.dp),
                enabled = !alreadyCreated
            ) {
                Text("Generar solicitud", color = Color.Black, fontWeight = FontWeight.Bold)
            }
            // ── Botón nueva captura ────────────────────────────
            Button(
                onClick = onNewCapture,
                modifier = Modifier.fillMaxWidth().height(52.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00D4AA)),
                shape = RoundedCornerShape(26.dp)
            ) {
                Text("Nueva captura", color = Color.Black,
                    fontWeight = FontWeight.Bold, fontSize = 15.sp)
            }
        }
    }
}

@Composable
private fun DetailRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, color = Color(0xFF8899AA), fontSize = 13.sp)
        Text(value, color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Medium)
    }
}