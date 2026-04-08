package com.example.inmobiliacontrol.repository

import com.example.inmobiliacontrol.dao.PropertyDao
import com.example.inmobiliacontrol.entity.Property

class PropertyRepository(private val propertyDao: PropertyDao) {

    suspend fun createProperty(address: String, reference: String): Long {
        val property = Property(
            address = address.trim(),
            reference = reference.trim()
        )
        return propertyDao.insertProperty(property)
    }

    suspend fun getAllProperties(): List<Property> {
        return propertyDao.getAllProperties()
    }

    suspend fun getPropertyById(propertyId: Int): Property? {
        return propertyDao.getPropertyById(propertyId)
    }
}