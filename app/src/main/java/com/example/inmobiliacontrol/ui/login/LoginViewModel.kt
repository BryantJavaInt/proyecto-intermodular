package com.example.inmobiliacontrol.ui.login

import androidx.lifecycle.ViewModel
import com.example.inmobiliacontrol.Role
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update

data class LoginUiState(
    val role: Role = Role.TENANT,
    val email: String = "",
    val password: String = "",
    val error: String? = null
)

class LoginViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState

    fun setRole(role: Role) {
        _uiState.update { it.copy(role = role, error = null) }
    }

    fun setEmail(email: String) {
        _uiState.update { it.copy(email = email, error = null) }
    }

    fun setPassword(password: String) {
        _uiState.update { it.copy(password = password, error = null) }
    }

    fun login(onSuccess: (Role) -> Unit) {
        val state = _uiState.value

        if (state.email.isBlank() || state.password.isBlank()) {
            _uiState.update { it.copy(error = "Todos los campos son obligatorios") }
            return
        }

        if (state.password.length < 4) {
            _uiState.update { it.copy(error = "La contraseña debe tener al menos 4 caracteres") }
            return
        }

        // Login demo (más adelante lo conectaremos a Room)
        onSuccess(state.role)
    }
}