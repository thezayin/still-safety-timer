package com.thezayin.safetynet.feature_timer.data.remote

import com.thezayin.safetynet.core.domain.logger.LocalLogger
import io.ktor.client.HttpClient
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

object MailjetClient {
    fun create(logger: LocalLogger): HttpClient {
        return HttpClient(Android) {
            install(ContentNegotiation) {
                json(Json {
                    ignoreUnknownKeys = true
                    isLenient = true
                    encodeDefaults = true
                })
            }

            install(Logging) {
                level = LogLevel.INFO
                this.logger = object : Logger {
                    override fun log(message: String) {
                        logger.d("Ktor-Mailjet", message)
                    }
                }
            }

            engine {
                connectTimeout = 10_000
                socketTimeout = 20_000
            }
        }
    }
}