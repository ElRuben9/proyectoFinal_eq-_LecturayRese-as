package ruben.gutierrez.proyectofinal_lecturasyresenas

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.cloudinary.android.MediaManager
import com.cloudinary.android.callback.ErrorInfo
import com.cloudinary.android.callback.UploadCallback
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import ruben.gutierrez.proyectofinal_lecturasyresenas.model.Libro

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

        btnSeleccionarImg.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK).apply { type = "image/*" }
            seleccionarImagen.launch(intent)
        }

        val toolbar = findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar_add_book)
        toolbar.setNavigationOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }

        btnGuardar.setOnClickListener {

            if (titulo.text.isBlank() || autor.text.isBlank()) {
                Toast.makeText(this, "Título y autor son obligatorios", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val libro = Libro(
                isbn = isbn.text.toString().ifBlank { null },
                titulo = titulo.text.toString(),
                autor = autor.text.toString(),
                categoria = categoria.selectedItem.toString(),
                tema = tema.selectedItem?.toString(),
                genero = genero.selectedItem?.toString(),
                paginas = paginas.text.toString().toIntOrNull(),
                sinopsis = sinopsis.text.toString(),
                paginaActual = paginaActual.text.toString().toIntOrNull(),
                portadaUri = null, // Se pondrá después
                userId = uid
            )

            if (portadaUri != null) {
                subirImagenACloudinary(libro)
            } else {
                guardarLibroEnFirestore(libro)
            }
        }

        btnCancelar.setOnClickListener { finish() }
    }

    private fun subirImagenACloudinary(libro: Libro) {

        val uri = portadaUri ?: return

        Toast.makeText(this, "Subiendo imagen...", Toast.LENGTH_SHORT).show()

        MediaManager.get().upload(uri)
            .unsigned("libros_preset")   //
            .option("folder", "libros/") //
            .callback(object : UploadCallback {
                override fun onStart(requestId: String?) {
                    // mostrar progress bar
                }

                override fun onProgress(requestId: String?, bytes: Long, totalBytes: Long) {
                    // actualizar progreso
                }

                override fun onSuccess(requestId: String?, resultData: Map<*, *>) {
                    val url = resultData["secure_url"] as? String
                    if (url != null) {
                        val libroConImagen = libro.copy(portadaUri = url)
                        guardarLibroEnFirestore(libroConImagen)
                    }
                }

                override fun onError(requestId: String?, error: ErrorInfo?) {
                    Toast.makeText(
                        this@AgregarLibroActivity,
                        "Error subiendo imagen: ${error?.description}",
                        Toast.LENGTH_SHORT
                    ).show()
                }

                override fun onReschedule(requestId: String?, error: ErrorInfo?) {
                    Toast.makeText(
                        this@AgregarLibroActivity,
                        "Error temporal, se reintentará: ${error?.description}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
            .dispatch()
    }

    private fun guardarLibroEnFirestore(libro: Libro) {
        val firestore = FirebaseFirestore.getInstance()
        val userId = FirebaseAuth.getInstance().currentUser!!.uid
        val ref = firestore.collection("usuarios")
            .document(userId)
            .collection("libros")
            .document() // genera id antes de guardar

        val libroConId = libro.copy(id = ref.id)

        ref.set(libroConId)
            .addOnSuccessListener {
                Toast.makeText(this, "Libro guardado correctamente", Toast.LENGTH_SHORT).show()
                finish()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Error guardando el libro", Toast.LENGTH_SHORT).show()
            }

    }
}
