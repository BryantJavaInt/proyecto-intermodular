package com.example.inmobiliacontrol

import android.content.Intent
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView

class TicketAdapter(private var listaTickets: List<TicketMock>) : RecyclerView.Adapter<TicketAdapter.TicketViewHolder>() {

    class TicketViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val cardContainer: CardView = view.findViewById(R.id.cardContainer)
        val tvTitulo: TextView = view.findViewById(R.id.tvTitulo)
        val tvDescripcion: TextView = view.findViewById(R.id.tvDescripcion)
        val tvFecha: TextView = view.findViewById(R.id.tvFecha)
        val tvPrioridad: TextView = view.findViewById(R.id.tvPrioridad)
        val tvEstado: TextView = view.findViewById(R.id.tvEstado)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TicketViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_ticket_card, parent, false)
        return TicketViewHolder(view)
    }

    override fun onBindViewHolder(holder: TicketViewHolder, position: Int) {
        val ticket = listaTickets[position]

        holder.tvTitulo.text = ticket.titulo
        holder.tvDescripcion.text = ticket.descripcion
        holder.tvFecha.text = ticket.fecha
        holder.tvPrioridad.text = ticket.prioridad.uppercase()
        holder.tvEstado.text = ticket.estado.uppercase()

        // 1. REGLA JEFE: Si está resuelta, usamos vuestro azul corporativo
        if (ticket.estado.uppercase() == "RESUELTA") {
            holder.cardContainer.setCardBackgroundColor(Color.parseColor("#DFEBFA")) // Azul claro de la marca para el fondo
            holder.tvPrioridad.setTextColor(Color.parseColor("#264378")) // Azul oscuro corporativo para las letras
            holder.tvEstado.setTextColor(Color.parseColor("#264378"))
        }
        // 2. Si no está resuelta, aplicamos los colores pastel según su prioridad
        else {
            when (ticket.prioridad.uppercase()) {
                "ALTA" -> {
                    holder.cardContainer.setCardBackgroundColor(Color.parseColor("#FFE0E0")) // Fondo Rosa/Rojo
                    holder.tvPrioridad.setTextColor(Color.parseColor("#E53935")) // Texto Rojo
                    holder.tvEstado.setTextColor(Color.parseColor("#E53935"))
                }
                "MEDIA" -> {
                    holder.cardContainer.setCardBackgroundColor(Color.parseColor("#FFF9C4")) // Fondo Amarillo
                    holder.tvPrioridad.setTextColor(Color.parseColor("#F57C00")) // Texto Naranja
                    holder.tvEstado.setTextColor(Color.parseColor("#F57C00"))
                }
                "BAJA" -> {
                    holder.cardContainer.setCardBackgroundColor(Color.parseColor("#E8F5E9")) // Fondo Verde
                    holder.tvPrioridad.setTextColor(Color.parseColor("#388E3C")) // Texto Verde
                    holder.tvEstado.setTextColor(Color.parseColor("#388E3C"))
                }
                else -> {
                    holder.cardContainer.setCardBackgroundColor(Color.WHITE)
                    holder.tvPrioridad.setTextColor(Color.GRAY)
                    holder.tvEstado.setTextColor(Color.GRAY)
                }
            }
        }

        // Acción al hacer clic en la tarjeta
        holder.itemView.setOnClickListener {
            val context = holder.itemView.context
            val intent = Intent(context, DetalleIncidenciaActivity::class.java)
            intent.putExtra("TICKET_ID", ticket.id)
            if (context is MainActivity) {
                val rol = context.intent.getStringExtra("ROL_USUARIO") ?: "Mantenimiento"
                intent.putExtra("ROL_USUARIO", rol)
            }
            context.startActivity(intent)
        }
    }

    override fun getItemCount() = listaTickets.size

    fun actualizarLista(nuevaLista: List<TicketMock>) {
        listaTickets = nuevaLista
        notifyDataSetChanged()
    }
}