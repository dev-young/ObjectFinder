package io.ymsoft.objectfinder.ui.main

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import io.ymsoft.objectfinder.OnItemClickListener
import io.ymsoft.objectfinder.models.StorageModel
import io.ymsoft.objectfinder.ui.view_holoer.StorageViewHolder

class StorageListAdapter : ListAdapter<StorageModel, StorageViewHolder>(object : DiffUtil.ItemCallback<StorageModel>(){
    override fun areItemsTheSame(oldItem: StorageModel, newItem: StorageModel): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: StorageModel, newItem: StorageModel): Boolean {
        return oldItem.objString == newItem.objString
    }

}) {

    var clickListener : OnItemClickListener? = null


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StorageViewHolder {
        return StorageViewHolder(parent, clickListener)
    }

    override fun onBindViewHolder(holder: StorageViewHolder, storage: Int) {
        holder.onBind(getItem(storage))
    }

}