package com.example

import com.example.data.datastore.SettingsDataStore
import com.example.data.model.Bill
import com.example.data.model.Budget
import com.example.data.model.Expense
import com.example.data.model.Income
import com.example.util.ExportHelper
import com.example.util.Formatters
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class DailyLifeLogicTest {

    @Test
    fun testCurrencyFormatting() {
        val formatted = Formatters.formatCurrency(125000.0)
        assertTrue("Formatted currency should contain rupee symbol", formatted.contains("₹"))
        assertTrue("Formatted currency should format Indian thousands", formatted.contains("1,25,000"))
        assertEquals("₹1,25,000.00", formatted)

        val smallAmount = Formatters.formatCurrency(450.0)
        assertEquals("₹450.00", smallAmount)

        val lakhsAmount = Formatters.formatCurrency(2500000.0, includeDecimals = false)
        assertEquals("₹25,00,000", lakhsAmount)
    }

    @Test
    fun testFinancialCalculations() {
        val incomes = listOf(
            Income(id = 1, amount = 45000.0, category = "Salary", paymentMethod = "Bank"),
            Income(id = 2, amount = 5000.0, category = "Freelance", paymentMethod = "UPI")
        )
        val expenses = listOf(
            Expense(id = 1, amount = 1850.0, category = "Grocery", paymentMethod = "UPI"),
            Expense(id = 2, amount = 450.0, category = "Food", paymentMethod = "Cash"),
            Expense(id = 3, amount = 1200.0, category = "Fuel", paymentMethod = "Card")
        )

        val totalIncome = incomes.sumOf { it.amount }
        val totalExpense = expenses.sumOf { it.amount }
        val balance = totalIncome - totalExpense

        assertEquals(50000.0, totalIncome, 0.001)
        assertEquals(3500.0, totalExpense, 0.001)
        assertEquals(46500.0, balance, 0.001)

        val budget = Budget(id = 1, monthYear = "11-2026", category = "ALL", amount = 30000.0)
        val remainingBudget = budget.amount - totalExpense
        val utilizationPct = ((totalExpense / budget.amount) * 100).toInt()

        assertEquals(26500.0, remainingBudget, 0.001)
        assertEquals(11, utilizationPct)
    }

    @Test
    fun testCsvExport() {
        val sampleExpenses = listOf(
            Expense(id = 1, amount = 450.0, category = "Food", paymentMethod = "UPI", description = "Lunch"),
            Expense(id = 2, amount = 1200.0, category = "Fuel", paymentMethod = "Card", description = "Petrol")
        )
        val csv = ExportHelper.generateExpensesCsv(sampleExpenses)
        assertTrue("CSV header should be present", csv.contains("ID,Date,Category,Amount (INR),Payment Method,Description"))
        assertTrue("CSV should contain expense record", csv.contains("Food"))
        assertTrue("CSV should contain amount", csv.contains("450.0"))
    }

    @Test
    fun testBillPendingLogic() {
        val bills = listOf(
            Bill(id = 1, title = "Airtel Fiber", category = "Internet", amount = 999.0, dueDate = 1700000000000L, isPaid = false),
            Bill(id = 2, title = "Rent", category = "Rent", amount = 12000.0, dueDate = 1700000000000L, isPaid = true)
        )
        val pendingBills = bills.filter { !it.isPaid }
        assertEquals(1, pendingBills.size)
        assertEquals(999.0, pendingBills.sumOf { it.amount }, 0.001)
    }

    @Test
    fun testSettingsDataStoreKeys() {
        assertEquals("dark_mode", SettingsDataStore.KEY_DARK_MODE.name)
        assertEquals("notification_reminders", SettingsDataStore.KEY_NOTIFICATION_REMINDERS.name)
        assertEquals("biometric_lock", SettingsDataStore.KEY_BIOMETRIC_LOCK.name)
    }
}
