package com.thezayin.safetynet.feature_onboarding.domain.usecase

import com.thezayin.safetynet.core.presentation.model.UiText
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class GetOnboardingSlidesUseCaseTest {

    private val useCase = GetOnboardingSlidesUseCase()

    @Test
    fun `invoke should return the correct list of onboarding slides`() {
        // When
        val slides = useCase()

        // Then
        assertEquals(2, slides.size)

        // Assert that we are properly using Android String Resources instead of hardcoded strings
        assertTrue(slides[0].title is UiText.StringResource)
        assertTrue(slides[0].description is UiText.StringResource)

        // Assert an image resource was mapped
        assertTrue(slides[0].imageRes > 0)
    }
}