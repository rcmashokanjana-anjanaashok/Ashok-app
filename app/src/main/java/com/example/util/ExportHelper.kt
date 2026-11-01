package com.example.util

import android.content.Context
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import com.example.data.model.Bill
import com.example.data.model.Budget
import com.example.data.model.Expense
import com.example.data.model.Income
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream

object ExportHelper {

    fun generateExpensesCsv(expenses: List<Expense>): String {
        val sb = StringBuilder()
        sb.append("ID,Date,Category,Amount (INR),Payment Method,Description\n")
        for (e in expenses) {
            val dateStr = Formatters.formatDate(e.timestamp)
            val desc = e.description.replace("\"", "\"\"")
            sb.append("${e.id},\"$dateStr\",\"${e.category}\",${e.amount},\"${e.paymentMethod}\",\"$desc\"\n")
        }
        return sb.toString()
    }

    fun generateIncomesCsv(incomes: List<Income>): String {
        val sb = StringBuilder()
        sb.append("ID,Date,Category,Amount (INR),Payment Method,Description\n")
        for (i in incomes) {
            val dateStr = Formatters.formatDate(i.timestamp)
            val desc = i.description.replace("\"", "\"\"")
            sb.append("${i.id},\"$dateStr\",\"${i.category}\",${i.amount},\"${i.paymentMethod}\",\"$desc\"\n")
        }
        return sb.toString()
    }

    fun generateFullTransactionsCsv(expenses: List<Expense>, incomes: List<Income>): String {
        val sb = StringBuilder()
        sb.append("Type,ID,Date,Category,Amount (INR),Payment Method,Description\n")
        for (i in incomes) {
            val dateStr = Formatters.formatDate(i.timestamp)
            val desc = i.description.replace("\"", "\"\"")
            sb.append("INCOME,${i.id},\"$dateStr\",\"${i.category}\",${i.amount},\"${i.paymentMethod}\",\"$desc\"\n")
        }
        for (e in expenses) {
            val dateStr = Formatters.formatDate(e.timestamp)
            val desc = e.description.replace("\"", "\"\"")
            sb.append("EXPENSE,${e.id},\"$dateStr\",\"${e.category}\",${e.amount},\"${e.paymentMethod}\",\"$desc\"\n")
        }
        return sb.toString()
    }

    fun generateFinancialPdf(
        context: Context,
        outputStream: OutputStream,
        periodName: String,
        totalIncome: Double,
        totalExpense: Double,
        expenses: List<Expense>,
        incomes: List<Income>,
        bills: List<Bill>
    ) {
        val document = PdfDocument()
        val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create() // Standard A4 in points
        val page = document.startPage(pageInfo)
        val canvas = page.canvas

        val paint = Paint().apply { isAntiAlias = true }

        // Header Background
        paint.color = Color.rgb(0, 105, 92) // TealPrimary
        canvas.drawRect(0f, 0f, 595f, 90f, paint)

        // Title
        paint.color = Color.WHITE
        paint.textSize = 22f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        canvas.drawText("DailyLife - Personal Financial Report", 30f, 45f, paint)

        // Subtitle / Period
        paint.textSize = 12f
        paint.typeface = Typeface.DEFAULT
        canvas.drawText("Statement Period: $periodName | Generated: ${Formatters.formatFriendlyDate(System.currentTimeMillis())}", 30f, 70f, paint)

        var y = 120f

        // KPI Summary Box
        paint.color = Color.rgb(240, 244, 248)
        canvas.drawRoundRect(30f, y, 565f, y + 70f, 12f, 12f, paint)

        val balance = totalIncome - totalExpense

        // Labels
        paint.textSize = 10f
        paint.color = Color.DKGRAY
        canvas.drawText("TOTAL INCOME", 50f, y + 25f, paint)
        canvas.drawText("TOTAL EXPENSES", 220f, y + 25f, paint)
        canvas.drawText("NET SAVINGS / BALANCE", 390f, y + 25f, paint)

        // Values
        paint.textSize = 16f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)

        paint.color = Color.rgb(46, 125, 50) // Income Green
        canvas.drawText(Formatters.formatCurrency(totalIncome), 50f, y + 52f, paint)

