package io.ymsoft.objectfinder

import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.util.Log
import android.util.TypedValue
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import io.ymsoft.objectfinder.utils.FileUtil

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

fun logI(message: String){
    Log.i("", message)
}

fun logE(message: String){
    Log.e("", message)
}

fun Activity?.showKeyboard() {
    val imm : InputMethodManager = this?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS)
}

fun Activity?.hideKeyboard() {
    val inputMethodManager =
        this?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

    // Check if no view has focus
    val currentFocusedView = currentFocus
    currentFocusedView?.let {
        inputMethodManager.hideSoftInputFromWindow(
            currentFocusedView.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
    }
}

fun Fragment.hideKeyboard() {
    val inputMethodManager =
        this?.activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

    // Check if no view has focus
    val currentFocusedView = this?.activity?.currentFocus
    currentFocusedView?.let {
        inputMethodManager.hideSoftInputFromWindow(
            currentFocusedView.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
    }
}