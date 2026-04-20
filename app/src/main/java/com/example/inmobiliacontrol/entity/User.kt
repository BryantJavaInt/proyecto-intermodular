package com.example.inmobiliacontrol.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "users",
    indices = [Index(value = ["email"], unique = true)]
)
data class User(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val email: String,
    val password: String,
    val role: String = "TENANT",
    val isActive: Boolean = true,
    // Datos de perfil
    val nombre: String = "",
    val apellidos: String = "",
    val telefono: String = "",
    val dni: String = "",
    // Solo TENANT
    val propertyAddress: String = "",
    // Solo AGENCY
    val agenciaNombre: String = "",
    val agenciaCif: String = "",
    // Solo MAINTENANCE
    val especialidad: String = ""
)
