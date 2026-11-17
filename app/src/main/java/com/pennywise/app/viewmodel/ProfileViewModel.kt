package com.pennywise.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.pennywise.app.data.repository.ThemePreference
import com.pennywise.app.data.repository.UserPreferencesRepository
import com.pennywise.app.data.repository.UserProfile
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ProfileViewModel(
    private val repository: UserPreferencesRepository,
) : ViewModel() {
    val profile: StateFlow<UserProfile> = repository.profile.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5_000),
        UserProfile(),
    )

    fun save(profile: UserProfile) {
        viewModelScope.launch { repository.updateProfile(profile) }
    }

    class Factory(
        private val repository: UserPreferencesRepository,
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(ProfileViewModel::class.java)) {
                return ProfileViewModel(repository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel: ${modelClass.name}")
        }
    }
}
