package com.baha.sushigarden.features.promotions

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

data class Promotion(val id: String, val title: String, val subtitle: String)

@HiltViewModel
class PromotionsViewModel @Inject constructor() : ViewModel() {
    private val _promos = MutableStateFlow(
        listOf(
            Promotion("p1", "ХОТ РОЛЛС",  "Горячие роллы со скидкой 20%"),
            Promotion("p2", "HAPPY HOUR",  "С 14:00 до 16:00 скидка на всё"),
            Promotion("p3", "COMBO SET",   "Набор из 4 ролл + напиток")
        )
    )
    val promos = _promos.asStateFlow()
}
