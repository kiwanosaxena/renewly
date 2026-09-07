package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.model.Subscription
import com.example.data.repository.RenewlyCalculations
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.time.LocalDate

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  @Test
  fun readStringFromContext() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("Renewly", appName)
  }

  @Test
  fun testNormalizedMonthlyCalculations() {
    val monthlySub = Subscription(
      id = "1",
      name = "Netflix",
      amount = 15.99,
      cycle = "monthly",
      chargeDay = 10,
      firstCharge = "2026-01-10",
      category = "Streaming",
      createdAt = "2026-01-10"
    )
    val yearlySub = Subscription(
      id = "2",
      name = "Amazon Prime",
      amount = 120.00,
      cycle = "yearly",
      chargeDay = 15,
      firstCharge = "2026-02-15",
      category = "Streaming",
      createdAt = "2026-02-15"
    )

    assertEquals(15.99, RenewlyCalculations.normalizedMonthly(monthlySub), 0.001)
    assertEquals(10.00, RenewlyCalculations.normalizedMonthly(yearlySub), 0.001)

    val subs = listOf(monthlySub, yearlySub)
    val total = RenewlyCalculations.monthlyTotal(subs)
    assertEquals(25.99, total, 0.001)
    assertEquals(25.99 * 12.0, RenewlyCalculations.annualProjection(subs), 0.001)
  }

  @Test
  fun testWorthASecondLookRule() {
    // Condition 1: barely used
    val s1 = Subscription(
      id = "1",
      name = "Gym",
      amount = 50.00,
      cycle = "monthly",
      chargeDay = 1,
      firstCharge = "2026-01-01",
      category = "Fitness",
      barelyUsed = true,
      createdAt = "2026-01-01"
    )
    val result1 = RenewlyCalculations.computeWorthASecondLook(listOf(s1), "$")
    assertNotNull(result1)
    assertEquals(50.00, result1!!.totalAmount, 0.001)
    assertEquals("You marked this as barely used.", result1.items[0].reason)

    // Condition 2: duplicate amount in category
    val m1 = Subscription(
      id = "2",
      name = "Spotify",
      amount = 10.99,
      cycle = "monthly",
      chargeDay = 5,
      firstCharge = "2026-01-05",
      category = "Music",
      createdAt = "2026-01-05"
    )
    val m2 = Subscription(
      id = "3",
      name = "Apple Music",
      amount = 10.99,
      cycle = "monthly",
      chargeDay = 12,
      firstCharge = "2026-01-12",
      category = "Music",
      createdAt = "2026-01-12"
    )
    val result2 = RenewlyCalculations.computeWorthASecondLook(listOf(m1, m2), "$")
    assertNotNull(result2)
    assertEquals(21.98, result2!!.totalAmount, 0.001)
    assertEquals("2 Music subscriptions at $10.99.", result2.items[0].reason)

    // Neither matches: returns null
    val resultNone = RenewlyCalculations.computeWorthASecondLook(listOf(m1), "$")
    assertNull(resultNone)
  }
}
