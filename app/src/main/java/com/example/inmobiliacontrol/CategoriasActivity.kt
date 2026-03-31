package com.example.inmobiliacontrol

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class CategoriasActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Ahora recibimos el rol del menú de prueba (en vez de escribirlo a mano)
        val rolUsuario = intent.getStringExtra("ROL_USUARIO") ?: "Inquilino"
        val casaInquilino = intent.getStringExtra("CASA_USUARIO") ?: "Casa 1"

        if (rolUsuario == "Inquilino") {
            val intent = Intent(this, MainActivity::class.java)
            intent.putExtra("ROL_USUARIO", rolUsuario)
            intent.putExtra("CASA_USUARIO", casaInquilino)
            startActivity(intent)
            finish()
            return
        }

        setContentView(R.layout.activity_categorias)

        val btnFontanero = findViewById<Button>(R.id.btnFontanero)
        val btnElectricista = findViewById<Button>(R.id.btnElectricista)
        val btnAlbanil = findViewById<Button>(R.id.btnAlbanil)
        val btnManitas = findViewById<Button>(R.id.btnManitas)
        val btnTodas = findViewById<Button>(R.id.btnTodas)

        btnFontanero.setOnClickListener { abrirLista("Fontanero", rolUsuario) }
        btnElectricista.setOnClickListener { abrirLista("Electricista", rolUsuario) }
        btnAlbanil.setOnClickListener { abrirLista("Albañil", rolUsuario) }
        btnManitas.setOnClickListener { abrirLista("Manitas", rolUsuario) }
        btnTodas.setOnClickListener { abrirLista("Todas", rolUsuario) }
    }

    private fun abrirLista(categoriaSeleccionada: String, rol: String) {
        val intent = Intent(this, MainActivity::class.java)
        intent.putExtra("FILTRO_CATEGORIA", categoriaSeleccionada)
        intent.putExtra("ROL_USUARIO", rol)
        startActivity(intent)
    }
}