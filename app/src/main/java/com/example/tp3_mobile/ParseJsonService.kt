import android.app.IntentService
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import com.example.tp3_mobile.SignupFormData
import com.google.gson.Gson
import java.io.InputStream
import java.io.InputStreamReader
import java.io.Serializable

class ParseJsonService : IntentService("ParseJsonService") {

    override fun onHandleIntent(intent: Intent?) {
        if (intent != null) {
            val action = intent.action
            if (ACTION_PARSE == action) {
                val fileUri = intent.getParcelableExtra<Uri>(EXTRA_FILE_URI)
                handleActionParse(fileUri)
            }
        }
    }

    private fun handleActionParse(fileUri: Uri?) {
        try {
            this.contentResolver.openInputStream(fileUri!!)?.use { inputStream ->
                val reader = InputStreamReader(inputStream)
                val gson = Gson()
                val myDataType = gson.fromJson(reader, SignupFormData::class.java)

                // Removed UI interactions
                val intent = Intent("com.example.tp3_mobile.ACTION_DATA_LOADED")

                intent.putExtra("data", myDataType as Serializable)

                sendBroadcast(intent)

                //afficher le message de succès dans un toast
                Toast.makeText(this, "Fichier lu avec succès", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            //afficher le message d'erreur dans un toast
            Toast.makeText(this, "Erreur lors de la lecture du fichier", Toast.LENGTH_SHORT)
                .show()
        }
    }

    companion object {
        private const val ACTION_PARSE = "com.example.tp3_mobile.action.PARSE"
        private const val EXTRA_FILE_URI = "com.example.tp3_mobile.extra.FILE_URI"

        fun startActionParse(context: Context, fileUri: Uri) {
            val intent = Intent(context, ParseJsonService::class.java).apply {
                action = ACTION_PARSE
                putExtra(EXTRA_FILE_URI, fileUri)
            }
            context.startService(intent)
        }
    }
}