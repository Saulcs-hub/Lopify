package com.carlossaulvillabonapinilla.lopify.ui.screens

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageFormat
import android.graphics.Rect
import android.graphics.YuvImage
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.carlossaulvillabonapinilla.lopify.ui.model.*
import java.io.ByteArrayOutputStream
import java.util.concurrent.Executors

// ── Pasos del flujo ──────────────────────────────────────
enum class CameraStep {
    SELECT_MATERIAL,   // Paso 1: elegir material y referencia
    DRAW_BAG,          // Paso 2: dibujar rectángulo sobre la bolsa
    DRAW_REFERENCE,    // Paso 3: dibujar rectángulo sobre la referencia
    CALCULATING        // Paso 4: calculando
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CameraScreen(onResult: () -> Unit, onBack: () -> Unit) {

    val context        = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    var step               by remember { mutableStateOf(CameraStep.SELECT_MATERIAL) }
    var selectedMaterial   by remember { mutableStateOf(WeightCalculator.materials[0]) }
    var selectedRef        by remember { mutableStateOf(WeightCalculator.referenceObjects[0]) }
    var expandedMaterial   by remember { mutableStateOf(false) }
    var expandedRef        by remember { mutableStateOf(false) }

    // Rectángulos dibujados por el usuario (en píxeles de pantalla)
    var bagRect            by remember { mutableStateOf<androidx.compose.ui.geometry.Rect?>(null) }
    var refRect            by remember { mutableStateOf<androidx.compose.ui.geometry.Rect?>(null) }
    var dragStart          by remember { mutableStateOf(Offset.Zero) }
    var dragCurrent        by remember { mutableStateOf(Offset.Zero) }
    var isDragging         by remember { mutableStateOf(false) }

    var imageCaptureRef    by remember { mutableStateOf<ImageCapture?>(null) }
    val executor           = remember { Executors.newSingleThreadExecutor() }

    // Animación pulso
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulse by infiniteTransition.animateFloat(
        initialValue = 0.7f, targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(800), RepeatMode.Reverse),
        label = "pulse"
    )

