package io.ymsoft.objectfinder.ui.main

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import io.ymsoft.objectfinder.OnItemClickListener
import io.ymsoft.objectfinder.R
import io.ymsoft.objectfinder.databinding.FragmentPositionListBinding
import io.ymsoft.objectfinder.makeToast
import io.ymsoft.objectfinder.view_model.PositionViewModel

class PositionListFragment : Fragment() {

    private lateinit var binding : FragmentPositionListBinding

    private lateinit var viewModel : PositionViewModel
    private val positionListAdapter = PositionListAdapter().apply {
        clickListener = object : OnItemClickListener {
            override fun onItemClick(position: Int) {
                viewModel.setSelectedPosition(currentList[position])
                showDetail()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel = ViewModelProvider(this).get(PositionViewModel::class.java)

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_position_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding = FragmentPositionListBinding.bind(view)
        binding.recyclerView.adapter = positionListAdapter

        viewModel.positionList.observe(viewLifecycleOwner, Observer {
            Log.i("", "PositionList Changed!")
            if (it.isEmpty()){
                binding.emptyMessage.visibility = View.VISIBLE
            } else {
                binding.emptyMessage.visibility = View.GONE
                positionListAdapter.submitList(it)
            }
        })

        viewModel.toastMsg.observe(viewLifecycleOwner, Observer(context::makeToast))

        super.onViewCreated(view, savedInstanceState)
    }

    private fun showDetail() {
        findNavController().navigate(R.id.action_navPositionList_to_navPositionDetail)
    }


}
