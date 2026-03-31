package com.example.inmobiliacontrol

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton

class MainActivity : AppCompatActivity() {

    companion object {
        val misIncidencias = mutableListOf(
            TicketMock(1, "Fuga en baño", "El agua sale por debajo del lavabo...", "Alta", "Abierta", "12/03/2026", "Fontanero", "Casa 1"),
            TicketMock(2, "Electricidad salon", "El enchufe principal no funciona...", "Media", "En proceso", "10/03/2026", "Electricista", "Casa 2"),
            TicketMock(3, "Humedad en techo", "Mancha de humedad en la esquina...", "Alta", "Abierta", "09/03/2026", "Albañil", "Casa 1"),
            TicketMock(4, "Puerta atascada", "La puerta del portal roza...", "Baja", "Resuelta", "01/03/2026", "Cerrajero", "Casa 3"),
            TicketMock(5, "Grifo gotea", "El grifo de la cocina no cierra bien", "Baja", "Abierta", "15/03/2026", "Fontanero", "Casa 2")
        )
    }

    private var listaFiltradaPorCategoria: List<TicketMock> = listOf()
    private lateinit var adapter: TicketAdapter
    private var categoriaRecibida = "Todas"
    private var rolUsuario = "Inquilino"
    private var casaUsuario = "Casa 1"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        categoriaRecibida = intent.getStringExtra("FILTRO_CATEGORIA") ?: "Todas"
        rolUsuario = intent.getStringExtra("ROL_USUARIO") ?: "Inquilino"
        casaUsuario = intent.getStringExtra("CASA_USUARIO") ?: "Casa 1"

        val rvTickets = findViewById<RecyclerView>(R.id.rvTickets)
        rvTickets.layoutManager = LinearLayoutManager(this)
        adapter = TicketAdapter(listaFiltradaPorCategoria)
        rvTickets.adapter = adapter

        val spinnerFiltro = findViewById<Spinner>(R.id.spinnerFiltro)
        val opcionesFiltro = arrayOf("Todas", "Abierta", "En proceso", "Resuelta")
        val spinnerAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, opcionesFiltro)
        spinnerFiltro.adapter = spinnerAdapter

        spinnerFiltro.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                actualizarListaEnPantalla()
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        val btnNuevaIncidencia = findViewById<FloatingActionButton>(R.id.btnNuevaIncidencia)

        // ocultamos el boton si eres mantenimiento
        if (rolUsuario == "Mantenimiento") {
            btnNuevaIncidencia.visibility = View.GONE
        } else {
            btnNuevaIncidencia.visibility = View.VISIBLE
            btnNuevaIncidencia.setOnClickListener {
                val intent = Intent(this, CrearIncidenciaActivity::class.java)
                intent.putExtra("ROL_USUARIO", rolUsuario)
                intent.putExtra("CASA_USUARIO", casaUsuario)
                startActivity(intent)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        actualizarListaEnPantalla()
    }

    private fun actualizarListaEnPantalla() {
        listaFiltradaPorCategoria = if (rolUsuario == "Inquilino") {
            misIncidencias.filter { it.casa == casaUsuario }
        } else {
            if (categoriaRecibida == "Todas") {
                misIncidencias
            } else {
                misIncidencias.filter { it.categoria == categoriaRecibida }
            }
        }

        val spinnerFiltro = findViewById<Spinner>(R.id.spinnerFiltro)
        val estadoSeleccionado = spinnerFiltro.selectedItem?.toString() ?: "Todas"

        val listaDobleFiltro = if (estadoSeleccionado == "Todas") {
            listaFiltradaPorCategoria
        } else {
            listaFiltradaPorCategoria.filter { it.estado.uppercase() == estadoSeleccionado.uppercase() }
        }

        adapter.actualizarLista(listaDobleFiltro)
    }
}