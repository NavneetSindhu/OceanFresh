package com.example.oceanfresh

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.oceanfresh.data.local.AppDatabase
import com.example.oceanfresh.data.repository.GroceryRepository
import com.example.oceanfresh.navigation.FreshNavGraph
import com.example.oceanfresh.navigation.Routes
import com.example.oceanfresh.ui.components.FloatingFitSyncNavBar
import com.example.oceanfresh.ui.theme.OceanFreshTheme
import com.example.oceanfresh.viewmodel.CartViewModel
import com.example.oceanfresh.viewmodel.HomeViewModel
import com.freshexpress.viewmodel.AuthViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            OceanFreshApp()
        }
    }
}

/**
 * A generic Factory to handle our ViewModels that require the Repository.
 */
class GroceryViewModelFactory(private val repository: GroceryRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(HomeViewModel::class.java) -> HomeViewModel(repository) as T
            modelClass.isAssignableFrom(CartViewModel::class.java) -> CartViewModel(repository) as T
            else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }
}

@Composable
fun OceanFreshApp() {
    val context = LocalContext.current

    // 1. Initialize Room Database and Repository
    // In a real app, use Hilt or a Singleton for this.
    val database = remember { AppDatabase.getDatabase(context) }
    val repository = remember { GroceryRepository(database.cartDao()) }
    val factory = remember { GroceryViewModelFactory(repository) }

    // 2. Initialize ViewModels using the Factory
    val homeVM: HomeViewModel = viewModel(factory = factory)
    val cartVM: CartViewModel = viewModel(factory = factory)
    val authVM: AuthViewModel = viewModel() // Assuming AuthVM doesn't need the repo yet

    // 3. Observe the Dark Mode state from HomeViewModel
    val isDarkMode by homeVM.isDarkMode.collectAsStateWithLifecycle()

    // 4. Wrap everything in the Dynamic Theme
    OceanFreshTheme(darkTheme = isDarkMode) {
        val navController = rememberNavController()
        val currentBackStack by navController.currentBackStackEntryAsState()
        val currentRoute = currentBackStack?.destination?.route
        val showBottomNav = currentRoute in listOf(Routes.HOME, Routes.CATEGORIES)

        Scaffold { innerPadding ->
            Box(modifier = Modifier.fillMaxSize()) {
                FreshNavGraph(
                    navController = navController,
                    authVM = authVM,
                    cartVM = cartVM,
                    homeVM = homeVM, // Pass homeVM to the graph so it can use the same instance
                    modifier = Modifier.padding(
                        bottom = if (showBottomNav) innerPadding.calculateBottomPadding() else 0.dp
                    )
                )

                if (showBottomNav) {
                    Box(modifier = Modifier.align(Alignment.BottomCenter)) {
                        FloatingFitSyncNavBar(
                            currentDestination = currentRoute,
                            accentColor = MaterialTheme.colorScheme.primary,
                            isDarkTheme = isDarkMode, // Use the observed state
                            onNavigate = { route ->
                                navController.navigate(route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}