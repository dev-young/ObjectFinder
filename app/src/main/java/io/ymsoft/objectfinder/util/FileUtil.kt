package io.ymsoft.objectfinder.util

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.os.Environment
import androidx.core.content.FileProvider
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

object FileUtil {

    fun saveBitmapToFile(bitmap: Bitmap, file: File){
        if(!file.exists()){
            logI("파일 디렉토리 생성")
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

    @Throws(IOException::class)
    fun createTempImageFile(context: Context?): File? {
        context?.apply {
            // Create an image file name
//            val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
            return File(cacheDir, "temp_img.jpg"
//                .createTempFile(
//                "temp_", /* prefix */
//                ".jpg", /* suffix */
//                cacheDir /* directory */
            ).apply {
                // Save a file: path for use with ACTION_VIEW intents
                logI("파일 생성! $absolutePath")
            }
        }
        return null
    }

    @Throws(IOException::class)
    fun createImageFile(storageDir: File?): File? {
        storageDir?.let {
            // Create an image file name
            val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
            return File.createTempFile(
                "IMG_${timeStamp} ", /* prefix */
                ".jpg", /* suffix */
                it /* directory */
            ).apply {
                // Save a file: path for use with ACTION_VIEW intents
                logI("파일 생성! $absolutePath")
                deleteOnExit()
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

    fun delete(filePath: String?) {
        filePath?.let {
            File(it).apply {
                var r = false
                if(exists()){
                    r = delete()
                }
                if(r) logI("파일 삭제!  $it")
                else logE("삭제 실패!  $it")
            }
        }
    }

    fun deleteTempImgFile(context: Context?){
        context?.apply {
            File(cacheDir, "temp_img.jpg").delete()
        }
    }
}