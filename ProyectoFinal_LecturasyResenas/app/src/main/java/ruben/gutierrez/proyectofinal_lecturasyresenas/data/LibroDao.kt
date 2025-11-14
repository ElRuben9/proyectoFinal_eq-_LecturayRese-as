package ruben.gutierrez.proyectofinal_lecturasyresenas.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import ruben.gutierrez.proyectofinal_lecturasyresenas.model.Libro

@Dao
interface LibroDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarLibro(libro: Libro)

    @Query("SELECT * FROM libros WHERE userId = :uid")
    fun getLibrosByUser(uid: String): androidx.lifecycle.LiveData<List<Libro>>
}
