package com.baha.sushigarden.data.services.catalog

import com.baha.sushigarden.data.models.Category
import com.baha.sushigarden.data.models.Product

interface MenuRepository {
    fun getCategories(): List<Category>
    fun getProducts(categoryId: String): List<Product>
    fun getProduct(id: String): Product?
    fun getAllProducts(): List<Product>
}
