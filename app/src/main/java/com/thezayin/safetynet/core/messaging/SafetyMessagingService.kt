package com.thezayin.safetynet.core.messaging

import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.thezayin.safetynet.core.domain.logger.LocalLogger
import org.koin.android.ext.android.inject

/**
 * Handles incoming Cloud Messaging payloads.
 * Even without a backend, this allows SafetyNet to receive alerts from other users
 * or system-wide safety updates.
 */
class SafetyMessagingService : FirebaseMessagingService() {

    private val logger: LocalLogger by inject()

    /**
     * In a localized setup, we log the token for manual testing/debugging.
     */
    override fun onNewToken(token: String) {
        logger.d("MessagingService", "Local FCM Token: $token")
    }

    /**
     * Processes incoming messages. All logic is handled locally upon receipt.
     */
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        logger.d("MessagingService", "Message received from: ${remoteMessage.from}")

        // Process data payloads locally
        if (remoteMessage.data.isNotEmpty()) {
            val type = remoteMessage.data["type"] ?: "unknown"
            when (type) {
                "emergency_broadcast" -> {
                    // Logic for a local emergency broadcast
                    logger.d("MessagingService", "Local Emergency Broadcast Received")
                }

                "system_ping" -> {
                    logger.d("MessagingService", "Local system ping handled")
                }
            }
        }
    }
}