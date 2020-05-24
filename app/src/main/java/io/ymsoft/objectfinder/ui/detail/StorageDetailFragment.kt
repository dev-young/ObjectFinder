package io.ymsoft.objectfinder.ui.detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ScrollView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.navArgs
import io.ymsoft.objectfinder.R
import io.ymsoft.objectfinder.common.OnModelClickListener
import io.ymsoft.objectfinder.data.ObjectModel
import io.ymsoft.objectfinder.data.StorageModel
import io.ymsoft.objectfinder.databinding.FragmentStorageDetailBinding
import io.ymsoft.objectfinder.util.*
import io.ymsoft.objectfinder.util.CheckableChipGroupHelper.OnCheckableChangeListener
import io.ymsoft.objectfinder.util.CheckableChipGroupHelper.OnCheckedCounterChangeListener
import io.ymsoft.objectfinder.view_custom.SquareImageView

class StorageDetailFragment : Fragment() {

    private val viewModel: StorageDetailViewModel by viewModels()
    private lateinit var binding: FragmentStorageDetailBinding
    private val args by navArgs<StorageDetailFragmentArgs>()

    private val chipGroupHelper by lazy {
        CheckableChipGroupHelper<ObjectModel>(binding.chipGroup).apply {
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
                    } else {
                        binding.cancelBtn.visibility = View.GONE
                        binding.deleteBtn.visibility = View.VISIBLE
                    }
                }
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentStorageDetailBinding.inflate(inflater, container, false)

        //버튼 리스너 추가
        binding.addBtn.setOnClickListener { addObject() }
        binding.checkAllBtn.setOnClickListener {
            binding.checkAllCheckBox.toggle()
            chipGroupHelper.checkAllChips(binding.checkAllCheckBox.isChecked)
        }
        binding.moveBtn.setOnClickListener { context.makeToast(R.string.error_this_func_is_developing) }
        binding.deleteBtn.setOnClickListener { viewModel.deleteObjects(chipGroupHelper.getCheckedList()) }
        binding.cancelBtn.setOnClickListener { chipGroupHelper.setCheckable(false) }

        args.storageModel?.let {
            updateUI(it)
            it.id?.let { id ->
                viewModel.startObserve(id)
            }
        }

        viewModel.storageModel.observe(viewLifecycleOwner, Observer(this::updateUI))
        viewModel.objectModels.observe(viewLifecycleOwner, Observer(this::updateObjectList))
        viewModel.toastMessage.observe(viewLifecycleOwner, Observer(context::makeToast))

        changeChipGroupCheckMode(false)

        return binding.root
    }

    private fun updateUI(storageModel: StorageModel?) {
        if (storageModel == null) return

        if (storageModel.imgUrl.isNullOrBlank()) {
            binding.imageLayout.visibility = View.GONE
        } else {
            binding.imageLayout.visibility = View.VISIBLE
            binding.imgView.loadFilePath(storageModel.imgUrl)
        }

        storageModel.name.apply {
            if (!isNullOrBlank())
                (activity as AppCompatActivity).supportActionBar?.title = this
        }

        binding.imgView.setOnMeasureListener(object : SquareImageView.OnMeasureListener {
            override fun measured(width: Int, height: Int) {
                PointerUtil.movePointerByRelative(
                    binding.pointer,
                    width,
                    height,
                    storageModel.x,
                    storageModel.y
                )
            }
        })

    }

    private fun updateObjectList(list: List<ObjectModel>?) {
        if (list == null) return

        chipGroupHelper.setChipGroups(list)
        if (binding.chipGroup.childCount != 0)
            binding.scrollView.apply { post { this.fullScroll(ScrollView.FOCUS_DOWN) } }

        if (list.isEmpty()) binding.emptyMessage.visibility = View.VISIBLE
        else binding.emptyMessage.visibility = View.GONE
    }

    private fun addObject() {
        viewModel.addNewObject(binding.inputObject.text.toString())
        binding.inputObject.setText("")
        binding.scrollView.apply { post { this.fullScroll(ScrollView.FOCUS_DOWN) } }
    }

    /**체크모드에 따라 하단의 메뉴를 변경한다. */
    private fun changeChipGroupCheckMode(checkable: Boolean) {
        //체크모드 변경시 키보드를 숨긴다
        hideKeyboard()

        if (checkable) {
            binding.addLayout.visibility = View.GONE
            binding.checkActionMenu.visibility = View.VISIBLE
        } else {
            binding.addLayout.visibility = View.VISIBLE
            binding.checkActionMenu.visibility = View.GONE
        }
    }

}
