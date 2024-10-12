package com.example.finnishmp_app

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import java.io.FileOutputStream
import java.net.URL

const val SERVER_PATHNAME = "https://avoindata.eduskunta.fi/"
/*
Muche Berhanu 2219580
ImageHandler is an object responsible for managing image retrieval and caching in the Finnish MP
application*/
object ImageHandler {
    //Retrieves an image from a given URL or returns a cached version if available.
    fun getImage(urlString: String?): ImageBitmap? {
        if (urlString.isNullOrEmpty()) {
            return null
        }

        val filename = extractFilename(urlString)
        val cachedImage = loadCachedImage(filename)

        return cachedImage ?: fetchAndCacheImage(urlString, filename)
    }
//Extracts the filename from the provided URL.
    private fun extractFilename(urlString: String): String {
        return urlString.substringAfterLast("/")
    }
//Loads an image from local storage
    private fun loadCachedImage(filename: String): ImageBitmap? {
        return try {
            BitmapFactory.decodeFile(filename)?.asImageBitmap()
        } catch (e: Exception) {
            null
        }
    }
//Fetches an image from a remote server and caches it locally.
    private fun fetchAndCacheImage(urlString: String, filename: String): ImageBitmap? {
        val url = URL(SERVER_PATHNAME + urlString)

        return runBlocking {
            val bitmap = async(Dispatchers.IO) {
                try {
                    BitmapFactory.decodeStream(url.openConnection().getInputStream())
                } catch (e: Exception) {
                    null
                }
            }.await()

            cacheImage(filename, bitmap)
            return@runBlocking bitmap?.asImageBitmap()
        }
    }
//Saves a bitmap image to local storage using the specified filename.
    private fun cacheImage(filename: String, bitmap: Bitmap?) {
        if (bitmap == null) return

        val context = FinnishMPApp.appContext
        context.openFileOutput(filename, Context.MODE_PRIVATE).use { fileOutputStream ->
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream)
        }
    }
}
