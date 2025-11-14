package ruben.gutierrez.proyectofinal_lecturasyresenas.data

import androidx.lifecycle.LiveData
import ruben.gutierrez.proyectofinal_lecturasyresenas.model.Libro

class LibroRepository(private val dao: LibroDao) {

    fun obtenerLibros(uid: String): LiveData<List<Libro>> {
        return dao.getLibrosByUser(uid)
    }

    suspend fun insertar(libro: Libro) {
        dao.insertarLibro(libro)
    }
}
