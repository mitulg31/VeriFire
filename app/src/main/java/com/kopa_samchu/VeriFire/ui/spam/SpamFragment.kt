package com.kopa_samchu.VeriFire.ui.spam

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.kopa_samchu.VeriFire.BlockedMessage
import com.kopa_samchu.VeriFire.BlockedMessageAdapter
import com.kopa_samchu.VeriFire.BlockedMessagesViewModel
import com.kopa_samchu.VeriFire.OnMessageClickListener
import com.kopa_samchu.VeriFire.R
import com.kopa_samchu.VeriFire.SpamDetailActivity

class SpamFragment : Fragment(), OnMessageClickListener {

    private val viewModel: BlockedMessagesViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_spam, container, false)

        val recyclerView = root.findViewById<RecyclerView>(R.id.recyclerViewBlockedMessages)
        val emptyView = root.findViewById<TextView>(R.id.textViewEmptyState)
        val adapter = BlockedMessageAdapter(this)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(context)

        viewModel.allBlockedMessages.observe(viewLifecycleOwner) { messages ->
            messages?.let {
                adapter.submitList(it)
                emptyView.visibility = if (it.isEmpty()) View.VISIBLE else View.GONE
                recyclerView.visibility = if (it.isEmpty()) View.GONE else View.VISIBLE
            }
        }
        return root
    }

    override fun onDeleteClicked(message: BlockedMessage) {
        viewModel.delete(message)
        Toast.makeText(context, "Message deleted", Toast.LENGTH_SHORT).show()
    }

    override fun onMessageClicked(message: BlockedMessage) {
        val intent = Intent(activity, SpamDetailActivity::class.java).apply {
            putExtra("MESSAGE_DETAIL", message)
        }
        startActivity(intent)
    }
}
