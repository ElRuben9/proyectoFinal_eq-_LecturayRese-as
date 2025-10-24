package ruben.gutierrez.proyectofinal_lecturasyresenas

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

class RegisterActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        auth = FirebaseAuth.getInstance()

        val nombre = findViewById<EditText>(R.id.edtNombre)
        val correo = findViewById<EditText>(R.id.edtCorreoRegistro)
        val pass = findViewById<EditText>(R.id.edtContrasenaRegistro)
        val pass2 = findViewById<EditText>(R.id.edtConfirmarContrasena)
        val btnRegistro = findViewById<Button>(R.id.btnRegistrarme)
        val linkLogin = findViewById<TextView>(R.id.txtEnlaceLogin)

        linkLogin.setOnClickListener {
            finish()  // Regresa al login
        }

        btnRegistro.setOnClickListener {
            val email = correo.text.toString().trim()
            val password = pass.text.toString().trim()
            val password2 = pass2.text.toString().trim()

            if (email.isEmpty() || password.isEmpty() || password2.isEmpty()) {
                Toast.makeText(this, "Completa los campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (password != password2) {
                Toast.makeText(this, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            auth.createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener {
                    Toast.makeText(this, "Registro exitoso", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this, LoginActivity::class.java))
                    finish()
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Error al registrar: ${it.message}", Toast.LENGTH_SHORT).show()
                    Log.e("joto", "Error al registrar: ${it.message}")
                }
        }
    }
}
