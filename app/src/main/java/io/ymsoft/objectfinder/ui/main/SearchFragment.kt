package io.ymsoft.objectfinder.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import io.ymsoft.objectfinder.OnItemClickListener
import io.ymsoft.objectfinder.R
import io.ymsoft.objectfinder.databinding.FragmentSearchBinding
import io.ymsoft.objectfinder.view_model.StorageViewModel
import io.ymsoft.objectfinder.view_model.SearchViewModel

class SearchFragment : Fragment() {

    private lateinit var binding: FragmentSearchBinding

    private val viewModel : SearchViewModel by viewModels()
    private val storageViewModel : StorageViewModel by viewModels()
    private val adapter = StorageListAdapter().apply {
        clickListener = object : OnItemClickListener {
            override fun onItemClick(storage: Int) {
                storageViewModel.setSelectedStorage(currentList[storage])
                findNavController().navigate(R.id.action_navSearch_to_navStorageDetail)
            }

            override fun onItemLongClick(storage: Int) {

            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_search, container, false)
        binding.recyclerView.adapter = adapter

        viewModel.getSearchResult().observe(viewLifecycleOwner, Observer {
            adapter.submitList(it)
            if(it.isNotEmpty()){
                binding.emptyMessage.visibility = View.GONE
            } else {
                binding.emptyMessage.visibility = View.VISIBLE
            }
        })

        if(activity is MainActivity){
            // TODO: 이런식으로 메인 엑티비티를 가져와서 하지 말고 변경된 텍스트를 ViewModel에 넣어서 사용해보는건 어떨까?
            val sv = (activity as MainActivity).searchView

            sv.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
                    return false
                }

                override fun onQueryTextChange(newText: String?): Boolean {
                    newText?.let { viewModel.doQuery(it) }
                    return false
                }

            })
        }

        return binding.root
    }

}
