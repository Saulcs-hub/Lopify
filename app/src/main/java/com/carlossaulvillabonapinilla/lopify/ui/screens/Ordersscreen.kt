package com.carlossaulvillabonapinilla.lopify.ui.screens

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.carlossaulvillabonapinilla.lopify.R

// ─── COLORES (mismos que HomeScreen) ─────────────────────────────────────────
private val IconGreen         = Color(0xFF0A1A05)
private val BackgroundSurface = Color(0xFF93E575)
private val GlassSelector     = Color(0x33FFFFFF)
private val TextBlack         = Color(0xFF1A1A1A)
private val DarkGreenText     = Color(0xFF1B5E20)
private val LightGrayText     = Color(0xFF757575)

// ─── DATA CLASSES ─────────────────────────────────────────────────────────────
data class OrderData(
    val id: String,
    val material: String,
    val recyclerName: String,
    val timeAgo: String,
    val location: String,
    val distance: String,
    val weight: String,
    val status: OrderStatus,
    val iconRes: Int
)

enum class OrderStatus { PENDING, EN_ROUTE, COMPLETED }

// ─── PANTALLA PRINCIPAL ───────────────────────────────────────────────────────
@Composable
fun OrdersScreen(
    onNavigateToHome: () -> Unit = {},
    onNavigateToMap: () -> Unit = {},
    onNavigateToProfile: () -> Unit = {}
) {
    var selectedFilter by remember { mutableStateOf(0) }
    var selectedNavIndex by remember { mutableStateOf(1) }

    val filters = listOf("Todos", "Pendiente", "En camino", "Completado")

    val allOrders = listOf(
        OrderData("RP-0042", "Plástico + Cartón", "Carlos M.", "hace 8 min",
            "Punto Carrera 15", "0.8 km", "4.5 kg", OrderStatus.EN_ROUTE, R.drawable.icon_plastico),
        OrderData("RP-0041", "Papel + Cartón", "Sin asignar", "hace 15 min",
            "Punto San Francisco", "0.3 km", "2.0 kg", OrderStatus.PENDING, R.drawable.icon_papel),
        OrderData("RP-0040", "Vidrio", "Ana L.", "ayer 14:30",
            "Parque San Pío", "0.5 km", "3.2 kg", OrderStatus.COMPLETED, R.drawable.icon_vidrio),
        OrderData("RP-0039", "Cartón", "Pedro R.", "hace 2 días",
            "Punto Cabecera", "1.2 km", "6.0 kg", OrderStatus.COMPLETED, R.drawable.icon_carton)
    )

    val filteredOrders = when (selectedFilter) {
        1 -> allOrders.filter { it.status == OrderStatus.PENDING }
        2 -> allOrders.filter { it.status == OrderStatus.EN_ROUTE }
        3 -> allOrders.filter { it.status == OrderStatus.COMPLETED }
        else -> allOrders
    }

    val activeCount = allOrders.count { it.status == OrderStatus.EN_ROUTE || it.status == OrderStatus.PENDING }

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
            // ── Header ──────────────────────────────────────────
            item {
                OrdersHeader(
                    activeCount = activeCount,
                    modifier = Modifier.statusBarsPadding()
                )
            }

            // ── Filtros ──────────────────────────────────────────
            item {
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 24.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.padding(bottom = 16.dp)
                ) {
                    items(filters.size) { index ->
                        FilterChip(
                            label = filters[index],
                            isSelected = selectedFilter == index,
                            onClick = { selectedFilter = index }
                        )
                    }
                }
            }

            // ── Tracker del pedido activo ──────────────────────
            val enRouteOrder = allOrders.firstOrNull { it.status == OrderStatus.EN_ROUTE }
            if (enRouteOrder != null && (selectedFilter == 0 || selectedFilter == 2)) {
                item {
                    OrderProgressTracker(order = enRouteOrder)
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }

            // ── Sección: Pedidos activos ────────────────────────
            val activeOrders = filteredOrders.filter { it.status != OrderStatus.COMPLETED }
            if (activeOrders.isNotEmpty()) {
                item {
                    SectionHeader(title = "Pedidos activos", icon = R.drawable.delivery_home)
                }
                items(activeOrders) { order ->
                    OrderCard(order = order, showActions = true)
                    Spacer(modifier = Modifier.height(10.dp))
                }
            }

            // ── Sección: Historial ──────────────────────────────
            val completedOrders = filteredOrders.filter { it.status == OrderStatus.COMPLETED }
            if (completedOrders.isNotEmpty()) {
                item {
                    Spacer(modifier = Modifier.height(8.dp))
                    SectionHeader(title = "Historial", icon = R.drawable.bowl_reciclaje)
                }
                items(completedOrders) { order ->
                    OrderCard(order = order, showActions = false)
                    Spacer(modifier = Modifier.height(10.dp))
                }
            }

            // ── Estado vacío ────────────────────────────────────
            if (filteredOrders.isEmpty()) {
                item { EmptyOrdersState() }
            }
        }

        // ── Nav Bar ─────────────────────────────────────────────
        OrdersNavBar(
            selectedIndex = selectedNavIndex,
            onItemSelected = { index ->
                selectedNavIndex = index
                when (index) {
                    0 -> onNavigateToHome()
                    // 1 = Pedidos (pantalla actual, no navega)
                    3 -> onNavigateToMap()
                    4 -> onNavigateToProfile()
                }
            },
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}

