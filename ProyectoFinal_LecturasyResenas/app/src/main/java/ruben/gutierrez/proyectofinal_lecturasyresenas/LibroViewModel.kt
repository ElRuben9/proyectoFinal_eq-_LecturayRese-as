package ruben.gutierrez.proyectofinal_lecturasyresenas

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import ruben.gutierrez.proyectofinal_lecturasyresenas.model.Libro

class LibroViewModel(application: Application) : AndroidViewModel(application) {

    private val firestore = FirebaseFirestore.getInstance()
    private val userId = FirebaseAuth.getInstance().currentUser?.uid.orEmpty()

    private val _libros = MutableLiveData<List<Libro>>()
    val libros: LiveData<List<Libro>> get() = _libros

    init {
        cargarLibros()
    }

    private fun cargarLibros() {
        if (userId.isEmpty()) return

        firestore.collection("usuarios")
            .document(userId)
            .collection("libros")
            .addSnapshotListener { snapshot, _ ->
                if (snapshot != null) {
                    val lista = snapshot.documents.mapNotNull { doc ->
                        val libro = doc.toObject(Libro::class.java)
                        libro?.copy(id = doc.id)
                    }
                    _libros.value = lista
                }
            }
    }

    fun agregarLibro(libro: Libro) {
        if (userId.isEmpty()) return

        val data = libro.copy(userId = userId)

        firestore.collection("usuarios")
            .document(userId)
            .collection("libros")
            .add(data)
    }
}
