package com.carlossaulvillabonapinilla.lopify.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.carlossaulvillabonapinilla.lopify.ui.screens.CameraScreen
import com.carlossaulvillabonapinilla.lopify.ui.screens.HomeScreen
import com.carlossaulvillabonapinilla.lopify.ui.screens.LoginScreen
import com.carlossaulvillabonapinilla.lopify.ui.screens.LoopifySplashScreen
import com.carlossaulvillabonapinilla.lopify.ui.screens.RegisterScreen
// 🟢 Nuevos imports
import com.carlossaulvillabonapinilla.lopify.ui.screens.OrdersScreen
import com.carlossaulvillabonapinilla.lopify.ui.screens.MapScreen
import com.carlossaulvillabonapinilla.lopify.ui.screens.ProfileScreen
import com.carlossaulvillabonapinilla.lopify.ui.screens.ResultScreen

object Routes {
    const val SPLASH   = "splash"
    const val LOGIN    = "login"
    const val REGISTER = "register"
    const val HOME     = "home"
    // 🟢 Nuevas rutas
    const val ORDERS   = "orders"
    const val MAP      = "map"
    const val PROFILE  = "profile"

    const val CAMERA = "camera"

    const val RESULT = "result"
}

@Composable
fun LoopifyNavGraph() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = Routes.SPLASH) {

        composable(Routes.SPLASH) {
            LoopifySplashScreen(
                onSplashFinished = {
                    navController.navigate(Routes.LOGIN) {
                        popUpTo(Routes.SPLASH) { inclusive = true }
                    }
                }
            )
        }

        composable(Routes.LOGIN) {
            LoginScreen(
                onLoginClick = {
                    navController.navigate(Routes.HOME) {
                        popUpTo(Routes.LOGIN) { inclusive = true }
                    }
                },
                onGoogleClick = {},
                onFacebookClick = {},
                onForgotPassword = {},
                onRegister = { navController.navigate(Routes.REGISTER) }
            )
        }

        composable(Routes.REGISTER) {
            RegisterScreen(
                onBackClick = { navController.popBackStack() },
                onRegisterClick = {
                    navController.navigate(Routes.LOGIN) {
                        popUpTo(Routes.LOGIN) { inclusive = false }
                    }
                },
                onLoginClick = { navController.popBackStack() }
            )
        }

        composable(Routes.HOME) {
            HomeScreen(
                // 🟢 Pasa el navController a HomeScreen para que pueda navegar
                onNavigateToOrders  = { navController.navigate(Routes.ORDERS) },
                onNavigateToMap     = { navController.navigate(Routes.MAP) },
                onNavigateToProfile = { navController.navigate(Routes.PROFILE) },
                onNavigateToCamera  = { navController.navigate(Routes.CAMERA) }
            )
        }

        // 🟢 Nuevas pantallas
        composable(Routes.ORDERS) {

            OrdersScreen(
                onNavigateToHome    = { navController.navigate(Routes.HOME) },
                onNavigateToMap     = { navController.navigate(Routes.MAP) },
                onNavigateToProfile = { navController.navigate(Routes.PROFILE) },
                onNavigateToCamera  = {
                    navController.navigate(Routes.CAMERA) {
                        launchSingleTop = true
                    }
                }
            )

        }


        composable(Routes.MAP) {
            MapScreen(
                onNavigateToHome    = { navController.navigate(Routes.HOME) },
                onNavigateToOrders  = { navController.navigate(Routes.ORDERS) },
                onNavigateToProfile = { navController.navigate(Routes.PROFILE) },
                onNavigateToCamera  = {
                    navController.navigate(Routes.CAMERA) {
                        launchSingleTop = true
                    }
                }
            )
        }



        composable(Routes.PROFILE) {
            ProfileScreen(
                onNavigateToHome   = { navController.navigate(Routes.HOME) },
                onNavigateToOrders = { navController.navigate(Routes.ORDERS) },
                onNavigateToMap    = { navController.navigate(Routes.MAP) },
                onNavigateToCamera = {
                    navController.navigate(Routes.CAMERA) {
                        launchSingleTop = true
                    }
                }
            )
        }


        composable(Routes.CAMERA) {
            CameraScreen(
                onResult = {
                    navController.navigate(Routes.RESULT) {
                        popUpTo(Routes.CAMERA) { inclusive = true }
                    }
                },
                onBack = { navController.popBackStack() }
            )
        }

        composable(Routes.RESULT) {
            ResultScreen(
                onNewCapture = {
                    navController.navigate(Routes.CAMERA) {
                        popUpTo(Routes.RESULT) { inclusive = true }
                    }
                },
                onGoToOrders = {
                    navController.navigate(Routes.ORDERS) {
                        popUpTo(Routes.HOME) { inclusive = false }
                        launchSingleTop = true
                    }
                }
            )
        }



    }
}