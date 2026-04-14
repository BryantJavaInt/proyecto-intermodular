package com.example.inmobiliacontrol.ui.ticket

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.inmobiliacontrol.Role
import com.example.inmobiliacontrol.database.InmobiliaDatabase
import com.example.inmobiliacontrol.entity.Property
import com.example.inmobiliacontrol.repository.PropertyRepository
import com.example.inmobiliacontrol.repository.TicketRepository
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.Locale

@Composable
fun CreateTicketScreen(
    role: Role,
    userId: Int,
    onSubmit: () -> Unit = {}
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val db = remember { InmobiliaDatabase.getInstance(context) }
    val ticketRepository = remember { TicketRepository(db.ticketDao()) }
    val propertyRepository = remember { PropertyRepository(db.propertyDao()) }

    val properties = remember { mutableStateListOf<Property>() }

    val categoriasDisponibles = listOf(
        "Fontanería",
        "Electricidad",
        "Carpintería",
        "Pintura",
        "Electrodomésticos",
        "Humedades",
        "General"
    )

    var categoria by remember { mutableStateOf("") }
    var categoriaExpanded by remember { mutableStateOf(false) }

    var titulo by remember { mutableStateOf("") }
    var descripcion by remember { mutableStateOf("") }
    var prioridad by remember { mutableStateOf("Media") }
    var fechaDisponible by remember { mutableStateOf("") }
    var horaDisponible by remember { mutableStateOf("") }

    var selectedPropertyId by remember { mutableStateOf<Int?>(null) }
    var selectedPropertyLabel by remember { mutableStateOf("") }
    var propertyExpanded by remember { mutableStateOf(false) }

    var loading by remember { mutableStateOf(false) }
    var loadingProperties by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }

    val puedeElegirPrioridad = role == Role.AGENCY

    val calendar = remember { Calendar.getInstance() }

    val datePickerDialog = remember {
        DatePickerDialog(
            context,
            { _, year, month, dayOfMonth ->
                fechaDisponible = String.format(
                    Locale.getDefault(),
                    "%02d/%02d/%04d",
                    dayOfMonth,
                    month + 1,
                    year
                )
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
    }

    val timePickerDialog = remember {
        TimePickerDialog(
            context,
            { _, hourOfDay, minute ->
                horaDisponible = String.format(
                    Locale.getDefault(),
                    "%02d:%02d",
                    hourOfDay,
                    minute
                )
            },
            calendar.get(Calendar.HOUR_OF_DAY),
            calendar.get(Calendar.MINUTE),
            true
        )
    }

    LaunchedEffect(Unit) {
        try {
            val result = propertyRepository.getAllProperties()
            properties.clear()
            properties.addAll(result)

            if (properties.isNotEmpty()) {
                val first = properties.first()
                selectedPropertyId = first.propertyId
                selectedPropertyLabel = formatSelectedPropertyLabel(first)
            }
        } finally {
            loadingProperties = false
        }
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
                text = "Crear incidencia",
                style = MaterialTheme.typography.headlineSmall
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = if (role == Role.AGENCY) {
                    "Registra una incidencia y define su prioridad inicial."
                } else {
                    "Registra una incidencia asociada a una vivienda."
                },
                style = MaterialTheme.typography.bodyMedium
            )

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "Propiedad",
                style = MaterialTheme.typography.labelLarge
            )

            Spacer(modifier = Modifier.height(6.dp))

            if (loadingProperties) {
                Text("Cargando propiedades...")
            } else {
                Box(modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(
                        value = selectedPropertyLabel,
                        onValueChange = {},
                        readOnly = true,
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("Selecciona una propiedad") },
                        trailingIcon = { Text("▼") }
                    )

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                            .clickable { propertyExpanded = true }
                    )

                    DropdownMenu(
                        expanded = propertyExpanded,
                        onDismissRequest = { propertyExpanded = false }
                    ) {
                        properties.forEach { property ->
                            DropdownMenuItem(
                                text = { Text(formatSelectedPropertyLabel(property)) },
                                onClick = {
                                    selectedPropertyId = property.propertyId
                                    selectedPropertyLabel = formatSelectedPropertyLabel(property)
                                    propertyExpanded = false
                                }
                            )
                        }
                    }
                }

                if (properties.isEmpty()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "No hay propiedades disponibles",
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Categoría",
                style = MaterialTheme.typography.labelLarge
            )

            Spacer(modifier = Modifier.height(6.dp))

            Box(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = categoria,
                    onValueChange = {},
                    readOnly = true,
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Selecciona una categoría") },
                    trailingIcon = { Text("▼") }
                )

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .clickable { categoriaExpanded = true }
                )

                DropdownMenu(
                    expanded = categoriaExpanded,
                    onDismissRequest = { categoriaExpanded = false }
                ) {
                    categoriasDisponibles.forEach { opcion ->
                        DropdownMenuItem(
                            text = { Text(opcion) },
                            onClick = {
                                categoria = opcion
                                categoriaExpanded = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = titulo,
                onValueChange = { titulo = it },
                label = { Text("Título") },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Ej: Fuga en baño") }
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = descripcion,
                onValueChange = { descripcion = it },
                label = { Text("Descripción") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 4,
                placeholder = { Text("Describe el problema con el mayor detalle posible") }
            )

            Spacer(modifier = Modifier.height(12.dp))

            if (puedeElegirPrioridad) {
                OutlinedTextField(
                    value = prioridad,
                    onValueChange = { prioridad = it },
                    label = { Text("Prioridad") },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Ej: Alta") }
                )

                Spacer(modifier = Modifier.height(12.dp))
            }

            Text(
                text = "Fecha disponible",
                style = MaterialTheme.typography.labelLarge
            )

            Spacer(modifier = Modifier.height(6.dp))

            Box(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = fechaDisponible,
                    onValueChange = {},
                    readOnly = true,
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Selecciona una fecha") },
                    trailingIcon = { Text("📅") }
                )

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .clickable { datePickerDialog.show() }
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Hora disponible",
                style = MaterialTheme.typography.labelLarge
            )

            Spacer(modifier = Modifier.height(6.dp))

            Box(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = horaDisponible,
                    onValueChange = {},
                    readOnly = true,
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Selecciona una hora") },
                    trailingIcon = { Text("🕒") }
                )

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .clickable { timePickerDialog.show() }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            error?.let {
                Text(it, color = MaterialTheme.colorScheme.error)
                Spacer(modifier = Modifier.height(8.dp))
            }

            Button(
                onClick = {
                    if (selectedPropertyId == null) {
                        error = "Selecciona una propiedad"
                        return@Button
                    }

                    if (categoria.isBlank() || titulo.isBlank() || descripcion.isBlank()) {
                        error = "Rellena todos los campos obligatorios"
                        return@Button
                    }

                    val prioridadFinal = if (role == Role.TENANT) "Media" else prioridad

                    loading = true
                    error = null

                    scope.launch {
                        try {
                            ticketRepository.createTicket(
                                title = titulo,
                                description = descripcion,
                                category = categoria,
                                priority = prioridadFinal,
                                createdByUserId = userId,
                                propertyId = selectedPropertyId
                            )

                            loading = false
                            onSubmit()
                        } catch (e: Exception) {
                            loading = false
                            error = "Error al guardar"
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = !loading
            ) {
                Text(if (loading) "Guardando..." else "Crear incidencia")
            }
        }
    }
}

private fun formatSelectedPropertyLabel(property: Property): String {
    return "${property.address} - ${property.reference}"
}



