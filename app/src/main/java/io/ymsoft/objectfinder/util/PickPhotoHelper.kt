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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.io.IOException

const val REQUEST_TAKE_PHOTO = 100
const val PICK_FROM_ALBUM = 10

/**촬영, 갤러리를 사용하여 사진을 고르는 작업을 편하게 해주는 클래스 */
class PickPhotoHelper {
    var fileNeverBeDeleted : String? = null //삭제되면 안되는 FilePath (새롭게 생성된 이미지가 아닌 기존에 있던 이미지)
        set(value) {
            currentPhotoPath = value
            field = value
        }
    var photoUri : Uri? = null
    var currentPhotoPath : String? = null

    fun startPickFromAlbumActivity(activity: Activity? = null, fragment: Fragment? = null) {
        val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
            type = "image/*" //allows any image file type. Change * to specific extension to limit it
        }

        activity?.apply {
            if (intent.resolveActivity(packageManager) != null) startActivityForResult(intent, PICK_FROM_ALBUM)
            return
        }

        fragment?.apply {
            if (fragment?.activity?.packageManager?.let { intent.resolveActivity(it) } != null) {
                startActivityForResult(intent, PICK_FROM_ALBUM)
            }
        }
    }

    /**외부 저장소의 Uri를 Bitmap으로 변환한 뒤 내부 저장소에 새로운 파일로 저장한다.*/
    fun makePhotoFromUri(context:Context, uri: Uri){
        CoroutineScope(Dispatchers.IO).launch {
            photoUri = uri
            val bm = getBitmap(context, uri)
            val file = FileUtil.createImageFile(context)
            file?.let{
                currentPhotoPath = file.absolutePath
                FileUtil.saveBitmapToFile(bm, file)
            }
        }
    }

    fun getBitmap(context: Context, uri: Uri): Bitmap {
        val input = context.contentResolver.openInputStream(uri)
        val bm = BitmapFactory.decodeStream(input)
        input?.close()
        return bm
    }

    fun dispatchTakePictureIntentIfAvailable(activity: Activity? = null, fragment: Fragment? = null){
        val pm = activity?.packageManager ?: fragment?.activity?.packageManager
        val context : Context? = activity ?: fragment?.context

        //카메라 기능 체크
        pm?.let {
            if(!it.hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY)){
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

    fun dispatchTakePictureIntent(pm:PackageManager?,
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
                        FileUtil.createImageFile(context)
                    } catch (ex: IOException) {
                        // Error occurred while creating the File
                        null
                    }
                    // Continue only if the File was successfully created
                    photoFile?.also {
                        val photoURI: Uri? = context?.let { c -> FileUtil.getUri(c, it) }
                        photoUri = photoURI
                        currentPhotoPath = it.absolutePath
                        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                        startTakePicture(activity, fragment, takePictureIntent)
                    }
                }
            }
        }

    }

    private fun startTakePicture(activity: Activity? = null, fragment: Fragment? = null, intent:Intent){
        activity?.apply {
            startActivityForResult(intent, REQUEST_TAKE_PHOTO)
            return
        }

        fragment?.apply {
            startActivityForResult(intent, REQUEST_TAKE_PHOTO)
        }

    }

    /**촬영된 원본 사진을 삭제한다.
     * 주로 뒤로가기에 의해 저장하지 않고 나갈경우 사용된다*/
    fun deletePickedPhoto(){
        if(fileNeverBeDeleted == null || fileNeverBeDeleted != currentPhotoPath)
            FileUtil.delete(currentPhotoPath)
        clear()
    }

    /**사진이 사용될경우 추후 deletePickedPhoto() 에 의해 삭제되지 않도록 초기화 */
    fun clear() {
        photoUri = null
        currentPhotoPath = null
    }


}