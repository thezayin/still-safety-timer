package com.thezayin.safetynet.core.infrastructure.logger

import android.util.Log
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.thezayin.safetynet.core.domain.logger.LocalLogger

class AndroidLocalLogger(private val crashlytics: FirebaseCrashlytics) : LocalLogger {

    override fun d(tag: String, message: String) {
        Log.d(tag, message)
        crashlytics.log("$tag: $message")
    }

    override fun i(tag: String, message: String) {
        Log.i(tag, message)
        crashlytics.log("$tag: $message")
    }

    override fun w(tag: String, message: String) {
        Log.w(tag, message)
        crashlytics.log("$tag: $message")
    }

    override fun e(tag: String, message: String, throwable: Throwable?) {
        crashlytics.log("$tag: $message")
        if (throwable != null) {
            Log.e(tag, message, throwable)
            crashlytics.recordException(throwable)
        } else {
            Log.e(tag, message)
        }
    }
}