package com.dicoding.asclepius.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.dicoding.asclepius.data.local.entity.BookmarkEntity
import com.dicoding.asclepius.databinding.ItemBookmarkBinding


class BookmarkAdapter : ListAdapter<BookmarkEntity, BookmarkAdapter.MyViewHolder>(DIFF_CALLBACK) {

    private lateinit var onItemClickCallback: OnItemClickCallback

    fun setOnItemClickCallback(onItemClickCallback: OnItemClickCallback) {
        this.onItemClickCallback = onItemClickCallback
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding =
            ItemBookmarkBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item)

        holder.binding.bookmarkDelete.setOnClickListener {
            onItemClickCallback.removeItem(position)
            notifyItemRemoved(position)
        }
    }

    class MyViewHolder(val binding: ItemBookmarkBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: BookmarkEntity) {
            binding.bookmarkDesc.text = "${item.label}: ${item.score}"
            Glide.with(itemView.context)
                .load(item.image)
                .into(binding.bookmarkImage)
        }
    }

    interface OnItemClickCallback {
        fun removeItem(position: Int)
    }

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<BookmarkEntity>() {
            override fun areItemsTheSame(
                oldItem: BookmarkEntity, newItem: BookmarkEntity
            ): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(
                oldItem: BookmarkEntity, newItem: BookmarkEntity
            ): Boolean {
                return oldItem == newItem
            }
        }
    }
}