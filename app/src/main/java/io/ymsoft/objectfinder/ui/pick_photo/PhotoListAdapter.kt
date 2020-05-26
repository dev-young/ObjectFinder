package io.ymsoft.objectfinder.ui.pick_photo

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import io.ymsoft.objectfinder.R
import io.ymsoft.objectfinder.databinding.ItemPhotoBinding
import io.ymsoft.objectfinder.util.loadFilePath

class PhotoListAdapter(private var itemClickListener : (filePath: String) -> Unit?) : RecyclerView.Adapter<PhotoListAdapter.PhotoViewHolder>() {
    var photoList = arrayListOf<String>()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotoViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_photo, parent, false)
        val binding = ItemPhotoBinding.bind(view)
        val holder = PhotoViewHolder(binding)
        binding.clickableLayout.setOnClickListener { itemClickListener.invoke(photoList[holder.adapterPosition]) }
        return holder
    }

    override fun getItemCount(): Int {
        return photoList.size
    }

    override fun onBindViewHolder(holder: PhotoViewHolder, position: Int) {
        holder.binding.imgView.loadFilePath(photoList[position])
    }

    class PhotoViewHolder(val binding: ItemPhotoBinding) : RecyclerView.ViewHolder(binding.root)
}