package com.example.inmobiliacontrol.ui.login

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.example.inmobiliacontrol.Role
import com.example.inmobiliacontrol.database.InmobiliaDatabase
import com.example.inmobiliacontrol.repository.UserRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update

data class LoginUiState(
    val role: Role = Role.TENANT,
    val email: String = "",
    val password: String = "",
    val error: String? = null,
    val loading: Boolean = false
)

class LoginViewModel(application: Application) : AndroidViewModel(application) {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)

    private val repo: UserRepository by lazy {
        val db = InmobiliaDatabase.getInstance(getApplication())
        UserRepository(db.userDao())
    }

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

    /**
     * Login con Room:
     * 1) valida campos
     * 2) intenta login(email,password)
     * 3) si no existe, registra y deja pasar (demo)
     */
    fun login(onSuccess: (Role) -> Unit) {
        val state = _uiState.value
        val email = state.email.trim()
        val password = state.password
        val role = state.role

        if (email.isBlank() || password.isBlank()) {
            _uiState.update { it.copy(error = "Todos los campos son obligatorios") }
            return
        }

        if (password.length < 4) {
            _uiState.update { it.copy(error = "La contraseña debe tener al menos 4 caracteres") }
            return
        }

        _uiState.update { it.copy(loading = true, error = null) }

        scope.launch {
            try {
                val exists = withContext(Dispatchers.IO) {
                    repo.login(email, password) != null
                }

                if (exists) {
                    _uiState.update { it.copy(loading = false, error = null) }
                    onSuccess(role)
                } else {
                    // demo: si no existe, lo registramos y dejamos pasar
                    withContext(Dispatchers.IO) {
                        repo.register(email, password)
                    }
                    _uiState.update { it.copy(loading = false, error = null) }
                    onSuccess(role)
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        loading = false,
                        error = "Error de base de datos: ${e.message ?: "desconocido"}"
                    )
                }
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        // SupervisorJob se cancela automáticamente al perder referencias,
        // pero lo dejamos explícito si lo prefieres (opcional).
    }
}