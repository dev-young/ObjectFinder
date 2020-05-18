package io.ymsoft.objectfinder.ui.add

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import io.ymsoft.objectfinder.*
import io.ymsoft.objectfinder.databinding.ActivityAddStorageBinding
import io.ymsoft.objectfinder.models.StorageModel
import io.ymsoft.objectfinder.repository.ObjectRepository
import io.ymsoft.objectfinder.ui.BaseActivity
import io.ymsoft.objectfinder.utils.*
import io.ymsoft.objectfinder.utils.PickPhotoHelper
import io.ymsoft.objectfinder.view_model.AddStorageViewModel
import java.util.concurrent.TimeUnit

@Deprecated("Fragment 사용으로 대체")
class AddStorageActivity : BaseActivity() {

    private lateinit var binding: ActivityAddStorageBinding
    
    private lateinit var viewModel: AddStorageViewModel
    private val pickPhotoHelper = PickPhotoHelper()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_add_storage)
        setHomeBtn(binding.toolbar)
        
        viewModel = ViewModelProvider(this).get(AddStorageViewModel::class.java)
        

        binding.content.imgView.setOnClickListener{
            takePhoto()
        }

        binding.content.saveBtn.setOnClickListener {
            save()
        }

        setPointLayoutEnabled(false)

    }

    private fun save() {
        val photoUrl = pickPhotoHelper.currentPhotoPath
        val name = binding.content.storageName.text.toString()
        
        //유효성 검사
        if(photoUrl.isNullOrBlank() && name.isBlank()){
            makeToast(R.string.please_input_name_or_photo)
            return
        }
        
        val model = StorageModel(id = null, imgUrl = photoUrl, name = name)
        ObjectRepository.add(model, TaskListener { 
            if(it.isSuccessful){
                it.result?.let {
                    // TODO: 추가된 StorageModel에 대한 StorageDetailFragment으로 이동
                }
                pickPhotoHelper.clear()
                finish()
            } else {
                makeToast(it.message)
            }
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == RESULT_OK) {
            var isPhotoLoaded = false
            if (requestCode == PICK_FROM_ALBUM) {
                data?.data?.let {
//                    val input = contentResolver.openInputStream(it)
//                    val bm = BitmapFactory.decodeStream(input)
//                    input?.close()
                    binding.content.imgView.loadUri(it)
                    isPhotoLoaded = true
                }
            } else if (requestCode == REQUEST_TAKE_PHOTO) {
                /*저해상도
                val bm = data?.extras?.get("data") as Bitmap?
                binding.content.imgView.loadBitmap(bm)
                */

                pickPhotoHelper.photoUri?.let {
                    binding.content.imgView.loadUri(it)
                    isPhotoLoaded = true
                }
            }

            if(isPhotoLoaded)
                setPointLayoutEnabled(isPhotoLoaded)
        }
    }

    fun pickFromAlbum(){
        pickPhotoHelper.startPickFromAlbumActivity(activity = this)
    }

    private fun takePhoto(){
        pickPhotoHelper.deletePickedPhoto()
        pickPhotoHelper.dispatchTakePictureIntentIfAvailable(this)
    }


    @SuppressLint("ClickableViewAccessibility")
    private fun setPointLayoutEnabled(enable:Boolean){
        if(enable){
            binding.content.pointer.visibility = View.VISIBLE
            binding.content.pointLayout.setOnTouchListener { v, event ->
                Log.i("point", "${event.x} , ${event.y}, ${event.action}")
                movePointer(binding.content.pointer, binding.content.pointLayout, event.x, event.y)
                when(event.action){
                    MotionEvent.ACTION_UP -> v.parent.requestDisallowInterceptTouchEvent(false)
                    MotionEvent.ACTION_DOWN -> v.parent.requestDisallowInterceptTouchEvent(true)
                }

                v.onTouchEvent(event)
            }
            binding.content.imgView.isClickable = false
            binding.content.imgView.background = null
            Observable.just(0)
                .delay(100, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe {
                    movePointer(binding.content.pointer, binding.content.pointLayout, binding.content.pointLayout.pivotX, binding.content.pointLayout.pivotY)
                }

        } else {
            binding.content.pointer.visibility = View.GONE
            binding.content.pointLayout.setOnTouchListener(null)
            binding.content.imgView.isClickable = true
            binding.content.imgView.addRipple()
        }

    }

    private fun movePointer(pointer: View, parent:ViewGroup, x: Float, y: Float) {
        if(x < 0 || y < 0 || x > parent.width || y > parent.height)
            return
        val params = pointer.layoutParams as FrameLayout.LayoutParams
        pointer.layoutParams = params.apply {
            leftMargin = x.toInt() - (pointer.width/2)
            topMargin = y.toInt() - (pointer.height/2)
        }
    }

    override fun onBackPressed() {
        pickPhotoHelper.deletePickedPhoto()
        super.onBackPressed()
    }

}
