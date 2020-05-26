package io.ymsoft.objectfinder.util

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.os.Environment
import android.util.Log
import androidx.core.content.FileProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*

object FileUtil {

    fun saveBitmapToFile(bitmap: Bitmap, file: File){
        CoroutineScope(Dispatchers.IO).launch {
            if(!file.exists()){
                Log.i("", "파일 디렉토리 생성")
                file.mkdirs()
            }

            var out : FileOutputStream? = null
            try {
                out = FileOutputStream(file)
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out)
            } catch (e:Exception){
                e.printStackTrace()
            } finally {
                out?.close()
            }
        }
    }

    @Throws(IOException::class)
    fun createImageFile(context: Context?): File? {
        context?.apply {
            // Create an image file name
            val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
            val storageDir: File = getExternalFilesDir(Environment.DIRECTORY_PICTURES)!!
            return File.createTempFile(
                "JPEG_${timeStamp}_", /* prefix */
                ".jpg", /* suffix */
                storageDir /* directory */
            ).apply {
                // Save a file: path for use with ACTION_VIEW intents

            }
        }
        return null
    }

    fun getUri(context: Context, file:File): Uri {
        return FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            file
        )
    }

    fun getUri(context: Context, filePath:String?): Uri {
        return FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            File(filePath)
        )
    }

    fun delete(currentPhotoPath: String?) {
        currentPhotoPath?.let {
            File(it).apply {
                var r = false
                if(exists()){
                    r = delete()
                }
                if(r) Log.i("", "삭제 성공!  $it")
                else Log.e("", "삭제 실패!  $it")
            }
        }
    }
}