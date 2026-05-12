package com.carlossaulvillabonapinilla.lopify.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.carlossaulvillabonapinilla.lopify.R

// ─── COLORES (mismos que HomeScreen) ─────────────────────────────────────────
private val ProfIconGreen         = Color(0xFF0A1A05)
private val ProfBackgroundSurface = Color(0xFF93E575)
private val ProfGlassSelector     = Color(0x33FFFFFF)
private val ProfTextBlack         = Color(0xFF1A1A1A)
private val ProfDarkGreenText     = Color(0xFF1B5E20)
private val ProfLightGrayText     = Color(0xFF757575)

// ─── DATA CLASSES ─────────────────────────────────────────────────────────────
data class Achievement(
    val name: String,
    val description: String,
    val iconRes: Int,
    val isUnlocked: Boolean,
    val requiredDays: Int = 0
)

data class UserStats(
    val kgThisMonth: Double,
    val totalDeliveries: Int,
    val greenPoints: Int,
    val currentStreak: Int,
    val bestStreak: Int,
    val level: Int,
    val levelName: String
)

// ─── PANTALLA PRINCIPAL ───────────────────────────────────────────────────────
@Composable
fun ProfileScreen(
    onNavigateToHome: () -> Unit = {},
    onNavigateToOrders: () -> Unit = {},
    onNavigateToMap: () -> Unit = {}
) {
    var selectedNavIndex by remember { mutableStateOf(4) }

    val userStats = UserStats(
        kgThisMonth = 23.5,
        totalDeliveries = 47,
        greenPoints = 320,
        currentStreak = 7,
        bestStreak = 7,
        level = 4,
        levelName = "Reciclador Experto"
    )

    val achievements = listOf(
        Achievement("Primer Reciclaje", "Tu primera entrega", R.drawable.bowl_reciclaje, true),
        Achievement("Racha de 3 días", "3 días seguidos reciclando", R.drawable.fuego_home, true, 3),
        Achievement("10 kg Reciclados", "Alcanzaste 10 kg este mes", R.drawable.bowl_reciclaje, true),
        Achievement("Guardián Verde", "Racha de 8 días", R.drawable.bowl_reciclaje, false, 8),
        Achievement("Maestro del Vidrio", "20 entregas de vidrio", R.drawable.icon_vidrio, false),
        Achievement("Héroe del Cartón", "15 entregas de cartón", R.drawable.icon_carton, false)
    )

    // Próxima recompensa a desbloquear
    val nextAchievement = achievements.firstOrNull { !it.isUnlocked }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(Color(0xFF2ECC40), Color(0xFFA4DCA4), Color(0xFFBBEABB)),
                    startY = 0f, endY = 1200f
                )
            )
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 120.dp)
        ) {
            // ── Header del perfil ────────────────────────────
            item {
                ProfileHeader(
                    stats = userStats,
                    modifier = Modifier.statusBarsPadding()
                )
            }

            // ── Stats resumidos ──────────────────────────────
            item {
                Spacer(modifier = Modifier.height(4.dp))
                ProfileStatsRow(stats = userStats)
            }

            // ── Tarjeta de Racha ─────────────────────────────
            item {
                Spacer(modifier = Modifier.height(16.dp))
                StreakCard(stats = userStats)
            }

            // ── Próxima recompensa ───────────────────────────
            nextAchievement?.let {
                item {
                    Spacer(modifier = Modifier.height(12.dp))
                    NextRewardCard(
                        achievement = it,
                        currentStreak = userStats.currentStreak
                    )
                }
            }

            // ── Logros ──────────────────────────────────────
            item {
                Spacer(modifier = Modifier.height(12.dp))
                AchievementsSection(achievements = achievements)
            }

            // ── Configuración / extras ───────────────────────
            item {
                Spacer(modifier = Modifier.height(12.dp))
                ProfileOptionsSection()
            }
        }

        // ── Nav Bar ─────────────────────────────────────────
        ProfileNavBar(
            selectedIndex = selectedNavIndex,
            onItemSelected = { index ->
                selectedNavIndex = index
                when (index) {
                    0 -> onNavigateToHome()
                    1 -> onNavigateToOrders()
                    // 2 = botón Enviar (cámara, pendiente)
                    3 -> onNavigateToMap()
                    // 4 = Perfil, pantalla actual, no navega
                }
            },
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}

