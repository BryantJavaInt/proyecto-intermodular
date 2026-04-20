package com.example.inmobiliacontrol.ui.register

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

data class RegisterUiState(
    val role: Role = Role.TENANT,
    // Campos comunes
    val email: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val nombre: String = "",
    val apellidos: String = "",
    val telefono: String = "",
    val dni: String = "",
    // TENANT
    val propertyAddress: String = "",
    // AGENCY
    val agenciaNombre: String = "",
    val agenciaCif: String = "",
    // MAINTENANCE
    val especialidad: String = "",
    // Estado
    val loading: Boolean = false,
    val error: String? = null,
    val success: Boolean = false
)

class RegisterViewModel(application: Application) : AndroidViewModel(application) {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)

    private val repo: UserRepository by lazy {
        val db = InmobiliaDatabase.getInstance(getApplication())
        UserRepository(db.userDao())
    }

    private val _uiState = MutableStateFlow(RegisterUiState())
    val uiState: StateFlow<RegisterUiState> = _uiState

    fun setRole(role: Role) = _uiState.update { it.copy(role = role, error = null) }
    fun setEmail(v: String) = _uiState.update { it.copy(email = v, error = null) }
    fun setPassword(v: String) = _uiState.update { it.copy(password = v, error = null) }
    fun setConfirmPassword(v: String) = _uiState.update { it.copy(confirmPassword = v, error = null) }
    fun setNombre(v: String) = _uiState.update { it.copy(nombre = v, error = null) }
    fun setApellidos(v: String) = _uiState.update { it.copy(apellidos = v, error = null) }
    fun setTelefono(v: String) = _uiState.update { it.copy(telefono = v, error = null) }
    fun setDni(v: String) = _uiState.update { it.copy(dni = v, error = null) }
    fun setPropertyAddress(v: String) = _uiState.update { it.copy(propertyAddress = v, error = null) }
    fun setAgenciaNombre(v: String) = _uiState.update { it.copy(agenciaNombre = v, error = null) }
    fun setAgenciaCif(v: String) = _uiState.update { it.copy(agenciaCif = v, error = null) }
    fun setEspecialidad(v: String) = _uiState.update { it.copy(especialidad = v, error = null) }

    fun register(onSuccess: (Role, Int) -> Unit) {
        val s = _uiState.value

        // Validaciones comunes
        if (s.email.isBlank() || s.password.isBlank() || s.nombre.isBlank() || s.apellidos.isBlank()) {
            _uiState.update { it.copy(error = "Nombre, apellidos, email y contraseña son obligatorios") }
            return
        }
        if (!s.email.contains("@")) {
            _uiState.update { it.copy(error = "El email no es válido") }
            return
        }
        if (s.password.length < 4) {
            _uiState.update { it.copy(error = "La contraseña debe tener al menos 4 caracteres") }
            return
        }
        if (s.password != s.confirmPassword) {
            _uiState.update { it.copy(error = "Las contraseñas no coinciden") }
            return
        }
        // Validaciones por rol
        when (s.role) {
            Role.TENANT -> {
                if (s.propertyAddress.isBlank()) {
                    _uiState.update { it.copy(error = "Indica la dirección de tu vivienda") }
                    return
                }
            }
            Role.AGENCY -> {
                if (s.agenciaNombre.isBlank()) {
                    _uiState.update { it.copy(error = "El nombre de la agencia es obligatorio") }
                    return
                }
            }
            Role.MAINTENANCE -> {
                if (s.especialidad.isBlank()) {
                    _uiState.update { it.copy(error = "Indica tu especialidad") }
                    return
                }
            }
        }

        _uiState.update { it.copy(loading = true, error = null) }

        scope.launch {
            try {
                val newId = withContext(Dispatchers.IO) {
                    repo.registerCompleto(
                        email = s.email,
                        password = s.password,
                        role = s.role.name,
                        nombre = s.nombre,
                        apellidos = s.apellidos,
                        telefono = s.telefono,
                        dni = s.dni,
                        propertyAddress = s.propertyAddress,
                        agenciaNombre = s.agenciaNombre,
                        agenciaCif = s.agenciaCif,
                        especialidad = s.especialidad
                    )
                }

                when {
                    newId == -2L -> {
                        _uiState.update { it.copy(loading = false, error = "Ese email ya está registrado") }
                    }
                    newId > 0 -> {
                        _uiState.update { it.copy(loading = false, success = true) }
                        onSuccess(s.role, newId.toInt())
                    }
                    else -> {
                        _uiState.update { it.copy(loading = false, error = "Error al crear la cuenta") }
                    }
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(loading = false, error = "Error: ${e.message ?: "desconocido"}")
                }
            }
        }
    }
}
