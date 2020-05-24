package io.ymsoft.objectfinder.ui.storage_list

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import io.ymsoft.objectfinder.common.OnItemClickListener
import io.ymsoft.objectfinder.common.OnItemLongClickListener
import io.ymsoft.objectfinder.data.StorageModel
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
    var longClickListener : OnItemLongClickListener? = null


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StorageViewHolder {
        return StorageViewHolder(parent, clickListener, longClickListener)
    }

    override fun onBindViewHolder(holder: StorageViewHolder, storage: Int) {
        holder.onBind(getItem(storage))
    }

}