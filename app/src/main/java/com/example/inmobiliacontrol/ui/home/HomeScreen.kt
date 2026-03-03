package com.example.inmobiliacontrol.ui.home

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.inmobiliacontrol.Role

@Composable
fun HomeScreen(role: Role, onLogout: () -> Unit) {
    val title = when (role) {
        Role.TENANT -> "Panel Inquilino"
        Role.AGENCY -> "Panel Agencia"
        Role.OWNER -> "Panel Dueño"
    }

    Surface(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .padding(24.dp)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(title, style = MaterialTheme.typography.headlineSmall)
            Spacer(Modifier.height(12.dp))
            Text("Aquí irá la segunda pantalla específica (por definir).")
            Spacer(Modifier.height(24.dp))
            Button(onClick = onLogout) { Text("Cerrar sesión") }
        }
    }
}