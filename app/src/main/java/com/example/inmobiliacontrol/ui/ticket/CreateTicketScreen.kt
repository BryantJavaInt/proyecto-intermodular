package com.example.inmobiliacontrol.ui.ticket

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.inmobiliacontrol.Role
import com.example.inmobiliacontrol.database.InmobiliaDatabase
import com.example.inmobiliacontrol.repository.TicketRepository
import kotlinx.coroutines.launch

@Composable
fun CreateTicketScreen(
    role: Role,
    onSubmit: () -> Unit = {}
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val repository = remember {
        val db = InmobiliaDatabase.getInstance(context)
        TicketRepository(db.ticketDao())
    }

    var categoria by remember { mutableStateOf("") }
    var titulo by remember { mutableStateOf("") }
    var descripcion by remember { mutableStateOf("") }
    var prioridad by remember { mutableStateOf("Media") }
    var fechaDisponible by remember { mutableStateOf("") }
    var horaDisponible by remember { mutableStateOf("") }
    var loading by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }

    val puedeElegirPrioridad = role == Role.AGENCY
    val puedeCrearIncidencia = role == Role.TENANT || role == Role.AGENCY

    val tituloPantalla = when (role) {
        Role.TENANT -> "Crear incidencia"
        Role.AGENCY -> "Crear incidencia"
        Role.MAINTENANCE -> "Gestión de incidencia"
    }

    Surface(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .padding(24.dp)
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.Top
        ) {
            Text(
                text = tituloPantalla,
                style = MaterialTheme.typography.headlineSmall
            )

            Spacer(modifier = Modifier.height(12.dp))

            when (role) {
                Role.TENANT -> {
                    Text(
                        text = "Como inquilino puedes registrar una nueva incidencia. La prioridad se asignará automáticamente.",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
                Role.AGENCY -> {
                    Text(
                        text = "Como agencia puedes registrar incidencias y definir su prioridad inicial.",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
                Role.MAINTENANCE -> {
                    Text(
                        text = "El perfil de mantenimiento no crea incidencias nuevas.",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = categoria,
                onValueChange = { categoria = it },
                label = { Text("Categoría") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                placeholder = { Text("Ej: Fontanería") },
                enabled = !loading && puedeCrearIncidencia
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = titulo,
                onValueChange = { titulo = it },
                label = { Text("Título de la incidencia") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                enabled = !loading && puedeCrearIncidencia
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = descripcion,
                onValueChange = { descripcion = it },
                label = { Text("Descripción") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 4,
                enabled = !loading && puedeCrearIncidencia
            )

            Spacer(modifier = Modifier.height(12.dp))

            if (puedeElegirPrioridad) {
                OutlinedTextField(
                    value = prioridad,
                    onValueChange = { prioridad = it },
                    label = { Text("Prioridad") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    placeholder = { Text("Ej: Alta") },
                    enabled = !loading
                )

                Spacer(modifier = Modifier.height(12.dp))
            }

            OutlinedTextField(
                value = fechaDisponible,
                onValueChange = { fechaDisponible = it },
                label = { Text("Fecha disponible") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                placeholder = { Text("Ej: 02/04/2026") },
                enabled = !loading && puedeCrearIncidencia
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = horaDisponible,
                onValueChange = { horaDisponible = it },
                label = { Text("Hora disponible") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                placeholder = { Text("Ej: 10:30") },
                enabled = !loading && puedeCrearIncidencia
            )

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = { },
                modifier = Modifier.fillMaxWidth(),
                enabled = !loading && puedeCrearIncidencia
            ) {
                Text("Añadir foto")
            }

            Spacer(modifier = Modifier.height(12.dp))

            if (fechaDisponible.isNotBlank() || horaDisponible.isNotBlank()) {
                Text(
                    text = "Disponibilidad indicada: $fechaDisponible $horaDisponible",
                    style = MaterialTheme.typography.bodySmall
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            error?.let {
                Text(
                    text = it,
                    color = MaterialTheme.colorScheme.error
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    if (!puedeCrearIncidencia) {
                        error = "Este perfil no puede crear incidencias"
                        return@Button
                    }

                    if (categoria.isBlank() || titulo.isBlank() || descripcion.isBlank()) {
                        error = "Categoría, título y descripción son obligatorios"
                        return@Button
                    }

                    val prioridadFinal = when (role) {
                        Role.TENANT -> "Media"
                        Role.AGENCY -> if (prioridad.isBlank()) "Media" else prioridad
                        Role.MAINTENANCE -> "Media"
                    }

                    loading = true
                    error = null

                    scope.launch {
                        try {
                            repository.createTicket(
                                title = titulo,
                                description = descripcion,
                                category = categoria,
                                priority = prioridadFinal,
                                createdByUserId = 1
                            )
                            loading = false
                            onSubmit()
                        } catch (e: Exception) {
                            loading = false
                            error = "Error al guardar la incidencia"
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = !loading
            ) {
                Text(if (loading) "Guardando..." else "Enviar incidencia")
            }
        }
    }
}