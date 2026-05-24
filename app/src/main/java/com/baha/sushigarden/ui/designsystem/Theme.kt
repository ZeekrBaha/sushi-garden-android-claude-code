package com.baha.sushigarden.ui.designsystem

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

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

@Composable
fun SushiGardenTheme(content: @Composable () -> Unit) {
    MaterialTheme(colorScheme = SushiColorScheme, content = content)
}