// ─── 1. HEADER ────────────────────────────────────────────────────────────────
@Composable
fun OrdersHeader(activeCount: Int, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 24.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = "Mis Pedidos",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = TextBlack
            )
            Text(
                text = "$activeCount activos ahora",
                fontSize = 14.sp,
                color = DarkGreenText,
                fontWeight = FontWeight.Medium
            )
        }

        // Badge contador
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = DarkGreenText
        ) {
            Text(
                text = "$activeCount activos",
                color = Color.White,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
            )
        }
    }
}

// ─── 2. FILTRO CHIP ───────────────────────────────────────────────────────────
@Composable
fun FilterChip(label: String, isSelected: Boolean, onClick: () -> Unit) {
    val bgColor by animateColorAsState(
        targetValue = if (isSelected) DarkGreenText else Color.White.copy(alpha = 0.5f),
        animationSpec = tween(200),
        label = "chipColor"
    )
    val textColor by animateColorAsState(
        targetValue = if (isSelected) Color.White else TextBlack,
        animationSpec = tween(200),
        label = "chipText"
    )

    Surface(
        shape = RoundedCornerShape(20.dp),
        color = bgColor,
        modifier = Modifier.clickable(
            interactionSource = remember { MutableInteractionSource() },
            indication = null,
            onClick = onClick
        )
    ) {
        Text(
            text = label,
            color = textColor,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )
    }
}

// ─── 3. TRACKER DE PROGRESO ───────────────────────────────────────────────────
@Composable
fun OrderProgressTracker(order: OrderData) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp),
        shape = RoundedCornerShape(24.dp),
        color = Color.White.copy(alpha = 0.75f)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "📦 Pedido #${order.id} — En camino",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = TextBlack
            )
            Spacer(modifier = Modifier.height(14.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Paso 1: Solicitado ✓
                TrackStep(label = "Solicitado", state = TrackState.DONE, icon = R.drawable.icon_papel) // reemplaza con tu icono check
                TrackLine(done = true)
                // Paso 2: Asignado ✓
                TrackStep(label = "Asignado", state = TrackState.DONE, icon = R.drawable.icon_papel)
                TrackLine(done = true)
                // Paso 3: En camino (activo)
                TrackStep(label = "En camino", state = TrackState.ACTIVE, icon = R.drawable.delivery_home)
                TrackLine(done = false)
                // Paso 4: Entregado (pendiente)
                TrackStep(label = "Entregado", state = TrackState.PENDING, icon = R.drawable.bowl_reciclaje)
            }
        }
    }
}

enum class TrackState { DONE, ACTIVE, PENDING }

@Composable
fun RowScope.TrackStep(label: String, state: TrackState, icon: Int) {
    val bgColor = when (state) {
        TrackState.DONE    -> Color(0xFF2ECC40)
        TrackState.ACTIVE  -> DarkGreenText
        TrackState.PENDING -> Color.White.copy(alpha = 0.5f)
    }
    val iconAlpha = if (state == TrackState.PENDING) 0.3f else 1f

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .background(bgColor, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            // Pon aquí el ícono o texto de cada paso
            // Si tienes íconos propios para check/bici/etc úsalos
            // Por ahora usamos los que tengas disponibles:
            Image(
                painter = painterResource(id = icon),
                contentDescription = label,
                modifier = Modifier
                    .size(18.dp)
                    .alpha(iconAlpha)
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = label,
            fontSize = 9.sp,
            fontWeight = FontWeight.Bold,
            color = LightGrayText
        )
    }
}

@Composable
fun RowScope.TrackLine(done: Boolean) {
    Box(
        modifier = Modifier
            .weight(1f)
            .height(3.dp)
            .offset(y = (-10).dp)
            .background(
                color = if (done) Color(0xFF2ECC40) else Color(0x33000000),
                shape = RoundedCornerShape(2.dp)
            )
    )
}

// ─── 4. SECCIÓN HEADER ────────────────────────────────────────────────────────
@Composable
fun SectionHeader(title: String, icon: Int) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = title,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = TextBlack
            )
            Spacer(modifier = Modifier.width(8.dp))
            Image(
                painter = painterResource(id = icon),
                contentDescription = null,
                modifier = Modifier.size(24.dp)
            )
        }
        Text(
            text = "Ver todos",
            color = DarkGreenText,
            fontWeight = FontWeight.Bold,
            fontSize = 13.sp,
            modifier = Modifier.clickable { }
        )
    }
}

