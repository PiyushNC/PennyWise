package com.pennywise.app.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowDownward
import androidx.compose.material.icons.outlined.ArrowUpward
import androidx.compose.material.icons.outlined.ChevronLeft
import androidx.compose.material.icons.outlined.ChevronRight
import androidx.compose.material.icons.outlined.MilitaryTech
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pennywise.app.ui.anime.AnimeEmptyStateIllustration
import com.pennywise.app.ui.components.AmountDisplay
import com.pennywise.app.ui.components.PennyWiseCard
import com.pennywise.app.ui.theme.AnimeAtmosphericBackground
import com.pennywise.app.ui.theme.AnimeCoral
import com.pennywise.app.ui.theme.AnimeEmerald
import com.pennywise.app.ui.theme.ElectricViolet
import com.pennywise.app.ui.theme.NeonCyan
import com.pennywise.app.ui.theme.PennyWiseSpacing
import com.pennywise.app.ui.theme.SakuraPink
import com.pennywise.app.ui.theme.SoftLavender
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
    AnimeAtmosphericBackground(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = PennyWiseSpacing.screen, vertical = PennyWiseSpacing.large),
            verticalArrangement = Arrangement.spacedBy(PennyWiseSpacing.section),
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(
                    text = "ANIME STATS",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = SakuraPink,
                    letterSpacing = 1.2.sp,
                )
                Text(
                    text = "Financial Battle Report",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.ExtraBold,
                )
                Text(
                    text = "Analyze your spending patterns with game-like analytics.",
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
                PennyWiseCard(modifier = Modifier.fillMaxWidth()) {
                    AnimeEmptyStateIllustration(
                        title = "No Battle Stats Available",
                        subtitle = "Log your income and expenses for this month to generate analytical charts.",
                    )
                }
            } else {
                SummaryMetrics(uiState = uiState)
                CategoryDistributionCard(categories = uiState.categoryDistribution)
                IncomeExpenseCard(uiState = uiState)
                TrendCard(
                    title = "Weekly Expenditure",
                    subtitle = "Weekly spending breakdown",
                    points = uiState.weeklySpending,
                    line = false,
                )
                TrendCard(
                    title = "6-Month Trend Curve",
                    subtitle = "Monthly trajectory comparison",
                    points = uiState.monthlySpending,
                    line = true,
                )
            }
            Spacer(modifier = Modifier.height(72.dp))
        }
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
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .border(1.dp, Brush.horizontalGradient(listOf(SakuraPink.copy(alpha = 0.5f), ElectricViolet.copy(alpha = 0.5f))), RoundedCornerShape(16.dp))
            .padding(horizontal = 8.dp, vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        IconButton(onClick = onPrevious) {
            Icon(Icons.Outlined.ChevronLeft, contentDescription = "Previous month", tint = SakuraPink)
        }
        Text(
            text = month.format(java.time.format.DateTimeFormatter.ofPattern("MMMM yyyy", Locale.getDefault())).uppercase(),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.ExtraBold,
            letterSpacing = 1.sp,
        )
        IconButton(onClick = onNext) {
            Icon(Icons.Outlined.ChevronRight, contentDescription = "Next month", tint = SakuraPink)
        }
    }
}

@Composable
private fun SummaryMetrics(uiState: AnalyticsUiState) {
    Row(horizontalArrangement = Arrangement.spacedBy(PennyWiseSpacing.standard)) {
        PennyWiseCard(modifier = Modifier.weight(1f)) {
            AmountDisplay(amount = formatCurrency(uiState.totalIncome), label = "Total Income")
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(top = 6.dp),
            ) {
                Icon(Icons.Outlined.ArrowUpward, contentDescription = null, tint = AnimeEmerald, modifier = Modifier.size(18.dp))
                Text("Income Logged", style = MaterialTheme.typography.labelSmall, color = AnimeEmerald, fontWeight = FontWeight.Bold)
            }
        }
        PennyWiseCard(modifier = Modifier.weight(1f)) {
            AmountDisplay(amount = formatCurrency(uiState.totalExpenses), label = "Total Expenses")
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(top = 6.dp),
            ) {
                Icon(Icons.Outlined.ArrowDownward, contentDescription = null, tint = AnimeCoral, modifier = Modifier.size(18.dp))
                Text("Expenses Out", style = MaterialTheme.typography.labelSmall, color = AnimeCoral, fontWeight = FontWeight.Bold)
            }
        }
    }

    Row(
        modifier = Modifier.padding(top = PennyWiseSpacing.standard),
        horizontalArrangement = Arrangement.spacedBy(PennyWiseSpacing.standard),
    ) {
        InsightMetricCard(
            label = "Highest Spender",
            value = uiState.highestSpendingCategory?.name ?: "—",
            icon = Icons.Outlined.MilitaryTech,
            color = SakuraPink,
            modifier = Modifier.weight(1f),
        )
        InsightMetricCard(
            label = "Daily Average",
            value = formatCurrency(uiState.averageDailySpending),
            icon = null,
            color = NeonCyan,
            modifier = Modifier.weight(1f),
        )
    }
}

