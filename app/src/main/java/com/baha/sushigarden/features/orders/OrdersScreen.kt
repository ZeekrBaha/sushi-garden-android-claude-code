package com.baha.sushigarden.features.orders

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.baha.sushigarden.ui.designsystem.SushiColors

@Composable
fun OrdersScreen(navController: NavController) {
    Box(Modifier.fillMaxSize().background(SushiColors.Background), contentAlignment = Alignment.Center) {
        Text("Заказы — скоро", color = SushiColors.PrimaryText)
    }
}