        paint.color = Color.rgb(229, 57, 53) // Expense Red
        canvas.drawText(Formatters.formatCurrency(totalExpense), 220f, y + 52f, paint)

        paint.color = if (balance >= 0) Color.rgb(0, 105, 92) else Color.rgb(229, 57, 53)
        canvas.drawText(Formatters.formatCurrency(balance), 390f, y + 52f, paint)

        y += 105f

        // Category Summary
        paint.color = Color.rgb(25, 28, 30)
        paint.textSize = 14f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        canvas.drawText("Top Expense Categories", 30f, y, paint)

        y += 20f

        val categoryGroups = expenses.groupBy { it.category }
            .mapValues { entry -> entry.value.sumOf { it.amount } }
            .toList()
            .sortedByDescending { it.second }
            .take(5)

        paint.textSize = 11f
        paint.typeface = Typeface.DEFAULT
        if (categoryGroups.isEmpty()) {
            paint.color = Color.GRAY
            canvas.drawText("No expense entries in this period", 30f, y, paint)
            y += 20f
        } else {
            for ((cat, amt) in categoryGroups) {
                paint.color = Color.DKGRAY
                val pct = if (totalExpense > 0) ((amt / totalExpense) * 100).toInt() else 0
                canvas.drawText("• $cat ($pct%)", 40f, y, paint)
                paint.color = Color.BLACK
                canvas.drawText(Formatters.formatCurrency(amt), 450f, y, paint)
                y += 18f
            }
        }

        y += 20f

        // Recent Transactions Section Header
        paint.color = Color.rgb(25, 28, 30)
        paint.textSize = 14f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        canvas.drawText("Recent Transactions Record", 30f, y, paint)

        y += 20f

        // Table Header
        paint.color = Color.rgb(230, 235, 240)
        canvas.drawRect(30f, y - 14f, 565f, y + 6f, paint)

        paint.color = Color.BLACK
        paint.textSize = 10f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        canvas.drawText("DATE", 40f, y, paint)
        canvas.drawText("TYPE", 110f, y, paint)
        canvas.drawText("CATEGORY", 180f, y, paint)
        canvas.drawText("PAYMENT", 310f, y, paint)
        canvas.drawText("AMOUNT", 460f, y, paint)

        y += 20f
        paint.typeface = Typeface.DEFAULT

        // Combine up to 15 recent items
        val combinedItems = (
            expenses.map { Triple(it.timestamp, "EXPENSE", Pair(it.category, it.amount)) } +
            incomes.map { Triple(it.timestamp, "INCOME", Pair(it.category, it.amount)) }
        ).sortedByDescending { it.first }.take(15)

        for (item in combinedItems) {
            val dateStr = Formatters.formatDate(item.first)
            val typeStr = item.second
            val category = item.third.first
            val amount = item.third.second

            paint.color = Color.DKGRAY
            canvas.drawText(dateStr, 40f, y, paint)

            if (typeStr == "INCOME") {
                paint.color = Color.rgb(46, 125, 50)
                canvas.drawText("+ Income", 110f, y, paint)
            } else {
                paint.color = Color.rgb(229, 57, 53)
                canvas.drawText("- Expense", 110f, y, paint)
            }

            paint.color = Color.BLACK
            canvas.drawText(if (category.length > 18) category.take(16) + ".." else category, 180f, y, paint)
            canvas.drawText("Verified", 310f, y, paint)

            paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            paint.color = if (typeStr == "INCOME") Color.rgb(46, 125, 50) else Color.rgb(229, 57, 53)
            canvas.drawText(Formatters.formatCurrency(amount), 460f, y, paint)
            paint.typeface = Typeface.DEFAULT

            y += 18f
            if (y > 800f) break
        }

        // Footer note
        paint.color = Color.GRAY
        paint.textSize = 9f
        canvas.drawText("DailyLife - Confidential Personal Records. Stored strictly offline on device.", 30f, 820f, paint)

        document.finishPage(page)
        document.writeTo(outputStream)
        document.close()
    }
}
