package com.example.inmobiliacontrol.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "properties")
data class Property(
    @PrimaryKey(autoGenerate = true)
    val propertyId: Int = 0,
    val address: String,
    val reference: String
)