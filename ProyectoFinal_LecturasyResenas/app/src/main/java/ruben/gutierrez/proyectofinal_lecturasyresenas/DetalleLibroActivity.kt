package ruben.gutierrez.proyectofinal_lecturasyresenas

import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.picasso.Picasso
import ruben.gutierrez.proyectofinal_lecturasyresenas.model.Libro


class DetalleLibroActivity : AppCompatActivity() {

    private lateinit var layoutProgreso: LinearLayout
    private lateinit var layoutResumen: LinearLayout

    // Views
    private lateinit var imgPortada: ImageView
    private lateinit var tvTitulo: TextView
    private lateinit var tvAutor: TextView
    private lateinit var tvCategoria: TextView
    private lateinit var tvGenero: TextView
    private lateinit var tvSinopsis: TextView
    private lateinit var tvEditorial: TextView
    private lateinit var tvAno: TextView
    private lateinit var tvIsbn: TextView
    private lateinit var tvResumen: TextView

    private lateinit var ratingBar: RatingBar
    private lateinit var tvValorRating: TextView

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detalle_libro)

        // Inicializar views
        imgPortada = findViewById(R.id.imgPortada)
        tvTitulo = findViewById(R.id.tvTituloLibro)
        tvAutor = findViewById(R.id.tvAutorLibro)
        tvCategoria = findViewById(R.id.tvCategoria)
        tvGenero = findViewById(R.id.tvGenero)
        tvSinopsis = findViewById(R.id.tvSinopsis)
        tvEditorial = findViewById(R.id.tvEditorial)
        tvAno = findViewById(R.id.tvAno)
        tvIsbn = findViewById(R.id.tvIsbn)
        tvResumen = findViewById(R.id.tvResumen)

        layoutProgreso = findViewById(R.id.layoutProgreso)
        layoutResumen = findViewById(R.id.layoutResumen)

        ratingBar = findViewById(R.id.ratingBar)
        tvValorRating = findViewById(R.id.tvValorRating)

        // Obtener ID del libro
        val idLibro = intent.getStringExtra("idLibro")
        if (idLibro != null) cargarLibroDesdeFirestore(idLibro)

        ratingBar.setOnRatingBarChangeListener { _, rating, _ ->
            tvValorRating.text = rating.toString()
        }
    }

    private fun cargarLibroDesdeFirestore(idLibro: String) {
        val user = FirebaseAuth.getInstance().currentUser ?: return
        val userId = user.uid

        FirebaseFirestore.getInstance()
            .collection("usuarios")
            .document(userId)
            .collection("libros")
            .document(idLibro)
            .get()
            .addOnSuccessListener { doc ->
                val libro = doc.toObject(Libro::class.java)
                if (libro != null) {
                    mostrarDatosDelLibro(libro)
                    actualizarVistaSegunEstado(libro)
                }
            }
    }

    private fun mostrarDatosDelLibro(libro: Libro) {

        tvTitulo.text = libro.titulo
        tvAutor.text = libro.autor
        tvCategoria.text = libro.categoria
        tvGenero.text = libro.genero ?: "Sin género"
        tvSinopsis.text = libro.sinopsis ?: "Sin sinopsis"
        tvIsbn.text = libro.isbn ?: "Sin ISBN"
        tvEditorial.text = "Editorial: ${libro.editorial ?: "Desconocida"}"
        tvAno.text = "Año: ${libro.anio ?: "N/A"}"
        tvResumen.text = libro.resumen ?: "Sin resumen"

        // Si tiene portada
        if (!libro.portadaUri.isNullOrEmpty()) {
            Picasso.get().load(libro.portadaUri).into(imgPortada)
        }

        // Rating
        ratingBar.rating = libro.rating ?: 0f
        tvValorRating.text = (libro.rating ?: 0f).toString()
    }

    private fun actualizarVistaSegunEstado(libro: Libro) {
        val paginaActual = libro.paginaActual ?: 0
        val paginasTotales = libro.paginas ?: 0

        val terminado = paginasTotales > 0 && paginaActual >= paginasTotales

        if (terminado) {
            layoutProgreso.visibility = View.GONE
            layoutResumen.visibility = View.VISIBLE
        } else {
            layoutProgreso.visibility = View.VISIBLE
            layoutResumen.visibility = View.GONE
        }
    }
}
