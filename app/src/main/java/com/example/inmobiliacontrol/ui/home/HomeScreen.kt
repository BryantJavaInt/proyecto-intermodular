package com.example.inmobiliacontrol.ui.home

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.inmobiliacontrol.Role

@Composable
fun HomeScreen(
    role: Role,
    onLogout: () -> Unit,
    onNavigateToTickets: () -> Unit,
    onNavigateToCreateTicket: () -> Unit
) {
    val title = when (role) {
        Role.TENANT -> "Panel Inquilino"
        Role.AGENCY -> "Panel Agencia"
        Role.MAINTENANCE -> "Panel Mantenimiento"
    }

    val description = when (role) {
        Role.TENANT -> "Desde aquí puedes registrar incidencias y consultar su estado."
        Role.AGENCY -> "Desde aquí puedes crear, consultar y gestionar incidencias."
        Role.MAINTENANCE -> "Desde aquí puedes consultar y gestionar las incidencias asignadas."
    }

    val puedeCrearIncidencia = role == Role.TENANT || role == Role.AGENCY

    Surface(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .padding(24.dp)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(title, style = MaterialTheme.typography.headlineSmall)

            Spacer(Modifier.height(12.dp))

            Text(description)

            Spacer(Modifier.height(24.dp))

            if (puedeCrearIncidencia) {
                Button(
                    onClick = onNavigateToCreateTicket,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Crear incidencia")
                }

                Spacer(Modifier.height(16.dp))
            }

            Button(
                onClick = onNavigateToTickets,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Ver incidencias")
            }

            Spacer(Modifier.height(16.dp))

            Button(
                onClick = onLogout,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Cerrar sesión")
            }
        }
    }
}