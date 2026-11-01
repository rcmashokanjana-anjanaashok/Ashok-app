package com.example.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.Budget
import com.example.data.model.Expense
import com.example.data.model.Income
import com.example.ui.components.CategoryDonutChart
import com.example.ui.components.ConfirmDeleteDialog
import com.example.ui.components.getCategoryIcon
import com.example.ui.theme.ExpenseRed
import com.example.ui.theme.ExpenseRedLight
import com.example.ui.theme.IncomeGreen
import com.example.ui.theme.IncomeGreenLight
import com.example.ui.theme.TealPrimary
import com.example.util.Formatters
import com.example.viewmodel.DailyLifeViewModel
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun ExpensesScreen(
    viewModel: DailyLifeViewModel,
    onOpenQuickAdd: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val expenses by viewModel.expenses.collectAsStateWithLifecycle()
    val incomes by viewModel.incomes.collectAsStateWithLifecycle()
    val budgets by viewModel.budgets.collectAsStateWithLifecycle()

    var selectedTab by remember { mutableIntStateOf(0) }
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategoryFilter by remember { mutableStateOf("ALL") }

    var itemToDelete by remember { mutableStateOf<Any?>(null) }
    var itemToEditExpense by remember { mutableStateOf<Expense?>(null) }
    var itemToEditIncome by remember { mutableStateOf<Income?>(null) }
    var showSetBudgetDialog by remember { mutableStateOf(false) }

    val tabs = listOf("All", "Expenses", "Income", "Budgets")

    // Categories list for chips
    val allExpenseCategories = remember(expenses) {
        listOf("ALL") + expenses.map { it.category }.distinct()
    }

    Scaffold(
        modifier = modifier.fillMaxSize().testTag("expenses_screen"),
        floatingActionButton = {
            FloatingActionButton(
                onClick = { onOpenQuickAdd(if (selectedTab == 2) 1 else 0) },
                containerColor = TealPrimary,
                contentColor = Color.White,
                modifier = Modifier.testTag("add_transaction_fab")
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Transaction")
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Transactions & Budgets",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
            }

            // Tab Row
            PrimaryTabRow(
                selectedTabIndex = selectedTab,
                containerColor = MaterialTheme.colorScheme.surface
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = {
                            Text(
                                text = title,
                                fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    )
                }
            }

            // Filter & Search bar (for tab 0, 1, 2)
            if (selectedTab != 3) {
                Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        placeholder = { Text("Search description, category, payment method...") },
                        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                        trailingIcon = {
                            if (searchQuery.isNotEmpty()) {
                                IconButton(onClick = { searchQuery = "" }) {
                                    Icon(Icons.Default.Clear, contentDescription = "Clear")
                                }
                            }
                        },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth().testTag("transaction_search_input")
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        allExpenseCategories.take(6).forEach { cat ->
                            FilterChip(
                                selected = selectedCategoryFilter == cat,
                                onClick = { selectedCategoryFilter = cat },
                                label = { Text(cat, fontSize = 12.sp) }
                            )
                        }
                    }
                }
            }

            // Content List
            when (selectedTab) {
                0 -> {
                    // All Transactions
                    val filteredExpenses = expenses.filter {
                        (selectedCategoryFilter == "ALL" || it.category == selectedCategoryFilter) &&
                        (searchQuery.isBlank() || it.description.contains(searchQuery, true) || it.category.contains(searchQuery, true))
                    }.map { TransactionDisplay(it.id, it.timestamp, it.category, it.description, it.amount, false, it.paymentMethod) }

                    val filteredIncomes = incomes.filter {
                        (selectedCategoryFilter == "ALL" || it.category == selectedCategoryFilter) &&
                        (searchQuery.isBlank() || it.description.contains(searchQuery, true) || it.category.contains(searchQuery, true))
                    }.map { TransactionDisplay(it.id, it.timestamp, it.category, it.description, it.amount, true, it.paymentMethod) }

                    val combined = (filteredExpenses + filteredIncomes).sortedByDescending { it.timestamp }

                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        if (combined.isEmpty()) {
                            item {
                                Box(modifier = Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                                    Text("No matching transactions found.", color = Color.Gray)
                                }
                            }
                        } else {
                            items(combined) { item ->
                                TransactionRowItem(item = item)
                            }
                        }
                        item { Spacer(modifier = Modifier.height(72.dp)) }
                    }
                }

                1 -> {
                    // Expenses Only
                    val filtered = expenses.filter {
                        (selectedCategoryFilter == "ALL" || it.category == selectedCategoryFilter) &&
                        (searchQuery.isBlank() || it.description.contains(searchQuery, true) || it.category.contains(searchQuery, true))
                    }

                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Category donut breakdown
                        item {
                            val catGroups = expenses.groupBy { it.category }
                                .mapValues { entry -> entry.value.sumOf { it.amount } }
                                .toList()
                                .sortedByDescending { it.second }

                            if (catGroups.isNotEmpty()) {
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(16.dp),
                                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                                ) {
                                    Column(modifier = Modifier.padding(14.dp)) {
                                        Text("Category Breakdown", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                                        Spacer(modifier = Modifier.height(8.dp))
                                        CategoryDonutChart(categories = catGroups)
                                    }
                                }
                            }
                        }

                        if (filtered.isEmpty()) {
                            item {
                                Box(modifier = Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                                    Text("No expenses logged.", color = Color.Gray)
                                }
                            }
                        } else {
                            items(filtered) { expense ->
                                EditableExpenseCard(
                                    expense = expense,
                                    onEdit = { itemToEditExpense = expense },
                                    onDelete = { itemToDelete = expense }
                                )
                            }
                        }
                        item { Spacer(modifier = Modifier.height(72.dp)) }
                    }
                }

                2 -> {
                    // Income Only
                    val filtered = incomes.filter {
                        (searchQuery.isBlank() || it.description.contains(searchQuery, true) || it.category.contains(searchQuery, true))
                    }

                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        if (filtered.isEmpty()) {
                            item {
                                Box(modifier = Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                                    Text("No income records found.", color = Color.Gray)
                                }
                            }
                        } else {
                            items(filtered) { inc ->
                                EditableIncomeCard(
                                    income = inc,
                                    onEdit = { itemToEditIncome = inc },
                                    onDelete = { itemToDelete = inc }
                                )
                            }
                        }
                        item { Spacer(modifier = Modifier.height(72.dp)) }
                    }
                }

                3 -> {
                    // Budgets Tab
                    val cal = Calendar.getInstance()
                    val monthYearKey = String.format("%02d-%04d", cal.get(Calendar.MONTH) + 1, cal.get(Calendar.YEAR))
                    val monthBudgets = budgets.filter { it.monthYear == monthYearKey }
                    val totalMonthlyExpenses = expenses.filter {
                        val c = Calendar.getInstance().apply { timeInMillis = it.timestamp }
                        c.get(Calendar.MONTH) == cal.get(Calendar.MONTH) && c.get(Calendar.YEAR) == cal.get(Calendar.YEAR)
                    }

                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        item {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Monthly Budgets (${Formatters.formatMonthYear(System.currentTimeMillis())})",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                Button(
                                    onClick = { showSetBudgetDialog = true },
                                    colors = ButtonDefaults.buttonColors(containerColor = TealPrimary),
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.testTag("create_budget_button")
                                ) {
                                    Text("+ Set Budget")
                                }
                            }
                        }

                        if (monthBudgets.isEmpty()) {
                            item {
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(12.dp),
                                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                                ) {
                                    Column(
                                        modifier = Modifier.padding(24.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Text("No budget set for this month yet.", fontWeight = FontWeight.SemiBold)
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text("Set overall or category budgets to keep your expenses under control.", color = Color.Gray, fontSize = 12.sp)
                                    }
                                }
                            }
                        } else {
                            items(monthBudgets) { budget ->
                                val spent = if (budget.category == "ALL") {
                                    totalMonthlyExpenses.sumOf { it.amount }
                                } else {
                                    totalMonthlyExpenses.filter { it.category == budget.category }.sumOf { it.amount }
                                }
                                val pct = if (budget.amount > 0) ((spent / budget.amount) * 100).toInt() else 0
                                val progress = if (budget.amount > 0) (spent / budget.amount).toFloat().coerceIn(0f, 1f) else 0f
                                val left = (budget.amount - spent).coerceAtLeast(0.0)

                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(12.dp),
                                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
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
                                                Icon(
                                                    imageVector = if (budget.category == "ALL") Icons.Default.Tune else getCategoryIcon(budget.category),
                                                    contentDescription = null,
                                                    tint = TealPrimary,
                                                    modifier = Modifier.size(20.dp)
                                                )
                                                Spacer(modifier = Modifier.width(8.dp))
                                                Text(
                                                    text = if (budget.category == "ALL") "Overall Monthly Budget" else "${budget.category} Budget",
                                                    fontWeight = FontWeight.Bold,
                                                    style = MaterialTheme.typography.titleSmall
                                                )
                                            }
                                            IconButton(onClick = { viewModel.deleteBudget(budget) }) {
                                                Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color.Gray)
                                            }
                                        }

                                        LinearProgressIndicator(
                                            progress = { progress },
                                            modifier = Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(4.dp)),
                                            color = if (pct >= 90) ExpenseRed else if (pct >= 75) Color(0xFFF59E0B) else TealPrimary,
                                            trackColor = Color(0xFFE2E8F0)
                                        )

                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Text("Spent: ${Formatters.formatCurrency(spent)} ($pct%)", fontSize = 12.sp, color = Color.DarkGray)
                                            Text("Limit: ${Formatters.formatCurrency(budget.amount)}", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }
                            }
                        }

                        item { Spacer(modifier = Modifier.height(72.dp)) }
                    }
                }
            }
        }
    }

    // Delete Confirmation
    if (itemToDelete != null) {
        ConfirmDeleteDialog(
            title = "Delete Record",
            message = "Are you sure you want to delete this transaction permanently?",
            onConfirm = {
                val item = itemToDelete
                if (item is Expense) viewModel.deleteExpense(item)
                if (item is Income) viewModel.deleteIncome(item)
                itemToDelete = null
            },
            onDismiss = { itemToDelete = null }
        )
    }

    // Set Budget Dialog
    if (showSetBudgetDialog) {
        SetBudgetDialog(
            viewModel = viewModel,
            onDismiss = { showSetBudgetDialog = false }
        )
    }

    // Edit Expense Dialog
    if (itemToEditExpense != null) {
        EditExpenseDialog(
            expense = itemToEditExpense!!,
            onSave = { updated ->
                viewModel.updateExpense(updated)
                itemToEditExpense = null
            },
            onDismiss = { itemToEditExpense = null }
        )
    }

    // Edit Income Dialog
    if (itemToEditIncome != null) {
        EditIncomeDialog(
            income = itemToEditIncome!!,
            onSave = { updated ->
                viewModel.updateIncome(updated)
                itemToEditIncome = null
            },
            onDismiss = { itemToEditIncome = null }
        )
    }
}

