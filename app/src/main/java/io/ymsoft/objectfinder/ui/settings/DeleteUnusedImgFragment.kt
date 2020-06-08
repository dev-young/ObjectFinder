package io.ymsoft.objectfinder.ui.settings

import android.os.Bundle
import android.os.Environment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import io.ymsoft.objectfinder.databinding.FragmentDeleteUnusedImgBinding
import io.ymsoft.objectfinder.util.makeToast
import io.ymsoft.objectfinder.util.setOnSingleClickListener
import io.ymsoft.objectfinder.util.setVisible

class DeleteUnusedImgFragment : Fragment() {

    lateinit var binding: FragmentDeleteUnusedImgBinding
    private val viewModel by viewModels<DeleteUnusedImgViewModel>()
    private val listAdapter = CheckablePhotoListAdapter().apply {
        checkedChangeListener = {
            val size = it.size
            binding.checkAllCheckBox.isChecked = itemCount == size
            binding.checkedCounter.text = size.toString()
            if(it.isEmpty()){   //이런식으로 해줘야 애니메이션 효과가 이쁘게 나온다. (animateLayoutChanges 속성을 통한 애니메이션)
                binding.deleteBtn.isVisible = false
                binding.cancelBtn.isVisible = true
                binding.checkAllCheckBox.isChecked = false
            } else {
                binding.cancelBtn.isVisible = false
                binding.deleteBtn.isVisible = true
            }

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentDeleteUnusedImgBinding.inflate(inflater, container, false)
        binding.recyclerView.adapter = listAdapter
        binding.checkAllBtn.setOnClickListener {
            binding.checkAllCheckBox.toggle()
            listAdapter.checkAll(binding.checkAllCheckBox.isChecked)
        }
        binding.deleteBtn.setOnSingleClickListener {
            viewModel.removeImages(listAdapter.getCheckedImages())
        }
        binding.cancelBtn.setOnSingleClickListener { findNavController().navigateUp() }

        viewModel.isWorking.observe(viewLifecycleOwner, Observer(binding.progress::setVisible))
        viewModel.toastMessage.observe(viewLifecycleOwner, Observer(requireContext()::makeToast))
        viewModel.unusedFiles.observe(viewLifecycleOwner, Observer {
            listAdapter.photoList = it
            binding.emptyMessage.setVisible(it.isEmpty())
        })
        viewModel.deleteAllCompleteEvent.observe(viewLifecycleOwner, Observer {
            if(it){
                findNavController().navigateUp()
            }
        })

        val dir = requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        viewModel.loadUnusedImage(dir)


        return binding.root
    }


}