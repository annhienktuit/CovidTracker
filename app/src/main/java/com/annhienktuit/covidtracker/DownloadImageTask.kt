package com.annhienktuit.covidtracker

import android.annotation.SuppressLint
import android.os.AsyncTask
import android.graphics.BitmapFactory

import android.graphics.Bitmap
import android.util.Log
import android.widget.ImageView
import android.widget.Toast
import java.io.IOException
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL


class DownloadImageTask(): AsyncTask<ImageView, Void, Bitmap>() {
    var imgView: ImageView? = null
    override fun doInBackground(vararg imageView: ImageView): Bitmap? {
        try {
            val thread = Thread.currentThread()
            Log.i("imagedownload: ","start at thread ${thread.id}")
            this.imgView = imageView[0]
            return getBitmapFromURL(imgView!!.tag.toString())
        }
        catch (e: IOException) {
            e.printStackTrace()
        }
        return null
    }

    override fun onPostExecute(result: Bitmap?) {
        super.onPostExecute(result)
        imgView?.setImageBitmap(result)
        Log.i("imagedownload: ","stop")
    }
    private fun getBitmapFromURL(src: String?): Bitmap? {
        return try {
            val url = URL(src)
            val connection: HttpURLConnection = url
                .openConnection() as HttpURLConnection
            connection.doInput = true
            connection.connect()
            val input: InputStream = connection.inputStream
            BitmapFactory.decodeStream(input)
        } catch (e: IOException) {
            e.printStackTrace()
            null
        }
    }
}