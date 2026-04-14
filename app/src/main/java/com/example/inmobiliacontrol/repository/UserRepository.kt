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

    suspend fun login(email: String, password: String): User? {
        return userDao.login(email.trim(), password)
    }

    suspend fun getByEmail(email: String): User? {
        return userDao.getByEmail(email.trim())
    }

    suspend fun getAll(): List<User> = userDao.getAll()
}