@Composable
fun EditableExpenseCard(
    expense: Expense,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    modifier = Modifier.size(40.dp).clip(CircleShape).background(ExpenseRedLight),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(getCategoryIcon(expense.category), contentDescription = null, tint = ExpenseRed, modifier = Modifier.size(20.dp))
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = if (expense.description.isNotBlank()) expense.description else expense.category,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = "${Formatters.formatFriendlyDate(expense.timestamp)} • ${expense.category} • ${expense.paymentMethod}",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "-${Formatters.formatCurrency(expense.amount)}",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = ExpenseRed
                )
                IconButton(onClick = onEdit) {
                    Icon(Icons.Default.Edit, contentDescription = "Edit", tint = Color.Gray, modifier = Modifier.size(18.dp))
                }
                IconButton(onClick = onDelete) {
                    Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color.Gray, modifier = Modifier.size(18.dp))
                }
            }
        }
    }
}

@Composable
fun EditableIncomeCard(
    income: Income,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    modifier = Modifier.size(40.dp).clip(CircleShape).background(IncomeGreenLight),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(getCategoryIcon(income.category), contentDescription = null, tint = IncomeGreen, modifier = Modifier.size(20.dp))
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = if (income.description.isNotBlank()) income.description else income.category,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = "${Formatters.formatFriendlyDate(income.timestamp)} • ${income.category} • ${income.paymentMethod}",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "+${Formatters.formatCurrency(income.amount)}",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = IncomeGreen
                )
                IconButton(onClick = onEdit) {
                    Icon(Icons.Default.Edit, contentDescription = "Edit", tint = Color.Gray, modifier = Modifier.size(18.dp))
                }
                IconButton(onClick = onDelete) {
                    Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color.Gray, modifier = Modifier.size(18.dp))
                }
            }
        }
    }
}