@Composable
private fun InsightMetricCard(
    label: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector?,
    color: Color,
    modifier: Modifier = Modifier,
) {
    PennyWiseCard(modifier = modifier) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f)) {
                Text(label.uppercase(), style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant, fontWeight = FontWeight.Bold)
                Text(value, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.ExtraBold, color = color)
            }
            icon?.let {
                Icon(it, contentDescription = null, tint = color, modifier = Modifier.size(24.dp))
            }
        }
    }
}

@Composable
private fun CategoryDistributionCard(categories: List<AnalyticsCategory>) {
    PennyWiseCard(modifier = Modifier.fillMaxWidth()) {
        Text("Category Breakdown", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = PennyWiseSpacing.large),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            DonutChart(categories = categories, modifier = Modifier.size(140.dp))
            Column(
                modifier = Modifier.padding(start = PennyWiseSpacing.large),
                verticalArrangement = Arrangement.spacedBy(8.dp),
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
        Box(
            modifier = Modifier
                .size(10.dp)
                .clip(CircleShape)
                .background(Color(category.color)),
        )
        Text(
            text = category.name,
            modifier = Modifier
                .padding(start = 8.dp)
                .weight(1f),
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold,
        )
        Text(
            text = "${(category.percentage * 100).toInt()}%",
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.Bold,
            color = SakuraPink,
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
                style = Stroke(width = 24.dp.toPx(), cap = StrokeCap.Butt),
            )
            startAngle += sweep
        }
    }
}

@Composable
private fun IncomeExpenseCard(uiState: AnalyticsUiState) {
    PennyWiseCard(modifier = Modifier.fillMaxWidth()) {
        Text("Income vs Expense Battle", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
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
        ComparisonBar("Income", income, maximum, AnimeEmerald)
        ComparisonBar("Expenses", expenses, maximum, AnimeCoral)
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
            Text(label, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
            Text(formatCurrency(value), style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold, color = color)
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(10.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.surfaceVariant),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize((value / maximum).toFloat().coerceIn(0f, 1f))
                    .clip(CircleShape)
                    .background(color),
            )
        }
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
        Text(title, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        Text(
            subtitle,
            modifier = Modifier.padding(top = 2.dp),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        if (line) {
            LineTrendChart(points = points, modifier = Modifier.padding(top = PennyWiseSpacing.large).fillMaxWidth().height(160.dp))
        } else {
            WeeklyBarChart(points = points, modifier = Modifier.padding(top = PennyWiseSpacing.large).fillMaxWidth().height(160.dp))
        }
    }
}

@Composable
private fun WeeklyBarChart(points: List<AnalyticsPoint>, modifier: Modifier = Modifier) {
    val primary = SakuraPink
    val bottomPadding = 24.dp
    val topPadding = 28.dp
    Canvas(modifier = modifier) {
        val maximum = points.maxOfOrNull { it.amount }?.coerceAtLeast(1.0) ?: 1.0
        val slot = size.width / points.size
        points.forEachIndexed { index, point ->
            val height = (point.amount / maximum).toFloat() * (size.height - topPadding.toPx())
            drawRoundRect(
                brush = Brush.verticalGradient(listOf(primary, ElectricViolet)),
                topLeft = Offset(index * slot + slot * 0.25f, size.height - height - bottomPadding.toPx()),
                size = androidx.compose.ui.geometry.Size(slot * 0.5f, height),
                cornerRadius = androidx.compose.ui.geometry.CornerRadius(8.dp.toPx()),
            )
        }
    }
}

@Composable
private fun LineTrendChart(points: List<AnalyticsPoint>, modifier: Modifier = Modifier) {
    val primary = NeonCyan
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
            drawCircle(primary, 6.dp.toPx(), Offset(x, y))
            drawCircle(Color.White, 3.dp.toPx(), Offset(x, y))
        }
    }
}

private fun formatCurrency(amount: Double): String =
    NumberFormat.getCurrencyInstance(Locale("en", "IN")).apply {
        maximumFractionDigits = 0
        minimumFractionDigits = 0
    }.format(amount)
