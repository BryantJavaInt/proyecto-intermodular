package com.example.inmobiliacontrol.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.inmobiliacontrol.dao.UserDao
import com.example.inmobiliacontrol.entity.User

@Database(entities = [User::class], version = 1, exportSchema = false)
abstract class InmobiliaDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao

    companion object {
        @Volatile private var INSTANCE: InmobiliaDatabase? = null

        fun getInstance(context: Context): InmobiliaDatabase =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    InmobiliaDatabase::class.java,
                    "inmobilia_db"
                ).build().also { INSTANCE = it }
            }
    }
}