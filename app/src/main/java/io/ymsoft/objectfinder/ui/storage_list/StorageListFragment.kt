package io.ymsoft.objectfinder.ui.storage_list

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.doOnPreDraw
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.FragmentNavigator
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import io.ymsoft.objectfinder.common.OnItemLongClickListener
import io.ymsoft.objectfinder.data.StorageModel
import io.ymsoft.objectfinder.databinding.FragmentStorageListBinding
import io.ymsoft.objectfinder.util.SharedViewUtil
import timber.log.Timber

class StorageListFragment : Fragment() {

    private lateinit var binding: FragmentStorageListBinding
    private val viewModel by viewModels<StorageModelsViewModel>()

    private val storageListAdapter = StorageListAdapter()
        .apply {
            setClickListener { position, sharedViews ->

                showDetail(currentList[position], sharedViews)
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

    private fun showDetail(model: StorageModel?, rootViews: List<View>) {
        model?.let {
            val direction = StorageListFragmentDirections.actionNavStorageListToNavStorageDetail(it)
            findNavController().navigate(direction, SharedViewUtil.makeStorageTransition(rootViews))
        }
    }


}
