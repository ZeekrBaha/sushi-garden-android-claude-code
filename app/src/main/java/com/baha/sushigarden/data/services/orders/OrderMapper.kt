package com.baha.sushigarden.data.services.orders

import com.baha.sushigarden.data.models.DeliveryAddress
import com.baha.sushigarden.data.models.Order
import com.baha.sushigarden.data.models.OrderLine
import org.json.JSONArray
import org.json.JSONObject

fun Order.toEntity(): OrderEntity {
    val linesArr = JSONArray()
    lines.forEach { line ->
        linesArr.put(JSONObject().apply {
            put("name", line.productName)
            put("qty", line.qty)
            put("total", line.lineTotal)
        })
    }
    val addrObj = JSONObject().apply {
        put("street", address.street)
        put("name", address.recipientName)
        put("phone", address.phone)
        put("email", address.email)
    }
    return OrderEntity(id, linesArr.toString(), subtotal, deliveryFee, serviceFee, total,
        addrObj.toString(), createdAt)
}

fun OrderEntity.toOrder(): Order {
    val arr = JSONArray(linesJson)
    val lines = (0 until arr.length()).map { i ->
        val o = arr.getJSONObject(i)
        OrderLine(o.getString("name"), o.getInt("qty"), o.getInt("total"))
    }
    val addr = JSONObject(addressJson).let {
        DeliveryAddress(it.getString("street"), it.getString("name"),
            it.getString("phone"), it.getString("email"))
    }
    return Order(id, lines, subtotal, deliveryFee, serviceFee, total, addr, createdAt)
}
