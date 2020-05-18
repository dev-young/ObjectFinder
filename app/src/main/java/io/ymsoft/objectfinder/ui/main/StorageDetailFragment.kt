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
import io.ymsoft.objectfinder.databinding.FragmentStorageDetailBinding
import io.ymsoft.objectfinder.loadFilePath
import io.ymsoft.objectfinder.makeToast
import io.ymsoft.objectfinder.models.ObjectModel
import io.ymsoft.objectfinder.models.StorageModel
import io.ymsoft.objectfinder.ui.custom.SquareImageView
import io.ymsoft.objectfinder.utils.PointerUtil
import io.ymsoft.objectfinder.view_model.StorageViewModel
import java.util.concurrent.TimeUnit

class StorageDetailFragment : Fragment() {

    private lateinit var viewModel: StorageViewModel
    private lateinit var binding: FragmentStorageDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel = ViewModelProvider(this).get(StorageViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_storage_detail, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding = FragmentStorageDetailBinding.bind(view)

        binding.addBtn.setOnClickListener { addObject() }

        viewModel.getSelectedStorage().observe(viewLifecycleOwner, Observer(this::updateUI))
        viewModel.getObjectList().observe(viewLifecycleOwner, Observer(this::setChipGroups))
        viewModel.toastMsg.observe(viewLifecycleOwner, Observer(context::makeToast))




        super.onViewCreated(view, savedInstanceState)
    }

    private fun addObject() {
        viewModel.addNewObject(binding.inputObject.text.toString())
    }

    @SuppressLint("CheckResult")
    private fun updateUI(storageModel: StorageModel) {
//        binding.text.text = storageModel.toString()

        if (storageModel.imgUrl.isNullOrBlank()){
            binding.imgView.visibility = View.GONE
        } else {
            binding.imgView.visibility = View.VISIBLE
            binding.imgView.loadFilePath(storageModel.imgUrl)
        }

        storageModel.name.apply {
            if(!isNullOrBlank())
                (activity as AppCompatActivity).supportActionBar?.title = this
        }

        binding.imgView.setOnMeasureListener(object : SquareImageView.OnMeasureListener{
            override fun measured(width: Int, height: Int) {
                PointerUtil.movePointerByRelative(binding.pointer, width, height, storageModel.x, storageModel.y)
            }
        })



    }

    @SuppressLint("CheckResult")
    private fun setChipGroups(objList : List<ObjectModel>) {
        val clickListener = View.OnClickListener {

        }

        val longClickListener = View.OnLongClickListener {
            val model = it.tag as ObjectModel
            viewModel.onObjectLongClicked(model)
            false
        }

//        Observable.just(objList)
//            .flatMap { Observable.fromIterable(it) }
//            .map {
//                val chip = Chip(context)
//                chip.text = it.objName
//                chip.tag = it
//                chip.setOnClickListener(clickListener)
//                chip.setOnLongClickListener(longClickListener)
//
//                chip
//            }
//            .subscribeOn(Schedulers.io())
//            .observeOn(AndroidSchedulers.mainThread())
//            .subscribe {
//                binding.chipGroup.addView(it)
//            }

        Observable.just(objList)
            .map {
                val chipList = arrayListOf<Chip>()
                objList.forEach {
                    val chip = Chip(context)
                    chip.text = it.objName
                    chip.tag = it
//                    chip.isCheckable = true
                    chip.setOnClickListener(clickListener)
                    chip.setOnLongClickListener(longClickListener)
                    chipList.add(chip)
                }
                chipList
            }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {list ->
                // TODO: 뷰가 갱신될때마다 한번 다 지우고 다시 넣는게 많은 작업을 필요로 한다는 생각이 든다
                // TODO: 리싸이클러 뷰를 사용해 칩을 넣던가 뷰모델에서 LiveData 가 아닌 Single 이나 일반 리스트를 사용해 로딩을 한 뒤 삭제 추가되는 Object를 LiveData 로 관리하자
                binding.chipGroup.removeAllViews()
                list.forEach {
                    binding.chipGroup.addView(it)
                }
            }
//        objList.forEach {
//            val chip = Chip(context)
//            chip.text = it.objName
//            chip.tag = it
////            chip.isCheckable = true
//            chip.setOnClickListener(clickListener)
//            chip.setOnLongClickListener(longClickListener)
//            binding.chipGroup.addView(chip)
//        }

    }

}
