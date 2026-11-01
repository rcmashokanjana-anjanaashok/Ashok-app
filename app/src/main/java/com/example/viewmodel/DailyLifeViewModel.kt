package com.example.viewmodel

import android.app.AlarmManager
import android.app.Application
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.DailyLifeApp
import com.example.data.model.Bill
import com.example.data.model.Budget
import com.example.data.model.CategoryItem
import com.example.data.model.Expense
import com.example.data.model.Income
import com.example.data.model.LoanRecord
import com.example.data.model.PersonalNote
import com.example.data.model.SavingsGoal
import com.example.data.model.TaskItem
import com.example.data.security.SecurityPreferences
import com.example.receiver.ReminderReceiver
import com.example.util.BackupData
import com.example.util.BackupHelper
import com.example.util.Formatters
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Calendar

data class DashboardSummary(
    val monthlyIncome: Double = 0.0,
    val monthlyExpense: Double = 0.0,
    val availableBalance: Double = 0.0,
    val todayExpense: Double = 0.0,
    val pendingBillsCount: Int = 0,
    val pendingBillsTotal: Double = 0.0,
    val upcomingBills: List<Bill> = emptyList(),
    val pendingTasksCount: Int = 0,
    val pendingTasks: List<TaskItem> = emptyList(),
    val monthlyBudget: Double = 0.0,
    val budgetSpentPercentage: Float = 0f,
    val totalSavings: Double = 0.0,
    val pendingToReceive: Double = 0.0,
    val pendingToPay: Double = 0.0
)

enum class DateRangeFilter {
    TODAY,
    THIS_WEEK,
    THIS_MONTH,
    THIS_YEAR,
    ALL,
    CUSTOM
}

class DailyLifeViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = (application as DailyLifeApp).repository
    val appSettingsPrefs = com.example.data.security.AppSettingsPreferences(application)
    val securityPrefs = appSettingsPrefs // Backwards compatible alias

    // Profile state
    val userName = MutableStateFlow(appSettingsPrefs.userName)
    val userEmail = MutableStateFlow(appSettingsPrefs.userEmail)
    val userPhone = MutableStateFlow(appSettingsPrefs.userPhone)
    val profilePhotoUri = MutableStateFlow(appSettingsPrefs.profilePhotoUri)
    val avatarPreset = MutableStateFlow(appSettingsPrefs.avatarPreset)

    // Settings on/off toggles
    val isDailyReminderEnabled = MutableStateFlow(appSettingsPrefs.isDailyReminderEnabled)
    val isBillAlertEnabled = MutableStateFlow(appSettingsPrefs.isBillAlertEnabled)
    val isTaskReminderEnabled = MutableStateFlow(appSettingsPrefs.isTaskReminderEnabled)
    val isBudgetAlertEnabled = MutableStateFlow(appSettingsPrefs.isBudgetAlertEnabled)

    val themeMode = MutableStateFlow(appSettingsPrefs.themeMode)
    val isHapticFeedbackEnabled = MutableStateFlow(appSettingsPrefs.isHapticFeedbackEnabled)
    val isMaskBalanceEnabled = MutableStateFlow(appSettingsPrefs.isMaskBalanceEnabled)
    val isCompactNumbersEnabled = MutableStateFlow(appSettingsPrefs.isCompactNumbersEnabled)
    val isDecimalPlacesEnabled = MutableStateFlow(appSettingsPrefs.isDecimalPlacesEnabled)
    val currencySymbol = MutableStateFlow(appSettingsPrefs.currencySymbol)
    val settingsDataStore = com.example.data.datastore.SettingsDataStore(application)

    // DataStore Persisted Flows
    val isDarkModeDataStore: StateFlow<Boolean> = settingsDataStore.isDarkModeFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    val isNotificationRemindersDataStore: StateFlow<Boolean> = settingsDataStore.isNotificationRemindersFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), true)

    val isBiometricLockDataStore: StateFlow<Boolean> = settingsDataStore.isBiometricLockFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    // Security state
    private val _isUnlocked = MutableStateFlow(!appSettingsPrefs.isPinEnabled)
    val isUnlocked: StateFlow<Boolean> = _isUnlocked.asStateFlow()

    // Core flows from Room
    val expenses: StateFlow<List<Expense>> = repository.allExpenses
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val incomes: StateFlow<List<Income>> = repository.allIncomes
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val budgets: StateFlow<List<Budget>> = repository.allBudgets
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val bills: StateFlow<List<Bill>> = repository.allBills
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val tasks: StateFlow<List<TaskItem>> = repository.allTasks
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val notes: StateFlow<List<PersonalNote>> = repository.allNotes
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val savingsGoals: StateFlow<List<SavingsGoal>> = repository.allGoals
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val loans: StateFlow<List<LoanRecord>> = repository.allLoans
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val categories: StateFlow<List<CategoryItem>> = repository.allCategories
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Filter states for Expenses & Reports
    val selectedFilter = MutableStateFlow(DateRangeFilter.THIS_MONTH)
    val customStartDate = MutableStateFlow(System.currentTimeMillis() - 30L * 86400000L)
    val customEndDate = MutableStateFlow(System.currentTimeMillis())
    val selectedCategoryFilter = MutableStateFlow("ALL")
    val searchQuery = MutableStateFlow("")

    // Dashboard aggregated metrics StateFlow
    val dashboardSummary: StateFlow<DashboardSummary> = combine(
        expenses,
        incomes,
        budgets,
        bills,
        tasks
    ) { expList: List<Expense>, incList: List<Income>, budList: List<Budget>, billList: List<Bill>, taskList: List<TaskItem> ->
        val goalList = savingsGoals.value
        val loanList = loans.value
        val now = System.currentTimeMillis()
        val cal = Calendar.getInstance()
        val currentMonth = cal.get(Calendar.MONTH)
        val currentYear = cal.get(Calendar.YEAR)
        val currentDay = cal.get(Calendar.DAY_OF_YEAR)

        // Month start and end timestamps
        cal.set(Calendar.DAY_OF_MONTH, 1)
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)
        val monthStart = cal.timeInMillis

        cal.add(Calendar.MONTH, 1)
        val monthEnd = cal.timeInMillis

        // Monthly totals
        val mIncome = incList.filter { it.timestamp in monthStart until monthEnd }.sumOf { it.amount }
        val mExpense = expList.filter { it.timestamp in monthStart until monthEnd }.sumOf { it.amount }
        val availableBal = incList.sumOf { it.amount } - expList.sumOf { it.amount }

        // Today expense
        val todayExp = expList.filter {
            val c = Calendar.getInstance().apply { timeInMillis = it.timestamp }
            c.get(Calendar.YEAR) == currentYear && c.get(Calendar.DAY_OF_YEAR) == currentDay
        }.sumOf { it.amount }

        // Bills
        val pendingBills = billList.filter { !it.isPaid }
        val pendingBillsTot = pendingBills.sumOf { it.amount }
        val upcoming = pendingBills.filter { it.dueDate >= now - 86400000L && it.dueDate <= now + (7L * 86400000L) }

        // Tasks
        val pendingT = taskList.filter { !it.isCompleted }

        // Budget for current month
        val monthKey = String.format("%02d-%04d", currentMonth + 1, currentYear)
        val totalBudgetObj = budList.firstOrNull { it.monthYear == monthKey && it.category == "ALL" }
        val monthlyBud = totalBudgetObj?.amount ?: 0.0
        val spentPct = if (monthlyBud > 0) ((mExpense / monthlyBud).toFloat()).coerceIn(0f, 1.5f) else 0f

        // Savings
        val totalSav = goalList.sumOf { it.currentAmount }

        // Loans (Khata)
        val toReceive = loanList.filter { it.type == "LENT" && !it.isSettled }
            .sumOf { (it.originalAmount - it.paidAmount).coerceAtLeast(0.0) }
        val toPay = loanList.filter { it.type == "BORROWED" && !it.isSettled }
            .sumOf { (it.originalAmount - it.paidAmount).coerceAtLeast(0.0) }

        DashboardSummary(
            monthlyIncome = mIncome,
            monthlyExpense = mExpense,
            availableBalance = availableBal,
            todayExpense = todayExp,
            pendingBillsCount = pendingBills.size,
            pendingBillsTotal = pendingBillsTot,
            upcomingBills = upcoming,
            pendingTasksCount = pendingT.size,
            pendingTasks = pendingT,
            monthlyBudget = monthlyBud,
            budgetSpentPercentage = spentPct,
            totalSavings = totalSav,
            pendingToReceive = toReceive,
            pendingToPay = toPay
        )
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        DashboardSummary()
    )

    // Unlock PIN
    fun unlockWithPin(pin: String): Boolean {
        if (securityPrefs.verifyPin(pin)) {
            _isUnlocked.value = true
            return true
        }
        return false
    }

    fun lockApp() {
        if (securityPrefs.isPinEnabled) {
            _isUnlocked.value = false
        }
    }

    fun setAppPin(pin: String) {
        securityPrefs.setPin(pin)
        _isUnlocked.value = true
    }

    fun disableAppPin() {
        securityPrefs.disablePin()
        _isUnlocked.value = true
    }

    // Profile updates
    fun updateProfile(name: String, email: String, phone: String) {
        appSettingsPrefs.userName = name
        appSettingsPrefs.userEmail = email
        appSettingsPrefs.userPhone = phone
        userName.value = name
        userEmail.value = email
        userPhone.value = phone
    }

    fun setProfilePhotoUri(uriString: String?) {
        appSettingsPrefs.profilePhotoUri = uriString
        profilePhotoUri.value = uriString
    }

    fun setAvatarPreset(preset: Int) {
        appSettingsPrefs.avatarPreset = preset
        appSettingsPrefs.profilePhotoUri = null
        avatarPreset.value = preset
        profilePhotoUri.value = null
    }

    // Settings on/off toggles
    fun setDailyReminderEnabled(enabled: Boolean) {
        appSettingsPrefs.isDailyReminderEnabled = enabled
        isDailyReminderEnabled.value = enabled
    }

    fun setBillAlertEnabled(enabled: Boolean) {
        appSettingsPrefs.isBillAlertEnabled = enabled
        isBillAlertEnabled.value = enabled
    }

    fun setTaskReminderEnabled(enabled: Boolean) {
        appSettingsPrefs.isTaskReminderEnabled = enabled
        isTaskReminderEnabled.value = enabled
    }

    fun setBudgetAlertEnabled(enabled: Boolean) {
        appSettingsPrefs.isBudgetAlertEnabled = enabled
        isBudgetAlertEnabled.value = enabled
    }

    fun setThemeMode(mode: String) {
        appSettingsPrefs.themeMode = mode
        themeMode.value = mode
    }

    fun setHapticFeedbackEnabled(enabled: Boolean) {
        appSettingsPrefs.isHapticFeedbackEnabled = enabled
        isHapticFeedbackEnabled.value = enabled
    }

    fun setMaskBalanceEnabled(enabled: Boolean) {
        appSettingsPrefs.isMaskBalanceEnabled = enabled
        isMaskBalanceEnabled.value = enabled
    }

    fun setCompactNumbersEnabled(enabled: Boolean) {
        appSettingsPrefs.isCompactNumbersEnabled = enabled
        isCompactNumbersEnabled.value = enabled
    }

    fun setDecimalPlacesEnabled(enabled: Boolean) {
        appSettingsPrefs.isDecimalPlacesEnabled = enabled
        isDecimalPlacesEnabled.value = enabled
    }

    fun setCurrencySymbol(symbol: String) {
        appSettingsPrefs.currencySymbol = symbol
        currencySymbol.value = symbol
    }

    // DataStore Persisted Setters
    fun setDarkModeDataStore(enabled: Boolean) {
        viewModelScope.launch {
            settingsDataStore.setDarkMode(enabled)
            themeMode.value = if (enabled) "DARK" else "LIGHT"
        }
    }

    fun setNotificationRemindersDataStore(enabled: Boolean) {
        viewModelScope.launch {
            settingsDataStore.setNotificationReminders(enabled)
            isDailyReminderEnabled.value = enabled
            isBillAlertEnabled.value = enabled
            isTaskReminderEnabled.value = enabled
        }
    }

    fun setBiometricLockDataStore(enabled: Boolean) {
        viewModelScope.launch {
            settingsDataStore.setBiometricLock(enabled)
        }
    }

    // --- Expense Actions ---
    fun addExpense(
        amount: Double,
        category: String,
        paymentMethod: String,
        timestamp: Long = System.currentTimeMillis(),
        description: String = "",
        photoUri: String? = null
    ) {
        viewModelScope.launch {
            repository.insertExpense(
                Expense(
                    amount = amount,
                    category = category,
                    paymentMethod = paymentMethod,
                    timestamp = timestamp,
                    description = description,
                    photoUri = photoUri
                )
            )
        }
    }

    fun updateExpense(expense: Expense) {
        viewModelScope.launch {
            repository.updateExpense(expense)
        }
    }

    fun deleteExpense(expense: Expense) {
        viewModelScope.launch {
            repository.deleteExpense(expense)
        }
    }

    // --- Income Actions ---
    fun addIncome(
        amount: Double,
        category: String,
        paymentMethod: String,
        timestamp: Long = System.currentTimeMillis(),
        description: String = ""
    ) {
        viewModelScope.launch {
            repository.insertIncome(
                Income(
                    amount = amount,
                    category = category,
                    paymentMethod = paymentMethod,
                    timestamp = timestamp,
                    description = description
                )
            )
        }
    }

    fun updateIncome(income: Income) {
        viewModelScope.launch {
            repository.updateIncome(income)
        }
    }

    fun deleteIncome(income: Income) {
        viewModelScope.launch {
            repository.deleteIncome(income)
        }
    }

    // --- Budget Actions ---
    fun setBudget(monthYear: String, category: String, amount: Double) {
        viewModelScope.launch {
            val existing = budgets.value.firstOrNull { it.monthYear == monthYear && it.category == category }
            if (existing != null) {
                repository.updateBudget(existing.copy(amount = amount))
            } else {
                repository.insertBudget(Budget(monthYear = monthYear, category = category, amount = amount))
            }
        }
    }

    fun deleteBudget(budget: Budget) {
        viewModelScope.launch {
            repository.deleteBudget(budget)
        }
    }

    // --- Bill Actions ---
    fun addBill(
        title: String,
        category: String,
        amount: Double,
        dueDate: Long,
        frequency: String = "Monthly",
        reminder: Boolean = true,
        notes: String = ""
    ) {
        viewModelScope.launch {
            val billId = repository.insertBill(
                Bill(
                    title = title,
                    category = category,
                    amount = amount,
                    dueDate = dueDate,
                    frequency = frequency,
                    reminderEnabled = reminder,
                    notes = notes
                )
            )
            if (reminder) {
                scheduleBillReminder(title, amount, dueDate, billId.toInt())
            }
        }
    }

    fun updateBill(bill: Bill) {
        viewModelScope.launch {
            repository.updateBill(bill)
        }
    }

    fun toggleBillPaid(bill: Bill) {
        viewModelScope.launch {
            repository.toggleBillPaid(bill)
        }
    }

    fun deleteBill(bill: Bill) {
        viewModelScope.launch {
            repository.deleteBill(bill)
        }
    }

    // --- Task Actions ---
    fun addTask(
        title: String,
        description: String = "",
        priority: String = "MEDIUM",
        dueDate: Long = System.currentTimeMillis(),
        dueTime: String = "10:00 AM",
        reminder: Boolean = true
    ) {
        viewModelScope.launch {
            val taskId = repository.insertTask(
                TaskItem(
                    title = title,
                    description = description,
                    priority = priority,
                    dueDate = dueDate,
                    dueTime = dueTime,
                    reminderEnabled = reminder
                )
            )
            if (reminder) {
                scheduleTaskReminder(title, dueDate, taskId.toInt())
            }
        }
    }

    fun updateTask(task: TaskItem) {
        viewModelScope.launch {
            repository.updateTask(task)
        }
    }

    fun toggleTaskCompleted(task: TaskItem) {
        viewModelScope.launch {
            repository.toggleTaskCompleted(task)
        }
    }

    fun deleteTask(task: TaskItem) {
        viewModelScope.launch {
            repository.deleteTask(task)
        }
    }

    // --- Note Actions ---
    fun addNote(title: String, content: String, isPinned: Boolean = false, colorHex: String = "#FFFFFF") {
        viewModelScope.launch {
            repository.insertNote(
                PersonalNote(
                    title = title,
                    content = content,
                    isPinned = isPinned,
                    colorTagHex = colorHex,
                    updatedAt = System.currentTimeMillis()
                )
            )
        }
    }

    fun updateNote(note: PersonalNote) {
        viewModelScope.launch {
            repository.updateNote(note.copy(updatedAt = System.currentTimeMillis()))
        }
    }

    fun toggleNotePinned(note: PersonalNote) {
        viewModelScope.launch {
            repository.toggleNotePinned(note)
        }
    }

    fun deleteNote(note: PersonalNote) {
        viewModelScope.launch {
            repository.deleteNote(note)
        }
    }

    // --- Savings Actions ---
    fun addSavingsGoal(title: String, targetAmount: Double, targetDate: Long, note: String = "", colorHex: String = "#00695C") {
        viewModelScope.launch {
            repository.insertGoal(
                SavingsGoal(
                    title = title,
                    targetAmount = targetAmount,
                    currentAmount = 0.0,
                    targetDate = targetDate,
                    note = note,
                    colorHex = colorHex
                )
            )
        }
    }

    fun addSavingsDeposit(goal: SavingsGoal, amount: Double, note: String) {
        viewModelScope.launch {
            val updated = goal.copy(currentAmount = goal.currentAmount + amount)
            repository.updateGoal(updated)
            repository.addDeposit(goal.id, amount, note)
        }
    }

    fun deleteSavingsGoal(goal: SavingsGoal) {
        viewModelScope.launch {
            repository.deleteGoal(goal)
        }
    }

    // --- Loan (Khata) Actions ---
    fun addLoan(
        personName: String,
        phoneNumber: String,
        type: String, // "LENT" or "BORROWED"
        originalAmount: Double,
        dueDate: Long,
        notes: String = ""
    ) {
        viewModelScope.launch {
            repository.insertLoan(
                LoanRecord(
                    personName = personName,
                    phoneNumber = phoneNumber,
                    type = type,
                    originalAmount = originalAmount,
                    paidAmount = 0.0,
                    dueDate = dueDate,
                    notes = notes,
                    isSettled = false
                )
            )
        }
    }

    fun recordLoanPayment(loan: LoanRecord, paymentAmount: Double, note: String) {
        viewModelScope.launch {
            val newPaid = loan.paidAmount + paymentAmount
            val settled = newPaid >= loan.originalAmount
            val updated = loan.copy(paidAmount = newPaid, isSettled = settled)
            repository.updateLoan(updated)
            repository.recordLoanPayment(loan.id, paymentAmount, note)
        }
    }

    fun deleteLoan(loan: LoanRecord) {
        viewModelScope.launch {
            repository.deleteLoan(loan)
        }
    }

    // --- Category Management ---
    fun addCategory(name: String, type: String = "EXPENSE") {
        viewModelScope.launch {
            repository.insertCategory(CategoryItem(name = name, type = type))
        }
    }

    fun deleteCategory(category: CategoryItem) {
        viewModelScope.launch {
            repository.deleteCategory(category)
        }
    }

    // --- Backup & Restore ---
    fun generateBackupJson(): String {
        return BackupHelper.createBackupJson(
            expenses = expenses.value,
            incomes = incomes.value,
            budgets = budgets.value,
            bills = bills.value,
            tasks = tasks.value,
            notes = notes.value,
            goals = savingsGoals.value,
            loans = loans.value
        )
    }

    fun restoreFromJson(jsonString: String, onComplete: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            try {
                val data = BackupHelper.parseBackupJson(jsonString)
                repository.clearAndRestore(
                    expenses = data.expenses,
                    incomes = data.incomes,
                    budgets = data.budgets,
                    bills = data.bills,
                    tasks = data.tasks,
                    notes = data.notes,
                    goals = data.goals,
                    loans = data.loans
                )
                onComplete(true, "Successfully restored ${data.expenses.size} expenses, ${data.incomes.size} income records, and all data.")
            } catch (e: Exception) {
                onComplete(false, "Failed to restore backup: ${e.localizedMessage}")
            }
        }
    }

    // Local notification scheduling
    private fun scheduleBillReminder(title: String, amount: Double, dueDate: Long, id: Int) {
        try {
            val context = getApplication<Application>()
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            val intent = Intent(context, ReminderReceiver::class.java).apply {
                putExtra(ReminderReceiver.EXTRA_TITLE, "Bill Due Reminder: $title")
                putExtra(ReminderReceiver.EXTRA_MESSAGE, "Amount ${Formatters.formatCurrency(amount)} is due on ${Formatters.formatDate(dueDate)}")
                putExtra(ReminderReceiver.EXTRA_ID, id)
            }
            val pendingIntent = PendingIntent.getBroadcast(
                context,
                id,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            // Schedule alert at 9:00 AM on due date if in the future
            if (dueDate > System.currentTimeMillis()) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    alarmManager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, dueDate, pendingIntent)
                } else {
                    alarmManager.set(AlarmManager.RTC_WAKEUP, dueDate, pendingIntent)
                }
            }
        } catch (_: Exception) {}
    }

    private fun scheduleTaskReminder(title: String, dueDate: Long, id: Int) {
        try {
            val context = getApplication<Application>()
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            val intent = Intent(context, ReminderReceiver::class.java).apply {
                putExtra(ReminderReceiver.EXTRA_TITLE, "Daily Task Reminder")
                putExtra(ReminderReceiver.EXTRA_MESSAGE, "Pending: $title")
                putExtra(ReminderReceiver.EXTRA_ID, id + 50000)
            }
            val pendingIntent = PendingIntent.getBroadcast(
                context,
                id + 50000,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            if (dueDate > System.currentTimeMillis()) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    alarmManager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, dueDate, pendingIntent)
                } else {
                    alarmManager.set(AlarmManager.RTC_WAKEUP, dueDate, pendingIntent)
                }
            }
        } catch (_: Exception) {}
    }
}
