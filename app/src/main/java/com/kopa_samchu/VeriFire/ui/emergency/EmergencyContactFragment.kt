package com.kopa_samchu.VeriFire.ui.emergency

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.kopa_samchu.VeriFire.R

class EmergencyContactFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_emergency_contact, container, false)
        val numberEditText: EditText = root.findViewById(R.id.editTextEmergencyNumber)
        val saveButton: Button = root.findViewById(R.id.buttonSaveEmergencyNumber)
        val prefs = activity?.getSharedPreferences("VeriFirePrefs", Context.MODE_PRIVATE)

        numberEditText.setText(prefs?.getString("emergency_contact", ""))

        saveButton.setOnClickListener {
            val number = numberEditText.text.toString().trim()
            if (number.isNotEmpty()) {
                prefs?.edit()?.putString("emergency_contact", number)?.apply()
                Toast.makeText(requireContext(), "Emergency contact saved.", Toast.LENGTH_SHORT).show()
                findNavController().popBackStack() // Go back to settings screen
            } else {
                Toast.makeText(requireContext(), "Please enter a number.", Toast.LENGTH_SHORT).show()
            }
        }

        return root
    }
}
