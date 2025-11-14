package ruben.gutierrez.proyectofinal_lecturasyresenas

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.firebase.auth.FirebaseAuth
import ruben.gutierrez.proyectofinal_lecturasyresenas.data.AppDatabase
import ruben.gutierrez.proyectofinal_lecturasyresenas.model.Libro
import kotlinx.coroutines.launch

class AgregarLibroActivity : AppCompatActivity() {

    private var portadaUri: Uri? = null

    private val seleccionarImagen =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK && result.data != null) {
                portadaUri = result.data!!.data
                findViewById<ImageView>(R.id.imgPortadaPreview).setImageURI(portadaUri)
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_book)

        val db = AppDatabase.getInstance(this)
        val dao = db.libroDao()

        val isbn = findViewById<EditText>(R.id.edtISBN)
        val titulo = findViewById<EditText>(R.id.edtTitulo)
        val autor = findViewById<EditText>(R.id.edtAutor)
        val categoria = findViewById<Spinner>(R.id.spnCategoria)
        val tema = findViewById<Spinner>(R.id.spnTema)
        val genero = findViewById<Spinner>(R.id.spnGenero)
        val paginas = findViewById<EditText>(R.id.edtNumeroPaginas)
        val sinopsis = findViewById<EditText>(R.id.edtSinopsis)
        val paginaActual = findViewById<EditText>(R.id.edtPaginaActual)
        val btnSeleccionarImg = findViewById<Button>(R.id.btnSeleccionarImagen)
        val btnGuardar = findViewById<Button>(R.id.btnGuardarLibro)
        val btnCancelar = findViewById<Button>(R.id.btnCancelar)
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return

        // --- Seleccionar imagen ---
        btnSeleccionarImg.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK).apply {
                type = "image/*"
            }
            seleccionarImagen.launch(intent)
        }

        // --- Botón Guardar ---
        btnGuardar.setOnClickListener {

            if (titulo.text.isBlank() || autor.text.isBlank()) {
                Toast.makeText(this, "Título y autor son obligatorios", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val nuevoLibro = Libro(
                isbn = isbn.text.toString().ifBlank { null },
                titulo = titulo.text.toString(),
                autor = autor.text.toString(),
                categoria = categoria.selectedItem.toString(),
                tema = tema.selectedItem?.toString(),
                genero = genero.selectedItem?.toString(),
                paginas = paginas.text.toString().toIntOrNull(),
                sinopsis = sinopsis.text.toString(),
                paginaActual = paginaActual.text.toString().toIntOrNull(),
                portadaUri = portadaUri?.toString(),
                userId = uid
            )

            lifecycleScope.launch {
                dao.insertarLibro(nuevoLibro)
                Toast.makeText(this@AgregarLibroActivity, "Libro guardado", Toast.LENGTH_SHORT).show()
                finish()
            }
        }

        // --- Botón Cancelar ---
        btnCancelar.setOnClickListener {
            finish()
        }
    }
}