// ─── 5. TARJETA DE PEDIDO ─────────────────────────────────────────────────────
@Composable
fun OrderCard(order: OrderData, showActions: Boolean) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp),
        shape = RoundedCornerShape(20.dp),
        color = Color.White.copy(alpha = 0.75f)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {

            // Fila principal
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Ícono del material
                Box(
                    modifier = Modifier
                        .size(50.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(Color.White),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = order.iconRes),
                        contentDescription = null,
                        modifier = Modifier.size(30.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                // Info central
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = order.material,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = TextBlack
                    )
                    Text(
                        text = "${order.recyclerName} · ${order.timeAgo}",
                        fontSize = 12.sp,
                        color = LightGrayText
                    )
                }

                // Info derecha
                Column(horizontalAlignment = Alignment.End) {
                    OrderStatusPill(status = order.status)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = order.weight,
                        fontWeight = FontWeight.Bold,
                        color = DarkGreenText,
                        fontSize = 13.sp
                    )
                }
            }

            // Fila ubicación
            Spacer(modifier = Modifier.height(10.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 2.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Punto verde de ubicación
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .background(
                            color = if (order.status == OrderStatus.COMPLETED) Color(0xFF4CAF50) else Color(0xFF2ECC40),
                            shape = CircleShape
                        )
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = order.location,
                    fontSize = 12.sp,
                    color = LightGrayText,
                    modifier = Modifier.weight(1f)
                )
                Text(
                    text = if (order.status == OrderStatus.COMPLETED) "+0.5 ⭐" else order.distance,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = DarkGreenText
                )
            }

            // Botones de acción (solo pedidos activos)
            if (showActions && order.status != OrderStatus.COMPLETED) {
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Botón secundario
                    Surface(
                        modifier = Modifier
                            .weight(1f)
                            .clickable { },
                        shape = RoundedCornerShape(12.dp),
                        color = Color.Transparent,
                        border = androidx.compose.foundation.BorderStroke(
                            width = 1.5.dp,
                            color = DarkGreenText.copy(alpha = 0.3f)
                        )
                    ) {
                        Text(
                            text = "Ver mapa",
                            color = DarkGreenText,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 10.dp)
                                .wrapContentWidth(Alignment.CenterHorizontally)
                        )
                    }
                    // Botón primario
                    Surface(
                        modifier = Modifier
                            .weight(1f)
                            .clickable { },
                        shape = RoundedCornerShape(12.dp),
                        color = DarkGreenText
                    ) {
                        Text(
                            text = "Contactar",
                            color = Color.White,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 10.dp)
                                .wrapContentWidth(Alignment.CenterHorizontally)
                        )
                    }
                }
            }
        }
    }
}

// ─── 6. PILL DE ESTADO ────────────────────────────────────────────────────────
@Composable
fun OrderStatusPill(status: OrderStatus) {
    val (bgColor, textColor, label) = when (status) {
        OrderStatus.PENDING   -> Triple(Color(0xFFFFF9C4), Color(0xFF827717), "Pendiente")
        OrderStatus.EN_ROUTE  -> Triple(Color(0xFFE3F2FD), Color(0xFF1565C0), "En camino")
        OrderStatus.COMPLETED -> Triple(Color(0xFFE8F5E9), Color(0xFF2E7D32), "Completado")
    }
    Surface(shape = RoundedCornerShape(20.dp), color = bgColor) {
        Text(
            text = label,
            color = textColor,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 3.dp)
        )
    }
}

// ─── 7. ESTADO VACÍO ──────────────────────────────────────────────────────────
@Composable
fun EmptyOrdersState() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 60.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = R.drawable.bowl_reciclaje),
            contentDescription = null,
            modifier = Modifier.size(80.dp).alpha(0.4f)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "No hay pedidos aquí",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = TextBlack.copy(alpha = 0.5f)
        )
        Text(
            text = "¡Empieza a reciclar hoy!",
            fontSize = 13.sp,
            color = LightGrayText
        )
    }
}

// ─── 8. NAV BAR (igual que HomeScreen) ───────────────────────────────────────
@Composable
fun OrdersNavBar(
    selectedIndex: Int,
    onItemSelected: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shadowElevation = 24.dp,
        color = BackgroundSurface,
        shape = RoundedCornerShape(topStart = 30.dp, topEnd = 30.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp, horizontal = 8.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            OrdersNavItem(R.drawable.icon_home, "Inicio", selectedIndex == 0)   { onItemSelected(0) }
            OrdersNavItem(R.drawable.delivery_home, "Pedidos", selectedIndex == 1) { onItemSelected(1) }

            // Botón central Enviar
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
                Text("Enviar", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = IconGreen)
            }

            OrdersNavItem(R.drawable.mapa_home, "Mapa", selectedIndex == 3)   { onItemSelected(3) }
            OrdersNavItem(R.drawable.perfil_home, "Perfil", selectedIndex == 4) { onItemSelected(4) }
        }
    }
}

@Composable
private fun OrdersNavItem(iconRes: Int, label: String, selected: Boolean, onClick: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .background(if (selected) GlassSelector else Color.Transparent)
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
            color = IconGreen,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium
        )
    }
}
//