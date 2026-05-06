package com.carlossaulvillabonapinilla.lopify.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.carlossaulvillabonapinilla.lopify.R

private val IconGreen         = Color(0xFF0A1A05)
private val BackgroundSurface = Color(0xFF93E575)
private val GlassSelector     = Color(0x33FFFFFF)   // blanco 20% → efecto vidrio
private val GlassBorder       = Color(0x55FFFFFF)   // blanco 33% → borde vidrioso

// ─── Preview ──────────────────────────────────────────────────────────────────
@Preview(showBackground = true, showSystemUi = true, device = "spec:width=390dp,height=844dp,dpi=460")
@Composable
fun HomeScren() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF2ECC40),
                        Color(0xFFA4DCA4),
                        Color(0xFFBBEABB)
                    ),
                    startY = 0f,
                    endY = 600f
                )
            )
    ) {
        HomeNavBar(
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}

// ─── Bottom NavBar ────────────────────────────────────────────────────────────
@Composable
fun HomeNavBar(
    modifier: Modifier = Modifier,
    selectedIndex: Int = 0,
    selectedColor: Color = IconGreen,
    unselectedColor: Color = IconGreen,
    onInicioClick: () -> Unit = {},
    onPedidosClick: () -> Unit = {},
    onEnviarClick: () -> Unit = {},
    onMapaClick: () -> Unit = {},
    onPerfilClick: () -> Unit = {}
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shadowElevation = 16.dp,
        color = BackgroundSurface,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        border = BorderStroke(
            width = 1.5.dp,
            brush = Brush.verticalGradient(
                colors = listOf(
                    Color(0xF03B6C2E),
                    Color(0x00FFFFFF)
                )
            )
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 16.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {

            // ── Inicio ────────────────────────────────────────────────────
            NavBarItem(
                iconRes = R.drawable.icon_home,
                label = "Inicio",
                selected = selectedIndex == 0,
                selectedColor = selectedColor,
                unselectedColor = unselectedColor,
                onClick = onInicioClick
            )

            // ── Pedidos ───────────────────────────────────────────────────
            NavBarItem(
                iconRes = R.drawable.delivery_home,
                label = "Pedidos",
                selected = selectedIndex == 1,
                selectedColor = selectedColor,
                unselectedColor = unselectedColor,
                onClick = onPedidosClick
            )

            // ── Enviar (botón central elevado) ────────────────────────────
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(62.dp)
                        .shadow(
                            elevation = 10.dp,
                            shape = CircleShape,
                            ambientColor = Color(0x554CAF50),
                            spotColor = Color(0x554CAF50)
                        )
                        .clip(CircleShape)
                        .background(
                            brush = Brush.verticalGradient(
                                colors = listOf(Color(0xFF5CD65C), Color(0xFF1A6B1A))
                            )
                        )
                        .clickable { onEnviarClick() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.camara_home),
                        contentDescription = "Enviar",
                        tint = selectedColor,   // ← mismo color que los otros íconos
                        modifier = Modifier.size(28.dp)
                    )
                }
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "Enviar",
                    fontSize = 10.sp,
                    color = IconGreen,
                    fontWeight = FontWeight.SemiBold
                )
            }

            // ── Mapa ──────────────────────────────────────────────────────
            NavBarItem(
                iconRes = R.drawable.mapa_home,
                label = "Mapa",
                selected = selectedIndex == 3,
                selectedColor = selectedColor,
                unselectedColor = unselectedColor,
                onClick = onMapaClick
            )

            // ── Perfil ────────────────────────────────────────────────────
            NavBarItem(
                iconRes = R.drawable.perfil_home,
                label = "Perfil",
                selected = selectedIndex == 4,
                selectedColor = selectedColor,
                unselectedColor = unselectedColor,
                onClick = onPerfilClick
            )
        }
    }
}

// ─── Item normal del NavBar ───────────────────────────────────────────────────
@Composable
private fun NavBarItem(
    iconRes: Int,
    label: String,
    selected: Boolean,
    selectedColor: Color = IconGreen,
    unselectedColor: Color = IconGreen,
    onClick: () -> Unit
) {
    val color = if (selected) selectedColor else unselectedColor

    // ── Contenedor con selector vidrioso ──────────────────────────────────────
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            // Fondo vidrioso solo cuando está seleccionado
            .background(
                color = if (selected) GlassSelector else Color.Transparent
            )
            // Borde vidrioso solo cuando está seleccionado
            .then(
                if (selected) Modifier.shadow(
                    elevation = 0.dp,
                    shape = RoundedCornerShape(16.dp)
                ) else Modifier
            )
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) { onClick() }
            .padding(horizontal = 14.dp, vertical = 6.dp)
    ) {
        // Borde interno vidrioso cuando está seleccionado
        if (selected) {
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .clip(RoundedCornerShape(16.dp))
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                GlassBorder,
                                Color(0x22FFFFFF)
                            )
                        )
                    )
            )
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                painter = painterResource(id = iconRes),
                contentDescription = label,
                tint = color,
                modifier = Modifier.size(40.dp)
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = label,
                fontSize = 10.sp,
                color = color,
                fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal
            )
        }
    }
}