    Box(modifier = Modifier.fillMaxSize().background(Color(0xFF0D1117))) {

        // ── Cámara en vivo ───────────────────────────────────
        AndroidView(
            modifier = Modifier.fillMaxSize(),
            factory = { ctx ->
                val previewView = PreviewView(ctx)
                val future = ProcessCameraProvider.getInstance(ctx)
                future.addListener({
                    val provider = future.get()
                    val preview  = Preview.Builder().build().also {
                        it.setSurfaceProvider(previewView.surfaceProvider)
                    }
                    val capture = ImageCapture.Builder()
                        .setCaptureMode(ImageCapture.CAPTURE_MODE_MAXIMIZE_QUALITY)
                        .build()
                    imageCaptureRef = capture
                    try {
                        provider.unbindAll()
                        provider.bindToLifecycle(
                            lifecycleOwner,
                            CameraSelector.DEFAULT_BACK_CAMERA,
                            preview, capture
                        )
                    } catch (e: Exception) { /* ignorar */ }
                }, ContextCompat.getMainExecutor(ctx))
                previewView
            }
        )

        // ── Canvas para dibujar rectángulos ─────────────────
        if (step == CameraStep.DRAW_BAG || step == CameraStep.DRAW_REFERENCE) {
            Canvas(
                modifier = Modifier
                    .fillMaxSize()
                    .pointerInput(step) {
                        detectDragGestures(
                            onDragStart = { offset ->
                                dragStart   = offset
                                dragCurrent = offset
                                isDragging  = true
                            },
                            onDrag = { change, _ ->
                                dragCurrent = change.position
                            },
                            onDragEnd = {
                                isDragging = false
                                val rect = androidx.compose.ui.geometry.Rect(
                                    left   = minOf(dragStart.x, dragCurrent.x),
                                    top    = minOf(dragStart.y, dragCurrent.y),
                                    right  = maxOf(dragStart.x, dragCurrent.x),
                                    bottom = maxOf(dragStart.y, dragCurrent.y)
                                )
                                if (step == CameraStep.DRAW_BAG) {
                                    bagRect = rect
                                } else {
                                    refRect = rect
                                }
                            }
                        )
                    }
            ) {
                // Rectángulo ya guardado de la bolsa
                bagRect?.let { r ->
                    drawRect(
                        color = Color(0xFF00D4AA),
                        topLeft = Offset(r.left, r.top),
                        size = Size(r.width, r.height),
                        style = Stroke(width = 3f)
                    )
                }
                // Rectángulo ya guardado de la referencia
                refRect?.let { r ->
                    drawRect(
                        color = Color(0xFFFFD700),
                        topLeft = Offset(r.left, r.top),
                        size = Size(r.width, r.height),
                        style = Stroke(width = 3f)
                    )
                }
                // Rectángulo siendo dibujado ahora
                if (isDragging) {
                    val color = if (step == CameraStep.DRAW_BAG)
                        Color(0xFF00D4AA) else Color(0xFFFFD700)
                    drawRect(
                        color = color.copy(alpha = pulse),
                        topLeft = Offset(
                            minOf(dragStart.x, dragCurrent.x),
                            minOf(dragStart.y, dragCurrent.y)
                        ),
                        size = Size(
                            kotlin.math.abs(dragCurrent.x - dragStart.x),
                            kotlin.math.abs(dragCurrent.y - dragStart.y)
                        ),
                        style = Stroke(width = 3f)
                    )
                }
            }
        }

        // ── Panel superior con instrucciones ─────────────────
        if (step != CameraStep.SELECT_MATERIAL) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xCC000000))
                    .padding(16.dp)
                    .align(Alignment.TopCenter)
            ) {
                val instruccion = when (step) {
                    CameraStep.DRAW_BAG       -> "🟢 Arrastra para marcar la BOLSA"
                    CameraStep.DRAW_REFERENCE -> "🟡 Arrastra para marcar la REFERENCIA (${selectedRef.name})"
                    CameraStep.CALCULATING    -> "⏳ Calculando..."
                    else -> ""
                }
                Text(instruccion, color = Color.White, fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.align(Alignment.Center))
            }
        }

        // ── Botón atrás ──────────────────────────────────────
        IconButton(
            onClick = {
                when (step) {
                    CameraStep.SELECT_MATERIAL -> onBack()
                    CameraStep.DRAW_BAG        -> step = CameraStep.SELECT_MATERIAL
                    CameraStep.DRAW_REFERENCE  -> { step = CameraStep.DRAW_BAG; bagRect = null }
                    CameraStep.CALCULATING     -> {}
                }
            },
            modifier = Modifier.align(Alignment.TopStart).padding(top = 48.dp, start = 8.dp)
        ) {
            Icon(Icons.Default.ArrowBack, "Atrás", tint = Color.White)
        }

        // ── Panel inferior ───────────────────────────────────
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .background(
                    Color(0xFF1E2A3A),
                    RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
                )
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            when (step) {

                // ── PASO 1: Seleccionar material y referencia ──
                CameraStep.SELECT_MATERIAL -> {
                    Text("Material en la bolsa", color = Color.White,
                        fontSize = 13.sp, fontWeight = FontWeight.SemiBold)

                    ExposedDropdownMenuBox(
                        expanded = expandedMaterial,
                        onExpandedChange = { expandedMaterial = it }
                    ) {
                        OutlinedTextField(
                            value = "${selectedMaterial.emoji} ${selectedMaterial.name}",
                            onValueChange = {},
                            readOnly = true,
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expandedMaterial) },
                            modifier = Modifier.fillMaxWidth().menuAnchor(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor   = Color.White,
                                unfocusedTextColor = Color.White,
                                focusedBorderColor   = Color(0xFF00D4AA),
                                unfocusedBorderColor = Color(0xFF334455)
                            ),
                            shape = RoundedCornerShape(12.dp)
                        )
                        ExposedDropdownMenu(
                            expanded = expandedMaterial,
                            onDismissRequest = { expandedMaterial = false },
                            modifier = Modifier.background(Color(0xFF1E2A3A))
                        ) {
                            WeightCalculator.materials.forEach { mat ->
                                DropdownMenuItem(
                                    text = { Text("${mat.emoji} ${mat.name}", color = Color.White) },
                                    onClick = { selectedMaterial = mat; expandedMaterial = false }
                                )
                            }
                        }
                    }

                    Text("Objeto de referencia en la foto", color = Color.White,
                        fontSize = 13.sp, fontWeight = FontWeight.SemiBold)

                    ExposedDropdownMenuBox(
                        expanded = expandedRef,
                        onExpandedChange = { expandedRef = it }
                    ) {
                        OutlinedTextField(
                            value = "📐 ${selectedRef.name}",
                            onValueChange = {},
                            readOnly = true,
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expandedRef) },
                            modifier = Modifier.fillMaxWidth().menuAnchor(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor   = Color.White,
                                unfocusedTextColor = Color.White,
                                focusedBorderColor   = Color(0xFF00D4AA),
                                unfocusedBorderColor = Color(0xFF334455)
                            ),
                            shape = RoundedCornerShape(12.dp)
                        )
                        ExposedDropdownMenu(
                            expanded = expandedRef,
                            onDismissRequest = { expandedRef = false },
                            modifier = Modifier.background(Color(0xFF1E2A3A))
                        ) {
                            WeightCalculator.referenceObjects.forEach { ref ->
                                DropdownMenuItem(
                                    text = { Text("📐 ${ref.name}", color = Color.White) },
                                    onClick = { selectedRef = ref; expandedRef = false }
                                )
                            }
                        }
                    }

                    Button(
                        onClick = { step = CameraStep.DRAW_BAG },
                        modifier = Modifier.fillMaxWidth().height(52.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00D4AA)),
                        shape = RoundedCornerShape(26.dp)
                    ) {
                        Text("Siguiente → Marcar bolsa", color = Color.Black,
                            fontWeight = FontWeight.Bold)
                    }
                }

                // ── PASO 2: Confirmar rectángulo de la bolsa ───
                CameraStep.DRAW_BAG -> {
                    Text(
                        if (bagRect == null) "Arrastra con el dedo sobre la bolsa"
                        else "✅ Bolsa marcada. ¿Correcto?",
                        color = Color.White, fontSize = 14.sp
                    )
                    if (bagRect != null) {
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedButton(
                                onClick = { bagRect = null },
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White)
                            ) { Text("Redibujar") }

                            Button(
                                onClick = { step = CameraStep.DRAW_REFERENCE },
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00D4AA))
                            ) { Text("Confirmar", color = Color.Black, fontWeight = FontWeight.Bold) }
                        }
                    }
                }

                // ── PASO 3: Confirmar rectángulo de referencia ─
                CameraStep.DRAW_REFERENCE -> {
                    Text(
                        if (refRect == null) "Arrastra sobre el objeto de referencia (${selectedRef.name})"
                        else "✅ Referencia marcada. ¿Correcto?",
                        color = Color.White, fontSize = 14.sp
                    )
                    if (refRect != null) {
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedButton(
                                onClick = { refRect = null },
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White)
                            ) { Text("Redibujar") }

                            Button(
                                onClick = {
                                    step = CameraStep.CALCULATING
                                    // Calcular con los rectángulos dibujados
                                    val bag = bagRect!!
                                    val ref = refRect!!
                                    val result = WeightCalculator.calculate(
                                        bagWidthPx  = bag.width,
                                        bagHeightPx = bag.height,
                                        refWidthPx  = ref.width,
                                        refHeightPx = ref.height,
                                        reference   = selectedRef,
                                        material    = selectedMaterial,
                                        usedFallback = false
                                    )
                                    AppState.lastResult = result
                                    onResult()
                                },
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00D4AA))
                            ) { Text("Calcular peso", color = Color.Black, fontWeight = FontWeight.Bold) }
                        }
                    }
                }

                CameraStep.CALCULATING -> {
                    Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = Color(0xFF00D4AA))
                    }
                }
            }
        }
    }
}