package com.thezayin.safetynet

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.thezayin.safetynet.core.domain.logger.LocalLogger
import com.thezayin.safetynet.core.navigation.Screen
import com.thezayin.safetynet.core.navigation.StillNavGraph
import com.thezayin.safetynet.core.ui.theme.SafetyNetTheme
import org.koin.android.ext.android.inject

class MainActivity : ComponentActivity() {

    private val logger: LocalLogger by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val navController = rememberNavController()

            SafetyNetTheme {
                StillNavGraph(navController = navController)
            }

            LaunchedEffect(intent) {
                processIntent(intent, navController)
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
    }

    private fun processIntent(intent: Intent?, navController: NavHostController) {
        val destination = intent?.getStringExtra("NAV_DESTINATION") ?: return

        logger.i("MainActivity", "Deep link received: $destination")

        when (destination) {
            "ROUTE_TIMER", "ROUTE_TIMER_CRITICAL", "ROUTE_TIMER_ABORT" -> {
                navController.navigate(Screen.Timer) {
                    popUpTo(navController.graph.startDestinationId) {
                        inclusive = true
                    }
                    launchSingleTop = true
                }
            }
        }

        intent.removeExtra("NAV_DESTINATION")
    }
}