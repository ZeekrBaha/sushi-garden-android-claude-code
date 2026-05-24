package com.baha.sushigarden.features.tracking

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.baha.sushigarden.data.services.delivery.CourierSimulator
import com.google.android.gms.maps.model.LatLng
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class TrackingViewModel @Inject constructor(
    private val courierSimulator: CourierSimulator
) : ViewModel() {

    val position: StateFlow<LatLng> = courierSimulator.position
        .stateIn(viewModelScope, SharingStarted.Eagerly, courierSimulator.position.value)

    val progress: StateFlow<Float> = courierSimulator.progress
        .stateIn(viewModelScope, SharingStarted.Eagerly, 0f)

    val etaMinutes: StateFlow<Int> = courierSimulator.etaMinutes
        .stateIn(viewModelScope, SharingStarted.Eagerly, 30)

    init {
        courierSimulator.start()
    }

    override fun onCleared() {
        courierSimulator.stop()
        super.onCleared()
    }
}
