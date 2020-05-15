package io.ymsoft.objectfinder.ui.main

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import io.ymsoft.objectfinder.R
import io.ymsoft.objectfinder.databinding.FragmentPositionDetailBinding
import io.ymsoft.objectfinder.view_model.PositionViewModel

class PositionDetailFragment : Fragment() {

    private lateinit var positionViewModel: PositionViewModel
    private lateinit var binding: FragmentPositionDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        positionViewModel = ViewModelProvider(this).get(PositionViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_position_detail, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding = FragmentPositionDetailBinding.bind(view)

        positionViewModel.getSelectedPosition()?.let {
            binding.text.text = it.toString()
        }

        super.onViewCreated(view, savedInstanceState)
    }

}
