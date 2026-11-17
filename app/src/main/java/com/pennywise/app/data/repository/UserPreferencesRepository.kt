package com.pennywise.app.data.repository

import android.content.Context
import androidx.datastore.preferences.preferencesDataStore

private val Context.userPreferencesDataStore by preferencesDataStore(name = "user_preferences")

class UserPreferencesRepository(
    private val context: Context,
) {
    val preferences = context.userPreferencesDataStore.data
}