// ─── 1. HEADER DEL PERFIL ─────────────────────────────────────────────────────
@Composable
fun ProfileHeader(stats: UserStats, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 20.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Avatar con ring de nivel
        Box(
            modifier = Modifier.size(80.dp),
            contentAlignment = Alignment.Center
        ) {
            // Ring exterior (gradiente de nivel)
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        brush = Brush.linearGradient(
                            listOf(Color(0xFF2ECC40), Color(0xFF1B5E20))
                        ),
                        shape = CircleShape
                    )
                    .padding(3.dp)
            )
            // Avatar interior
            Box(
                modifier = Modifier
                    .size(74.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFEBC1CC)),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.avatar),
                    contentDescription = "Foto de perfil",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }
            // Badge de nivel
            Surface(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .size(22.dp),
                shape = CircleShape,
                color = Color(0xFF1B5E20),
                border = androidx.compose.foundation.BorderStroke(2.dp, Color.White)
            ) {
                Text(
                    text = "${stats.level}",
                    color = Color.White,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.wrapContentSize(Alignment.Center)
                )
            }
        }

        Spacer(modifier = Modifier.width(16.dp))

        // Nombre y nivel
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "Maria Garcia",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = ProfTextBlack
            )
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(top = 2.dp)
            ) {
                // Icono hoja (usa tu recurso propio)
                Image(
                    painter = painterResource(id = R.drawable.fuego_home), // ← reemplaza con icon_hoja si lo tienes
                    contentDescription = null,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = stats.levelName + " · Nivel ${stats.level}",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = ProfDarkGreenText
                )
            }
        }

        // Botón editar
        Surface(
            modifier = Modifier.clickable { },
            shape = RoundedCornerShape(12.dp),
            color = Color.White.copy(alpha = 0.6f)
        ) {
            Text(
                text = "Editar",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = ProfTextBlack,
                modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
            )
        }
    }
}

// ─── 2. STATS ROW ─────────────────────────────────────────────────────────────
@Composable
fun ProfileStatsRow(stats: UserStats) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        StatBox(
            value = "${stats.kgThisMonth} kg",
            label = "este mes",
            modifier = Modifier.weight(1f)
        )
        StatBox(
            value = "${stats.totalDeliveries}",
            label = "entregas",
            modifier = Modifier.weight(1f)
        )
        StatBox(
            value = "${stats.greenPoints}",
            label = "puntos verdes",
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
fun StatBox(value: String, label: String, modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        color = Color.White.copy(alpha = 0.7f)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(vertical = 14.dp, horizontal = 8.dp)
        ) {
            Text(
                text = value,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = ProfDarkGreenText
            )
            Text(
                text = label,
                fontSize = 10.sp,
                fontWeight = FontWeight.Medium,
                color = ProfLightGrayText,
                textAlign = TextAlign.Center
            )
        }
    }
}

