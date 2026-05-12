package com.thezayin.safetynet.feature_profile.domain.usecase

import com.thezayin.safetynet.feature_profile.domain.model.InputType
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ValidateProfileInputUseCaseTest {

    private val validate = ValidateProfileInputUseCase()

    @Test
    fun `when input is empty, validation fails for all types except Review`() {
        assertFalse(validate("", InputType.Name))
        assertFalse(validate("   ", InputType.CircleName))
        assertFalse(validate("", InputType.CircleEmail))
        assertTrue(validate("", InputType.Review)) // Review always passes
    }

    @Test
    fun `when name is valid, validation passes`() {
        assertTrue(validate("Sarah", InputType.Name))
        assertTrue(validate("Sarah Jenkins", InputType.CircleName))
    }

    @Test
    fun `when name is invalid (too short or numbers only), validation fails`() {
        assertFalse(validate("A", InputType.Name))
        assertFalse(validate("123", InputType.CircleName))
    }

    @Test
    fun `when email is valid, validation passes`() {
        assertTrue(validate("test@example.com", InputType.CircleEmail))
        assertTrue(validate("user.name+tag@domain.co.uk", InputType.CircleEmail))
    }

    @Test
    fun `when email is invalid, validation fails`() {
        assertFalse(validate("test@com", InputType.CircleEmail))
        assertFalse(validate("test.com", InputType.CircleEmail))
        assertFalse(validate("test@example", InputType.CircleEmail))
    }
}