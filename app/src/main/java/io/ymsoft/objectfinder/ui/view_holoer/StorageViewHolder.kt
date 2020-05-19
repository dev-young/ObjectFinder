package io.ymsoft.objectfinder.ui.view_holoer

import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import io.ymsoft.objectfinder.OnItemClickListener
import io.ymsoft.objectfinder.R
import io.ymsoft.objectfinder.databinding.ItemStorageBinding
import io.ymsoft.objectfinder.loadFilePath
import io.ymsoft.objectfinder.models.StorageModel
import io.ymsoft.objectfinder.utils.PointerUtil

class StorageViewHolder(itemView: View, clickListener: OnItemClickListener?) : RecyclerView.ViewHolder(itemView) {
    constructor(parent:ViewGroup, clickListener: OnItemClickListener?):
            this(LayoutInflater.from(parent.context).inflate(R.layout.item_storage, parent, false), clickListener)

    private val binding : ItemStorageBinding by lazy {ItemStorageBinding.bind(itemView)}

    init {
        binding.clickableLayout.setOnClickListener { clickListener?.onItemClick(adapterPosition) }
        binding.clickableLayout.setOnLongClickListener {
            clickListener?.onItemLongClick(adapterPosition)
            true
        }
    }

    fun onBind(model: StorageModel) {
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