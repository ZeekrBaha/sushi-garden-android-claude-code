package com.baha.sushigarden.features.catalog

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
import com.baha.sushigarden.R
import com.baha.sushigarden.UiState
import com.baha.sushigarden.data.models.Product
import com.baha.sushigarden.navigation.Screen
import com.baha.sushigarden.ui.designsystem.Spacing
import com.baha.sushigarden.ui.designsystem.SushiColors

@Composable
fun CatalogScreen(
    navController: NavController,
    viewModel: CatalogViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val products by viewModel.products.collectAsState()
    val selectedCat by viewModel.selectedCategoryId.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SushiColors.Background)
    ) {
        Text(
            text = "ул. Пушкина, 10",
            color = SushiColors.PrimaryText,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .padding(Spacing.md)
                .testTag("catalog_address")
        )

        if (uiState is UiState.Success) {
            val categories = (uiState as UiState.Success).data
            Row(
                modifier = Modifier
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = Spacing.md)
                    .testTag("catalog_categories"),
                horizontalArrangement = Arrangement.spacedBy(Spacing.sm)
            ) {
                categories.forEach { cat ->
                    val selected = cat.id == selectedCat
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = if (selected) SushiColors.AccentRed else SushiColors.CardSurface,
                        modifier = Modifier
                            .clickable { viewModel.selectCategory(cat.id) }
                            .testTag("category_${cat.id}")
                    ) {
                        Text(
                            cat.name,
                            color = SushiColors.PrimaryText,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        Spacer(Modifier.height(Spacing.md))

        PromoBannerStrip()

        Spacer(Modifier.height(Spacing.md))

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            contentPadding = PaddingValues(Spacing.md),
            horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
            verticalArrangement = Arrangement.spacedBy(Spacing.sm),
            modifier = Modifier.testTag("catalog_grid")
        ) {
            items(products) { product ->
                ProductCard(product) {
                    navController.navigate(Screen.ProductDetail.createRoute(product.id))
                }
            }
        }
    }
}

@Composable
private fun PromoBannerStrip() {
    val banners = listOf(R.drawable.banner_promo_1, R.drawable.banner_promo_2)
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
        contentPadding = PaddingValues(horizontal = Spacing.md),
        modifier = Modifier
            .fillMaxWidth()
            .height(132.dp)
            .testTag("catalog_banners")
    ) {
        items(banners.size) { index ->
            AsyncImage(
                model = banners[index],
                contentDescription = "Промо ${index + 1}",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .width(296.dp)
                    .height(132.dp)
                    .clip(RoundedCornerShape(Spacing.cardCorner))
                    .testTag("catalog_banner_$index")
            )
        }
    }
}

@Composable
private fun ProductCard(product: Product, onClick: () -> Unit) {
    Surface(
        shape = RoundedCornerShape(Spacing.cardCorner),
        color = SushiColors.CardSurface,
        modifier = Modifier
            .clickable(onClick = onClick)
            .testTag("product_${product.id}")
    ) {
        Column {
            AsyncImage(
                model = product.imageRes,
                contentDescription = product.name,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp)
                    .clip(
                        RoundedCornerShape(
                            topStart = Spacing.cardCorner,
                            topEnd = Spacing.cardCorner
                        )
                    )
            )
            Column(Modifier.padding(Spacing.sm)) {
                Text(
                    product.name,
                    color = SushiColors.PrimaryText,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold
                )
                Text("${product.weightGrams}г", color = SushiColors.SecondaryText, fontSize = 12.sp)
                Text(
                    "${product.price}₽",
                    color = SushiColors.AccentRed,
                    fontSize = 19.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
