package io.ymsoft.objectfinder.ui.storage_add

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import io.ymsoft.objectfinder.R
import io.ymsoft.objectfinder.data.StorageModel
import io.ymsoft.objectfinder.databinding.FragmentAddStorageBinding
import io.ymsoft.objectfinder.util.*
import io.ymsoft.objectfinder.view_custom.SquareImageView

class AddStorageFragment : Fragment() {
    private lateinit var binding : FragmentAddStorageBinding
    private val pickPhotoHelper = PickPhotoHelper()
    private val viewModel by viewModels<AddEditStorageViewModel>()
    
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentAddStorageBinding.inflate(inflater, container, false)

        binding.takePhoto.setOnClickListener { takePhoto() }
        binding.pickFromAlbum.setOnClickListener { pickFromAlbum() }

        binding.saveBtn.setOnClickListener { save() }
        binding.removeBtn.setOnClickListener { clearPhoto() }

        viewModel.isSaved.observe(viewLifecycleOwner, Observer {
            it?.let {
                pickPhotoHelper.clear()
                val direction = AddStorageFragmentDirections.actionNavAddStorageToNavStorageDetail(it)
                findNavController().navigate(direction)
            }
        })

        setPointLayoutEnabled(false)

        return binding.root
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == AppCompatActivity.RESULT_OK) {
            var isPhotoLoaded = false
            if (requestCode == PICK_FROM_ALBUM) {
                data?.data?.let {uri ->
                    binding.imgView.loadUri(uri)
                    context?.let { pickPhotoHelper.makePhotoFromUri(it, uri) }

                    isPhotoLoaded = true
                }
            } else if (requestCode == REQUEST_TAKE_PHOTO) {
                /*저해상도
                val bm = data?.extras?.get("data") as Bitmap?
                binding.imgView.loadBitmap(bm)
                */

                pickPhotoHelper.photoUri?.let {
                    binding.imgView.loadUri(it)
                    isPhotoLoaded = true
                }
            }

            if(isPhotoLoaded)
                setPointLayoutEnabled(isPhotoLoaded)
        } else {
            logE("사진 불러오기 취소")
            pickPhotoHelper.clear()

        }
    }

    /**현재 고른 사진을 지운다 */
    private fun clearPhoto() {
        pickPhotoHelper.deletePickedPhoto()
        setPointLayoutEnabled(false)
        binding.imgView.setImageDrawable(null)
    }


    private fun save() {
        val photoUrl = pickPhotoHelper.currentPhotoPath
        val name = binding.storageName.text.toString()
        val point = getRelativeCoordinate()
        val memo = binding.storageMemo.text.toString()

        //유효성 검사
        if(photoUrl.isNullOrBlank() && name.isBlank()){
            context.makeToast(R.string.please_input_name_or_photo)
            return
        }

        //StorageModel 생성
        val model = StorageModel(
            imgUrl = photoUrl,
            name = name,
            x = point?.first,
            y = point?.second,
            memo = memo
        )

        viewModel.saveStorageModel(model)

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


    private fun pickFromAlbum(){
        pickPhotoHelper.startPickFromAlbumActivity(fragment = this)
    }


    private fun takePhoto(){
        pickPhotoHelper.deletePickedPhoto()
        pickPhotoHelper.dispatchTakePictureIntentIfAvailable(fragment = this)
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
            binding.imgView.setOnMeasureListener( object : SquareImageView.OnMeasureListener {
                override fun measured(width: Int, height: Int) {
                    PointerUtil.movePointerByRelative(binding.pointer, width, height, 0.5f, 0.5f)
                    binding.imgView.setOnMeasureListener(null)
                }
            })

        } else {
            binding.addPhotoBtnGroup.visibility = View.VISIBLE
            binding.photoMenuLayout.visibility = View.GONE
            binding.pointer.visibility = View.GONE
            binding.pointLayout.setOnTouchListener(null)

        }

    }


    override fun onDestroy() {
        pickPhotoHelper.deletePickedPhoto()
        super.onDestroy()
    }
}
