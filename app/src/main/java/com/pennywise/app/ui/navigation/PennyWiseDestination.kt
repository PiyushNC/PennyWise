package com.pennywise.app.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ReceiptLong
import androidx.compose.material.icons.outlined.AccountBalanceWallet
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Analytics
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.PieChart
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.vector.ImageVector

sealed class PennyWiseDestination(
    val route: String,
    val label: String,
    val icon: ImageVector,
) {
    data object Dashboard : PennyWiseDestination("dashboard", "Dashboard", Icons.Outlined.AccountBalanceWallet)
    data object Transactions : PennyWiseDestination("transactions", "Transactions", Icons.AutoMirrored.Outlined.ReceiptLong)
    data object Analytics : PennyWiseDestination("analytics", "Analytics", Icons.Outlined.Analytics)
    data object Budgets : PennyWiseDestination("budgets", "Budgets", Icons.Outlined.PieChart)
    data object AddTransaction : PennyWiseDestination("transactions/add", "Add transaction", Icons.Outlined.Add)

    data object EditTransaction : PennyWiseDestination("transactions/edit", "Edit transaction", Icons.Outlined.Edit)
    data object AddBudget : PennyWiseDestination("budgets/add", "Add budget", Icons.Outlined.Add)
    data object EditBudget : PennyWiseDestination("budgets/edit", "Edit budget", Icons.Outlined.Edit)
}

@Composable
fun rememberPennyWiseNavItems(): List<PennyWiseDestination> = remember {
    listOf(
        PennyWiseDestination.Dashboard,
        PennyWiseDestination.Transactions,
        PennyWiseDestination.Analytics,
        PennyWiseDestination.Budgets,
    )
}
