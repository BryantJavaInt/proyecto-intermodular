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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.inmobiliacontrol.R
import com.example.inmobiliacontrol.Role

@Composable
fun LoginScreen(
    onLoggedIn: (Role, Int) -> Unit,
    onNavigateToRegister: () -> Unit,
    vm: LoginViewModel = viewModel()
) {
    val state by vm.uiState.collectAsState()

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color(0xFFF4F7FB)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            Image(
                painter = painterResource(id = R.drawable.logo_inmobiliacontrol),
                contentDescription = "Logo",
                modifier = Modifier
                    .fillMaxWidth(0.7f)
                    .height(160.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "InmobiliaControl",
                style = MaterialTheme.typography.headlineSmall,
                color = Color(0xFF1A1A1A)
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Gestión de incidencias",
                style = MaterialTheme.typography.bodyMedium,
                color = Color(0xFF757575)
            )

            Spacer(modifier = Modifier.height(32.dp))

            OutlinedTextField(
                value = state.email,
                onValueChange = vm::setEmail,
                label = { Text("Email") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = state.password,
                onValueChange = vm::setPassword,
                label = { Text("Contraseña") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                visualTransformation = PasswordVisualTransformation()
            )

            Spacer(modifier = Modifier.height(16.dp))

            state.error?.let {
                Text(text = it, color = MaterialTheme.colorScheme.error, textAlign = TextAlign.Center)
                Spacer(modifier = Modifier.height(8.dp))
            }

            Button(
                onClick = { vm.login(onSuccess = onLoggedIn) },
                modifier = Modifier.fillMaxWidth().height(50.dp),
                enabled = !state.loading
            ) {
                Text(
                    text = if (state.loading) "Entrando..." else "Iniciar sesión",
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            HorizontalDivider(color = Color(0xFFE0E0E0))

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "¿No tienes cuenta?",
                style = MaterialTheme.typography.bodyMedium,
                color = Color(0xFF757575)
            )

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedButton(
                onClick = onNavigateToRegister,
                modifier = Modifier.fillMaxWidth().height(50.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF1565C0)),
                border = androidx.compose.foundation.BorderStroke(2.dp, Color(0xFF1565C0))
            ) {
                Text(
                    text = "Crear cuenta nueva",
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

@Composable
fun RoleDropdown(
    selectedRole: Role,
    onRoleSelected: (Role) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    val label = when (selectedRole) {
        Role.TENANT -> "Inquilino"
        Role.AGENCY -> "Agencia"
        Role.MAINTENANCE -> "Mantenimiento"
    }

    Box(modifier = Modifier.fillMaxWidth()) {
        OutlinedTextField(
            value = label,
            onValueChange = {},
            readOnly = true,
            label = { Text("Rol (cuenta nueva)") },
            modifier = Modifier.fillMaxWidth(),
            trailingIcon = { Text("▼") }
        )
        Box(modifier = Modifier.matchParentSize().clickable { expanded = true })
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            Role.entries.forEach { role ->
                val roleLabel = when (role) {
                    Role.TENANT -> "Inquilino"
                    Role.AGENCY -> "Agencia"
                    Role.MAINTENANCE -> "Mantenimiento"
                }
                DropdownMenuItem(
                    text = { Text(roleLabel) },
                    onClick = {
                        onRoleSelected(role)
                        expanded = false
                    }
                )
            }
        }
    }
}
