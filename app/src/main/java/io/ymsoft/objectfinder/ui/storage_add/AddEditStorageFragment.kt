package io.ymsoft.objectfinder.ui.storage_add

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.transition.Slide
import com.google.android.material.transition.MaterialContainerTransform
import com.yalantis.ucrop.UCrop
import io.ymsoft.objectfinder.R
import io.ymsoft.objectfinder.data.StorageModel
import io.ymsoft.objectfinder.databinding.FragmentAddStorageBinding
import io.ymsoft.objectfinder.ui.pick_photo.PickPhotoFromSpecificDirActivity
import io.ymsoft.objectfinder.ui.pick_photo.REQUEST_PICK_FROM_STORAGE
import io.ymsoft.objectfinder.util.*
import io.ymsoft.objectfinder.view_custom.SquareImageView
import java.io.File


class AddEditStorageFragment : Fragment() {
    private lateinit var binding: FragmentAddStorageBinding
    private val viewModel by viewModels<AddEditStorageViewModel>()
    private val pickPhotoHelper = PickPhotoHelper()

    private val args by navArgs<AddEditStorageFragmentArgs>()

    private val tempCropImage by lazy { File(requireContext().cacheDir, "temp_img") }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        val transition = MaterialContainerTransform().apply {
//            fadeMode = MaterialContainerTransform.FADE_MODE_CROSS
//            scrimColor = Color.TRANSPARENT
//        }
//        sharedElementEnterTransition = transition
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentAddStorageBinding.inflate(inflater, container, false)

        binding.takePhoto.setOnSingleClickListener { takePhoto() }
        binding.pickFromAlbum.setOnSingleClickListener { pickFromAlbum() }
        binding.pickFromOtherStorage.setOnSingleClickListener { pickFromOtherStorage() }

        binding.saveBtn.setOnSingleClickListener { save() }
        binding.removeBtn.setOnClickListener {
            viewModel.clearImage()
            setPointLayoutEnabled(false)
        }

        viewModel.isSaved.observe(viewLifecycleOwner, Observer {
            returnTransition = null
            it?.let {
                if (args.storage != null)
                    findNavController().navigateUp()
                else {
                    val direction =
                        AddEditStorageFragmentDirections.actionNavAddStorageToNavStorageDetail(it)
                    findNavController().navigate(direction)
                }
            }
        })
        viewModel.toastMessage.observe(
            viewLifecycleOwner,
            Observer { requireContext().makeToast(it) })
        viewModel.storageModel.observe(viewLifecycleOwner, Observer(this::initView))
        viewModel.initModel(args.storage)



        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if(args.storage == null) {
            enterTransition = MaterialContainerTransform().apply {
                startView = requireActivity().findViewById(R.id.fab)
                endView = binding.root
                duration = resources.getInteger(R.integer.default_transition_duration).toLong()
                scrimColor = Color.TRANSPARENT
                containerColor = requireContext().themeColor(R.attr.colorSurface)
                startContainerColor = requireContext().themeColor(R.attr.colorSecondary)
                endContainerColor = requireContext().themeColor(R.attr.colorSurface)
            }
            returnTransition = Slide().apply {
                duration = resources.getInteger(R.integer.default_transition_duration).toLong()
                addTarget(binding.root)
            }
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setAppBarVisible(top = true)
    }

    private fun initView(storage: StorageModel?) {
        if (storage == null) {
            setPointLayoutEnabled(false)
            return
        }
        //기존의 보관함을 수정하는 경우

        setToolbarTitle(R.string.title_edit_storage) //툴바 타이틀명 변경

        //추가버튼 '저장'으로 이름 변경
        binding.saveBtn.setText(R.string.save)

        if (storage.imgUrl == null) {
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
                    pickPhotoHelper.photoUri?.let {
                        startCropImage(it, Uri.fromFile(tempCropImage))
                    }
                }
                REQUEST_PICK_FROM_ALBUM -> {
                    data?.data?.let { uri ->
                        startCropImage(uri, Uri.fromFile(tempCropImage))
                    }
                }

                UCrop.REQUEST_CROP -> {
                    data?.let {
                        val resultUri = UCrop.getOutput(it)
                        resultUri?.let { uri ->
//                            val bitmap = pickPhotoHelper.getBitmap(requireContext(), uri)
//                            binding.imgView.setImageBitmap(bitmap)
//                            FileUtil.saveBitmapToFile(bitmap, uri.toFile())
                            binding.imgView.loadWithNoCache(uri, bitmapListener = { bitmap ->
                                viewModel.bitmap = bitmap
                                Unit
                            })
                            isPhotoLoaded = true
                        }
                    }
                }

                REQUEST_PICK_FROM_STORAGE -> {
                    data?.getStringExtra("filePath")?.let {
                        binding.imgView.loadFilePath(it)
                        viewModel.imagePath = it
                        isPhotoLoaded = true
                    }
                }
            }

            if (isPhotoLoaded)
                setPointLayoutEnabled(isPhotoLoaded)
        } else {
            // 사진을 불러오는작업을 취소하는 경우 (이 경우 사진을 고르지 않은 상태이다)
            logE("사진 불러오기 취소")

        }
    }

    private fun startCropImage(uri: Uri, destinationUri: Uri) {
        val options = UCrop.Options()
        options.withAspectRatio(1f, 1f)
        options.setCropFrameStrokeWidth(5)
        options.setCropFrameColor(ContextCompat.getColor(requireContext(), R.color.colorAccent))
        options.setShowCropGrid(false)
        options.setActiveControlsWidgetColor(
            ContextCompat.getColor(requireContext(), R.color.colorAccent)
        )
        options.setRootViewBackgroundColor(
            ContextCompat.getColor(requireContext(), R.color.colorDimmedLayer)
        )
        options.setDimmedLayerColor(
            ContextCompat.getColor(
                requireContext(),
                R.color.colorDimmedLayer
            )
        )

        UCrop.of(uri, destinationUri)
            .withOptions(options)
            .start(requireContext(), this)

    }

    /**현재 고른 사진을 지운다 */
    private fun clearPhoto() {
    }


    private fun save() {
        val name = binding.storageName.text.toString()
        val point = getRelativeCoordinate()
        val memo = binding.storageMemo.text.toString()

        viewModel.saveStorageModel(name, point, memo)

    }

    /**포인터가 화면에 표시되어있는경우 상대좌표 반환*/
    private fun getRelativeCoordinate(): Pair<Float, Float>? {
        if (binding.pointer.visibility != View.VISIBLE)
            return null

        val w = binding.pointLayout.width
        val h = binding.pointLayout.height
        val x = binding.pointer.x + binding.pointer.pivotX
        val y = binding.pointer.y + binding.pointer.pivotY

        return Pair(x / w, y / h)
    }


    private fun takePhoto() {
        pickPhotoHelper.dispatchTakePictureIntentIfAvailable(fragment = this)
    }


    private fun pickFromAlbum() {
        pickPhotoHelper.startPickFromAlbumActivity(fragment = this)
    }


    private fun pickFromOtherStorage() {
//        pickPhotoHelper.startPickFromOtherStorage(fragment = this)
//        ActivityUtil.start(requireContext(), PickPhotoFromSpecificDirActivity::class.java)
        PickPhotoFromSpecificDirActivity.start(this, null)
    }


    private fun setPointLayoutEnabled(enable: Boolean) {
        setPointLayoutEnabled(enable, 0.5f, 0.5f)
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setPointLayoutEnabled(enable: Boolean, rx: Float?, ry: Float?) {
        if (enable) {
            binding.addPhotoBtnGroup.visibility = View.GONE
            binding.imgView.visibility = View.VISIBLE
            binding.photoMenuLayout.visibility = View.VISIBLE
            binding.removeBtn.visibility = View.VISIBLE
            binding.pointer.visibility = View.VISIBLE
            binding.pointLayout.setOnTouchListener { v, event ->
//                Log.i("point", "${event.x} , ${event.y}, ${event.action}")
                PointerUtil.movePointer(binding.pointer, binding.pointLayout, event.x, event.y)
                when (event.action) {
                    MotionEvent.ACTION_DOWN -> v.parent.requestDisallowInterceptTouchEvent(true)
                    MotionEvent.ACTION_UP -> {
                        v.parent.requestDisallowInterceptTouchEvent(false)
                        logI(getRelativeCoordinate().toString())
                    }
                }

                v.onTouchEvent(event)
            }
            binding.imgView.setOnMeasureListener(object : SquareImageView.OnMeasureListener {
                override fun measured(width: Int, height: Int) {
                    PointerUtil.movePointerByRelative(binding.pointer, width, height, rx, ry)
                    binding.imgView.setOnMeasureListener(null)
                }
            })

        } else {
            binding.addPhotoBtnGroup.visibility = View.VISIBLE
            binding.imgView.visibility = View.INVISIBLE
            binding.removeBtn.visibility = View.INVISIBLE
            binding.photoMenuLayout.visibility = View.GONE
            binding.pointer.visibility = View.GONE
            binding.pointLayout.setOnTouchListener(null)

        }

    }


    override fun onDestroy() {
        tempCropImage.delete()
        FileUtil.deleteTempImgFile(requireContext())
        super.onDestroy()
    }
}
