package com.thezayin.safetynet.core.messaging.di

import com.google.firebase.inappmessaging.FirebaseInAppMessaging
import com.google.firebase.messaging.FirebaseMessaging
import org.koin.dsl.module

val messagingModule = module {
    // Provide Firebase Cloud Messaging for receiving remote alerts
    single { FirebaseMessaging.getInstance() }

    // Provide Firebase In-App Messaging for local UI triggers
    single { FirebaseInAppMessaging.getInstance() }
}