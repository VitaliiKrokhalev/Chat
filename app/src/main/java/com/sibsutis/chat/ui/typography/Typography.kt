package com.sibsutis.chat.ui.typography

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.sibsutis.chat.R

private val kanitLight = FontFamily(
    Font(R.font.kanit_light)
)

private val kanitMedium = FontFamily(
    Font(R.font.kanit_medium)
)

private val kanitRegular = FontFamily(
    Font(R.font.kanit_regular)
)

fun appTypography() = Typography(
    displayLarge = TextStyle(
        fontFamily = kanitRegular,
        fontWeight = FontWeight.Normal,
        fontStyle = FontStyle.Normal,
        fontSize = 57.sp,
        letterSpacing = (-0.2).sp,
        lineHeight = 64.sp
    ),
    displayMedium = TextStyle(
        fontFamily = kanitRegular,
        fontWeight = FontWeight.Normal,
        fontStyle = FontStyle.Normal,
        fontSize = 45.sp,
        letterSpacing = 0.0.sp,
        lineHeight = 52.sp
    ),
    displaySmall = TextStyle(
        fontFamily = kanitRegular,
        fontWeight = FontWeight.Normal,
        fontStyle = FontStyle.Normal,
        fontSize = 36.sp,
        letterSpacing = 0.0.sp,
        lineHeight = 44.sp
    ),
    headlineLarge = TextStyle(
        fontFamily = kanitLight,
        fontWeight = FontWeight.Light,
        fontStyle = FontStyle.Normal,
        fontSize = 32.sp,
        letterSpacing = 0.0.sp,
        lineHeight = 40.sp
    ),
    headlineMedium = TextStyle(
        fontFamily = kanitLight,
        fontWeight = FontWeight.Light,
        fontStyle = FontStyle.Normal,
        fontSize = 28.sp,
        letterSpacing = 0.0.sp,
        lineHeight = 36.sp
    ),
    headlineSmall = TextStyle(
        fontFamily = kanitLight,
        fontWeight = FontWeight.Light,
        fontStyle = FontStyle.Normal,
        fontSize = 24.sp,
        letterSpacing = 0.0.sp,
        lineHeight = 32.sp
    ),
    titleLarge = TextStyle(
        fontFamily = kanitRegular,
        fontWeight = FontWeight.Normal,
        fontStyle = FontStyle.Normal,
        fontSize = 22.sp,
        letterSpacing = 0.0.sp,
        lineHeight = 28.sp
    ),
    titleMedium = TextStyle(
        fontFamily = kanitMedium,
        fontWeight = FontWeight.Medium,
        fontStyle = FontStyle.Normal,
        fontSize = 16.sp,
        letterSpacing = 0.2.sp,
        lineHeight = 24.sp
    ),
    titleSmall = TextStyle(
        fontFamily = kanitMedium,
        fontWeight = FontWeight.Medium,
        fontStyle = FontStyle.Normal,
        fontSize = 14.sp,
        letterSpacing = 0.1.sp,
        lineHeight = 20.sp
    ),
    bodyLarge = TextStyle(
        fontFamily = kanitRegular,
        fontWeight = FontWeight.Normal,
        fontStyle = FontStyle.Normal,
        fontSize = 16.sp,
        letterSpacing = 0.5.sp,
        lineHeight = 24.sp
    ),
    bodyMedium = TextStyle(
        fontFamily = kanitRegular,
        fontWeight = FontWeight.Normal,
        fontStyle = FontStyle.Normal,
        fontSize = 14.sp,
        letterSpacing = 0.2.sp,
        lineHeight = 20.sp
    ),
    bodySmall = TextStyle(
        fontFamily = kanitRegular,
        fontWeight = FontWeight.Normal,
        fontStyle = FontStyle.Normal,
        fontSize = 12.sp,
        letterSpacing = 0.4.sp,
        lineHeight = 16.sp
    ),
    labelLarge = TextStyle(
        fontFamily = kanitMedium,
        fontWeight = FontWeight.Medium,
        fontStyle = FontStyle.Normal,
        fontSize = 14.sp,
        letterSpacing = 0.1.sp,
        lineHeight = 20.sp
    ),
    labelMedium = TextStyle(
        fontFamily = kanitMedium,
        fontWeight = FontWeight.Medium,
        fontStyle = FontStyle.Normal,
        fontSize = 12.sp,
        letterSpacing = 0.5.sp,
        lineHeight = 16.sp
    ),
    labelSmall = TextStyle(
        fontFamily = kanitMedium,
        fontWeight = FontWeight.Medium,
        fontStyle = FontStyle.Normal,
        fontSize = 11.sp,
        letterSpacing = 0.5.sp,
        lineHeight = 16.sp
    )
)

