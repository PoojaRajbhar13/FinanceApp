package com.example.projectz

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Insights
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Insights
import androidx.compose.material.icons.outlined.List
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.projectz.presentation.viewmodel.AuthViewModel
import com.example.projectz.presentation.screens.auth.LoginScreen
import com.example.projectz.presentation.screens.auth.RegisterScreen
import com.example.projectz.presentation.screens.dashboard.DashboardScreen
import com.example.projectz.presentation.viewmodel.DashboardViewModel
import com.example.projectz.presentation.screens.goals.GoalsScreen
import com.example.projectz.presentation.viewmodel.GoalsViewModel
import com.example.projectz.presentation.screens.insights.InsightsScreen
import com.example.projectz.presentation.viewmodel.InsightsViewModel
import com.example.projectz.presentation.screens.transactions.add_edit.AddTransactionScreen
import com.example.projectz.presentation.viewmodel.AddTransactionViewModel
import com.example.projectz.presentation.screens.transactions.list.TransactionListScreen
import com.example.projectz.presentation.viewmodel.TransactionListViewModel
import com.example.projectz.presentation.theme.FinanceCompanionTheme
import com.example.projectz.presentation.screens.splashscreen.SplashScreen
import com.example.projectz.presentation.viewmodel.UserPreferenceViewModel

sealed class Screen(val route: String, val title: String, val selectedIcon: ImageVector, val unselectedIcon: ImageVector) {
    object Splash : Screen("splash", "Splash", Icons.Filled.Home, Icons.Filled.Home)
    object Onboarding : Screen("onboarding", "Onboarding", Icons.Filled.Home, Icons.Filled.Home)
    object Dashboard : Screen("dashboard", "Home", Icons.Filled.Home, Icons.Outlined.Home)
    object Transactions : Screen("transactions", "History", Icons.Filled.List, Icons.Outlined.List)
    object AddTransaction : Screen("add_transaction", "Add", Icons.Filled.Add, Icons.Filled.Add)
    object Goals : Screen("goals", "Goals", Icons.Filled.Star, Icons.Outlined.Star)
    object Insights : Screen("insights", "Insights", Icons.Filled.Insights, Icons.Outlined.Insights)
    object Login : Screen("login", "Login", Icons.Filled.Home, Icons.Filled.Home)
    object Register : Screen("register", "Register", Icons.Filled.Home, Icons.Filled.Home)
}

val items = listOf(
    Screen.Dashboard,
    Screen.Transactions,
    Screen.Goals,
    Screen.Insights
)

