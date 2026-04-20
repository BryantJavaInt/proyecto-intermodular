package com.example.inmobiliacontrol.ui.ticket

import android.app.DatePickerDialog
import android.app.TimePickerDialog
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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

    val categoriasDisponibles = listOf(
        "Fontanería", "Electricidad", "Carpintería",
        "Pintura", "Electrodomésticos", "Humedades", "General"
    )
    val prioridadesDisponibles = listOf("Alta", "Media", "Baja")

    var categoria by remember { mutableStateOf("") }
    var categoriaExpanded by remember { mutableStateOf(false) }

    var titulo by remember { mutableStateOf("") }
    var descripcion by remember { mutableStateOf("") }
    var prioridad by remember { mutableStateOf("Media") }
    var prioridadExpanded by remember { mutableStateOf(false) }
    var fechaDisponible by remember { mutableStateOf("") }
    var horaDisponible by remember { mutableStateOf("") }

    // ── PROPIEDAD: texto libre + referencia opcional ──────────────────
    var addressInput by remember { mutableStateOf("") }
    var referenceInput by remember { mutableStateOf("") }

    var loading by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }

    val puedeElegirPrioridad = role == Role.AGENCY

    val calendar = remember { Calendar.getInstance() }

    val datePickerDialog = remember {
        DatePickerDialog(
            context,
            { _, year, month, dayOfMonth ->
                fechaDisponible = String.format(
                    Locale.getDefault(), "%02d/%02d/%04d", dayOfMonth, month + 1, year
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
                horaDisponible = String.format(Locale.getDefault(), "%02d:%02d", hourOfDay, minute)
            },
            calendar.get(Calendar.HOUR_OF_DAY),
            calendar.get(Calendar.MINUTE),
            true
        )
    }

    Surface(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .padding(24.dp)
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.Top
        ) {
            Text(text = "Crear incidencia", style = MaterialTheme.typography.headlineSmall)

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = if (role == Role.AGENCY) {
                    "Registra una incidencia y define su prioridad inicial."
                } else {
                    "Registra una incidencia asociada a una vivienda."
                },
                style = MaterialTheme.typography.bodyMedium,
                color = Color(0xFF757575)
            )

            Spacer(modifier = Modifier.height(20.dp))

            // ── PROPIEDAD: campo libre ────────────────────────────────
            SectionLabel("Propiedad *")
            Spacer(modifier = Modifier.height(6.dp))

            OutlinedTextField(
                value = addressInput,
                onValueChange = { addressInput = it; error = null },
                label = { Text("Dirección") },
                placeholder = { Text("Ej: Calle Mayor 10, Madrid") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = referenceInput,
                onValueChange = { referenceInput = it },
                label = { Text("Piso / Referencia (opcional)") },
                placeholder = { Text("Ej: 2ºB, Bajo, Ático") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(20.dp))

            // ── CATEGORÍA ─────────────────────────────────────────────
            SectionLabel("Categoría *")
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
                                error = null
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // ── TÍTULO ────────────────────────────────────────────────
            OutlinedTextField(
                value = titulo,
                onValueChange = { titulo = it; error = null },
                label = { Text("Título *") },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Ej: Fuga en el baño") },
                singleLine = true
            )

            Spacer(modifier = Modifier.height(12.dp))

            // ── DESCRIPCIÓN ───────────────────────────────────────────
            OutlinedTextField(
                value = descripcion,
                onValueChange = { descripcion = it; error = null },
                label = { Text("Descripción *") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 4,
                placeholder = { Text("Describe el problema con el mayor detalle posible") }
            )

            Spacer(modifier = Modifier.height(12.dp))

            // ── PRIORIDAD (solo AGENCY, dropdown) ─────────────────────
            if (puedeElegirPrioridad) {
                SectionLabel("Prioridad")
                Spacer(modifier = Modifier.height(6.dp))

                Box(modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(
                        value = prioridad,
                        onValueChange = {},
                        readOnly = true,
                        modifier = Modifier.fillMaxWidth(),
                        trailingIcon = { Text("▼") }
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                            .clickable { prioridadExpanded = true }
                    )
                    DropdownMenu(
                        expanded = prioridadExpanded,
                        onDismissRequest = { prioridadExpanded = false }
                    ) {
                        prioridadesDisponibles.forEach { opcion ->
                            DropdownMenuItem(
                                text = { Text(opcion) },
                                onClick = {
                                    prioridad = opcion
                                    prioridadExpanded = false
                                }
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
            }

            // ── FECHA DISPONIBLE ──────────────────────────────────────
            SectionLabel("Fecha disponible")
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

            // ── HORA DISPONIBLE ───────────────────────────────────────
            SectionLabel("Hora disponible")
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

            Spacer(modifier = Modifier.height(20.dp))

            // ── ERROR ─────────────────────────────────────────────────
            error?.let {
                Text(it, color = MaterialTheme.colorScheme.error)
                Spacer(modifier = Modifier.height(8.dp))
            }

            // ── BOTÓN CREAR ───────────────────────────────────────────
            Button(
                onClick = {
                    if (addressInput.isBlank()) {
                        error = "Introduce la dirección de la propiedad"
                        return@Button
                    }
                    if (categoria.isBlank() || titulo.isBlank() || descripcion.isBlank()) {
                        error = "Rellena todos los campos obligatorios (*)"
                        return@Button
                    }

                    val prioridadFinal = if (role == Role.TENANT) "Media" else prioridad
                    loading = true
                    error = null

                    scope.launch {
                        try {
                            // Buscar si ya existe esa propiedad exacta, si no crearla
                            val allProps = propertyRepository.getAllProperties()
                            val existing = allProps.firstOrNull {
                                it.address.equals(addressInput.trim(), ignoreCase = true) &&
                                it.reference.equals(referenceInput.trim(), ignoreCase = true)
                            }

                            val propertyId: Int = if (existing != null) {
                                existing.propertyId
                            } else {
                                val newId = propertyRepository.createProperty(
                                    address = addressInput.trim(),
                                    reference = referenceInput.trim()
                                )
                                newId.toInt()
                            }

                            ticketRepository.createTicket(
                                title = titulo,
                                description = descripcion,
                                category = categoria,
                                priority = prioridadFinal,
                                createdByUserId = userId,
                                propertyId = propertyId
                            )

                            loading = false
                            onSubmit()
                        } catch (e: Exception) {
                            loading = false
                            error = "Error al guardar: ${e.message}"
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = !loading
            ) {
                Text(if (loading) "Guardando..." else "Crear incidencia")
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun SectionLabel(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.labelLarge,
        fontWeight = FontWeight.SemiBold,
        color = Color(0xFF424242)
    )
}

private fun formatSelectedPropertyLabel(property: Property): String {
    return "${property.address} - ${property.reference}"
}
