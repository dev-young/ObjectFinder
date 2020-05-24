package io.ymsoft.objectfinder.ui.search

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
import io.ymsoft.objectfinder.common.OnItemClickListener
import io.ymsoft.objectfinder.R
import io.ymsoft.objectfinder.data.StorageModel
import io.ymsoft.objectfinder.databinding.FragmentSearchBinding
import io.ymsoft.objectfinder.ui.MainActivity
import io.ymsoft.objectfinder.ui.storage_list.StorageListAdapter

class SearchFragment : Fragment() {

    private lateinit var binding: FragmentSearchBinding

    private val viewModel : SearchViewModel by viewModels()
    private val adapter = StorageListAdapter()
        .apply {
            clickListener = object : OnItemClickListener{
                override fun onItemClick(position: Int) {
                    showDetail(currentList[position])
                }
            }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_search, container, false)
        binding.recyclerView.adapter = adapter

        viewModel.searchResult.observe(viewLifecycleOwner, Observer {
            adapter.submitList(it)
            if(it.isNotEmpty()){
                binding.emptyMessage.visibility = View.GONE
            } else {
                binding.emptyMessage.visibility = View.VISIBLE
            }
        })

        viewModel.isEmpty.observe(viewLifecycleOwner, Observer {isEmpty ->
            if(isEmpty){
                binding.emptyMessage.visibility = View.VISIBLE
            } else {
                binding.emptyMessage.visibility = View.GONE
            }
        })

        if(activity is MainActivity){
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

    private fun showDetail(model: StorageModel?) {
        model?.let {
            val direction = SearchFragmentDirections.actionNavSearchToNavStorageDetail(it)
            findNavController().navigate(direction)
        }
    }

}
