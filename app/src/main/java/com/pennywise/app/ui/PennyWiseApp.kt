package com.pennywise.app.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.pennywise.app.ui.components.PennyWiseBottomBar
import com.pennywise.app.ui.navigation.PennyWiseDestination
import com.pennywise.app.ui.navigation.rememberPennyWiseNavItems
import com.pennywise.app.ui.screens.AnalyticsScreen
import com.pennywise.app.ui.screens.BudgetsScreen
import com.pennywise.app.ui.screens.AddEditBudgetScreen
import com.pennywise.app.ui.screens.DashboardScreen
import com.pennywise.app.ui.screens.TransactionsScreen
import com.pennywise.app.ui.screens.AddEditTransactionScreen
import com.pennywise.app.PennyWiseApplication
import com.pennywise.app.viewmodel.TransactionsViewModel
import com.pennywise.app.viewmodel.BudgetViewModel
import com.pennywise.app.viewmodel.DashboardViewModel
import com.pennywise.app.viewmodel.DashboardViewModelFactory
import com.pennywise.app.viewmodel.AnalyticsViewModel

@Composable
fun PennyWiseApp() {
    val navController = rememberNavController()
    val destinations = rememberPennyWiseNavItems()
    val currentBackStackEntry = navController.currentBackStackEntryAsState()
    val application = LocalContext.current.applicationContext as PennyWiseApplication
    val transactionsViewModel: TransactionsViewModel = viewModel(
        factory = TransactionsViewModel.Factory(application.transactionRepository),
    )
    val budgetViewModel: BudgetViewModel = viewModel(
        factory = BudgetViewModel.Factory(
            application.budgetRepository,
            application.transactionRepository,
        ),
    )
    val dashboardViewModel: DashboardViewModel = viewModel(
        factory = DashboardViewModelFactory(
            application.budgetRepository,
            application.transactionRepository,
        ),
    )
    val analyticsViewModel: AnalyticsViewModel = viewModel(
        factory = AnalyticsViewModel.Factory(application.transactionRepository),
    )

    Scaffold(
        bottomBar = {
            val route = currentBackStackEntry.value?.destination?.route
            if (route in destinations.map { it.route }) {
                PennyWiseBottomBar(
                    destinations = destinations,
                    currentRoute = route,
                    onDestinationSelected = { destination ->
                        navController.navigate(destination.route) {
                            popUpTo(PennyWiseDestination.Dashboard.route) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                )
            }
        },
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = PennyWiseDestination.Dashboard.route,
            modifier = Modifier.padding(innerPadding),
        ) {
            composable(PennyWiseDestination.Dashboard.route) {
                DashboardScreen(
                    viewModel = dashboardViewModel,
                    onAddTransaction = {
                        navController.navigate(PennyWiseDestination.AddTransaction.route)
                    },
                )
            }
            composable(PennyWiseDestination.Transactions.route) {
                TransactionsScreen(
                    viewModel = transactionsViewModel,
                    onAddTransaction = { navController.navigate(PennyWiseDestination.AddTransaction.route) },
                    onEditTransaction = { id ->
                        navController.navigate("${PennyWiseDestination.EditTransaction.route}/$id")
                    },
                )
            }
            composable(PennyWiseDestination.AddTransaction.route) {
                AddEditTransactionScreen(
                    onBack = { navController.popBackStack() },
                    onSaved = { navController.popBackStack() },
                    viewModel = transactionsViewModel,
                )
            }
            composable("${PennyWiseDestination.EditTransaction.route}/{transactionId}") { entry ->
                AddEditTransactionScreen(
                    transactionId = entry.arguments?.getString("transactionId")?.toLongOrNull(),
                    onBack = { navController.popBackStack() },
                    onSaved = { navController.popBackStack() },
                    viewModel = transactionsViewModel,
                )
            }
            composable(PennyWiseDestination.Analytics.route) {
                AnalyticsScreen(viewModel = analyticsViewModel)
            }
            composable(PennyWiseDestination.Budgets.route) {
                BudgetsScreen(
                    viewModel = budgetViewModel,
                    onAddBudget = { navController.navigate(PennyWiseDestination.AddBudget.route) },
                    onEditBudget = { id ->
                        navController.navigate("${PennyWiseDestination.EditBudget.route}/$id")
                    },
                )
            }
            composable(PennyWiseDestination.AddBudget.route) {
                AddEditBudgetScreen(
                    viewModel = budgetViewModel,
                    onBack = { navController.popBackStack() },
                    onSaved = { navController.popBackStack() },
                )
            }
            composable("${PennyWiseDestination.EditBudget.route}/{budgetId}") { entry ->
                AddEditBudgetScreen(
                    viewModel = budgetViewModel,
                    budgetId = entry.arguments?.getString("budgetId")?.toLongOrNull(),
                    onBack = { navController.popBackStack() },
                    onSaved = { navController.popBackStack() },
                )
            }
        }
    }
}
