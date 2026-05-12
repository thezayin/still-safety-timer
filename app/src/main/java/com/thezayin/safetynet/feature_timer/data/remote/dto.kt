package com.thezayin.safetynet.feature_timer.data.remote

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MailjetRequest(
    @SerialName("Messages") val messages: List<MailjetMessage>
)

@Serializable
data class MailjetMessage(
    @SerialName("From") val from: EmailContact,
    @SerialName("To") val to: List<EmailContact>,
    @SerialName("Subject") val subject: String,
    @SerialName("TextPart") val textPart: String,
    @SerialName("HTMLPart") val htmlPart: String
)

@Serializable
data class EmailContact(
    @SerialName("Email") val email: String,
    @SerialName("Name") val name: String
)