package ruben.gutierrez.proyectofinal_lecturasyresenas

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
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
                portadaUri = null,
                userId = uid
            )

            // Si hay imagen, primero se sube a Storage
            if (portadaUri != null) {
                subirImagenYGuardarLibro(libro)
            } else {
                guardarLibroEnFirestore(libro)
            }
        }

        btnCancelar.setOnClickListener { finish() }
    }

    private fun subirImagenYGuardarLibro(libro: Libro) {
        val ref = FirebaseStorage.getInstance().reference
            .child("portadas/${System.currentTimeMillis()}_${libro.userId}.jpg")

        ref.putFile(portadaUri!!)
            .continueWithTask { task ->
                if (!task.isSuccessful) throw task.exception ?: Exception("Error subiendo imagen")
                ref.downloadUrl
            }
            .addOnSuccessListener { downloadUrl ->
                val libroConImagen = libro.copy(portadaUri = downloadUrl.toString())
                guardarLibroEnFirestore(libroConImagen)
            }
            .addOnFailureListener {
                Toast.makeText(this, "Error subiendo la imagen", Toast.LENGTH_SHORT).show()
            }
    }

    private fun guardarLibroEnFirestore(libro: Libro) {
        val firestore = FirebaseFirestore.getInstance()
        val userId = FirebaseAuth.getInstance().currentUser!!.uid

        firestore.collection("usuarios")
            .document(userId)
            .collection("libros")
            .add(libro) // Firestore generará un ID automáticamente
            .addOnSuccessListener {
                Toast.makeText(this, "Libro guardado correctamente", Toast.LENGTH_SHORT).show()
                finish()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Error guardando el libro", Toast.LENGTH_SHORT).show()
            }
    }

}
