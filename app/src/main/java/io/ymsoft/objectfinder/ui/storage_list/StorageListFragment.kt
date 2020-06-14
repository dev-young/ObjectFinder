package io.ymsoft.objectfinder.ui.storage_list

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.core.view.doOnPreDraw
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import io.ymsoft.objectfinder.R
import io.ymsoft.objectfinder.common.OnItemLongClickListener
import io.ymsoft.objectfinder.data.StorageModel
import io.ymsoft.objectfinder.databinding.FragmentStorageListBinding
import io.ymsoft.objectfinder.util.SharedViewUtil
import io.ymsoft.objectfinder.util.replaceBottomMenu
import io.ymsoft.objectfinder.util.setAppBarVisible
import io.ymsoft.objectfinder.util.showFabAnimation
import kotlinx.android.synthetic.main.fragment_storage_list.*
import timber.log.Timber
import java.lang.Exception

class StorageListFragment : Fragment() {

    private lateinit var binding: FragmentStorageListBinding
    private val viewModel by viewModels<StorageModelsViewModel>()
    private var loadCounter = 0

    private val storageListAdapter = StorageListAdapter()
        .apply {
            setClickListener { position, sharedViews ->

                showDetailWithSharedElements(currentList[position], sharedViews)
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

        viewModel.items.observe(viewLifecycleOwner, Observer(this::updateItems))

        viewModel.isEmpty.observe(viewLifecycleOwner, Observer {
            if (it) {
                binding.emptyMessage.visibility = View.VISIBLE
                showFabAnimation(true)
            } else {
                binding.emptyMessage.visibility = View.GONE
                showFabAnimation(false)
            }
        })

        postponeEnterTransition()
        binding.recyclerView.doOnPreDraw {
            startPostponedEnterTransition()
        }

        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        replaceBottomMenu(R.menu.main)
        if( findNavController().currentDestination?.id == R.id.navStorageList )
            setAppBarVisible(top = true, bottom = true)
    }

    /**뷰가 변경된 경우에는 딜레이를 주고 변경한다.
     * 이유: 공유요소 전환을 사용시 애니메이션이 끝난 뒤 리스트를 업데이트해야 애니메이션이 문제 없이 작동한다.*/
    private fun updateItems(models: List<StorageModel>?) {
        Timber.i("StorageList Changed!")
        if(loadCounter++ == 0)
            storageListAdapter.submitList(models)
        else
            binding.recyclerView.postDelayed({
                storageListAdapter.submitList(models)
            }, animationDuration)
    }
    private val animationDuration by lazy { requireContext().resources.getInteger(R.integer.default_transition_duration).toLong() }


    private fun showDetailWithSharedElements(model: StorageModel?, sharedViews: List<View>) {
        model?.let {
            val direction = StorageListFragmentDirections.actionNavStorageListToNavStorageDetail(it, true)
            try {
                findNavController().navigate(direction, SharedViewUtil.makeStorageTransition(sharedViews))
            } catch (e: Exception){}
        }
    }


}
