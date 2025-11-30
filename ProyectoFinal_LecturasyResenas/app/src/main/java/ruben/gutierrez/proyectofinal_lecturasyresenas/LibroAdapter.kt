package ruben.gutierrez.proyectofinal_lecturasyresenas

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.RatingBar
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import ruben.gutierrez.proyectofinal_lecturasyresenas.model.Libro

class LibroAdapter(
    private val onClick: (Libro) -> Unit
) : ListAdapter<Libro, LibroAdapter.LibroViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LibroViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_libro, parent, false)
        return LibroViewHolder(view, onClick)
    }

    override fun onBindViewHolder(holder: LibroViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class LibroViewHolder(itemView: View, val onClick: (Libro) -> Unit)
        : RecyclerView.ViewHolder(itemView) {

        private val portada: ImageView = itemView.findViewById(R.id.portadaLibro)
        private val titulo: TextView = itemView.findViewById(R.id.tituloLibro)
        private val autor: TextView = itemView.findViewById(R.id.autorLibro)
        private val rating: RatingBar = itemView.findViewById(R.id.ratingBar)
        private val progreso: ProgressBar = itemView.findViewById(R.id.progresoLibro)
        private val textoProgreso: TextView = itemView.findViewById(R.id.textoProgreso)

        fun bind(libro: Libro) {

            titulo.text = libro.titulo
            autor.text = libro.autor

            //para que muestre el rating realll
            rating.rating = libro.rating ?: 0f

            //para que muestre la portada si es que tiene
            if (!libro.portadaUri.isNullOrEmpty()) {
                Glide.with(itemView.context)
                    .load(libro.portadaUri)
                    .into(portada)
            }

            //para el progreso de la lectura
            val paginasTotales = libro.paginas ?: 1
            val paginaActual = when {
                libro.paginaActual != null -> libro.paginaActual!!
                libro.estadoLectura == "Terminado" -> paginasTotales //  si esta terminado va mostrar completo
                else -> 0
            }

                progreso.max = paginasTotales
            progreso.progress = paginaActual
            textoProgreso.text = "Página $paginaActual de $paginasTotales"

            itemView.setOnClickListener { onClick(libro) }
        }
    }
}

    class DiffCallback : DiffUtil.ItemCallback<Libro>() {
        override fun areItemsTheSame(oldItem: Libro, newItem: Libro): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Libro, newItem: Libro): Boolean {
            return oldItem == newItem
        }
    }

