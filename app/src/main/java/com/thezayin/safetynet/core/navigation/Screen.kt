package com.thezayin.safetynet.core.navigation

import kotlinx.serialization.Serializable

sealed interface Screen {

    @Serializable data object Splash            : Screen
    @Serializable data object Consent           : Screen
    @Serializable data object PermissionSetup   : Screen
    @Serializable data object Onboarding        : Screen

    @Serializable data object ProfileGraph      : Screen
    @Serializable data object EnterName         : Screen
    @Serializable data object EnterContactName  : Screen
    @Serializable data object EnterContactEmail : Screen
    @Serializable data object ReviewProfile     : Screen

    @Serializable data object Timer             : Screen
    @Serializable data object Settings          : Screen
}