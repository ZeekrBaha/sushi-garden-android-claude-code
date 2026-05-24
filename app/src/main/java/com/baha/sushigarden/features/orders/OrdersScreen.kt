package com.baha.sushigarden.features.orders

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.baha.sushigarden.navigation.Screen
import com.baha.sushigarden.ui.designsystem.Spacing
import com.baha.sushigarden.ui.designsystem.SushiColors
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun OrdersScreen(
    navController: NavController,
    viewModel: OrdersViewModel = hiltViewModel()
) {
    val orders by viewModel.orders.collectAsState()

    Column(
        Modifier
            .fillMaxSize()
            .background(SushiColors.Background)
            .padding(Spacing.md)
    ) {
        Text(
            "Заказы",
            color = SushiColors.PrimaryText,
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(Modifier.height(Spacing.md))

        if (orders.isEmpty()) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(
                    "Нет заказов",
                    color = SushiColors.SecondaryText,
                    modifier = Modifier.testTag("orders_empty")
                )
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(Spacing.sm),
                modifier = Modifier.testTag("orders_list")
            ) {
                items(orders) { order ->
                    Surface(
                        shape = RoundedCornerShape(Spacing.cardCorner),
                        color = SushiColors.CardSurface,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                navController.navigate(Screen.OrderDetail.createRoute(order.id))
                            }
                            .testTag("order_row_${order.id}")
                    ) {
                        Row(
                            Modifier.padding(Spacing.md),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text(
                                    "Заказ #${order.id.take(8)}",
                                    color = SushiColors.PrimaryText,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
                                        .format(Date(order.createdAt)),
                                    color = SushiColors.SecondaryText,
                                    fontSize = 12.sp
                                )
                            }
                            Text(
                                "${order.total}₽",
                                color = SushiColors.AccentRed,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}
