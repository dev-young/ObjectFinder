package io.ymsoft.objectfinder.util

import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.util.TypedValue
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target
import io.ymsoft.objectfinder.R
import timber.log.Timber

fun logI(message: String) {
    Timber.i(message)
}

fun logE(message: String) {
    Timber.e(message)
}

fun ImageView.loadBitmap(image: Bitmap?) {
    Glide.with(this).load(image).apply(RequestOptions.centerCropTransform()).into(this)
}

fun ImageView.loadUrl(imageUrl: String?) {
    Glide.with(this).load(imageUrl).into(this)
}

fun ImageView.loadFilePath(filePath: String?) {
    loadUri(FileUtil.getUri(context, filePath))
}

fun ImageView.loadUri(uri: Uri?) {
    Glide.with(this)
        .asBitmap()
        .load(uri)
        .apply(RequestOptions.centerCropTransform())
        .error(R.drawable.ic_error_outline_24dp)
        .addListener(object : RequestListener<Bitmap>{
            override fun onLoadFailed(
                e: GlideException?,
                model: Any?,
                target: Target<Bitmap>?,
                isFirstResource: Boolean
            ): Boolean {
                return false
            }

            override fun onResourceReady(
                resource: Bitmap?,
                model: Any?,
                target: Target<Bitmap>?,
                dataSource: DataSource?,
                isFirstResource: Boolean
            ): Boolean {
                resource?.printInfo()
                return false
            }

        })
        .into(this)
}

fun Bitmap.printInfo(){
    Timber.i(
        String.format(
            "Ready bitmap %,d Kbytes, size: %d x %d",
            byteCount / 1024,
            width,
            height
        ))
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
    id?.let { makeToast(this?.getString(id)) }
}

fun Context?.makeToast(text: String?) {
    Toast.makeText(this, text, Toast.LENGTH_SHORT).show()
}

fun Activity?.showKeyboard() {
    val imm: InputMethodManager =
        this?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS)
}

fun Activity?.hideKeyboard() {
    val inputMethodManager =
        this?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

    currentFocus?.let {
        inputMethodManager.hideSoftInputFromWindow(
            it.windowToken, InputMethodManager.HIDE_NOT_ALWAYS
        )
    }
}

fun Fragment.hideKeyboard() {
    activity.hideKeyboard()
}

fun Fragment.setToolbarTitle(@StringRes resId: Int){
    setToolbarTitle(getString(resId))
}

fun Fragment.setToolbarTitle(title: String){
    (activity as AppCompatActivity).supportActionBar?.title = title
}