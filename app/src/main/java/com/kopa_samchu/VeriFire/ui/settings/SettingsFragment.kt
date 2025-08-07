package com.kopa_samchu.VeriFire.ui.settings

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.switchmaterial.SwitchMaterial
import com.kopa_samchu.VeriFire.R

class SettingsFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_settings, container, false)
        val blocklistButton = root.findViewById<CardView>(R.id.buttonManageBlocklist)
        val allowlistButton = root.findViewById<CardView>(R.id.buttonManageAllowlist)
        val emergencyButton = root.findViewById<CardView>(R.id.buttonEmergencyContact)
        val notificationSwitch = root.findViewById<SwitchMaterial>(R.id.switchNotifications)
        val prefs = activity?.getSharedPreferences("VeriFirePrefs", Context.MODE_PRIVATE)
        notificationSwitch.isChecked = prefs?.getBoolean("notifications_enabled", true) ?: true
        notificationSwitch.setOnCheckedChangeListener { _, isChecked ->
            prefs?.edit()?.putBoolean("notifications_enabled", isChecked)?.apply()
        }

        blocklistButton.setOnClickListener {
            findNavController().navigate(R.id.action_settings_to_blocklist)
        }

        allowlistButton.setOnClickListener {
            findNavController().navigate(R.id.action_settings_to_allowlist)
        }

        emergencyButton.setOnClickListener {
            findNavController().navigate(R.id.action_settings_to_emergency)
        }

        return root
    }
}

