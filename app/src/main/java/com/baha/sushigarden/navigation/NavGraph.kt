package com.baha.sushigarden.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.baha.sushigarden.features.auth.AuthScreen
import com.baha.sushigarden.features.auth.AuthViewModel
import com.baha.sushigarden.features.cart.CartScreen
import com.baha.sushigarden.features.catalog.CatalogScreen
import com.baha.sushigarden.features.checkout.CheckoutScreen
import com.baha.sushigarden.features.orders.OrderDetailScreen
import com.baha.sushigarden.features.orders.OrdersScreen
import com.baha.sushigarden.features.productdetail.ProductDetailScreen
import com.baha.sushigarden.features.profile.ProfileScreen
import com.baha.sushigarden.features.promotions.PromotionsScreen
import com.baha.sushigarden.features.tracking.TrackingScreen
import com.baha.sushigarden.ui.designsystem.SushiColors

sealed class Screen(val route: String) {
    data object Auth    : Screen("auth")
    data object Catalog : Screen("catalog")
    data object Promos  : Screen("promos")
    data object Orders  : Screen("orders")
    data object Cart    : Screen("cart")
    data object Profile : Screen("profile")
    data object ProductDetail : Screen("product/{productId}") {
        fun createRoute(id: String) = "product/$id"
    }
    data object Checkout    : Screen("checkout")
    data object Tracking    : Screen("tracking")
    data object OrderDetail : Screen("order/{orderId}") {
        fun createRoute(id: String) = "order/$id"
    }
}

@Composable
fun NavGraph(navController: NavHostController = rememberNavController()) {
    val authViewModel: AuthViewModel = hiltViewModel()
    val isLoggedIn by authViewModel.isLoggedIn.collectAsState(initial = false)

    if (!isLoggedIn) {
        AuthScreen(onAuthSuccess = {
            navController.navigate(Screen.Catalog.route) {
                popUpTo(Screen.Auth.route) { inclusive = true }
            }
        })
        return
    }

    Scaffold(bottomBar = { BottomNavBar(navController) }) { padding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Catalog.route,
            modifier = Modifier.padding(padding)
        ) {
            composable(Screen.Catalog.route) {
                CatalogScreen(navController)
            }
            composable(Screen.Promos.route) {
                PromotionsScreen()
            }
            composable(Screen.Orders.route) {
                OrdersScreen(navController)
            }
            composable(Screen.Cart.route) {
                CartScreen(navController)
            }
            composable(Screen.Profile.route) {
                ProfileScreen(navController)
            }
            composable(Screen.ProductDetail.route) { backStack ->
                val id = backStack.arguments?.getString("productId") ?: ""
                ProductDetailScreen(id, navController)
            }
            composable(Screen.Checkout.route) {
                CheckoutScreen(navController)
            }
            composable(Screen.Tracking.route) {
                TrackingScreen(navController)
            }
            composable(Screen.OrderDetail.route) { backStack ->
                val id = backStack.arguments?.getString("orderId") ?: ""
                OrderDetailScreen(id, navController)
            }
        }
    }
}
