package com.baha.sushigarden.features.checkout

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.baha.sushigarden.UiState
import com.baha.sushigarden.navigation.Screen
import com.baha.sushigarden.ui.designsystem.Spacing
import com.baha.sushigarden.ui.designsystem.SushiColors

@Composable
fun CheckoutScreen(
    navController: NavController,
    viewModel: CheckoutViewModel = hiltViewModel()
) {
    val cartState by viewModel.cartState.collectAsState()
    val orderState by viewModel.orderState.collectAsState()
    val street by viewModel.street.collectAsState()
    val recipient by viewModel.recipient.collectAsState()
    val phone by viewModel.phone.collectAsState()
    val email by viewModel.email.collectAsState()

    LaunchedEffect(orderState) {
        if (orderState is UiState.Success) {
            navController.navigate(Screen.Tracking.route) {
                popUpTo(Screen.Cart.route) { inclusive = true }
            }
        }
    }

    Column(
        Modifier
            .fillMaxSize()
            .background(SushiColors.Background)
            .verticalScroll(rememberScrollState())
            .padding(Spacing.md)
    ) {
        Text(
            "Оформление заказа",
            color = SushiColors.PrimaryText,
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(Modifier.height(Spacing.md))

        checkoutField("Адрес доставки", street, { viewModel.street.value = it }, "field_street")
        checkoutField("Кому", recipient, { viewModel.recipient.value = it }, "field_recipient")
        checkoutField("Телефон", phone, { viewModel.phone.value = it }, "field_phone")
        checkoutField("Почта", email, { viewModel.email.value = it }, "field_email")

        Spacer(Modifier.height(Spacing.md))
        Surface(
            color = SushiColors.CardSurface,
            shape = RoundedCornerShape(Spacing.cardCorner)
        ) {
            Column(Modifier.padding(Spacing.md)) {
                Text("Картой онлайн", color = SushiColors.PrimaryText)
            }
        }

        Spacer(Modifier.height(Spacing.md))
        summaryRow("Сумма заказа", "${cartState.subtotal}₽")
        summaryRow("Доставка", "${cartState.deliveryFee}₽")
        summaryRow("Сервисный сбор", "${cartState.serviceFee}₽")
        HorizontalDivider(
            color = SushiColors.Divider,
            modifier = Modifier.padding(vertical = Spacing.sm)
        )
        summaryRow("Итого", "${cartState.total}₽", bold = true)

        Spacer(Modifier.height(Spacing.lg))
        Button(
            onClick = viewModel::confirm,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
                .testTag("btn_confirm"),
            colors = ButtonDefaults.buttonColors(containerColor = SushiColors.AccentRed)
        ) {
            if (orderState is UiState.Loading) {
                CircularProgressIndicator(
                    color = Color.White,
                    modifier = Modifier.size(20.dp)
                )
            } else {
                Text("Подтвердить")
            }
        }
    }
}

@Composable
private fun checkoutField(
    label: String,
    value: String,
    onChange: (String) -> Unit,
    tag: String
) {
    OutlinedTextField(
        value = value,
        onValueChange = onChange,
        label = { Text(label) },
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = Spacing.sm)
            .testTag(tag),
        colors = OutlinedTextFieldDefaults.colors(
            focusedTextColor = SushiColors.PrimaryText,
            unfocusedTextColor = SushiColors.PrimaryText,
            focusedBorderColor = SushiColors.AccentRed,
            unfocusedBorderColor = SushiColors.SecondaryText,
            focusedLabelColor = SushiColors.AccentRed,
            unfocusedLabelColor = SushiColors.SecondaryText
        )
    )
}

@Composable
private fun summaryRow(label: String, value: String, bold: Boolean = false) {
    Row(
        Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, color = SushiColors.SecondaryText)
        Text(
            value,
            color = SushiColors.PrimaryText,
            fontWeight = if (bold) FontWeight.Bold else FontWeight.Normal
        )
    }
}
