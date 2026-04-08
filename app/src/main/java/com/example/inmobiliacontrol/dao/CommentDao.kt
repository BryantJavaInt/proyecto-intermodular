package com.example.inmobiliacontrol.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.inmobiliacontrol.entity.Comment

@Dao
interface CommentDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertComment(comment: Comment): Long

    @Query("SELECT * FROM comments WHERE ticketId = :ticketId ORDER BY createdAt ASC")
    suspend fun getCommentsByTicket(ticketId: Int): List<Comment>

    @Query("SELECT * FROM comments ORDER BY createdAt DESC")
    suspend fun getAllComments(): List<Comment>

    @Query("DELETE FROM comments WHERE commentId = :commentId")
    suspend fun deleteComment(commentId: Int)
}