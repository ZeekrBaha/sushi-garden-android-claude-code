package com.baha.sushigarden.ui.designsystem

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight

private val SushiColorScheme = darkColorScheme(
    background   = SushiColors.Background,
    surface      = SushiColors.CardSurface,
    primary      = SushiColors.AccentRed,
    onPrimary    = SushiColors.PrimaryText,
    onBackground = SushiColors.PrimaryText,
    onSurface    = SushiColors.PrimaryText,
    secondary    = SushiColors.SecondaryText,
    onSecondary  = SushiColors.PrimaryText
)

private val SushiTypography = Typography(
    displayLarge  = TextStyle(fontFamily = SenFontFamily, fontWeight = FontWeight.Bold),
    displayMedium = TextStyle(fontFamily = SenFontFamily, fontWeight = FontWeight.Bold),
    displaySmall  = TextStyle(fontFamily = SenFontFamily, fontWeight = FontWeight.Bold),
    headlineLarge  = TextStyle(fontFamily = SenFontFamily, fontWeight = FontWeight.Bold),
    headlineMedium = TextStyle(fontFamily = SenFontFamily, fontWeight = FontWeight.Bold),
    headlineSmall  = TextStyle(fontFamily = SenFontFamily, fontWeight = FontWeight.Bold),
    titleLarge  = TextStyle(fontFamily = SenFontFamily, fontWeight = FontWeight.Bold),
    titleMedium = TextStyle(fontFamily = SenFontFamily, fontWeight = FontWeight.Bold),
    titleSmall  = TextStyle(fontFamily = SenFontFamily, fontWeight = FontWeight.Bold),
    bodyLarge   = TextStyle(fontFamily = SenFontFamily),
    bodyMedium  = TextStyle(fontFamily = SenFontFamily),
    bodySmall   = TextStyle(fontFamily = SenFontFamily),
    labelLarge  = TextStyle(fontFamily = SenFontFamily, fontWeight = FontWeight.Bold),
    labelMedium = TextStyle(fontFamily = SenFontFamily),
    labelSmall  = TextStyle(fontFamily = SenFontFamily),
)

@Composable
fun SushiGardenTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = SushiColorScheme,
        typography  = SushiTypography,
        content     = content
    )
}
