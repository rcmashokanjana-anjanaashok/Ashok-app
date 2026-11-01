package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "expenses")
data class Expense(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val amount: Double,
    val category: String,
    val paymentMethod: String, // "Cash", "UPI", "Bank Transfer", "Card"
    val timestamp: Long = System.currentTimeMillis(),
    val description: String = "",
    val photoUri: String? = null,
    val tags: String = ""
)

@Entity(tableName = "incomes")
data class Income(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val amount: Double,
    val category: String, // "Salary", "Business", "Freelance", "Investment", "Cash Received", "Other"
    val paymentMethod: String, // "Bank", "Cash", "UPI", "Cheque"
    val timestamp: Long = System.currentTimeMillis(),
    val description: String = ""
)

@Entity(tableName = "budgets")
data class Budget(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val monthYear: String, // e.g. "11-2026"
    val category: String, // "ALL" or specific category name
    val amount: Double
)

@Entity(tableName = "bills")
data class Bill(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val category: String, // "Electricity", "Water", "Mobile Recharge", "Internet", "Rent", "EMI", "Insurance", "Fees", "Credit Card", "Other"
    val amount: Double,
    val dueDate: Long, // timestamp
    val isPaid: Boolean = false,
    val frequency: String = "Monthly", // "One-Time", "Monthly", "Quarterly", "Yearly"
    val paidDate: Long? = null,
    val reminderEnabled: Boolean = true,
    val notes: String = ""
)

@Entity(tableName = "tasks")
data class TaskItem(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val description: String = "",
    val priority: String = "MEDIUM", // "HIGH", "MEDIUM", "LOW"
    val dueDate: Long, // timestamp
    val dueTime: String = "10:00 AM",
    val isCompleted: Boolean = false,
    val reminderEnabled: Boolean = true,
    val checklistJson: String = "" // e.g. "Item 1,Item 2"
)

@Entity(tableName = "notes")
data class PersonalNote(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val content: String = "",
    val isPinned: Boolean = false,
    val colorTagHex: String = "#FFFFFF",
    val updatedAt: Long = System.currentTimeMillis(),
    val checklistItems: String = "" // JSON or newline separated items
)

@Entity(tableName = "savings_goals")
data class SavingsGoal(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val targetAmount: Double,
    val currentAmount: Double = 0.0,
    val targetDate: Long, // timestamp
    val note: String = "",
    val colorHex: String = "#00695C"
)

@Entity(tableName = "savings_transactions")
data class SavingsDeposit(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val goalId: Long,
    val amount: Double,
    val timestamp: Long = System.currentTimeMillis(),
    val note: String = ""
)

@Entity(tableName = "loans")
data class LoanRecord(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val personName: String,
    val phoneNumber: String = "",
    val type: String, // "LENT" (Gave), "BORROWED" (Took)
    val originalAmount: Double,
    val paidAmount: Double = 0.0,
    val transactionDate: Long = System.currentTimeMillis(),
    val dueDate: Long = System.currentTimeMillis() + 30L * 86400000L,
    val notes: String = "",
    val isSettled: Boolean = false
)

@Entity(tableName = "loan_payments")
data class LoanPayment(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val loanId: Long,
    val amount: Double,
    val paymentDate: Long = System.currentTimeMillis(),
    val note: String = ""
)

@Entity(tableName = "categories")
data class CategoryItem(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val type: String = "EXPENSE", // "EXPENSE" or "INCOME"
    val iconName: String = "category",
    val colorHex: String = "#00695C"
)
