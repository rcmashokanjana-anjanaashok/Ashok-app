package com.example.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.Fastfood
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocalGasStation
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.ExpenseRed
import com.example.ui.theme.ExpenseRedLight
import com.example.ui.theme.IncomeGreen
import com.example.ui.theme.IncomeGreenLight
import com.example.ui.theme.TealPrimary
import com.example.util.Formatters

@Composable
fun SummaryKpiCard(
    title: String,
    amount: Double,
    modifier: Modifier = Modifier,
    isIncome: Boolean = false,
    subtitle: String? = null,
    testTag: String = "kpi_card"
) {
    Card(
        modifier = modifier.testTag(testTag),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isIncome) IncomeGreenLight else ExpenseRedLight
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.labelMedium,
                    color = if (isIncome) IncomeGreen else ExpenseRed,
                    fontWeight = FontWeight.SemiBold
                )
                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .clip(CircleShape)
                        .background(if (isIncome) IncomeGreen else ExpenseRed),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (isIncome) Icons.Default.ArrowDownward else Icons.Default.ArrowUpward,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(14.dp)
                    )
                }
            }
            Text(
                text = Formatters.formatCurrency(amount),
                style = MaterialTheme.typography.titleMedium.copy(fontSize = 18.sp),
                fontWeight = FontWeight.Bold,
                color = if (isIncome) Color(0xFF1B5E20) else Color(0xFFB71C1C)
            )
            if (subtitle != null) {
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.DarkGray,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
fun BudgetProgressCard(
    spent: Double,
    budget: Double,
    modifier: Modifier = Modifier,
    onSetBudgetClick: () -> Unit
) {
    val percentage = if (budget > 0) ((spent / budget) * 100).toInt() else 0
    val progress = if (budget > 0) (spent / budget).toFloat().coerceIn(0f, 1f) else 0f
    val remaining = (budget - spent).coerceAtLeast(0.0)

    val progressColor = when {
        percentage >= 90 -> ExpenseRed
        percentage >= 75 -> Color(0xFFF59E0B)
        else -> TealPrimary
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("budget_progress_card"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Monthly Budget Utilization",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = if (budget > 0) "${percentage}% used of ${Formatters.formatCurrency(budget)}" else "No budget limit set for this month",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )
                }
                TextButton(
                    onClick = onSetBudgetClick,
                    modifier = Modifier.testTag("set_budget_button")
                ) {
                    Text("Manage", style = MaterialTheme.typography.labelLarge)
                }
            }

            if (budget > 0) {
                LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp)),
                    color = progressColor,
                    trackColor = Color(0xFFE2E8F0)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Spent: ${Formatters.formatCurrency(spent)}",
                        style = MaterialTheme.typography.bodySmall,
                        color = progressColor,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = "Left: ${Formatters.formatCurrency(remaining)}",
                        style = MaterialTheme.typography.bodySmall,
                        color = if (remaining > 0) IncomeGreen else ExpenseRed,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}

// Custom High-Quality Canvas Bar Chart for Expenses
@Composable
fun MonthlyBarChart(
    data: List<Pair<String, Double>>,
    modifier: Modifier = Modifier,
    barColor: Color = TealPrimary
) {
    if (data.isEmpty()) {
        Box(
            modifier = modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            Text("No recent transaction data", color = Color.Gray, style = MaterialTheme.typography.bodySmall)
        }
        return
    }

    val maxVal = (data.maxOfOrNull { it.second } ?: 1.0).coerceAtLeast(100.0)

    Column(modifier = modifier.fillMaxWidth()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(130.dp)
        ) {
            Canvas(modifier = Modifier.matchParentSize()) {
                val barWidth = (size.width / (data.size * 2f)).coerceIn(12f, 40f)
                val spacing = (size.width - (barWidth * data.size)) / (data.size + 1)

                // Draw background guidelines
                val linePaintColor = Color(0xFFE2E8F0)
                drawLine(
                    color = linePaintColor,
                    start = Offset(0f, size.height),
                    end = Offset(size.width, size.height),
                    strokeWidth = 2f
                )
                drawLine(
                    color = linePaintColor.copy(alpha = 0.5f),
                    start = Offset(0f, size.height / 2f),
                    end = Offset(size.width, size.height / 2f),
                    strokeWidth = 1f
                )

                data.forEachIndexed { index, pair ->
                    val x = spacing + index * (barWidth + spacing)
                    val barHeight = ((pair.second / maxVal) * (size.height - 16f)).toFloat()
                    val y = size.height - barHeight

                    drawRoundRect(
                        color = barColor,
                        topLeft = Offset(x, y),
                        size = Size(barWidth, barHeight),
                        cornerRadius = androidx.compose.ui.geometry.CornerRadius(6f, 6f)
                    )
                }
            }
        }

        // Labels under bars
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 4.dp),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            data.forEach { pair ->
                Text(
                    text = pair.first,
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                    color = Color.Gray,
                    maxLines = 1
                )
            }
        }
    }
}

