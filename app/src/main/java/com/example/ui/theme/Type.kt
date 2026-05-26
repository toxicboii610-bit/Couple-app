package com.example.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

val HandwrittenFont = FontFamily.Cursive
val ArtisticSerifFont = FontFamily.Serif
val CleanSansFont = FontFamily.SansSerif
val StarNodeFont = FontFamily.Monospace

val Typography = Typography(
    displayLarge = TextStyle(
        fontFamily = ArtisticSerifFont,
        fontWeight = FontWeight.Light,
        fontSize = 32.sp,
        letterSpacing = 1.sp
    ),
    displayMedium = TextStyle(
        fontFamily = ArtisticSerifFont,
        fontWeight = FontWeight.Normal,
        fontSize = 26.sp,
        letterSpacing = 0.5.sp
    ),
    headlineMedium = TextStyle(
        fontFamily = ArtisticSerifFont,
        fontWeight = FontWeight.SemiBold,
        fontSize = 20.sp
    ),
    titleLarge = TextStyle(
        fontFamily = CleanSansFont,
        fontWeight = FontWeight.Bold,
        fontSize = 18.sp,
        letterSpacing = 0.5.sp
    ),
    bodyLarge = TextStyle(
        fontFamily = CleanSansFont,
        fontWeight = FontWeight.Normal,
        fontSize = 15.sp,
        lineHeight = 22.sp
    ),
    bodyMedium = TextStyle(
        fontFamily = CleanSansFont,
        fontWeight = FontWeight.Light,
        fontSize = 13.sp,
        lineHeight = 18.sp
    ),
    labelMedium = TextStyle(
        fontFamily = StarNodeFont,
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp,
        letterSpacing = 1.sp
    )
)
