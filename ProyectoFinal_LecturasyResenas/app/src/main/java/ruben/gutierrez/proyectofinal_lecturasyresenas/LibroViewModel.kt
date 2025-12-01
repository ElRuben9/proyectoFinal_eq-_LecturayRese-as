package ruben.gutierrez.proyectofinal_lecturasyresenas

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import ruben.gutierrez.proyectofinal_lecturasyresenas.model.Libro

class LibroViewModel : ViewModel() {

    private val _libros = MutableLiveData<List<Libro>>()
    val libros: LiveData<List<Libro>> = _libros

    private val firestore = FirebaseFirestore.getInstance()

    init {
        cargarLibros()
    }

    private fun cargarLibros() {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return

        firestore.collection("usuarios")
            .document(userId)
            .collection("libros")
            .addSnapshotListener { snapshot, e ->
                if (e != null || snapshot == null) return@addSnapshotListener

                val listaLibros = snapshot.documents.mapNotNull { doc ->

                    val l = doc.toObject(Libro::class.java) ?: return@mapNotNull null

                    // Creamos un nuevo Libro sin usar copy()
                    Libro(
                        id = doc.id,
                        isbn = l.isbn,
                        titulo = l.titulo,
                        autor = l.autor,
                        categoria = l.categoria,
                        tema = l.tema,
                        genero = l.genero,
                        paginas = l.paginas,
                        sinopsis = l.sinopsis,
                        paginaActual = l.paginaActual,
                        portadaUri = l.portadaUri,
                        rating = l.rating,
                        resumen = l.resumen,
                        userId = l.userId,
                        estadoLectura = l.estadoLectura
                    )
                }

                _libros.value = listaLibros
            }
    }
}
