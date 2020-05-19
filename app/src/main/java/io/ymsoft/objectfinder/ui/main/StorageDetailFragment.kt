package io.ymsoft.objectfinder.ui.main

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import io.ymsoft.objectfinder.*
import io.ymsoft.objectfinder.databinding.FragmentStorageDetailBinding
import io.ymsoft.objectfinder.models.ObjectModel
import io.ymsoft.objectfinder.models.StorageModel
import io.ymsoft.objectfinder.ui.custom.SquareImageView
import io.ymsoft.objectfinder.utils.CheckableChipGroupHelper
import io.ymsoft.objectfinder.utils.CheckableChipGroupHelper.OnCheckableChangeListener
import io.ymsoft.objectfinder.utils.CheckableChipGroupHelper.OnCheckedCounterChangeListener
import io.ymsoft.objectfinder.utils.PointerUtil
import io.ymsoft.objectfinder.view_model.StorageViewModel

class StorageDetailFragment : Fragment() {

    private lateinit var viewModel: StorageViewModel
    private lateinit var binding: FragmentStorageDetailBinding

    private val chipGroupHelper by lazy {
        CheckableChipGroupHelper<ObjectModel>(binding.chipGroup).apply {
            chipClickListener = object : OnModelClickListener<ObjectModel> {
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
                    if(count == 0){
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel = ViewModelProvider(this).get(StorageViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_storage_detail, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding = FragmentStorageDetailBinding.bind(view)

        //버튼 리스너 추가
        binding.addBtn.setOnClickListener { addObject() }
        binding.checkAllBtn.setOnClickListener {
            binding.checkAllCheckBox.toggle()
            chipGroupHelper.checkAllChips(binding.checkAllCheckBox.isChecked)
        }
        binding.moveBtn.setOnClickListener { context.makeToast(R.string.error_this_func_is_developing) }
        binding.deleteBtn.setOnClickListener { viewModel.delete(chipGroupHelper.getCheckedList()) }
        binding.cancelBtn.setOnClickListener { chipGroupHelper.setCheckable(false) }

        viewModel.getSelectedStorage().observe(viewLifecycleOwner, Observer(this::updateUI))
        viewModel.getObjectList().observe(viewLifecycleOwner, Observer(chipGroupHelper::setChipGroups))
        viewModel.toastMsg.observe(viewLifecycleOwner, Observer(context::makeToast))

        super.onViewCreated(view, savedInstanceState)
    }

    private fun addObject() {
        viewModel.addNewObject(binding.inputObject.text.toString())
        binding.inputObject.setText("")
    }

    @SuppressLint("CheckResult")
    private fun updateUI(storageModel: StorageModel) {

        if (storageModel.imgUrl.isNullOrBlank()){
            binding.imgView.visibility = View.GONE
        } else {
            binding.imgView.visibility = View.VISIBLE
            binding.imgView.loadFilePath(storageModel.imgUrl)
        }

        storageModel.name.apply {
            if(!isNullOrBlank())
                (activity as AppCompatActivity).supportActionBar?.title = this
        }

        binding.imgView.setOnMeasureListener(object : SquareImageView.OnMeasureListener{
            override fun measured(width: Int, height: Int) {
                PointerUtil.movePointerByRelative(binding.pointer, width, height, storageModel.x, storageModel.y)
            }
        })

        changeChipGroupCheckMode(false)

    }

    /**체크모드에 따라 하단의 메뉴를 변경한다. */
    private fun changeChipGroupCheckMode(checkable: Boolean){
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
