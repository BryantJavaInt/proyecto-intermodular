package com.example.inmobiliacontrol.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.inmobiliacontrol.dao.CommentDao
import com.example.inmobiliacontrol.dao.PropertyDao
import com.example.inmobiliacontrol.dao.TicketDao
import com.example.inmobiliacontrol.dao.UserDao
import com.example.inmobiliacontrol.entity.Comment
import com.example.inmobiliacontrol.entity.Property
import com.example.inmobiliacontrol.entity.Ticket
import com.example.inmobiliacontrol.entity.User

@Database(
    entities = [User::class, Property::class, Ticket::class, Comment::class],
    version = 5,
    exportSchema = false
)
abstract class InmobiliaDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao
    abstract fun propertyDao(): PropertyDao
    abstract fun ticketDao(): TicketDao
    abstract fun commentDao(): CommentDao

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
