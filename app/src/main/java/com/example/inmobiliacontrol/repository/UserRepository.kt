package com.example.inmobiliacontrol.repository

import com.example.inmobiliacontrol.dao.UserDao
import com.example.inmobiliacontrol.entity.User

class UserRepository(private val userDao: UserDao) {

    suspend fun register(email: String, password: String, role: String): Long {
        val user = User(
            email = email.trim(),
            password = password,
            role = role
        )
        userDao.upsert(user)
        return userDao.getByEmail(email.trim())?.id?.toLong() ?: -1L
    }

    suspend fun registerCompleto(
        email: String,
        password: String,
        role: String,
        nombre: String,
        apellidos: String,
        telefono: String,
        dni: String,
        propertyAddress: String = "",
        agenciaNombre: String = "",
        agenciaCif: String = "",
        especialidad: String = ""
    ): Long {
        // Verificar que el email no existe ya
        val existing = userDao.existsByEmail(email.trim())
        if (existing != null) return -2L  // -2 = email ya existe

        val user = User(
            email = email.trim(),
            password = password,
            role = role,
            nombre = nombre.trim(),
            apellidos = apellidos.trim(),
            telefono = telefono.trim(),
            dni = dni.trim(),
            propertyAddress = propertyAddress.trim(),
            agenciaNombre = agenciaNombre.trim(),
            agenciaCif = agenciaCif.trim(),
            especialidad = especialidad.trim()
        )
        userDao.upsert(user)
        return userDao.getByEmail(email.trim())?.id?.toLong() ?: -1L
    }

    suspend fun login(email: String, password: String): User? {
        return userDao.login(email.trim(), password)
    }

    suspend fun getByEmail(email: String): User? {
        return userDao.getByEmail(email.trim())
    }

    suspend fun getById(id: Int): User? {
        return userDao.getById(id)
    }

    suspend fun getAll(): List<User> = userDao.getAll()
}
