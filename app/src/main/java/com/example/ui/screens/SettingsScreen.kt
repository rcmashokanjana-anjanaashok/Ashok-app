package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Vibration
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.theme.TealPrimary
import com.example.viewmodel.DailyLifeViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: DailyLifeViewModel,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    // DataStore Persisted States
    val isDarkMode by viewModel.isDarkModeDataStore.collectAsStateWithLifecycle()
    val isNotificationReminders by viewModel.isNotificationRemindersDataStore.collectAsStateWithLifecycle()
    val isBiometricLock by viewModel.isBiometricLockDataStore.collectAsStateWithLifecycle()

    // Additional preferences
    val isHapticFeedback by viewModel.isHapticFeedbackEnabled.collectAsStateWithLifecycle()
    val isMaskBalance by viewModel.isMaskBalanceEnabled.collectAsStateWithLifecycle()
    val currencySymbol by viewModel.currencySymbol.collectAsStateWithLifecycle()

    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .testTag("settings_screen"),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Settings",
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleLarge
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = onNavigateBack,
                        modifier = Modifier.testTag("settings_back_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Section 1: Appearance & Theme
            item {
                SettingsSectionCard(
                    title = "Appearance & Theme",
                    icon = Icons.Default.Palette
                ) {
                    SettingsItemSwitch(
                        icon = Icons.Default.DarkMode,
                        iconTint = Color(0xFF6366F1),
                        title = "Dark Mode",
                        subtitle = "Persist dark theme across the entire application",
                        checked = isDarkMode,
                        onCheckedChange = { checked ->
                            viewModel.setDarkModeDataStore(checked)
                            Toast.makeText(
                                context,
                                if (checked) "Dark mode enabled (DataStore)" else "Light mode enabled (DataStore)",
                                Toast.LENGTH_SHORT
                            ).show()
                        },
                        testTag = "toggle_dark_mode"
                    )
                }
            }

            // Section 2: Notifications & Reminders
            item {
                SettingsSectionCard(
                    title = "Notifications & Reminders",
                    icon = Icons.Default.NotificationsActive
                ) {
                    SettingsItemSwitch(
                        icon = Icons.Default.NotificationsActive,
                        iconTint = Color(0xFF0284C7),
                        title = "Notification Reminders",
                        subtitle = "Receive daily task alerts and scheduled bill payment reminders",
                        checked = isNotificationReminders,
                        onCheckedChange = { checked ->
                            viewModel.setNotificationRemindersDataStore(checked)
                            Toast.makeText(
                                context,
                                if (checked) "Notification reminders active (DataStore)" else "Reminders disabled (DataStore)",
                                Toast.LENGTH_SHORT
                            ).show()
                        },
                        testTag = "toggle_notification_reminders"
                    )
                }
            }

            // Section 3: Privacy & Security
            item {
                SettingsSectionCard(
                    title = "Privacy & Security",
                    icon = Icons.Default.Security
                ) {
                    SettingsItemSwitch(
                        icon = Icons.Default.Fingerprint,
                        iconTint = TealPrimary,
                        title = "Biometric Lock",
                        subtitle = "Require biometric verification or device credentials to access records",
                        checked = isBiometricLock,
                        onCheckedChange = { checked ->
                            viewModel.setBiometricLockDataStore(checked)
                            Toast.makeText(
                                context,
                                if (checked) "Biometric lock activated (DataStore)" else "Biometric lock turned off (DataStore)",
                                Toast.LENGTH_SHORT
                            ).show()
                        },
                        testTag = "toggle_biometric_lock"
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    SettingsItemSwitch(
                        icon = Icons.Default.Visibility,
                        iconTint = Color(0xFFF59E0B),
                        title = "Mask Balance",
                        subtitle = "Hide sensitive total balance on home screen",
                        checked = isMaskBalance,
                        onCheckedChange = { viewModel.setMaskBalanceEnabled(it) },
                        testTag = "toggle_mask_balance_settings"
                    )
                }
            }

            // Section 4: General Preferences
            item {
                SettingsSectionCard(
                    title = "Preferences & Feedback",
                    icon = Icons.Default.Vibration
                ) {
                    SettingsItemSwitch(
                        icon = Icons.Default.Vibration,
                        iconTint = Color(0xFF8B5CF6),
                        title = "Haptic Vibration",
                        subtitle = "Provide tactile feedback on button presses",
                        checked = isHapticFeedback,
                        onCheckedChange = { viewModel.setHapticFeedbackEnabled(it) },
                        testTag = "toggle_haptic_feedback"
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("Currency Symbol", fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                            Text("Selected symbol across all screens", fontSize = 11.sp, color = Color.Gray)
                        }
                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            listOf("₹", "$", "€", "£").forEach { sym ->
                                FilterChip(
                                    selected = currencySymbol == sym,
                                    onClick = { viewModel.setCurrencySymbol(sym) },
                                    label = { Text(sym, fontWeight = FontWeight.Bold) }
                                )
                            }
                        }
                    }
                }
            }

            // Section 5: App Storage & Architecture Info
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(TealPrimary.copy(alpha = 0.1f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Info,
                                contentDescription = null,
                                tint = TealPrimary,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "Jetpack DataStore Preferences",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Preferences are stored securely in local DataStore with Kotlin Coroutines and reactive Flow.",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.Gray
                            )
                        }
                    }
                }
            }

            item { Spacer(modifier = Modifier.height(32.dp)) }
        }
    }
}

@Composable
private fun SettingsSectionCard(
    title: String,
    icon: ImageVector,
    content: @Composable () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 4.dp)
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = TealPrimary,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
            }
            content()
        }
    }
}

@Composable
private fun SettingsItemSwitch(
    icon: ImageVector,
    iconTint: Color,
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    testTag: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f).padding(end = 12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(iconTint.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconTint,
                    modifier = Modifier.size(18.dp)
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
            }
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = TealPrimary
            ),
            modifier = Modifier.testTag(testTag)
        )
    }
}
