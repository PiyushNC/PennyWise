package com.pennywise.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.DeleteOutline
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pennywise.app.ui.anime.AnimeCategoryBadge
import com.pennywise.app.ui.anime.AnimeEmptyStateIllustration
import com.pennywise.app.ui.anime.AnimeSpeechBubbleCard
import com.pennywise.app.ui.anime.CompanionEmotion
import com.pennywise.app.ui.components.BudgetProgress
import com.pennywise.app.ui.components.PennyWiseCard
import com.pennywise.app.ui.theme.AnimeAtmosphericBackground
import com.pennywise.app.ui.theme.AnimeCoral
import com.pennywise.app.ui.theme.AnimeEmerald
import com.pennywise.app.ui.theme.ElectricViolet
import com.pennywise.app.ui.theme.PennyWiseSpacing
import com.pennywise.app.ui.theme.SakuraPink
import com.pennywise.app.viewmodel.BudgetSummary
import com.pennywise.app.viewmodel.BudgetUiState
import com.pennywise.app.viewmodel.BudgetViewModel
import java.text.NumberFormat
import java.util.Locale
import kotlinx.coroutines.launch

@Composable
fun BudgetsScreen(
    onAddBudget: () -> Unit,
    onEditBudget: (Long) -> Unit,
    viewModel: BudgetViewModel,
) {
    val uiState by viewModel.uiState.collectAsState()
    BudgetsContent(
        uiState = uiState,
        onAddBudget = onAddBudget,
        onEditBudget = onEditBudget,
        onDeleteBudget = viewModel::delete,
    )
}

@Composable
private fun BudgetsContent(
    uiState: BudgetUiState,
    onAddBudget: () -> Unit,
    onEditBudget: (Long) -> Unit,
    onDeleteBudget: (BudgetSummary) -> Unit,
) {
    var pendingDelete by remember { mutableStateOf<BudgetSummary?>(null) }
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    pendingDelete?.let { summary ->
        AlertDialog(
            onDismissRequest = { pendingDelete = null },
            title = { Text("Delete budget?", fontWeight = FontWeight.Bold) },
            text = { Text("This budget target will be permanently removed.") },
            confirmButton = {
                TextButton(onClick = {
                    onDeleteBudget(summary)
                    pendingDelete = null
                    coroutineScope.launch {
                        snackbarHostState.showSnackbar("Budget deleted")
                    }
                }) {
                    Text("Delete", color = AnimeCoral, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { pendingDelete = null }) { Text("Cancel") }
            },
            containerColor = MaterialTheme.colorScheme.surface,
        )
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddBudget,
                containerColor = SakuraPink,
                contentColor = Color.White,
                shape = CircleShape,
                modifier = Modifier.border(2.dp, Brush.horizontalGradient(listOf(SakuraPink, ElectricViolet)), CircleShape),
            ) {
                Icon(Icons.Outlined.Add, contentDescription = "Add budget", modifier = Modifier.size(28.dp))
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
                verticalArrangement = Arrangement.spacedBy(PennyWiseSpacing.standard),
            ) {
                item {
                    Column(
                        modifier = Modifier.padding(top = PennyWiseSpacing.large),
                        verticalArrangement = Arrangement.spacedBy(2.dp),
                    ) {
                        Text(
                            text = "BUDGET QUESTS",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = SakuraPink,
                            letterSpacing = 1.2.sp,
                        )
                        Text(
                            text = "Monthly Spending Plan",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.ExtraBold,
                        )
                        Text(
                            text = "Track your budget goals for ${uiState.monthLabel}.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }

                if (!uiState.hasBudgets) {
                    item {
                        PennyWiseCard(modifier = Modifier.fillMaxWidth()) {
                            AnimeEmptyStateIllustration(
                                title = "No Active Budget Quests!",
                                subtitle = "Create an overall monthly budget or category budget target to start tracking.",
                            )
                        }
                    }
                } else {
                    item {
                        val overall = uiState.overallBudget
                        val (message, emotion) = when {
                            overall == null -> "Set a monthly budget to keep Penny informed of your spending goals!" to CompanionEmotion.ENCOURAGING
                            overall.remaining < 0 -> "Attention! You've crossed your monthly budget goal. Time for a spending cool-down!" to CompanionEmotion.ALERT
                            overall.utilization >= 0.8f -> "Warning! You've used over 80% of your total budget. Watch out for impulse buys!" to CompanionEmotion.WARNING
                            else -> "Great discipline! Your budget quest is right on track this month!" to CompanionEmotion.JOY
                        }
                        AnimeSpeechBubbleCard(
                            message = message,
                            title = "QUEST STATUS",
                            emotion = emotion,
                        )
                    }

                    uiState.overallBudget?.let { summary ->
                        item {
                            BudgetSummaryCard(
                                title = "Overall Monthly Budget",
                                category = null,
                                summary = summary,
                                onEdit = { onEditBudget(summary.budget.id) },
                                onDelete = null,
                            )
                        }
                    }

                    if (uiState.categoryBudgets.isNotEmpty()) {
                        item {
                            Text(
                                text = "Category Budget Targets",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                            )
                        }

                        items(uiState.categoryBudgets, key = { it.budget.id }) { summary ->
                            BudgetSummaryCard(
                                title = summary.budget.category ?: "Category",
                                category = summary.budget.category,
                                summary = summary,
                                onEdit = { onEditBudget(summary.budget.id) },
                                onDelete = { pendingDelete = summary },
                            )
                        }
                    }
                }
                item { Column(modifier = Modifier.padding(bottom = 80.dp)) {} }
            }
        }
    }
}

@Composable
private fun BudgetSummaryCard(
    title: String,
    category: String?,
    summary: BudgetSummary,
    onEdit: () -> Unit,
    onDelete: (() -> Unit)?,
) {
    PennyWiseCard(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                category?.let {
                    AnimeCategoryBadge(category = it, size = 36.dp)
                }
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(start = if (category != null) 10.dp else 0.dp),
                )
            }
            Row {
                IconButton(onClick = onEdit) {
                    Icon(Icons.Outlined.Edit, contentDescription = "Edit $title budget", tint = SakuraPink)
                }
                onDelete?.let {
                    IconButton(onClick = it) {
                        Icon(Icons.Outlined.DeleteOutline, contentDescription = "Delete $title budget", tint = AnimeCoral.copy(alpha = 0.8f))
                    }
                }
            }
        }

        BudgetProgress(
            label = "Target Usage",
            spent = formatCurrency(summary.spent),
            limit = formatCurrency(summary.budget.amount),
            progress = summary.utilization,
            color = budgetStatusColor(summary),
            modifier = Modifier.padding(top = PennyWiseSpacing.standard),
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            val statusText = if (summary.remaining >= 0) {
                "${formatCurrency(summary.remaining)} remaining"
            } else {
                "${formatCurrency(-summary.remaining)} over budget!"
            }

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(budgetStatusColor(summary).copy(alpha = 0.15f))
                    .padding(horizontal = 8.dp, vertical = 4.dp),
            ) {
                Text(
                    text = statusText,
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = budgetStatusColor(summary),
                )
            }

            Text(
                text = "${(summary.utilization * 100).toInt()}% Used",
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

private fun budgetStatusColor(summary: BudgetSummary) =
    when {
        summary.remaining < 0 -> AnimeCoral
        summary.utilization >= 0.8f -> Color(0xFFFFB703)
        else -> AnimeEmerald
    }

private fun formatCurrency(amount: Double): String =
    NumberFormat.getCurrencyInstance(Locale("en", "IN")).apply {
        maximumFractionDigits = 0
        minimumFractionDigits = 0
    }.format(amount)
