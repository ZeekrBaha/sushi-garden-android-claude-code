package com.baha.sushigarden.data.services.delivery

import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class CourierSimulator(private val dispatcher: CoroutineDispatcher = Dispatchers.Default) {
    private val route = listOf(
        LatLng(55.7558, 37.6173),
        LatLng(55.7580, 37.6200),
        LatLng(55.7600, 37.6230),
        LatLng(55.7620, 37.6260),
        LatLng(55.7640, 37.6290),
        LatLng(55.7660, 37.6320)
    )

    private val _position   = MutableStateFlow(route.first())
    private val _progress   = MutableStateFlow(0f)
    private val _etaMinutes = MutableStateFlow(30)

    val position    = _position.asStateFlow()
    val progress    = _progress.asStateFlow()
    val etaMinutes  = _etaMinutes.asStateFlow()

    private var job: Job? = null

    fun start() {
        if (job?.isActive == true) return  // guard: ignore duplicate start() calls
        _position.value   = route.first()
        _progress.value   = 0f
        _etaMinutes.value = 30
        job = CoroutineScope(dispatcher).launch {
            val totalSteps = route.size - 1
            for (i in 0 until totalSteps) {
                delay(3_000)
                _position.value   = route[i + 1]
                _progress.value   = (i + 1).toFloat() / totalSteps
                _etaMinutes.value = (30 * (1f - _progress.value)).toInt().coerceAtLeast(1)
            }
        }
    }

    fun stop() {
        job?.cancel()
        job = null
    }
}
