package com.ecommerce.letgoecommerce.extension

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Environment
import android.os.Parcelable
import android.util.Base64
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import android.widget.Toast
import androidx.core.content.ContextCompat.getSystemService
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade
import com.bumptech.glide.request.transition.DrawableCrossFadeFactory
import com.ecommerce.letgoecommerce.activity.MainActivity
import java.io.ByteArrayOutputStream
import java.io.File
import java.text.DecimalFormat
import java.util.*


// startIntent ile daha pratik geçiş ve veri göndermek
fun Activity.startIntent(
    targetActivity: Class<out Activity>,
    key: String? = null,
    value: Any? = null,
    isFinish: Boolean = false,
    key2: String? = null,
    value2: Any? = null,
) {

    val intent = Intent(this, targetActivity)
    if (key != null && value != null)
        when (value) {
            is Uri -> intent.setData(value)
            is Int -> intent.putExtra(key, value)
            is String -> intent.putExtra(key, value)
            is Boolean -> intent.putExtra(key, value)
            is ArrayList<*> -> intent.putParcelableArrayListExtra(
                key,
                value as ArrayList<out Parcelable>
            )

        }
    if (key2 != null && value2 != null)
        when (value2) {
            is Uri -> intent.setData(value2)
            is Int -> intent.putExtra(key2, value2)
            is String -> intent.putExtra(key2, value2)
            is Boolean -> intent.putExtra(key2, value2)
            is ArrayList<*> -> intent.putParcelableArrayListExtra(
                key2,
                value2 as ArrayList<out Parcelable>
            )

        }
    this.startActivity(intent)

    if (isFinish)
        finish()

}

@SuppressLint("CheckResult")
fun ImageView.load(url: String) {

    var factory = DrawableCrossFadeFactory.Builder().setCrossFadeEnabled(true).build()

    Glide.with(this.context)
        .load(url)
        .transition(withCrossFade(factory))
        .diskCacheStrategy(DiskCacheStrategy.ALL)
        .into(this)

}

fun showToast(message: String, isLong: Boolean = false) {
    Toast.makeText(
        MainActivity._context,
        message,
        if (isLong) Toast.LENGTH_LONG else Toast.LENGTH_SHORT
    ).show()
}


fun convertImagetoBitmap(data: String?): Bitmap? {
    if (data != null) {
        val _data = data.split(",")
        val imageBytes = Base64.decode(_data[1], Base64.DEFAULT)
        return BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
    }
    return data
}

fun createFileImage(context: Context): File {
    val dir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
    return File.createTempFile(UUID.randomUUID().toString(), ".jpg", dir)
}

fun convertBitmaptoBase64(context: Context, uri: Uri): String {


    val imageStream = context.contentResolver.openInputStream(uri)
    val _bitmap = BitmapFactory.decodeStream(imageStream)
    val baos = ByteArrayOutputStream()
    _bitmap.compress(Bitmap.CompressFormat.PNG, 70, baos)
    val b = baos.toByteArray()
    return "data:photo_url/png;base64," + Base64.encodeToString(b, Base64.DEFAULT)
}

fun moneyFormatter(number: Float): String {

    return if (number > 1000) {
        DecimalFormat("#,###.##").format(number).replace(",", "'").replace(".", ",")
            .replace("'", ".")
    } else number.toString().replace(".", ",")
}

