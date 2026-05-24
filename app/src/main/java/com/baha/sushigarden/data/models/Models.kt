package com.baha.sushigarden.data.models

data class AddOn(val id: String, val name: String, val price: Int)

data class Product(
    val id: String,
    val name: String,
    val price: Int,
    val weightGrams: Int,
    val categoryId: String,
    val imageRes: Int,
    val availableAddOns: List<AddOn> = emptyList()
)

data class Category(val id: String, val name: String)

data class CartItem(
    val product: Product,
    val quantity: Int,
    val selectedAddOns: List<AddOn> = emptyList()
) {
    val lineTotal: Int get() = (product.price + selectedAddOns.sumOf { it.price }) * quantity
}

data class CartState(val items: List<CartItem> = emptyList()) {
    val itemCount: Int  get() = items.sumOf { it.quantity }
    val subtotal: Int   get() = items.sumOf { it.lineTotal }
    val deliveryFee: Int get() = if (items.isEmpty()) 0 else 199
    val serviceFee: Int  get() = if (items.isEmpty()) 0 else 49
    val total: Int       get() = subtotal + deliveryFee + serviceFee
}

data class DeliveryAddress(
    val street: String = "",
    val recipientName: String = "",
    val phone: String = "",
    val email: String = ""
)

data class OrderLine(val productName: String, val qty: Int, val lineTotal: Int)

data class Order(
    val id: String,
    val lines: List<OrderLine>,
    val subtotal: Int,
    val deliveryFee: Int,
    val serviceFee: Int,
    val total: Int,
    val address: DeliveryAddress,
    val createdAt: Long = System.currentTimeMillis()
)

data class UserProfile(
    val uid: String,
    val name: String,
    val email: String,
    val phone: String = ""
)

data class Courier(
    val name: String = "Максим Винокур",
    val title: String = "Курьер",
    val avatarRes: Int = 0
)
