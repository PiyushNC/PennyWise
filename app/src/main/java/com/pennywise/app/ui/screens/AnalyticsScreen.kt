package com.pennywise.app.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.ArrowDownward
import androidx.compose.material.icons.outlined.ArrowUpward
import androidx.compose.material.icons.outlined.ChevronLeft
import androidx.compose.material.icons.outlined.ChevronRight
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.pennywise.app.ui.components.AmountDisplay
import com.pennywise.app.ui.components.PennyWiseCard
import com.pennywise.app.ui.theme.PennyWiseSpacing
import com.pennywise.app.ui.theme.PennyWiseTheme
import com.pennywise.app.viewmodel.AnalyticsCategory
import com.pennywise.app.viewmodel.AnalyticsPoint
import com.pennywise.app.viewmodel.AnalyticsUiState
import com.pennywise.app.viewmodel.AnalyticsViewModel
import java.text.NumberFormat
import java.time.YearMonth
import java.util.Locale
import kotlin.math.max

@Composable
fun AnalyticsScreen(
    viewModel: AnalyticsViewModel,
) {
    val uiState by viewModel.uiState.collectAsState()
    AnalyticsContent(
        uiState = uiState,
        onPreviousMonth = viewModel::previousMonth,
        onNextMonth = viewModel::nextMonth,
    )
}

@Composable
private fun AnalyticsContent(
    uiState: AnalyticsUiState,
    onPreviousMonth: () -> Unit,
    onNextMonth: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = PennyWiseSpacing.screen, vertical = PennyWiseSpacing.large),
        verticalArrangement = Arrangement.spacedBy(PennyWiseSpacing.section),
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(PennyWiseSpacing.compact)) {
            Text("Analytics", style = MaterialTheme.typography.headlineSmall)
            Text(
                "Understand the patterns behind your money.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        MonthSelector(
            month = uiState.selectedMonth,
            onPrevious = onPreviousMonth,
            onNext = onNextMonth,
        )
        if (!uiState.hasData) {
            EmptyAnalyticsState()
        } else {
            SummaryMetrics(uiState = uiState)
            CategoryDistributionCard(categories = uiState.categoryDistribution)
            IncomeExpenseCard(uiState = uiState)
            TrendCard(
                title = "Weekly spending",
                subtitle = "Expense totals by week",
                points = uiState.weeklySpending,
                line = false,
            )
            TrendCard(
                title = "Monthly spending",
                subtitle = "Expense totals over the last six months",
                points = uiState.monthlySpending,
                line = true,
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
private fun MonthSelector(
    month: YearMonth,
    onPrevious: () -> Unit,
    onNext: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface, MaterialTheme.shapes.small)
            .padding(horizontal = 8.dp, vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        IconButton(onClick = onPrevious) {
            Icon(Icons.Outlined.ChevronLeft, contentDescription = "Previous month")
        }
        Text(
            text = month.format(java.time.format.DateTimeFormatter.ofPattern("MMMM yyyy", Locale.getDefault())),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
        )
        IconButton(onClick = onNext) {
            Icon(Icons.Outlined.ChevronRight, contentDescription = "Next month")
        }
    }
}

@Composable
private fun SummaryMetrics(uiState: AnalyticsUiState) {
    Row(horizontalArrangement = Arrangement.spacedBy(PennyWiseSpacing.standard)) {
        PennyWiseCard(modifier = Modifier.weight(1f)) {
            AmountDisplay(amount = formatCurrency(uiState.totalIncome), label = "Total income")
            Icon(
                imageVector = Icons.Outlined.ArrowUpward,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(top = PennyWiseSpacing.small),
            )
        }
        PennyWiseCard(modifier = Modifier.weight(1f)) {
            AmountDisplay(amount = formatCurrency(uiState.totalExpenses), label = "Total expenses")
            Icon(
                imageVector = Icons.Outlined.ArrowDownward,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(top = PennyWiseSpacing.small),
            )
        }
    }
    Row(
        modifier = Modifier.padding(top = PennyWiseSpacing.standard),
        horizontalArrangement = Arrangement.spacedBy(PennyWiseSpacing.standard),
    ) {
        InsightMetric(
            label = "Highest category",
            value = uiState.highestSpendingCategory?.name ?: "—",
            modifier = Modifier.weight(1f),
        )
        InsightMetric(
            label = "Average per day",
            value = formatCurrency(uiState.averageDailySpending),
            modifier = Modifier.weight(1f),
        )
    }
}

@Composable
private fun InsightMetric(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        Text(label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(value, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
    }
}

@Composable
private fun CategoryDistributionCard(categories: List<AnalyticsCategory>) {
    PennyWiseCard(modifier = Modifier.fillMaxWidth()) {
        Text("Expense distribution", style = MaterialTheme.typography.titleLarge)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = PennyWiseSpacing.large),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            DonutChart(categories = categories, modifier = Modifier.size(150.dp))
            Column(
                modifier = Modifier.padding(start = PennyWiseSpacing.large),
                verticalArrangement = Arrangement.spacedBy(PennyWiseSpacing.small),
            ) {
                categories.take(5).forEach { category ->
                    LegendRow(category)
                }
            }
        }
    }
}

@Composable
private fun LegendRow(category: AnalyticsCategory) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Spacer(
            modifier = Modifier
                .size(9.dp)
                .background(Color(category.color), CircleShape),
        )
        Text(
            text = category.name,
            modifier = Modifier.padding(start = 8.dp).weight(1f),
            style = MaterialTheme.typography.bodyMedium,
        )
        Text(
            text = "${(category.percentage * 100).toInt()}%",
            style = MaterialTheme.typography.labelLarge,
        )
    }
}

