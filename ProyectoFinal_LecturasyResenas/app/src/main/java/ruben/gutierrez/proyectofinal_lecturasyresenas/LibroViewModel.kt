package ruben.gutierrez.proyectofinal_lecturasyresenas
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch
import ruben.gutierrez.proyectofinal_lecturasyresenas.data.AppDatabase
import ruben.gutierrez.proyectofinal_lecturasyresenas.data.LibroRepository
import ruben.gutierrez.proyectofinal_lecturasyresenas.model.Libro

class LibroViewModel(application: Application) : AndroidViewModel(application) {

    private val repo: LibroRepository
    private val userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""

    val libros: LiveData<List<Libro>>

    init {
        val dao = AppDatabase.getInstance(application).libroDao()
        repo = LibroRepository(dao)
        libros = repo.obtenerLibros(userId)
    }

    fun agregarLibro(libro: Libro) {
        viewModelScope.launch {
            repo.insertar(libro)
        }
    }
}
