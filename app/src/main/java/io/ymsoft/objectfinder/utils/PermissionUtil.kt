package io.ymsoft.objectfinder.utils

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.TedPermission
import io.ymsoft.objectfinder.R

object PermissionUtil {
    const val REQUEST_CODE_LOCATION = 2

    /** 저장소 관련 권한 요청  */
    fun requestStoragePermissions(
        context: Context,
        permissionlistener: PermissionListener
    ) {
        val permisions =
            arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        var isDenied = false
        for (permision in permisions) {
            val permssionCheck = ContextCompat.checkSelfPermission(context, permision)
            if (permssionCheck == PackageManager.PERMISSION_DENIED) {
                isDenied = true
                break
            }
        }
        if (isDenied) {
            TedPermission.with(context)
                .setPermissionListener(permissionlistener)
                .setRationaleMessage(context.getString(R.string.permission_rationale_storage))
                .setDeniedMessage(context.getString(R.string.permission_denied_explanation))
                .setPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .check()
        } else permissionlistener.onPermissionGranted()
    }

    /** 카메라 관련 권한 요청  */
    fun requestCameraPermissions(
        context: Context,
        permissionlistener: PermissionListener
    ) {
        val permisions =
            arrayOf(Manifest.permission.CAMERA)
        var isDenied = false
        for (permision in permisions) {
            val permssionCheck = ContextCompat.checkSelfPermission(context, permision)
            if (permssionCheck == PackageManager.PERMISSION_DENIED) {
                isDenied = true
                break
            }
        }
        if (isDenied) {
            TedPermission.with(context)
                .setPermissionListener(permissionlistener)
                .setRationaleMessage(context.getString(R.string.permission_rationale_camera))
                .setDeniedMessage(context.getString(R.string.permission_denied_explanation))
                .setPermissions(Manifest.permission.CAMERA)
                .check()
        } else permissionlistener.onPermissionGranted()
    }
}