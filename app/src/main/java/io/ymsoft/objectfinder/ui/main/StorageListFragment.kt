package io.ymsoft.objectfinder.ui.main

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import io.ymsoft.objectfinder.OnItemClickListener
import io.ymsoft.objectfinder.R
import io.ymsoft.objectfinder.databinding.FragmentStorageListBinding
import io.ymsoft.objectfinder.makeToast
import io.ymsoft.objectfinder.view_model.StorageViewModel

class StorageListFragment : Fragment() {

    private lateinit var binding : FragmentStorageListBinding
    private val viewModel : StorageViewModel by viewModels()

    private val storageListAdapter = StorageListAdapter().apply {
        clickListener = object : OnItemClickListener {
            override fun onItemClick(storage: Int) {
                viewModel.setSelectedStorage(currentList[storage])
                showDetail()
            }

            override fun onItemLongClick(storage: Int) {
                viewModel.itemlongClicked(currentList[storage])
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_storage_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding = FragmentStorageListBinding.bind(view)
        binding.recyclerView.adapter = storageListAdapter

        viewModel.storageList.observe(viewLifecycleOwner, Observer {
            Log.i("", "StorageList Changed!")
            if (it.isEmpty()){
                binding.emptyMessage.visibility = View.VISIBLE
            } else {
                binding.emptyMessage.visibility = View.GONE
                storageListAdapter.submitList(it)
            }
        })

        viewModel.toastMsg.observe(viewLifecycleOwner, Observer(context::makeToast))

        super.onViewCreated(view, savedInstanceState)
    }

    private fun showDetail() {
        findNavController().navigate(R.id.action_navStorageList_to_navStorageDetail)
    }


}
