package com.thezayin.safetynet

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.thezayin.safetynet.core.ads.manager.AdManager
import com.thezayin.safetynet.core.ads.model.AdType
import com.thezayin.safetynet.core.domain.logger.LocalLogger
import com.thezayin.safetynet.core.navigation.Screen
import com.thezayin.safetynet.core.navigation.StillNavGraph
import com.thezayin.safetynet.core.ui.theme.SafetyNetTheme
import org.koin.android.ext.android.inject

class MainActivity : ComponentActivity() {

    private val logger: LocalLogger by inject()
    private val adManager: AdManager by inject()
    private var isShowingFullScreenAd = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        adManager.initializeConsentAndAds(this) {
            com.google.android.gms.ads.MobileAds.initialize(this) {}
        }
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

    override fun onResume() {
        super.onResume()
        if (!isShowingFullScreenAd) {
            showAppOpenAd()
        }
        isShowingFullScreenAd = false
    }

    fun suppressNextAppOpen() {
        isShowingFullScreenAd = true
    }

    private fun showAppOpenAd() {
        adManager.show(AdType.APP_OPEN, this) {
            adManager.load(AdType.APP_OPEN)
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