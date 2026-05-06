package com.carlossaulvillabonapinilla.lopify.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.carlossaulvillabonapinilla.lopify.ui.screens.LoginScreen
import com.carlossaulvillabonapinilla.lopify.ui.screens.LoopifySplashScreen
import com.carlossaulvillabonapinilla.lopify.ui.screens.RegisterScreen

// ─── Rutas ────────────────────────────────────────────────────────────────────
object Routes {
    const val SPLASH   = "splash"
    const val LOGIN    = "login"
    const val REGISTER = "register"
}

// ─── Grafo de navegación ──────────────────────────────────────────────────────
@Composable
fun LoopifyNavGraph() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Routes.SPLASH
    ) {

        // ── Splash ────────────────────────────────────────────────────────────
        composable(Routes.SPLASH) {
            LoopifySplashScreen(
                onSplashFinished = {
                    navController.navigate(Routes.LOGIN) {
                        // Elimina el Splash del back stack para que el usuario
                        // no pueda volver a él con el botón atrás
                        popUpTo(Routes.SPLASH) { inclusive = true }
                    }
                }
            )
        }

        // ── Login ─────────────────────────────────────────────────────────────
        composable(Routes.LOGIN) {
            LoginScreen(
                onLoginClick = {
                    // TODO: navegar a Home cuando la tengas
                    // navController.navigate(Routes.HOME)
                },
                onGoogleClick = {
                    // TODO: lógica Google Sign-In
                },
                onFacebookClick = {
                    // TODO: lógica Facebook Sign-In
                },
                onForgotPassword = {
                    // TODO: navegar a ForgotPasswordScreen
                },
                onRegister = {
                    navController.navigate(Routes.REGISTER)
                }
            )
        }

        // ── Register ──────────────────────────────────────────────────────────
        composable(Routes.REGISTER) {
            RegisterScreen(
                onBackClick = {
                    // Flecha atrás → regresa al Login
                    navController.popBackStack()
                },
                onRegisterClick = {
                    // Registro exitoso → va al Login y limpia el stack
                    navController.navigate(Routes.LOGIN) {
                        popUpTo(Routes.LOGIN) { inclusive = false }
                    }
                },
                onLoginClick = {
                    // "Ya tienes cuenta?" → regresa al Login
                    navController.popBackStack()
                }
            )
        }
    }
}