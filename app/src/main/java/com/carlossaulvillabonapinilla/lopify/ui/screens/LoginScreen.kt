package com.carlossaulvillabonapinilla.lopify.ui.screens

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.runtime.saveable.rememberSaveable
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.lifecycle.viewmodel.compose.viewModel
import com.carlossaulvillabonapinilla.lopify.R
import com.carlossaulvillabonapinilla.lopify.viewmodel.AuthState
import com.carlossaulvillabonapinilla.lopify.viewmodel.AuthViewModel
import com.carlossaulvillabonapinilla.lopify.ui.model.UserSession


// ─── Colores ──────────────────────────────────────────────────────────────────
private val BackgroundColor = Color(0xFFF0F5F0)
private val GreenPrimary    = Color(0xFF4CAF50)
private val GreenText       = Color(0xFF4CAF50)
private val TitleColor      = Color(0xFF1A1A1A)
private val SubtitleColor   = Color(0xFF888888)
private val FieldBackground = Color(0xFFFFFFFF)
private val FieldBorder     = Color(0xFFE8E8E8)
private val SocialBorder    = Color(0xFFDDDDDD)
private val OrColor         = Color(0xFFAAAAAA)

// ─── Fuente ───────────────────────────────────────────────────────────────────
private val GoogleLogin = FontFamily(Font(R.font.googlesans_semibold))

