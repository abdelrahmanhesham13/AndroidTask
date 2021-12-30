package com.example.openlibrary.utils

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Handler
import android.os.Looper
import android.widget.ImageView
import java.io.InterruptedIOException
import java.util.concurrent.Executor
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class ImageLoader {

    private val executor = Executors.newSingleThreadExecutor()
    private val handler = Handler(Looper.getMainLooper())
    private var image: Bitmap? = null


    fun load(
        url: String,
        imageView: ImageView,
        errorImage: Int,
        placeHolderImage: Int
    ): ExecutorService {
        imageView.setImageResource(placeHolderImage)
        executor.execute {

            try {
                val `in` = java.net.URL(url).openStream()
                image = BitmapFactory.decodeStream(`in`)

                handler.post {
                    //if (Thread.interrupted())
                        imageView.setImageBitmap(image)
                }
            } catch (e: InterruptedIOException) {

            } catch (e: Exception) {
                handler.post {
                    imageView.setImageResource(errorImage)
                }
                e.printStackTrace()
            }
        }
        return executor
    }
}