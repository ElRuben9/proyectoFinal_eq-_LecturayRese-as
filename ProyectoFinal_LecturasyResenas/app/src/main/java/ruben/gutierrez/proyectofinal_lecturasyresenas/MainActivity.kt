package ruben.gutierrez.proyectofinal_lecturasyresenas

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import ruben.gutierrez.proyectofinal_lecturasyresenas.LibroAdapter

class MainActivity : AppCompatActivity() {

    private lateinit var viewModel: LibroViewModel
    private lateinit var adapter: LibroAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        // -------------------------
        // RecyclerView
        // -------------------------
        val recycler = findViewById<RecyclerView>(R.id.recyclerViewLibros)
        adapter = LibroAdapter()
        recycler.layoutManager = LinearLayoutManager(this)
        recycler.adapter = adapter

        // -------------------------
        // ViewModel
        // -------------------------
        viewModel = ViewModelProvider(this)[LibroViewModel::class.java]

        viewModel.libros.observe(this) { lista ->
            adapter.submitList(lista)
        }

        // -------------------------
        // Bottom Navigation
        // -------------------------
        val bottomNavigation = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNavigation.selectedItemId = R.id.nav_biblioteca

        bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_biblioteca -> true
                R.id.nav_estadisticas -> {
                    startActivity(Intent(this, EstadisticasActivity::class.java))
                    overridePendingTransition(0, 0)
                    true
                }
                else -> false
            }
        }

        // -------------------------
        // Botón Agregar Libro
        // -------------------------
        findViewById<FloatingActionButton>(R.id.fab_agregar_libro).setOnClickListener {
            startActivity(Intent(this, AgregarLibroActivity::class.java))
        }

        // -------------------------
        // Botón de Perfil
        // -------------------------
        findViewById<ImageView>(R.id.boton_perfil).setOnClickListener {
            startActivity(Intent(this, PerfilActivity::class.java))
        }
    }
}
