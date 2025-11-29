package ruben.gutierrez.proyectofinal_lecturasyresenas.model

data class Libro(
    val id: String? = null,
    val isbn: String? = null,
    val titulo: String = "",
    val autor: String = "",
    val categoria: String = "",
    val tema: String? = null,
    val genero: String? = null,
    val paginas: Int? = null,
    val sinopsis: String? = null,
    val paginaActual: Int? = null,
    val portadaUri: String? = null,
    val editorial: String? = null,
    val anio: Int? = null,
    val rating: Float? = null,
    val resumen: String? = null,
    val userId: String = ""
)
