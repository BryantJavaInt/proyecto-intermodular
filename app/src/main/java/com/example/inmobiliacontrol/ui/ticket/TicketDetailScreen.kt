package com.example.inmobiliacontrol.ui.ticket

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
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
import com.example.inmobiliacontrol.entity.Comment
import com.example.inmobiliacontrol.entity.Property
import com.example.inmobiliacontrol.entity.Ticket
import com.example.inmobiliacontrol.entity.User
import com.example.inmobiliacontrol.repository.CommentRepository
import com.example.inmobiliacontrol.repository.PropertyRepository
import com.example.inmobiliacontrol.repository.TicketRepository
import com.example.inmobiliacontrol.repository.UserRepository
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
    val commentRepository = remember { CommentRepository(db.commentDao()) }
    val userRepository = remember { UserRepository(db.userDao()) }

    var ticket by remember { mutableStateOf<Ticket?>(null) }
    var property by remember { mutableStateOf<Property?>(null) }
    var comments by remember { mutableStateOf<List<Comment>>(emptyList()) }
    // Mapa userId -> User para mostrar nombre y rol en cada burbuja
    val authorMap = remember { mutableStateMapOf<Int, User>() }
    var loading by remember { mutableStateOf(true) }
    var nuevoComentario by remember { mutableStateOf("") }
    var enviandoComentario by remember { mutableStateOf(false) }

    fun recargarDetalle() {
        scope.launch {
            loading = true
            val loadedTicket = ticketRepository.getTicketById(ticketId)
            ticket = loadedTicket
            property = loadedTicket?.propertyId?.let { propertyRepository.getPropertyById(it) }
            val loadedComments = commentRepository.getCommentsByTicket(ticketId)
            comments = loadedComments

            // Cargar datos de cada autor único
            loadedComments.map { it.authorUserId }.distinct().forEach { authorId ->
                if (!authorMap.containsKey(authorId)) {
                    userRepository.getById(authorId)?.let { user ->
                        authorMap[authorId] = user
                    }
                }
            }
            loading = false
        }
    }

    LaunchedEffect(Unit) { recargarDetalle() }

    val currentTicket = ticket
    val puedeEditarEstadoAgencia = role == Role.AGENCY
    val puedeCerrarMantenimiento =
        role == Role.MAINTENANCE && currentTicket?.status.equals("En proceso", ignoreCase = true)

    Surface(modifier = Modifier.fillMaxSize(), color = Color(0xFFF5F7FB)) {
        when {
            loading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }

            currentTicket == null -> {
                Column(
                    modifier = Modifier.fillMaxSize().padding(24.dp),
                    verticalArrangement = Arrangement.Center
                ) {
                    Text("No se ha podido cargar la incidencia.", style = MaterialTheme.typography.bodyLarge)
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(onClick = onBack, modifier = Modifier.fillMaxWidth()) { Text("Volver") }
                }
            }

            else -> {
                val (estadoBg, estadoText) = getStatusColors(currentTicket.status)
                val (prioridadBg, prioridadText) = getPriorityColors(currentTicket.priority)

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp)
                ) {
                    // ── CABECERA ──────────────────────────────────────────────
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFEAF3FF)),
                        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                    ) {
                        Row(modifier = Modifier.fillMaxWidth().padding(18.dp)) {
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
                                    text = "Consulta el estado y comunícate con la agencia.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color(0xFF616161)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // ── DATOS DEL TICKET ──────────────────────────────────────
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(modifier = Modifier.fillMaxWidth().padding(20.dp)) {
                            Text(
                                text = currentTicket.title,
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF1A1A1A)
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
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

                    // ── ACCIONES AGENCIA ──────────────────────────────────────
                    if (puedeEditarEstadoAgencia) {
                        Spacer(modifier = Modifier.height(16.dp))
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                        ) {
                            Column(modifier = Modifier.fillMaxWidth().padding(20.dp)) {
                                Text(
                                    text = "Cambiar estado",
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
                                    ) { Text("Abierto") }
                                    Button(
                                        onClick = {
                                            scope.launch {
                                                ticketRepository.updateTicketStatus(ticketId, "En proceso")
                                                recargarDetalle()
                                            }
                                        },
                                        modifier = Modifier.weight(1f)
                                    ) { Text("Proceso") }
                                    Button(
                                        onClick = {
                                            scope.launch {
                                                ticketRepository.updateTicketStatus(ticketId, "Cerrado")
                                                recargarDetalle()
                                            }
                                        },
                                        modifier = Modifier.weight(1f)
                                    ) { Text("Cerrado") }
                                }
                            }
                        }
                    }

                    // ── ACCIONES MANTENIMIENTO ────────────────────────────────
                    if (puedeCerrarMantenimiento) {
                        Spacer(modifier = Modifier.height(16.dp))
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                        ) {
                            Column(modifier = Modifier.fillMaxWidth().padding(20.dp)) {
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
                                ) { Text("Marcar como cerrado") }
                            }
                        }
                    }

                    // ── CHAT / COMENTARIOS ────────────────────────────────────
                    Spacer(modifier = Modifier.height(16.dp))
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(modifier = Modifier.fillMaxWidth().padding(20.dp)) {

                            // Cabecera del chat
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text(text = "💬", style = MaterialTheme.typography.titleMedium)
                                Column {
                                    Text(
                                        text = "Chat de la incidencia",
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF1A1A1A)
                                    )
                                    Text(
                                        text = "Comunícate con la agencia sobre esta incidencia",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = Color(0xFF757575)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))
                            HorizontalDivider(color = Color(0xFFE3F2FD), thickness = 1.dp)
                            Spacer(modifier = Modifier.height(12.dp))

                            if (comments.isEmpty()) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 16.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Text(text = "💬", style = MaterialTheme.typography.headlineMedium)
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Text(
                                            text = "Aún no hay mensajes.",
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = Color(0xFF9E9E9E)
                                        )
                                        Text(
                                            text = "Sé el primero en escribir.",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = Color(0xFFBDBDBD)
                                        )
                                    }
                                }
                            } else {
                                comments.forEach { comment ->
                                    val author = authorMap[comment.authorUserId]
                                    ChatBubble(
                                        comment = comment,
                                        author = author,
                                        isOwnMessage = comment.authorUserId == userId,
                                        canDelete = role == Role.AGENCY || comment.authorUserId == userId,
                                        onDelete = {
                                            scope.launch {
                                                commentRepository.deleteComment(comment.commentId)
                                                recargarDetalle()
                                            }
                                        }
                                    )
                                    Spacer(modifier = Modifier.height(10.dp))
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))
                            HorizontalDivider(color = Color(0xFFE0E0E0))
                            Spacer(modifier = Modifier.height(12.dp))

                            // Campo de escritura
                            OutlinedTextField(
                                value = nuevoComentario,
                                onValueChange = { nuevoComentario = it },
                                modifier = Modifier.fillMaxWidth(),
                                placeholder = { Text("Escribe un mensaje...") },
                                minLines = 2,
                                maxLines = 5,
                                shape = RoundedCornerShape(12.dp)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Button(
                                onClick = {
                                    val mensaje = nuevoComentario.trim()
                                    if (mensaje.isBlank()) return@Button
                                    enviandoComentario = true
                                    scope.launch {
                                        commentRepository.createComment(
                                            message = mensaje,
                                            ticketId = ticketId,
                                            authorUserId = userId
                                        )
                                        nuevoComentario = ""
                                        enviandoComentario = false
                                        recargarDetalle()
                                    }
                                },
                                modifier = Modifier.align(Alignment.End),
                                enabled = !enviandoComentario && nuevoComentario.isNotBlank()
                            ) {
                                Text(
                                    text = if (enviandoComentario) "Enviando..." else "➤  Enviar",
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = onBack,
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1565C0))
                    ) { Text("Volver") }

                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }
}

