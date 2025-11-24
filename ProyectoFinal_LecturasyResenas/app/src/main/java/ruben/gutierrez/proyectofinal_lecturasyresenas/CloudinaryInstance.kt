package ruben.gutierrez.proyectofinal_lecturasyresenas
import android.content.Context
import com.cloudinary.android.MediaManager
import com.cloudinary.android.callback.UploadCallback

object CloudinaryInstance {

    fun init(context: Context) {

        val config = mapOf(
            "cloud_name" to "dm76tjfdg",
            "api_key" to "143944168384665"
        )
        MediaManager.init(context, config)
    }


    fun get(): MediaManager = MediaManager.get()
}