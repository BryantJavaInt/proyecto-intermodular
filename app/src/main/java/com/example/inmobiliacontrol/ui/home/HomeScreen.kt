package com.example.inmobiliacontrol.ui.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
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
        Role.TENANT -> "Panel de inquilino"
        Role.AGENCY -> "Panel de agencia"
        Role.MAINTENANCE -> "Panel de mantenimiento"
    }

    val subtitle = when (role) {
        Role.TENANT -> "Desde aquí puedes registrar incidencias y consultar su estado."
        Role.AGENCY -> "Desde aquí puedes crear, revisar y gestionar todas las incidencias."
        Role.MAINTENANCE -> "Desde aquí puedes consultar incidencias en proceso y cerrarlas cuando termines el trabajo."
    }

    val icon = when (role) {
        Role.TENANT -> "🏠"
        Role.AGENCY -> "🗂️"
        Role.MAINTENANCE -> "🛠️"
    }

    val puedeCrearIncidencia = role == Role.TENANT || role == Role.AGENCY

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color(0xFFF4F7FB)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFEAF3FF)),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = icon,
                        style = MaterialTheme.typography.headlineLarge
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = title,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1A1A1A)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color(0xFF616161)
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            if (puedeCrearIncidencia) {
                Button(
                    onClick = onNavigateToCreateTicket,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                ) {
                    Text("Crear incidencia")
                }

                Spacer(modifier = Modifier.height(12.dp))
            }

            Button(
                onClick = onNavigateToTickets,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                Text("Ver incidencias")
            }

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedButton(
                onClick = onLogout,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = Color(0xFF1565C0)
                )
            ) {
                Text("Cerrar sesión")
            }
        }
    }
}
