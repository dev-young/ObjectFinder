package io.ymsoft.objectfinder.ui.search

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.core.view.doOnPreDraw
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import io.ymsoft.objectfinder.R
import io.ymsoft.objectfinder.data.StorageModel
import io.ymsoft.objectfinder.databinding.FragmentSearchBinding
import io.ymsoft.objectfinder.ui.MainActivity
import io.ymsoft.objectfinder.ui.storage_list.StorageListAdapter
import io.ymsoft.objectfinder.util.SharedViewUtil
import io.ymsoft.objectfinder.util.showKeyboard
import timber.log.Timber
import java.util.concurrent.TimeUnit


class SearchFragment : Fragment() {

    private lateinit var binding: FragmentSearchBinding

    private val viewModel : SearchViewModel by viewModels()
    private val storageListAdapter = StorageListAdapter()
        .apply {
            setClickListener { position, sharedViews ->
                showDetail(currentList[position], sharedViews)
            }
        }
    private var loadCounter = 0

    @SuppressLint("CheckResult")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Observable.just(0)
            .delay(300, TimeUnit.MILLISECONDS)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                activity.showKeyboard()
            }

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_search, container, false)
        binding.recyclerView.adapter = storageListAdapter

        viewModel.searchResult.observe(viewLifecycleOwner, Observer {
            updateItems(it)
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

            viewModel.doQuery(sv.query.toString())
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        postponeEnterTransition()
        binding.recyclerView.doOnPreDraw {
            startPostponedEnterTransition()
        }
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
            }, requireContext().resources.getInteger(R.integer.default_transition_duration).toLong())
    }

    private fun showDetail(model: StorageModel?, rootViews: List<View>) {
        model?.let {
            val direction = SearchFragmentDirections.actionNavSearchToNavStorageDetail(it)
            findNavController().navigate(direction, SharedViewUtil.makeStorageTransition(rootViews))
        }
    }

}
