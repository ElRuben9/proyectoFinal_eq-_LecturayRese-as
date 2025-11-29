package ruben.gutierrez.proyectofinal_lecturasyresenas

import android.app.Application
import com.cloudinary.android.MediaManager

class App : Application() {
    override fun onCreate() {
        super.onCreate()

        val config = HashMap<String, String>()

        config["cloud_name"] = getString(R.string.cloud_name)
        config["api_key"] = getString(R.string.api_key)

        MediaManager.init(this, config)
    }
}
