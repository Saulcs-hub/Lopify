package com.carlossaulvillabonapinilla.lopify.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
import com.carlossaulvillabonapinilla.lopify.R

// ─── Colores ──────────────────────────────────────────────────────────────────
private val BackgroundColor = Color(0xFFF0F5F0)
private val GreenDark       = Color(0xFF119420)        // parada 5% figma
private val GreenBright     = Color(0xBF47FF78)        // parada 90% figma (74% opacidad = BF)
private val GreenPrimary    = Color(0xFF4CAF50)
private val GreenText       = Color(0xFF4CAF50)
private val TitleColor      = Color(0xFF1A1A1A)
private val SubtitleColor   = Color(0xFF888888)
private val FieldBackground = Color(0xFFFFFFFF)
private val FieldBorder     = Color(0xFFE8E8E8)
private val SocialBorder    = Color(0xFFDDDDDD)
private val OrColor         = Color(0xFFAAAAAA)

// ─── Fuente ───────────────────────────────────────────────────────────────────
private val GoogleLogin = FontFamily(
    Font(R.font.googlesans_medium,)
)

// ─── Login Screen ─────────────────────────────────────────────────────────────
@Composable
fun LoginScreen(
    onLoginClick: () -> Unit = {},
    onGoogleClick: () -> Unit = {},
    onFacebookClick: () -> Unit = {},
    onForgotPassword: () -> Unit = {},
    onRegister: () -> Unit = {}
) {

    // Estados
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var rememberMe by remember { mutableStateOf(false) }

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

            // ── Header con gradiente lineal verde → fondo ────────────────────
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp)
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                Color(0xFF3DD92A),   // verde vibrante arriba
                                Color(0xFFF0F5F0)    // mismo color que el fondo abajo
                            )
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
                )
            }

            // ── Espacio para el ícono flotante ─────────────────────────────────
            Spacer(modifier = Modifier.height(68.dp))

            // ── Contenido ──────────────────────────────────────────────────────
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
                    isPassword = true,
                    trailingIcon = {
                        // TODO: Reemplaza R.drawable.bombillo con tu ícono
                        Icon(
                            painter = painterResource(id = R.drawable.bombillo),
                            contentDescription = "Password icon",
                            tint = Color.Unspecified,
                            modifier = Modifier.size(24.dp)
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
                        Text(text = "Recuérdame", fontSize = 13.sp, color = SubtitleColor)
                    }
                    TextButton(onClick = onForgotPassword) {
                        Text(
                            text = "Has olvidado tu contraseña ?",
                            fontSize = 13.sp,
                            color = GreenText,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = onLoginClick,
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
                    Text(
                        text = "Iniciar Sesion",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White
                    )
                }

                Spacer(modifier = Modifier.height(18.dp))

                Text(text = "Or", fontSize = 13.sp, color = OrColor, textAlign = TextAlign.Center)

                Spacer(modifier = Modifier.height(18.dp))

                SocialButton(
                    text = "Continue with Google",
                    iconRes = R.drawable.icon_google,   // TODO: agrega ic_google.png en drawable
                    onClick = onGoogleClick
                )

                Spacer(modifier = Modifier.height(12.dp))

                SocialButton(
                    text = "Continue with Facebook",
                    iconRes = R.drawable.icon_facebook, // TODO: agrega ic_facebook.png en drawable
                    onClick = onFacebookClick
                )

                Spacer(modifier = Modifier.height(24.dp))

                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "No tienes una cuenta? ", fontSize = 13.sp, color = SubtitleColor)
                    TextButton(onClick = onRegister, contentPadding = PaddingValues(0.dp)) {
                        Text(
                            text = "Regístrate",
                            fontSize = 13.sp,
                            color = GreenText,
                            fontWeight = FontWeight.SemiBold
                        )
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
            .shadow(
                elevation = 4.dp,
                shape = RoundedCornerShape(14.dp),
                ambientColor = Color(0x1A000000),
                spotColor = Color(0x1A000000)
            )
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
            .shadow(
                elevation = 4.dp,
                shape = RoundedCornerShape(14.dp),
                ambientColor = Color(0x1A000000),
                spotColor = Color(0x1A000000)
            ),
        shape = RoundedCornerShape(14.dp),
        border = BorderStroke(1.dp, SocialBorder),
        colors = ButtonDefaults.outlinedButtonColors(containerColor = Color.White)
    ) {
        Icon(
            painter = painterResource(id = iconRes),
            contentDescription = text,
            tint = Color.Unspecified,
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(10.dp))
        Text(text = text, fontSize = 15.sp, color = TitleColor, fontWeight = FontWeight.Medium)
    }
}

// ─── Preview ──────────────────────────────────────────────────────────────────
@Preview(showBackground = true, showSystemUi = true, device = "spec:width=390dp,height=844dp,dpi=460")
@Composable
fun LoginScreenPreview() {
    LoginScreen()
}