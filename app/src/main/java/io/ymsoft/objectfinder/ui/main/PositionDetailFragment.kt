package io.ymsoft.objectfinder.ui.main

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.chip.Chip
import io.ymsoft.objectfinder.R
import io.ymsoft.objectfinder.databinding.FragmentPositionDetailBinding
import io.ymsoft.objectfinder.models.ObjectModel
import io.ymsoft.objectfinder.models.PositionModel
import io.ymsoft.objectfinder.view_model.PositionViewModel

class PositionDetailFragment : Fragment() {

    private lateinit var viewModel: PositionViewModel
    private lateinit var binding: FragmentPositionDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel = ViewModelProvider(this).get(PositionViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_position_detail, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding = FragmentPositionDetailBinding.bind(view)

        binding.addBtn.setOnClickListener { addObject() }

        viewModel.getSelectedPosition().observe(viewLifecycleOwner, Observer(this::updateUI))
        viewModel.getObjectList().observe(viewLifecycleOwner, Observer(this::setChipGroups))





        super.onViewCreated(view, savedInstanceState)
    }

    private fun addObject() {
        viewModel.addNewObject(binding.inputObject.text.toString())
    }

    private fun updateUI(positionModel: PositionModel) {
        binding.text.text = positionModel.toString()

        if (positionModel.imgUrl.isNullOrBlank()){
            binding.imgView.visibility = View.GONE
        } else {
            binding.imgView.visibility = View.VISIBLE
        }

        positionModel.name?.let {
            (activity as AppCompatActivity).supportActionBar?.setTitle(it)
        }


    }

    private fun setChipGroups(objList : List<ObjectModel>) {
        binding.chipGroup.removeAllViews()
        val clickListener = View.OnClickListener {

        }

        val longClickListener = View.OnLongClickListener {
            val model = it.tag as ObjectModel
            viewModel.onObjectLongClicked(model)
            false
        }
        objList.forEach {
            val chip = Chip(context)
            chip.text = it.objName
            chip.tag = it
//            chip.isCheckable = true
            chip.setOnClickListener(clickListener)
            chip.setOnLongClickListener(longClickListener)
            binding.chipGroup.addView(chip)
        }

    }

}
