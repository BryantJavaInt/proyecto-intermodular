package com.example.inmobiliacontrol.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.inmobiliacontrol.entity.Ticket

@Dao
interface TicketDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTicket(ticket: Ticket): Long

    @Query("SELECT * FROM tickets ORDER BY createdAt DESC")
    suspend fun getAllTickets(): List<Ticket>

    @Query("SELECT * FROM tickets WHERE createdByUserId = :userId ORDER BY createdAt DESC")
    suspend fun getTicketsByUser(userId: Int): List<Ticket>

    @Query("SELECT * FROM tickets WHERE status = :status ORDER BY createdAt DESC")
    suspend fun getTicketsByStatus(status: String): List<Ticket>

    @Query("UPDATE tickets SET status = :newStatus WHERE ticketId = :ticketId")
    suspend fun updateTicketStatus(ticketId: Int, newStatus: String)

    @Query("SELECT * FROM tickets WHERE ticketId = :ticketId LIMIT 1")
    suspend fun getTicketById(ticketId: Int): Ticket?
}