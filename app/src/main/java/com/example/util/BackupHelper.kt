package com.example.util

import com.example.data.model.Bill
import com.example.data.model.Budget
import com.example.data.model.Expense
import com.example.data.model.Income
import com.example.data.model.LoanRecord
import com.example.data.model.PersonalNote
import com.example.data.model.SavingsGoal
import com.example.data.model.TaskItem
import org.json.JSONArray
import org.json.JSONObject

data class BackupData(
    val expenses: List<Expense>,
    val incomes: List<Income>,
    val budgets: List<Budget>,
    val bills: List<Bill>,
    val tasks: List<TaskItem>,
    val notes: List<PersonalNote>,
    val goals: List<SavingsGoal>,
    val loans: List<LoanRecord>
)

object BackupHelper {

    fun createBackupJson(
        expenses: List<Expense>,
        incomes: List<Income>,
        budgets: List<Budget>,
        bills: List<Bill>,
        tasks: List<TaskItem>,
        notes: List<PersonalNote>,
        goals: List<SavingsGoal>,
        loans: List<LoanRecord>
    ): String {
        val root = JSONObject()
        root.put("app", "DailyLife")
        root.put("version", 1)
        root.put("backupDate", System.currentTimeMillis())

        val expArr = JSONArray()
        for (e in expenses) {
            val obj = JSONObject().apply {
                put("id", e.id)
                put("amount", e.amount)
                put("category", e.category)
                put("paymentMethod", e.paymentMethod)
                put("timestamp", e.timestamp)
                put("description", e.description)
                put("photoUri", e.photoUri ?: "")
                put("tags", e.tags)
            }
            expArr.put(obj)
        }
        root.put("expenses", expArr)

        val incArr = JSONArray()
        for (i in incomes) {
            val obj = JSONObject().apply {
                put("id", i.id)
                put("amount", i.amount)
                put("category", i.category)
                put("paymentMethod", i.paymentMethod)
                put("timestamp", i.timestamp)
                put("description", i.description)
            }
            incArr.put(obj)
        }
        root.put("incomes", incArr)

        val budArr = JSONArray()
        for (b in budgets) {
            val obj = JSONObject().apply {
                put("id", b.id)
                put("monthYear", b.monthYear)
                put("category", b.category)
                put("amount", b.amount)
            }
            budArr.put(obj)
        }
        root.put("budgets", budArr)

        val billArr = JSONArray()
        for (b in bills) {
            val obj = JSONObject().apply {
                put("id", b.id)
                put("title", b.title)
                put("category", b.category)
                put("amount", b.amount)
                put("dueDate", b.dueDate)
                put("isPaid", b.isPaid)
                put("frequency", b.frequency)
                put("paidDate", b.paidDate ?: 0L)
                put("reminderEnabled", b.reminderEnabled)
                put("notes", b.notes)
            }
            billArr.put(obj)
        }
        root.put("bills", billArr)

        val taskArr = JSONArray()
        for (t in tasks) {
            val obj = JSONObject().apply {
                put("id", t.id)
                put("title", t.title)
                put("description", t.description)
                put("priority", t.priority)
                put("dueDate", t.dueDate)
                put("dueTime", t.dueTime)
                put("isCompleted", t.isCompleted)
                put("reminderEnabled", t.reminderEnabled)
                put("checklistJson", t.checklistJson)
            }
            taskArr.put(obj)
        }
        root.put("tasks", taskArr)

        val noteArr = JSONArray()
        for (n in notes) {
            val obj = JSONObject().apply {
                put("id", n.id)
                put("title", n.title)
                put("content", n.content)
                put("isPinned", n.isPinned)
                put("colorTagHex", n.colorTagHex)
                put("updatedAt", n.updatedAt)
                put("checklistItems", n.checklistItems)
            }
            noteArr.put(obj)
        }
        root.put("notes", noteArr)

        val goalArr = JSONArray()
        for (g in goals) {
            val obj = JSONObject().apply {
                put("id", g.id)
                put("title", g.title)
                put("targetAmount", g.targetAmount)
                put("currentAmount", g.currentAmount)
                put("targetDate", g.targetDate)
                put("note", g.note)
                put("colorHex", g.colorHex)
            }
            goalArr.put(obj)
        }
        root.put("goals", goalArr)

        val loanArr = JSONArray()
        for (l in loans) {
            val obj = JSONObject().apply {
                put("id", l.id)
                put("personName", l.personName)
                put("phoneNumber", l.phoneNumber)
                put("type", l.type)
                put("originalAmount", l.originalAmount)
                put("paidAmount", l.paidAmount)
                put("transactionDate", l.transactionDate)
                put("dueDate", l.dueDate)
                put("notes", l.notes)
                put("isSettled", l.isSettled)
            }
            loanArr.put(obj)
        }
        root.put("loans", loanArr)

        return root.toString(2)
    }

