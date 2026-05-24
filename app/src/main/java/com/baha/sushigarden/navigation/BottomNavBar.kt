package com.baha.sushigarden.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocalOffer
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.baha.sushigarden.ui.designsystem.SushiColors

data class BottomNavItem(
    val screen: Screen,
    val label: String,
    val icon: ImageVector
)

val bottomNavItems = listOf(
    BottomNavItem(Screen.Catalog, "Каталог", Icons.Default.Home),
    BottomNavItem(Screen.Promos,  "Акции",   Icons.Default.LocalOffer),
    BottomNavItem(Screen.Orders,  "Заказы",  Icons.Default.Receipt),
    BottomNavItem(Screen.Cart,    "Корзина", Icons.Default.ShoppingCart),
    BottomNavItem(Screen.Profile, "Профиль", Icons.Default.Person)
)

@Composable
fun BottomNavBar(navController: NavController) {
    val backStack by navController.currentBackStackEntryAsState()
    val current = backStack?.destination?.route

    NavigationBar(containerColor = SushiColors.TabBar) {
        bottomNavItems.forEach { item ->
            NavigationBarItem(
                selected = current == item.screen.route,
                onClick = {
                    navController.navigate(item.screen.route) {
                        popUpTo(Screen.Catalog.route) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                icon = { Icon(item.icon, item.label) },
                label = { Text(item.label) },
                modifier = Modifier.testTag("nav_${item.screen.route}"),
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor   = SushiColors.AccentRed,
                    selectedTextColor   = SushiColors.AccentRed,
                    unselectedIconColor = SushiColors.IconInactive,
                    unselectedTextColor = SushiColors.IconInactive,
                    indicatorColor      = Color.Transparent
                )
            )
        }
    }
}
