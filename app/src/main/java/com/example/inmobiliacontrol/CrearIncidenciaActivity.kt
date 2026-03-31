package com.example.inmobiliacontrol

import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class CrearIncidenciaActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_crear_incidencia)

        val spinnerCategoria = findViewById<Spinner>(R.id.spinnerCategoria)
        val etTitulo = findViewById<EditText>(R.id.etTitulo)
        val etDescripcion = findViewById<EditText>(R.id.etDescripcion)
        val tvPrioridad = findViewById<TextView>(R.id.tvPrioridad)
        val rgPrioridad = findViewById<RadioGroup>(R.id.rgPrioridad)
        val btnGuardar = findViewById<Button>(R.id.btnGuardar)
        val btnCancelar = findViewById<Button>(R.id.btnCancelar)

        val rolUsuario = intent.getStringExtra("ROL_USUARIO") ?: "Inquilino"
        val casaUsuario = intent.getStringExtra("CASA_USUARIO") ?: "Casa 1"

        if (rolUsuario == "Inquilino") {
            tvPrioridad.visibility = View.GONE
            rgPrioridad.visibility = View.GONE
        } else {
            tvPrioridad.visibility = View.VISIBLE
            rgPrioridad.visibility = View.VISIBLE
        }

        val opcionesCategoria = arrayOf("Fontanero", "Electricista", "Albañil", "Manitas")
        val adapterSpinner = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, opcionesCategoria)
        spinnerCategoria.adapter = adapterSpinner

        btnGuardar.setOnClickListener {
            val titulo = etTitulo.text.toString()
            val descripcion = etDescripcion.text.toString()
            val categoria = spinnerCategoria.selectedItem.toString()

            val prioridad: String
            if (rolUsuario == "Inquilino") {
                prioridad = "Baja"
            } else {
                val selectedPrioridadId = rgPrioridad.checkedRadioButtonId
                prioridad = if (selectedPrioridadId != -1) {
                    findViewById<RadioButton>(selectedPrioridadId).text.toString()
                } else {
                    "Baja"
                }
            }

            if (titulo.isNotEmpty() && descripcion.isNotEmpty()) {
                val fechaActual = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())
                val nuevaId = MainActivity.misIncidencias.size + 1

                val nuevaIncidencia = TicketMock(
                    id = nuevaId,
                    titulo = titulo,
                    descripcion = descripcion,
                    prioridad = prioridad,
                    estado = "Abierta",
                    fecha = fechaActual,
                    categoria = categoria,
                    casa = casaUsuario
                )

                MainActivity.misIncidencias.add(nuevaIncidencia)

                Toast.makeText(this, "Guardada correctamente", Toast.LENGTH_SHORT).show()
                finish()
            } else {
                Toast.makeText(this, "Por favor, rellena el título y la descripción", Toast.LENGTH_SHORT).show()
            }
        }

        btnCancelar.setOnClickListener {
            finish()
        }
    }
}