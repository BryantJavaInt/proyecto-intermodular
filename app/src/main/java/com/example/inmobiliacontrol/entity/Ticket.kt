package com.example.inmobiliacontrol.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "tickets",
    foreignKeys = [
        ForeignKey(
            entity = User::class,
            parentColumns = ["id"],
            childColumns = ["createdByUserId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["createdByUserId"])
    ]
)
data class Ticket(
    @PrimaryKey(autoGenerate = true)
    val ticketId: Int = 0,
    val title: String,
    val description: String,
    val category: String,
    val priority: String,
    val status: String,
    val createdAt: Long = System.currentTimeMillis(),
    val createdByUserId: Int
)