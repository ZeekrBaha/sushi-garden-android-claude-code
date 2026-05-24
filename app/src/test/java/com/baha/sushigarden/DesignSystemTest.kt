package com.baha.sushigarden

import androidx.compose.ui.graphics.Color
import com.baha.sushigarden.ui.designsystem.SushiColors
import org.junit.Test
import org.junit.Assert.assertEquals

class DesignSystemTest {
    @Test fun backgroundColorToken() {
        assertEquals(Color(0xFF0F0F11), SushiColors.Background)
    }
    @Test fun accentRedToken() {
        assertEquals(Color(0xFFEC1A35), SushiColors.AccentRed)
    }
    @Test fun cardSurfaceToken() {
        assertEquals(Color(0xFF292830), SushiColors.CardSurface)
    }
}