@Composable
fun FinanceApp() {
    val navController = rememberNavController()
    val isDarkTheme = androidx.compose.foundation.isSystemInDarkTheme()

    FinanceCompanionTheme(darkTheme = isDarkTheme) {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentDestination = navBackStackEntry?.destination

        val hideBottomBarRoutes = listOf(Screen.Splash.route, Screen.Onboarding.route, Screen.AddTransaction.route, Screen.Login.route, Screen.Register.route)
        val shouldShowBottomBar = currentDestination?.route !in hideBottomBarRoutes

        Scaffold(
            bottomBar = {
                if (shouldShowBottomBar) {
                    NavigationBar {
                        items.forEach { screen ->
                            val isSelected = currentDestination?.hierarchy?.any { it.route == screen.route } == true
                            NavigationBarItem(
                                icon = {
                                    Icon(
                                        imageVector = if (isSelected) screen.selectedIcon else screen.unselectedIcon,
                                        contentDescription = screen.title
                                    )
                                },
                                label = { Text(screen.title) },
                                selected = isSelected,
                                onClick = {
                                    navController.navigate(screen.route) {
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
        ) { innerPadding ->
            NavHost(
                navController = navController,
                startDestination = Screen.Splash.route,
                modifier = Modifier.padding(innerPadding)
            ) {
                composable(Screen.Splash.route) {
                    val viewModel: UserPreferenceViewModel = hiltViewModel()
                    SplashScreen(
                        userPreferenceViewModel = viewModel,
                        onNavigateToHome = {
                            navController.navigate(Screen.Dashboard.route) {
                                popUpTo(Screen.Splash.route) { inclusive = true }
                            }
                        },
                        onNavigateToLogin = {
                            navController.navigate(Screen.Login.route) {
                                popUpTo(Screen.Splash.route) { inclusive = true }
                            }
                        },
                        onNavigateToOnboarding = {
                            navController.navigate(Screen.Onboarding.route) {
                                popUpTo(Screen.Splash.route) { inclusive = true }
                            }
                        }
                    )
                }

                composable(Screen.Onboarding.route) {
                    // Placeholder for OnboardingScreen - redirect to Login for now
                    LaunchedEffect(Unit) {
                        navController.navigate(Screen.Login.route) {
                            popUpTo(Screen.Onboarding.route) { inclusive = true }
                        }
                    }
                }

                composable(Screen.Dashboard.route) {
                    val viewModel: DashboardViewModel = hiltViewModel()
                    DashboardScreen(
                        viewModel = viewModel,
                        onNavigateToTransactions = {
                            navController.navigate(Screen.Transactions.route) {
                                popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        onNavigateToInsight = {
                            navController.navigate(Screen.Goals.route) {
                                popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        onNavigateToEditTransaction = { transactionId ->
                            navController.navigate("${Screen.AddTransaction.route}?transactionId=$transactionId")
                        }
                    )
                }

                composable(Screen.Transactions.route) {
                    val viewModel: TransactionListViewModel = hiltViewModel()
                    TransactionListScreen(
                        viewModel = viewModel,
                        onAddTransactionClick = { navController.navigate(Screen.AddTransaction.route) },
                        onNavigateToEditTransaction = { transactionId ->
                            navController.navigate("${Screen.AddTransaction.route}?transactionId=$transactionId")
                        }
                    )
                }

                composable(
                    route = "${Screen.AddTransaction.route}?transactionId={transactionId}",
                    arguments = listOf(
                        androidx.navigation.navArgument("transactionId") {
                            nullable = true
                            defaultValue = null
                            type = androidx.navigation.NavType.StringType
                        }
                    )
                ) { backStackEntry ->
                    val transactionId = backStackEntry.arguments?.getString("transactionId")
                    val viewModel: AddTransactionViewModel = hiltViewModel()
                    AddTransactionScreen(
                        viewModel = viewModel,
                        transactionId = transactionId,
                        onNavigateBack = { navController.popBackStack() }
                    )
                }

                composable(Screen.Goals.route) {
                    val viewModel: GoalsViewModel = hiltViewModel()
                    GoalsScreen(viewModel = viewModel)
                }

                composable(Screen.Insights.route) {
                    val viewModel: InsightsViewModel = hiltViewModel()
                    InsightsScreen(viewModel = viewModel)
                }

                composable(Screen.Login.route) {
                    val viewModel: AuthViewModel = hiltViewModel()
                    LoginScreen(
                        viewModel = viewModel,
                        onNavigateToRegister = {
                            navController.navigate(Screen.Register.route)
                        },
                        onNavigateToHome = {
                            navController.navigate(Screen.Dashboard.route) {
                                popUpTo(navController.graph.id) { inclusive = true }
                            }
                        }
                    )
                }

                composable(Screen.Register.route) {
                    val viewModel: AuthViewModel = hiltViewModel()
                    RegisterScreen(
                        viewModel = viewModel,
                        onNavigateToLogin = {
                            navController.navigate(Screen.Login.route) {
                                popUpTo(Screen.Login.route) { inclusive = true }
                            }
                        },
                        onNavigateToHome = {
                            navController.navigate(Screen.Dashboard.route) {
                                popUpTo(navController.graph.id) { inclusive = true }
                            }
                        }
                    )
                }
            }
        }
    }
}
