package com.baha.sushigarden.features.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.foundation.layout.size
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.baha.sushigarden.navigation.Screen
import com.baha.sushigarden.ui.designsystem.Spacing
import com.baha.sushigarden.ui.designsystem.SushiColors

@Composable
fun ProfileScreen(
    navController: NavController,
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val user by viewModel.user.collectAsState()
    val orders by viewModel.orders.collectAsState()

    LaunchedEffect(user) {
        if (user == null) {
            navController.navigate(Screen.Auth.route) {
                popUpTo(0) { inclusive = true }
            }
        }
    }

    LazyColumn(
        Modifier
            .fillMaxSize()
            .background(SushiColors.Background)
            .padding(Spacing.md)
    ) {
        item {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    Modifier
                        .size(64.dp)
                        .clip(CircleShape)
                        .background(SushiColors.CardSurface),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        user?.name?.firstOrNull()?.toString() ?: "?",
                        color = SushiColors.PrimaryText,
                        fontSize = 19.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                Spacer(Modifier.width(Spacing.md))
                Column {
                    Text(
                        user?.name ?: "",
                        color = SushiColors.PrimaryText,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.testTag("profile_name")
                    )
                    Text(
                        user?.email ?: "",
                        color = SushiColors.SecondaryText,
                        modifier = Modifier.testTag("profile_email")
                    )
                }
            }
            Spacer(Modifier.height(Spacing.md))
            val phone = user?.phone.orEmpty()
            if (phone.isNotEmpty()) {
                Text(
                    phone,
                    color = SushiColors.SecondaryText,
                    modifier = Modifier.testTag("profile_phone")
                )
                Spacer(Modifier.height(Spacing.md))
            }
            Text(
                "Мои заказы",
                color = SushiColors.PrimaryText,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )
            Spacer(Modifier.height(Spacing.sm))
        }
        items(orders) { order ->
            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(vertical = Spacing.xs)
                    .testTag("profile_order_${order.id}"),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Заказ #${order.id.take(8)}", color = SushiColors.PrimaryText)
                Text("${order.total}₽", color = SushiColors.AccentRed)
            }
        }
        item {
            Spacer(Modifier.height(Spacing.lg))
            Button(
                onClick = viewModel::logout,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("btn_logout"),
                colors = ButtonDefaults.buttonColors(containerColor = SushiColors.CardSurface)
            ) {
                Text("Выйти", color = SushiColors.AccentRed)
            }
        }
    }
}
