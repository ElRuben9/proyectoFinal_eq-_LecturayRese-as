package ruben.gutierrez.proyectofinal_lecturasyresenas.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "libros")
data class Libro(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val isbn: String?,
    val titulo: String,
    val autor: String,
    val categoria: String,
    val tema: String?,
    val genero: String?,
    val paginas: Int?,
    val sinopsis: String?,
    val paginaActual: Int?,
    val portadaUri: String?,
    val userId: String
)