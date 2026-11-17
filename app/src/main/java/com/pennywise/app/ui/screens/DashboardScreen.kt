package com.pennywise.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccountBalanceWallet
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.ArrowDownward
import androidx.compose.material.icons.outlined.ArrowUpward
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pennywise.app.ui.anime.AnimeAvatarView
import com.pennywise.app.ui.anime.AnimeCategoryBadge
import com.pennywise.app.ui.anime.AnimeEmptyStateIllustration
import com.pennywise.app.ui.anime.AnimeSpeechBubbleCard
import com.pennywise.app.ui.anime.CompanionEmotion
import com.pennywise.app.ui.components.AmountDisplay
import com.pennywise.app.ui.components.BudgetProgress
import com.pennywise.app.ui.components.PennyWiseCard
import com.pennywise.app.ui.components.PennyWisePrimaryButton
import com.pennywise.app.ui.components.TransactionListItem
import com.pennywise.app.ui.theme.AnimeAtmosphericBackground
import com.pennywise.app.ui.theme.AnimeCoral
import com.pennywise.app.ui.theme.AnimeEmerald
import com.pennywise.app.ui.theme.ElectricViolet
import com.pennywise.app.ui.theme.NeonCyan
import com.pennywise.app.ui.theme.PennyWiseSpacing
import com.pennywise.app.ui.theme.SakuraPink
import com.pennywise.app.ui.theme.SakuraPinkLight
import com.pennywise.app.ui.theme.SoftLavender
import com.pennywise.app.viewmodel.CategorySpending
import com.pennywise.app.viewmodel.DashboardUiState
import com.pennywise.app.viewmodel.DashboardViewModel
import com.pennywise.app.viewmodel.ProfileViewModel
import java.text.NumberFormat
import java.util.Locale

@Composable
fun DashboardScreen(
    onAddTransaction: () -> Unit = {},
    onOpenProfile: () -> Unit = {},
    viewModel: DashboardViewModel,
    profileViewModel: ProfileViewModel,
) {
    val uiState by viewModel.uiState.collectAsState()
    val profile by profileViewModel.profile.collectAsState()
    DashboardContent(
        uiState = uiState,
        onAddTransaction = onAddTransaction,
        onOpenProfile = onOpenProfile,
        profileAvatar = profile.avatar,
        profileName = profile.displayName,
    )
}

@Composable
private fun DashboardContent(
    uiState: DashboardUiState,
    onAddTransaction: () -> Unit,
    onOpenProfile: () -> Unit = {},
    profileAvatar: String = "moon",
    profileName: String = "",
) {
    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddTransaction,
                containerColor = SakuraPink,
                contentColor = Color.White,
                shape = CircleShape,
                modifier = Modifier
                    .border(2.dp, Brush.horizontalGradient(listOf(SakuraPink, ElectricViolet)), CircleShape),
            ) {
                Icon(Icons.Outlined.Add, contentDescription = "Add transaction", modifier = Modifier.size(28.dp))
            }
        },
        containerColor = Color.Transparent,
    ) { paddingValues ->
        AnimeAtmosphericBackground(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = PennyWiseSpacing.screen),
                verticalArrangement = Arrangement.spacedBy(PennyWiseSpacing.section),
            ) {
                item {
                    DashboardHeader(
                        greeting = uiState.greeting,
                        date = uiState.currentDate,
                        onOpenProfile = onOpenProfile,
                        profileAvatar = profileAvatar,
                        profileName = profileName,
                    )
                }

                if (!uiState.hasFinancialData) {
                    item {
                        EmptyDashboardState(onAddTransaction = onAddTransaction)
                    }
                } else {
                    item {
                        BalanceSummaryCard(uiState = uiState)
                    }

                    item {
                        val progress = uiState.spendingProgress
                        val (message, emotion) = when {
                            progress > 1.0f -> "Warning! You've exceeded your monthly budget. Let's adjust your spending routine." to CompanionEmotion.ALERT
                            progress >= 0.8f -> "Careful! You've used ${(progress * 100).toInt()}% of your budget. Slow down on extras!" to CompanionEmotion.WARNING
                            progress >= 0.5f -> "Steady progress! You've used ${(progress * 100).toInt()}% of your budget this month." to CompanionEmotion.ENCOURAGING
                            else -> "Sugoi! You're doing great keeping your expenses under control!" to CompanionEmotion.JOY
                        }
                        AnimeSpeechBubbleCard(
                            message = message,
                            title = "PENNY'S INSIGHTS",
                            emotion = emotion,
                        )
                    }

                    item {
                        IncomeExpenseCard(uiState = uiState)
                    }

                    item {
                        CategorySpendingCard(categories = uiState.categorySpending)
                    }

                    item {
                        Text(
                            text = "Recent Transactions",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                        )
                    }

                    if (uiState.recentTransactions.isEmpty()) {
                        item {
                            Text(
                                text = "Your latest five transactions will appear here.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                    } else {
                        item {
                            PennyWiseCard(modifier = Modifier.fillMaxWidth()) {
                                uiState.recentTransactions.take(5).forEachIndexed { index, transaction ->
                                    TransactionListItem(
                                        title = transaction.title,
                                        category = transaction.category,
                                        amount = signedCurrency(transaction.amount, transaction.isIncome),
                                        date = transaction.date,
                                        isIncome = transaction.isIncome,
                                    )
                                    if (index < uiState.recentTransactions.take(5).lastIndex) {
                                        HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
                                    }
                                }
                            }
                        }
                    }

                    item { Spacer(modifier = Modifier.height(72.dp)) }
                }
            }
        }
    }
}