@Composable
private fun DonutChart(
    categories: List<AnalyticsCategory>,
    modifier: Modifier = Modifier,
) {
    Canvas(modifier = modifier) {
        var startAngle = -90f
        categories.forEach { category ->
            val sweep = category.percentage * 360f
            drawArc(
                color = Color(category.color),
                startAngle = startAngle,
                sweepAngle = sweep,
                useCenter = false,
                style = Stroke(width = 28.dp.toPx(), cap = StrokeCap.Butt),
            )
            startAngle += sweep
        }
    }
}

@Composable
private fun IncomeExpenseCard(uiState: AnalyticsUiState) {
    PennyWiseCard(modifier = Modifier.fillMaxWidth()) {
        Text("Income vs expenses", style = MaterialTheme.typography.titleLarge)
        ComparisonBars(
            income = uiState.totalIncome,
            expenses = uiState.totalExpenses,
            modifier = Modifier.padding(top = PennyWiseSpacing.large),
        )
    }
}

@Composable
private fun ComparisonBars(
    income: Double,
    expenses: Double,
    modifier: Modifier = Modifier,
) {
    val maximum = max(income, expenses).coerceAtLeast(1.0)
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(PennyWiseSpacing.standard)) {
        ComparisonBar("Income", income, maximum, MaterialTheme.colorScheme.primary)
        ComparisonBar("Expenses", expenses, maximum, MaterialTheme.colorScheme.error)
    }
}

@Composable
private fun ComparisonBar(
    label: String,
    value: Double,
    maximum: Double,
    color: Color,
) {
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(label, style = MaterialTheme.typography.bodyMedium)
            Text(formatCurrency(value), style = MaterialTheme.typography.labelLarge)
        }
        androidx.compose.material3.LinearProgressIndicator(
            progress = { (value / maximum).toFloat() },
            modifier = Modifier.fillMaxWidth().height(8.dp),
            color = color,
            trackColor = MaterialTheme.colorScheme.surfaceVariant,
        )
    }
}

