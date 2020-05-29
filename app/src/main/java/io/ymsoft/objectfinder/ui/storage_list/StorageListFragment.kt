package io.ymsoft.objectfinder.ui.storage_list

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.doOnPreDraw
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import io.ymsoft.objectfinder.common.OnItemLongClickListener
import io.ymsoft.objectfinder.data.StorageModel
import io.ymsoft.objectfinder.databinding.FragmentStorageListBinding
import timber.log.Timber

class StorageListFragment : Fragment() {

    private lateinit var binding: FragmentStorageListBinding
    private val viewModel by viewModels<StorageModelsViewModel>()

    private val storageListAdapter = StorageListAdapter()
        .apply {
            clickListener = object : (Int, View, View) -> Unit? {
                override fun invoke(p1: Int, p2: View, p3: View) {
                    showDetail(currentList[p1], p2, p3)
                }

            }

            longClickListener = object : OnItemLongClickListener {
                override fun onItemLongClick(position: Int) {
//                    currentList[position].id?.let { viewModel.deleteStorageModel(it) }
                }

            }
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentStorageListBinding.inflate(inflater, container, false)

        binding.recyclerView.adapter = storageListAdapter

        viewModel.items.observe(viewLifecycleOwner, Observer {
            Timber.i("StorageList Changed!")
            storageListAdapter.submitList(it)
        })

        viewModel.isEmpty.observe(viewLifecycleOwner, Observer {
            if (it) {
                Timber.e("비어있음")
                binding.emptyMessage.visibility = View.VISIBLE
            } else {
                Timber.i("보관함 갱신!")
                binding.emptyMessage.visibility = View.GONE
            }
        })

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        postponeEnterTransition()
        binding.recyclerView.doOnPreDraw {
            startPostponedEnterTransition()
        }
    }

    private fun showDetail(model: StorageModel?, rootView: View, sharedView: View) {
        model?.let {
            val extras = FragmentNavigatorExtras(
                rootView to rootView.transitionName,
                sharedView to sharedView.transitionName
            )
            val direction = StorageListFragmentDirections.actionNavStorageListToNavStorageDetail(it)
            findNavController().navigate(direction, extras)
        }
    }


}
