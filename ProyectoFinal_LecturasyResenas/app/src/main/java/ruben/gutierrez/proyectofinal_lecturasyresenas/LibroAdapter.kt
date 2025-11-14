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
import ruben.gutierrez.proyectofinal_lecturasyresenas.model.Libro

class LibroAdapter :
    ListAdapter<Libro, LibroAdapter.LibroViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LibroViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_libro, parent, false)
        return LibroViewHolder(view)
    }

    override fun onBindViewHolder(holder: LibroViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class LibroViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        private val portada = view.findViewById<ImageView>(R.id.portadaLibro)
        private val titulo = view.findViewById<TextView>(R.id.tituloLibro)
        private val autor = view.findViewById<TextView>(R.id.autorLibro)
        private val rating = view.findViewById<RatingBar>(R.id.ratingBar)
        private val barraProgreso = view.findViewById<ProgressBar>(R.id.progresoLibro)
        private val textoProgreso = view.findViewById<TextView>(R.id.textoProgreso)

        fun bind(libro: Libro) {
            titulo.text = libro.titulo
            autor.text = libro.autor

            // Si en algún momento agregas rating, aquí lo cargas
            rating.rating = 0f

            // Progress = paginas leídas
            val actual = libro.paginaActual ?: 0
            val total = libro.paginas ?: 0

            barraProgreso.max = total
            barraProgreso.progress = actual

            textoProgreso.text = "Página $actual de $total"

            // Imagen de portada (placeholder si no hay URI)
            // portada.setImageResource(R.drawable.portadaLibro)
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<Libro>() {
        override fun areItemsTheSame(oldItem: Libro, newItem: Libro): Boolean =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: Libro, newItem: Libro): Boolean =
            oldItem == newItem
    }
}
