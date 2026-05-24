package com.baha.sushigarden.features.orders

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.baha.sushigarden.ui.designsystem.Spacing
import com.baha.sushigarden.ui.designsystem.SushiColors

@Composable
fun OrderDetailScreen(
    orderId: String,
    navController: NavController,
    viewModel: OrdersViewModel = hiltViewModel()
) {
    val orders by viewModel.orders.collectAsState()
    val order = orders.find { it.id == orderId }

    Column(
        Modifier
            .fillMaxSize()
            .background(SushiColors.Background)
            .padding(Spacing.md)
    ) {
        IconButton(onClick = { navController.popBackStack() }) {
            Icon(Icons.Default.ArrowBack, "Назад", tint = SushiColors.PrimaryText)
        }
        order?.let { o ->
            Text(
                "Заказ #${o.id.take(8)}",
                color = SushiColors.PrimaryText,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.height(Spacing.md))
            o.lines.forEach { line ->
                Row(
                    Modifier
                        .fillMaxWidth()
                        .testTag("order_line_${line.productName}"),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("${line.productName} ×${line.qty}", color = SushiColors.PrimaryText)
                    Text("${line.lineTotal}₽", color = SushiColors.PrimaryText)
                }
                Spacer(Modifier.height(Spacing.xs))
            }
            HorizontalDivider(
                color = SushiColors.Divider,
                modifier = Modifier.padding(vertical = Spacing.sm)
            )
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Итого", color = SushiColors.PrimaryText, fontWeight = FontWeight.Bold)
                Text(
                    "${o.total}₽",
                    color = SushiColors.AccentRed,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.testTag("order_total")
                )
            }
        }
    }
}
