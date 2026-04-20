package com.example.inmobiliacontrol.ui.register

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.inmobiliacontrol.Role

@Composable
fun RegisterScreen(
    onRegistered: (Role, Int) -> Unit,
    onBack: () -> Unit,
    vm: RegisterViewModel = viewModel()
) {
    val state by vm.uiState.collectAsState()

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color(0xFFF4F7FB)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 32.dp)
        ) {

            // ── CABECERA ───────────────────────────────────────────────
            Text(
                text = "Crear cuenta",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1A1A1A)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Rellena tus datos para registrarte en InmobiliaControl.",
                style = MaterialTheme.typography.bodyMedium,
                color = Color(0xFF757575)
            )

            Spacer(modifier = Modifier.height(24.dp))

            // ── SELECTOR DE ROL ────────────────────────────────────────
            SectionTitle("Tipo de cuenta")
            Spacer(modifier = Modifier.height(8.dp))
            RoleSelector(
                selectedRole = state.role,
                onRoleSelected = vm::setRole
            )

            Spacer(modifier = Modifier.height(24.dp))

            // ── DATOS PERSONALES ───────────────────────────────────────
            SectionTitle("Datos personales")
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = state.nombre,
                onValueChange = vm::setNombre,
                label = { Text("Nombre *") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
                value = state.apellidos,
                onValueChange = vm::setApellidos,
                label = { Text("Apellidos *") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
                value = state.telefono,
                onValueChange = vm::setTelefono,
                label = { Text("Teléfono") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone)
            )
            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
                value = state.dni,
                onValueChange = vm::setDni,
                label = { Text("DNI / NIE") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(24.dp))

            // ── CAMPOS ESPECÍFICOS POR ROL ─────────────────────────────
            when (state.role) {
                Role.TENANT -> {
                    SectionTitle("Datos de tu vivienda")
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = state.propertyAddress,
                        onValueChange = vm::setPropertyAddress,
                        label = { Text("Dirección de tu vivienda *") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        placeholder = { Text("Ej: Calle Mayor 10, 2ºB") }
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                }
                Role.AGENCY -> {
                    SectionTitle("Datos de la agencia")
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = state.agenciaNombre,
                        onValueChange = vm::setAgenciaNombre,
                        label = { Text("Nombre de la agencia *") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = state.agenciaCif,
                        onValueChange = vm::setAgenciaCif,
                        label = { Text("CIF de la agencia") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                }
                Role.MAINTENANCE -> {
                    SectionTitle("Datos profesionales")
                    Spacer(modifier = Modifier.height(8.dp))
                    EspecialidadDropdown(
                        selected = state.especialidad,
                        onSelected = vm::setEspecialidad
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                }
            }

            // ── CREDENCIALES ───────────────────────────────────────────
            SectionTitle("Credenciales de acceso")
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = state.email,
                onValueChange = vm::setEmail,
                label = { Text("Email *") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
            )
            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
                value = state.password,
                onValueChange = vm::setPassword,
                label = { Text("Contraseña * (mín. 4 caracteres)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                visualTransformation = PasswordVisualTransformation()
            )
            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
                value = state.confirmPassword,
                onValueChange = vm::setConfirmPassword,
                label = { Text("Confirmar contraseña *") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                visualTransformation = PasswordVisualTransformation(),
                isError = state.confirmPassword.isNotBlank() && state.password != state.confirmPassword,
                supportingText = {
                    if (state.confirmPassword.isNotBlank() && state.password != state.confirmPassword) {
                        Text("Las contraseñas no coinciden", color = MaterialTheme.colorScheme.error)
                    }
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // ── ERROR ──────────────────────────────────────────────────
            state.error?.let {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFFEBEE))
                ) {
                    Text(
                        text = it,
                        color = Color(0xFFD32F2F),
                        modifier = Modifier.padding(12.dp),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))
            }

            // ── BOTÓN CREAR CUENTA ─────────────────────────────────────
            Button(
                onClick = { vm.register(onRegistered) },
                modifier = Modifier.fillMaxWidth().height(52.dp),
                enabled = !state.loading
            ) {
                Text(
                    text = if (state.loading) "Creando cuenta..." else "Crear cuenta",
                    style = MaterialTheme.typography.titleSmall
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // ── VOLVER AL LOGIN ────────────────────────────────────────
            OutlinedButton(
                onClick = onBack,
                modifier = Modifier.fillMaxWidth().height(50.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF1565C0))
            ) {
                Text("Ya tengo cuenta — Iniciar sesión")
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

// ── COMPONENTES AUXILIARES ─────────────────────────────────────────────────────

@Composable
private fun SectionTitle(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.titleSmall,
        fontWeight = FontWeight.Bold,
        color = Color(0xFF1565C0)
    )
    HorizontalDivider(
        modifier = Modifier.padding(top = 4.dp),
        color = Color(0xFFBBDEFB),
        thickness = 1.dp
    )
}

@Composable
private fun RoleSelector(
    selectedRole: Role,
    onRoleSelected: (Role) -> Unit
) {
    val roles = listOf(
        Role.TENANT to "🏠  Inquilino",
        Role.AGENCY to "🗂️  Agencia",
        Role.MAINTENANCE to "🛠️  Mantenimiento"
    )

    val descriptions = mapOf(
        Role.TENANT to "Soy inquilino de una vivienda y quiero reportar incidencias.",
        Role.AGENCY to "Gestiono propiedades y coordino las incidencias de mis inquilinos.",
        Role.MAINTENANCE to "Me encargo de resolver las incidencias de mantenimiento."
    )

    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        roles.forEach { (role, label) ->
            val selected = selectedRole == role
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onRoleSelected(role) },
                colors = CardDefaults.cardColors(
                    containerColor = if (selected) Color(0xFFE3F2FD) else Color.White
                ),
                border = if (selected) {
                    androidx.compose.foundation.BorderStroke(2.dp, Color(0xFF1565C0))
                } else {
                    androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE0E0E0))
                }
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = selected,
                        onClick = { onRoleSelected(role) }
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = label,
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
                            color = if (selected) Color(0xFF1565C0) else Color(0xFF1A1A1A)
                        )
                        Text(
                            text = descriptions[role] ?: "",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFF757575)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun EspecialidadDropdown(
    selected: String,
    onSelected: (String) -> Unit
) {
    val especialidades = listOf(
        "Fontanería",
        "Electricidad",
        "Carpintería",
        "Pintura",
        "Electrodomésticos",
        "Climatización",
        "Albañilería",
        "General"
    )
    var expanded by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxWidth()) {
        OutlinedTextField(
            value = selected,
            onValueChange = {},
            readOnly = true,
            label = { Text("Especialidad *") },
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("Selecciona tu especialidad") },
            trailingIcon = { Text("▼") }
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .clickable { expanded = true }
        )
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            especialidades.forEach { esp ->
                DropdownMenuItem(
                    text = { Text(esp) },
                    onClick = {
                        onSelected(esp)
                        expanded = false
                    }
                )
            }
        }
    }
}
