package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.Bill
import com.example.data.model.Expense
import com.example.data.model.TaskItem
import com.example.ui.theme.ExpenseRed
import com.example.ui.theme.IncomeGreen
import com.example.ui.theme.TealPrimary
import com.example.util.Formatters
import com.example.viewmodel.DailyLifeViewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@Composable
fun CalendarScreen(
    viewModel: DailyLifeViewModel,
    onOpenQuickAdd: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val expenses by viewModel.expenses.collectAsStateWithLifecycle()
    val bills by viewModel.bills.collectAsStateWithLifecycle()
    val tasks by viewModel.tasks.collectAsStateWithLifecycle()

    var displayedYear by remember { mutableIntStateOf(Calendar.getInstance().get(Calendar.YEAR)) }
    var displayedMonth by remember { mutableIntStateOf(Calendar.getInstance().get(Calendar.MONTH)) }
    var selectedDay by remember { mutableIntStateOf(Calendar.getInstance().get(Calendar.DAY_OF_MONTH)) }

    val monthName = remember(displayedYear, displayedMonth) {
        val cal = Calendar.getInstance().apply {
            set(Calendar.YEAR, displayedYear)
            set(Calendar.MONTH, displayedMonth)
        }
        SimpleDateFormat("MMMM yyyy", Locale.ENGLISH).format(cal.time)
    }

    // Days in current displayed month
    val daysInMonth = remember(displayedYear, displayedMonth) {
        val cal = Calendar.getInstance().apply {
            set(Calendar.YEAR, displayedYear)
            set(Calendar.MONTH, displayedMonth)
            set(Calendar.DAY_OF_MONTH, 1)
        }
        cal.getActualMaximum(Calendar.DAY_OF_MONTH)
    }

    // Day of week offset for the 1st day of month (1=Sun, 2=Mon...)
    val firstDayOffset = remember(displayedYear, displayedMonth) {
        val cal = Calendar.getInstance().apply {
            set(Calendar.YEAR, displayedYear)
            set(Calendar.MONTH, displayedMonth)
            set(Calendar.DAY_OF_MONTH, 1)
        }
        cal.get(Calendar.DAY_OF_WEEK) - 1 // 0-indexed for Sunday
    }

    // Aggregate items for selected date
    val selectedDateCal = remember(displayedYear, displayedMonth, selectedDay) {
        Calendar.getInstance().apply {
            set(Calendar.YEAR, displayedYear)
            set(Calendar.MONTH, displayedMonth)
            set(Calendar.DAY_OF_MONTH, selectedDay)
        }
    }

    val selectedDateExpenses = remember(expenses, displayedYear, displayedMonth, selectedDay) {
        expenses.filter {
            val c = Calendar.getInstance().apply { timeInMillis = it.timestamp }
            c.get(Calendar.YEAR) == displayedYear &&
            c.get(Calendar.MONTH) == displayedMonth &&
            c.get(Calendar.DAY_OF_MONTH) == selectedDay
        }
    }

    val selectedDateBills = remember(bills, displayedYear, displayedMonth, selectedDay) {
        bills.filter {
            val c = Calendar.getInstance().apply { timeInMillis = it.dueDate }
            c.get(Calendar.YEAR) == displayedYear &&
            c.get(Calendar.MONTH) == displayedMonth &&
            c.get(Calendar.DAY_OF_MONTH) == selectedDay
        }
    }

    val selectedDateTasks = remember(tasks, displayedYear, displayedMonth, selectedDay) {
        tasks.filter {
            val c = Calendar.getInstance().apply { timeInMillis = it.dueDate }
            c.get(Calendar.YEAR) == displayedYear &&
            c.get(Calendar.MONTH) == displayedMonth &&
            c.get(Calendar.DAY_OF_MONTH) == selectedDay
        }
    }

    val totalSpentOnDay = selectedDateExpenses.sumOf { it.amount }

    Scaffold(
        modifier = modifier.fillMaxSize().testTag("calendar_screen"),
        floatingActionButton = {
            FloatingActionButton(
                onClick = { onOpenQuickAdd(2) }, // Default to Add Task or Bill
                containerColor = TealPrimary,
                contentColor = Color.White,
                modifier = Modifier.testTag("calendar_fab")
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Item")
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Month navigation header
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = {
                            if (displayedMonth == 0) {
                                displayedMonth = 11
                                displayedYear -= 1
                            } else {
                                displayedMonth -= 1
                            }
                            selectedDay = 1
                        }
                    ) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Previous Month")
                    }

                    Text(
                        text = monthName,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )

                    IconButton(
                        onClick = {
                            if (displayedMonth == 11) {
                                displayedMonth = 0
                                displayedYear += 1
                            } else {
                                displayedMonth += 1
                            }
                            selectedDay = 1
                        }
                    ) {
                        Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = "Next Month")
                    }
                }
            }

            // Calendar Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        // Day of week labels
                        val weekdays = listOf("Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat")
                        Row(modifier = Modifier.fillMaxWidth()) {
                            weekdays.forEach { dayLabel ->
                                Text(
                                    text = dayLabel,
                                    modifier = Modifier.weight(1f),
                                    textAlign = TextAlign.Center,
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.Gray
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Grid cells
                        val totalCells = ((firstDayOffset + daysInMonth + 6) / 7) * 7
                        for (week in 0 until (totalCells / 7)) {
                            Row(modifier = Modifier.fillMaxWidth()) {
                                for (dayOfWeek in 0 until 7) {
                                    val cellIndex = week * 7 + dayOfWeek
                                    val dayNum = cellIndex - firstDayOffset + 1
                                    if (dayNum in 1..daysInMonth) {
                                        val isSelected = (dayNum == selectedDay)

                                        // Check if this day has expenses, bills, or tasks
                                        val hasExpense = expenses.any {
                                            val c = Calendar.getInstance().apply { timeInMillis = it.timestamp }
                                            c.get(Calendar.YEAR) == displayedYear && c.get(Calendar.MONTH) == displayedMonth && c.get(Calendar.DAY_OF_MONTH) == dayNum
                                        }
                                        val hasBill = bills.any {
                                            val c = Calendar.getInstance().apply { timeInMillis = it.dueDate }
                                            c.get(Calendar.YEAR) == displayedYear && c.get(Calendar.MONTH) == displayedMonth && c.get(Calendar.DAY_OF_MONTH) == dayNum
                                        }
                                        val hasTask = tasks.any {
                                            val c = Calendar.getInstance().apply { timeInMillis = it.dueDate }
                                            c.get(Calendar.YEAR) == displayedYear && c.get(Calendar.MONTH) == displayedMonth && c.get(Calendar.DAY_OF_MONTH) == dayNum
                                        }

                                        Box(
                                            modifier = Modifier
                                                .weight(1f)
                                                .aspectRatio(1f)
                                                .padding(2.dp)
                                                .clip(RoundedCornerShape(8.dp))
                                                .background(
                                                    if (isSelected) TealPrimary else Color.Transparent
                                                )
                                                .clickable { selectedDay = dayNum }
                                                .testTag("cal_day_$dayNum"),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Column(
                                                horizontalAlignment = Alignment.CenterHorizontally,
                                                verticalArrangement = Arrangement.Center
                                            ) {
                                                Text(
                                                    text = dayNum.toString(),
                                                    style = MaterialTheme.typography.bodyMedium,
                                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                                    color = if (isSelected) Color.White else Color.Black
                                                )

                                                // Dots row
                                                Row(
                                                    horizontalArrangement = Arrangement.spacedBy(2.dp),
                                                    modifier = Modifier.padding(top = 2.dp)
                                                ) {
                                                    if (hasExpense) {
                                                        Box(modifier = Modifier.size(4.dp).clip(CircleShape).background(if (isSelected) Color.White else ExpenseRed))
                                                    }
                                                    if (hasBill) {
                                                        Box(modifier = Modifier.size(4.dp).clip(CircleShape).background(if (isSelected) Color.Yellow else Color(0xFF0284C7)))
                                                    }
                                                    if (hasTask) {
                                                        Box(modifier = Modifier.size(4.dp).clip(CircleShape).background(if (isSelected) Color.White else Color(0xFF7C3AED)))
                                                    }
                                                }
                                            }
                                        }
                                    } else {
                                        Spacer(modifier = Modifier.weight(1f).aspectRatio(1f))
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Daily Agenda Section
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Daily Agenda: ${Formatters.formatFriendlyDate(selectedDateCal.timeInMillis)}",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        if (totalSpentOnDay > 0) {
                            Text(
                                text = "Spent on this date: ${Formatters.formatCurrency(totalSpentOnDay)}",
                                style = MaterialTheme.typography.bodySmall,
                                color = ExpenseRed,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }

            // Bills Due On Selected Date
            if (selectedDateBills.isNotEmpty()) {
                item {
                    Text(
                        text = "Bills Due (${selectedDateBills.size})",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF0284C7)
                    )
                }
                items(selectedDateBills) { bill ->
                    BillItemRow(bill = bill, onTogglePaid = { viewModel.toggleBillPaid(bill) })
                }
            }

            // Tasks Due On Selected Date
            if (selectedDateTasks.isNotEmpty()) {
                item {
                    Text(
                        text = "Tasks Scheduled (${selectedDateTasks.size})",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF7C3AED)
                    )
                }
                items(selectedDateTasks) { task ->
                    TaskItemRow(task = task, onToggleCompleted = { viewModel.toggleTaskCompleted(task) })
                }
            }

            // Expenses On Selected Date
            if (selectedDateExpenses.isNotEmpty()) {
                item {
                    Text(
                        text = "Expenses Logged (${selectedDateExpenses.size})",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = ExpenseRed
                    )
                }
                items(selectedDateExpenses) { exp ->
                    TransactionRowItem(
                        item = TransactionDisplay(
                            id = exp.id,
                            timestamp = exp.timestamp,
                            category = exp.category,
                            description = exp.description,
                            amount = exp.amount,
                            isIncome = false,
                            method = exp.paymentMethod
                        )
                    )
                }
            }

            if (selectedDateBills.isEmpty() && selectedDateTasks.isEmpty() && selectedDateExpenses.isEmpty()) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Box(modifier = Modifier.padding(24.dp), contentAlignment = Alignment.Center) {
                            Text(
                                text = "No bills, tasks, or expenses logged for this date.",
                                color = Color.Gray,
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }
                }
            }

            item { Spacer(modifier = Modifier.height(72.dp)) }
        }
    }
}
