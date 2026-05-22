package com.carlossaulvillabonapinilla.lopify.ui.screens

import android.graphics.BitmapFactory
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
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.carlossaulvillabonapinilla.lopify.R
import com.mapbox.geojson.Point
import com.mapbox.maps.extension.compose.MapboxMap
import com.mapbox.maps.extension.compose.animation.viewport.rememberMapViewportState
import com.mapbox.maps.extension.compose.annotation.generated.PointAnnotation
import com.mapbox.maps.extension.compose.annotation.generated.PolylineAnnotation

// ─── COLORES (mismos que HomeScreen) ─────────────────────────────────────────
private val MapIconGreen         = Color(0xFF0A1A05)
private val MapBackgroundSurface = Color(0xFF93E575)
private val MapGlassSelector     = Color(0x33FFFFFF)
private val MapTextBlack         = Color(0xFF1A1A1A)
private val MapDarkGreenText     = Color(0xFF1B5E20)
private val MapLightGrayText     = Color(0xFF757575)

// ─── DATA CLASS ───────────────────────────────────────────────────────────────
data class RecyclePoint(
    val name: String,
    val address: String,
    val distance: String,
    val distanceKm: Double,
    val materials: List<String>,
    val lat: Double,
    val lng: Double,
    val iconRes: Int
)

// ─── PANTALLA PRINCIPAL ───────────────────────────────────────────────────────
@Composable
fun MapScreen(
    onNavigateToHome: () -> Unit = {},
    onNavigateToOrders: () -> Unit = {},
    onNavigateToProfile: () -> Unit = {},
    onNavigateToCamera: () -> Unit = {}
) {
    var selectedFilter by rememberSaveable  { mutableStateOf(0) }
    var selectedNavIndex by rememberSaveable  { mutableStateOf(3) }
    var selectedPoint by remember { mutableStateOf<RecyclePoint?>(null) }

    val materialFilters = listOf("Todos", "Papel", "Vidrio", "Plástico", "Cartón")

    val allPoints = listOf(
        RecyclePoint(
            name = "Parque San Pío",
            address = "Cra. 35 #48-12, Bucaramanga",
            distance = "0.8 km",
            distanceKm = 0.8,
            materials = listOf("Papel", "Vidrio", "Plástico"),
            lat = 7.1189, lng = -73.1158,
            iconRes = R.drawable.bowl_reciclaje
        ),
        RecyclePoint(
            name = "Centro Reciclaje SF",
            address = "Cra. 27 #51-40",
            distance = "1.2 km",
            distanceKm = 1.2,
            materials = listOf("Cartón", "Papel"),
            lat = 7.1200, lng = -73.1172,
            iconRes = R.drawable.icon_papel
        ),
        RecyclePoint(
            name = "Punto Cabecera",
            address = "Cll. 52 #34-10",
            distance = "2.1 km",
            distanceKm = 2.1,
            materials = listOf("Vidrio", "Plástico"),
            lat = 7.1210, lng = -73.1140,
            iconRes = R.drawable.icon_vidrio
        ),
        RecyclePoint(
            name = "Punto Carrera 15",
            address = "Cra. 15 #20-30",
            distance = "0.3 km",
            distanceKm = 0.3,
            materials = listOf("Plástico", "Cartón"),
            lat = 7.1175, lng = -73.1165,
            iconRes = R.drawable.icon_plastico
        )
    )

    val filteredPoints = if (selectedFilter == 0) allPoints
    else allPoints.filter { point ->
        point.materials.contains(materialFilters[selectedFilter])
    }

    // Seleccionar el punto más cercano por defecto
    LaunchedEffect(filteredPoints) {
        selectedPoint = filteredPoints.minByOrNull { it.distanceKm }
    }

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
                MapHeader(
                    pointCount = filteredPoints.size,
                    modifier = Modifier.statusBarsPadding()
                )
            }

            // ── Barra de búsqueda (decorativa) ──────────────────
            item { MapSearchBar() }

            // ── Mapa Mapbox ──────────────────────────────────────
            item {
                MapBoxSection(
                    points = filteredPoints,
                    selectedPoint = selectedPoint,
                    onPointSelected = { selectedPoint = it }
                )
            }

            // ── Filtros de material ──────────────────────────────
            item {
                Spacer(modifier = Modifier.height(16.dp))
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 24.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(materialFilters.size) { index ->
                        MapFilterChip(
                            label = materialFilters[index],
                            isSelected = selectedFilter == index,
                            onClick = { selectedFilter = index }
                        )
                    }
                }
            }

            // ── Punto seleccionado destacado ─────────────────────
            selectedPoint?.let { point ->
                item {
                    Spacer(modifier = Modifier.height(14.dp))
                    SelectedPointCard(point = point)
                }
            }

            // ── Lista de puntos cercanos ─────────────────────────
            item {
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Puntos cercanos",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = MapTextBlack
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Image(
                        painter = painterResource(id = R.drawable.punto),
                        contentDescription = null,
                        modifier = Modifier.size(24.dp)
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))
            }

            items(filteredPoints) { point ->
                NearbyPointCard(
                    point = point,
                    isSelected = selectedPoint == point,
                    onClick = { selectedPoint = point }
                )
                Spacer(modifier = Modifier.height(10.dp))
            }
        }

        // ── Nav Bar ─────────────────────────────────────────────
        MapNavBar(
            selectedIndex = selectedNavIndex,
            onItemSelected = { index ->
                selectedNavIndex = index
                when (index) {

                    0 -> onNavigateToHome()
                    1 -> onNavigateToOrders()
                    2 -> onNavigateToCamera()
                    3 -> { /* Ya estás en Mapa */ }
                    4 -> onNavigateToProfile()

                }
            },
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}

