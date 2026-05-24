package com.baha.sushigarden.features.tracking

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.baha.sushigarden.ui.designsystem.SushiColors
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState

@Composable
fun TrackingScreen(
    navController: NavController,
    viewModel: TrackingViewModel = hiltViewModel()
) {
    val position by viewModel.position.collectAsState()
    val progress by viewModel.progress.collectAsState()
    val eta by viewModel.etaMinutes.collectAsState()

    val cameraState = rememberCameraPositionState {
        this.position = CameraPosition.fromLatLngZoom(position, 15f)
    }

    Column(
        Modifier
            .fillMaxSize()
            .background(SushiColors.Background)
    ) {
        Row(
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = { navController.navigateUp() },
                modifier = Modifier.testTag("btn_back_tracking")
            ) {
                Icon(
                    Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Назад",
                    tint = SushiColors.PrimaryText
                )
            }
            Spacer(Modifier.width(8.dp))
            Text(
                "Отслеживание заказа",
                color = SushiColors.PrimaryText,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )
        }

        Box(
            Modifier
                .fillMaxWidth()
                .weight(1f)
                .testTag("tracking_map")
        ) {
            GoogleMap(
                modifier = Modifier.fillMaxSize(),
                cameraPositionState = cameraState
            ) {
                Marker(
                    state = MarkerState(position = position),
                    title = "Курьер",
                    icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)
                )
            }
        }

        Column(
            Modifier
                .fillMaxWidth()
                .background(SushiColors.CardSurface)
                .padding(horizontal = 16.dp, vertical = 12.dp)
                .testTag("tracking_eta_card")
        ) {
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "Курьер в пути",
                    color = SushiColors.PrimaryText,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
                Text(
                    "~$eta мин",
                    color = SushiColors.AccentRed,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    modifier = Modifier.testTag("tracking_eta_label")
                )
            }
            Spacer(Modifier.height(8.dp))
            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp))
                    .testTag("tracking_progress"),
                color = SushiColors.AccentRed,
                trackColor = SushiColors.Background
            )
        }
    }
}
