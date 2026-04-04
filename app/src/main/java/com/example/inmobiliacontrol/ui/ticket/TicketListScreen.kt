package com.example.inmobiliacontrol.ui.ticket

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.inmobiliacontrol.Role
import com.example.inmobiliacontrol.database.InmobiliaDatabase
import com.example.inmobiliacontrol.entity.Ticket
import com.example.inmobiliacontrol.repository.TicketRepository
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun TicketListScreen(role: Role) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val repository = remember {
        val db = InmobiliaDatabase.getInstance(context)
        TicketRepository(db.ticketDao())
    }

    val tickets = remember { mutableStateListOf<Ticket>() }
    var loading by remember { mutableStateOf(true) }

    fun recargarTickets() {
        scope.launch {
            loading = true
            tickets.clear()

            val userId = 1

            val data = when (role) {
                Role.TENANT -> repository.getTicketsByUser(userId)
                else -> repository.getAllTickets()
            }

            tickets.addAll(data)
            loading = false
        }
    }

    LaunchedEffect(Unit) {
        recargarTickets()
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color(0xFFF5F5F5)
    ) {
        when {
            loading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Cargando incidencias...")
                }
            }

            tickets.isEmpty() -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No hay incidencias registradas todavía.",
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }

            else -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(tickets) { ticket ->
                        TicketCard(
                            ticket = ticket,
                            role = role,
                            onChangeStatus = { nuevoEstado ->
                                scope.launch {
                                    repository.updateTicketStatus(ticket.ticketId, nuevoEstado)
                                    recargarTickets()
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun TicketCard(
    ticket: Ticket,
    role: Role,
    onChangeStatus: (String) -> Unit
) {
    val (estadoBg, estadoText) = getStatusColors(ticket.status)
    val (prioridadBg, prioridadText) = getPriorityColors(ticket.priority)
    val puedeEditarEstado = role == Role.AGENCY

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Text(
                text = ticket.title,
                modifier = Modifier.fillMaxWidth(),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1A1A1A)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                InfoChip(
                    text = ticket.category,
                    backgroundColor = Color(0xFFE3F2FD),
                    textColor = Color(0xFF1565C0)
                )

                InfoChip(
                    text = ticket.priority,
                    backgroundColor = prioridadBg,
                    textColor = prioridadText
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Fecha: ${formatDate(ticket.createdAt)}",
                style = MaterialTheme.typography.bodySmall,
                color = Color(0xFF9E9E9E)
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = ticket.description,
                style = MaterialTheme.typography.bodyMedium,
                color = Color(0xFF616161)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                StatusChip(
                    text = ticket.status.uppercase(),
                    backgroundColor = estadoBg,
                    textColor = estadoText
                )
            }

            if (puedeEditarEstado) {
                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Cambiar estado",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = { onChangeStatus("Abierto") },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Abierto")
                    }

                    Button(
                        onClick = { onChangeStatus("En proceso") },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Proceso")
                    }

                    Button(
                        onClick = { onChangeStatus("Resuelta") },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Resuelta")
                    }
                }
            }
        }
    }
}

@Composable
fun StatusChip(
    text: String,
    backgroundColor: Color,
    textColor: Color
) {
    Box(
        modifier = Modifier
            .background(
                color = backgroundColor,
                shape = MaterialTheme.shapes.small
            )
            .padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
        Text(
            text = text,
            color = textColor,
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun InfoChip(
    text: String,
    backgroundColor: Color,
    textColor: Color
) {
    Box(
        modifier = Modifier
            .background(
                color = backgroundColor,
                shape = MaterialTheme.shapes.small
            )
            .padding(horizontal = 10.dp, vertical = 5.dp)
    ) {
        Text(
            text = text,
            color = textColor,
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.Medium
        )
    }
}

fun getStatusColors(estado: String): Pair<Color, Color> {
    return when (estado.uppercase()) {
        "ABIERTO", "ABIERTA" -> Pair(Color(0xFFFFF3E0), Color(0xFFEF6C00))
        "EN PROCESO", "PROCESO" -> Pair(Color(0xFFFFF8E1), Color(0xFFF9A825))
        "RESUELTA" -> Pair(Color(0xFFE8F5E9), Color(0xFF2E7D32))
        else -> Pair(Color.LightGray, Color.DarkGray)
    }
}

fun getPriorityColors(prioridad: String): Pair<Color, Color> {
    return when (prioridad.uppercase()) {
        "ALTA" -> Pair(Color(0xFFFFEBEE), Color(0xFFD32F2F))
        "MEDIA" -> Pair(Color(0xFFFFF8E1), Color(0xFFF57F17))
        "BAJA" -> Pair(Color(0xFFE8F5E9), Color(0xFF2E7D32))
        else -> Pair(Color.LightGray, Color.DarkGray)
    }
}

fun formatDate(timestamp: Long): String {
    val formatter = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
    return formatter.format(Date(timestamp))
}