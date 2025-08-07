package com.kopa_samchu.VeriFire.ui.home

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.viewpager2.widget.ViewPager2
import com.kopa_samchu.VeriFire.*
import kotlin.random.Random

class HomeFragment : Fragment() {

    private lateinit var textViewTotalBlocked: TextView
    private lateinit var textViewTotalScanned: TextView
    private lateinit var textViewTipContent: TextView
    private lateinit var viewPagerAlerts: ViewPager2
    private lateinit var alertAdapter: ScamAlertAdapter

    private val viewModel: BlockedMessagesViewModel by activityViewModels()

    private val autoScrollHandler = Handler(Looper.getMainLooper())
    private lateinit var autoScrollRunnable: Runnable
    private val SCROLL_DELAY = 5000L

    private val safetyTips = listOf(
        "Your bank will never ask for your password or PIN in an SMS. If you get a message like this, it's a scam.",
        "Never click on links in unexpected messages, even if they seem to be from a trusted company like AusPost or the ATO.",
        "Be wary of messages that create a sense of urgency, like 'your account will be suspended' or 'limited time offer'.",
        "If a message asks you to install an app to track a package or listen to a voicemail, it could be malware.",
        "Government agencies like Centrelink or Medicare will not ask you for personal details or payment via SMS."
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_home, container, false)

        textViewTotalBlocked = root.findViewById(R.id.textViewBlockedTodayValue)
        textViewTotalScanned = root.findViewById(R.id.textViewTotalScannedValue)
        textViewTipContent = root.findViewById(R.id.textViewTipContent)
        viewPagerAlerts = root.findViewById(R.id.viewPagerAlerts)

        setupTipOfTheDay()
        setupAlertsFeed()
        setupAutoScroll()

        viewModel.allBlockedMessages.observe(viewLifecycleOwner) { messages ->
            textViewTotalBlocked.text = messages.size.toString()
        }

        return root
    }

    private fun setupTipOfTheDay() {
        textViewTipContent.text = safetyTips[Random.nextInt(safetyTips.size)]
    }

    private fun setupAlertsFeed() {
        val staticAlerts = listOf(
            ScamAlert("ATO Tax Scam", "Watch out for SMS messages claiming you have an unpaid tax debt. The ATO will never ask for payment via text."),
            ScamAlert("AusPost Delivery Fee", "Scammers are sending fake AusPost texts about a pending delivery fee. Do not click the link."),
            ScamAlert("Flubot Malware", "Be cautious of messages about missed calls or voicemails that ask you to install an app. This could be Flubot malware."),
            ScamAlert("Relationship Scams", "Be wary of online contacts who quickly profess strong feelings and then ask for money for emergencies.")
        )

        alertAdapter = ScamAlertAdapter(staticAlerts)
        viewPagerAlerts.adapter = alertAdapter
    }

    private fun setupAutoScroll() {
        autoScrollRunnable = Runnable {
            val currentItem = viewPagerAlerts.currentItem
            val itemCount = alertAdapter.itemCount
            if (itemCount > 1) { // Only scroll if there's more than one item
                val nextItem = (currentItem + 1) % itemCount
                viewPagerAlerts.setCurrentItem(nextItem, true)
            }
            autoScrollHandler.postDelayed(autoScrollRunnable, SCROLL_DELAY)
        }
    }

    private fun startAutoScroll() {
        autoScrollHandler.removeCallbacks(autoScrollRunnable)
        autoScrollHandler.postDelayed(autoScrollRunnable, SCROLL_DELAY)
    }

    private fun stopAutoScroll() {
        autoScrollHandler.removeCallbacks(autoScrollRunnable)
    }

    override fun onResume() {
        super.onResume()
        updateTotalScannedCounter()
        startAutoScroll()
    }

    override fun onPause() {
        super.onPause()
        stopAutoScroll()
    }

    private fun updateTotalScannedCounter() {
        val prefs = activity?.getSharedPreferences("VeriFirePrefs", Context.MODE_PRIVATE)
        val totalScanned = prefs?.getInt("total_scanned", 0) ?: 0
        textViewTotalScanned.text = totalScanned.toString()
    }
}