// ─── 1. HEADER ────────────────────────────────────────────────────────────────
@Composable
fun MapHeader(pointCount: Int, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 24.dp)
    ) {
        Text(
            text = "Explorar Mapa",
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            color = MapTextBlack
        )
        Text(
            text = "$pointCount puntos cerca de ti en Bucaramanga",
            fontSize = 14.sp,
            color = MapDarkGreenText,
            fontWeight = FontWeight.Medium
        )
    }
}

// ─── 2. BARRA DE BÚSQUEDA ─────────────────────────────────────────────────────
@Composable
fun MapSearchBar() {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
            .clickable { /* Abrir búsqueda */ },
        shape = RoundedCornerShape(16.dp),
        color = Color.White.copy(alpha = 0.8f)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Reemplaza con tu ícono de búsqueda
            Image(
                painter = painterResource(id = R.drawable.icon_papel), // ← usa tu ícono de lupa
                contentDescription = "Buscar",
                modifier = Modifier.size(20.dp).alpha(0.4f)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = "Buscar punto de reciclaje...",
                fontSize = 14.sp,
                color = MapLightGrayText,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

// ─── 3. SECCIÓN MAPA MAPBOX ───────────────────────────────────────────────────
@Composable
fun MapBoxSection(
    points: List<RecyclePoint>,
    selectedPoint: RecyclePoint?,
    onPointSelected: (RecyclePoint) -> Unit
) {
    val context = LocalContext.current

    val markerReciclaje = remember(context) {
        BitmapFactory.decodeResource(context.resources, R.drawable.bowl_reciclaje)
    }
    val markerUser = remember(context) {
        BitmapFactory.decodeResource(context.resources, R.drawable.avatar)
    }

    // Ubicación del usuario (Bucaramanga)
    val puntoUsuario = Point.fromLngLat(-73.1165, 7.1175)

    val mapViewportState = rememberMapViewportState {
        setCameraOptions {
            center(Point.fromLngLat(-73.1158, 7.1182))
            zoom(15.5)
            pitch(40.0)
        }
    }

    Spacer(modifier = Modifier.height(14.dp))

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(240.dp)
            .padding(horizontal = 24.dp)
            .clip(RoundedCornerShape(24.dp)),
        shadowElevation = 6.dp
    ) {
        MapboxMap(
            modifier = Modifier.fillMaxSize(),
            mapViewportState = mapViewportState
        ) {
            // Ruta al punto seleccionado
            selectedPoint?.let { punto ->
                val destinoPunto = Point.fromLngLat(punto.lng, punto.lat)
                val esquina = Point.fromLngLat(punto.lng, puntoUsuario.latitude())

                PolylineAnnotation(
                    points = listOf(puntoUsuario, esquina, destinoPunto),
                    lineColorString = "#1B5E20",
                    lineWidth = 5.0
                )
            }

            // Marcador del usuario
            PointAnnotation(
                point = puntoUsuario,
                iconImageBitmap = markerUser,
                iconSize = 0.7
            )

            // Marcadores de los puntos de reciclaje
            points.forEach { punto ->
                PointAnnotation(
                    point = Point.fromLngLat(punto.lng, punto.lat),
                    iconImageBitmap = markerReciclaje,
                    iconSize = if (selectedPoint == punto) 1.3 else 1.0
                )
            }
        }
    }
}

// ─── 4. FILTRO CHIP DEL MAPA ──────────────────────────────────────────────────
@Composable
fun MapFilterChip(label: String, isSelected: Boolean, onClick: () -> Unit) {
    Surface(
        shape = RoundedCornerShape(20.dp),
        color = if (isSelected) MapDarkGreenText else Color.White.copy(alpha = 0.6f),
        modifier = Modifier.clickable(
            interactionSource = remember { MutableInteractionSource() },
            indication = null,
            onClick = onClick
        )
    ) {
        Text(
            text = label,
            color = if (isSelected) Color.White else MapTextBlack,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
        )
    }
}

// ─── 5. TARJETA PUNTO SELECCIONADO ────────────────────────────────────────────
@Composable
fun SelectedPointCard(point: RecyclePoint) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp),
        shape = RoundedCornerShape(20.dp),
        color = Color.White.copy(alpha = 0.85f)
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(Color(0xFFE8F5E9)),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = point.iconRes),
                    contentDescription = null,
                    modifier = Modifier.size(32.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(point.name, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = MapTextBlack)
                Text(point.address, fontSize = 11.sp, color = MapLightGrayText, modifier = Modifier.padding(top = 2.dp))
                // Tags de materiales
                Row(
                    modifier = Modifier.padding(top = 6.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    point.materials.forEach { material ->
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = Color(0xFF2ECC40).copy(alpha = 0.15f)
                        ) {
                            Text(
                                text = material,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = MapDarkGreenText,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                            )
                        }
                    }
                }
            }

            // Badge distancia
            Surface(shape = RoundedCornerShape(12.dp), color = MapDarkGreenText) {
                Text(
                    text = point.distance,
                    color = Color.White,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                )
            }
        }
    }
}