@Composable
private fun SetBudgetDialog(
    viewModel: DailyLifeViewModel,
    onDismiss: () -> Unit
) {
    var amountText by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("ALL") }

    val categories = listOf("ALL", "Grocery", "Food", "Fuel", "Shopping", "Entertainment", "Medical", "Travel")

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Set Monthly Budget", fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = amountText,
                    onValueChange = { amountText = it },
                    label = { Text("Budget Limit (₹ INR)") },
                    leadingIcon = { Text("₹", fontWeight = FontWeight.Bold, color = TealPrimary) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("budget_amount_input")
                )

                Text("Budget Scope", style = MaterialTheme.typography.labelMedium)
                Row(modifier = Modifier.fillMaxWidth()) {
                    categories.take(4).forEach { cat ->
                        FilterChip(
                            selected = selectedCategory == cat,
                            onClick = { selectedCategory = cat },
                            label = { Text(cat, fontSize = 11.sp) },
                            modifier = Modifier.padding(end = 4.dp)
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val amt = amountText.toDoubleOrNull()
                    if (amt != null && amt > 0) {
                        val cal = Calendar.getInstance()
                        val monthYearKey = String.format("%02d-%04d", cal.get(Calendar.MONTH) + 1, cal.get(Calendar.YEAR))
                        viewModel.setBudget(monthYearKey, selectedCategory, amt)
                        onDismiss()
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = TealPrimary),
                modifier = Modifier.testTag("save_budget_button")
            ) {
                Text("Save Budget")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

@Composable
private fun EditExpenseDialog(
    expense: Expense,
    onSave: (Expense) -> Unit,
    onDismiss: () -> Unit
) {
    var amountText by remember { mutableStateOf(expense.amount.toString()) }
    var description by remember { mutableStateOf(expense.description) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Edit Expense", fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = amountText,
                    onValueChange = { amountText = it },
                    label = { Text("Amount (₹ INR)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val amt = amountText.toDoubleOrNull() ?: expense.amount
                    onSave(expense.copy(amount = amt, description = description))
                },
                colors = ButtonDefaults.buttonColors(containerColor = TealPrimary)
            ) {
                Text("Save Changes")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

@Composable
private fun EditIncomeDialog(
    income: Income,
    onSave: (Income) -> Unit,
    onDismiss: () -> Unit
) {
    var amountText by remember { mutableStateOf(income.amount.toString()) }
    var description by remember { mutableStateOf(income.description) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Edit Income", fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = amountText,
                    onValueChange = { amountText = it },
                    label = { Text("Amount (₹ INR)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description / Source") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val amt = amountText.toDoubleOrNull() ?: income.amount
                    onSave(income.copy(amount = amt, description = description))
                },
                colors = ButtonDefaults.buttonColors(containerColor = TealPrimary)
            ) {
                Text("Save Changes")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}