@Composable
private fun DashboardHeader(
    greeting: String,
    date: String,
    onOpenProfile: () -> Unit,
    profileAvatar: String,
    profileName: String,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = PennyWiseSpacing.large),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(
                text = greeting.uppercase(),
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                color = SakuraPink,
                letterSpacing = 1.2.sp,
            )
            Text(
                text = "Financial Command",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.ExtraBold,
            )
            Text(
                text = date,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }

        AnimeAvatarView(
            avatarId = profileAvatar,
            size = 52.dp,
            isSelected = true,
            onClick = onOpenProfile,
        )
    }
}

@Composable
private fun BalanceSummaryCard(uiState: DashboardUiState) {
    PennyWiseCard(
        modifier = Modifier.fillMaxWidth(),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top,
        ) {
            Column {
                Text(
                    text = "TOTAL MONTHLY BUDGET",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = SakuraPink,
                    letterSpacing = 1.sp,
                )
                Text(
                    text = uiState.monthlyBudget?.let(::formatCurrency) ?: "—",
                    style = MaterialTheme.typography.displaySmall,
                    fontWeight = FontWeight.Black,
                )
            }

            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(Brush.linearGradient(listOf(SakuraPink, ElectricViolet))),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = Icons.Outlined.AccountBalanceWallet,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(24.dp),
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        BudgetProgress(
            label = "Monthly spending",
            spent = formatCurrency(uiState.monthlyExpenses),
            limit = uiState.monthlyBudget?.let(::formatCurrency) ?: "—",
            progress = uiState.spendingProgress,
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = PennyWiseSpacing.large),
            horizontalArrangement = Arrangement.spacedBy(PennyWiseSpacing.standard),
        ) {
            SummaryMetric(
                label = "Spent",
                value = formatCurrency(uiState.monthlyExpenses),
                color = AnimeCoral,
                modifier = Modifier.weight(1f),
            )
            SummaryMetric(
                label = "Remaining",
                value = uiState.remainingBudget?.let(::formatCurrency) ?: "—",
                color = AnimeEmerald,
                modifier = Modifier.weight(1f),
            )
        }
    }
}

@Composable
private fun SummaryMetric(
    label: String,
    value: String,
    color: Color,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        Text(
            text = label.uppercase(),
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            letterSpacing = 0.8.sp,
        )
        Text(
            text = value,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.ExtraBold,
            color = color,
        )
    }
}

@Composable
private fun IncomeExpenseCard(uiState: DashboardUiState) {
    PennyWiseCard(modifier = Modifier.fillMaxWidth()) {
        Text("Income vs Expenses", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = PennyWiseSpacing.large),
            horizontalArrangement = Arrangement.spacedBy(PennyWiseSpacing.standard),
        ) {
            IncomeExpenseMetric(
                label = "Income",
                value = formatCurrency(uiState.monthlyIncome),
                positive = true,
                modifier = Modifier.weight(1f),
            )
            IncomeExpenseMetric(
                label = "Expenses",
                value = formatCurrency(uiState.monthlyExpenses),
                positive = false,
                modifier = Modifier.weight(1f),
            )
        }
    }
}

@Composable
private fun IncomeExpenseMetric(
    label: String,
    value: String,
    positive: Boolean,
    modifier: Modifier = Modifier,
) {
    val metricColor = if (positive) AnimeEmerald else AnimeCoral
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(38.dp)
                .clip(CircleShape)
                .background(metricColor.copy(alpha = 0.2f)),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = if (positive) Icons.Outlined.ArrowUpward else Icons.Outlined.ArrowDownward,
                contentDescription = null,
                tint = metricColor,
                modifier = Modifier.size(20.dp),
            )
        }
        Column(modifier = Modifier.padding(start = PennyWiseSpacing.small)) {
            Text(label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(value, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = metricColor)
        }
    }
}

@Composable
private fun CategorySpendingCard(categories: List<CategorySpending>) {
    PennyWiseCard(modifier = Modifier.fillMaxWidth()) {
        Text("Where Money Goes", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        if (categories.isEmpty()) {
            Text(
                "Category insights will appear after you add expenses.",
                modifier = Modifier.padding(top = PennyWiseSpacing.compact),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        } else {
            categories.take(4).forEach { category ->
                CategorySpendingRow(category = category)
            }
        }
    }
}

@Composable
private fun CategorySpendingRow(category: CategorySpending) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = PennyWiseSpacing.standard),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        AnimeCategoryBadge(category = category.name, size = 38.dp)

        Text(
            text = category.name,
            modifier = Modifier
                .weight(1f)
                .padding(start = PennyWiseSpacing.small),
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.SemiBold,
        )
        Text(
            text = formatCurrency(category.amount),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = SakuraPink,
        )
    }
}

@Composable
private fun EmptyDashboardState(onAddTransaction: () -> Unit) {
    PennyWiseCard(modifier = Modifier.fillMaxWidth()) {
        AnimeEmptyStateIllustration(
            title = "Your Financial Journey Begins!",
            subtitle = "Add your first transaction or set a monthly budget to see your anime command dashboard come alive.",
        )
        PennyWisePrimaryButton(
            text = "+ Add First Transaction",
            onClick = onAddTransaction,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = PennyWiseSpacing.standard),
        )
    }
}

private fun formatCurrency(amount: Double): String =
    NumberFormat.getCurrencyInstance(Locale("en", "IN")).apply {
        maximumFractionDigits = 0
        minimumFractionDigits = 0
    }.format(amount)

private fun signedCurrency(amount: Double, isIncome: Boolean): String =
    "${if (isIncome) "+" else "−"}${formatCurrency(amount)}"
