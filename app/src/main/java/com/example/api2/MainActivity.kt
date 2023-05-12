package com.example.api2

import android.annotation.SuppressLint
import android.content.ContentValues
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.os.StrictMode
import android.provider.MediaStore
import android.util.Base64
import android.util.Log

import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.api2.databinding.ActivityMainBinding
import org.json.JSONObject
import java.io.IOException
import java.security.SecureRandom
import java.security.cert.X509Certificate
import javax.net.ssl.*

class MainActivity : AppCompatActivity() {
    val junior: String = ""


    private var imageUrl: String? = null

    private lateinit var Binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(Binding.root)


        configureSSL()

        // Deshabilitar la verificación de certificado SSL/TLS por completo
        val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)


        // Agregar un listener al botón para generar una imagen aleatoria al hacer clic
        Binding.GenerarI.setOnClickListener { generarImagenAleatoria() }


        Binding.Descargar.setOnClickListener {

            val bitmap = (Binding.imageView.drawable as? BitmapDrawable)?.bitmap
            if (bitmap != null) {
                guardarImagen(bitmap)
            } else {
                Toast.makeText(this, "No hay imagen para guardar", Toast.LENGTH_SHORT).show()
            }
        }

    }


    private fun generarImagenAleatoria() {

        //Crea una cadena que contiene la URL de la API
        // y luego la imprime en la consola de registro (logcat) para depurar.
        val url = "https://192.168.1.9/multimedia/enrutador.php"
        Log.d("URL_DEBUG", "URL: $url")

        //Crea una cola de solicitudes (request queue) para almacenar
        // y administrar las solicitudes de red.
        val queue = Volley.newRequestQueue(this)

        //Crea una solicitud de objeto JSON (JsonObjectRequest) que utiliza el método HTTP GET
        val request = JsonObjectRequest(Request.Method.GET, url, null,
            { response ->

                if (response.has("message")) {
                    val message = response.getString("message")

                    if (message is String) {
                        // Procesar la cadena como se desee
                        val imageBytes = Base64.decode(message, Base64.DEFAULT)
                        val decodedImage =
                            BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
                        Binding.imageView.setImageBitmap(decodedImage)
                    } else {
                        // El valor de "message" es un objeto JSON
                        val messageObject = JSONObject(message)
                        if (messageObject.has("data")) {
                            imageUrl = messageObject.getString("data")

                        } else {
                            Log.e("Volley", "La clave 'data' no está presente en la respuesta JSON")
                        }
                    }
                } else {
                    Log.e("Volley", "La clave 'message' no está presente en la respuesta JSON")
                }
            },

            //Manejo de errores de volley
            { error ->
                Log.e("Volley", error.toString())
            })

        //Se agrega la solicitud a la cola de solicitudes
        request.tag = "API_REQUEST_TAG"
        queue.add(request)
    }

    private fun guardarImagen(bitmap: Bitmap) {


        val filename = "imagen_descargada.png"
        val mimeType = "image/png"


        //Se crea un objeto ContentValues que se utilizará para almacenar los valores de metadatos de la imagen en la galería de imágenes.
        val contentValues = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, filename)
            put(MediaStore.Images.Media.MIME_TYPE, mimeType)
        }

        //ContentResolver que se utilizará para realizar operaciones de acceso
        // y manipulación de contenido en la aplicación y se define una variable Uri nula.
        val resolver = contentResolver
        var uri: Uri? = null


        //Se obtiene la Uri de la colección de imágenes en la galería de imágenes externa principal
        // y se inserta un nuevo registro en la galería de imágenes
        try {
            val collection =
                MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
            uri = resolver.insert(collection, contentValues)
            if (uri == null) {
                throw IOException("Failed to create new MediaStore record.")
            }


            //Se abre un flujo de salida en la Uri y se comprime el objeto Bitmap en formato PNG y se escribe en el flujo de salida.
            resolver.openOutputStream(uri).use {

                    outputStream ->
                if (!bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)) {
                    throw IOException("Failed to save bitmap.")
                }
            }
            Toast.makeText(this, "Imagen descargada con éxito", Toast.LENGTH_SHORT).show()
        } catch (e: IOException) {
            Log.e("GuardarImagen", e.toString())
            Toast.makeText(this, "Error al guardar la imagen", Toast.LENGTH_SHORT).show()
        } finally {
            if (uri != null) {
                resolver.notifyChange(uri, null)
            }
        }
    }


    private fun configureSSL(): SSLSocketFactory? {
        val trustAllCerts = arrayOf<TrustManager>(@SuppressLint("CustomX509TrustManager")
        object : X509TrustManager {
            @SuppressLint("TrustAllX509TrustManager")
            override fun checkClientTrusted(
                chain: Array<out X509Certificate>?,
                authType: String?
            ) {
            }

            @SuppressLint("TrustAllX509TrustManager")
            override fun checkServerTrusted(
                chain: Array<out X509Certificate>?,
                authType: String?
            ) {
            }

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