// ─── Login Screen ─────────────────────────────────────────────────────────────
@Composable
fun LoginScreen(
    onLoginClick: () -> Unit = {},
    onGoogleClick: () -> Unit = {},
    onFacebookClick: () -> Unit = {},
    onForgotPassword: () -> Unit = {},
    onRegister: () -> Unit = {},
    viewModel: AuthViewModel = viewModel()
) {
    // Estados
    var email by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var rememberMe by rememberSaveable { mutableStateOf(false) }
    var passwordVisible by rememberSaveable { mutableStateOf(false) }

    val authState by viewModel.authState.collectAsState()
    val isLoading = authState is AuthState.Loading

    // Navegar al Home cuando login sea exitoso
    LaunchedEffect(authState) {
        if (authState is AuthState.Success) {
            viewModel.resetState()
            UserSession.username.value = email.substringBefore("@")
            onLoginClick()
        }
    }

    // Animaciones
    val infiniteTransition = rememberInfiniteTransition(label = "iconPulse")
    val iconScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.08f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 900, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )
    val bulbScale = remember { Animatable(1f) }
    LaunchedEffect(passwordVisible) {
        bulbScale.animateTo(1.2f, animationSpec = tween(100))
        bulbScale.animateTo(1f, animationSpec = tween(100))
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundColor)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header con gradiente
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp)
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(Color(0xFF3DD92A), Color(0xFFF0F5F0))
                        )
                    ),
                contentAlignment = Alignment.BottomCenter
            ) {
                Image(
                    painter = painterResource(id = R.drawable.basurero),
                    contentDescription = "Loopify icon",
                    modifier = Modifier
                        .size(130.dp)
                        .offset(y = 48.dp)
                        .zIndex(2f)
                        .graphicsLayer { scaleX = iconScale; scaleY = iconScale }
                )
            }

            Spacer(modifier = Modifier.height(68.dp))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Inicia sesión en tu cuenta",
                    fontFamily = GoogleLogin,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 28.sp,
                    color = TitleColor,
                    lineHeight = 34.sp,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Introduce tu correo electrónico y contraseña para iniciar sesión.",
                    fontSize = 13.sp,
                    color = SubtitleColor,
                    lineHeight = 18.sp,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(24.dp))

                LoginTextField(
                    value = email,
                    onValueChange = { email = it },
                    placeholder = "Email",
                    keyboardType = KeyboardType.Email
                )

                Spacer(modifier = Modifier.height(12.dp))

                LoginTextField(
                    value = password,
                    onValueChange = { password = it },
                    placeholder = "Contraseña",
                    isPassword = !passwordVisible,
                    trailingIcon = {
                        Image(
                            painter = painterResource(id = R.drawable.bombillo),
                            contentDescription = if (passwordVisible) "Ocultar" else "Mostrar",
                            modifier = Modifier
                                .size(28.dp)
                                .scale(bulbScale.value)
                                .alpha(if (passwordVisible) 1f else 0.35f)
                                .clickable(
                                    interactionSource = remember { MutableInteractionSource() },
                                    indication = null
                                ) { passwordVisible = !passwordVisible },
                            colorFilter = if (passwordVisible) null else
                                ColorFilter.colorMatrix(ColorMatrix().apply { setToSaturation(0f) })
                        )
                    }
                )

                Spacer(modifier = Modifier.height(14.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(
                            checked = rememberMe,
                            onCheckedChange = { rememberMe = it },
                            colors = CheckboxDefaults.colors(
                                uncheckedColor = Color(0xFFCCCCCC),
                                checkedColor = GreenPrimary
                            ),
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Recuérdame", fontSize = 13.sp, color = SubtitleColor)
                    }
                    TextButton(onClick = onForgotPassword) {
                        Text(
                            "Has olvidado tu contraseña ?",
                            fontSize = 13.sp,
                            color = GreenText,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // ── Botón Login con loading ────────────────────────────────────
                Button(
                    onClick = { viewModel.login(email, password) },
                    enabled = !isLoading,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .shadow(
                            elevation = 8.dp,
                            shape = RoundedCornerShape(14.dp),
                            ambientColor = Color(0x554CAF50),
                            spotColor = Color(0x554CAF50)
                        ),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = GreenPrimary)
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            color = Color.White,
                            modifier = Modifier.size(22.dp),
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text("Iniciar Sesion", fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = Color.White)
                    }
                }

                // ── Mensaje de error ───────────────────────────────────────────
                if (authState is AuthState.Error) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = (authState as AuthState.Error).message,
                        color = Color.Red,
                        fontSize = 13.sp,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                Spacer(modifier = Modifier.height(18.dp))

                Text("Or", fontSize = 13.sp, color = OrColor, textAlign = TextAlign.Center)

                Spacer(modifier = Modifier.height(18.dp))

                SocialButton("Continue with Google", R.drawable.icon_google, onGoogleClick)
                Spacer(modifier = Modifier.height(12.dp))
                SocialButton("Continue with Facebook", R.drawable.icon_facebook, onFacebookClick)

                Spacer(modifier = Modifier.height(24.dp))

                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("No tienes una cuenta? ", fontSize = 13.sp, color = SubtitleColor)
                    TextButton(onClick = onRegister, contentPadding = PaddingValues(0.dp)) {
                        Text("Regístrate", fontSize = 13.sp, color = GreenText, fontWeight = FontWeight.SemiBold)
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}

// ─── Campo de texto ───────────────────────────────────────────────────────────
@Composable
private fun LoginTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    keyboardType: KeyboardType = KeyboardType.Text,
    isPassword: Boolean = false,
    trailingIcon: @Composable (() -> Unit)? = null
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        placeholder = { Text(text = placeholder, color = Color(0xFFBBBBBB), fontSize = 15.sp) },
        singleLine = true,
        visualTransformation = if (isPassword) PasswordVisualTransformation() else VisualTransformation.None,
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        trailingIcon = trailingIcon,
        shape = RoundedCornerShape(14.dp),
        colors = OutlinedTextFieldDefaults.colors(
            unfocusedContainerColor = FieldBackground,
            focusedContainerColor = FieldBackground,
            unfocusedBorderColor = FieldBorder,
            focusedBorderColor = GreenPrimary,
        ),
        modifier = Modifier
            .fillMaxWidth()
            .shadow(elevation = 4.dp, shape = RoundedCornerShape(14.dp),
                ambientColor = Color(0x1A000000), spotColor = Color(0x1A000000))
    )
}

// ─── Botón social ─────────────────────────────────────────────────────────────
@Composable
private fun SocialButton(text: String, iconRes: Int, onClick: () -> Unit) {
    OutlinedButton(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(52.dp)
            .shadow(elevation = 4.dp, shape = RoundedCornerShape(14.dp),
                ambientColor = Color(0x1A000000), spotColor = Color(0x1A000000)),
        shape = RoundedCornerShape(14.dp),
        border = BorderStroke(1.dp, SocialBorder),
        colors = ButtonDefaults.outlinedButtonColors(containerColor = Color.White)
    ) {
        Icon(painter = painterResource(id = iconRes), contentDescription = text,
            tint = Color.Unspecified, modifier = Modifier.size(20.dp))
        Spacer(modifier = Modifier.width(10.dp))
        Text(text = text, fontSize = 15.sp, color = TitleColor, fontWeight = FontWeight.Medium)
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun LoginScreenPreview() { LoginScreen() }