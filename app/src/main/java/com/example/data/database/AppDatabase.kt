package com.example.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.dao.BillDao
import com.example.data.dao.BudgetDao
import com.example.data.dao.CategoryDao
import com.example.data.dao.ExpenseDao
import com.example.data.dao.IncomeDao
import com.example.data.dao.LoanDao
import com.example.data.dao.NoteDao
import com.example.data.dao.SavingsDao
import com.example.data.dao.TaskDao
import com.example.data.model.Bill
import com.example.data.model.Budget
import com.example.data.model.CategoryItem
import com.example.data.model.Expense
import com.example.data.model.Income
import com.example.data.model.LoanPayment
import com.example.data.model.LoanRecord
import com.example.data.model.PersonalNote
import com.example.data.model.SavingsDeposit
import com.example.data.model.SavingsGoal
import com.example.data.model.TaskItem
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        Expense::class,
        Income::class,
        Budget::class,
        Bill::class,
        TaskItem::class,
        PersonalNote::class,
        SavingsGoal::class,
        SavingsDeposit::class,
        LoanRecord::class,
        LoanPayment::class,
        CategoryItem::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun expenseDao(): ExpenseDao
    abstract fun incomeDao(): IncomeDao
    abstract fun budgetDao(): BudgetDao
    abstract fun billDao(): BillDao
    abstract fun taskDao(): TaskDao
    abstract fun noteDao(): NoteDao
    abstract fun savingsDao(): SavingsDao
    abstract fun loanDao(): LoanDao
    abstract fun categoryDao(): CategoryDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "daily_life_database"
                )
                    .addCallback(DatabaseCallback())
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }

        private class DatabaseCallback : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    CoroutineScope(Dispatchers.IO).launch {
                        populateDefaultData(database)
                    }
                }
            }

            private suspend fun populateDefaultData(db: AppDatabase) {
                val defaultCategories = listOf(
                    "Food", "Grocery", "Shopping", "Travel", "Fuel",
                    "Medical", "Education", "Electricity", "Mobile Recharge",
                    "Internet", "Rent", "Entertainment", "Household", "Personal", "Other"
                )
                val catItems = defaultCategories.map {
                    CategoryItem(name = it, type = "EXPENSE")
                }
                db.categoryDao().insertAll(catItems)

                // Add starter sample items so the dashboard displays live charts immediately
                val now = System.currentTimeMillis()
                val oneDay = 86400000L

                // Starter Income
                db.incomeDao().insertIncome(
                    Income(
                        amount = 45000.0,
                        category = "Salary",
                        paymentMethod = "Bank",
                        timestamp = now - (oneDay * 2),
                        description = "Monthly Salary Deposit"
                    )
                )
                db.incomeDao().insertIncome(
                    Income(
                        amount = 5000.0,
                        category = "Freelance",
                        paymentMethod = "UPI",
                        timestamp = now - (oneDay * 1),
                        description = "Website Design Consulting"
                    )
                )

                // Starter Expenses
                db.expenseDao().insertExpense(
                    Expense(
                        amount = 1850.0,
                        category = "Grocery",
                        paymentMethod = "UPI",
                        timestamp = now,
                        description = "Weekly vegetable & pantry provisions"
                    )
                )
                db.expenseDao().insertExpense(
                    Expense(
                        amount = 450.0,
                        category = "Food",
                        paymentMethod = "UPI",
                        timestamp = now,
                        description = "Lunch with team"
                    )
                )
                db.expenseDao().insertExpense(
                    Expense(
                        amount = 1200.0,
                        category = "Fuel",
                        paymentMethod = "Card",
                        timestamp = now - oneDay,
                        description = "Petrol refill"
                    )
                )
                db.expenseDao().insertExpense(
                    Expense(
                        amount = 2499.0,
                        category = "Electricity",
                        paymentMethod = "Bank",
                        timestamp = now - (oneDay * 3),
                        description = "State Electricity Board bill"
                    )
                )

                // Starter Budget
                val javaCal = java.util.Calendar.getInstance()
                val monthYearKey = String.format("%02d-%04d", javaCal.get(java.util.Calendar.MONTH) + 1, javaCal.get(java.util.Calendar.YEAR))
                db.budgetDao().insertBudget(
                    Budget(
                        monthYear = monthYearKey,
                        category = "ALL",
                        amount = 30000.0
                    )
                )
                db.budgetDao().insertBudget(
                    Budget(
                        monthYear = monthYearKey,
                        category = "Grocery",
                        amount = 6000.0
                    )
                )
                db.budgetDao().insertBudget(
                    Budget(
                        monthYear = monthYearKey,
                        category = "Food",
                        amount = 4000.0
                    )
                )

                // Starter Bills
                db.billDao().insertBill(
                    Bill(
                        title = "Airtel Fiber Broadband",
                        category = "Internet",
                        amount = 999.0,
                        dueDate = now + (oneDay * 4),
                        isPaid = false,
                        frequency = "Monthly"
                    )
                )
                db.billDao().insertBill(
                    Bill(
                        title = "Apartment Maintenance / Rent",
                        category = "Rent",
                        amount = 12500.0,
                        dueDate = now + (oneDay * 7),
                        isPaid = false,
                        frequency = "Monthly"
                    )
                )

                // Starter Tasks
                db.taskDao().insertTask(
                    TaskItem(
                        title = "Review monthly credit card statement",
                        description = "Check all rewards points and unauthorized debits",
                        priority = "HIGH",
                        dueDate = now,
                        dueTime = "06:00 PM",
                        isCompleted = false
                    )
                )
                db.taskDao().insertTask(
                    TaskItem(
                        title = "Renew two-wheeler vehicle insurance",
                        description = "Compare quotes online before Friday",
                        priority = "MEDIUM",
                        dueDate = now + (oneDay * 2),
                        dueTime = "11:00 AM",
                        isCompleted = false
                    )
                )

                // Starter Note
                db.noteDao().insertNote(
                    PersonalNote(
                        title = "Weekend Shopping Checklist",
                        content = "1. Almond milk\n2. Whole wheat flour\n3. Olive oil\n4. Green tea",
                        isPinned = true,
                        colorTagHex = "#E0F2FE",
                        updatedAt = now
                    )
                )

                // Starter Savings Goal
                val emergencyGoalId = db.savingsDao().insertGoal(
                    SavingsGoal(
                        title = "Emergency Contingency Fund",
                        targetAmount = 100000.0,
                        currentAmount = 35000.0,
                        targetDate = now + (oneDay * 180),
                        note = "Target 3-6 months essential expenses",
                        colorHex = "#00695C"
                    )
                )
                db.savingsDao().insertDeposit(
                    SavingsDeposit(
                        goalId = emergencyGoalId,
                        amount = 10000.0,
                        timestamp = now - (oneDay * 10),
                        note = "Initial deposit from bonus"
                    )
                )

                // Starter Loan
                db.loanDao().insertLoan(
                    LoanRecord(
                        personName = "Rahul Verma",
                        phoneNumber = "+91 98765 43210",
                        type = "LENT",
                        originalAmount = 5000.0,
                        paidAmount = 2000.0,
                        transactionDate = now - (oneDay * 14),
                        dueDate = now + (oneDay * 15),
                        notes = "Lent for travel booking, promised to return by month end",
                        isSettled = false
                    )
                )
            }
        }
    }
}
