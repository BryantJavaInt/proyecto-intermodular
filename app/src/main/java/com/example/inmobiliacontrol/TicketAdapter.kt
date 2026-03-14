package com.example.inmobiliacontrol

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView

class TicketAdapter(private var ticketList: List<TicketMock>) :
    RecyclerView.Adapter<TicketAdapter.TicketViewHolder>() {

    class TicketViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        // 1. Enganchamos la tarjeta principal aquí
        val mainCard: CardView = view.findViewById(R.id.mainCard)

        val tvTitulo: TextView = view.findViewById(R.id.tvTitulo)
        val tvFecha: TextView = view.findViewById(R.id.tvFecha)
        val tvDescripcion: TextView = view.findViewById(R.id.tvDescripcion)
        val tvPrioridad: TextView = view.findViewById(R.id.tvPrioridad)
        val tvEstado: TextView = view.findViewById(R.id.tvEstado)

        // Obtenemos los CardViews que envuelven a los textos para cambiar el fondo
        val cardPrioridad: CardView = tvPrioridad.parent as CardView
        val cardEstado: CardView = tvEstado.parent as CardView
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TicketViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_ticket_card, parent, false)
        return TicketViewHolder(view)
    }

    override fun onBindViewHolder(holder: TicketViewHolder, position: Int) {
        val ticket = ticketList[position]
        val context = holder.itemView.context

        holder.tvTitulo.text = ticket.titulo
        holder.tvFecha.text = ticket.fecha
        holder.tvDescripcion.text = ticket.descripcion
        holder.tvPrioridad.text = ticket.prioridad.uppercase()
        holder.tvEstado.text = ticket.estado.uppercase()

        // Lógica de colores para PRIORIDAD
        when (ticket.prioridad.uppercase()) {
            "ALTA" -> {
                holder.cardPrioridad.setCardBackgroundColor(ContextCompat.getColor(context, R.color.prioridad_alta_bg))
                holder.tvPrioridad.setTextColor(ContextCompat.getColor(context, R.color.prioridad_alta_txt))
            }
            "MEDIA" -> {
                holder.cardPrioridad.setCardBackgroundColor(ContextCompat.getColor(context, R.color.prioridad_media_bg))
                holder.tvPrioridad.setTextColor(ContextCompat.getColor(context, R.color.prioridad_media_txt))
            }
            "BAJA" -> {
                holder.cardPrioridad.setCardBackgroundColor(ContextCompat.getColor(context, R.color.prioridad_baja_bg))
                holder.tvPrioridad.setTextColor(ContextCompat.getColor(context, R.color.prioridad_baja_txt))
            }
        }

        // Lógica de colores para ESTADO y fondo general
        when (ticket.estado.uppercase()) {
            "ABIERTA" -> {
                val colorAbierta = ContextCompat.getColor(context, R.color.estado_abierta_bg)
                // 2. Usamos mainCard en lugar de itemView
                holder.mainCard.setCardBackgroundColor(colorAbierta)
                holder.cardEstado.setCardBackgroundColor(colorAbierta)
                holder.tvEstado.setTextColor(ContextCompat.getColor(context, R.color.estado_abierta_txt))
            }
            "EN PROCESO", "PROCESO" -> {
                val colorProceso = ContextCompat.getColor(context, R.color.estado_proceso_bg)
                // 2. Usamos mainCard en lugar de itemView
                holder.mainCard.setCardBackgroundColor(colorProceso)
                holder.cardEstado.setCardBackgroundColor(colorProceso)
                holder.tvEstado.setTextColor(ContextCompat.getColor(context, R.color.estado_proceso_txt))
            }
            "RESUELTA" -> {
                val colorResuelta = ContextCompat.getColor(context, R.color.estado_resuelta_bg)
                // 2. Usamos mainCard en lugar de itemView
                holder.mainCard.setCardBackgroundColor(colorResuelta)
                holder.cardEstado.setCardBackgroundColor(colorResuelta)
                holder.tvEstado.setTextColor(ContextCompat.getColor(context, R.color.estado_resuelta_txt))
            }
        }
    }

    override fun getItemCount(): Int = ticketList.size

    fun actualizarLista(nuevaLista: List<TicketMock>) {
        ticketList = nuevaLista
        notifyDataSetChanged()
    }
}