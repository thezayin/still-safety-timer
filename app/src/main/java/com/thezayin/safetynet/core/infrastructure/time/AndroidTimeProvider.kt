package com.thezayin.safetynet.core.infrastructure.time

import android.os.SystemClock
import com.thezayin.safetynet.core.domain.time.TimeProvider

class AndroidTimeProvider : TimeProvider {

    override fun currentTimeMillis(): Long =
        System.currentTimeMillis()

    override fun elapsedRealtime(): Long =
        SystemClock.elapsedRealtime()
}