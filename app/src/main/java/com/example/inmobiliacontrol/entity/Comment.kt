package com.example.inmobiliacontrol.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "comments",
    foreignKeys = [
        ForeignKey(
            entity = Ticket::class,
            parentColumns = ["ticketId"],
            childColumns = ["ticketId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = User::class,
            parentColumns = ["id"],
            childColumns = ["authorUserId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["ticketId"]),
        Index(value = ["authorUserId"])
    ]
)
data class Comment(
    @PrimaryKey(autoGenerate = true)
    val commentId: Int = 0,
    val message: String,
    val createdAt: Long = System.currentTimeMillis(),
    val ticketId: Int,
    val authorUserId: Int
)