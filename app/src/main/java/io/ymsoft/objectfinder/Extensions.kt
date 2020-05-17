package io.ymsoft.objectfinder

import android.R
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.util.TypedValue
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import io.ymsoft.objectfinder.utils.FileUtil
import java.io.File

fun ImageView.loadBitmap(image: Bitmap?) {
    Glide.with(this).load(image).apply(RequestOptions.centerCropTransform()).into(this)
}

fun ImageView.loadUrl(imageUrl: String?) {
    Glide.with(this).load(imageUrl).into(this)
}

fun ImageView.loadFilePath(filePath: String?) {
    Glide.with(this).load(FileUtil.getUri(context, filePath)).apply(RequestOptions.centerCropTransform()).into(this)
}

fun ImageView.loadUri(uri: Uri?) {
    Glide.with(this).load(uri).apply(RequestOptions.centerCropTransform()).into(this)
}

fun View.addRipple() = with(TypedValue()) {
    context.theme.resolveAttribute(R.attr.selectableItemBackground, this, true)
    setBackgroundResource(resourceId)
}

fun View.addCircleRipple() = with(TypedValue()) {
    context.theme.resolveAttribute(R.attr.selectableItemBackgroundBorderless, this, true)
    setBackgroundResource(resourceId)
}

fun Context?.makeToast(@StringRes id: Int?) {
    id?.let { Toast.makeText(this, this?.getString(id), Toast.LENGTH_LONG).show() }
}

fun Context?.makeToast(text: String?) {
    Toast.makeText(this, text, Toast.LENGTH_LONG).show()
}