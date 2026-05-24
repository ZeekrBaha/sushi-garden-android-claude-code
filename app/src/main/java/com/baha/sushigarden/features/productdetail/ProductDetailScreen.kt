package com.baha.sushigarden.features.productdetail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.baha.sushigarden.ui.designsystem.Spacing
import com.baha.sushigarden.ui.designsystem.SushiColors

@Composable
fun ProductDetailScreen(
    productId: String,
    navController: NavController,
    viewModel: ProductDetailViewModel = hiltViewModel()
) {
    LaunchedEffect(productId) { viewModel.loadProduct(productId) }
    val product by viewModel.product.collectAsState()
    val qty by viewModel.quantity.collectAsState()
    val selectedAddOns by viewModel.selectedAddOns.collectAsState()

    product?.let { p ->
        Column(
            Modifier
                .fillMaxSize()
                .background(SushiColors.Background)
        ) {
            IconButton(
                onClick = { navController.popBackStack() },
                modifier = Modifier.testTag("btn_back")
            ) {
                Icon(Icons.Default.ArrowBack, "Назад", tint = SushiColors.PrimaryText)
            }

            AsyncImage(
                model = p.imageRes,
                contentDescription = p.name,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(240.dp)
                    .clip(
                        RoundedCornerShape(
                            bottomStart = Spacing.cardCorner,
                            bottomEnd = Spacing.cardCorner
                        )
                    )
            )

            Column(Modifier.padding(Spacing.md)) {
                Text(
                    p.name,
                    color = SushiColors.PrimaryText,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold
                )
                Text("${p.weightGrams}г", color = SushiColors.SecondaryText, fontSize = 14.sp)

                Spacer(Modifier.height(Spacing.md))

                if (p.availableAddOns.isNotEmpty()) {
                    Text("Добавки", color = SushiColors.SecondaryText, fontSize = 14.sp)
                    p.availableAddOns.forEach { addOn ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.testTag("addon_${addOn.id}")
                        ) {
                            Checkbox(
                                checked = selectedAddOns.contains(addOn.id),
                                onCheckedChange = { viewModel.toggleAddOn(addOn.id) },
                                colors = CheckboxDefaults.colors(checkedColor = SushiColors.AccentRed)
                            )
                            Text("${addOn.name} +${addOn.price}₽", color = SushiColors.PrimaryText)
                        }
                    }
                }

                Spacer(Modifier.height(Spacing.md))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Button(
                        onClick = viewModel::decrement,
                        modifier = Modifier
                            .size(44.dp)
                            .testTag("btn_decrement"),
                        contentPadding = androidx.compose.foundation.layout.PaddingValues(0.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = SushiColors.CardSurface)
                    ) {
                        Text("−", color = SushiColors.PrimaryText, fontSize = 20.sp)
                    }
                    Text(
                        "$qty",
                        color = SushiColors.PrimaryText,
                        fontSize = 14.sp,
                        modifier = Modifier
                            .padding(horizontal = 16.dp)
                            .testTag("qty_label")
                    )
                    Button(
                        onClick = viewModel::increment,
                        modifier = Modifier
                            .size(44.dp)
                            .testTag("btn_increment"),
                        contentPadding = androidx.compose.foundation.layout.PaddingValues(0.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = SushiColors.AccentRed)
                    ) {
                        Text("+", color = SushiColors.PrimaryText, fontSize = 20.sp)
                    }
                }

                Spacer(Modifier.height(Spacing.lg))

                Button(
                    onClick = {
                        viewModel.addToCart()
                        navController.popBackStack()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .testTag("btn_add_to_cart"),
                    colors = ButtonDefaults.buttonColors(containerColor = SushiColors.AccentRed)
                ) {
                    Text("В корзину — ${p.price}₽", color = SushiColors.PrimaryText)
                }
            }
        }
    }
}
