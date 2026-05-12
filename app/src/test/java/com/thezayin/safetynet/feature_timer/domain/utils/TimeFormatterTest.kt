package com.thezayin.safetynet.feature_timer.domain.utils

import com.thezayin.safetynet.core.domain.utils.TimeFormatter
import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * TimeFormatterTest: Validates the luxury string formatting.
 * Tests edge cases like zero, single digits, and large durations.
 */
class TimeFormatterTest {

    @Test
    fun `formatCountdown converts milliseconds to HH-mm-ss correctly`() {
        // 1 hour, 1 minute, 1 second
        val millis = (3600 + 60 + 1) * 1000L
        assertEquals("01:01:01", TimeFormatter.formatCountdown(millis))

        // Zero case
        assertEquals("00:00:00", TimeFormatter.formatCountdown(0L))

        // Large duration (over 24 hours)
        val largeMillis = 48 * 3600 * 1000L // 48 hours
        assertEquals("48:00:00", TimeFormatter.formatCountdown(largeMillis))
    }

    @Test
    fun `formatCountdown handles negative values by returning zero`() {
        assertEquals("00:00:00", TimeFormatter.formatCountdown(-5000L))
    }

    @Test
    fun `formatAbortSeconds always returns two digits`() {
        assertEquals("09", TimeFormatter.formatAbortSeconds(9))
        assertEquals("59", TimeFormatter.formatAbortSeconds(59))
        assertEquals("00", TimeFormatter.formatAbortSeconds(0))
        assertEquals("00", TimeFormatter.formatAbortSeconds(-1)) // Safety floor
    }

    @Test
    fun `formatDuration returns human readable strings for settings`() {
        // Both hours and minutes
        val both = (2 * 3600 + 15 * 60) * 1000L
        assertEquals("2h 15m", TimeFormatter.formatDuration(both))

        // Only hours
        val hoursOnly = 5 * 3600 * 1000L
        assertEquals("5h", TimeFormatter.formatDuration(hoursOnly))

        // Only minutes
        val minutesOnly = 45 * 60 * 1000L
        assertEquals("45m", TimeFormatter.formatDuration(minutesOnly))
    }

    @Test
    fun `formatHours handles singular and plural correctly`() {
        assertEquals("1 hour", TimeFormatter.formatHours(1))
        assertEquals("48 hours", TimeFormatter.formatHours(48))
        assertEquals("0 hours", TimeFormatter.formatHours(0))
    }
}