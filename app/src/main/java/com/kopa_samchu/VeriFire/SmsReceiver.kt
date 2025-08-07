package com.kopa_samchu.VeriFire

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.provider.Telephony
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class SmsReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        if (context == null) return

        if (intent?.action == Telephony.Sms.Intents.SMS_RECEIVED_ACTION) {
            incrementTotalScanned(context)
            val messages = Telephony.Sms.Intents.getMessagesFromIntent(intent)
            val classifier = SpamClassifier(context)
            val db = AppDatabase.getDatabase(context)
            val blockedMessageDao = db.blockedMessageDao()
            val blocklistItems: List<BlocklistItem>
            val allowlistItems: List<AllowlistItem>
            runBlocking(Dispatchers.IO) {
                blocklistItems = db.blocklistDao().getAllItemsList()
                allowlistItems = db.allowlistDao().getAllItemsList()
            }
            try {
                classifier.initialize()
                messages?.forEach { smsMessage ->
                    val sender = smsMessage.displayOriginatingAddress
                    val messageBody = smsMessage.messageBody
                    var isSpam = false
                    var spamType = "General Spam"
                    var reason = ""
                    if (allowlistItems.any { it.phoneNumber == sender }) {
                        isSpam = false
                        reason = "Sender on Allowlist"
                    } else {
                        val blockedNumber = blocklistItems.find { it.type == "number" && it.value == sender }
                        val blockedKeyword = blocklistItems.find { it.type == "keyword" && messageBody.contains(it.value, ignoreCase = true) }
                        if (blockedNumber != null) {
                            isSpam = true
                            spamType = "Blocked Number"
                            reason = "Sender on Blocklist"
                        } else if (blockedKeyword != null) {
                            isSpam = true
                            spamType = "Blocked Keyword"
                            reason = "Keyword '${blockedKeyword.value}' on Blocklist"
                        } else {
                            val result = classifier.classify(messageBody)
                            isSpam = result.isSpam
                            spamType = result.spamType
                            reason = "AI Detection"
                        }
                    }
                    Log.d("SmsReceiver", "From: $sender, IsSpam: $isSpam, Reason: $reason")
                    if (isSpam) {
                        CoroutineScope(Dispatchers.IO).launch {
                            val blockedMessage = BlockedMessage(
                                sender = sender,
                                messageBody = messageBody,
                                timestamp = System.currentTimeMillis(),
                                spamType = spamType
                            )
                            blockedMessageDao.insert(blockedMessage)
                        }
                        val prefs = context.getSharedPreferences("VeriFirePrefs", Context.MODE_PRIVATE)
                        val notificationsEnabled = prefs.getBoolean("notifications_enabled", true)
                        if (notificationsEnabled) {
                            showSpamNotification(context, sender, messageBody)
                        }
                    }
                }
            } catch (e: Exception) {
                Log.e("SmsReceiver", "Error classifying SMS", e)
            } finally {
                classifier.close()
            }
        }
    }

    private fun incrementTotalScanned(context: Context) {
        val prefs = context.getSharedPreferences("VeriFirePrefs", Context.MODE_PRIVATE)
        val currentCount = prefs.getInt("total_scanned", 0)
        prefs.edit().putInt("total_scanned", currentCount + 1).apply()
    }

    private fun showSpamNotification(context: Context, sender: String, message: String) {
        val channelId = "spam_notification_channel"
        val notificationManager = NotificationManagerCompat.from(context)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId, "Spam Notifications", NotificationManager.IMPORTANCE_HIGH
            )
            notificationManager.createNotificationChannel(channel)
        }

        val openAppIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent: PendingIntent = PendingIntent.getActivity(context, 0, openAppIntent, PendingIntent.FLAG_IMMUTABLE)

        val notificationBuilder = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.ic_alert)
            .setContentTitle("Spam Message Detected")
            .setContentText("From: $sender")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
            notificationManager.notify(System.currentTimeMillis().toInt(), notificationBuilder.build())
        }
    }
}
