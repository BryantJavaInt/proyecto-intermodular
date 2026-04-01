package com.example.inmobiliacontrol.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.inmobiliacontrol.dao.TicketDao
import com.example.inmobiliacontrol.dao.UserDao
import com.example.inmobiliacontrol.entity.Ticket
import com.example.inmobiliacontrol.entity.User

@Database(
    entities = [User::class, Ticket::class],
    version = 2,
    exportSchema = false
)
abstract class InmobiliaDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao
    abstract fun ticketDao(): TicketDao

    companion object {
        @Volatile
        private var INSTANCE: InmobiliaDatabase? = null

        fun getInstance(context: Context): InmobiliaDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    InmobiliaDatabase::class.java,
                    "inmobilia_db"
                )
                    .fallbackToDestructiveMigration()
                    .build()

                INSTANCE = instance
                instance
            }
        }
    }
}