package com.example.ui.theme

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp

@Immutable
data class RenewlyTypography(
  val heroAmount: TextStyle,
  val detailAmount: TextStyle,
  val screenHeadline: TextStyle,
  val onboardingHeadline: TextStyle,
  val insightsAnnualFigure: TextStyle,
  val detailServiceName: TextStyle,
  val heroSecondaryAmount: TextStyle,
  val cardTitle: TextStyle,
  val sectionHeader: TextStyle,
  val listRowName: TextStyle,
  val listRowAmount: TextStyle,
  val body: TextStyle,
  val bodyEmphasis: TextStyle,
  val bodyLarge: TextStyle,
  val secondarySupporting: TextStyle,
  val rowSubtitle: TextStyle,
  val fieldLabel: TextStyle,
  val eyebrow: TextStyle,
  val microLabel: TextStyle,
  val calendarWeekdayHeader: TextStyle,
  val calendarDayNumber: TextStyle,
  val calendarDayNumberSelected: TextStyle,
  val buttonLarge: TextStyle,
  val buttonMedium: TextStyle,
  val buttonSmall: TextStyle,
  val tabLabel: TextStyle,
  val chipLabel: TextStyle
)

val baseFontFamily = FontFamily.SansSerif

val RenewlyType = RenewlyTypography(
  heroAmount = TextStyle(
    fontFamily = baseFontFamily,
    fontSize = 50.sp,
    fontWeight = FontWeight(700),
    letterSpacing = (-0.04).em,
    lineHeight = 50.sp
  ),
  detailAmount = TextStyle(
    fontFamily = baseFontFamily,
    fontSize = 44.sp,
    fontWeight = FontWeight(700),
    letterSpacing = (-0.04).em,
    lineHeight = 44.sp
  ),
  screenHeadline = TextStyle(
    fontFamily = baseFontFamily,
    fontSize = 24.sp,
    fontWeight = FontWeight(700),
    letterSpacing = (-0.03).em,
    lineHeight = (24 * 1.20).sp
  ),
  onboardingHeadline = TextStyle(
    fontFamily = baseFontFamily,
    fontSize = 40.sp,
    fontWeight = FontWeight(700),
    letterSpacing = (-0.03).em,
    lineHeight = (40 * 1.08).sp
  ),
  insightsAnnualFigure = TextStyle(
    fontFamily = baseFontFamily,
    fontSize = 40.sp,
    fontWeight = FontWeight(700),
    letterSpacing = (-0.04).em,
    lineHeight = 40.sp
  ),
  detailServiceName = TextStyle(
    fontFamily = baseFontFamily,
    fontSize = 24.sp,
    fontWeight = FontWeight(700),
    letterSpacing = (-0.03).em,
    lineHeight = (24 * 1.15).sp
  ),
  heroSecondaryAmount = TextStyle(
    fontFamily = baseFontFamily,
    fontSize = 28.sp,
    fontWeight = FontWeight(700),
    letterSpacing = (-0.03).em,
    lineHeight = 28.sp
  ),
  cardTitle = TextStyle(
    fontFamily = baseFontFamily,
    fontSize = 13.5.sp,
    fontWeight = FontWeight(700),
    letterSpacing = (-0.01).em,
    lineHeight = (13.5 * 1.3).sp
  ),
  sectionHeader = TextStyle(
    fontFamily = baseFontFamily,
    fontSize = 20.sp,
    fontWeight = FontWeight(700),
    letterSpacing = (-0.02).em,
    lineHeight = (20 * 1.25).sp
  ),
  listRowName = TextStyle(
    fontFamily = baseFontFamily,
    fontSize = 15.sp,
    fontWeight = FontWeight(600),
    letterSpacing = (-0.01).em,
    lineHeight = (15 * 1.3).sp
  ),
  listRowAmount = TextStyle(
    fontFamily = baseFontFamily,
    fontSize = 15.5.sp,
    fontWeight = FontWeight(700),
    letterSpacing = (-0.02).em,
    lineHeight = (15.5 * 1.3).sp
  ),
  body = TextStyle(
    fontFamily = baseFontFamily,
    fontSize = 14.sp,
    fontWeight = FontWeight(500),
    letterSpacing = 0.em,
    lineHeight = (14 * 1.50).sp
  ),
  bodyEmphasis = TextStyle(
    fontFamily = baseFontFamily,
    fontSize = 14.sp,
    fontWeight = FontWeight(600),
    letterSpacing = 0.em,
    lineHeight = (14 * 1.50).sp
  ),
  bodyLarge = TextStyle(
    fontFamily = baseFontFamily,
    fontSize = 14.5.sp,
    fontWeight = FontWeight(500),
    letterSpacing = 0.em,
    lineHeight = (14.5 * 1.55).sp
  ),
  secondarySupporting = TextStyle(
    fontFamily = baseFontFamily,
    fontSize = 13.sp,
    fontWeight = FontWeight(500),
    letterSpacing = 0.em,
    lineHeight = (13 * 1.50).sp
  ),
  rowSubtitle = TextStyle(
    fontFamily = baseFontFamily,
    fontSize = 12.sp,
    fontWeight = FontWeight(500),
    letterSpacing = 0.em,
    lineHeight = (12 * 1.40).sp
  ),
  fieldLabel = TextStyle(
    fontFamily = baseFontFamily,
    fontSize = 11.sp,
    fontWeight = FontWeight(600),
    letterSpacing = 0.08.em,
    lineHeight = (11 * 1.2).sp
  ),
  eyebrow = TextStyle(
    fontFamily = baseFontFamily,
    fontSize = 11.sp,
    fontWeight = FontWeight(600),
    letterSpacing = 0.09.em,
    lineHeight = (11 * 1.2).sp
  ),
  microLabel = TextStyle(
    fontFamily = baseFontFamily,
    fontSize = 10.5.sp,
    fontWeight = FontWeight(600),
    letterSpacing = 0.08.em,
    lineHeight = (10.5 * 1.2).sp
  ),
  calendarWeekdayHeader = TextStyle(
    fontFamily = baseFontFamily,
    fontSize = 10.sp,
    fontWeight = FontWeight(600),
    letterSpacing = 0.06.em,
    lineHeight = (10 * 1.2).sp
  ),
  calendarDayNumber = TextStyle(
    fontFamily = baseFontFamily,
    fontSize = 13.sp,
    fontWeight = FontWeight(500),
    letterSpacing = 0.em,
    lineHeight = 13.sp
  ),
  calendarDayNumberSelected = TextStyle(
    fontFamily = baseFontFamily,
    fontSize = 13.sp,
    fontWeight = FontWeight(700),
    letterSpacing = 0.em,
    lineHeight = 13.sp
  ),
  buttonLarge = TextStyle(
    fontFamily = baseFontFamily,
    fontSize = 15.sp,
    fontWeight = FontWeight(600),
    letterSpacing = 0.em,
    lineHeight = 15.sp
  ),
  buttonMedium = TextStyle(
    fontFamily = baseFontFamily,
    fontSize = 14.sp,
    fontWeight = FontWeight(600),
    letterSpacing = 0.em,
    lineHeight = 14.sp
  ),
  buttonSmall = TextStyle(
    fontFamily = baseFontFamily,
    fontSize = 13.sp,
    fontWeight = FontWeight(600),
    letterSpacing = 0.em,
    lineHeight = 13.sp
  ),
  tabLabel = TextStyle(
    fontFamily = baseFontFamily,
    fontSize = 10.5.sp,
    fontWeight = FontWeight(600),
    letterSpacing = 0.em,
    lineHeight = 10.5.sp
  ),
  chipLabel = TextStyle(
    fontFamily = baseFontFamily,
    fontSize = 13.sp,
    fontWeight = FontWeight(600),
    letterSpacing = 0.em,
    lineHeight = 13.sp
  )
)

val LocalRenewlyTypography = staticCompositionLocalOf { RenewlyType }
