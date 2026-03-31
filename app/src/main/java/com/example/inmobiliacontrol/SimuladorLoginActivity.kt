package com.example.inmobiliacontrol

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class SimuladorLoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_simulador_login)

        val btnLoginInquilino = findViewById<Button>(R.id.btnLoginInquilino)
        val btnLoginAgencia = findViewById<Button>(R.id.btnLoginAgencia)
        val btnLoginMantenimiento = findViewById<Button>(R.id.btnLoginMantenimiento)

        // Todos le pasan el rol elegido a la pantalla de Categorías
        btnLoginInquilino.setOnClickListener { entrarConRol("Inquilino") }
        btnLoginAgencia.setOnClickListener { entrarConRol("Agencia") }
        btnLoginMantenimiento.setOnClickListener { entrarConRol("Mantenimiento") }
    }

    private fun entrarConRol(rol: String) {
        val intent = Intent(this, CategoriasActivity::class.java)
        intent.putExtra("ROL_USUARIO", rol)
        // Simulamos que todos son de la Casa 1 para las pruebas
        intent.putExtra("CASA_USUARIO", "Casa 1")
        startActivity(intent)
        finish() // Cerramos el simulador para que no puedan volver atrás con la flecha
    }
}