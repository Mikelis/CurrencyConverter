package lv.mikelis.currencyconverter.common

import java.text.NumberFormat
import java.util.Currency
import java.util.Locale

fun Double.toCurrency(currencyCode: String): String {
    val formatter = NumberFormat.getCurrencyInstance(Locale.getDefault())
    return try {
        formatter.currency = Currency.getInstance(currencyCode)
        formatter.format(this)
    } catch (_: IllegalArgumentException) {
        this.toString()
    }

}

fun String.toCurrencyFullName(): String {
    return try {
        val currency = Currency.getInstance(this)
        currency.displayName
    } catch (_: IllegalArgumentException) {
        this
    }
}

fun Double.toCurrencyWithoutSymbol(currencyCode: String): String {
    val formatter = NumberFormat.getCurrencyInstance(Locale.getDefault()).apply {
        // Remove the currency symbol
        maximumFractionDigits = 2 // Set to 2 decimal places
        minimumFractionDigits = 2
    }
    return try {
        formatter.currency = Currency.getInstance(currencyCode)
        formatter.format(this).replace(Regex("[^\\d.,-]"), "")
    } catch (_: IllegalArgumentException) {
        this.toString()
    }
}