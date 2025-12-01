package ruben.gutierrez.proyectofinal_lecturasyresenas.utilities

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.*

class StatisticsManager {

    private val userId = FirebaseAuth.getInstance().currentUser!!.uid
    private val statsRef = FirebaseFirestore.getInstance()
        .collection("usuarios")
        .document(userId)
        .collection("estadisticas")
        .document("general")

    private val sdfDia = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    private val sdfMes = SimpleDateFormat("yyyy-MM", Locale.getDefault())
    private val sdfAno = SimpleDateFormat("yyyy", Locale.getDefault())

    private val hoy = Date()
    private val diaActual = sdfDia.format(hoy)
    private val mesActual = sdfMes.format(hoy)
    private val anoActual = sdfAno.format(hoy)


    fun registrarPaginas(leidas: Int) {
        statsRef.get().addOnSuccessListener { doc ->

            val data = doc.data ?: emptyMap()

            val ultimaFecha = data["ultimaActualizacion"] as? String ?: diaActual
            val paginasHoyPrevio = (data["paginasHoy"] as? Long ?: 0).toInt()
            val rachaPrev = (data["rachaDias"] as? Long ?: 1).toInt()

            // Mapas de paginas
            val paginasMes = (data["paginasPorMes"] as? Map<String, Long>)?.toMutableMap()
                ?: mutableMapOf()
            val paginasAno = (data["paginasPorAno"] as? Map<String, Long>)?.toMutableMap()
                ?: mutableMapOf()


            // ------ RESETEO DE DÍA ------
            val paginasHoy = if (ultimaFecha != diaActual) {
                leidas
            } else {
                paginasHoyPrevio + leidas
            }

            // ------ ACTUALIZAR MESES Y AÑO ------
            paginasMes[mesActual] = (paginasMes[mesActual] ?: 0) + leidas
            paginasAno[anoActual] = (paginasAno[anoActual] ?: 0) + leidas

            // ------ RACHA ------
            val racha = calcularRacha(ultimaFecha, rachaPrev)

            if (paginasHoy!=paginasHoyPrevio || racha != rachaPrev) {
                val updates = mapOf(
                    "paginasPorMes" to paginasMes,
                    "paginasPorAno" to paginasAno,
                    "paginasHoy" to paginasHoy,
                    "rachaDias" to racha,
                    "ultimaActualizacion" to diaActual
                )

                statsRef.set(updates, com.google.firebase.firestore.SetOptions.merge())

            }
        }
    }


    fun registrarLibroLeido() {
        statsRef.get().addOnSuccessListener { doc ->

            val data = doc.data ?: emptyMap()

            // Mapas de libros
            val librosMes = (data["librosPorMes"] as? Map<String, Long>)?.toMutableMap()
                ?: mutableMapOf()
            val librosAno = (data["librosPorAno"] as? Map<String, Long>)?.toMutableMap()
                ?: mutableMapOf()

            librosMes[mesActual] = (librosMes[mesActual] ?: 0) + 1
            librosAno[anoActual] = (librosAno[anoActual] ?: 0) + 1

            statsRef.set(
                mapOf(
                    "librosPorMes" to librosMes,
                    "librosPorAno" to librosAno,
                    "ultimaActualizacion" to diaActual
                ), com.google.firebase.firestore.SetOptions.merge()
            )
        }
    }


    private fun calcularRacha(ultimaFecha: String, rachaPrev: Int): Int {
        val fechaUlt = sdfDia.parse(ultimaFecha) ?: return 1
        val diff = (hoy.time - fechaUlt.time) / (1000 * 60 * 60 * 24)

        return when {
            diff == 0L -> rachaPrev
            diff == 1L -> rachaPrev + 1
            else -> 1
        }
    }
}
