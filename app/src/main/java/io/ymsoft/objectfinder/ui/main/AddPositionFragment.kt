package io.ymsoft.objectfinder.ui.main

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.findNavController
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import io.ymsoft.objectfinder.*
import io.ymsoft.objectfinder.databinding.FragmentAddPositionBinding
import io.ymsoft.objectfinder.models.PositionModel
import io.ymsoft.objectfinder.repository.ObjectRepository
import io.ymsoft.objectfinder.utils.PICK_FROM_ALBUM
import io.ymsoft.objectfinder.utils.PickPhotoHelper
import io.ymsoft.objectfinder.utils.PointerUtil
import io.ymsoft.objectfinder.utils.REQUEST_TAKE_PHOTO
import java.util.concurrent.TimeUnit

/**
 * A simple [Fragment] subclass.
 */
class AddPositionFragment : Fragment() {
    private lateinit var binding : FragmentAddPositionBinding
    private val pickPhotoUtil = PickPhotoHelper()
    
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_add_position, container, false)

        binding.takePhoto.setOnClickListener { takePhoto() }
        binding.pickFromAlbum.setOnClickListener { pickFromAlbum() }

        binding.saveBtn.setOnClickListener { save() }
        binding.removeBtn.setOnClickListener { clearPhoto() }

        setPointLayoutEnabled(false)

        return binding.root
    }

    /**현재 고른 사진을 지운다 */
    private fun clearPhoto() {
        pickPhotoUtil.deletePickedPhoto()
        setPointLayoutEnabled(false)
        binding.imgView.setImageDrawable(null)
    }

    private fun save() {
        var photoUrl = pickPhotoUtil.currentPhotoPath
        val name = binding.positionName.text.toString()
        val point = getRelativeCoordinate()
        val memo = binding.positionMemo.text.toString()

        //유효성 검사
        if(photoUrl.isNullOrBlank() && name.isBlank()){
            context.makeToast(R.string.please_input_name_or_photo)
            return
        }

        //PositionModel 생성
        val model = PositionModel(
            imgUrl = photoUrl,
            name = name,
            x = point?.first,
            y = point?.second,
            memo = memo)
        ObjectRepository.add(model, TaskListener {
            if(it.isSuccessful){
                pickPhotoUtil.clear()
                it.result?.let { positionModel ->
                    //추가된 PositionModel에 대한 PositionDetailFragment으로 이동
                    findNavController().navigate(R.id.action_navAddPosition_to_navPositionDetail)
                }


            } else {
                context.makeToast(it.message)
            }
        })

    }

    /**포인터가 화면에 표시되어있는경우 상대좌표 반환*/
    private fun getRelativeCoordinate(): Pair<Float, Float>? {
        if(binding.pointer.visibility != View.VISIBLE)
            return null

        val w = binding.pointLayout.width
        val h = binding.pointLayout.height
        val x = binding.pointer.x + binding.pointer.pivotX
        val y = binding.pointer.y + binding.pointer.pivotY

        return Pair(x/w, y/h)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == AppCompatActivity.RESULT_OK) {
            var isPhotoLoaded = false
            if (requestCode == PICK_FROM_ALBUM) {
                data?.data?.let {uri ->
                    binding.imgView.loadUri(uri)
                    context?.let { pickPhotoUtil.makePhotoFromUri(it, uri) }

                    isPhotoLoaded = true
                }
            } else if (requestCode == REQUEST_TAKE_PHOTO) {
                /*저해상도
                val bm = data?.extras?.get("data") as Bitmap?
                binding.imgView.loadBitmap(bm)
                */

                pickPhotoUtil.photoUri?.let {
                    binding.imgView.loadUri(it)
                    isPhotoLoaded = true
                }
            }

            if(isPhotoLoaded)
                setPointLayoutEnabled(isPhotoLoaded)
        }
    }



    private fun pickFromAlbum(){
        pickPhotoUtil.startPickFromAlbumActivity(fragment = this)
    }

    private fun takePhoto(){
        pickPhotoUtil.deletePickedPhoto()
        pickPhotoUtil.dispatchTakePictureIntentIfAvailable(fragment = this)
    }


    @SuppressLint("ClickableViewAccessibility")
    private fun setPointLayoutEnabled(enable:Boolean){
        if(enable){
            binding.addPhotoBtnGroup.visibility = View.GONE
            binding.photoMenuLayout.visibility = View.VISIBLE
            binding.pointer.visibility = View.VISIBLE
            binding.pointLayout.setOnTouchListener { v, event ->
//                Log.i("point", "${event.x} , ${event.y}, ${event.action}")
                PointerUtil.movePointer(binding.pointer, binding.pointLayout, event.x, event.y)
                when(event.action){
                    MotionEvent.ACTION_DOWN -> v.parent.requestDisallowInterceptTouchEvent(true)
                    MotionEvent.ACTION_UP -> {
                        v.parent.requestDisallowInterceptTouchEvent(false)
                        Log.i("", getRelativeCoordinate().toString())
                    }
                }

                v.onTouchEvent(event)
            }
            Observable.just(0)
                .delay(100, TimeUnit.MILLISECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    PointerUtil.movePointer(binding.pointer, binding.pointLayout, binding.pointLayout.pivotX, binding.pointLayout.pivotY)
                }

        } else {
            binding.addPhotoBtnGroup.visibility = View.VISIBLE
            binding.photoMenuLayout.visibility = View.GONE
            binding.pointer.visibility = View.GONE
            binding.pointLayout.setOnTouchListener(null)

        }

    }

    override fun onDestroy() {
        pickPhotoUtil.deletePickedPhoto()
        super.onDestroy()
    }
}