// ─── 3. TARJETA DE RACHA ──────────────────────────────────────────────────────
@Composable
fun StreakCard(stats: UserStats) {
    // Animación de pulso del fuego
    val infiniteTransition = rememberInfiniteTransition(label = "streakPulse")
    val fireScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = EaseInOut),
            repeatMode = RepeatMode.Reverse
        ),
        label = "fireScale"
    )

    val weekDays = listOf("Lun", "Mar", "Mié", "Jue", "Vie", "Sáb", "Hoy")
    // Los primeros N días están completos según la racha
    val streakDays = stats.currentStreak.coerceAtMost(7)

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp),
        shape = RoundedCornerShape(24.dp),
        color = Color.White.copy(alpha = 0.8f)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {

            // Fila superior: fuego + número + record
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                // Fuego animado (usa tu recurso)
                Box(
                    modifier = Modifier
                        .size((32 * fireScale).dp)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.fuego_home),
                        contentDescription = "Racha",
                        modifier = Modifier.fillMaxSize()
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.Bottom) {
                        Text(
                            text = "${stats.currentStreak}",
                            fontSize = 40.sp,
                            fontWeight = FontWeight.Bold,
                            color = ProfTextBlack,
                            lineHeight = 44.sp
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "días de racha",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = ProfTextBlack,
                            modifier = Modifier.padding(bottom = 6.dp)
                        )
                    }
                    if (stats.currentStreak >= stats.bestStreak) {
                        Text(
                            text = "¡Tu mejor racha! Récord personal 🏆",
                            fontSize = 12.sp,
                            color = ProfLightGrayText
                        )
                    } else {
                        Text(
                            text = "Récord: ${stats.bestStreak} días",
                            fontSize = 12.sp,
                            color = ProfLightGrayText
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Etiquetas de días
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                weekDays.forEachIndexed { index, day ->
                    Text(
                        text = day,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (index == 6) Color(0xFFF4845F) else ProfLightGrayText,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Barras de días
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                weekDays.forEachIndexed { index, _ ->
                    val isDone    = index < streakDays - 1
                    val isToday   = index == streakDays - 1
                    val isEmpty   = index >= streakDays

                    val barColor = when {
                        isDone  -> Brush.horizontalGradient(listOf(Color(0xFF2ECC40), Color(0xFF1B5E20)))
                        isToday -> Brush.horizontalGradient(listOf(Color(0xFFF9C74F), Color(0xFFF4845F)))
                        else    -> Brush.horizontalGradient(listOf(Color(0x1A000000), Color(0x1A000000)))
                    }

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(8.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(barColor)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Mensaje motivacional
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = Color(0xFF2ECC40).copy(alpha = 0.1f)
            ) {
                Text(
                    text = "🎯  ¡1 día más para desbloquear \"Guardián Verde\"!",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = ProfDarkGreenText,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 10.dp, horizontal = 12.dp)
                )
            }
        }
    }
}

// ─── 4. PRÓXIMA RECOMPENSA ────────────────────────────────────────────────────
@Composable
fun NextRewardCard(achievement: Achievement, currentStreak: Int) {
    val progress = if (achievement.requiredDays > 0)
        (currentStreak.toFloat() / achievement.requiredDays).coerceIn(0f, 1f)
    else 0.7f  // fallback para logros no basados en racha

    // Animación de la barra de progreso
    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = tween(durationMillis = 1000, easing = EaseOut),
        label = "progressBar"
    )

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp),
        shape = RoundedCornerShape(24.dp),
        color = Color.White.copy(alpha = 0.7f),
        border = androidx.compose.foundation.BorderStroke(1.5.dp, Color(0xFF2ECC40).copy(alpha = 0.4f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "⚡ Próxima recompensa",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = ProfDarkGreenText
            )
            Spacer(modifier = Modifier.height(10.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFFF0F4C3)),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = achievement.iconRes),
                        contentDescription = null,
                        modifier = Modifier.size(28.dp)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(achievement.name, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = ProfTextBlack)
                    Text(achievement.description, fontSize = 11.sp, color = ProfLightGrayText)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Barra de progreso
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(10.dp)
                    .clip(RoundedCornerShape(5.dp))
                    .background(Color(0x1A000000))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(animatedProgress)
                        .fillMaxHeight()
                        .background(
                            brush = Brush.horizontalGradient(
                                listOf(Color(0xFF2ECC40), Color(0xFF1B5E20))
                            ),
                            shape = RoundedCornerShape(5.dp)
                        )
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = if (achievement.requiredDays > 0)
                        "$currentStreak de ${achievement.requiredDays} días"
                    else "70% completado",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = ProfLightGrayText
                )
                Text(
                    text = "${(animatedProgress * 100).toInt()}%",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = ProfDarkGreenText
                )
            }
        }
    }
}

// ─── 5. SECCIÓN DE LOGROS ─────────────────────────────────────────────────────
@Composable
fun AchievementsSection(achievements: List<Achievement>) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp),
        shape = RoundedCornerShape(24.dp),
        color = Color.White.copy(alpha = 0.7f)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header de sección
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "Mis Logros",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = ProfTextBlack
                )
                Spacer(modifier = Modifier.width(8.dp))
                Image(
                    painter = painterResource(id = R.drawable.bowl_reciclaje),
                    contentDescription = null,
                    modifier = Modifier.size(22.dp)
                )
                Spacer(modifier = Modifier.weight(1f))
                val unlockedCount = achievements.count { it.isUnlocked }
                Text(
                    text = "$unlockedCount / ${achievements.size}",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = ProfDarkGreenText
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Grid 2 columnas
            val chunked = achievements.chunked(2)
            chunked.forEach { row ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    row.forEach { achievement ->
                        AchievementItem(
                            achievement = achievement,
                            modifier = Modifier.weight(1f)
                        )
                    }
                    // Si la fila tiene solo 1 elemento, balancear
                    if (row.size == 1) Spacer(modifier = Modifier.weight(1f))
                }
                Spacer(modifier = Modifier.height(10.dp))
            }
        }
    }
}

