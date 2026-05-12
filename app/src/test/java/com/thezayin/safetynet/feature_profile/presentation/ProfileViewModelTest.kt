package com.thezayin.safetynet.feature_profile.presentation

import app.cash.turbine.test
import com.thezayin.safetynet.core.domain.error.DomainResult
import com.thezayin.safetynet.feature_profile.domain.model.InputType
import com.thezayin.safetynet.feature_profile.domain.model.ProfileData
import com.thezayin.safetynet.feature_profile.domain.repository.ProfileRepository
import com.thezayin.safetynet.feature_profile.domain.usecase.SaveProfileDataUseCase
import com.thezayin.safetynet.feature_profile.domain.usecase.ValidateProfileInputUseCase
import com.thezayin.safetynet.feature_profile.presentation.mvi.ProfileEffect
import com.thezayin.safetynet.feature_profile.presentation.mvi.ProfileIntent
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class ProfileViewModelTest {

    // @get:Rule
    // val mainDispatcherRule = MainDispatcherRule() // Add your coroutine rule here

    private lateinit var repository: ProfileRepository
    private lateinit var validateUseCase: ValidateProfileInputUseCase
    private lateinit var saveUseCase: SaveProfileDataUseCase
    private lateinit var viewModel: ProfileViewModel

    private val profileDataFlow = MutableStateFlow(ProfileData())

    @Before
    fun setup() {
        repository = mockk()
        // We can use the real validator since it has no dependencies!
        validateUseCase = ValidateProfileInputUseCase()
        saveUseCase = mockk()

        every { repository.profileData } returns profileDataFlow

        viewModel = ProfileViewModel(repository, validateUseCase, saveUseCase)
    }

    @Test
    fun `OnInputChanged updates state and validation correctly`() = runTest {
        viewModel.state.test {
            // Skip initial state emission
            skipItems(1)

            // Send Intent
            viewModel.onIntent(ProfileIntent.OnInputChanged("Sarah"))

            // Await the new state
            val state = awaitItem()
            assertEquals("Sarah", state.inputValue)
            assertEquals(true, state.isNextEnabled) // "Sarah" is valid
        }
    }

    @Test
    fun `OnContinueClicked saves data and emits NavigateNext effect`() = runTest {
        // Setup valid state
        viewModel.onIntent(ProfileIntent.OnInputChanged("Sarah"))

        coEvery { saveUseCase("Sarah", InputType.Name) } returns DomainResult.Success(Unit)

        viewModel.effect.test {
            // Fire the intent
            viewModel.onIntent(ProfileIntent.OnContinueClicked)

            // Assert the exact effect was fired
            assertEquals(ProfileEffect.NavigateNext, awaitItem())
        }
    }

    @Test
    fun `OnBackClicked time machine steps backward through states`() = runTest {
        viewModel.state.test {
            // Assume we are on the Email Step
            viewModel.onIntent(ProfileIntent.Init(InputType.CircleEmail))
            skipItems(2) // Skip init emissions

            // Fire Back intent
            viewModel.onIntent(ProfileIntent.OnBackClicked)

            // State should step backward to CircleName
            val state = awaitItem()
            assertEquals(InputType.CircleName, state.inputType)
        }
    }

    @Test
    fun `OnBackClicked on first step emits NavigateBack effect`() = runTest {
        // Ensure we are on the first step
        viewModel.onIntent(ProfileIntent.Init(InputType.Name))

        viewModel.effect.test {
            viewModel.onIntent(ProfileIntent.OnBackClicked)

            // Should tell the NavGraph to pop the screen completely
            assertEquals(ProfileEffect.NavigateBack, awaitItem())
        }
    }
}