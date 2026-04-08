package com.example.inmobiliacontrol.repository

import com.example.inmobiliacontrol.dao.TicketDao
import com.example.inmobiliacontrol.entity.Ticket

class TicketRepository(private val ticketDao: TicketDao) {

    suspend fun createTicket(
        title: String,
        description: String,
        category: String,
        priority: String,
        createdByUserId: Int,
        propertyId: Int? = null
    ): Long {
        val ticket = Ticket(
            title = title.trim(),
            description = description.trim(),
            category = category.trim(),
            priority = priority.trim(),
            status = "Abierto",
            createdByUserId = createdByUserId,
            propertyId = propertyId
        )
        return ticketDao.insertTicket(ticket)
    }

    suspend fun getAllTickets(): List<Ticket> {
        return ticketDao.getAllTickets()
    }

    suspend fun getTicketsByUser(userId: Int): List<Ticket> {
        return ticketDao.getTicketsByUser(userId)
    }

    suspend fun getTicketsByStatus(status: String): List<Ticket> {
        return ticketDao.getTicketsByStatus(status)
    }

    suspend fun getTicketById(ticketId: Int): Ticket? {
        return ticketDao.getTicketById(ticketId)
    }

    suspend fun updateTicketStatus(ticketId: Int, newStatus: String) {
        ticketDao.updateTicketStatus(ticketId, newStatus)
    }

    suspend fun deleteTicket(ticketId: Int) {
        ticketDao.deleteTicket(ticketId)
    }
}