    fun parseBackupJson(jsonString: String): BackupData {
        val root = JSONObject(jsonString)

        val expenses = mutableListOf<Expense>()
        val expArr = root.optJSONArray("expenses") ?: JSONArray()
        for (i in 0 until expArr.length()) {
            val obj = expArr.getJSONObject(i)
            expenses.add(
                Expense(
                    id = obj.optLong("id", 0),
                    amount = obj.optDouble("amount", 0.0),
                    category = obj.optString("category", "Other"),
                    paymentMethod = obj.optString("paymentMethod", "Cash"),
                    timestamp = obj.optLong("timestamp", System.currentTimeMillis()),
                    description = obj.optString("description", ""),
                    photoUri = obj.optString("photoUri").takeIf { it.isNotEmpty() },
                    tags = obj.optString("tags", "")
                )
            )
        }

        val incomes = mutableListOf<Income>()
        val incArr = root.optJSONArray("incomes") ?: JSONArray()
        for (i in 0 until incArr.length()) {
            val obj = incArr.getJSONObject(i)
            incomes.add(
                Income(
                    id = obj.optLong("id", 0),
                    amount = obj.optDouble("amount", 0.0),
                    category = obj.optString("category", "Other"),
                    paymentMethod = obj.optString("paymentMethod", "Bank"),
                    timestamp = obj.optLong("timestamp", System.currentTimeMillis()),
                    description = obj.optString("description", "")
                )
            )
        }

        val budgets = mutableListOf<Budget>()
        val budArr = root.optJSONArray("budgets") ?: JSONArray()
        for (i in 0 until budArr.length()) {
            val obj = budArr.getJSONObject(i)
            budgets.add(
                Budget(
                    id = obj.optLong("id", 0),
                    monthYear = obj.optString("monthYear", ""),
                    category = obj.optString("category", "ALL"),
                    amount = obj.optDouble("amount", 0.0)
                )
            )
        }

        val bills = mutableListOf<Bill>()
        val billArr = root.optJSONArray("bills") ?: JSONArray()
        for (i in 0 until billArr.length()) {
            val obj = billArr.getJSONObject(i)
            bills.add(
                Bill(
                    id = obj.optLong("id", 0),
                    title = obj.optString("title", ""),
                    category = obj.optString("category", "Other"),
                    amount = obj.optDouble("amount", 0.0),
                    dueDate = obj.optLong("dueDate", System.currentTimeMillis()),
                    isPaid = obj.optBoolean("isPaid", false),
                    frequency = obj.optString("frequency", "Monthly"),
                    paidDate = if (obj.has("paidDate")) obj.optLong("paidDate").takeIf { it > 0 } else null,
                    reminderEnabled = obj.optBoolean("reminderEnabled", true),
                    notes = obj.optString("notes", "")
                )
            )
        }

        val tasks = mutableListOf<TaskItem>()
        val taskArr = root.optJSONArray("tasks") ?: JSONArray()
        for (i in 0 until taskArr.length()) {
            val obj = taskArr.getJSONObject(i)
            tasks.add(
                TaskItem(
                    id = obj.optLong("id", 0),
                    title = obj.optString("title", ""),
                    description = obj.optString("description", ""),
                    priority = obj.optString("priority", "MEDIUM"),
                    dueDate = obj.optLong("dueDate", System.currentTimeMillis()),
                    dueTime = obj.optString("dueTime", "10:00 AM"),
                    isCompleted = obj.optBoolean("isCompleted", false),
                    reminderEnabled = obj.optBoolean("reminderEnabled", true),
                    checklistJson = obj.optString("checklistJson", "")
                )
            )
        }

        val notes = mutableListOf<PersonalNote>()
        val noteArr = root.optJSONArray("notes") ?: JSONArray()
        for (i in 0 until noteArr.length()) {
            val obj = noteArr.getJSONObject(i)
            notes.add(
                PersonalNote(
                    id = obj.optLong("id", 0),
                    title = obj.optString("title", ""),
                    content = obj.optString("content", ""),
                    isPinned = obj.optBoolean("isPinned", false),
                    colorTagHex = obj.optString("colorTagHex", "#FFFFFF"),
                    updatedAt = obj.optLong("updatedAt", System.currentTimeMillis()),
                    checklistItems = obj.optString("checklistItems", "")
                )
            )
        }

        val goals = mutableListOf<SavingsGoal>()
        val goalArr = root.optJSONArray("goals") ?: JSONArray()
        for (i in 0 until goalArr.length()) {
            val obj = goalArr.getJSONObject(i)
            goals.add(
                SavingsGoal(
                    id = obj.optLong("id", 0),
                    title = obj.optString("title", ""),
                    targetAmount = obj.optDouble("targetAmount", 0.0),
                    currentAmount = obj.optDouble("currentAmount", 0.0),
                    targetDate = obj.optLong("targetDate", System.currentTimeMillis()),
                    note = obj.optString("note", ""),
                    colorHex = obj.optString("colorHex", "#00695C")
                )
            )
        }

        val loans = mutableListOf<LoanRecord>()
        val loanArr = root.optJSONArray("loans") ?: JSONArray()
        for (i in 0 until loanArr.length()) {
            val obj = loanArr.getJSONObject(i)
            loans.add(
                LoanRecord(
                    id = obj.optLong("id", 0),
                    personName = obj.optString("personName", ""),
                    phoneNumber = obj.optString("phoneNumber", ""),
                    type = obj.optString("type", "LENT"),
                    originalAmount = obj.optDouble("originalAmount", 0.0),
                    paidAmount = obj.optDouble("paidAmount", 0.0),
                    transactionDate = obj.optLong("transactionDate", System.currentTimeMillis()),
                    dueDate = obj.optLong("dueDate", System.currentTimeMillis()),
                    notes = obj.optString("notes", ""),
                    isSettled = obj.optBoolean("isSettled", false)
                )
            )
        }

        return BackupData(expenses, incomes, budgets, bills, tasks, notes, goals, loans)
    }
}
