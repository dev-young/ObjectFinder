package io.ymsoft.objectfinder.ui.detail

import android.animation.AnimatorInflater
import android.graphics.Color
import android.os.Bundle
import android.view.*
import android.view.inputmethod.EditorInfo
import android.widget.ScrollView
import android.widget.TextView.OnEditorActionListener
import androidx.activity.addCallback
import androidx.appcompat.app.AlertDialog
import androidx.core.view.doOnPreDraw
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.transition.MaterialContainerTransform
import io.ymsoft.objectfinder.R
import io.ymsoft.objectfinder.common.OnModelClickListener
import io.ymsoft.objectfinder.data.ObjectModel
import io.ymsoft.objectfinder.data.StorageModel
import io.ymsoft.objectfinder.databinding.FragmentStorageDetailBinding
import io.ymsoft.objectfinder.util.*
import io.ymsoft.objectfinder.util.CheckableChipGroupHelper.OnCheckableChangeListener
import io.ymsoft.objectfinder.util.CheckableChipGroupHelper.OnCheckedCounterChangeListener
import timber.log.Timber

class StorageDetailFragment : Fragment() {

    private val viewModel: StorageDetailViewModel by viewModels()
    private lateinit var binding: FragmentStorageDetailBinding
    private val args by navArgs<StorageDetailFragmentArgs>()

    private val chipGroupHelper by lazy {
        CheckableChipGroupHelper<ObjectModel>().apply {
            chipClickListener = object :
                OnModelClickListener<ObjectModel> {
                override fun onItemClick(model: ObjectModel) {
                    binding.inputObject.setText(model.objName)
                    binding.inputObject.setSelection(model.objName.length)
                }
            }

            checkableChangeListener = object : OnCheckableChangeListener {
                override fun onChanged(checkable: Boolean) {
                    changeChipGroupCheckMode(checkable)
                }
            }

            checkedCounterChangeListener = object : OnCheckedCounterChangeListener {
                override fun onChanged(count: Int, total: Int) {
                    binding.checkedCounter.text = count.toString()
                    binding.checkAllCheckBox.isChecked = (count == total)
                    if (count == 0) {
                        binding.cancelBtn.visibility = View.VISIBLE
                        binding.deleteBtn.visibility = View.GONE
                        binding.moveBtn.visibility = View.GONE
                    } else {
//                        binding.cancelBtn.visibility = View.GONE
                        binding.deleteBtn.visibility = View.VISIBLE
                        binding.moveBtn.visibility = View.VISIBLE
                    }
                }
            }
        }
    }
    private val storageListDialog by lazy {
        StorageListBottomSheetFragment {
            moveAnotherStorage(it.id!!)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (args.hasSharedElement) {
            sharedElementEnterTransition = MaterialContainerTransform().apply {
                drawingViewId = R.id.nav_host_fragment
                duration = resources.getInteger(R.integer.default_transition_duration).toLong()
                scrimColor = Color.TRANSPARENT
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentStorageDetailBinding.inflate(inflater, container, false)
        chipGroupHelper.chipGroup = binding.chipGroup
        //버튼 리스너 추가
        binding.addBtn.setOnClickListener { addObject() }
        binding.checkAllBtn.setOnClickListener {
            binding.checkAllCheckBox.toggle()
            chipGroupHelper.checkAllChips(binding.checkAllCheckBox.isChecked)
        }
        binding.moveBtn.setOnSingleClickListener { showStorageList() }
        binding.deleteBtn.setOnSingleClickListener { viewModel.deleteObjects(chipGroupHelper.getCheckedList()) }
        binding.cancelBtn.setOnSingleClickListener { chipGroupHelper.setCheckable(false) }

        binding.inputObject.setOnEditorActionListener(OnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                addObject()
                return@OnEditorActionListener true
            }
            false
        })

        binding.inputObject.apply {
            setOnFocusChangeListener { v, hasFocus ->
                if (hasFocus) {
                    postDelayed({ binding.scrollView.fullScroll(ScrollView.FOCUS_DOWN) }, 200)
                }
            }
        }

        args.storageModel.let {
            updateUI(it)
            it.id?.let { id ->
                viewModel.startObserve(id)
            }
        }

        viewModel.storageModel.observe(viewLifecycleOwner, Observer(this::updateUI))
        viewModel.objectModels.observe(viewLifecycleOwner, Observer(this::updateObjectList))
        viewModel.toastMessage.observe(viewLifecycleOwner, Observer(context::makeToast))

        changeChipGroupCheckMode(false)


        setHasOptionsMenu(true)

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, onBackPressed = {
            if (binding.checkActionMenu.visibility == View.VISIBLE) {
                chipGroupHelper.setCheckable(false)
            } else {
                findNavController().navigateUp()
            }
        })

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

//        postponeEnterTransition()
//        view.doOnPreDraw { startPostponedEnterTransition() }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setAppBarVisible(top = true)
    }


