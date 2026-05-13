package com.example.oceanfresh.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.oceanfresh.ui.screens.*
import com.example.oceanfresh.viewmodel.CartViewModel
import com.example.oceanfresh.viewmodel.HomeViewModel
import com.freshexpress.viewmodel.AuthViewModel

object Routes {
    const val LOGIN = "login"
    const val HOME = "home"
    const val CART = "cart"
    const val CHECKOUT = "checkout"
    const val SUCCESS = "success"
    const val CATEGORIES = "categories"
}

@Composable
fun FreshNavGraph(
    navController: NavHostController,
    authVM: AuthViewModel,
    cartVM: CartViewModel,
    homeVM: HomeViewModel, // Receive the shared instance from MainActivity
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = Routes.LOGIN,
        modifier = modifier
    ) {

        // ── 1. LOGIN ────────────────────────────────────────────────
        composable(Routes.LOGIN) {
            LoginScreen(
                viewModel = authVM,
                onLoginSuccess = {
                    navController.navigate(Routes.HOME) {
                        popUpTo(Routes.LOGIN) { inclusive = true }
                    }
                }
            )
        }

        // ── 2. HOME ─────────────────────────────────────────────────
        composable(Routes.HOME) {
            HomeScreen(
                homeVM = homeVM,
                cartVM = cartVM,
                onNavigateToCart = { navController.navigate(Routes.CART) }
            )
        }

        // ── 3. CATEGORIES ───────────────────────────────────────────
        composable(Routes.CATEGORIES) {
            CategoriesScreen(
                homeVM = homeVM, // Uses the SAME instance as Home
                onCategoryClick = { categoryName ->
                    homeVM.onCategorySelected(categoryName)
                    navController.navigate(Routes.HOME) {
                        // popUpTo HOME ensures we don't build a massive backstack
                        popUpTo(Routes.HOME) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }

        // ── 4. CART ─────────────────────────────────────────────────
        composable(Routes.CART) {
            CartScreen(
                cartVM = cartVM,
                onBack = { navController.popBackStack() },
                onCheckout = { navController.navigate(Routes.CHECKOUT) }
            )
        }

        // ── 5. CHECKOUT ─────────────────────────────────────────────
        composable(Routes.CHECKOUT) {
            CheckoutScreen(
                cartVM = cartVM,
                onBack = { navController.popBackStack() },
                onOrderPlaced = {
                    navController.navigate(Routes.SUCCESS) {
                        // Keeps Home in the stack but clears the path to it
                        popUpTo(Routes.HOME) { inclusive = false }
                    }
                }
            )
        }

        // ── 6. ORDER SUCCESS ────────────────────────────────────────
        composable(Routes.SUCCESS) {
            OrderSuccessScreen(
                cartVM = cartVM,
                onBackToHome = {
                    navController.navigate(Routes.HOME) {
                        popUpTo(Routes.HOME) { inclusive = true }
                    }
                }
            )
        }
    }
}