package com.kopa_samchu.VeriFire

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView

class AllowlistAdapter(private val onDelete: (AllowlistItem) -> Unit) :
    ListAdapter<AllowlistItem, AllowlistAdapter.ViewHolder>(AllowlistComparator()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.list_item_management, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val current = getItem(position)
        holder.bind(current, onDelete)
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val valueTextView: TextView = itemView.findViewById(R.id.textViewValue)
        private val deleteButton: ImageButton = itemView.findViewById(R.id.buttonDeleteItem)

        fun bind(item: AllowlistItem, onDelete: (AllowlistItem) -> Unit) {
            valueTextView.text = item.phoneNumber
            deleteButton.setOnClickListener { onDelete(item) }
        }
    }

    class AllowlistComparator : DiffUtil.ItemCallback<AllowlistItem>() {
        override fun areItemsTheSame(oldItem: AllowlistItem, newItem: AllowlistItem) = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: AllowlistItem, newItem: AllowlistItem) = oldItem == newItem
    }
}
    