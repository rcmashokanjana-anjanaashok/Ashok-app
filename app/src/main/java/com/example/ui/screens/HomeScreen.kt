package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CheckCircleOutline
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.TrendingDown
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.Bill
import com.example.data.model.Expense
import com.example.data.model.Income
import com.example.data.model.TaskItem
import com.example.ui.components.BudgetProgressCard
import com.example.ui.components.MonthlyBarChart
import com.example.ui.components.SummaryKpiCard
import com.example.ui.components.getCategoryIcon
import com.example.ui.theme.ExpenseRed
import com.example.ui.theme.ExpenseRedLight
import com.example.ui.theme.IncomeGreen
import com.example.ui.theme.IncomeGreenLight
import com.example.ui.theme.TealPrimary
import com.example.util.Formatters
import com.example.viewmodel.DailyLifeViewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import androidx.compose.material.icons.automirrored.filled.TrendingDown
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.ui.layout.ContentScale
import androidx.compose.foundation.border
import coil.compose.AsyncImage
import java.util.Locale

@Composable
fun HomeScreen(
    viewModel: DailyLifeViewModel,
    onNavigateToExpenses: () -> Unit,
    onNavigateToCalendar: () -> Unit,
    onNavigateToReports: () -> Unit,
    onNavigateToProfile: () -> Unit = {},
    onNavigateToSettings: () -> Unit = {},
    onOpenQuickAdd: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val summary by viewModel.dashboardSummary.collectAsStateWithLifecycle()
    val expenses by viewModel.expenses.collectAsStateWithLifecycle()
    val incomes by viewModel.incomes.collectAsStateWithLifecycle()
    val bills by viewModel.bills.collectAsStateWithLifecycle()
    val tasks by viewModel.tasks.collectAsStateWithLifecycle()

    val userName by viewModel.userName.collectAsStateWithLifecycle()
    val profilePhotoUri by viewModel.profilePhotoUri.collectAsStateWithLifecycle()
    val avatarPreset by viewModel.avatarPreset.collectAsStateWithLifecycle()
    val isMaskBalanceEnabled by viewModel.isMaskBalanceEnabled.collectAsStateWithLifecycle()
    var isBalanceRevealed by remember { mutableStateOf(false) }

    val currentDateStr = remember {
        val sdf = SimpleDateFormat("EEEE, dd MMMM yyyy", Locale.ENGLISH)
        sdf.format(Date())
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("home_screen"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Top Header with User Profile Greeting, Settings button & Avatar
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = currentDateStr,
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.Gray
                    )
                    Text(
                        text = "Hello, $userName 👋",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    IconButton(
                        onClick = onNavigateToSettings,
                        modifier = Modifier.testTag("home_settings_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "Settings",
                            tint = Color.DarkGray
                        )
                    }

                    // Profile Avatar Clickable
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .border(1.5.dp, TealPrimary, CircleShape)
                            .clickable { onNavigateToProfile() }
                            .testTag("home_profile_avatar"),
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
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(TealPrimary),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = userName.take(1).uppercase(),
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }
                        }
                    }
                }
            }
        }

        // Available Balance Hero Card (with Mask/Unmask toggle)
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("balance_hero_card"),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = TealPrimary),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Available Balance",
                            style = MaterialTheme.typography.titleSmall,
                            color = Color.White.copy(alpha = 0.85f)
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            if (isMaskBalanceEnabled) {
                                IconButton(
                                    onClick = { isBalanceRevealed = !isBalanceRevealed },
                                    modifier = Modifier.size(24.dp)
                                ) {
                                    Icon(
                                        imageVector = if (isBalanceRevealed) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                        contentDescription = "Toggle Balance Visibility",
                                        tint = Color.White.copy(alpha = 0.85f),
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(6.dp))
                            }
                            Icon(
                                imageVector = Icons.Default.AccountBalanceWallet,
                                contentDescription = null,
                                tint = Color.White.copy(alpha = 0.85f),
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }

                    val displayBalance = if (isMaskBalanceEnabled && !isBalanceRevealed) {
                        "₹ ••••••"
                    } else {
                        Formatters.formatCurrency(summary.availableBalance)
                    }

                    Text(
                        text = displayBalance,
                        style = MaterialTheme.typography.headlineMedium.copy(fontSize = 32.sp),
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.AutoMirrored.Filled.TrendingUp, contentDescription = null, tint = Color(0xFF81C784), modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Inc: ${Formatters.formatCurrency(summary.monthlyIncome, includeDecimals = false)}",
                                color = Color.White.copy(alpha = 0.9f),
                                style = MaterialTheme.typography.bodySmall
                            )
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.AutoMirrored.Filled.TrendingDown, contentDescription = null, tint = Color(0xFFFF8A80), modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Exp: ${Formatters.formatCurrency(summary.monthlyExpense, includeDecimals = false)}",
                                color = Color.White.copy(alpha = 0.9f),
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }
                }
            }
        }

        // Summary KPI row
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                SummaryKpiCard(
                    title = "TODAY'S SPENT",
                    amount = summary.todayExpense,
                    modifier = Modifier.weight(1f),
                    isIncome = false,
                    subtitle = "Track daily limit"
                )
                SummaryKpiCard(
                    title = "PENDING BILLS",
                    amount = summary.pendingBillsTotal,
                    modifier = Modifier.weight(1f),
                    isIncome = false,
                    subtitle = "${summary.pendingBillsCount} upcoming bills"
                )
            }
        }

        // Quick Action Buttons
        item {
            Text(
                text = "Quick Actions",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                QuickActionItem(
                    label = "+ Expense",
                    icon = Icons.AutoMirrored.Filled.TrendingDown,
                    containerColor = ExpenseRedLight,
                    iconColor = ExpenseRed,
                    onClick = { onOpenQuickAdd(0) }
                )
                QuickActionItem(
                    label = "+ Income",
                    icon = Icons.AutoMirrored.Filled.TrendingUp,
                    containerColor = IncomeGreenLight,
                    iconColor = IncomeGreen,
                    onClick = { onOpenQuickAdd(1) }
                )
                QuickActionItem(
                    label = "+ Task",
                    icon = Icons.Default.Assignment,
                    containerColor = Color(0xFFEDE9FE),
                    iconColor = Color(0xFF6D28D9),
                    onClick = { onOpenQuickAdd(2) }
                )
                QuickActionItem(
                    label = "+ Bill",
                    icon = Icons.Default.Receipt,
                    containerColor = Color(0xFFE0F2FE),
                    iconColor = Color(0xFF0284C7),
                    onClick = { onOpenQuickAdd(3) }
                )
                QuickActionItem(
                    label = "+ Note",
                    icon = Icons.Default.EditNote,
                    containerColor = Color(0xFFFEF3C7),
                    iconColor = Color(0xFFD97706),
                    onClick = { onOpenQuickAdd(4) }
                )
            }
        }

        // Monthly Budget Utilization Card
        item {
            BudgetProgressCard(
                spent = summary.monthlyExpense,
                budget = summary.monthlyBudget,
                onSetBudgetClick = onNavigateToExpenses
            )
        }

        // Monthly Expense Trend Chart
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
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Weekly Expense Trend",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Last 7 days spending pattern",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.Gray
                            )
                        }
                        TextButton(onClick = onNavigateToReports) {
                            Text("Analytics")
                        }
                    }

                    // Build last 7 days chart data
                    val last7Days = remember(expenses) {
                        val sdfDay = SimpleDateFormat("EEE", Locale.ENGLISH)
                        val cal = Calendar.getInstance()
                        val list = mutableListOf<Pair<String, Double>>()
                        for (i in 6 downTo 0) {
                            val targetCal = Calendar.getInstance().apply {
                                add(Calendar.DAY_OF_YEAR, -i)
                            }
                            val dayOfYear = targetCal.get(Calendar.DAY_OF_YEAR)
                            val year = targetCal.get(Calendar.YEAR)
                            val dayExpenses = expenses.filter {
                                val c = Calendar.getInstance().apply { timeInMillis = it.timestamp }
                                c.get(Calendar.YEAR) == year && c.get(Calendar.DAY_OF_YEAR) == dayOfYear
                            }.sumOf { it.amount }
                            list.add(Pair(sdfDay.format(targetCal.time), dayExpenses))
                        }
                        list
                    }

                    MonthlyBarChart(data = last7Days, barColor = ExpenseRed)
                }
            }
        }

        // Upcoming Bills Section
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Upcoming Bills",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
                TextButton(onClick = onNavigateToCalendar) {
                    Text("View Calendar")
                }
            }

            val pendingBills = bills.filter { !it.isPaid }.take(3)
            if (pendingBills.isEmpty()) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Box(modifier = Modifier.padding(16.dp)) {
                        Text("No pending bills for this period. Great job!", color = Color.Gray, style = MaterialTheme.typography.bodySmall)
                    }
                }
            } else {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    pendingBills.forEach { bill ->
                        BillItemRow(
                            bill = bill,
                            onTogglePaid = { viewModel.toggleBillPaid(bill) }
                        )
                    }
                }
            }
        }

        // Pending Tasks Section
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Today's Pending Tasks",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
                TextButton(onClick = onNavigateToCalendar) {
                    Text("Planner")
                }
            }

            val pendingTasks = tasks.filter { !it.isCompleted }.take(3)
            if (pendingTasks.isEmpty()) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Box(modifier = Modifier.padding(16.dp)) {
                        Text("All tasks completed for today!", color = Color.Gray, style = MaterialTheme.typography.bodySmall)
                    }
                }
            } else {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    pendingTasks.forEach { task ->
                        TaskItemRow(
                            task = task,
                            onToggleCompleted = { viewModel.toggleTaskCompleted(task) }
                        )
                    }
                }
            }
        }

        // Recent Transactions
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Recent Transactions",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
                TextButton(onClick = onNavigateToExpenses) {
                    Text("See All")
                }
            }

            // Combine recent 5 expenses & incomes
            val recentItems = remember(expenses, incomes) {
                val eList = expenses.map { TransactionDisplay(it.id, it.timestamp, it.category, it.description, it.amount, isIncome = false, it.paymentMethod) }
                val iList = incomes.map { TransactionDisplay(it.id, it.timestamp, it.category, it.description, it.amount, isIncome = true, it.paymentMethod) }
                (eList + iList).sortedByDescending { it.timestamp }.take(5)
            }

            if (recentItems.isEmpty()) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Box(modifier = Modifier.padding(16.dp)) {
                        Text("No transactions logged yet. Tap + to add.", color = Color.Gray, style = MaterialTheme.typography.bodySmall)
                    }
                }
            } else {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    recentItems.forEach { item ->
                        TransactionRowItem(item = item)
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(60.dp))
        }
    }
}