// ─── 6. TARJETA PUNTO CERCANO (lista) ─────────────────────────────────────────
@Composable
fun NearbyPointCard(point: RecyclePoint, isSelected: Boolean, onClick: () -> Unit) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(20.dp),
        color = if (isSelected) Color.White.copy(alpha = 0.9f) else Color.White.copy(alpha = 0.65f),
        border = if (isSelected) androidx.compose.foundation.BorderStroke(1.5.dp, Color(0xFF2ECC40)) else null
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color.White),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = point.iconRes),
                    contentDescription = null,
                    modifier = Modifier.size(28.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(point.name, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = MapTextBlack)
                Text(point.address, fontSize = 11.sp, color = MapLightGrayText)
                Row(
                    modifier = Modifier.padding(top = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    point.materials.take(2).forEach { material ->
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = Color(0xFF2ECC40).copy(alpha = 0.12f)
                        ) {
                            Text(
                                text = material,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = MapDarkGreenText,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                }
            }

            Text(
                text = point.distance,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = MapDarkGreenText
            )
        }
    }
}

// ─── 7. NAV BAR ───────────────────────────────────────────────────────────────
@Composable
fun MapNavBar(
    selectedIndex: Int,
    onItemSelected: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shadowElevation = 24.dp,
        color = MapBackgroundSurface,
        shape = RoundedCornerShape(topStart = 30.dp, topEnd = 30.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp, horizontal = 8.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            MapNavItem(R.drawable.icon_home, "Inicio", selectedIndex == 0)     { onItemSelected(0) }
            MapNavItem(R.drawable.delivery_home, "Pedidos", selectedIndex == 1) { onItemSelected(1) }

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
                Text("Enviar", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = MapIconGreen)
            }

            MapNavItem(R.drawable.mapa_home, "Mapa", selectedIndex == 3)     { onItemSelected(3) }
            MapNavItem(R.drawable.perfil_home, "Perfil", selectedIndex == 4) { onItemSelected(4) }
        }
    }
}

@Composable
private fun MapNavItem(iconRes: Int, label: String, selected: Boolean, onClick: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .background(if (selected) MapGlassSelector else Color.Transparent)
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
            color = MapIconGreen,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium
        )
    }
}