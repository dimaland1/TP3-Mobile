package com.example.tp3_mobile

import android.app.IntentService
import android.content.Intent
import android.content.Context
import android.util.Log
import java.io.File
import java.net.URL

/**
 * An [IntentService] subclass for handling asynchronous task requests in
 * a service on a separate handler thread.

 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.

 */
class DownloadService : IntentService("DownloadService") {

    override fun onHandleIntent(intent: Intent?) {
        try {
            val urlString = intent?.getStringExtra("url")
            val fileName = intent?.getStringExtra("fileName")
            val outputDir = applicationContext.cacheDir // Utilisez le répertoire de cache de l'app pour le téléchargement

            if (urlString != null && fileName != null) {
                val url = URL(urlString)
                val connection = url.openConnection()
                connection.connect()
                val file = File(outputDir, fileName)

                url.openStream().use { input ->
                    file.outputStream().use { output ->
                        input.copyTo(output)
                    }
                }

                Log.i("DownloadService", "Téléchargement terminé : $fileName")
                // Ici, vous pouvez appeler une méthode pour parser le fichier si nécessaire.
            }
        } catch (e: Exception) {
            Log.e("DownloadService", "Erreur lors du téléchargement", e)
        }
    }

}