package com.thezayin.safetynet.feature_profile.presentation.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.thezayin.safetynet.R
import com.thezayin.safetynet.core.ui.components.PrimaryButton
import com.thezayin.safetynet.core.ui.components.PrimaryTextField

@Composable
fun ProfileFieldContent(
    title: String,
    subtitle: String,
    value: String,
    placeholder: String,
    hintText: String,
    isError: Boolean,
    isLoading: Boolean,
    isButtonEnabled: Boolean,
    keyboardType: KeyboardType = KeyboardType.Text,
    onValueChange: (String) -> Unit,
    onBackClick: () -> Unit,
    onContinueClick: () -> Unit,
    bannerAdContent: @Composable () -> Unit = {}
) {
    Box(modifier = Modifier.fillMaxSize()) {
        ProfileBackground()

        IconButton(
            onClick = onBackClick,
            modifier = Modifier
                .align(Alignment.TopStart)
                .statusBarsPadding()
                .padding(top = 16.dp, start = 16.dp)
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = stringResource(R.string.btn_back),
                tint = MaterialTheme.colorScheme.onBackground
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .padding(horizontal = 32.dp), horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(100.dp))

            Text(
                text = title,
                style = MaterialTheme.typography.displayMedium,
                fontWeight = FontWeight.Light,
                color = MaterialTheme.colorScheme.onBackground,
                lineHeight = 48.sp,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                lineHeight = 26.sp,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(64.dp))

            PrimaryTextField(
                value = value,
                onValueChange = onValueChange,
                placeholder = placeholder,
                keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
                autoFocus = true
            )

            if (isError && value.isNotEmpty()) {
                Text(
                    text = hintText,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier
                        .padding(top = 16.dp, start = 16.dp)
                        .fillMaxWidth()
                )
            }

            Spacer(modifier = Modifier.weight(1f))
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) { }
            PrimaryButton(
                text = stringResource(R.string.profile_btn_continue),
                onClick = onContinueClick,
                enabled = isButtonEnabled && !isLoading,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 40.dp)
            )
            bannerAdContent()
        }
    }
}