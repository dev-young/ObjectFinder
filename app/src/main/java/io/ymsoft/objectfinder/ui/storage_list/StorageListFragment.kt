package io.ymsoft.objectfinder.ui.storage_list

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import io.ymsoft.objectfinder.common.OnItemClickListener
import io.ymsoft.objectfinder.R
import io.ymsoft.objectfinder.databinding.FragmentStorageListBinding
import io.ymsoft.objectfinder.util.makeToast

class StorageListFragment : Fragment() {

    private lateinit var binding : FragmentStorageListBinding
    private val listViewModel : StorageListViewModel by viewModels()

    private val storageListAdapter = StorageListAdapter()
        .apply {
        clickListener = object :
            OnItemClickListener {
            override fun onItemClick(position: Int) {
                listViewModel.setSelectedStorage(currentList[position])
                showDetail()
            }

            override fun onItemLongClick(position: Int) {
                listViewModel.itemlongClicked(currentList[position])
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

        listViewModel.storageList.observe(viewLifecycleOwner, Observer {
            Log.i("", "StorageList Changed!")
            if (it.isEmpty()){
                binding.emptyMessage.visibility = View.VISIBLE
            } else {
                binding.emptyMessage.visibility = View.GONE
                storageListAdapter.submitList(it)
            }
        })

        listViewModel.toastMsg.observe(viewLifecycleOwner, Observer(context::makeToast))

        super.onViewCreated(view, savedInstanceState)
    }

    private fun showDetail() {
        findNavController().navigate(R.id.action_navStorageList_to_navStorageDetail)
    }


}
