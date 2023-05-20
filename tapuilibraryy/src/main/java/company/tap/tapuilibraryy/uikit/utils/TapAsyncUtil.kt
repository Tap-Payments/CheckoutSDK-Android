package company.tap.tapuilibraryy.uikit.utils

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.AsyncTask
import android.view.View
import android.widget.ImageView
import company.tap.tapuilibraryy.uikit.atoms.TapTextViewNew
import java.io.InputStream
import java.net.URL


/**
 * Created by AhlaamK on 6/22/20.

Copyright (c) 2020    Tap Payments.
All rights reserved.
 **/
class TapAsyncUtil {
    open class DownLoadImageTask(imageView: ImageView, textView: TapTextViewNew) :
        AsyncTask<String, Void, Bitmap>() {
        private var imageView: ImageView
        private var textView: TapTextViewNew


        override fun onPreExecute() {
            super.onPreExecute()
            textView.visibility = View.VISIBLE
            imageView.visibility = View.GONE
        }

        override fun onPostExecute(result: Bitmap) {
            imageView.setImageBitmap(result)
            textView.visibility = View.GONE
            imageView.visibility = View.VISIBLE

        }

        init {
            this.imageView = imageView
            this.textView = textView

        }

        override fun doInBackground(vararg urls: String): Bitmap? {
            val urlOfImage = urls[0]
            var logo: Bitmap? = null
            try {
                val `is`: InputStream = URL(urlOfImage).openStream()
                logo = BitmapFactory.decodeStream(`is`)
            } catch (e: Exception) { // Catch the download exception
                e.printStackTrace()
            }
            return logo
        }
    }
}
