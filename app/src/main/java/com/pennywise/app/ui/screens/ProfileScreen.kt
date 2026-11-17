package com.pennywise.app.ui.screens

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.DarkMode
import androidx.compose.material.icons.outlined.LightMode
import androidx.compose.material.icons.outlined.SettingsBrightness
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pennywise.app.data.repository.ThemePreference
import com.pennywise.app.data.repository.UserProfile
import com.pennywise.app.ui.anime.AnimeAvatarView
import com.pennywise.app.ui.anime.AvailableAnimeAvatars
import com.pennywise.app.ui.components.PennyWiseCard
import com.pennywise.app.ui.components.PennyWisePrimaryButton
import com.pennywise.app.ui.theme.AnimeAtmosphericBackground
import com.pennywise.app.ui.theme.ElectricViolet
import com.pennywise.app.ui.theme.PennyWiseSpacing
import com.pennywise.app.ui.theme.SakuraPink

@Composable
fun ProfileScreen(
    onBack: () -> Unit,
    viewModel: com.pennywise.app.viewmodel.ProfileViewModel,
) {
    val profile by viewModel.profile.collectAsState()
    ProfileContent(
        profile = profile,
        onBack = onBack,
        onSave = viewModel::save,
    )
}

@Composable
private fun ProfileContent(
    profile: UserProfile,
    onBack: () -> Unit,
    onSave: (UserProfile) -> Unit,
) {
    var name by remember(profile.displayName) { mutableStateOf(profile.displayName) }
    var avatar by remember(profile.avatar) { mutableStateOf(profile.avatar) }
    var theme by remember(profile.theme) { mutableStateOf(profile.theme) }

    AnimeAtmosphericBackground(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(PennyWiseSpacing.screen),
            verticalArrangement = Arrangement.spacedBy(PennyWiseSpacing.standard),
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(top = PennyWiseSpacing.large),
            ) {
                IconButton(onClick = onBack) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                        contentDescription = "Back",
                        tint = SakuraPink,
                    )
                }
                Column {
                    Text(
                        text = "CHARACTER CUSTOMIZATION",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = SakuraPink,
                        letterSpacing = 1.2.sp,
                    )
                    Text(
                        text = "Profile & Settings",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.ExtraBold,
                    )
                }
            }

            // Anime Character Profile Card
            PennyWiseCard(modifier = Modifier.fillMaxWidth()) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    AnimeAvatarView(
                        avatarId = avatar,
                        size = 72.dp,
                        isSelected = true,
                    )
                    Column(modifier = Modifier.padding(start = PennyWiseSpacing.standard)) {
                        Text(
                            text = name.ifBlank { "PennyWise User" },
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.ExtraBold,
                        )
                        Text(
                            text = "Master of Financial Routine",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }
            }

            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                label = { Text("Display Name") },
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = SakuraPink,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
                ),
            )

            Text("Select Your Anime Avatar", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)

            // 6 Avatar Selector Grid
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                AvailableAnimeAvatars.take(3).forEach { avatarItem ->
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(4.dp),
                    ) {
                        AnimeAvatarView(
                            avatarId = avatarItem.id,
                            size = 64.dp,
                            isSelected = avatar == avatarItem.id,
                            onClick = { avatar = avatarItem.id },
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = avatarItem.name,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = if (avatar == avatarItem.id) FontWeight.Bold else FontWeight.Medium,
                            color = if (avatar == avatarItem.id) SakuraPink else MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                AvailableAnimeAvatars.drop(3).forEach { avatarItem ->
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(4.dp),
                    ) {
                        AnimeAvatarView(
                            avatarId = avatarItem.id,
                            size = 64.dp,
                            isSelected = avatar == avatarItem.id,
                            onClick = { avatar = avatarItem.id },
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = avatarItem.name,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = if (avatar == avatarItem.id) FontWeight.Bold else FontWeight.Medium,
                            color = if (avatar == avatarItem.id) SakuraPink else MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }
            }

            Text("World Appearance", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)

            Row(horizontalArrangement = Arrangement.spacedBy(PennyWiseSpacing.small)) {
                ThemePreference.entries.forEach { option ->
                    val isSelected = theme == option.name
                    FilterChip(
                        selected = isSelected,
                        onClick = { theme = option.name },
                        leadingIcon = {
                            Icon(
                                when (option) {
                                    ThemePreference.LIGHT -> Icons.Outlined.LightMode
                                    ThemePreference.DARK -> Icons.Outlined.DarkMode
                                    ThemePreference.SYSTEM -> Icons.Outlined.SettingsBrightness
                                },
                                contentDescription = null,
                                tint = if (isSelected) SakuraPink else MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        },
                        label = {
                            Text(
                                when (option) {
                                    ThemePreference.LIGHT -> "Sakura Day"
                                    ThemePreference.DARK -> "Neon Night"
                                    ThemePreference.SYSTEM -> "System"
                                },
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = SakuraPink.copy(alpha = 0.25f),
                            selectedLabelColor = SakuraPink,
                        ),
                    )
                }
            }

            PennyWiseCard(modifier = Modifier.fillMaxWidth()) {
                Text("App Preferences", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    "Currency: Indian Rupee (₹)\nLocal DataStore Sync: Active\nVersion: 1.0.0 (Sakura Nights Edition)",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    lineHeight = 22.sp,
                )
            }

            PennyWisePrimaryButton(
                text = "Save Character Profile",
                onClick = {
                    onSave(
                        profile.copy(
                            displayName = name,
                            avatar = avatar,
                            theme = theme,
                        ),
                    )
                },
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}
