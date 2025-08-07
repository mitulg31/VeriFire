package com.kopa_samchu.VeriFire

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.telephony.SmsManager
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.material.appbar.MaterialToolbar
import java.text.SimpleDateFormat
import java.util.*
import java.util.regex.Pattern

class SpamDetailActivity : AppCompatActivity() {

    private val blocklistViewModel: BlocklistViewModel by viewModels()
    private val blockedMessagesViewModel: BlockedMessagesViewModel by viewModels()
    private var emergencyContactNumber: String? = null

    private val requestCallPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
        if (isGranted) {
            makePhoneCall()
        } else {
            Toast.makeText(this, "Call permission denied.", Toast.LENGTH_SHORT).show()
        }
    }
    private val requestSmsPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
        if (isGranted) {
            sendSmsMessage()
        } else {
            Toast.makeText(this, "SMS permission denied.", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_spam_details)

        val toolbar: MaterialToolbar = findViewById(R.id.toolbar)
        toolbar.setNavigationOnClickListener { finish() }

        val message = intent.getParcelableExtra<BlockedMessage>("MESSAGE_DETAIL")
        if (message == null) {
            Toast.makeText(this, "Error loading message details.", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        val senderTextView: TextView = findViewById(R.id.textViewDetailSender)
        val timeTextView: TextView = findViewById(R.id.textViewDetailTime)
        val categoryTextView: TextView = findViewById(R.id.textViewDetailCategory)
        val messageTextView: TextView = findViewById(R.id.textViewDetailMessage)
        val notSpamButton: Button = findViewById(R.id.buttonDetailNotSpam)
        val deleteButton: Button = findViewById(R.id.buttonDetailDelete)
        val blockSimilarButton: Button = findViewById(R.id.buttonDetailBlockSimilar)
        val getHelpButton: Button = findViewById(R.id.buttonDetailGetHelp)

        senderTextView.text = message.sender
        timeTextView.text = formatTimestamp(message.timestamp)
        categoryTextView.text = message.spamType
        messageTextView.text = message.messageBody

        val prefs = getSharedPreferences("VeriFirePrefs", Context.MODE_PRIVATE)
        emergencyContactNumber = prefs.getString("emergency_contact", null)

        getHelpButton.visibility = if (emergencyContactNumber.isNullOrEmpty()) View.GONE else View.VISIBLE
        getHelpButton.setOnClickListener {
            showHelpDialog()
        }

        blockSimilarButton.setOnClickListener {
            blocklistViewModel.insert(BlocklistItem(value = message.sender, type = "number"))
            val keyword = extractKeyword(message.messageBody)
            if (keyword != null) {
                blocklistViewModel.insert(BlocklistItem(value = keyword, type = "keyword"))
                Toast.makeText(this, "Sender and keyword '$keyword' blocked", Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(this, "Sender blocked", Toast.LENGTH_SHORT).show()
            }
            blockedMessagesViewModel.delete(message)
            finish()
        }

        notSpamButton.setOnClickListener {
            blockedMessagesViewModel.delete(message)
            Toast.makeText(this, "Message marked as not spam", Toast.LENGTH_SHORT).show()
            finish()
        }

        deleteButton.setOnClickListener {
            blockedMessagesViewModel.delete(message)
            Toast.makeText(this, "Message deleted", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    private fun showHelpDialog() {
        val options = arrayOf("Call for Help", "Send a Help Message")
        AlertDialog.Builder(this)
            .setTitle("Contact Your Emergency Contact?")
            .setItems(options) { _, which ->
                when (which) {
                    0 -> checkCallPermission()
                    1 -> checkSmsPermission()
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun checkCallPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
            makePhoneCall()
        } else {
            requestCallPermissionLauncher.launch(Manifest.permission.CALL_PHONE)
        }
    }

    private fun makePhoneCall() {
        emergencyContactNumber?.let {
            val intent = Intent(Intent.ACTION_CALL, Uri.parse("tel:$it"))
            startActivity(intent)
        }
    }

    private fun checkSmsPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED) {
            sendSmsMessage()
        } else {
            requestSmsPermissionLauncher.launch(Manifest.permission.SEND_SMS)
        }
    }

    private fun sendSmsMessage() {
        try {
            val smsManager = getSystemService(SmsManager::class.java)
            val helpMessage = "I think I may have received a scam message and I'm not sure what to do. Can you please help me?"
            smsManager.sendTextMessage(emergencyContactNumber, null, helpMessage, null, null)
            Toast.makeText(this, "Help message sent.", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            Toast.makeText(this, "Failed to send message.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun extractKeyword(messageBody: String): String? {
        val urlPattern = Pattern.compile(
            "(?:(?:https|http):\\/\\/|www\\.|bit\\.ly)[\\w\\/\\.-]+|\\b[\\w.-]+\\.(?:com|net|org|au)\\b"
        )
        val matcher = urlPattern.matcher(messageBody)
        return if (matcher.find()) {
            matcher.group(0)
        } else {
            null
        }
    }

    private fun formatTimestamp(timestamp: Long): String {
        val sdf = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault())
        return sdf.format(Date(timestamp))
    }
}
