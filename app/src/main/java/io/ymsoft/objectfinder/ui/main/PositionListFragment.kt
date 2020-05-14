package io.ymsoft.objectfinder.ui.main

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import io.ymsoft.objectfinder.R
import io.ymsoft.objectfinder.databinding.FragmentPositionListBinding
import io.ymsoft.objectfinder.view_model.PositionListViewModel

class PositionListFragment : Fragment() {

    private lateinit var binding : FragmentPositionListBinding

    private lateinit var positionListViewModel : PositionListViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        positionListViewModel = ViewModelProvider(this).get(PositionListViewModel::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_position_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding = FragmentPositionListBinding.bind(view)

        binding.btn.setOnClickListener {
            findNavController().navigate(R.id.action_navPositionList_to_navPositionDetail)
        }

        positionListViewModel.positionList.observe(viewLifecycleOwner, Observer {
            binding.text.text = it.toString()
        })

        super.onViewCreated(view, savedInstanceState)
    }


}
