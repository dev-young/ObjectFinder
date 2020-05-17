package io.ymsoft.objectfinder.ui.main

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.chip.Chip
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import io.ymsoft.objectfinder.R
import io.ymsoft.objectfinder.databinding.FragmentPositionDetailBinding
import io.ymsoft.objectfinder.loadFilePath
import io.ymsoft.objectfinder.makeToast
import io.ymsoft.objectfinder.models.ObjectModel
import io.ymsoft.objectfinder.models.PositionModel
import io.ymsoft.objectfinder.utils.PointerUtil
import io.ymsoft.objectfinder.view_model.PositionViewModel
import java.util.concurrent.TimeUnit

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
        viewModel.toastMsg.observe(viewLifecycleOwner, Observer(context::makeToast))




        super.onViewCreated(view, savedInstanceState)
    }

    private fun addObject() {
        viewModel.addNewObject(binding.inputObject.text.toString())
    }

    @SuppressLint("CheckResult")
    private fun updateUI(positionModel: PositionModel) {
//        binding.text.text = positionModel.toString()

        if (positionModel.imgUrl.isNullOrBlank()){
            binding.imgView.visibility = View.GONE
        } else {
            binding.imgView.visibility = View.VISIBLE
            binding.imgView.loadFilePath(positionModel.imgUrl)
        }

        positionModel.name.apply {
            if(!isNullOrBlank())
                (activity as AppCompatActivity).supportActionBar?.title = this
        }

        if(positionModel.x != null && positionModel.y != null){
            // TODO: 2020-05-17 딜레이를 사용하는 방식이 아니라 뷰가 그려진 후 포인터를 그리도록 수정해야함
            Observable.just(Pair(positionModel.x!!, positionModel.y!!))
                .delay(50, TimeUnit.MILLISECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe{
                    PointerUtil.movePointerByRelative(binding.pointer, binding.imgView, it.first, it.second)
                    binding.pointer.visibility = View.VISIBLE
                }

        } else {
            binding.pointer.visibility = View.INVISIBLE
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
