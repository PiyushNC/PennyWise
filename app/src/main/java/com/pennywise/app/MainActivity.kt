package com.pennywise.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.pennywise.app.data.repository.ThemePreference
import com.pennywise.app.viewmodel.ProfileViewModel
import com.pennywise.app.ui.PennyWiseApp
import com.pennywise.app.ui.theme.PennyWiseTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val application = LocalContext.current.applicationContext as PennyWiseApplication
            val profileViewModel: ProfileViewModel = viewModel(
                factory = ProfileViewModel.Factory(application.userPreferencesRepository),
            )
            val profile by profileViewModel.profile.collectAsState()
            PennyWiseTheme(
                darkTheme = when (profile.theme) {
                    ThemePreference.DARK.name -> true
                    ThemePreference.LIGHT.name -> false
                    else -> androidx.compose.foundation.isSystemInDarkTheme()
                },
            ) {
                Surface { PennyWiseApp() }
            }
        }
    }
}
