package com.example.ui.screens

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.TableChart
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.components.CategoryDonutChart
import com.example.ui.components.MonthlyBarChart
import com.example.ui.components.SummaryKpiCard
import com.example.ui.theme.ExpenseRed
import com.example.ui.theme.IncomeGreen
import com.example.ui.theme.TealPrimary
import com.example.util.ExportHelper
import com.example.util.Formatters
import com.example.viewmodel.DailyLifeViewModel
import com.example.viewmodel.DateRangeFilter
import java.io.File
import java.io.FileOutputStream
import java.util.Calendar

@Composable
fun ReportsScreen(
    viewModel: DailyLifeViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val expenses by viewModel.expenses.collectAsStateWithLifecycle()
    val incomes by viewModel.incomes.collectAsStateWithLifecycle()
    val bills by viewModel.bills.collectAsStateWithLifecycle()
    val savingsGoals by viewModel.savingsGoals.collectAsStateWithLifecycle()
    val loans by viewModel.loans.collectAsStateWithLifecycle()

    var selectedPeriod by remember { mutableStateOf(DateRangeFilter.THIS_MONTH) }

    // Filter calculations
    val (periodExpenses, periodIncomes, periodLabel) = remember(expenses, incomes, selectedPeriod) {
        val now = System.currentTimeMillis()
        val cal = Calendar.getInstance()

        when (selectedPeriod) {
            DateRangeFilter.TODAY -> {
                val dayOfYear = cal.get(Calendar.DAY_OF_YEAR)
                val year = cal.get(Calendar.YEAR)
                val e = expenses.filter {
                    val c = Calendar.getInstance().apply { timeInMillis = it.timestamp }
                    c.get(Calendar.YEAR) == year && c.get(Calendar.DAY_OF_YEAR) == dayOfYear
                }
                val i = incomes.filter {
                    val c = Calendar.getInstance().apply { timeInMillis = it.timestamp }
                    c.get(Calendar.YEAR) == year && c.get(Calendar.DAY_OF_YEAR) == dayOfYear
                }
                Triple(e, i, "Today")
            }
            DateRangeFilter.THIS_WEEK -> {
                val week = cal.get(Calendar.WEEK_OF_YEAR)
                val year = cal.get(Calendar.YEAR)
                val e = expenses.filter {
                    val c = Calendar.getInstance().apply { timeInMillis = it.timestamp }
                    c.get(Calendar.YEAR) == year && c.get(Calendar.WEEK_OF_YEAR) == week
                }
                val i = incomes.filter {
                    val c = Calendar.getInstance().apply { timeInMillis = it.timestamp }
                    c.get(Calendar.YEAR) == year && c.get(Calendar.WEEK_OF_YEAR) == week
                }
                Triple(e, i, "This Week")
            }
            DateRangeFilter.THIS_MONTH -> {
                val month = cal.get(Calendar.MONTH)
                val year = cal.get(Calendar.YEAR)
                val e = expenses.filter {
                    val c = Calendar.getInstance().apply { timeInMillis = it.timestamp }
                    c.get(Calendar.YEAR) == year && c.get(Calendar.MONTH) == month
                }
                val i = incomes.filter {
                    val c = Calendar.getInstance().apply { timeInMillis = it.timestamp }
                    c.get(Calendar.YEAR) == year && c.get(Calendar.MONTH) == month
                }
                Triple(e, i, Formatters.formatMonthYear(now))
            }
            DateRangeFilter.THIS_YEAR -> {
                val year = cal.get(Calendar.YEAR)
                val e = expenses.filter {
                    val c = Calendar.getInstance().apply { timeInMillis = it.timestamp }
                    c.get(Calendar.YEAR) == year
                }
                val i = incomes.filter {
                    val c = Calendar.getInstance().apply { timeInMillis = it.timestamp }
                    c.get(Calendar.YEAR) == year
                }
                Triple(e, i, "Year $year")
            }
            else -> {
                Triple(expenses, incomes, "All Time")
            }
        }
    }

    val totalIncome = periodIncomes.sumOf { it.amount }
    val totalExpense = periodExpenses.sumOf { it.amount }
    val netBalance = totalIncome - totalExpense

    // PDF Create Document launcher
    val pdfExportLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("application/pdf")
    ) { uri: Uri? ->
        if (uri != null) {
            try {
                context.contentResolver.openOutputStream(uri)?.use { os ->
                    ExportHelper.generateFinancialPdf(
                        context = context,
                        outputStream = os,
                        periodName = periodLabel,
                        totalIncome = totalIncome,
                        totalExpense = totalExpense,
                        expenses = periodExpenses,
                        incomes = periodIncomes,
                        bills = bills
                    )
                }
                Toast.makeText(context, "PDF Report exported successfully!", Toast.LENGTH_LONG).show()
            } catch (e: Exception) {
                Toast.makeText(context, "Failed to export PDF: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // CSV Create Document launcher
    val csvExportLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("text/csv")
    ) { uri: Uri? ->
        if (uri != null) {
            try {
                context.contentResolver.openOutputStream(uri)?.use { os ->
                    val csvText = ExportHelper.generateFullTransactionsCsv(periodExpenses, periodIncomes)
                    os.write(csvText.toByteArray())
                }
                Toast.makeText(context, "Excel/CSV Report exported successfully!", Toast.LENGTH_LONG).show()
            } catch (e: Exception) {
                Toast.makeText(context, "Failed to export CSV: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("reports_screen"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header
        item {
            Column {
                Text(
                    text = "Reports & Analytics",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Detailed summaries, category charts & official exports",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
            }
        }

        // Period filter chips
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                val periods = listOf(
                    DateRangeFilter.TODAY to "Today",
                    DateRangeFilter.THIS_WEEK to "This Week",
                    DateRangeFilter.THIS_MONTH to "This Month",
                    DateRangeFilter.THIS_YEAR to "Yearly",
                    DateRangeFilter.ALL to "All Time"
                )
                periods.forEach { (filter, label) ->
                    FilterChip(
                        selected = selectedPeriod == filter,
                        onClick = { selectedPeriod = filter },
                        label = { Text(label, fontSize = 11.sp) },
                        modifier = Modifier.testTag("filter_$label")
                    )
                }
            }
        }

        // Export Actions Row
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "Export Statement ($periodLabel)",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = {
                                val fileName = "DailyLife_Report_${periodLabel.replace(" ", "_")}.pdf"
                                pdfExportLauncher.launch(fileName)
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = TealPrimary),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("export_pdf_button"),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Icon(Icons.Default.PictureAsPdf, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Export PDF", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }

                        OutlinedButton(
                            onClick = {
                                val fileName = "DailyLife_Transactions_${periodLabel.replace(" ", "_")}.csv"
                                csvExportLauncher.launch(fileName)
                            },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("export_csv_button"),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Icon(Icons.Default.TableChart, contentDescription = null, modifier = Modifier.size(18.dp), tint = Color(0xFF0284C7))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Excel/CSV", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0284C7))
                        }
                    }
                }
            }
        }

        // Financial Overview KPI Cards
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                SummaryKpiCard(
                    title = "TOTAL INCOME",
                    amount = totalIncome,
                    modifier = Modifier.weight(1f),
                    isIncome = true,
                    subtitle = "${periodIncomes.size} entries"
                )
                SummaryKpiCard(
                    title = "TOTAL EXPENSES",
                    amount = totalExpense,
                    modifier = Modifier.weight(1f),
                    isIncome = false,
                    subtitle = "${periodExpenses.size} entries"
                )
            }
        }

        // Net Savings Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (netBalance >= 0) Color(0xFFE8F5E9) else Color(0xFFFFEBEE)
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = if (netBalance >= 0) "Net Period Savings" else "Net Period Deficit",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = if (netBalance >= 0) IncomeGreen else ExpenseRed
                        )
                        Text(
                            text = if (netBalance >= 0) "Great financial surplus retained" else "Expenses exceeded earnings",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.DarkGray
                        )
                    }
                    Text(
                        text = Formatters.formatCurrency(netBalance),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = if (netBalance >= 0) IncomeGreen else ExpenseRed
                    )
                }
            }
        }

        // Income vs Expense Comparison Bar
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
                    Text(
                        text = "Income vs Expense Breakdown",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )

                    val comparisonData = listOf(
                        Pair("Total Income", totalIncome),
                        Pair("Total Expense", totalExpense)
                    )
                    MonthlyBarChart(data = comparisonData, barColor = TealPrimary)
                }
            }
        }

        // Category-wise Breakdown Donut & Table
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
                    Text(
                        text = "Category-Wise Expense Distribution",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )

                    val catGroups = remember(periodExpenses) {
                        periodExpenses.groupBy { it.category }
                            .mapValues { entry -> entry.value.sumOf { it.amount } }
                            .toList()
                            .sortedByDescending { it.second }
                    }

                    CategoryDonutChart(categories = catGroups)
                }
            }
        }

        // Savings and Loans Summary Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = "Savings & Outstanding Debts",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )

                    val totalSavings = savingsGoals.sumOf { it.currentAmount }
                    val toReceive = loans.filter { it.type == "LENT" && !it.isSettled }
                        .sumOf { (it.originalAmount - it.paidAmount).coerceAtLeast(0.0) }
                    val toPay = loans.filter { it.type == "BORROWED" && !it.isSettled }
                        .sumOf { (it.originalAmount - it.paidAmount).coerceAtLeast(0.0) }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text("Total Accumulated Savings", fontSize = 12.sp, color = Color.Gray)
                            Text(Formatters.formatCurrency(totalSavings), fontWeight = FontWeight.Bold, color = TealPrimary)
                        }
                        Column {
                            Text("To Receive (Lent)", fontSize = 12.sp, color = Color.Gray)
                            Text(Formatters.formatCurrency(toReceive), fontWeight = FontWeight.Bold, color = IncomeGreen)
                        }
                        Column {
                            Text("To Pay (Borrowed)", fontSize = 12.sp, color = Color.Gray)
                            Text(Formatters.formatCurrency(toPay), fontWeight = FontWeight.Bold, color = ExpenseRed)
                        }
                    }
                }
            }
        }

        item { Spacer(modifier = Modifier.height(72.dp)) }
    }
}
