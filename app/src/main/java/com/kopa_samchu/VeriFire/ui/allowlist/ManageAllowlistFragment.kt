package com.kopa_samchu.VeriFire.ui.allowlist

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.ContactsContract
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.kopa_samchu.VeriFire.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ManageAllowlistFragment : Fragment() {

    private val viewModel: AllowlistViewModel by viewModels()

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            if (isGranted) {
                syncContacts()
            } else {
                Toast.makeText(requireContext(), "Permission denied. Cannot sync contacts.", Toast.LENGTH_SHORT).show()
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_manage_allowlist, container, false)

        val recyclerView = root.findViewById<RecyclerView>(R.id.recyclerViewAllowlist)
        val adapter = AllowlistAdapter { item -> viewModel.delete(item) }
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(context)

        viewModel.allAllowlistItems.observe(viewLifecycleOwner) { items ->
            adapter.submitList(items)
        }

        val phoneEditText = root.findViewById<EditText>(R.id.editTextPhoneNumber)
        val addButton = root.findViewById<Button>(R.id.buttonAdd)
        val syncButton = root.findViewById<Button>(R.id.buttonSyncContacts)

        addButton.setOnClickListener {
            val phoneNumber = phoneEditText.text.toString().trim()
            if (phoneNumber.isNotEmpty()) {
                viewModel.insert(AllowlistItem(phoneNumber = phoneNumber))
                phoneEditText.text.clear()
            }
        }

        syncButton.setOnClickListener {
            checkPermissionAndSync()
        }

        return root
    }

    private fun checkPermissionAndSync() {
        when {
            ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.READ_CONTACTS
            ) == PackageManager.PERMISSION_GRANTED -> {
                // Permission is already granted
                syncContacts()
            }
            else -> {
                requestPermissionLauncher.launch(Manifest.permission.READ_CONTACTS)
            }
        }
    }

    private fun syncContacts() {
        Toast.makeText(requireContext(), "Syncing contacts...", Toast.LENGTH_SHORT).show()
        // Use a coroutine to fetch contacts off the main thread
        CoroutineScope(Dispatchers.IO).launch {
            val contacts = fetchContacts()
            contacts.forEach { number ->
                viewModel.insert(AllowlistItem(phoneNumber = number))
            }
            // Show a confirmation on the main thread
            launch(Dispatchers.Main) {
                Toast.makeText(requireContext(), "${contacts.size} contacts added to allowlist.", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun fetchContacts(): Set<String> {
        val numbers = mutableSetOf<String>()
        val cursor = requireContext().contentResolver.query(
            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
            null,
            null,
            null,
            null
        )

        cursor?.use {
            val numberIndex = it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)
            while (it.moveToNext()) {
                val number = it.getString(numberIndex)
                // Clean up the number to remove spaces and hyphens
                val normalizedNumber = number.replace(Regex("[\\s-]"), "")
                numbers.add(normalizedNumber)
            }
        }
        return numbers
    }
}
