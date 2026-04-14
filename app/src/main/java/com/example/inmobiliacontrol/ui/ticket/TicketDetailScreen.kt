package com.example.inmobiliacontrol.ui.ticket

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.inmobiliacontrol.Role
import com.example.inmobiliacontrol.database.InmobiliaDatabase
import com.example.inmobiliacontrol.entity.Property
import com.example.inmobiliacontrol.entity.Ticket
import com.example.inmobiliacontrol.repository.PropertyRepository
import com.example.inmobiliacontrol.repository.TicketRepository
import kotlinx.coroutines.launch

@Composable
fun TicketDetailScreen(
    role: Role,
    userId: Int,
    ticketId: Int,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val db = remember { InmobiliaDatabase.getInstance(context) }
    val ticketRepository = remember { TicketRepository(db.ticketDao()) }
    val propertyRepository = remember { PropertyRepository(db.propertyDao()) }

    var ticket by remember { mutableStateOf<Ticket?>(null) }
    var property by remember { mutableStateOf<Property?>(null) }
    var loading by remember { mutableStateOf(true) }

    fun recargarDetalle() {
        scope.launch {
            loading = true
            val loadedTicket = ticketRepository.getTicketById(ticketId)
            ticket = loadedTicket
            property = loadedTicket?.propertyId?.let { propertyRepository.getPropertyById(it) }
            loading = false
        }
    }

    LaunchedEffect(Unit) {
        recargarDetalle()
    }

    val currentTicket = ticket
    val puedeEditarEstadoAgencia = role == Role.AGENCY
    val puedeCerrarMantenimiento =
        role == Role.MAINTENANCE && currentTicket?.status.equals("En proceso", ignoreCase = true)

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color(0xFFF5F7FB)
    ) {
        when {
            loading -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "Cargando detalle...",
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }

            currentTicket == null -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "No se ha podido cargar la incidencia.",
                        style = MaterialTheme.typography.bodyLarge
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = onBack,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Volver")
                    }
                }
            }

            else -> {
                val (estadoBg, estadoText) = getStatusColors(currentTicket.status)
                val (prioridadBg, prioridadText) = getPriorityColors(currentTicket.priority)

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                ) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFEAF3FF)),
                        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(18.dp)
                        ) {
                            Text(
                                text = categoryIcon(currentTicket.category),
                                style = MaterialTheme.typography.headlineMedium,
                                modifier = Modifier.size(40.dp)
                            )

                            Spacer(modifier = Modifier.padding(6.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Detalle de incidencia",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF1A1A1A)
                                )

                                Spacer(modifier = Modifier.height(4.dp))

                                Text(
                                    text = "Consulta toda la información y, según el rol, actualiza el estado.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color(0xFF616161)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(20.dp)
                        ) {
                            Text(
                                text = currentTicket.title,
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF1A1A1A)
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                InfoChip(
                                    text = currentTicket.category,
                                    backgroundColor = Color(0xFFE3F2FD),
                                    textColor = Color(0xFF1565C0)
                                )

                                InfoChip(
                                    text = currentTicket.priority,
                                    backgroundColor = prioridadBg,
                                    textColor = prioridadText
                                )
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            StatusChip(
                                text = currentTicket.status.uppercase(),
                                backgroundColor = estadoBg,
                                textColor = estadoText
                            )

                            Spacer(modifier = Modifier.height(14.dp))

                            DetailRow("Fecha de creación", formatDate(currentTicket.createdAt))

                            if (property != null) {
                                Spacer(modifier = Modifier.height(10.dp))
                                DetailRow("Propiedad", formatPropertyLabel(property!!))
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            Text(
                                text = "Descripción",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.SemiBold,
                                color = Color(0xFF1A1A1A)
                            )

                            Spacer(modifier = Modifier.height(6.dp))

                            Text(
                                text = currentTicket.description,
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color(0xFF616161)
                            )
                        }
                    }

                    if (puedeEditarEstadoAgencia) {
                        Spacer(modifier = Modifier.height(16.dp))

                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(20.dp)
                            ) {
                                Text(
                                    text = "Acciones de agencia",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.SemiBold
                                )

                                Spacer(modifier = Modifier.height(12.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Button(
                                        onClick = {
                                            scope.launch {
                                                ticketRepository.updateTicketStatus(ticketId, "Abierto")
                                                recargarDetalle()
                                            }
                                        },
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Text("Abierto")
                                    }

                                    Button(
                                        onClick = {
                                            scope.launch {
                                                ticketRepository.updateTicketStatus(ticketId, "En proceso")
                                                recargarDetalle()
                                            }
                                        },
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Text("Proceso")
                                    }

                                    Button(
                                        onClick = {
                                            scope.launch {
                                                ticketRepository.updateTicketStatus(ticketId, "Cerrado")
                                                recargarDetalle()
                                            }
                                        },
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Text("Cerrado")
                                    }
                                }
                            }
                        }
                    }

                    if (puedeCerrarMantenimiento) {
                        Spacer(modifier = Modifier.height(16.dp))

                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(20.dp)
                            ) {
                                Text(
                                    text = "Acciones de mantenimiento",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.SemiBold
                                )

                                Spacer(modifier = Modifier.height(12.dp))

                                Button(
                                    onClick = {
                                        scope.launch {
                                            ticketRepository.updateTicketStatus(ticketId, "Cerrado")
                                            recargarDetalle()
                                        }
                                    },
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text("Marcar como cerrado")
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = onBack,
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1565C0))
                    ) {
                        Text("Volver")
                    }
                }
            }
        }
    }
}

@Composable
private fun DetailRow(label: String, value: String) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = Color(0xFF757575),
            fontWeight = FontWeight.Medium
        )

        Spacer(modifier = Modifier.height(2.dp))

        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            color = Color(0xFF1A1A1A)
        )
    }
}

