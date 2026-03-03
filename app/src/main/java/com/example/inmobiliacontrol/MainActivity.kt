package com.example.inmobiliacontrol

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // 1. Creamos nuestra lista de datos inventados (Mock Data)
        val misIncidencias = listOf(
            TicketMock(1, "Fuga en baño", "El agua sale por debajo del lavabo cuando abro el grifo.", "ALTA", "Abierta", "12/03/2025 10:30"),
            TicketMock(2, "Electricidad salón", "El enchufe principal no funciona desde ayer.", "MEDIA", "En proceso", "10/03/2025 15:45"),
            TicketMock(3, "Humedad en techo", "Mancha de humedad creciendo en la esquina del dormitorio.", "ALTA", "Abierta", "09/03/2025 09:15"),
            TicketMock(4, "Puerta atascada", "La puerta del portal roza y cuesta mucho abrirla.", "BAJA", "Resuelta", "01/03/2025 11:20")
        )

        // 2. Buscamos la lista (RecyclerView) en nuestro diseño XML
        val rvTickets = findViewById<RecyclerView>(R.id.rvTickets)

        // 3. Le decimos que los elementos se pongan uno debajo de otro
        rvTickets.layoutManager = LinearLayoutManager(this)

        // 4. Creamos el adaptador pasándole nuestros datos y lo conectamos a la lista
        val adapter = TicketAdapter(misIncidencias)
        rvTickets.adapter = adapter
    }
}