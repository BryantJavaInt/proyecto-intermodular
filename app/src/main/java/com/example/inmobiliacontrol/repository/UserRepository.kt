package com.example.inmobiliacontrol.repository

import com.example.inmobiliacontrol.dao.UserDao
import com.example.inmobiliacontrol.entity.User

class UserRepository(private val userDao: UserDao) {

    suspend fun register(email: String, password: String) {
        userDao.upsert(
            User(
                email = email.trim(),
                password = password
            )
        )
    }

    suspend fun login(email: String, password: String): User? {
        return userDao.login(email.trim(), password)
    }

    suspend fun getAll(): List<User> = userDao.getAll()
}