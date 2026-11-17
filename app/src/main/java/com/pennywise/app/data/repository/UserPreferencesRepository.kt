package com.pennywise.app.data.repository

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.userPreferencesDataStore by preferencesDataStore(name = "user_preferences")

class UserPreferencesRepository(
    private val context: Context,
) {
    val preferences = context.userPreferencesDataStore.data

    val profile: Flow<UserProfile> = preferences.map { values ->
        UserProfile(
            displayName = values[Keys.DISPLAY_NAME].orEmpty(),
            avatar = values[Keys.AVATAR] ?: "moon",
            theme = values[Keys.THEME] ?: ThemePreference.SYSTEM.name,
            currency = values[Keys.CURRENCY] ?: "INR",
            notificationsEnabled = values[Keys.NOTIFICATIONS] ?: true,
        )
    }

    suspend fun updateProfile(profile: UserProfile) {
        context.userPreferencesDataStore.edit { values ->
            values[Keys.DISPLAY_NAME] = profile.displayName.trim()
            values[Keys.AVATAR] = profile.avatar
            values[Keys.THEME] = profile.theme
            values[Keys.CURRENCY] = profile.currency
            values[Keys.NOTIFICATIONS] = profile.notificationsEnabled
        }
    }

    private object Keys {
        val DISPLAY_NAME = stringPreferencesKey("display_name")
        val AVATAR = stringPreferencesKey("avatar")
        val THEME = stringPreferencesKey("theme")
        val CURRENCY = stringPreferencesKey("currency")
        val NOTIFICATIONS = booleanPreferencesKey("notifications_enabled")
    }
}

data class UserProfile(
    val displayName: String = "",
    val avatar: String = "moon",
    val theme: String = ThemePreference.SYSTEM.name,
    val currency: String = "INR",
    val notificationsEnabled: Boolean = true,
)

enum class ThemePreference {
    SYSTEM,
    LIGHT,
    DARK,
}