    private fun updateUI(model: StorageModel?) {
        if (model == null) return
//        SharedViewUtil.setTransitionName(binding, model)

        if (model.imgUrl.isNullOrBlank()) {
            binding.imageLayout.visibility = View.GONE
//            binding.root.doOnPreDraw { startPostponedEnterTransition() }
        } else {
//            postponeEnterTransition()
            binding.imageLayout.visibility = View.VISIBLE
            binding.imgView.loadFilePath(model.imgUrl) {
//                startPostponedEnterTransition()
            }
        }

        model.name?.apply {
            if (isNotEmpty())
                setToolbarTitle(this)
            else
                setToolbarTitle(R.string.storage_detail)
        }

        binding.imgView.doOnPreDraw {
            PointerUtil.movePointerByRelative(
                binding.pointer,
                it.width,
                it.height,
                model.x,
                model.y
            )

            AnimatorInflater.loadAnimator(requireContext(), R.animator.cross_hair).apply {
                setTarget(binding.pointer)
                start()
            }
        }

    }

    private fun updateObjectList(list: List<ObjectModel>?) {
        if (list == null) return
        chipGroupHelper.setChipGroups(list)


        if (list.isEmpty()) binding.emptyMessage.visibility = View.VISIBLE
        else binding.emptyMessage.visibility = View.GONE
    }

    private fun addObject() {
        viewModel.addNewObject(binding.inputObject.text.toString())
        binding.inputObject.setText("")
        binding.scrollView.apply { postDelayed({ this.fullScroll(ScrollView.FOCUS_DOWN) }, 500) }
    }

    private fun moveAnotherStorage(targetStorageId: Long) {
        viewModel.moveStorage(chipGroupHelper.getCheckedList(), targetStorageId)
    }

    private fun showStorageList() {
        storageListDialog.storageList = viewModel.getCurrentStorageList()
        storageListDialog.show(childFragmentManager, storageListDialog.tag)
    }

    /**체크모드에 따라 하단의 메뉴를 변경한다. */
    private fun changeChipGroupCheckMode(checkable: Boolean) {
        //체크모드 변경시 키보드를 숨긴다
//        hideKeyboard()

        if (checkable) {
            binding.addLayout.visibility = View.GONE
            binding.checkActionMenu.visibility = View.VISIBLE
        } else {
            binding.addLayout.visibility = View.VISIBLE
            binding.checkActionMenu.visibility = View.GONE
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_edit -> {
                startEdit()
                true
            }
            R.id.action_remove -> {
                showRemoveCheckDialog()
                true
            }
            else -> false
        }
    }

    private fun removeStorage() {
        viewModel.removeStorage()
        findNavController().navigateUp()
    }

    private fun startEdit() {
        viewModel.storageModel.value?.let {
            val direction =
                StorageDetailFragmentDirections.actionNavStorageDetailToNavAddStorage(it)
            findNavController().navigate(direction)
        }
    }

    private fun showRemoveCheckDialog() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle(R.string.check_remove_title).setMessage(R.string.check_remove_message)
        builder.setPositiveButton(R.string.ok) { _, _ ->
            removeStorage()
        }
        builder.setNegativeButton(R.string.cancel) { dialog, _ ->
            dialog.dismiss()
        }

        val alertDialog: AlertDialog = builder.create()
        alertDialog.show()
    }


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.storage_detail_toolbar, menu)
    }

//    override fun postponeEnterTransition() {
//        if (args.hasSharedElement)
//            super.postponeEnterTransition()
//    }


}
