package io.ymsoft.objectfinder.ui.view_holoer

import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import io.ymsoft.objectfinder.R
import io.ymsoft.objectfinder.common.OnItemLongClickListener
import io.ymsoft.objectfinder.databinding.ItemStorageBinding
import io.ymsoft.objectfinder.util.loadFilePath
import io.ymsoft.objectfinder.data.StorageModel
import io.ymsoft.objectfinder.util.PointerUtil

class StorageViewHolder(parent: ViewGroup, clickListener: ((Int, View, View) -> Unit?)?, longClickListener: OnItemLongClickListener?) :
    RecyclerView.ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_storage, parent, false)) {


    private val binding : ItemStorageBinding by lazy {ItemStorageBinding.bind(itemView)}

    init {
        binding.clickableLayout.setOnClickListener { clickListener?.invoke(adapterPosition, binding.root, binding.imgView) }
        binding.clickableLayout.setOnLongClickListener {
            longClickListener?.onItemLongClick(adapterPosition)
            true
        }
    }

    fun onBind(model: StorageModel) {
        binding.root.transitionName = "root_${model.id}"
        binding.imgView.transitionName = "${model.id}"
        if (model.imgUrl.isNullOrBlank()){
            binding.photoLayout.visibility = GONE
        } else {
            binding.photoLayout.visibility = VISIBLE
            binding.imgView.loadFilePath(model.imgUrl)
            PointerUtil.movePointerByRelative(binding.pointer, binding.imgView, model.x, model.y)
        }

        if(model.name.isNullOrBlank()){
            binding.title.visibility = GONE
        } else {
            binding.title.text = model.name
            binding.title.visibility = VISIBLE
        }

        val c = binding.root.context
        if(model.objString.isNullOrBlank()){
            binding.objects.text = c.getString(R.string.empty_msg_storage)
            binding.objects.setTextColor(c.resources.getColor(android.R.color.darker_gray, null))
        } else{
            binding.objects.text = model.objString
            binding.objects.setTextColor(c.resources.getColor(R.color.colorAccent, null))
        }
    }
}