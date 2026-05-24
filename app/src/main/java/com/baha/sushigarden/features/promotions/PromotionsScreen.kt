package com.baha.sushigarden.features.promotions

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.baha.sushigarden.ui.designsystem.Spacing
import com.baha.sushigarden.ui.designsystem.SushiColors

@Composable
fun PromotionsScreen(viewModel: PromotionsViewModel = hiltViewModel()) {
    val promos by viewModel.promos.collectAsState()

    Column(
        Modifier
            .fillMaxSize()
            .background(SushiColors.Background)
            .padding(Spacing.md)
    ) {
        Text(
            "Акции",
            color = SushiColors.PrimaryText,
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(Modifier.height(Spacing.md))
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(Spacing.sm),
            modifier = Modifier.testTag("promos_list")
        ) {
            items(promos) { promo ->
                Surface(
                    shape = RoundedCornerShape(Spacing.cardCorner),
                    color = SushiColors.CardSurface,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("promo_${promo.id}")
                ) {
                    Column(Modifier.padding(Spacing.md)) {
                        Text(
                            promo.title,
                            color = SushiColors.PrimaryText,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )
                        Spacer(Modifier.height(Spacing.xs))
                        Text(
                            promo.subtitle,
                            color = SushiColors.SecondaryText,
                            fontSize = 14.sp
                        )
                    }
                }
            }
        }
    }
}
