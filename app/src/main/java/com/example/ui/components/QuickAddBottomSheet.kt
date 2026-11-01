package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Switch
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.ExpenseRed
import com.example.ui.theme.IncomeGreen
import com.example.ui.theme.TealPrimary
import com.example.viewmodel.DailyLifeViewModel

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun QuickAddBottomSheet(
    viewModel: DailyLifeViewModel,
    initialTab: Int = 0,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var selectedTab by remember { mutableIntStateOf(initialTab) }

    val tabs = listOf("Expense", "Income", "Task", "Bill", "Note")

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surface,
        modifier = Modifier.testTag("quick_add_bottom_sheet")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 8.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Quick Add Entry",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                IconButton(onClick = onDismiss) {
                    Icon(Icons.Default.Close, contentDescription = "Close")
                }
            }

            ScrollableTabRow(
                selectedTabIndex = selectedTab,
                edgePadding = 0.dp,
                divider = {},
                containerColor = Color.Transparent,
                modifier = Modifier.padding(bottom = 16.dp)
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = {
                            Text(
                                text = title,
                                fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal,
                                color = if (selectedTab == index) TealPrimary else Color.Gray
                            )
                        }
                    )
                }
            }

            when (selectedTab) {
                0 -> AddExpenseForm(viewModel, onDismiss)
                1 -> AddIncomeForm(viewModel, onDismiss)
                2 -> AddTaskForm(viewModel, onDismiss)
                3 -> AddBillForm(viewModel, onDismiss)
                4 -> AddNoteForm(viewModel, onDismiss)
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun AddExpenseForm(
    viewModel: DailyLifeViewModel,
    onSuccess: () -> Unit
) {
    var amountText by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("Food") }
    var selectedMethod by remember { mutableStateOf("UPI") }

    val categories = listOf(
        "Food", "Grocery", "Shopping", "Travel", "Fuel",
        "Medical", "Education", "Electricity", "Mobile Recharge",
        "Internet", "Rent", "Entertainment", "Household", "Personal", "Other"
    )
    val methods = listOf("UPI", "Cash", "Card", "Bank Transfer")

    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
        OutlinedTextField(
            value = amountText,
            onValueChange = { amountText = it },
            label = { Text("Amount (₹ INR)") },
            leadingIcon = { Text("₹", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = TealPrimary) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .testTag("expense_amount_input")
        )

        OutlinedTextField(
            value = description,
            onValueChange = { description = it },
            label = { Text("Description / Notes") },
            placeholder = { Text("e.g. Dinner, supermarket groceries...") },
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .testTag("expense_description_input")
        )

        Text("Category", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.SemiBold)
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            categories.forEach { cat ->
                FilterChip(
                    selected = selectedCategory == cat,
                    onClick = { selectedCategory = cat },
                    label = { Text(cat, fontSize = 12.sp) },
                    leadingIcon = {
                        Icon(getCategoryIcon(cat), contentDescription = null, modifier = Modifier.size(16.dp))
                    },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = TealPrimary.copy(alpha = 0.15f),
                        selectedLabelColor = TealPrimary
                    )
                )
            }
        }

        Text("Payment Method", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.SemiBold)
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            methods.forEach { method ->
                FilterChip(
                    selected = selectedMethod == method,
                    onClick = { selectedMethod = method },
                    label = { Text(method) }
                )
            }
        }

        Button(
            onClick = {
                val amt = amountText.toDoubleOrNull()
                if (amt != null && amt > 0) {
                    viewModel.addExpense(
                        amount = amt,
                        category = selectedCategory,
                        paymentMethod = selectedMethod,
                        description = description
                    )
                    onSuccess()
                }
            },
            enabled = (amountText.toDoubleOrNull() ?: 0.0) > 0,
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .testTag("save_expense_button"),
            colors = ButtonDefaults.buttonColors(containerColor = ExpenseRed),
            shape = RoundedCornerShape(12.dp)
        ) {
            Icon(Icons.Default.Add, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Record Expense", fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun AddIncomeForm(
    viewModel: DailyLifeViewModel,
    onSuccess: () -> Unit
) {
    var amountText by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("Salary") }
    var selectedMethod by remember { mutableStateOf("Bank") }

    val categories = listOf("Salary", "Business", "Freelance", "Investment", "Cash Received", "Other")
    val methods = listOf("Bank", "UPI", "Cash", "Cheque")

    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
        OutlinedTextField(
            value = amountText,
            onValueChange = { amountText = it },
            label = { Text("Income Amount (₹ INR)") },
            leadingIcon = { Text("₹", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = IncomeGreen) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .testTag("income_amount_input")
        )

        OutlinedTextField(
            value = description,
            onValueChange = { description = it },
            label = { Text("Source / Note") },
            placeholder = { Text("e.g. October monthly salary, client payout...") },
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .testTag("income_description_input")
        )

        Text("Income Category", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.SemiBold)
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            categories.take(3).forEach { cat ->
                FilterChip(
                    selected = selectedCategory == cat,
                    onClick = { selectedCategory = cat },
                    label = { Text(cat) }
                )
            }
        }
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            categories.drop(3).forEach { cat ->
                FilterChip(
                    selected = selectedCategory == cat,
                    onClick = { selectedCategory = cat },
                    label = { Text(cat) }
                )
            }
        }

        Text("Received Via", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.SemiBold)
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            methods.forEach { method ->
                FilterChip(
                    selected = selectedMethod == method,
                    onClick = { selectedMethod = method },
                    label = { Text(method) }
                )
            }
        }

        Button(
            onClick = {
                val amt = amountText.toDoubleOrNull()
                if (amt != null && amt > 0) {
                    viewModel.addIncome(
                        amount = amt,
                        category = selectedCategory,
                        paymentMethod = selectedMethod,
                        description = description
                    )
                    onSuccess()
                }
            },
            enabled = (amountText.toDoubleOrNull() ?: 0.0) > 0,
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .testTag("save_income_button"),
            colors = ButtonDefaults.buttonColors(containerColor = IncomeGreen),
            shape = RoundedCornerShape(12.dp)
        ) {
            Icon(Icons.Default.Add, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Record Income", fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun AddTaskForm(
    viewModel: DailyLifeViewModel,
    onSuccess: () -> Unit
) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var priority by remember { mutableStateOf("MEDIUM") }
    var dueTime by remember { mutableStateOf("10:00 AM") }
    var reminder by remember { mutableStateOf(true) }

    val priorities = listOf("HIGH", "MEDIUM", "LOW")

    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
        OutlinedTextField(
            value = title,
            onValueChange = { title = it },
            label = { Text("Task Title *") },
            placeholder = { Text("e.g. Call accountant, Pay gym fee...") },
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .testTag("task_title_input")
        )

        OutlinedTextField(
            value = description,
            onValueChange = { description = it },
            label = { Text("Details / Checklist note") },
            maxLines = 3,
            modifier = Modifier.fillMaxWidth()
        )

        Text("Priority Level", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.SemiBold)
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            priorities.forEach { p ->
                FilterChip(
                    selected = priority == p,
                    onClick = { priority = p },
                    label = { Text(p) }
                )
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Enable Local Reminder Notification", style = MaterialTheme.typography.bodyMedium)
            Switch(checked = reminder, onCheckedChange = { reminder = it })
        }

        Button(
            onClick = {
                if (title.isNotBlank()) {
                    viewModel.addTask(
                        title = title.trim(),
                        description = description.trim(),
                        priority = priority,
                        dueTime = dueTime,
                        reminder = reminder
                    )
                    onSuccess()
                }
            },
            enabled = title.isNotBlank(),
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .testTag("save_task_button"),
            colors = ButtonDefaults.buttonColors(containerColor = TealPrimary),
            shape = RoundedCornerShape(12.dp)
        ) {
            Icon(Icons.Default.Add, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Create Task", fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun AddBillForm(
    viewModel: DailyLifeViewModel,
    onSuccess: () -> Unit
) {
    var title by remember { mutableStateOf("") }
    var amountText by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("Electricity") }
    var frequency by remember { mutableStateOf("Monthly") }
    var reminder by remember { mutableStateOf(true) }

    val categories = listOf("Electricity", "Water", "Mobile Recharge", "Internet", "Rent", "EMI", "Insurance", "Fees", "Credit Card")
    val frequencies = listOf("Monthly", "Quarterly", "Yearly", "One-Time")

    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
        OutlinedTextField(
            value = title,
            onValueChange = { title = it },
            label = { Text("Bill / Reminder Title *") },
            placeholder = { Text("e.g. Torrent Power, Jio Fiber, House Rent...") },
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .testTag("bill_title_input")
        )

        OutlinedTextField(
            value = amountText,
            onValueChange = { amountText = it },
            label = { Text("Bill Amount (₹ INR) *") },
            leadingIcon = { Text("₹", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = TealPrimary) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .testTag("bill_amount_input")
        )

        Text("Category", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.SemiBold)
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            categories.take(3).forEach { cat ->
                FilterChip(selected = category == cat, onClick = { category = cat }, label = { Text(cat) })
            }
        }

        Text("Recurring Frequency", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.SemiBold)
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            frequencies.forEach { freq ->
                FilterChip(selected = frequency == freq, onClick = { frequency = freq }, label = { Text(freq) })
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Notification Alert on Due Date", style = MaterialTheme.typography.bodyMedium)
            Switch(checked = reminder, onCheckedChange = { reminder = it })
        }

        Button(
            onClick = {
                val amt = amountText.toDoubleOrNull() ?: 0.0
                if (title.isNotBlank() && amt > 0) {
                    val dueDate = System.currentTimeMillis() + (7L * 86400000L) // Default 7 days from now
                    viewModel.addBill(
                        title = title.trim(),
                        category = category,
                        amount = amt,
                        dueDate = dueDate,
                        frequency = frequency,
                        reminder = reminder
                    )
                    onSuccess()
                }
            },
            enabled = title.isNotBlank() && (amountText.toDoubleOrNull() ?: 0.0) > 0,
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .testTag("save_bill_button"),
            colors = ButtonDefaults.buttonColors(containerColor = TealPrimary),
            shape = RoundedCornerShape(12.dp)
        ) {
            Icon(Icons.Default.Add, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Add Bill Reminder", fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun AddNoteForm(
    viewModel: DailyLifeViewModel,
    onSuccess: () -> Unit
) {
    var title by remember { mutableStateOf("") }
    var content by remember { mutableStateOf("") }
    var isPinned by remember { mutableStateOf(false) }
    var selectedColor by remember { mutableStateOf("#FFFFFF") }

    val colors = listOf("#FFFFFF", "#FEF3C7", "#E0F2FE", "#DCFCE7", "#FCE7F3")

    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
        OutlinedTextField(
            value = title,
            onValueChange = { title = it },
            label = { Text("Note Title *") },
            placeholder = { Text("e.g. Grocery list, Important ideas...") },
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .testTag("note_title_input")
        )

        OutlinedTextField(
            value = content,
            onValueChange = { content = it },
            label = { Text("Content / Notes") },
            placeholder = { Text("Write your thoughts or bullet points here...") },
            minLines = 3,
            maxLines = 6,
            modifier = Modifier
                .fillMaxWidth()
                .testTag("note_content_input")
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Pin to Top", style = MaterialTheme.typography.bodyMedium)
            Switch(checked = isPinned, onCheckedChange = { isPinned = it })
        }

        Text("Card Color", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.SemiBold)
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            colors.forEach { hex ->
                val color = Color(android.graphics.Color.parseColor(hex))
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(color)
                        .clickable { selectedColor = hex },
                    contentAlignment = Alignment.Center
                ) {
                    if (selectedColor == hex) {
                        Icon(Icons.Default.Check, contentDescription = null, tint = Color.DarkGray, modifier = Modifier.size(18.dp))
                    }
                }
            }
        }

        Button(
            onClick = {
                if (title.isNotBlank()) {
                    viewModel.addNote(
                        title = title.trim(),
                        content = content.trim(),
                        isPinned = isPinned,
                        colorHex = selectedColor
                    )
                    onSuccess()
                }
            },
            enabled = title.isNotBlank(),
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .testTag("save_note_button"),
            colors = ButtonDefaults.buttonColors(containerColor = TealPrimary),
            shape = RoundedCornerShape(12.dp)
        ) {
            Icon(Icons.Default.Add, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Save Note", fontWeight = FontWeight.Bold)
        }
    }
}
