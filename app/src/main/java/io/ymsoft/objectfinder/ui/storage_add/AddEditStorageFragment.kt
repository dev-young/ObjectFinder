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
import androidx.navigation.fragment.navArgs
import io.ymsoft.objectfinder.R
import io.ymsoft.objectfinder.data.StorageModel
import io.ymsoft.objectfinder.databinding.FragmentAddStorageBinding
import io.ymsoft.objectfinder.ui.pick_photo.PickPhotoFromSpecificDirActivity
import io.ymsoft.objectfinder.ui.pick_photo.REQUEST_PICK_FROM_STORAGE
import io.ymsoft.objectfinder.util.*
import io.ymsoft.objectfinder.view_custom.SquareImageView
import java.io.File


class AddEditStorageFragment : Fragment() {
    private lateinit var binding : FragmentAddStorageBinding
    private val pickPhotoHelper = PickPhotoHelper()
    private val viewModel by viewModels<AddEditStorageViewModel>()

    private val args by navArgs<AddEditStorageFragmentArgs>()
    
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentAddStorageBinding.inflate(inflater, container, false)

        binding.takePhoto.setOnClickListener { takePhoto() }
        binding.pickFromAlbum.setOnClickListener { pickFromAlbum() }
        binding.pickFromOtherStorage.setOnClickListener { pickFromOtherStorage() }

        binding.saveBtn.setOnClickListener { save() }
        binding.removeBtn.setOnClickListener { clearPhoto() }

        viewModel.isSaved.observe(viewLifecycleOwner, Observer {
            it?.let {
                pickPhotoHelper.clear()
                val direction = AddEditStorageFragmentDirections.actionNavAddStorageToNavStorageDetail(it)
                findNavController().navigate(direction)
            }
        })

        initView(args.storage)



        return binding.root
    }

    private fun initView(storage: StorageModel?) {
        if(storage == null){
            setPointLayoutEnabled(false)
            return
        }
        //기존의 보관함을 수정하는 경우

        setToolbarTitle(R.string.title_edit_storage) //툴바 타이틀명 변경

        //추가버튼 '저장'으로 이름 변경
        binding.saveBtn.setText(R.string.save)

        pickPhotoHelper.fileNeverBeDeleted = storage.imgUrl //초기에 저장된 이미지를 삭제하면 안되는 이미지로 지정
        if(storage.imgUrl == null) {
            setPointLayoutEnabled(false)
        } else {
            binding.imgView.loadFilePath(storage.imgUrl)
            setPointLayoutEnabled(true, storage.x, storage.y)
        }
        storage.name?.let { binding.storageName.setText(it) }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == AppCompatActivity.RESULT_OK) {
            var isPhotoLoaded = false
            when (requestCode) {
                REQUEST_TAKE_PHOTO -> {
                    //저해상도
//                    val bm = data?.extras?.get("data") as Bitmap
//                    binding.imgView.loadBitmap(bm)

                    pickPhotoHelper.photoUri?.let {
                        binding.imgView.loadUri(it, bitmapListener = {bitmap ->
                            pickPhotoHelper.currentPhotoPath?.let {filePath->
                                FileUtil.saveBitmapToFile(bitmap, File(filePath))
                            }
                        })

                        isPhotoLoaded = true
                    }
                }
                REQUEST_PICK_FROM_ALBUM -> {
                    data?.data?.let {uri ->
//                        val bitmap = pickPhotoHelper.getBitmap(requireContext(), uri)
//                        binding.imgView.setImageBitmap(bitmap)
//                        binding.imgView.loadBitmap(bitmap)

                        binding.imgView.loadUri(uri, bitmapListener = {bitmap ->
                            pickPhotoHelper.makePhotoFromBitmap(requireContext(), bitmap)
                        })
                        isPhotoLoaded = true
                    }
                }

                REQUEST_PICK_FROM_STORAGE -> {
                    data?.getStringExtra("filePath")?.let {
                        pickPhotoHelper.fileNeverBeDeleted = it
                        binding.imgView.loadFilePath(it)
                        isPhotoLoaded = true
                    }
                }
            }

            if(isPhotoLoaded)
                setPointLayoutEnabled(isPhotoLoaded)
        } else {
            // 사진을 불러오는작업을 취소하는 경우 (이 경우 사진을 고르지 않은 상태이다)
            logE("사진 불러오기 취소")
            pickPhotoHelper.deletePickedPhoto()

        }
    }

    /**현재 고른 사진을 지운다 */
    private fun clearPhoto() {
        pickPhotoHelper.deletePickedPhoto()
        setPointLayoutEnabled(false)
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
        var model = args.storage
        if(model == null){
            model = StorageModel(
                imgUrl = photoUrl,
                name = name,
                x = point?.first,
                y = point?.second,
                memo = memo
            )
        } else {
            model.imgUrl = photoUrl
            model.name = name
            model.x = point?.first
            model.y = point?.second
            model.memo = memo
        }

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


    private fun takePhoto(){
        pickPhotoHelper.deletePickedPhoto()
        pickPhotoHelper.dispatchTakePictureIntentIfAvailable(fragment = this)
    }


    private fun pickFromAlbum(){
        pickPhotoHelper.startPickFromAlbumActivity(fragment = this)
    }


    private fun pickFromOtherStorage() {
//        pickPhotoHelper.startPickFromOtherStorage(fragment = this)
//        ActivityUtil.start(requireContext(), PickPhotoFromSpecificDirActivity::class.java)
        PickPhotoFromSpecificDirActivity.start(this, null)
    }


    private fun setPointLayoutEnabled(enable:Boolean){
        setPointLayoutEnabled(enable, 0.5f, 0.5f)
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setPointLayoutEnabled(enable:Boolean, rx:Float?, ry:Float?){
        if(enable){
            binding.addPhotoBtnGroup.visibility = View.GONE
            binding.imgView.visibility = View.VISIBLE
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
                    PointerUtil.movePointerByRelative(binding.pointer, width, height, rx, ry)
                    binding.imgView.setOnMeasureListener(null)
                }
            })

        } else {
            binding.addPhotoBtnGroup.visibility = View.VISIBLE
            binding.imgView.visibility = View.INVISIBLE
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
