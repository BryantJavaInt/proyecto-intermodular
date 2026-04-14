package com.example.inmobiliacontrol.ui.login

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.example.inmobiliacontrol.Role
import com.example.inmobiliacontrol.database.InmobiliaDatabase
import com.example.inmobiliacontrol.repository.UserRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

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

    fun login(onSuccess: (Role, Int) -> Unit) {
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
                val userId = withContext(Dispatchers.IO) {
                    val existingUser = repo.login(email, password)
                    if (existingUser != null) {
                        existingUser.id
                    } else {
                        repo.register(email, password, role.name).toInt()
                    }
                }

                if (userId > 0) {
                    _uiState.update { it.copy(loading = false, error = null) }
                    onSuccess(role, userId)
                } else {
                    _uiState.update {
                        it.copy(
                            loading = false,
                            error = "No se pudo recuperar el usuario"
                        )
                    }
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
    }
}