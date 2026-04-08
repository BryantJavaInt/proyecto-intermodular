package com.example.inmobiliacontrol.repository

import com.example.inmobiliacontrol.dao.CommentDao
import com.example.inmobiliacontrol.entity.Comment

class CommentRepository(private val commentDao: CommentDao) {

    suspend fun createComment(message: String, ticketId: Int, authorUserId: Int): Long {
        val comment = Comment(
            message = message.trim(),
            ticketId = ticketId,
            authorUserId = authorUserId
        )
        return commentDao.insertComment(comment)
    }

    suspend fun getCommentsByTicket(ticketId: Int): List<Comment> {
        return commentDao.getCommentsByTicket(ticketId)
    }

    suspend fun getAllComments(): List<Comment> {
        return commentDao.getAllComments()
    }

    suspend fun deleteComment(commentId: Int) {
        commentDao.deleteComment(commentId)
    }
}