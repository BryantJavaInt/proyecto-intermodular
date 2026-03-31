package com.example.inmobiliacontrol

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class DetalleIncidenciaActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detalle_incidencia)

        val tvTituloDetalle = findViewById<TextView>(R.id.tvTituloDetalle)
        val tvDescripcionDetalle = findViewById<TextView>(R.id.tvDescripcionDetalle)
        val spinnerEstado = findViewById<Spinner>(R.id.spinnerEstado)
        val spinnerPrioridad = findViewById<Spinner>(R.id.spinnerPrioridad)
        val btnActualizar = findViewById<Button>(R.id.btnActualizar)
        val btnVolver = findViewById<Button>(R.id.btnVolver)

        val ticketId = intent.getIntExtra("TICKET_ID", -1)
        val rolUsuario = intent.getStringExtra("ROL_USUARIO") ?: "Mantenimiento"

        val ticket = MainActivity.misIncidencias.find { it.id == ticketId }

        if (ticket != null) {
            tvTituloDetalle.text = ticket.titulo
            tvDescripcionDetalle.text = ticket.descripcion

            val opcionesEstado = arrayOf("Abierta", "En proceso", "Resuelta")
            spinnerEstado.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, opcionesEstado)
            spinnerEstado.setSelection(opcionesEstado.indexOf(ticket.estado).coerceAtLeast(0))

            val opcionesPrioridad = arrayOf("Baja", "Media", "Alta")
            spinnerPrioridad.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, opcionesPrioridad)
            spinnerPrioridad.setSelection(opcionesPrioridad.indexOf(ticket.prioridad).coerceAtLeast(0))

            if (rolUsuario == "Inquilino") {
                spinnerEstado.isEnabled = false
                spinnerPrioridad.isEnabled = false
                btnActualizar.isEnabled = false
            }
        }

        btnActualizar.setOnClickListener {
            ticket?.let {
                it.estado = spinnerEstado.selectedItem.toString()
                it.prioridad = spinnerPrioridad.selectedItem.toString()
                Toast.makeText(this, "Incidencia actualizada", Toast.LENGTH_SHORT).show()
                finish()
            }
        }

        btnVolver.setOnClickListener { finish() }
    }
}