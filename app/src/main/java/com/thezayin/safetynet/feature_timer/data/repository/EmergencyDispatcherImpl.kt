package com.thezayin.safetynet.feature_timer.data.repository

import com.thezayin.safetynet.BuildConfig
import com.thezayin.safetynet.core.domain.error.AppError
import com.thezayin.safetynet.core.domain.error.DomainResult
import com.thezayin.safetynet.core.domain.logger.LocalLogger
import com.thezayin.safetynet.feature_timer.data.remote.EmailContact
import com.thezayin.safetynet.feature_timer.data.remote.MailjetMessage
import com.thezayin.safetynet.feature_timer.data.remote.MailjetRequest
import com.thezayin.safetynet.feature_timer.domain.repository.EmergencyDispatcher
import io.ktor.client.HttpClient
import io.ktor.client.request.basicAuth
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.http.isSuccess
import org.koin.core.logger.Logger

class EmergencyDispatcherImpl(
    private val httpClient: HttpClient,
    private val logger: LocalLogger
) : EmergencyDispatcher {

    companion object {
        private const val MAILJET_URL = "https://api.mailjet.com/v3.1/send"
        private const val COMPANY_ADDRESS = "97A, Glenny Road, Barking, IG11 8QG"
    }

    override suspend fun sendEmailAlert(
        toEmail: String, toName: String, userName: String
    ): DomainResult<Unit> {
        val requestBody = MailjetRequest(
            messages = listOf(
                MailjetMessage(
                    from = EmailContact("zainshahid4950@gmail.com", "Still Safety System"),
                    to = listOf(EmailContact(toEmail, toName)),
                    subject = "🚨 URGENT: Emergency Alert for $userName",
                    textPart = "EMERGENCY: $userName has failed to check in. Please contact them immediately.",
                    htmlPart = """
                        <div style="font-family: sans-serif; max-width: 600px; margin: auto; border: 1px solid #eee; border-radius: 10px; overflow: hidden;">
                            <!-- Header -->
                            <div style="background-color: #e74c3c; color: white; padding: 20px; text-align: center;">
                                <h1 style="margin: 0; font-size: 24px;">EMERGENCY ALERT</h1>
                            </div>
                            
                            <!-- Body -->
                            <div style="padding: 30px; color: #2c3e50; line-height: 1.6;">
                                <p>Dear <b>$toName</b>,</p>
                                <p>This is an automated emergency dispatch. Your contact, <b>$userName</b>, has failed to check in on the <b>Still Safety App</b> and the safety timer has expired.</p>
                                <p style="background-color: #fff3f3; padding: 15px; border-radius: 5px; border: 1px solid #fbc2c2;">
                                    <b>Required Action:</b> Please attempt to contact <b>$userName</b> immediately. If you cannot reach them, please verify their safety.
                                </p>
                            </div>
                            
                            <!-- Footer (Company Address) -->
                            <div style="background-color: #f8f9fa; padding: 20px; text-align: center; color: #7f8c8d; font-size: 12px; border-top: 1px solid #eee;">
                                <p style="margin: 0 0 5px 0;"><b>Still Safety System</b></p>
                                <p style="margin: 0;">$COMPANY_ADDRESS</p>
                                <p style="margin: 10px 0 0 0; font-style: italic;">Protecting what matters most.</p>
                            </div>
                        </div>
                    """.trimIndent()
                )
            )
        )

        return try {
            val response: HttpResponse = httpClient.post(MAILJET_URL) {
                basicAuth(BuildConfig.MAILJET_API_KEY, BuildConfig.MAILJET_SECRET_KEY)
                contentType(ContentType.Application.Json)
                setBody(requestBody)
            }
            val responseBody = response.bodyAsText()
            if (response.status.isSuccess()) {
                logger.i("EmergencyEngine", "SUCCESS: Email sent!")
                DomainResult.Success(Unit)
            } else {
                // 🔴 This will print the EXACT reason Mailjet rejected your email
                logger.e("EmergencyEngine", "FAILURE: Code ${response.status.value}, Body: $responseBody")
                DomainResult.Failure(AppError.Alert.NetworkFailure(response.status.value, responseBody))
            }
            if (response.status.isSuccess()) {
                DomainResult.Success(Unit)
            } else {
                DomainResult.Failure(
                    AppError.Alert.NetworkFailure(
                        response.status.value, response.bodyAsText()
                    )
                )
            }
        } catch (e: Exception) {
            DomainResult.Failure(AppError.Alert.SendFailed(e))
        }
    }
}