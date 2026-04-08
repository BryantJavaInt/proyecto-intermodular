package com.example.inmobiliacontrol.repository

import com.example.inmobiliacontrol.dao.UserDao
import com.example.inmobiliacontrol.entity.User

class UserRepository(private val userDao: UserDao) {

    suspend fun register(email: String, password: String, role: String = "TENANT") {
        userDao.upsert(
            User(
                email = email.trim(),
                password = password,
                role = role
            )
        )
    }

    suspend fun login(email: String, password: String): User? {
        return userDao.login(email.trim(), password)
    }

    suspend fun getByEmail(email: String): User? {
        return userDao.getByEmail(email.trim())
    }

    suspend fun getAll(): List<User> = userDao.getAll()
}