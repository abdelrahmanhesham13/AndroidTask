package com.example.openlibrary.ui.details.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.openlibrary.databinding.ItemAuthorBinding
import com.example.openlibrary.databinding.ItemIsbnBinding

class AuthorAdapter(private val authors: ArrayList<String>, val onAuthorClicked: (author: String) -> Unit) : RecyclerView.Adapter<AuthorAdapter.AuthorViewHolder>() {

    inner class AuthorViewHolder(private val binding: ItemAuthorBinding) : RecyclerView.ViewHolder(binding.root), View.OnClickListener {

        init {
            binding.root.setOnClickListener(this)
        }

        fun bind(author: String) {
            binding.authorTextView.text = author
        }

        override fun onClick(v: View?) {
            onAuthorClicked.invoke(authors[adapterPosition])
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AuthorViewHolder {
        val binding = ItemAuthorBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return AuthorViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AuthorViewHolder, position: Int) {
        holder.bind(authors[position])
    }

    override fun getItemCount(): Int {
        return authors.size
    }
}