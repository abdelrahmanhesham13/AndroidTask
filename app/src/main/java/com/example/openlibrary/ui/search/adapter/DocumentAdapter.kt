package com.example.openlibrary.ui.search.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.openlibrary.data.model.Document
import com.example.openlibrary.databinding.ItemDocumnetBinding

class DocumentAdapter(
    val onDocumentClicked : (Document) -> Unit,
    private val onLoadMore : () -> Unit
) : RecyclerView.Adapter<DocumentAdapter.DocumentViewHolder>() {

    val mDocuments = ArrayList<Document>()

    inner class DocumentViewHolder(private val binding: ItemDocumnetBinding) : RecyclerView.ViewHolder(binding.root), View.OnClickListener {

        init {
            binding.root.setOnClickListener(this)
        }

        fun bind(document: Document) {
            binding.titleTextView.text = document.title
            binding.authorsTextView.text = document.getAuthorsString()
        }

        override fun onClick(v: View?) {
            onDocumentClicked.invoke(mDocuments[adapterPosition])
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DocumentViewHolder {
        val binding = ItemDocumnetBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return DocumentViewHolder(binding)
    }

    override fun onBindViewHolder(holder: DocumentViewHolder, position: Int) {
        holder.bind(mDocuments[position])

        if (position == mDocuments.size - 1) {
            onLoadMore.invoke()
        }
    }

    fun addDocuments(documents: ArrayList<Document>) {
        val size = mDocuments.size
        val newSize = documents.size
        mDocuments.addAll(documents)
        notifyItemRangeInserted(size , newSize)
    }



    override fun getItemCount(): Int {
        return mDocuments.size
    }

    fun clearDocuments() {
        val size = mDocuments.size
        mDocuments.clear()
        notifyItemRangeRemoved(0, size)
    }
}