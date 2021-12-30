package com.example.openlibrary.ui.details.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.openlibrary.R
import com.example.openlibrary.data.model.Isbn
import com.example.openlibrary.databinding.ItemIsbnBinding
import com.example.openlibrary.utils.ImageLoader
import java.util.concurrent.Executor
import java.util.concurrent.ExecutorService

class IsbnAdapter(private val isbns: ArrayList<Isbn>) : RecyclerView.Adapter<IsbnAdapter.IsbnViewHolder>() {

    class IsbnViewHolder(private val binding: ItemIsbnBinding) : RecyclerView.ViewHolder(binding.root) {

        lateinit var executor : ExecutorService

        fun bind(isbn: Isbn) {
            binding.isbnTextView.text = isbn.isbn
            executor = ImageLoader().load(isbn.image, binding.isbnImageView, R.drawable.im_placeholder, R.drawable.im_placeholder)
        }

    }

    override fun onViewRecycled(holder: IsbnViewHolder) {
        super.onViewRecycled(holder)
        holder.executor.shutdownNow()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): IsbnViewHolder {
        val binding = ItemIsbnBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return IsbnViewHolder(binding)
    }

    override fun onBindViewHolder(holder: IsbnViewHolder, position: Int) {
        holder.bind(isbns[position])
    }

    override fun getItemCount(): Int {
        return isbns.size
    }

}