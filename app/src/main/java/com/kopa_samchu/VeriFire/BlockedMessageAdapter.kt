package com.kopa_samchu.VeriFire

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.chip.Chip

class BlockedMessageAdapter(private val listener: OnMessageClickListener) :
    ListAdapter<BlockedMessage, BlockedMessageAdapter.MessageViewHolder>(MessagesComparator()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
        return MessageViewHolder.create(parent)
    }

    override fun onBindViewHolder(holder: MessageViewHolder, position: Int) {
        val current = getItem(position)
        holder.bind(current, listener)
    }

    class MessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val senderTextView: TextView = itemView.findViewById(R.id.textViewSender)
        private val messageTextView: TextView = itemView.findViewById(R.id.textViewMessageSnippet)
        private val spamTypeChip: Chip = itemView.findViewById(R.id.chipSpamType)
        private val deleteButton: ImageButton = itemView.findViewById(R.id.buttonDelete)

        fun bind(message: BlockedMessage, listener: OnMessageClickListener) {
            senderTextView.text = message.sender
            messageTextView.text = message.messageBody
            spamTypeChip.text = message.spamType
            deleteButton.setOnClickListener {
                listener.onDeleteClicked(message)
            }
            itemView.setOnClickListener {
                listener.onMessageClicked(message)
            }
        }

        companion object {
            fun create(parent: ViewGroup): MessageViewHolder {
                val view: View = LayoutInflater.from(parent.context)
                    .inflate(R.layout.list_item_blocked_message, parent, false)
                return MessageViewHolder(view)
            }
        }
    }
    class MessagesComparator : DiffUtil.ItemCallback<BlockedMessage>() {
        override fun areItemsTheSame(oldItem: BlockedMessage, newItem: BlockedMessage): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: BlockedMessage, newItem: BlockedMessage): Boolean {
            return oldItem == newItem
        }
    }
}

