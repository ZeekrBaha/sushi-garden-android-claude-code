package com.baha.sushigarden.data.services.orders

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "orders")
data class OrderEntity(
    @PrimaryKey val id: String,
    val linesJson: String,
    val subtotal: Int,
    val deliveryFee: Int,
    val serviceFee: Int,
    val total: Int,
    val addressJson: String,
    val createdAt: Long
)
