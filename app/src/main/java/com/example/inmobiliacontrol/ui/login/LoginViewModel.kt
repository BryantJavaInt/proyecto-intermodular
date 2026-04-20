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

        if (email.isBlank() || password.isBlank()) {
            _uiState.update { it.copy(error = "Introduce tu email y contraseña") }
            return
        }

        _uiState.update { it.copy(loading = true, error = null) }

        scope.launch {
            try {
                val result: Pair<Int, Role>? = withContext(Dispatchers.IO) {
                    val existingUser = repo.login(email, password)
                    if (existingUser != null) {
                        val dbRole = try {
                            Role.valueOf(existingUser.role)
                        } catch (e: IllegalArgumentException) {
                            Role.TENANT
                        }
                        Pair(existingUser.id, dbRole)
                    } else {
                        null
                    }
                }

                if (result != null) {
                    _uiState.update { it.copy(loading = false, error = null) }
                    onSuccess(result.second, result.first)
                } else {
                    _uiState.update {
                        it.copy(loading = false, error = "Email o contraseña incorrectos")
                    }
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(loading = false, error = "Error: ${e.message ?: "desconocido"}")
                }
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
    }
}
