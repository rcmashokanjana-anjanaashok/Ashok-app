package com.example.ui.screens

import android.content.Context
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.CloudDownload
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Handshake
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.material.icons.filled.Savings
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Vibration
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Switch
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.example.data.model.CategoryItem
import com.example.data.model.LoanRecord
import com.example.data.model.PersonalNote
import com.example.data.model.SavingsGoal
import com.example.ui.components.ConfirmDeleteDialog
import com.example.ui.theme.ExpenseRed
import com.example.ui.theme.ExpenseRedLight
import com.example.ui.theme.IncomeGreen
import com.example.ui.theme.IncomeGreenLight
import com.example.ui.theme.TealPrimary
import com.example.util.Formatters
import com.example.viewmodel.DailyLifeViewModel

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun ProfileScreen(
    viewModel: DailyLifeViewModel,
    onNavigateToSettings: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val savingsGoals by viewModel.savingsGoals.collectAsStateWithLifecycle()
    val loans by viewModel.loans.collectAsStateWithLifecycle()
    val notes by viewModel.notes.collectAsStateWithLifecycle()
    val categories by viewModel.categories.collectAsStateWithLifecycle()

    // Profile state
    val userName by viewModel.userName.collectAsStateWithLifecycle()
    val userEmail by viewModel.userEmail.collectAsStateWithLifecycle()
    val userPhone by viewModel.userPhone.collectAsStateWithLifecycle()
    val profilePhotoUri by viewModel.profilePhotoUri.collectAsStateWithLifecycle()
    val avatarPreset by viewModel.avatarPreset.collectAsStateWithLifecycle()

    // Settings on/off toggles
    val isDailyReminderEnabled by viewModel.isDailyReminderEnabled.collectAsStateWithLifecycle()
    val isBillAlertEnabled by viewModel.isBillAlertEnabled.collectAsStateWithLifecycle()
    val isTaskReminderEnabled by viewModel.isTaskReminderEnabled.collectAsStateWithLifecycle()
    val isBudgetAlertEnabled by viewModel.isBudgetAlertEnabled.collectAsStateWithLifecycle()

    val themeMode by viewModel.themeMode.collectAsStateWithLifecycle()
    val isHapticFeedbackEnabled by viewModel.isHapticFeedbackEnabled.collectAsStateWithLifecycle()
    val isMaskBalanceEnabled by viewModel.isMaskBalanceEnabled.collectAsStateWithLifecycle()
    val isDecimalPlacesEnabled by viewModel.isDecimalPlacesEnabled.collectAsStateWithLifecycle()
    val currencySymbol by viewModel.currencySymbol.collectAsStateWithLifecycle()

    var selectedSectionTab by remember { mutableIntStateOf(0) }
    val sectionTabs = listOf("Settings & Toggles", "Savings", "Khata/Loans", "Notes", "Backup & Restore")

    // Modals
    var showEditProfileDialog by remember { mutableStateOf(false) }
    var showAvatarPresetDialog by remember { mutableStateOf(false) }
    var showAddGoalDialog by remember { mutableStateOf(false) }
    var selectedGoalForDeposit by remember { mutableStateOf<SavingsGoal?>(null) }
    var showAddLoanDialog by remember { mutableStateOf(false) }
    var selectedLoanForRepay by remember { mutableStateOf<LoanRecord?>(null) }
    var showSetPinDialog by remember { mutableStateOf(false) }
    var showAddCategoryDialog by remember { mutableStateOf(false) }
    var itemToDelete by remember { mutableStateOf<Any?>(null) }

    // Android zero-permission Photo Picker launcher
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            viewModel.setProfilePhotoUri(uri.toString())
            Toast.makeText(context, "Profile photo updated!", Toast.LENGTH_SHORT).show()
        }
    }

    // Backup Document Launcher
    val backupLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("application/json")
    ) { uri: Uri? ->
        if (uri != null) {
            try {
                context.contentResolver.openOutputStream(uri)?.use { os ->
                    val backupJson = viewModel.generateBackupJson()
                    os.write(backupJson.toByteArray())
                }
                Toast.makeText(context, "Database backup saved successfully!", Toast.LENGTH_LONG).show()
            } catch (e: Exception) {
                Toast.makeText(context, "Backup failed: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Restore Document Launcher
    val restoreLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri: Uri? ->
        if (uri != null) {
            try {
                val jsonString = context.contentResolver.openInputStream(uri)?.bufferedReader().use { it?.readText() }
                if (!jsonString.isNullOrBlank()) {
                    viewModel.restoreFromJson(jsonString) { success, msg ->
                        Toast.makeText(context, msg, Toast.LENGTH_LONG).show()
                    }
                }
            } catch (e: Exception) {
                Toast.makeText(context, "Restore failed: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    val presetGradients = listOf(
        Brush.linearGradient(listOf(TealPrimary, Color(0xFF004D40))),
        Brush.linearGradient(listOf(Color(0xFF1E3A8A), Color(0xFF3B82F6))),
        Brush.linearGradient(listOf(Color(0xFF7C3AED), Color(0xFFA855F7))),
        Brush.linearGradient(listOf(Color(0xFFF59E0B), Color(0xFFD97706))),
        Brush.linearGradient(listOf(Color(0xFFE11D48), Color(0xFFFB7185))),
        Brush.linearGradient(listOf(Color(0xFF059669), Color(0xFF34D399)))
    )

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("profile_screen"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // --- INTERACTIVE PROFILE CARD WITH PHOTO CHANGE ---
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("profile_header_card"),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Profile Avatar with Camera badge
                        Box(contentAlignment = Alignment.BottomEnd) {
                            Box(
                                modifier = Modifier
                                    .size(76.dp)
                                    .clip(CircleShape)
                                    .border(2.dp, TealPrimary.copy(alpha = 0.3f), CircleShape)
                                    .clickable {
                                        photoPickerLauncher.launch(
                                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                        )
                                    }
                                    .testTag("profile_avatar_box"),
                                contentAlignment = Alignment.Center
                            ) {
                                if (!profilePhotoUri.isNullOrBlank()) {
                                    AsyncImage(
                                        model = profilePhotoUri,
                                        contentDescription = "Profile Photo",
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier.fillMaxSize()
                                    )
                                } else {
                                    val bgBrush = presetGradients.getOrElse(avatarPreset) { presetGradients[0] }
                                    Box(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .background(bgBrush),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = userName.take(1).uppercase(),
                                            fontSize = 32.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White
                                        )
                                    }
                                }
                            }

                            // Camera change icon badge
                            Box(
                                modifier = Modifier
                                    .size(26.dp)
                                    .clip(CircleShape)
                                    .background(TealPrimary)
                                    .clickable {
                                        photoPickerLauncher.launch(
                                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                        )
                                    }
                                    .testTag("change_photo_badge"),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CameraAlt,
                                    contentDescription = "Change Photo",
                                    tint = Color.White,
                                    modifier = Modifier.size(14.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(16.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = userName,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = userEmail,
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.Gray
                            )
                            Text(
                                text = userPhone,
                                style = MaterialTheme.typography.labelSmall,
                                color = TealPrimary,
                                fontWeight = FontWeight.Medium
                            )
                        }

                        IconButton(
                            onClick = { showEditProfileDialog = true },
                            modifier = Modifier.testTag("edit_profile_btn")
                        ) {
                            Icon(Icons.Default.Edit, contentDescription = "Edit Profile", tint = TealPrimary)
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Photo action row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            onClick = {
                                photoPickerLauncher.launch(
                                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                )
                            },
                            modifier = Modifier.weight(1f).height(36.dp),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Icon(Icons.Default.CameraAlt, contentDescription = null, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Pick Photo", fontSize = 11.sp)
                        }

                        OutlinedButton(
                            onClick = { showAvatarPresetDialog = true },
                            modifier = Modifier.weight(1f).height(36.dp),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Icon(Icons.Default.Face, contentDescription = null, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Avatars", fontSize = 11.sp)
                        }

                        Button(
                            onClick = onNavigateToSettings,
                            colors = ButtonDefaults.buttonColors(containerColor = TealPrimary),
                            modifier = Modifier.weight(1f).height(36.dp),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Icon(Icons.Default.Settings, contentDescription = null, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Settings", fontSize = 11.sp)
                        }
                    }
                }
            }
        }

        // Section Tabs Row
        item {
            ScrollableTabRow(
                selectedTabIndex = selectedSectionTab,
                edgePadding = 0.dp,
                divider = {},
                containerColor = Color.Transparent
            ) {
                sectionTabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedSectionTab == index,
                        onClick = { selectedSectionTab = index },
                        text = {
                            Text(
                                text = title,
                                fontSize = 13.sp,
                                fontWeight = if (selectedSectionTab == index) FontWeight.Bold else FontWeight.Normal,
                                color = if (selectedSectionTab == index) TealPrimary else Color.Gray
                            )
                        }
                    )
                }
            }
        }

        // Tab Content
        when (selectedSectionTab) {
            0 -> {
                // --- SETTINGS WITH ON/OFF TOGGLES ---

                // 1. Notification Reminders (On/Off)
                item {
                    SettingsGroupCard(title = "Notification Reminders", icon = Icons.Default.Notifications) {
                        SettingsToggleRow(
                            title = "Daily Planner Notifications",
                            subtitle = "Daily morning reminder for pending agenda & finances",
                            checked = isDailyReminderEnabled,
                            onCheckedChange = { viewModel.setDailyReminderEnabled(it) },
                            testTag = "toggle_daily_reminder"
                        )
                        SettingsToggleRow(
                            title = "Bill Due Date Alerts",
                            subtitle = "Automatic alerts before utilities, rent, and card bills expire",
                            checked = isBillAlertEnabled,
                            onCheckedChange = { viewModel.setBillAlertEnabled(it) },
                            testTag = "toggle_bill_alert"
                        )
                        SettingsToggleRow(
                            title = "Task Due Reminders",
                            subtitle = "Local alerts when tasks are scheduled for the day",
                            checked = isTaskReminderEnabled,
                            onCheckedChange = { viewModel.setTaskReminderEnabled(it) },
                            testTag = "toggle_task_reminder"
                        )
                        SettingsToggleRow(
                            title = "Monthly Budget Limit Alerts",
                            subtitle = "Notify when monthly spending exceeds 80% of budget",
                            checked = isBudgetAlertEnabled,
                            onCheckedChange = { viewModel.setBudgetAlertEnabled(it) },
                            testTag = "toggle_budget_alert"
                        )
                    }
                }

                // 2. Display & Formatting Preferences (On/Off)
                item {
                    SettingsGroupCard(title = "Preferences & Privacy", icon = Icons.Default.Settings) {
                        SettingsToggleRow(
                            title = "Mask Balance on Dashboard",
                            subtitle = "Hide net balance on home screen to protect privacy",
                            checked = isMaskBalanceEnabled,
                            onCheckedChange = { viewModel.setMaskBalanceEnabled(it) },
                            testTag = "toggle_mask_balance"
                        )
                        SettingsToggleRow(
                            title = "Show Decimals (.00)",
                            subtitle = "Display full paise and cents or round off values",
                            checked = isDecimalPlacesEnabled,
                            onCheckedChange = { viewModel.setDecimalPlacesEnabled(it) },
                            testTag = "toggle_decimals"
                        )
                        SettingsToggleRow(
                            title = "Haptic Vibration Feedback",
                            subtitle = "Vibrate softly on keypad and transaction saves",
                            checked = isHapticFeedbackEnabled,
                            onCheckedChange = { viewModel.setHapticFeedbackEnabled(it) },
                            testTag = "toggle_haptic"
                        )

                        // Currency Selector
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text("Currency Symbol", fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                                Text("Standard transaction currency", fontSize = 11.sp, color = Color.Gray)
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

                // 3. App Security & Passcode (On/Off)
                item {
                    SettingsGroupCard(title = "App Lock & Security", icon = Icons.Default.Security) {
                        SettingsToggleRow(
                            title = "4-Digit Passcode Protection",
                            subtitle = "Require PIN verification whenever app is opened",
                            checked = viewModel.securityPrefs.isPinEnabled,
                            onCheckedChange = { enabled ->
                                if (enabled) {
                                    showSetPinDialog = true
                                } else {
                                    viewModel.disableAppPin()
                                    Toast.makeText(context, "PIN lock turned off", Toast.LENGTH_SHORT).show()
                                }
                            },
                            testTag = "toggle_pin_lock"
                        )

                        if (viewModel.securityPrefs.isPinEnabled) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 8.dp),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                OutlinedButton(
                                    onClick = { showSetPinDialog = true },
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text("Change PIN")
                                }
                                Button(
                                    onClick = { viewModel.lockApp() },
                                    colors = ButtonDefaults.buttonColors(containerColor = TealPrimary),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Icon(Icons.Default.Lock, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Lock Now")
                                }
                            }
                        }
                    }
                }

                // 4. Categories Management
                item {
                    SettingsGroupCard(title = "Categories Management", icon = Icons.Default.Category) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("${categories.size} Active Categories", fontSize = 13.sp, color = Color.DarkGray)
                            Button(
                                onClick = { showAddCategoryDialog = true },
                                colors = ButtonDefaults.buttonColors(containerColor = TealPrimary),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.height(34.dp)
                            ) {
                                Text("+ Add Category", fontSize = 11.sp)
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            categories.take(12).forEach { cat ->
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(Color(0xFFF1F5F9))
                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Text(cat.name, fontSize = 11.sp, color = Color.DarkGray)
                                }
                            }
                        }
                    }
                }
            }

            1 -> {
                // SAVINGS GOALS
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "My Savings Goals (${savingsGoals.size})",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Button(
                            onClick = { showAddGoalDialog = true },
                            colors = ButtonDefaults.buttonColors(containerColor = TealPrimary),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.testTag("add_savings_goal_button")
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("New Goal", fontSize = 12.sp)
                        }
                    }
                }

                if (savingsGoals.isEmpty()) {
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                        ) {
                            Box(modifier = Modifier.padding(24.dp), contentAlignment = Alignment.Center) {
                                Text("No savings goals created yet. Set a target to start saving!", color = Color.Gray, fontSize = 13.sp)
                            }
                        }
                    }
                } else {
                    items(savingsGoals) { goal ->
                        val pct = if (goal.targetAmount > 0) ((goal.currentAmount / goal.targetAmount) * 100).toInt() else 0
                        val progress = if (goal.targetAmount > 0) (goal.currentAmount / goal.targetAmount).toFloat().coerceIn(0f, 1f) else 0f

                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                        ) {
                            Column(
                                modifier = Modifier.padding(14.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text(goal.title, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                                        Text("Target Date: ${Formatters.formatDate(goal.targetDate)}", fontSize = 11.sp, color = Color.Gray)
                                    }
                                    IconButton(onClick = { itemToDelete = goal }) {
                                        Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color.Gray, modifier = Modifier.size(18.dp))
                                    }
                                }

                                LinearProgressIndicator(
                                    progress = { progress },
                                    modifier = Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(4.dp)),
                                    color = IncomeGreen,
                                    trackColor = Color(0xFFE2E8F0)
                                )

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "${Formatters.formatCurrency(goal.currentAmount)} of ${Formatters.formatCurrency(goal.targetAmount)} ($pct%)",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                    Button(
                                        onClick = { selectedGoalForDeposit = goal },
                                        shape = RoundedCornerShape(8.dp),
                                        colors = ButtonDefaults.buttonColors(containerColor = IncomeGreen),
                                        modifier = Modifier.height(34.dp)
                                    ) {
                                        Text("+ Deposit", fontSize = 11.sp)
                                    }
                                }
                            }
                        }
                    }
                }
            }

            2 -> {
                // KHATA / LOANS & BORROWING
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Khata (Loans & Debts)",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Button(
                            onClick = { showAddLoanDialog = true },
                            colors = ButtonDefaults.buttonColors(containerColor = TealPrimary),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.testTag("add_loan_button")
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Add Record", fontSize = 12.sp)
                        }
                    }
                }

                if (loans.isEmpty()) {
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                        ) {
                            Box(modifier = Modifier.padding(24.dp), contentAlignment = Alignment.Center) {
                                Text("No loan or borrowing entries found.", color = Color.Gray, fontSize = 13.sp)
                            }
                        }
                    }
                } else {
                    items(loans) { loan ->
                        val isLent = loan.type == "LENT"
                        val pending = (loan.originalAmount - loan.paidAmount).coerceAtLeast(0.0)

                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                        ) {
                            Column(
                                modifier = Modifier.padding(14.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(6.dp))
                                                .background(if (isLent) IncomeGreenLight else ExpenseRedLight)
                                                .padding(horizontal = 8.dp, vertical = 4.dp)
                                        ) {
                                            Text(
                                                text = if (isLent) "GAVE (To Receive)" else "TOOK (To Pay)",
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = if (isLent) IncomeGreen else ExpenseRed
                                            )
                                        }
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(loan.personName, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                                    }

                                    IconButton(onClick = { itemToDelete = loan }) {
                                        Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color.Gray, modifier = Modifier.size(18.dp))
                                    }
                                }

                                if (loan.notes.isNotBlank()) {
                                    Text(loan.notes, style = MaterialTheme.typography.bodySmall, color = Color.DarkGray)
                                }

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("Due: ${Formatters.formatDate(loan.dueDate)}", fontSize = 11.sp, color = Color.Gray)
                                    Text(
                                        text = if (loan.isSettled) "SETTLED" else "Pending: ${Formatters.formatCurrency(pending)}",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (loan.isSettled) IncomeGreen else (if (isLent) IncomeGreen else ExpenseRed)
                                    )
                                }

                                if (!loan.isSettled) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.End
                                    ) {
                                        OutlinedButton(
                                            onClick = { selectedLoanForRepay = loan },
                                            modifier = Modifier.height(34.dp),
                                            shape = RoundedCornerShape(8.dp)
                                        ) {
                                            Text("Record Repayment", fontSize = 11.sp)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            3 -> {
                // PERSONAL NOTES & TO-DO LISTS
                item {
                    Text(
                        text = "Notes & Checklists (${notes.size})",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }

                if (notes.isEmpty()) {
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                        ) {
                            Box(modifier = Modifier.padding(24.dp), contentAlignment = Alignment.Center) {
                                Text("No notes saved yet. Use Quick Add (+ Note) to create one.", color = Color.Gray, fontSize = 13.sp)
                            }
                        }
                    }
                } else {
                    items(notes) { note ->
                        val cardBg = try {
                            Color(android.graphics.Color.parseColor(note.colorTagHex))
                        } catch (_: Exception) {
                            Color.White
                        }

                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(containerColor = cardBg),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                        ) {
                            Column(
                                modifier = Modifier.padding(14.dp),
                                verticalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        if (note.isPinned) {
                                            Icon(Icons.Default.PushPin, contentDescription = "Pinned", tint = TealPrimary, modifier = Modifier.size(16.dp))
                                            Spacer(modifier = Modifier.width(4.dp))
                                        }
                                        Text(note.title, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                                    }

                                    Row {
                                        IconButton(onClick = { viewModel.toggleNotePinned(note) }) {
                                            Icon(Icons.Default.PushPin, contentDescription = "Pin", tint = if (note.isPinned) TealPrimary else Color.LightGray)
                                        }
                                        IconButton(onClick = { itemToDelete = note }) {
                                            Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color.Gray, modifier = Modifier.size(18.dp))
                                        }
                                    }
                                }

                                if (note.content.isNotBlank()) {
                                    Text(note.content, style = MaterialTheme.typography.bodyMedium, color = Color.DarkGray)
                                }

                                Text(
                                    text = "Updated: ${Formatters.formatFriendlyDate(note.updatedAt)}",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Color.Gray
                                )
                            }
                        }
                    }
                }
            }

            4 -> {
                // DATA BACKUP & RESTORE
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.CloudUpload, contentDescription = null, tint = TealPrimary)
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text("Local Database Backup", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleSmall)
                                    Text("Export entire snapshot as JSON file on your device", fontSize = 11.sp, color = Color.Gray)
                                }
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Button(
                                    onClick = {
                                        val fileName = "DailyLife_Backup_${System.currentTimeMillis()}.json"
                                        backupLauncher.launch(fileName)
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = TealPrimary),
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier.weight(1f).testTag("backup_json_button")
                                ) {
                                    Icon(Icons.Default.CloudUpload, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Export Backup", fontSize = 12.sp)
                                }

                                OutlinedButton(
                                    onClick = {
                                        restoreLauncher.launch(arrayOf("application/json"))
                                    },
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier.weight(1f).testTag("restore_json_button")
                                ) {
                                    Icon(Icons.Default.CloudDownload, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Restore Backup", fontSize = 12.sp)
                                }
                            }
                        }
                    }
                }

                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.Info, contentDescription = null, tint = Color.Gray)
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text("DailyLife - Personal Manager v1.0", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
                                Text("Production Room SQLite • MVVM • Zero Cloud Leaks", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                            }
                        }
                    }
                }
            }
        }

        item { Spacer(modifier = Modifier.height(72.dp)) }
    }

    // --- DIALOGS ---

    // Edit Profile Info Dialog
    if (showEditProfileDialog) {
        var editName by remember { mutableStateOf(userName) }
        var editEmail by remember { mutableStateOf(userEmail) }
        var editPhone by remember { mutableStateOf(userPhone) }

        AlertDialog(
            onDismissRequest = { showEditProfileDialog = false },
            title = { Text("Edit Profile Details", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = editName,
                        onValueChange = { editName = it },
                        label = { Text("Full Name") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth().testTag("edit_name_input")
                    )
                    OutlinedTextField(
                        value = editEmail,
                        onValueChange = { editEmail = it },
                        label = { Text("Email Address") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth().testTag("edit_email_input")
                    )
                    OutlinedTextField(
                        value = editPhone,
                        onValueChange = { editPhone = it },
                        label = { Text("Phone Number") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth().testTag("edit_phone_input")
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (editName.isNotBlank()) {
                            viewModel.updateProfile(editName.trim(), editEmail.trim(), editPhone.trim())
                            showEditProfileDialog = false
                            Toast.makeText(context, "Profile saved!", Toast.LENGTH_SHORT).show()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = TealPrimary)
                ) {
                    Text("Save Profile")
                }
            },
            dismissButton = {
                TextButton(onClick = { showEditProfileDialog = false }) { Text("Cancel") }
            }
        )
    }

    // Choose Avatar Preset Dialog
    if (showAvatarPresetDialog) {
        AlertDialog(
            onDismissRequest = { showAvatarPresetDialog = false },
            title = { Text("Choose Avatar Style", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("Pick an avatar preset color:")
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        presetGradients.forEachIndexed { index, brush ->
                            Box(
                                modifier = Modifier
                                    .size(44.dp)
                                    .clip(CircleShape)
                                    .background(brush)
                                    .clickable {
                                        viewModel.setAvatarPreset(index)
                                        showAvatarPresetDialog = false
                                        Toast.makeText(context, "Avatar preset selected!", Toast.LENGTH_SHORT).show()
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = userName.take(1).uppercase(),
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 18.sp
                                )
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showAvatarPresetDialog = false }) { Text("Done") }
            }
        )
    }

    // Set PIN Dialog
    if (showSetPinDialog) {
        var pinText by remember { mutableStateOf("") }
        var error by remember { mutableStateOf<String?>(null) }

        AlertDialog(
            onDismissRequest = { showSetPinDialog = false },
            title = { Text("Set 4-Digit Passcode", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Enter a 4-digit numeric PIN for securing your data.")
                    OutlinedTextField(
                        value = pinText,
                        onValueChange = { if (it.length <= 4) pinText = it },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth().testTag("pin_setup_input")
                    )
                    if (error != null) {
                        Text(error ?: "", color = ExpenseRed, fontSize = 12.sp)
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (pinText.length == 4) {
                            viewModel.setAppPin(pinText)
                            Toast.makeText(context, "PIN set successfully!", Toast.LENGTH_SHORT).show()
                            showSetPinDialog = false
                        } else {
                            error = "PIN must be exactly 4 digits."
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = TealPrimary)
                ) {
                    Text("Save PIN")
                }
            },
            dismissButton = {
                TextButton(onClick = { showSetPinDialog = false }) { Text("Cancel") }
            }
        )
    }

    // Add Goal Dialog
    if (showAddGoalDialog) {
        var title by remember { mutableStateOf("") }
        var targetAmount by remember { mutableStateOf("") }

        AlertDialog(
            onDismissRequest = { showAddGoalDialog = false },
            title = { Text("New Savings Goal", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = title,
                        onValueChange = { title = it },
                        label = { Text("Goal Title (e.g. Vacation, Bike, Emergency)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth().testTag("goal_title_input")
                    )
                    OutlinedTextField(
                        value = targetAmount,
                        onValueChange = { targetAmount = it },
                        label = { Text("Target Amount (₹ INR)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth().testTag("goal_amount_input")
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val amt = targetAmount.toDoubleOrNull()
                        if (title.isNotBlank() && amt != null && amt > 0) {
                            val targetDate = System.currentTimeMillis() + (180L * 86400000L)
                            viewModel.addSavingsGoal(title = title.trim(), targetAmount = amt, targetDate = targetDate)
                            showAddGoalDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = TealPrimary)
                ) {
                    Text("Create Goal")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddGoalDialog = false }) { Text("Cancel") }
            }
        )
    }

    // Deposit to Goal Dialog
    if (selectedGoalForDeposit != null) {
        var depositAmount by remember { mutableStateOf("") }
        var note by remember { mutableStateOf("") }

        AlertDialog(
            onDismissRequest = { selectedGoalForDeposit = null },
            title = { Text("Deposit to ${selectedGoalForDeposit!!.title}", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = depositAmount,
                        onValueChange = { depositAmount = it },
                        label = { Text("Deposit Amount (₹ INR)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = note,
                        onValueChange = { note = it },
                        label = { Text("Note / Source") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val amt = depositAmount.toDoubleOrNull()
                        if (amt != null && amt > 0) {
                            viewModel.addSavingsDeposit(selectedGoalForDeposit!!, amt, note)
                            selectedGoalForDeposit = null
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = IncomeGreen)
                ) {
                    Text("Record Deposit")
                }
            },
            dismissButton = {
                TextButton(onClick = { selectedGoalForDeposit = null }) { Text("Cancel") }
            }
        )
    }

    // Add Loan Dialog
    if (showAddLoanDialog) {
        var personName by remember { mutableStateOf("") }
        var phone by remember { mutableStateOf("") }
        var amountText by remember { mutableStateOf("") }
        var type by remember { mutableStateOf("LENT") }
        var notesText by remember { mutableStateOf("") }

        AlertDialog(
            onDismissRequest = { showAddLoanDialog = false },
            title = { Text("Record Khata / Debt", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        FilterChip(
                            selected = type == "LENT",
                            onClick = { type = "LENT" },
                            label = { Text("Gave (Lent)") }
                        )
                        FilterChip(
                            selected = type == "BORROWED",
                            onClick = { type = "BORROWED" },
                            label = { Text("Took (Borrowed)") }
                        )
                    }

                    OutlinedTextField(
                        value = personName,
                        onValueChange = { personName = it },
                        label = { Text("Person Name *") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth().testTag("loan_person_input")
                    )

                    OutlinedTextField(
                        value = phone,
                        onValueChange = { phone = it },
                        label = { Text("Phone Number") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = amountText,
                        onValueChange = { amountText = it },
                        label = { Text("Amount (₹ INR) *") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth().testTag("loan_amount_input")
                    )

                    OutlinedTextField(
                        value = notesText,
                        onValueChange = { notesText = it },
                        label = { Text("Notes / Purpose") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val amt = amountText.toDoubleOrNull()
                        if (personName.isNotBlank() && amt != null && amt > 0) {
                            val due = System.currentTimeMillis() + (30L * 86400000L)
                            viewModel.addLoan(
                                personName = personName.trim(),
                                phoneNumber = phone.trim(),
                                type = type,
                                originalAmount = amt,
                                dueDate = due,
                                notes = notesText.trim()
                            )
                            showAddLoanDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = TealPrimary)
                ) {
                    Text("Save Record")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddLoanDialog = false }) { Text("Cancel") }
            }
        )
    }

    // Record Loan Payment Dialog
    if (selectedLoanForRepay != null) {
        var repayAmount by remember { mutableStateOf("") }
        var repayNote by remember { mutableStateOf("") }

        AlertDialog(
            onDismissRequest = { selectedLoanForRepay = null },
            title = { Text("Record Repayment for ${selectedLoanForRepay!!.personName}", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("Pending: ${Formatters.formatCurrency(selectedLoanForRepay!!.originalAmount - selectedLoanForRepay!!.paidAmount)}")
                    OutlinedTextField(
                        value = repayAmount,
                        onValueChange = { repayAmount = it },
                        label = { Text("Payment Amount (₹ INR)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = repayNote,
                        onValueChange = { repayNote = it },
                        label = { Text("Note / Mode") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val amt = repayAmount.toDoubleOrNull()
                        if (amt != null && amt > 0) {
                            viewModel.recordLoanPayment(selectedLoanForRepay!!, amt, repayNote)
                            selectedLoanForRepay = null
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = TealPrimary)
                ) {
                    Text("Record Payment")
                }
            },
            dismissButton = {
                TextButton(onClick = { selectedLoanForRepay = null }) { Text("Cancel") }
            }
        )
    }

    // Add Category Dialog
    if (showAddCategoryDialog) {
        var catName by remember { mutableStateOf("") }

        AlertDialog(
            onDismissRequest = { showAddCategoryDialog = false },
            title = { Text("Add Custom Category", fontWeight = FontWeight.Bold) },
            text = {
                OutlinedTextField(
                    value = catName,
                    onValueChange = { catName = it },
                    label = { Text("Category Name") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("custom_cat_input")
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (catName.isNotBlank()) {
                            viewModel.addCategory(catName.trim())
                            showAddCategoryDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = TealPrimary)
                ) {
                    Text("Add")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddCategoryDialog = false }) { Text("Cancel") }
            }
        )
    }

    // Delete Confirmation
    if (itemToDelete != null) {
        ConfirmDeleteDialog(
            title = "Delete Item",
            message = "Are you sure you want to permanently delete this item?",
            onConfirm = {
                val item = itemToDelete
                if (item is SavingsGoal) viewModel.deleteSavingsGoal(item)
                if (item is LoanRecord) viewModel.deleteLoan(item)
                if (item is PersonalNote) viewModel.deleteNote(item)
                itemToDelete = null
            },
            onDismiss = { itemToDelete = null }
        )
    }
}

@Composable
fun SettingsGroupCard(
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
                Icon(imageVector = icon, contentDescription = null, tint = TealPrimary, modifier = Modifier.size(20.dp))
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
fun SettingsToggleRow(
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    testTag: String = "settings_toggle"
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f).padding(end = 12.dp)) {
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
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            modifier = Modifier.testTag(testTag)
        )
    }
}
