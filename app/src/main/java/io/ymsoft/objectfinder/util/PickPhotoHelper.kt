package io.ymsoft.objectfinder.util

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import com.gun0912.tedpermission.PermissionListener
import io.ymsoft.objectfinder.R
import java.io.File
import java.io.IOException

const val REQUEST_TAKE_PHOTO = 100
const val REQUEST_PICK_FROM_ALBUM = 10
const val PICK_FROM_OTHERS = 20

/**촬영, 갤러리를 사용하여 사진을 고르는 작업을 편하게 해주는 클래스 */
class PickPhotoHelper {
    var photoUri: Uri? = null

    fun startPickFromAlbumActivity(activity: Activity? = null, fragment: Fragment? = null) {
        val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
            type =
                "image/*" //allows any image file type. Change * to specific extension to limit it
        }

        activity?.apply {
            if (intent.resolveActivity(packageManager) != null) startActivityForResult(
                intent,
                REQUEST_PICK_FROM_ALBUM
            )
            return
        }

        fragment?.apply {
            if (fragment.activity?.packageManager?.let { intent.resolveActivity(it) } != null) {
                startActivityForResult(intent, REQUEST_PICK_FROM_ALBUM)
            }
        }
    }

    fun dispatchTakePictureIntentIfAvailable(
        activity: Activity? = null,
        fragment: Fragment? = null
    ) {
        val pm = activity?.packageManager ?: fragment?.activity?.packageManager
        val context: Context? = activity ?: fragment?.context

        //카메라 기능 체크
        pm?.let {
            if (!it.hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY)) {
                context?.makeToast(R.string.camera_cannot_be_used)
                return
            }
        }

        //권한 체크
        context?.let {
            PermissionUtil.requestCameraPermissions(it, object : PermissionListener {
                override fun onPermissionGranted() {
                    dispatchTakePictureIntent(pm, it, activity, fragment)
                }

                override fun onPermissionDenied(deniedPermissions: MutableList<String>?) {

                }
            })
        }

        return
    }

    fun dispatchTakePictureIntent(
        pm: PackageManager?,
        context: Context,
        activity: Activity? = null,
        fragment: Fragment? = null
    ) {
        pm?.apply {
            Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
                // Ensure that there's a camera activity to handle the intent
                takePictureIntent.resolveActivity(this)?.also {
                    // Create the File where the photo should go
                    val photoFile: File? = try {
                        FileUtil.createTempImageFile(context)
                    } catch (ex: IOException) {
                        // Error occurred while creating the File
                        null
                    }
                    // Continue only if the File was successfully created
                    photoFile?.also {
                        val photoURI: Uri? = context.getUri(it)
                        photoUri = photoURI
                        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                        startTakePicture(activity, fragment, takePictureIntent)
                    }
                }
            }
        }

    }

    private fun startTakePicture(
        activity: Activity? = null,
        fragment: Fragment? = null,
        intent: Intent
    ) {
        activity?.apply {
            startActivityForResult(intent, REQUEST_TAKE_PHOTO)
            return
        }

        fragment?.apply {
            startActivityForResult(intent, REQUEST_TAKE_PHOTO)
        }

    }

    fun getBitmap(context: Context, uri: Uri): Bitmap {
        val input = context.contentResolver.openInputStream(uri)
        val bm = BitmapFactory.decodeStream(input)
        input?.close()
        return bm
    }
}