package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.components.PinLockScreen
import com.example.ui.components.QuickAddBottomSheet
import com.example.ui.screens.CalendarScreen
import com.example.ui.screens.ExpensesScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.ProfileScreen
import com.example.ui.screens.ReportsScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.TealPrimary
import com.example.viewmodel.DailyLifeViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val viewModel: DailyLifeViewModel = viewModel()
            val isDarkModeDataStore by viewModel.isDarkModeDataStore.collectAsStateWithLifecycle()
            val themeMode by viewModel.themeMode.collectAsStateWithLifecycle()

            val isDarkTheme = when {
                themeMode == "DARK" -> true
                themeMode == "LIGHT" -> false
                isDarkModeDataStore -> true
                else -> isSystemInDarkTheme()
            }

            MyApplicationTheme(darkTheme = isDarkTheme) {
                DailyLifeAppContent(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun DailyLifeAppContent(
    viewModel: DailyLifeViewModel
) {
    val isUnlocked by viewModel.isUnlocked.collectAsStateWithLifecycle()
    var currentScreenIndex by remember { mutableIntStateOf(0) }
    var quickAddInitialTab by remember { mutableStateOf<Int?>(null) }
    var isShowingSettings by remember { mutableStateOf(false) }

    if (!isUnlocked) {
        PinLockScreen(
            onUnlockAttempt = { pin -> viewModel.unlockWithPin(pin) }
        )
    } else if (isShowingSettings) {
        BackHandler {
            isShowingSettings = false
        }
        SettingsScreen(
            viewModel = viewModel,
            onNavigateBack = { isShowingSettings = false }
        )
    } else {
        // Back press handling: If on secondary tab, return to Home (index 0)
        BackHandler(enabled = currentScreenIndex != 0) {
            currentScreenIndex = 0
        }

        Scaffold(
            modifier = Modifier.fillMaxSize(),
            bottomBar = {
                NavigationBar(
                    containerColor = MaterialTheme.colorScheme.surface,
                    modifier = Modifier.testTag("bottom_nav_bar")
                ) {
                    val navItems = listOf(
                        Triple("Home", Icons.Default.Home, 0),
                        Triple("Expenses", Icons.Default.AccountBalanceWallet, 1),
                        Triple("Calendar", Icons.Default.CalendarMonth, 2),
                        Triple("Reports", Icons.Default.BarChart, 3),
                        Triple("Profile", Icons.Default.Person, 4)
                    )

                    navItems.forEach { (label, icon, index) ->
                        NavigationBarItem(
                            icon = { Icon(icon, contentDescription = label) },
                            label = { Text(label) },
                            selected = currentScreenIndex == index,
                            onClick = { currentScreenIndex = index },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = TealPrimary,
                                selectedTextColor = TealPrimary,
                                indicatorColor = TealPrimary.copy(alpha = 0.15f)
                            ),
                            modifier = Modifier.testTag("nav_item_$label")
                        )
                    }
                }
            }
        ) { innerPadding ->
            val screenModifier = Modifier.padding(innerPadding)

            when (currentScreenIndex) {
                0 -> HomeScreen(
                    viewModel = viewModel,
                    onNavigateToExpenses = { currentScreenIndex = 1 },
                    onNavigateToCalendar = { currentScreenIndex = 2 },
                    onNavigateToReports = { currentScreenIndex = 3 },
                    onNavigateToProfile = { currentScreenIndex = 4 },
                    onNavigateToSettings = { isShowingSettings = true },
                    onOpenQuickAdd = { tab -> quickAddInitialTab = tab },
                    modifier = screenModifier
                )
                1 -> ExpensesScreen(
                    viewModel = viewModel,
                    onOpenQuickAdd = { tab -> quickAddInitialTab = tab },
                    modifier = screenModifier
                )
                2 -> CalendarScreen(
                    viewModel = viewModel,
                    onOpenQuickAdd = { tab -> quickAddInitialTab = tab },
                    modifier = screenModifier
                )
                3 -> ReportsScreen(
                    viewModel = viewModel,
                    modifier = screenModifier
                )
                4 -> ProfileScreen(
                    viewModel = viewModel,
                    onNavigateToSettings = { isShowingSettings = true },
                    modifier = screenModifier
                )
            }

            if (quickAddInitialTab != null) {
                QuickAddBottomSheet(
                    viewModel = viewModel,
                    initialTab = quickAddInitialTab!!,
                    onDismiss = { quickAddInitialTab = null }
                )
            }
        }
    }
}
