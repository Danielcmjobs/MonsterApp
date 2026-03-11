package com.example.monsterapp.ui.fragments

import android.animation.ObjectAnimator
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.monsterapp.R
import com.example.monsterapp.databinding.FragmentLoadingBinding

/**
 * Fragment de la pantalla de carga inicial.
 * Muestra el logo, nombre de la app y un fondo decorativo
 * mientras se cargan los datos iniciales.
 *
 * Después de un tiempo de espera, navega automáticamente al MapFragment.
 */
class LoadingFragment : Fragment() {

    // ViewBinding para acceder a las vistas del fragment
    private var _binding: FragmentLoadingBinding? = null
    private val binding get() = _binding!!

    // Handler para gestionar el delay antes de navegar
    private val handler = Handler(Looper.getMainLooper())

    // Tiempo de espera en la pantalla de carga (en milisegundos)
    private val LOADING_DELAY = 3000L

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoadingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Log.d("LoadingFragment", "⏳ Mostrando pantalla de carga...")

        // [MEJORA OPCIONAL] Animación de rotación en el logo
        animarLogo()

        // Después del tiempo de carga, navegar al mapa
        handler.postDelayed({
            navegarAlMapa()
        }, LOADING_DELAY)
    }

    /**
     * [MEJORA OPCIONAL - No forma parte de la práctica]
     * Añade una animación de pulso/rotación suave al logo
     * para dar sensación de que la app está cargando.
     */
    private fun animarLogo() {
        try {
            // Animación de rotación continua
            val rotationAnimator = ObjectAnimator.ofFloat(
                binding.ivLogo,
                "rotation",
                0f, 360f
            ).apply {
                duration = 2000
                repeatCount = ObjectAnimator.INFINITE
                interpolator = AccelerateDecelerateInterpolator()
            }
            rotationAnimator.start()

            // Animación de escala (pulso)
            val scaleXAnimator = ObjectAnimator.ofFloat(
                binding.ivLogo,
                "scaleX",
                1f, 1.1f, 1f
            ).apply {
                duration = 1500
                repeatCount = ObjectAnimator.INFINITE
            }
            scaleXAnimator.start()

            val scaleYAnimator = ObjectAnimator.ofFloat(
                binding.ivLogo,
                "scaleY",
                1f, 1.1f, 1f
            ).apply {
                duration = 1500
                repeatCount = ObjectAnimator.INFINITE
            }
            scaleYAnimator.start()

        } catch (e: Exception) {
            // Si falla la animación, continuamos sin ella
            Log.w("LoadingFragment", "No se pudo iniciar animación: ${e.message}")
        }
    }

    /**
     * Navega al MapFragment usando Navigation Component.
     * Usa la acción definida en nav_graph.xml
     */
    private fun navegarAlMapa() {
        // Verificamos que el fragment siga adjunto antes de navegar
        if (isAdded && _binding != null) {
            Log.d("LoadingFragment", "🗺️ Navegando al mapa...")

            // Usamos la acción definida en nav_graph.xml para navegar
            findNavController().navigate(R.id.action_loading_to_map)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // Cancelamos cualquier tarea pendiente para evitar memory leaks
        handler.removeCallbacksAndMessages(null)
        // Limpiamos el binding para evitar memory leaks
        _binding = null
    }
}

