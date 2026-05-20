package com.carlossaulvillabonapinilla.lopify.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.*
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.carlossaulvillabonapinilla.lopify.R
import com.carlossaulvillabonapinilla.lopify.viewmodel.AuthState
import com.carlossaulvillabonapinilla.lopify.viewmodel.AuthViewModel

private val BackgroundColor = Color(0xFFF0F5F0)
private val GreenPrimary    = Color(0xFF4CAF50)
private val GreenText       = Color(0xFF4CAF50)
private val TitleColor      = Color(0xFF1A1A1A)
private val SubtitleColor   = Color(0xFF888888)
private val FieldBackground = Color(0xFFFFFFFF)
private val FieldBorder     = Color(0xFFE8E8E8)

private val GoogleSansSemiBold = FontFamily(Font(R.font.googlesans_semibold, FontWeight.SemiBold))

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(
    onBackClick: () -> Unit = {},
    onRegisterClick: () -> Unit = {},
    onLoginClick: () -> Unit = {},
    viewModel: AuthViewModel = viewModel()
) {
    var nombre by remember { mutableStateOf("") }
    var apellido by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var fechaNacimiento by remember { mutableStateOf("") }
    var telefono by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var showDatePicker by remember { mutableStateOf(false) }

    val authState by viewModel.authState.collectAsState()
    val isLoading = authState is AuthState.Loading

    // Navegar al Login cuando registro sea exitoso
    LaunchedEffect(authState) {
        if (authState is AuthState.Success) {
            viewModel.resetState()
            onRegisterClick()
        }
    }

    val infiniteTransition = rememberInfiniteTransition(label = "plantBob")
    val plantOffset by infiniteTransition.animateFloat(
        initialValue = 0f, targetValue = -10f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ), label = "plantOffset"
    )
    val plantRotation by infiniteTransition.animateFloat(
        initialValue = -2f, targetValue = 2f,
        animationSpec = infiniteRepeatable(
            animation = tween(1800, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ), label = "plantRotation"
    )
    val bulbScale = remember { Animatable(1f) }

    Box(modifier = Modifier.fillMaxSize().background(BackgroundColor)) {
        Column(modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState())) {

            // Header
            Box(
                modifier = Modifier.fillMaxWidth().height(100.dp)
                    .background(brush = Brush.verticalGradient(
                        colors = listOf(Color(0xFF3DD92A), Color(0xFFF0F5F0))
                    ))
            ) {
                IconButton(
                    onClick = onBackClick,
                    modifier = Modifier.padding(start = 8.dp, top = 16.dp).align(Alignment.TopStart)
                ) {
                    Icon(painter = painterResource(id = R.drawable.flecha),
                        contentDescription = "Volver", tint = TitleColor,
                        modifier = Modifier.size(24.dp))
                }
            }

            Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp)) {
                Spacer(modifier = Modifier.height(8.dp))

                Row(modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Regístrate", fontFamily = GoogleSansSemiBold,
                            fontWeight = FontWeight.SemiBold, fontSize = 32.sp, color = TitleColor)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("Crea una cuenta para continuar!", fontSize = 13.sp, color = SubtitleColor)
                    }
                    Image(
                        painter = painterResource(id = R.drawable.planta),
                        contentDescription = "Loopify plant icon",
                        modifier = Modifier.size(90.dp).padding(start = 8.dp)
                            .offset(y = plantOffset.dp)
                            .graphicsLayer { rotationZ = plantRotation }
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                RegisterTextField(nombre, { nombre = it }, "Nombre")
                Spacer(modifier = Modifier.height(12.dp))
                RegisterTextField(apellido, { apellido = it }, "Apellido")
                Spacer(modifier = Modifier.height(12.dp))
                RegisterTextField(email, { email = it }, "Email", KeyboardType.Email)
                Spacer(modifier = Modifier.height(12.dp))

                RegisterTextField(
                    value = fechaNacimiento,
                    onValueChange = { fechaNacimiento = it },
                    placeholder = "DD-MM-YYYY",
                    keyboardType = KeyboardType.Number,
                    trailingIcon = {
                        IconButton(onClick = { showDatePicker = true }) {
                            Icon(painter = painterResource(id = R.drawable.calendar),
                                contentDescription = "Calendario", tint = SubtitleColor,
                                modifier = Modifier.size(20.dp))
                        }
                    }
                )

                if (showDatePicker) {
                    val datePickerState = rememberDatePickerState()
                    DatePickerDialog(
                        onDismissRequest = { showDatePicker = false },
                        confirmButton = {
                            TextButton(onClick = {
                                datePickerState.selectedDateMillis?.let { millis ->
                                    val sdf = java.text.SimpleDateFormat("dd-MM-yyyy", java.util.Locale.getDefault())
                                    fechaNacimiento = sdf.format(java.util.Date(millis))
                                }
                                showDatePicker = false
                            }) { Text("Aceptar", color = GreenPrimary) }
                        },
                        dismissButton = {
                            TextButton(onClick = { showDatePicker = false }) {
                                Text("Cancelar", color = SubtitleColor)
                            }
                        }
                    ) {
                        DatePicker(state = datePickerState,
                            colors = DatePickerDefaults.colors(
                                selectedDayContainerColor = GreenPrimary,
                                selectedDayContentColor = Color.White,
                                todayDateBorderColor = GreenPrimary,
                                todayContentColor = GreenPrimary
                            ))
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Teléfono con bandera
                OutlinedTextField(
                    value = telefono, onValueChange = { telefono = it },
                    placeholder = { Text("(324) 556 - 6567", color = Color(0xFFBBBBBB), fontSize = 15.sp) },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    leadingIcon = {
                        Row(verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(start = 8.dp)) {
                            Icon(painter = painterResource(id = R.drawable.flecha_desplegable),
                                contentDescription = "País", tint = SubtitleColor,
                                modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Image(painter = painterResource(id = R.drawable.bandera_colombia),
                                contentDescription = "Colombia", modifier = Modifier.size(24.dp))
                        }
                    },
                    shape = RoundedCornerShape(14.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedContainerColor = FieldBackground, focusedContainerColor = FieldBackground,
                        unfocusedBorderColor = FieldBorder, focusedBorderColor = GreenPrimary,
                    ),
                    modifier = Modifier.fillMaxWidth()
                        .shadow(4.dp, RoundedCornerShape(14.dp), ambientColor = Color(0x1A000000), spotColor = Color(0x1A000000))
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Contraseña con bombillo
                RegisterTextField(
                    value = password, onValueChange = { password = it },
                    placeholder = "Contraseña", isPassword = !passwordVisible,
                    trailingIcon = {
                        Image(
                            painter = painterResource(id = R.drawable.bombillo),
                            contentDescription = if (passwordVisible) "Ocultar" else "Mostrar",
                            modifier = Modifier.size(28.dp).scale(bulbScale.value)
                                .alpha(if (passwordVisible) 1f else 0.35f)
                                .clickable(interactionSource = remember { MutableInteractionSource() },
                                    indication = null) { passwordVisible = !passwordVisible },
                            colorFilter = if (passwordVisible) null else
                                ColorFilter.colorMatrix(ColorMatrix().apply { setToSaturation(0f) })
                        )
                    }
                )

                Spacer(modifier = Modifier.height(28.dp))

                // ── Botón Register con loading ─────────────────────────────────
                Button(
                    onClick = { viewModel.register(nombre, apellido, email, password, telefono, fechaNacimiento) },
                    enabled = !isLoading,
                    modifier = Modifier.fillMaxWidth().height(52.dp)
                        .shadow(8.dp, RoundedCornerShape(14.dp),
                            ambientColor = Color(0x554CAF50), spotColor = Color(0x554CAF50)),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = GreenPrimary)
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(color = Color.White,
                            modifier = Modifier.size(22.dp), strokeWidth = 2.dp)
                    } else {
                        Text("Registrarse", fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = Color.White)
                    }
                }

                // ── Mensaje de error ───────────────────────────────────────────
                if (authState is AuthState.Error) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = (authState as AuthState.Error).message,
                        color = Color.Red, fontSize = 13.sp,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))

                Row(modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Ya tienes una cuenta? ", fontSize = 13.sp, color = SubtitleColor)
                    TextButton(onClick = onLoginClick, contentPadding = PaddingValues(0.dp)) {
                        Text("Ingresa sesión", fontSize = 13.sp, color = GreenText, fontWeight = FontWeight.SemiBold)
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}

@Composable
private fun RegisterTextField(
    value: String, onValueChange: (String) -> Unit, placeholder: String,
    keyboardType: KeyboardType = KeyboardType.Text, isPassword: Boolean = false,
    trailingIcon: @Composable (() -> Unit)? = null
) {
    OutlinedTextField(
        value = value, onValueChange = onValueChange,
        placeholder = { Text(placeholder, color = Color(0xFFBBBBBB), fontSize = 15.sp) },
        singleLine = true,
        visualTransformation = if (isPassword) PasswordVisualTransformation() else VisualTransformation.None,
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        trailingIcon = trailingIcon,
        shape = RoundedCornerShape(14.dp),
        colors = OutlinedTextFieldDefaults.colors(
            unfocusedContainerColor = Color.White, focusedContainerColor = Color.White,
            unfocusedBorderColor = Color(0xFFE8E8E8), focusedBorderColor = Color(0xFF4CAF50),
        ),
        modifier = Modifier.fillMaxWidth()
            .shadow(4.dp, RoundedCornerShape(14.dp), ambientColor = Color(0x1A000000), spotColor = Color(0x1A000000))
    )
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun RegisterScreenPreview() { RegisterScreen() }