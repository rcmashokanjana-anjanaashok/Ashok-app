package com.example.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
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

@Dao
interface ExpenseDao {
    @Query("SELECT * FROM expenses ORDER BY timestamp DESC")
    fun getAllExpenses(): Flow<List<Expense>>

    @Query("SELECT * FROM expenses WHERE timestamp BETWEEN :startTimestamp AND :endTimestamp ORDER BY timestamp DESC")
    fun getExpensesBetween(startTimestamp: Long, endTimestamp: Long): Flow<List<Expense>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExpense(expense: Expense): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(expenses: List<Expense>)

    @Update
    suspend fun updateExpense(expense: Expense)

    @Delete
    suspend fun deleteExpense(expense: Expense)

    @Query("DELETE FROM expenses WHERE id = :id")
    suspend fun deleteExpenseById(id: Long)

    @Query("DELETE FROM expenses")
    suspend fun clearAll()
}

@Dao
interface IncomeDao {
    @Query("SELECT * FROM incomes ORDER BY timestamp DESC")
    fun getAllIncomes(): Flow<List<Income>>

    @Query("SELECT * FROM incomes WHERE timestamp BETWEEN :startTimestamp AND :endTimestamp ORDER BY timestamp DESC")
    fun getIncomesBetween(startTimestamp: Long, endTimestamp: Long): Flow<List<Income>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertIncome(income: Income): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(incomes: List<Income>)

    @Update
    suspend fun updateIncome(income: Income)

    @Delete
    suspend fun deleteIncome(income: Income)

    @Query("DELETE FROM incomes WHERE id = :id")
    suspend fun deleteIncomeById(id: Long)

    @Query("DELETE FROM incomes")
    suspend fun clearAll()
}

@Dao
interface BudgetDao {
    @Query("SELECT * FROM budgets WHERE monthYear = :monthYear")
    fun getBudgetsForMonth(monthYear: String): Flow<List<Budget>>

    @Query("SELECT * FROM budgets ORDER BY id DESC")
    fun getAllBudgets(): Flow<List<Budget>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBudget(budget: Budget): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(budgets: List<Budget>)

    @Update
    suspend fun updateBudget(budget: Budget)

    @Delete
    suspend fun deleteBudget(budget: Budget)

    @Query("DELETE FROM budgets")
    suspend fun clearAll()
}

@Dao
interface BillDao {
    @Query("SELECT * FROM bills ORDER BY isPaid ASC, dueDate ASC")
    fun getAllBills(): Flow<List<Bill>>

    @Query("SELECT * FROM bills WHERE isPaid = 0 ORDER BY dueDate ASC")
    fun getPendingBills(): Flow<List<Bill>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBill(bill: Bill): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(bills: List<Bill>)

    @Update
    suspend fun updateBill(bill: Bill)

    @Delete
    suspend fun deleteBill(bill: Bill)

    @Query("DELETE FROM bills WHERE id = :id")
    suspend fun deleteBillById(id: Long)

    @Query("DELETE FROM bills")
    suspend fun clearAll()
}

@Dao
interface TaskDao {
    @Query("SELECT * FROM tasks ORDER BY isCompleted ASC, dueDate ASC")
    fun getAllTasks(): Flow<List<TaskItem>>

    @Query("SELECT * FROM tasks WHERE isCompleted = 0 ORDER BY dueDate ASC")
    fun getPendingTasks(): Flow<List<TaskItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(task: TaskItem): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(tasks: List<TaskItem>)

    @Update
    suspend fun updateTask(task: TaskItem)

    @Delete
    suspend fun deleteTask(task: TaskItem)

    @Query("DELETE FROM tasks WHERE id = :id")
    suspend fun deleteTaskById(id: Long)

    @Query("DELETE FROM tasks")
    suspend fun clearAll()
}

@Dao
interface NoteDao {
    @Query("SELECT * FROM notes ORDER BY isPinned DESC, updatedAt DESC")
    fun getAllNotes(): Flow<List<PersonalNote>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNote(note: PersonalNote): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(notes: List<PersonalNote>)

    @Update
    suspend fun updateNote(note: PersonalNote)

    @Delete
    suspend fun deleteNote(note: PersonalNote)

    @Query("DELETE FROM notes WHERE id = :id")
    suspend fun deleteNoteById(id: Long)

    @Query("DELETE FROM notes")
    suspend fun clearAll()
}

@Dao
interface SavingsDao {
    @Query("SELECT * FROM savings_goals ORDER BY id DESC")
    fun getAllGoals(): Flow<List<SavingsGoal>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGoal(goal: SavingsGoal): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllGoals(goals: List<SavingsGoal>)

    @Update
    suspend fun updateGoal(goal: SavingsGoal)

    @Delete
    suspend fun deleteGoal(goal: SavingsGoal)

    @Query("DELETE FROM savings_goals WHERE id = :id")
    suspend fun deleteGoalById(id: Long)

    @Query("SELECT * FROM savings_transactions WHERE goalId = :goalId ORDER BY timestamp DESC")
    fun getTransactionsForGoal(goalId: Long): Flow<List<SavingsDeposit>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDeposit(deposit: SavingsDeposit): Long

    @Query("DELETE FROM savings_goals")
    suspend fun clearAllGoals()
}

@Dao
interface LoanDao {
    @Query("SELECT * FROM loans ORDER BY isSettled ASC, dueDate ASC")
    fun getAllLoans(): Flow<List<LoanRecord>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLoan(loan: LoanRecord): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllLoans(loans: List<LoanRecord>)

    @Update
    suspend fun updateLoan(loan: LoanRecord)

    @Delete
    suspend fun deleteLoan(loan: LoanRecord)

    @Query("DELETE FROM loans WHERE id = :id")
    suspend fun deleteLoanById(id: Long)

    @Query("SELECT * FROM loan_payments WHERE loanId = :loanId ORDER BY paymentDate DESC")
    fun getPaymentsForLoan(loanId: Long): Flow<List<LoanPayment>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPayment(payment: LoanPayment): Long

    @Query("DELETE FROM loans")
    suspend fun clearAllLoans()
}

@Dao
interface CategoryDao {
    @Query("SELECT * FROM categories ORDER BY name ASC")
    fun getAllCategories(): Flow<List<CategoryItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCategory(category: CategoryItem): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(categories: List<CategoryItem>)

    @Delete
    suspend fun deleteCategory(category: CategoryItem)

    @Query("SELECT COUNT(*) FROM categories")
    suspend fun getCategoryCount(): Int
}
