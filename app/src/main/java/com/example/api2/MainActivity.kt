package com.example.api2


import java.security.SecureRandom
import java.security.cert.X509Certificate
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.Environment
import android.os.StrictMode
import android.util.Base64
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.VolleyLog
import com.android.volley.toolbox.HurlStack
import com.android.volley.toolbox.ImageRequest
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.squareup.picasso.Picasso
import org.json.JSONObject
import java.io.File
import java.io.FileOutputStream
import javax.net.ssl.*

class MainActivity : AppCompatActivity() {
    private var imageUrl: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        configureSSL()


        // Deshabilitar la verificación de certificado SSL/TLS por completo
        val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)


        // Agregar un listener al botón para generar una imagen aleatoria al hacer clic
        val generateButton: Button = findViewById(R.id.button_generate)
        generateButton.setOnClickListener { generarImagenAleatoria() }

        // Agregar un listener al botón para descargar la imagen actual al hacer clic
        val downloadButton: Button = findViewById(R.id.button_download)
        downloadButton.setOnClickListener {
            if (imageUrl != null) {
                descargarImagen(imageUrl!!)
            }
        }
    }






    private fun generarImagenAleatoria() {

        val url = "https://192.168.1.9/multimedia/enrutador.php\n"
        Log.d("URL_DEBUG", "URL: $url")

        val queue = Volley.newRequestQueue(this)
        val request = JsonObjectRequest(
            Request.Method.GET, url, null,
            { response ->

                val message = response.get("message")
                if (message is String) {
                    // Procesar la cadena como se desee
                    Log.d("Volley", "Mensaje: $message")
                } else {
                    // El valor de "message" es un objeto JSON
                    val messageObject = message as JSONObject
                    imageUrl = messageObject.getString("data")
                    // Resto del código para procesar la respuesta JSON
                }




            },
            { error ->
                Log.e("Volley", error.toString())
            })

        queue.add(request)
    }

    private fun descargarImagen(imageUrl: String) {
        // Hacer una solicitud HTTP utilizando Volley para obtener la imagen
        val queue = Volley.newRequestQueue(this)
        val imageRequest = ImageRequest(
            imageUrl,
            { response ->
                // Guardar la imagen en el almacenamiento externo
                guardarImagen(response)
            }, 0, 0, null, null
        )

        queue.add(imageRequest)
    }

    private fun guardarImagen(bitmap: Bitmap) {
        // Verificar si se tiene permiso de escritura en el almacenamiento externo

        // Crear un archivo en el directorio de Descargas
        val file = File(
            Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DOWNLOADS
            ), "imagen_descargada.png"
        )

        try {
            // Guardar la imagen en el archivo
            val stream = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
            stream.flush()
            stream.close()

            // Mostrar un mensaje de éxito
            Toast.makeText(this, "Imagen descargada con éxito", Toast.LENGTH_SHORT)
                .show()
        } catch (e: Exception) {
            Log.e("GuardarImagen", e.toString())
            Toast.makeText(this, "Error al guardar la imagen", Toast.LENGTH_SHORT)
                .show()
        }
    }

    private fun configureSSL(): SSLSocketFactory? {
        val trustAllCerts = arrayOf<TrustManager>(object : X509TrustManager {
            override fun checkClientTrusted(chain: Array<out X509Certificate>?, authType: String?) {}
            override fun checkServerTrusted(chain: Array<out X509Certificate>?, authType: String?) {}
            override fun getAcceptedIssuers(): Array<X509Certificate> {
                return arrayOf()
            }
        })

        try {
            val sc = SSLContext.getInstance("SSL")
            sc.init(null, trustAllCerts, SecureRandom())
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.socketFactory)
            HttpsURLConnection.setDefaultHostnameVerifier { _, _ -> true }
            return sc.socketFactory
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null


    }







}