@Composable
private fun TrendCard(
    title: String,
    subtitle: String,
    points: List<AnalyticsPoint>,
    line: Boolean,
) {
    PennyWiseCard(modifier = Modifier.fillMaxWidth()) {
        Text(title, style = MaterialTheme.typography.titleLarge)
        Text(
            subtitle,
            modifier = Modifier.padding(top = 4.dp),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        if (line) {
            LineTrendChart(points = points, modifier = Modifier.padding(top = PennyWiseSpacing.large).fillMaxWidth().height(170.dp))
        } else {
            WeeklyBarChart(points = points, modifier = Modifier.padding(top = PennyWiseSpacing.large).fillMaxWidth().height(170.dp))
        }
    }
}

@Composable
private fun WeeklyBarChart(points: List<AnalyticsPoint>, modifier: Modifier = Modifier) {
    val primary = MaterialTheme.colorScheme.primary
    val bottomPadding = 24.dp
    val topPadding = 28.dp
    Canvas(modifier = modifier) {
        val maximum = points.maxOfOrNull { it.amount }?.coerceAtLeast(1.0) ?: 1.0
        val slot = size.width / points.size
        points.forEachIndexed { index, point ->
            val height = (point.amount / maximum).toFloat() * (size.height - topPadding.toPx())
            drawRoundRect(
                color = primary,
                topLeft = Offset(index * slot + slot * 0.25f, size.height - height - bottomPadding.toPx()),
                size = androidx.compose.ui.geometry.Size(slot * 0.5f, height),
                cornerRadius = androidx.compose.ui.geometry.CornerRadius(8.dp.toPx()),
            )
        }
    }
}

@Composable
private fun LineTrendChart(points: List<AnalyticsPoint>, modifier: Modifier = Modifier) {
    val primary = MaterialTheme.colorScheme.primary
    val chartPadding = 20.dp
    Canvas(modifier = modifier) {
        val maximum = points.maxOfOrNull { it.amount }?.coerceAtLeast(1.0) ?: 1.0
        val xStep = if (points.size > 1) size.width / (points.size - 1) else size.width
        val path = Path()
        points.forEachIndexed { index, point ->
            val x = index * xStep
            val y = size.height - (point.amount / maximum).toFloat() * (size.height - chartPadding.toPx())
            if (index == 0) path.moveTo(x, y) else path.lineTo(x, y)
        }
        drawPath(path, primary, style = Stroke(width = 4.dp.toPx(), cap = StrokeCap.Round))
        points.forEachIndexed { index, point ->
            val x = index * xStep
            val y = size.height - (point.amount / maximum).toFloat() * (size.height - chartPadding.toPx())
            drawCircle(primary, 5.dp.toPx(), Offset(x, y))
        }
    }
}

@Composable
private fun EmptyAnalyticsState() {
    PennyWiseCard(modifier = Modifier.fillMaxWidth()) {
        Text("Not enough data yet", style = MaterialTheme.typography.titleLarge)
        Text(
            "Add income or expense transactions for this month to unlock spending insights.",
            modifier = Modifier.padding(top = PennyWiseSpacing.compact),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

private fun formatCurrency(amount: Double): String =
    NumberFormat.getCurrencyInstance(Locale("en", "IN")).apply {
        maximumFractionDigits = 0
        minimumFractionDigits = 0
    }.format(amount)

@Preview(showBackground = true)
@Composable
private fun AnalyticsPreview() {
    PennyWiseTheme {
        AnalyticsContent(
            uiState = AnalyticsUiState(
                selectedMonth = YearMonth.now(),
                totalIncome = 96_500.0,
                totalExpenses = 42_680.0,
                categoryDistribution = listOf(
                    AnalyticsCategory("Food", 8_420.0, 0.2f, 0xFFE59B54),
                    AnalyticsCategory("Home", 18_000.0, 0.42f, 0xFF1F9D72),
                ),
                weeklySpending = listOf(
                    AnalyticsPoint("W1", 5_000.0),
                    AnalyticsPoint("W2", 12_000.0),
                    AnalyticsPoint("W3", 9_000.0),
                    AnalyticsPoint("W4", 16_680.0),
                ),
                monthlySpending = listOf(
                    AnalyticsPoint("Jun", 30_000.0),
                    AnalyticsPoint("Jul", 36_000.0),
                    AnalyticsPoint("Aug", 42_000.0),
                    AnalyticsPoint("Sep", 39_000.0),
                    AnalyticsPoint("Oct", 46_000.0),
                    AnalyticsPoint("Nov", 42_680.0),
                ),
                highestSpendingCategory = AnalyticsCategory("Home", 18_000.0, 0.42f, 0xFF1F9D72),
                averageDailySpending = 1_422.0,
                hasData = true,
            ),
            onPreviousMonth = {},
            onNextMonth = {},
        )
    }
}
