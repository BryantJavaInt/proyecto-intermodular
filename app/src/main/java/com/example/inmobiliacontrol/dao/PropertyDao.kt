package com.example.inmobiliacontrol.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.inmobiliacontrol.entity.Property

@Dao
interface PropertyDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProperty(property: Property): Long

    @Query("SELECT * FROM properties ORDER BY propertyId ASC")
    suspend fun getAllProperties(): List<Property>

    @Query("SELECT * FROM properties WHERE propertyId = :propertyId LIMIT 1")
    suspend fun getPropertyById(propertyId: Int): Property?
}