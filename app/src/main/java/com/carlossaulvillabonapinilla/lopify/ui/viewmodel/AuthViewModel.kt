package com.carlossaulvillabonapinilla.lopify.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

// ─── Estados de la UI ─────────────────────────────────────────────────────────
sealed class AuthState {
    object Idle    : AuthState()
    object Loading : AuthState()
    object Success : AuthState()
    data class Error(val message: String) : AuthState()
}

class AuthViewModel : ViewModel() {

    private val auth = FirebaseAuth.getInstance()
    private val db   = FirebaseFirestore.getInstance()

    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    val authState: StateFlow<AuthState> = _authState

    // ── ¿Ya hay sesión activa? ────────────────────────────────────────────────
    val isLoggedIn: Boolean get() = auth.currentUser != null

    // ── Login ─────────────────────────────────────────────────────────────────
    fun login(email: String, password: String) {
        if (email.isBlank() || password.isBlank()) {
            _authState.value = AuthState.Error("Completa todos los campos")
            return
        }
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            try {
                auth.signInWithEmailAndPassword(email, password).await()
                _authState.value = AuthState.Success
            } catch (e: Exception) {
                _authState.value = AuthState.Error(
                    when {
                        e.message?.contains("password") == true -> "Contraseña incorrecta"
                        e.message?.contains("no user") == true  -> "No existe esta cuenta"
                        e.message?.contains("network") == true  -> "Sin conexión a internet"
                        else -> "Error al iniciar sesión"
                    }
                )
            }
        }
    }

    // ── Register ──────────────────────────────────────────────────────────────
    fun register(
        nombre: String, apellido: String, email: String,
        password: String, telefono: String, fechaNacimiento: String
    ) {
        // ── Validar campos vacíos ──────────────────────────────────────────────
        if (nombre.isBlank() || apellido.isBlank() || email.isBlank() || password.isBlank()) {
            _authState.value = AuthState.Error("Completa todos los campos obligatorios")
            return
        }

        // ── Validar que sea Gmail ──────────────────────────────────────────────
        if (!email.lowercase().endsWith("@gmail.com")) {
            _authState.value = AuthState.Error("Solo se permiten correos @gmail.com")
            return
        }

        // ── Validar mínimo 6 caracteres ────────────────────────────────────────
        if (password.length < 6) {
            _authState.value = AuthState.Error("La contraseña debe tener al menos 6 caracteres")
            return
        }

        // ── Validar al menos una mayúscula ─────────────────────────────────────
        if (!password.any { it.isUpperCase() }) {
            _authState.value = AuthState.Error("La contraseña debe tener al menos una mayúscula")
            return
        }

        // ── Validar al menos un signo especial ────────────────────────────────
        val specialChars = "!@#\$%^&*()_+-=[]{}|;':\",./<>?"
        if (!password.any { it in specialChars }) {
            _authState.value = AuthState.Error("La contraseña debe tener al menos un signo especial (!@#\$...)")
            return
        }

        viewModelScope.launch {
            _authState.value = AuthState.Loading
            try {
                val result = auth.createUserWithEmailAndPassword(email, password).await()
                val uid = result.user?.uid ?: throw Exception("Error al obtener UID")

                val userData = hashMapOf(
                    "nombre"          to nombre,
                    "apellido"        to apellido,
                    "email"           to email,
                    "telefono"        to telefono,
                    "fechaNacimiento" to fechaNacimiento,
                    "kgReciclados"    to 0.0,
                    "racha"           to 0,
                    "createdAt"       to System.currentTimeMillis()
                )
                db.collection("usuarios").document(uid).set(userData).await()
                _authState.value = AuthState.Success

            } catch (e: Exception) {
                _authState.value = AuthState.Error(
                    when {
                        e.message?.contains("email") == true   -> "Este correo ya está registrado"
                        e.message?.contains("network") == true -> "Sin conexión a internet"
                        else -> "Error al registrar: ${e.message}"
                    }
                )
            }
        }
    }

    // ── Logout ────────────────────────────────────────────────────────────────
    fun logout() {
        auth.signOut()
        _authState.value = AuthState.Idle
    }

    fun resetState() {
        _authState.value = AuthState.Idle
    }
}