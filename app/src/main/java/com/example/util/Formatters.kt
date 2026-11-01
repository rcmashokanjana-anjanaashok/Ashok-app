package com.example.util

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.abs

object Formatters {

    private val indianDateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH)
    private val friendlyDateFormat = SimpleDateFormat("EEE, dd MMM yyyy", Locale.ENGLISH)
    private val monthYearFormat = SimpleDateFormat("MMMM yyyy", Locale.ENGLISH)
    private val monthKeyFormat = SimpleDateFormat("MM-yyyy", Locale.ENGLISH)
    private val timeFormat = SimpleDateFormat("hh:mm a", Locale.ENGLISH)

    fun formatCurrency(amount: Double, includeDecimals: Boolean = true): String {
        val isNegative = amount < 0
        val absVal = abs(amount)
        val formattedFloat = String.format(Locale.US, "%.2f", absVal)
        val parts = formattedFloat.split(".")
        val intPart = parts[0]
        val decPart = if (parts.size > 1) parts[1] else "00"

        val formattedInt = if (intPart.length > 3) {
            val lastThree = intPart.substring(intPart.length - 3)
            val rest = intPart.substring(0, intPart.length - 3)
            val restFormatted = rest.reversed().chunked(2).joinToString(",").reversed()
            "$restFormatted,$lastThree"
        } else {
            intPart
        }

        val sign = if (isNegative) "-" else ""
        return if (includeDecimals) {
            "${sign}₹$formattedInt.$decPart"
        } else {
            "${sign}₹$formattedInt"
        }
    }

    fun formatDate(timestamp: Long): String {
        return indianDateFormat.format(Date(timestamp))
    }

    fun formatFriendlyDate(timestamp: Long): String {
        return friendlyDateFormat.format(Date(timestamp))
    }

    fun formatMonthYear(timestamp: Long): String {
        return monthYearFormat.format(Date(timestamp))
    }

    fun getMonthKey(timestamp: Long): String {
        return monthKeyFormat.format(Date(timestamp))
    }

    fun formatTime(timestamp: Long): String {
        return timeFormat.format(Date(timestamp))
    }
}
