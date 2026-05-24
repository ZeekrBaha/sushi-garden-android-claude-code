package com.baha.sushigarden.features.cart

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.baha.sushigarden.data.models.CartItem
import com.baha.sushigarden.navigation.Screen
import com.baha.sushigarden.ui.designsystem.Spacing
import com.baha.sushigarden.ui.designsystem.SushiColors

@Composable
fun CartScreen(
    navController: NavController,
    viewModel: CartViewModel = hiltViewModel()
) {
    val state by viewModel.cartState.collectAsState()

    Column(
        Modifier
            .fillMaxSize()
            .background(SushiColors.Background)
            .padding(Spacing.md)
    ) {
        Text(
            "Корзина",
            color = SushiColors.PrimaryText,
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.testTag("cart_title")
        )

        if (state.items.isEmpty()) {
            Box(
                Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "Корзина пуста",
                    color = SushiColors.SecondaryText,
                    modifier = Modifier.testTag("cart_empty")
                )
            }
        } else {
            LazyColumn(Modifier.weight(1f).testTag("cart_items")) {
                items(state.items) { item -> CartItemRow(item, viewModel) }
            }
            HorizontalDivider(color = SushiColors.Divider)
            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(vertical = Spacing.sm),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Итого", color = SushiColors.PrimaryText, fontWeight = FontWeight.Bold)
                Text(
                    "${state.total}₽",
                    color = SushiColors.AccentRed,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.testTag("cart_total")
                )
            }
        }

        Button(
            onClick = { navController.navigate(Screen.Checkout.route) },
            enabled = state.items.isNotEmpty(),
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
                .testTag("btn_checkout"),
            colors = ButtonDefaults.buttonColors(
                containerColor = SushiColors.AccentRed,
                disabledContainerColor = SushiColors.AccentRed.copy(alpha = 0.4f)
            )
        ) { Text("Оформить") }
    }
}

@Composable
private fun CartItemRow(item: CartItem, viewModel: CartViewModel) {
    Row(
        Modifier
            .fillMaxWidth()
            .padding(vertical = Spacing.sm)
            .testTag("cart_item_${item.product.id}"),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(Modifier.weight(1f)) {
            Text(item.product.name, color = SushiColors.PrimaryText)
            if (item.selectedAddOns.isNotEmpty()) {
                Text(
                    item.selectedAddOns.joinToString { it.name },
                    color = SushiColors.SecondaryText,
                    fontSize = 12.sp
                )
            }
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(
                onClick = { viewModel.decrement(item.product.id) },
                modifier = Modifier.testTag("cart_decrement_${item.product.id}")
            ) {
                Text("−", color = SushiColors.PrimaryText, fontSize = 20.sp)
            }
            Text(
                "${item.quantity}",
                color = SushiColors.PrimaryText,
                modifier = Modifier.testTag("cart_qty_${item.product.id}")
            )
            IconButton(
                onClick = { viewModel.increment(item.product.id) },
                modifier = Modifier.testTag("cart_increment_${item.product.id}")
            ) {
                Text("+", color = SushiColors.AccentRed, fontSize = 20.sp)
            }
        }
        Text("${item.lineTotal}₽", color = SushiColors.PrimaryText, fontWeight = FontWeight.Bold)
    }
}
