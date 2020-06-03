package io.ymsoft.objectfinder.util

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.os.Environment
import android.util.TypedValue
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.view.menu.ActionMenuItemView
import androidx.appcompat.widget.ActionMenuView
import androidx.core.view.children
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target
import io.ymsoft.objectfinder.R
import io.ymsoft.objectfinder.ui.MainActivity
import kotlinx.android.synthetic.main.activity_main.*
import timber.log.Timber

fun logI(message: String) {
    Timber.i(message)
}

fun logE(message: String) {
    Timber.e(message)
}

fun View.animateFadeIn(startDelay: Long = 0){
    alpha = 0f
    translationY = 5f
    val duration = resources.getInteger(R.integer.default_transition_duration).toLong()
    animate().alpha(1f).setStartDelay(startDelay).translationY(0f).setDuration(duration).start()
}

fun View.setOnSingleClickListener(action: (v: View) -> Unit){
    setOnClickListener(SingleClickListener(action))
}

fun ImageView.loadBitmap(image: Bitmap?) {
    Glide.with(this).load(image).apply(RequestOptions.centerCropTransform()).into(this)
}

fun ImageView.loadUrl(imageUrl: String?) {
    Glide.with(this).load(imageUrl).into(this)
}

fun ImageView.loadFilePath(filePath: String?, bitmapListener: ((bitmap:Bitmap?) -> Unit?)? = null) {
    loadUri(FileUtil.getUri(context, filePath), bitmapListener)
}

fun ImageView.loadUri(uri: Uri?, bitmapListener: ((bitmap:Bitmap?) -> Unit?)? = null) {
    Glide.with(this)
        .asBitmap()
        .load(uri)
        .dontAnimate()
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
                resource?.let { bitmapListener?.invoke(it) }
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

fun Context.getAbsolutePhotoPath(fileName : String): String? {
    var path : String? = null
    getExternalFilesDir(Environment.DIRECTORY_PICTURES)?.let {
        path = "${it.absolutePath}/$fileName"
    }
    return path
}

fun Context?.makeToast(@StringRes id: Int?) {
    id?.let { makeToast(this?.getString(id)) }
}

fun Context?.makeToast(text: String?) {
    Toast.makeText(this, text, Toast.LENGTH_SHORT).show()
}

fun Activity?.startToolbarAnimation(){
    if (this is MainActivity){
        this.toolbar.children.forEach {
            if(it is ActionMenuView){
                it.children.forEachIndexed { index, view ->
                    if(view is ActionMenuItemView){
                        view.setTranslationY(-30f)
                        view.alpha = 0f
                        view.animate().setStartDelay(100L + (index * 10)).setDuration(200).translationY(0f).alpha(1f)
                    }
                }

            }
        }
    }
}

fun Activity?.showKeyboard() {
    val imm = this?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager?
    imm?.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS)
}

fun Activity?.hideKeyboard() {
    val inputMethodManager =
        this?.getSystemService(Context.INPUT_METHOD_SERVICE)

    if( inputMethodManager is InputMethodManager) {
        this?.currentFocus?.let {
            inputMethodManager.hideSoftInputFromWindow(
                it.windowToken, InputMethodManager.HIDE_NOT_ALWAYS
            )
        }
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