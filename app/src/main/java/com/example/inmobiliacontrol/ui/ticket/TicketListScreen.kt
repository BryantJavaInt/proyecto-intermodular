package com.example.inmobiliacontrol.ui.ticket

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import androidx.compose.runtime.mutableStateMapOf
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
import com.example.inmobiliacontrol.entity.Property
import com.example.inmobiliacontrol.entity.Ticket
import com.example.inmobiliacontrol.repository.PropertyRepository
import com.example.inmobiliacontrol.repository.TicketRepository
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun TicketListScreen(
    role: Role,
    userId: Int,
    onOpenDetail: (Int) -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val db = remember { InmobiliaDatabase.getInstance(context) }
    val ticketRepository = remember { TicketRepository(db.ticketDao()) }
    val propertyRepository = remember { PropertyRepository(db.propertyDao()) }

    val tickets = remember { mutableStateListOf<Ticket>() }
    val propertyMap = remember { mutableStateMapOf<Int, Property>() }

    var loading by remember { mutableStateOf(true) }

    fun recargarTickets() {
        scope.launch {
            loading = true
            tickets.clear()
            propertyMap.clear()

            val allData = when (role) {
                Role.TENANT -> ticketRepository.getTicketsByUser(userId)
                else -> ticketRepository.getAllTickets()
            }

            val filteredData = when (role) {
                Role.TENANT -> allData
                Role.AGENCY -> allData
                Role.MAINTENANCE -> allData.filter {
                    it.status.equals("En proceso", ignoreCase = true) ||
                            it.status.equals("Cerrado", ignoreCase = true)
                }
            }

            tickets.addAll(filteredData)

            val propertyIds = filteredData.mapNotNull { it.propertyId }.distinct()
            propertyIds.forEach { propertyId ->
                val property = propertyRepository.getPropertyById(propertyId)
                if (property != null) {
                    propertyMap[propertyId] = property
                }
            }

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
                    item {
                        TicketListHeader(role = role, total = tickets.size)
                    }

                    items(tickets) { ticket ->
                        TicketCard(
                            ticket = ticket,
                            property = ticket.propertyId?.let { propertyMap[it] },
                            role = role,
                            onOpenDetail = { onOpenDetail(ticket.ticketId) },
                            onChangeStatus = { nuevoEstado ->
                                scope.launch {
                                    ticketRepository.updateTicketStatus(ticket.ticketId, nuevoEstado)
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
fun TicketListHeader(role: Role, total: Int) {
    val title = when (role) {
        Role.TENANT -> "Mis incidencias"
        Role.AGENCY -> "Gestión de incidencias"
        Role.MAINTENANCE -> "Incidencias asignadas"
    }

    val subtitle = when (role) {
        Role.TENANT -> "Aquí puedes consultar el estado de las incidencias creadas."
        Role.AGENCY -> "Aquí puedes revisar todas las incidencias y gestionar sus estados."
        Role.MAINTENANCE -> "Aquí puedes consultar las incidencias en proceso o cerradas."
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = subtitle,
            style = MaterialTheme.typography.bodyMedium,
            color = Color(0xFF616161)
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = "Total: $total",
            style = MaterialTheme.typography.bodySmall,
            color = Color(0xFF757575)
        )
    }
}

@Composable
fun TicketCard(
    ticket: Ticket,
    property: Property?,
    role: Role,
    onOpenDetail: () -> Unit,
    onChangeStatus: (String) -> Unit
) {
    val (estadoBg, estadoText) = getStatusColors(ticket.status)
    val (prioridadBg, prioridadText) = getPriorityColors(ticket.priority)

    val puedeEditarEstadoAgencia = role == Role.AGENCY
    val puedeCerrarMantenimiento =
        role == Role.MAINTENANCE && ticket.status.equals("En proceso", ignoreCase = true)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .clickable { onOpenDetail() },
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top
            ) {
                Text(
                    text = categoryIcon(ticket.category),
                    style = MaterialTheme.typography.headlineSmall
                )

                Spacer(modifier = Modifier.padding(4.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = ticket.title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1A1A1A)
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "Fecha: ${formatDate(ticket.createdAt)}",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFF9E9E9E)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

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

            if (property != null) {
                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Propiedad: ${formatPropertyLabel(property)}",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFF616161)
                )
            }

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

            if (puedeEditarEstadoAgencia) {
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
                        onClick = { onChangeStatus("Cerrado") },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Cerrado")
                    }
                }
            }

            if (puedeCerrarMantenimiento) {
                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = { onChangeStatus("Cerrado") },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Marcar como cerrado")
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
        "CERRADO" -> Pair(Color(0xFFE8F5E9), Color(0xFF2E7D32))
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

fun formatPropertyLabel(property: Property): String {
    return "${property.address} - ${property.reference}"
}

fun categoryIcon(category: String): String {
    return when (category.lowercase(Locale.getDefault())) {
        "fontanería" -> "🚿"
        "electricidad" -> "💡"
        "carpintería" -> "🪵"
        "pintura" -> "🖌️"
        "electrodomésticos" -> "🧺"
        "humedades" -> "💧"
        else -> "🏠"
    }
}

