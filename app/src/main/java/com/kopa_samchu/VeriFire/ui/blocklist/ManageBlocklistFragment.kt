package com.kopa_samchu.VeriFire.ui.blocklist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.kopa_samchu.VeriFire.*

class ManageBlocklistFragment : Fragment() {

    private val viewModel: BlocklistViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_manage_blocklist, container, false)

        val recyclerView = root.findViewById<RecyclerView>(R.id.recyclerViewBlocklist)
        val adapter = BlocklistAdapter { item -> viewModel.delete(item) }
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(context)

        viewModel.allBlocklistItems.observe(viewLifecycleOwner) { items ->
            adapter.submitList(items)
        }

        val valueEditText = root.findViewById<EditText>(R.id.editTextValue)
        val radioGroup = root.findViewById<RadioGroup>(R.id.radioGroupType)
        val addButton = root.findViewById<Button>(R.id.buttonAdd)

        addButton.setOnClickListener {
            val value = valueEditText.text.toString().trim()
            if (value.isNotEmpty()) {
                val selectedTypeId = radioGroup.checkedRadioButtonId
                val type = if (selectedTypeId == R.id.radioKeyword) "keyword" else "number"
                viewModel.insert(BlocklistItem(value = value, type = type))
                valueEditText.text.clear()
            }
        }

        return root
    }
}
    