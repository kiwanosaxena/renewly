package com.example.data.repository

import com.example.data.model.ChargeRecord
import com.example.data.model.Subscription
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.util.Locale

data class WorthASecondLookItem(
  val subscription: Subscription,
  val reason: String
)

data class WorthASecondLookSummary(
  val totalAmount: Double,
  val items: List<WorthASecondLookItem>
)

data class CategoryShare(
  val category: String,
  val total: Double,
  val sharePercentage: Double // 0.0 to 1.0
)

data class MonthHistoryBarData(
  val yearMonth: String, // YYYY-MM
  val displayMonth: String, // "Sep"
  val amount: Double,
  val isCurrentMonth: Boolean
)

object RenewlyCalculations {
  private val monthShortFormatter = DateTimeFormatter.ofPattern("MMM", Locale.US)
  private val fullMonthFormatter = DateTimeFormatter.ofPattern("MMMM yyyy", Locale.US)
  private val isoDateFormatter = DateTimeFormatter.ISO_LOCAL_DATE

  fun formatCurrency(amount: Double, currency: String): String {
    val symbols = DecimalFormatSymbols(Locale.US)
    val df = DecimalFormat("#,##0.00", symbols)
    return "$currency${df.format(amount)}"
  }

  fun formatIntegerCurrency(amount: Double, currency: String): String {
    val symbols = DecimalFormatSymbols(Locale.US)
    val df = DecimalFormat("#,##0", symbols)
    return "$currency${df.format(amount)}"
  }

  fun normalizedMonthly(sub: Subscription): Double {
    return if (sub.cycle.equals("yearly", ignoreCase = true)) {
      sub.amount / 12.0
    } else {
      sub.amount
    }
  }

  fun activeSubscriptions(subs: List<Subscription>): List<Subscription> {
    return subs.filter { !it.paused && it.cancelledAt == null }
  }

  fun monthlyTotal(subs: List<Subscription>): Double {
    return activeSubscriptions(subs).sumOf { normalizedMonthly(it) }
  }

  fun annualProjection(subs: List<Subscription>): Double {
    return monthlyTotal(subs) * 12.0
  }

  fun calculateNextChargeDate(sub: Subscription, today: LocalDate = LocalDate.now()): LocalDate {
    return try {
      if (sub.cycle.equals("yearly", ignoreCase = true)) {
        val firstDate = LocalDate.parse(sub.firstCharge, isoDateFormatter)
        var candidate = LocalDate.of(today.year, firstDate.month, sub.chargeDay.coerceAtMost(firstDate.month.length(today.isLeapYear)))
        if (candidate.isBefore(today)) {
          candidate = LocalDate.of(today.year + 1, firstDate.month, sub.chargeDay.coerceAtMost(firstDate.month.length(YearMonth.of(today.year + 1, firstDate.month).isLeapYear)))
        }
        candidate
      } else {
        val maxDayThisMonth = today.lengthOfMonth()
        val dayInMonth = sub.chargeDay.coerceAtMost(maxDayThisMonth)
        val thisMonthCandidate = LocalDate.of(today.year, today.month, dayInMonth)
        if (!thisMonthCandidate.isBefore(today)) {
          thisMonthCandidate
        } else {
          val nextMonth = today.plusMonths(1)
          val nextDay = sub.chargeDay.coerceAtMost(nextMonth.lengthOfMonth())
          LocalDate.of(nextMonth.year, nextMonth.month, nextDay)
        }
      }
    } catch (e: Exception) {
      today
    }
  }

  fun formatRelativeTiming(date: LocalDate, today: LocalDate = LocalDate.now()): String {
    val days = java.time.temporal.ChronoUnit.DAYS.between(today, date).toInt()
    return when {
      days == 0 -> "today"
      days == 1 -> "tomorrow"
      days > 1 -> "in $days days"
      else -> "today"
    }
  }

