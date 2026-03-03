package com.example.inmobiliacontrol

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView

class TicketAdapter(private val ticketList: List<TicketMock>) :
    RecyclerView.Adapter<TicketAdapter.TicketViewHolder>() {

    class TicketViewHolder(view: View) : RecyclerView.ViewHolder(view) {
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

        // Lógica de colores para ESTADO
        when (ticket.estado.uppercase()) {
            "ABIERTA" -> {
                holder.cardEstado.setCardBackgroundColor(ContextCompat.getColor(context, R.color.estado_abierta_bg))
                holder.tvEstado.setTextColor(ContextCompat.getColor(context, R.color.estado_abierta_txt))
            }
            "EN PROCESO", "PROCESO" -> {
                holder.cardEstado.setCardBackgroundColor(ContextCompat.getColor(context, R.color.estado_proceso_bg))
                holder.tvEstado.setTextColor(ContextCompat.getColor(context, R.color.estado_proceso_txt))
            }
            "RESUELTA" -> {
                holder.cardEstado.setCardBackgroundColor(ContextCompat.getColor(context, R.color.estado_resuelta_bg))
                holder.tvEstado.setTextColor(ContextCompat.getColor(context, R.color.estado_resuelta_txt))
            }
        }
    }

    override fun getItemCount(): Int = ticketList.size
}