@Composable
private fun QuickActionItem(
    label: String,
    icon: ImageVector,
    containerColor: Color,
    iconColor: Color,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable { onClick() }
    ) {
        Box(
            modifier = Modifier
                .size(52.dp)
                .clip(CircleShape)
                .background(containerColor),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = iconColor,
                modifier = Modifier.size(24.dp)
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
fun BillItemRow(
    bill: Bill,
    onTogglePaid: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                IconButton(onClick = onTogglePaid) {
                    Icon(
                        imageVector = if (bill.isPaid) Icons.Default.CheckCircle else Icons.Default.CheckCircleOutline,
                        contentDescription = "Toggle Paid",
                        tint = if (bill.isPaid) IncomeGreen else Color.Gray
                    )
                }
                Spacer(modifier = Modifier.width(4.dp))
                Column {
                    Text(
                        text = bill.title,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = "Due: ${Formatters.formatDate(bill.dueDate)} • ${bill.category}",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )
                }
            }

            Text(
                text = Formatters.formatCurrency(bill.amount),
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = if (bill.isPaid) IncomeGreen else ExpenseRed
            )
        }
    }
}

@Composable
fun TaskItemRow(
    task: TaskItem,
    onToggleCompleted: () -> Unit
) {
    val priorityColor = when (task.priority.uppercase()) {
        "HIGH" -> ExpenseRed
        "LOW" -> IncomeGreen
        else -> Color(0xFFF59E0B)
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                IconButton(onClick = onToggleCompleted) {
                    Icon(
                        imageVector = if (task.isCompleted) Icons.Default.CheckCircle else Icons.Default.CheckCircleOutline,
                        contentDescription = "Complete Task",
                        tint = if (task.isCompleted) IncomeGreen else Color.Gray
                    )
                }
                Spacer(modifier = Modifier.width(4.dp))
                Column {
                    Text(
                        text = task.title,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = "Due: ${Formatters.formatDate(task.dueDate)} at ${task.dueTime}",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )
                }
            }

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(priorityColor.copy(alpha = 0.15f))
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text(
                    text = task.priority,
                    style = MaterialTheme.typography.labelSmall,
                    color = priorityColor,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

data class TransactionDisplay(
    val id: Long,
    val timestamp: Long,
    val category: String,
    val description: String,
    val amount: Double,
    val isIncome: Boolean,
    val method: String
)

@Composable
fun TransactionRowItem(
    item: TransactionDisplay,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(if (item.isIncome) IncomeGreenLight else ExpenseRedLight),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = getCategoryIcon(item.category),
                        contentDescription = null,
                        tint = if (item.isIncome) IncomeGreen else ExpenseRed,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = if (item.description.isNotBlank()) item.description else item.category,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = "${Formatters.formatFriendlyDate(item.timestamp)} • ${item.method}",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )
                }
            }

            Text(
                text = "${if (item.isIncome) "+" else "-"}${Formatters.formatCurrency(item.amount)}",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = if (item.isIncome) IncomeGreen else ExpenseRed
            )
        }
    }
}
