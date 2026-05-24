package com.baha.sushigarden.data.services.catalog

import com.baha.sushigarden.R
import com.baha.sushigarden.data.models.AddOn
import com.baha.sushigarden.data.models.Category
import com.baha.sushigarden.data.models.Product

class LocalMenuRepository : MenuRepository {
    private val addOns = listOf(
        AddOn("ao1", "Васаби",      60),
        AddOn("ao2", "Имбирь",      60),
        AddOn("ao3", "Соевый соус", 60)
    )

    private val categories = listOf(
        Category("sushi",  "Суши"),
        Category("rolls",  "Роллы"),
        Category("hot",    "Горячие роллы"),
        Category("salads", "Салаты"),
        Category("wok",    "WOK")
    )

    private val products = listOf(
        Product("p1",  "Хикари",          620, 255, "sushi",  R.drawable.product_hikari,     addOns),
        Product("p2",  "Лос-Анджелес",    707, 285, "rolls",  R.drawable.product_losangeles, addOns),
        Product("p3",  "Айдахо маки",     810, 285, "rolls",  R.drawable.product_idaho,      addOns),
        Product("p4",  "Осака маки",      740, 275, "sushi",  R.drawable.product_osaka,      addOns),
        Product("p5",  "Филадельфия",     850, 300, "rolls",  R.drawable.product_losangeles, addOns),
        Product("p6",  "Унаги маки",      780, 270, "hot",    R.drawable.product_hikari,     addOns),
        Product("p7",  "Спайси тунец",    720, 260, "hot",    R.drawable.product_osaka,      addOns),
        Product("p8",  "Греческий салат", 390, 200, "salads", R.drawable.product_idaho,      addOns),
        Product("p9",  "WOK с курицей",   490, 350, "wok",    R.drawable.product_losangeles, addOns),
        Product("p10", "WOK с говядиной", 540, 350, "wok",    R.drawable.product_osaka,      addOns)
    )

    override fun getCategories() = categories
    override fun getProducts(categoryId: String) = products.filter { it.categoryId == categoryId }
    override fun getProduct(id: String) = products.find { it.id == id }
    override fun getAllProducts() = products
}
