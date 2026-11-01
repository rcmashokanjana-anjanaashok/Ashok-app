package com.example.data.repository

import com.example.data.database.AppDatabase
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
import kotlinx.coroutines.flow.Flow

class DailyLifeRepository(private val db: AppDatabase) {

    // Expenses
    val allExpenses: Flow<List<Expense>> = db.expenseDao().getAllExpenses()
    fun getExpensesBetween(start: Long, end: Long): Flow<List<Expense>> = db.expenseDao().getExpensesBetween(start, end)
    suspend fun insertExpense(expense: Expense): Long = db.expenseDao().insertExpense(expense)
    suspend fun updateExpense(expense: Expense) = db.expenseDao().updateExpense(expense)
    suspend fun deleteExpense(expense: Expense) = db.expenseDao().deleteExpense(expense)
    suspend fun deleteExpenseById(id: Long) = db.expenseDao().deleteExpenseById(id)

    // Incomes
    val allIncomes: Flow<List<Income>> = db.incomeDao().getAllIncomes()
    fun getIncomesBetween(start: Long, end: Long): Flow<List<Income>> = db.incomeDao().getIncomesBetween(start, end)
    suspend fun insertIncome(income: Income): Long = db.incomeDao().insertIncome(income)
    suspend fun updateIncome(income: Income) = db.incomeDao().updateIncome(income)
    suspend fun deleteIncome(income: Income) = db.incomeDao().deleteIncome(income)
    suspend fun deleteIncomeById(id: Long) = db.incomeDao().deleteIncomeById(id)

    // Budgets
    val allBudgets: Flow<List<Budget>> = db.budgetDao().getAllBudgets()
    fun getBudgetsForMonth(monthYear: String): Flow<List<Budget>> = db.budgetDao().getBudgetsForMonth(monthYear)
    suspend fun insertBudget(budget: Budget): Long = db.budgetDao().insertBudget(budget)
    suspend fun updateBudget(budget: Budget) = db.budgetDao().updateBudget(budget)
    suspend fun deleteBudget(budget: Budget) = db.budgetDao().deleteBudget(budget)

    // Bills
    val allBills: Flow<List<Bill>> = db.billDao().getAllBills()
    val pendingBills: Flow<List<Bill>> = db.billDao().getPendingBills()
    suspend fun insertBill(bill: Bill): Long = db.billDao().insertBill(bill)
    suspend fun updateBill(bill: Bill) = db.billDao().updateBill(bill)
    suspend fun deleteBill(bill: Bill) = db.billDao().deleteBill(bill)
    suspend fun toggleBillPaid(bill: Bill) {
        val updated = bill.copy(
            isPaid = !bill.isPaid,
            paidDate = if (!bill.isPaid) System.currentTimeMillis() else null
        )
        db.billDao().updateBill(updated)
    }

    // Tasks
    val allTasks: Flow<List<TaskItem>> = db.taskDao().getAllTasks()
    val pendingTasks: Flow<List<TaskItem>> = db.taskDao().getPendingTasks()
    suspend fun insertTask(task: TaskItem): Long = db.taskDao().insertTask(task)
    suspend fun updateTask(task: TaskItem) = db.taskDao().updateTask(task)
    suspend fun deleteTask(task: TaskItem) = db.taskDao().deleteTask(task)
    suspend fun toggleTaskCompleted(task: TaskItem) {
        val updated = task.copy(isCompleted = !task.isCompleted)
        db.taskDao().updateTask(updated)
    }

    // Notes
    val allNotes: Flow<List<PersonalNote>> = db.noteDao().getAllNotes()
    suspend fun insertNote(note: PersonalNote): Long = db.noteDao().insertNote(note)
    suspend fun updateNote(note: PersonalNote) = db.noteDao().updateNote(note)
    suspend fun deleteNote(note: PersonalNote) = db.noteDao().deleteNote(note)
    suspend fun toggleNotePinned(note: PersonalNote) {
        val updated = note.copy(isPinned = !note.isPinned, updatedAt = System.currentTimeMillis())
        db.noteDao().updateNote(updated)
    }

    // Savings
    val allGoals: Flow<List<SavingsGoal>> = db.savingsDao().getAllGoals()
    fun getTransactionsForGoal(goalId: Long): Flow<List<SavingsDeposit>> = db.savingsDao().getTransactionsForGoal(goalId)
    suspend fun insertGoal(goal: SavingsGoal): Long = db.savingsDao().insertGoal(goal)
    suspend fun updateGoal(goal: SavingsGoal) = db.savingsDao().updateGoal(goal)
    suspend fun deleteGoal(goal: SavingsGoal) = db.savingsDao().deleteGoal(goal)
    suspend fun addDeposit(goalId: Long, amount: Double, note: String) {
        db.savingsDao().insertDeposit(SavingsDeposit(goalId = goalId, amount = amount, note = note))
        // Update goal current amount
        // In Room we can update the goal
    }

    // Loans
    val allLoans: Flow<List<LoanRecord>> = db.loanDao().getAllLoans()
    fun getPaymentsForLoan(loanId: Long): Flow<List<LoanPayment>> = db.loanDao().getPaymentsForLoan(loanId)
    suspend fun insertLoan(loan: LoanRecord): Long = db.loanDao().insertLoan(loan)
    suspend fun updateLoan(loan: LoanRecord) = db.loanDao().updateLoan(loan)
    suspend fun deleteLoan(loan: LoanRecord) = db.loanDao().deleteLoan(loan)
    suspend fun recordLoanPayment(loanId: Long, amount: Double, note: String) {
        db.loanDao().insertPayment(LoanPayment(loanId = loanId, amount = amount, note = note))
    }

    // Categories
    val allCategories: Flow<List<CategoryItem>> = db.categoryDao().getAllCategories()
    suspend fun insertCategory(category: CategoryItem): Long = db.categoryDao().insertCategory(category)
    suspend fun deleteCategory(category: CategoryItem) = db.categoryDao().deleteCategory(category)

    // Data restore / clear
    suspend fun clearAndRestore(
        expenses: List<Expense>,
        incomes: List<Income>,
        budgets: List<Budget>,
        bills: List<Bill>,
        tasks: List<TaskItem>,
        notes: List<PersonalNote>,
        goals: List<SavingsGoal>,
        loans: List<LoanRecord>
    ) {
        db.expenseDao().clearAll()
        db.incomeDao().clearAll()
        db.budgetDao().clearAll()
        db.billDao().clearAll()
        db.taskDao().clearAll()
        db.noteDao().clearAll()
        db.savingsDao().clearAllGoals()
        db.loanDao().clearAllLoans()

        db.expenseDao().insertAll(expenses)
        db.incomeDao().insertAll(incomes)
        db.budgetDao().insertAll(budgets)
        db.billDao().insertAll(bills)
        db.taskDao().insertAll(tasks)
        db.noteDao().insertAll(notes)
        db.savingsDao().insertAllGoals(goals)
        db.loanDao().insertAllLoans(loans)
    }
}
