package io.ymsoft.objectfinder.ui.view_holoer

import android.view.LayoutInflater
import android.view.View
import android.view.View.*
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import io.ymsoft.objectfinder.OnItemClickListener
import io.ymsoft.objectfinder.R
import io.ymsoft.objectfinder.databinding.ItemStorageBinding
import io.ymsoft.objectfinder.loadFilePath
import io.ymsoft.objectfinder.models.StorageModel

class StorageViewHolder(itemView: View, clickListener: OnItemClickListener?) : RecyclerView.ViewHolder(itemView) {
    constructor(parent:ViewGroup, clickListener: OnItemClickListener?):
            this(LayoutInflater.from(parent.context).inflate(R.layout.item_storage, parent, false), clickListener)

    private val binding : ItemStorageBinding by lazy {ItemStorageBinding.bind(itemView)}

    init {
        binding.clickableLayout.setOnClickListener { clickListener?.onItemClick(adapterPosition) }
        binding.clickableLayout.setOnLongClickListener {
            clickListener?.onItemLongClick(adapterPosition)
            false
        }
    }

    fun onBind(model: StorageModel) {
        if (model.imgUrl.isNullOrBlank()){
            binding.imgView.visibility = GONE
        } else {
            binding.imgView.visibility = VISIBLE
            binding.imgView.loadFilePath(model.imgUrl)
        }

        if(model.name.isNullOrBlank()){
            binding.title.visibility = GONE
        } else {
            binding.title.text = model.name
            binding.title.visibility = VISIBLE
        }
        binding.objects.text = model.objString
    }
}