@Composable
fun AchievementItem(achievement: Achievement, modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier.alpha(if (achievement.isUnlocked) 1f else 0.45f),
        shape = RoundedCornerShape(16.dp),
        color = if (achievement.isUnlocked) Color.White else Color(0xFFF5F5F5),
        border = if (achievement.isUnlocked)
            androidx.compose.foundation.BorderStroke(1.5.dp, Color(0xFF2ECC40))
        else null
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(contentAlignment = Alignment.TopEnd) {
                Image(
                    painter = painterResource(id = achievement.iconRes),
                    contentDescription = achievement.name,
                    modifier = Modifier.size(36.dp)
                )
                if (achievement.isUnlocked) {
                    Surface(
                        modifier = Modifier
                            .size(16.dp)
                            .offset(x = 4.dp, y = (-4).dp),
                        shape = CircleShape,
                        color = Color(0xFF2ECC40)
                    ) {
                        Text(
                            text = "✓",
                            color = Color.White,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.wrapContentSize(Alignment.Center)
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = achievement.name,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = ProfTextBlack,
                textAlign = TextAlign.Center
            )
            Text(
                text = if (achievement.isUnlocked) "¡Desbloqueado!" else achievement.description,
                fontSize = 9.sp,
                color = if (achievement.isUnlocked) ProfDarkGreenText else ProfLightGrayText,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 2.dp)
            )
        }
    }
}

// ─── 6. SECCIÓN OPCIONES ──────────────────────────────────────────────────────
@Composable
fun ProfileOptionsSection() {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp),
        shape = RoundedCornerShape(24.dp),
        color = Color.White.copy(alpha = 0.7f)
    ) {
        Column(modifier = Modifier.padding(8.dp)) {
            listOf(
                Triple(R.drawable.icon_papel,   "Historial de reciclaje", true),
                Triple(R.drawable.bowl_reciclaje, "Puntos y recompensas",   true),
                Triple(R.drawable.fuego_home,   "Configuración",           false)
            ).forEach { (icon, label, hasDivider) ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) { }
                        .padding(horizontal = 10.dp, vertical = 14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .background(Color(0xFF2ECC40).copy(alpha = 0.1f), RoundedCornerShape(10.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = painterResource(id = icon),
                            contentDescription = null,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(14.dp))
                    Text(
                        text = label,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = ProfTextBlack,
                        modifier = Modifier.weight(1f)
                    )
                    // Flecha derecha (usa tu recurso de flecha)
                    Image(
                        painter = painterResource(id = R.drawable.icon_papel), // ← reemplaza con flecha_derecha
                        contentDescription = null,
                        modifier = Modifier.size(16.dp).alpha(0.3f)
                    )
                }
                if (hasDivider) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(1.dp)
                            .background(Color(0x0A000000))
                            .padding(horizontal = 10.dp)
                    )
                }
            }
        }
    }
}

// ─── 7. NAV BAR ───────────────────────────────────────────────────────────────
@Composable
fun ProfileNavBar(
    selectedIndex: Int,
    onItemSelected: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shadowElevation = 24.dp,
        color = ProfBackgroundSurface,
        shape = RoundedCornerShape(topStart = 30.dp, topEnd = 30.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp, horizontal = 8.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            ProfNavItem(R.drawable.icon_home,     "Inicio",  selectedIndex == 0) { onItemSelected(0) }
            ProfNavItem(R.drawable.delivery_home, "Pedidos", selectedIndex == 1) { onItemSelected(1) }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.offset(y = (-15).dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(70.dp)
                        .clip(CircleShape)
                        .background(
                            brush = Brush.verticalGradient(
                                listOf(Color(0xFF8EDF7C), Color(0xFF2D741C))
                            )
                        )
                        .clickable { onItemSelected(2) },
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.camara_home),
                        contentDescription = "Enviar",
                        modifier = Modifier.size(38.dp)
                    )
                }
                Text("Enviar", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = ProfIconGreen)
            }

            ProfNavItem(R.drawable.mapa_home,   "Mapa",   selectedIndex == 3) { onItemSelected(3) }
            ProfNavItem(R.drawable.perfil_home, "Perfil", selectedIndex == 4) { onItemSelected(4) }
        }
    }
}

@Composable
private fun ProfNavItem(iconRes: Int, label: String, selected: Boolean, onClick: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .background(if (selected) ProfGlassSelector else Color.Transparent)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) { onClick() }
            .padding(horizontal = 12.dp, vertical = 8.dp)
    ) {
        Image(
            painter = painterResource(id = iconRes),
            contentDescription = label,
            modifier = Modifier
                .size(32.dp)
                .alpha(if (selected) 1f else 0.6f)
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = label,
            fontSize = 11.sp,
            color = ProfIconGreen,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium
        )
    }
}