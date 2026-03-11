package com.example.monsterapp

import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.example.monsterapp.databinding.ActivityMainBinding

/**
 * Activity principal de la aplicación MonsterApp.
 * 
 * Utiliza Navigation Component para gestionar la navegación entre fragments:
 * - LoadingFragment: Pantalla de carga inicial
 * - MapFragment: Mapa para explorar y encontrar Pokémon
 * - DetailsFragment: Detalles de un Pokémon seleccionado
 * - FavoritesFragment: Lista de Pokémon capturados (Pokédex)
 * 
 * El NavHostFragment definido en activity_main.xml es el contenedor
 * donde se cargan y reemplazan los fragments automáticamente.
 */
class MainActivity : AppCompatActivity() {

    // ViewBinding para acceder a las vistas de forma segura (sin findViewById)
    private lateinit var binding: ActivityMainBinding

    // NavController para gestionar la navegación entre fragments
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Habilitar diseño edge-to-edge (contenido bajo las barras del sistema)
        enableEdgeToEdge()

        // Inflar el layout usando ViewBinding
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        Log.d("MainActivity", "🚀 App iniciada - Configurando Navigation Component...")

        // Configurar los insets para el diseño edge-to-edge
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Obtener el NavHostFragment del layout
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        
        // Obtener el NavController para gestionar la navegación
        navController = navHostFragment.navController

        // Log para depuración: mostrar el destino actual
        navController.addOnDestinationChangedListener { _, destination, _ ->
            Log.d("MainActivity", "📍 Navegando a: ${destination.label}")
        }
    }

    /**
     * Gestiona el botón de retroceso del sistema.
     * Navigation Component maneja automáticamente el back stack.
     */
    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }
}

