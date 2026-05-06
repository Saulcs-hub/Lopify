package com.carlossaulvillabonapinilla.lopify.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import com.carlossaulvillabonapinilla.lopify.R
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

// ─── Fuente ───────────────────────────────────────────────────────────────────
val FredokaFamily = FontFamily(
    Font(R.font.fredoka_semibold, FontWeight.SemiBold)
)

// ─── Colores del gradiente (igual al diseño Figma) ───────────────────────────
private val GreenLight = Color(0xFFB5F5A0)
private val GreenMid   = Color(0xFF6EE85A)
private val GreenVib   = Color(0xFF3DD92A)
private val LogoColor  = Color(0xFF1E3A1E)

// ─── Splash Screen ────────────────────────────────────────────────────────────
@Composable
fun LoopifySplashScreen(
    onSplashFinished: () -> Unit = {}
) {
    val alphaAnim = remember { Animatable(0f) }
    val scaleAnim = remember { Animatable(0.85f) }

    LaunchedEffect(Unit) {
        launch {
            alphaAnim.animateTo(
                targetValue = 1f,
                animationSpec = tween(durationMillis = 700, easing = EaseOutCubic)
            )
        }
        launch {
            scaleAnim.animateTo(
                targetValue = 1f,
                animationSpec = tween(durationMillis = 700, easing = EaseOutBack)
            )
        }

        delay(2000)
        onSplashFinished()
    }

    Box(
        modifier = Modifier

            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(GreenLight, GreenMid, GreenVib)
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "LOOPIFY",
            color = LogoColor,
            fontSize = 50.sp,
            fontFamily = FredokaFamily,
            fontWeight = FontWeight.SemiBold,
            letterSpacing = 1.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .alpha(alphaAnim.value)
                .scale(scaleAnim.value)
        )
    }
}

// ─── Preview ──────────────────────────────────────────────────────────────────
@Preview(
    showBackground = true,
    showSystemUi = true,
    device = "spec:width=390dp,height=844dp,dpi=460"
)
@Composable
fun LoopifySplashScreenPreview() {
    LoopifySplashScreen()
}