package com.kopa_samchu.VeriFire

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ScamAlertAdapter(private var alerts: List<ScamAlert>) :
    RecyclerView.Adapter<ScamAlertAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val titleTextView: TextView = view.findViewById(R.id.textViewAlertTitle)
        val descriptionTextView: TextView = view.findViewById(R.id.textViewAlertDescription)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.list_item_alert_horizontal, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val alert = alerts[position]
        holder.titleTextView.text = alert.title
        holder.descriptionTextView.text = alert.description
    }

    override fun getItemCount() = alerts.size

    fun updateAlerts(newAlerts: List<ScamAlert>) {
        alerts = newAlerts
        notifyDataSetChanged()
    }
}