// Custom Donut Chart for Category Breakdown
@Composable
fun CategoryDonutChart(
    categories: List<Pair<String, Double>>,
    modifier: Modifier = Modifier
) {
    if (categories.isEmpty()) {
        Box(
            modifier = modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            Text("No expense breakdown available", color = Color.Gray, style = MaterialTheme.typography.bodySmall)
        }
        return
    }

    val total = categories.sumOf { it.second }.coerceAtLeast(1.0)
    val colors = listOf(
        Color(0xFF00695C), Color(0xFFE53935), Color(0xFF1E3A8A),
        Color(0xFFF59E0B), Color(0xFF7C3AED), Color(0xFF0284C7),
        Color(0xFF10B981), Color(0xFFEC4899), Color(0xFF64748B)
    )

    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier.size(130.dp),
            contentAlignment = Alignment.Center
        ) {
            Canvas(modifier = Modifier.size(110.dp)) {
                var startAngle = -90f
                val strokeWidth = 24f

                categories.forEachIndexed { index, pair ->
                    val sweep = ((pair.second / total) * 360f).toFloat()
                    val color = colors[index % colors.size]
                    drawArc(
                        color = color,
                        startAngle = startAngle,
                        sweepAngle = sweep - 2f,
                        useCenter = false,
                        style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                    )
                    startAngle += sweep
                }
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "Total",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.Gray
                )
                Text(
                    text = Formatters.formatCurrency(total, includeDecimals = false),
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1
                )
            }
        }

        Spacer(modifier = Modifier.width(16.dp))

        // Legend
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            categories.take(5).forEachIndexed { index, pair ->
                val pct = ((pair.second / total) * 100).toInt()
                val color = colors[index % colors.size]
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(color)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = pair.first,
                            style = MaterialTheme.typography.bodySmall,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                    Text(
                        text = "$pct%",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.DarkGray
                    )
                }
            }
        }
    }
}

// Confirmation Dialog helper
@Composable
fun ConfirmDeleteDialog(
    title: String,
    message: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title, fontWeight = FontWeight.Bold) },
        text = { Text(message) },
        confirmButton = {
            TextButton(
                onClick = onConfirm,
                modifier = Modifier.testTag("confirm_delete_button")
            ) {
                Text("Delete", color = ExpenseRed, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                modifier = Modifier.testTag("cancel_delete_button")
            ) {
                Text("Cancel")
            }
        }
    )
}

// Helper for category icon lookup
fun getCategoryIcon(category: String): ImageVector {
    return when (category.lowercase()) {
        "food" -> Icons.Default.Fastfood
        "grocery" -> Icons.Default.ShoppingCart
        "shopping" -> Icons.Default.ShoppingBag
        "travel" -> Icons.Default.DirectionsCar
        "fuel" -> Icons.Default.LocalGasStation
        "medical" -> Icons.Default.LocalHospital
        "education" -> Icons.Default.School
        "electricity" -> Icons.Default.ElectricBolt
        "mobile recharge" -> Icons.Default.PhoneAndroid
        "internet" -> Icons.Default.Wifi
        "rent" -> Icons.Default.Home
        "salary", "business", "bank" -> Icons.Default.AccountBalance
        else -> Icons.Default.MoreHoriz
    }
}
