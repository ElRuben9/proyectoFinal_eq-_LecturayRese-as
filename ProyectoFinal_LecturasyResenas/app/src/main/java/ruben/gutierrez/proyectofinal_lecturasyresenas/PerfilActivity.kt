package ruben.gutierrez.proyectofinal_lecturasyresenas

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.cloudinary.android.MediaManager
import com.cloudinary.android.callback.UploadCallback
import com.cloudinary.android.callback.ErrorInfo
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class PerfilActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase

    private var nuevaImagenUri: Uri? = null

    private val seleccionarImagen =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK && result.data != null) {
                nuevaImagenUri = result.data!!.data
                findViewById<ImageView>(R.id.imgPerfil).setImageURI(nuevaImagenUri)
                subirImagenACloudinary()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_perfil)

        val toolbar = findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar_add_book)
        toolbar.setNavigationOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()

        val tvNombre = findViewById<TextView>(R.id.tvNombre)
        val tvCorreo = findViewById<TextView>(R.id.tvCorreo)
        val imgPerfil = findViewById<ImageView>(R.id.imgPerfil)
        val tvCambiarFoto = findViewById<TextView>(R.id.tvCambiarFoto)

        val userId = auth.currentUser?.uid ?: return
        val userRef = database.reference.child("Usuarios").child(userId)

        userRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                if (snapshot.exists()) {
                    val nombre = snapshot.child("nombre").value.toString()
                    val correo = snapshot.child("correo").value.toString()
                    val fotoUrl = snapshot.child("fotoPerfil").value.toString()

                    tvNombre.text = nombre
                    tvCorreo.text = correo

                    if (fotoUrl.isNotEmpty()) {
                        Glide.with(this@PerfilActivity)
                            .load(fotoUrl)
                            .into(imgPerfil)
                    }

                } else {
                    Toast.makeText(this@PerfilActivity, "No se encontraron datos", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@PerfilActivity, "Error: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })

        tvCambiarFoto.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK).apply { type = "image/*" }
            seleccionarImagen.launch(intent)
        }

        val tvEditarNombre = findViewById<TextView>(R.id.tvEditarNombre)
        tvEditarNombre.setOnClickListener { editarNombre() }
    }

    private fun subirImagenACloudinary() {
        val uri = nuevaImagenUri ?: return

        Toast.makeText(this, "Subiendo foto...", Toast.LENGTH_SHORT).show()

        MediaManager.get().upload(uri)
            .unsigned("perfiles_preset")
            .option("folder", "perfiles/")
            .callback(object : UploadCallback {

                override fun onStart(requestId: String?) {}

                override fun onProgress(requestId: String?, bytes: Long, totalBytes: Long) {}

                override fun onSuccess(requestId: String?, resultData: Map<*, *>?) {
                    val secureUrl = resultData?.get("secure_url")?.toString() ?: return
                    guardarUrlEnFirebase(secureUrl)
                }

                override fun onError(requestId: String?, error: ErrorInfo?) {
                    Toast.makeText(
                        this@PerfilActivity,
                        "Error al subir: ${error?.description}",
                        Toast.LENGTH_LONG
                    ).show()
                }

                override fun onReschedule(requestId: String?, error: ErrorInfo?) {
                    Toast.makeText(
                        this@PerfilActivity,
                        "Reprogramado por Cloudinary: ${error?.description}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            })
            .dispatch()
    }

    private fun guardarUrlEnFirebase(url: String) {
        val userId = auth.currentUser?.uid ?: return
        val userRef = database.reference.child("Usuarios").child(userId)

        userRef.child("fotoPerfil").setValue(url)
            .addOnSuccessListener {
                Toast.makeText(this, "Foto actualizada", Toast.LENGTH_SHORT).show()
            }.addOnFailureListener {
                Toast.makeText(this, "Error guardando URL", Toast.LENGTH_SHORT).show()
            }
    }

    private fun editarNombre() {

        val builder = android.app.AlertDialog.Builder(this)
        builder.setTitle("Editar nombre")

        val input = EditText(this)
        input.hint = "Nuevo nombre"
        input.setPadding(40, 30, 40, 30)

        builder.setView(input)

        builder.setPositiveButton("Guardar") { _, _ ->

            val nuevoNombre = input.text.toString().trim()

            if (nuevoNombre.isEmpty()) {
                Toast.makeText(this, "El nombre no puede estar vacío", Toast.LENGTH_SHORT).show()
                return@setPositiveButton
            }

            val userId = auth.currentUser?.uid ?: return@setPositiveButton
            val userRef = database.reference.child("Usuarios").child(userId)

            userRef.child("nombre").setValue(nuevoNombre)
                .addOnSuccessListener {
                    findViewById<TextView>(R.id.tvNombre).text = nuevoNombre
                    Toast.makeText(this, "Nombre actualizado", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Error al actualizar", Toast.LENGTH_SHORT).show()
                }
        }

        builder.setNegativeButton("Cancelar", null)
        builder.show()
    }
}