// ── BURBUJA DE CHAT ────────────────────────────────────────────────────────────

@Composable
fun ChatBubble(
    comment: Comment,
    author: User?,
    isOwnMessage: Boolean,
    canDelete: Boolean,
    onDelete: () -> Unit
) {
    // Colores y alineación según si es mensaje propio o ajeno
    val bubbleColor = when {
        isOwnMessage -> Color(0xFF1565C0)           // azul — mensaje propio
        author?.role == "AGENCY" -> Color(0xFF2E7D32) // verde — agencia
        author?.role == "MAINTENANCE" -> Color(0xFF6A1B9A) // morado — mantenimiento
        else -> Color(0xFF455A64)                   // gris — otro inquilino
    }
    val textColor = Color.White
    val alignment = if (isOwnMessage) Alignment.End else Alignment.Start

    // Nombre a mostrar
    val authorName = when {
        isOwnMessage -> "Tú"
        author != null && author.nombre.isNotBlank() ->
            "${author.nombre} ${author.apellidos}".trim()
        else -> "Usuario"
    }

    // Etiqueta de rol
    val roleTag = when (author?.role) {
        "AGENCY" -> "🏢 Agencia"
        "MAINTENANCE" -> "🛠️ Mantenimiento"
        "TENANT" -> "🏠 Inquilino"
        else -> ""
    }

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = alignment
    ) {
        // Nombre + rol encima de la burbuja
        if (!isOwnMessage) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(start = 4.dp, bottom = 2.dp)
            ) {
                Text(
                    text = authorName,
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = bubbleColor
                )
                if (roleTag.isNotBlank()) {
                    Box(
                        modifier = Modifier
                            .background(
                                color = bubbleColor.copy(alpha = 0.12f),
                                shape = RoundedCornerShape(4.dp)
                            )
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = roleTag,
                            style = MaterialTheme.typography.labelSmall,
                            color = bubbleColor
                        )
                    }
                }
            }
        }

        // Burbuja del mensaje
        Box(
            modifier = Modifier
                .fillMaxWidth(0.82f)
                .background(color = bubbleColor, shape = RoundedCornerShape(
                    topStart = if (isOwnMessage) 16.dp else 4.dp,
                    topEnd = if (isOwnMessage) 4.dp else 16.dp,
                    bottomStart = 16.dp,
                    bottomEnd = 16.dp
                ))
                .padding(horizontal = 14.dp, vertical = 10.dp)
        ) {
            Column {
                Text(
                    text = comment.message,
                    style = MaterialTheme.typography.bodyMedium,
                    color = textColor
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = formatDate(comment.createdAt),
                    style = MaterialTheme.typography.labelSmall,
                    color = textColor.copy(alpha = 0.7f),
                    modifier = Modifier.align(Alignment.End)
                )
            }
        }

        // Botón eliminar debajo de la burbuja
        if (canDelete) {
            TextButton(
                onClick = onDelete,
                modifier = Modifier.wrapContentWidth(),
                contentPadding = PaddingValues(horizontal = 4.dp, vertical = 0.dp)
            ) {
                Text(
                    text = "🗑 Eliminar",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color(0xFFD32F2F)
                )
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
