package ruben.gutierrez.proyectofinal_lecturasyresenas

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.play.core.integrity.bs
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth

class CambiarPassword : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cambiar_password)

        auth = FirebaseAuth.getInstance()

        val edtActual = findViewById<EditText>(R.id.edtContrasenaActual)
        val edtNueva = findViewById<EditText>(R.id.edtNuevaContrasena)
        val edtConfirmar = findViewById<EditText>(R.id.edtConfirmarNuevaContrasena)
        val btnAceptar = findViewById<Button>(R.id.btnAceptar)

        btnAceptar.setOnClickListener {

            val actual = edtActual.text.toString().trim()
            val nueva = edtNueva.text.toString().trim()
            val confirmar = edtConfirmar.text.toString().trim()

            if (actual.isEmpty() || nueva.isEmpty() || confirmar.isEmpty()) {
                Toast.makeText(this, "Completa todos los campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (nueva != confirmar) {
                Toast.makeText(this, "Las contraseñas nuevas no coinciden", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (nueva.length < 6) {
                Toast.makeText(this, "La contraseña debe tener al menos 6 caracteres", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val user = auth.currentUser
            if (user == null) {
                Toast.makeText(this, "Error: usuario no autenticado", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val credential = EmailAuthProvider.getCredential(user.email!!, actual)

            user.reauthenticate(credential).addOnCompleteListener { rea ->
                if (rea.isSuccessful) {

                    user.updatePassword(nueva).addOnCompleteListener { upd ->
                        if (upd.isSuccessful) {
                            Toast.makeText(this, "Contraseña actualizada", Toast.LENGTH_SHORT).show()
                            startActivity(Intent(this, PerfilActivity::class.java))
                            finish()
                        } else {
                            Toast.makeText(this, "Error al actualizar: ${upd.exception?.message}", Toast.LENGTH_SHORT).show()
                        }
                    }

                } else {
                    Toast.makeText(this, "La contraseña actual es incorrecta", Toast.LENGTH_SHORT).show()
                }
            }
        }
        findViewById<Button>(R.id.btnCancelar).setOnClickListener {
            startActivity(Intent(this, PerfilActivity::class.java))
        }
    }
}
