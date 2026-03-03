package com.example.inmobiliacontrol.ui.login

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.inmobiliacontrol.R
import com.example.inmobiliacontrol.Role

@Composable
fun LoginScreen(
    onLoggedIn: (Role) -> Unit,
    vm: LoginViewModel = viewModel()
) {
    val state by vm.uiState.collectAsState()

    // Fondo azul suave
    val softBlue = Color(0xFFEAF3FF)

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = softBlue
    ) {
        Column(
            modifier = Modifier
                .padding(horizontal = 24.dp, vertical = 18.dp)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(10.dp))

            // Logo
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp, bottom = 12.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.logo_inmobiliacontrol),
                    contentDescription = "Logo",
                    modifier = Modifier
                        .fillMaxWidth(0.75f)
                        .height(200.dp)
                )
            }

            Spacer(Modifier.height(10.dp))

            // Dropdown de rol
            RoleDropdown(
                selectedRole = state.role,
                onRoleSelected = vm::setRole
            )

            Spacer(Modifier.height(12.dp))

            // Campo Email/Usuario (filtra emojis y caracteres raros)
            OutlinedTextField(
                value = state.email,
                onValueChange = { vm.setEmail(sanitizeUserInput(it)) },
                label = { Text("Email / Usuario") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
            )

            Spacer(Modifier.height(12.dp))

            // Contraseña (filtra emojis y caracteres raros)
            OutlinedTextField(
                value = state.password,
                onValueChange = { vm.setPassword(sanitizePasswordInput(it)) },
                label = { Text("Contraseña") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
            )

            Spacer(Modifier.height(16.dp))

            state.error?.let { msg ->
                Text(msg, color = MaterialTheme.colorScheme.error)
                Spacer(Modifier.height(8.dp))
            }

            Button(
                onClick = { vm.login(onSuccess = onLoggedIn) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
            ) {
                Text("Entrar")
            }
        }
    }
}

@Composable
private fun RoleDropdown(
    selectedRole: Role,
    onRoleSelected: (Role) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    val label = when (selectedRole) {
        Role.TENANT -> "Inquilino"
        Role.AGENCY -> "Agencia"
        Role.OWNER -> "Dueño"
    }

    Column(modifier = Modifier.fillMaxWidth()) {
        Text("Tipo de usuario", style = MaterialTheme.typography.labelLarge)
        Spacer(Modifier.height(6.dp))

        Box(modifier = Modifier.fillMaxWidth()) {
            OutlinedTextField(
                value = label,
                onValueChange = {},
                readOnly = true,
                singleLine = true,
                trailingIcon = { Text("▼") },
                modifier = Modifier.fillMaxWidth()
            )

            // Capa transparente para capturar el click en todo el campo
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .clickable { expanded = true }
            )
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            DropdownMenuItem(
                text = { Text("Inquilino") },
                onClick = { onRoleSelected(Role.TENANT); expanded = false }
            )
            DropdownMenuItem(
                text = { Text("Agencia") },
                onClick = { onRoleSelected(Role.AGENCY); expanded = false }
            )
            DropdownMenuItem(
                text = { Text("Dueño") },
                onClick = { onRoleSelected(Role.OWNER); expanded = false }
            )
        }
    }
}

/**
 * Permite letras, números, espacios y caracteres típicos de email/usuario.
 * El resto (incluidos emojis) se elimina.
 */
private fun sanitizeUserInput(input: String): String {
    val disallowed = Regex("[^a-zA-Z0-9@._\\-\\s]")
    return input.replace(disallowed, "")
}

/**
 * Permite letras, números y algunos símbolos comunes.
 * El resto (incluidos emojis) se elimina.
 */
private fun sanitizePasswordInput(input: String): String {
    val disallowed = Regex("[^a-zA-Z0-9._\\-]")
    return input.replace(disallowed, "")
}