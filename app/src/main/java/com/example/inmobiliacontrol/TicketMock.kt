package com.example.inmobiliacontrol

data class TicketMock(
    val id: Int,
    val titulo: String,
    val descripcion: String,
    var prioridad: String,
    var estado: String,
    val fecha: String,
    val categoria: String,
    val casa: String
)