  fun formatRelativeTimingTitle(date: LocalDate, today: LocalDate = LocalDate.now()): String {
    val days = java.time.temporal.ChronoUnit.DAYS.between(today, date).toInt()
    return when {
      days == 0 -> "today"
      days == 1 -> "tomorrow"
      days > 1 -> "in $days days"
      else -> "today"
    }
  }

  fun categoryShares(subs: List<Subscription>): List<CategoryShare> {
    val active = activeSubscriptions(subs)
    val total = active.sumOf { normalizedMonthly(it) }
    if (total <= 0.0) return emptyList()

    return active.groupBy { it.category }
      .map { (cat, list) ->
        val catTotal = list.sumOf { normalizedMonthly(it) }
        CategoryShare(
          category = cat,
          total = catTotal,
          sharePercentage = catTotal / total
        )
      }
      .sortedByDescending { it.total }
  }

  // §6.3 Worth a second look rule:
  // An entry appears ONLY if:
  // 1. barelyUsed == true -> "You marked this as barely used."
  // 2. Duplicate amount within a category -> "{n} {Category} subscriptions at {amount}."
  // Headline sum is exact total of listed items. If no items match, does not render!
  fun computeWorthASecondLook(subs: List<Subscription>, currency: String): WorthASecondLookSummary? {
    val active = activeSubscriptions(subs)
    val matched = mutableListOf<WorthASecondLookItem>()
    val matchedIds = mutableSetOf<String>()

    // 1. Barely used
    for (s in active) {
      if (s.barelyUsed) {
        matched.add(WorthASecondLookItem(s, "You marked this as barely used."))
        matchedIds.add(s.id)
      }
    }

    // 2. Duplicate amount within category
    val grouped = active.groupBy { Pair(it.category.lowercase(), Math.round(it.amount * 100)) }
    for ((_, groupSubs) in grouped) {
      if (groupSubs.size >= 2) {
        val formattedAmt = formatCurrency(groupSubs[0].amount, currency)
        val reason = "${groupSubs.size} ${groupSubs[0].category} subscriptions at $formattedAmt."
        for (s in groupSubs) {
          if (!matchedIds.contains(s.id)) {
            matched.add(WorthASecondLookItem(s, reason))
            matchedIds.add(s.id)
          }
        }
      }
    }

    if (matched.isEmpty()) return null
    val totalSum = matched.sumOf { normalizedMonthly(it.subscription) }
    return WorthASecondLookSummary(totalAmount = totalSum, items = matched)
  }

  // Month-over-month: computed ONLY when recordedMonths >= 2
  fun computeMonthOverMonth(records: List<ChargeRecord>, currentMonthTotal: Double): Double? {
    if (records.isEmpty()) return null
    val distinctMonths = records.map { it.date.take(7) }.distinct()
    if (distinctMonths.size < 2) return null

    val sortedMonths = distinctMonths.sortedDescending()
    val prevMonth = sortedMonths[0] // or previous full recorded month
    val prevMonthTotal = records.filter { it.date.startsWith(prevMonth) }.sumOf { it.amount }
    return currentMonthTotal - prevMonthTotal
  }

  fun computeMonthlyHistoryBars(records: List<ChargeRecord>, today: LocalDate = LocalDate.now()): List<MonthHistoryBarData>? {
    val distinctMonths = records.map { it.date.take(7) }.distinct()
    if (distinctMonths.size < 2) return null

    val currentYm = YearMonth.now().toString()
    return distinctMonths.sorted().takeLast(6).map { ym ->
      val total = records.filter { it.date.startsWith(ym) }.sumOf { it.amount }
      val parsedYm = YearMonth.parse(ym)
      val monthName = parsedYm.month.name.take(3).lowercase().replaceFirstChar { it.uppercase() }
      MonthHistoryBarData(
        yearMonth = ym,
        displayMonth = monthName,
        amount = total,
        isCurrentMonth = (ym == currentYm)
      )
    }
  }
}
