package com.joehxblog.healthcompare

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.time.LocalDate
import java.time.LocalDateTime

@Preview(showBackground = true, showSystemUi = false, backgroundColor = 0xFFFFFF)
@Composable
fun HealthDashboard(
    @PreviewParameter(HealthFunctionsProvider::class)
    healthFunctions: HealthFunctions
) {
    var todaySteps by remember { mutableLongStateOf(0L) }
    var yesterdaySteps by remember { mutableLongStateOf(0L) }
    var todayCalories by remember { mutableLongStateOf(0L) }
    var yesterdayCalories by remember { mutableLongStateOf(0L) }
    var weeklyAvgSteps by remember { mutableLongStateOf(0L) }
    var weeklyAvgCalories by remember { mutableLongStateOf(0L) }

    var chartData by remember { mutableStateOf(ChartData(emptyList(), emptyList())) }

    var isRefreshing by remember { mutableStateOf(false) }

    var now by remember { mutableStateOf(LocalDateTime.now()) }

    val scope = rememberCoroutineScope()

    suspend fun refresh() {
        now = LocalDateTime.now()
        val startOfToday = LocalDate.now().atStartOfDay()
        val startOfYesterday = startOfToday.minusDays(1)

        todaySteps = healthFunctions.aggregateSteps(startOfToday, now)
        yesterdaySteps = healthFunctions.aggregateSteps(startOfYesterday, startOfYesterday.plusHours(now.hour.toLong()))

        todayCalories = healthFunctions.aggregateCalories(startOfToday, now).toLong()
        yesterdayCalories = healthFunctions.aggregateCalories(startOfYesterday, startOfYesterday.plusHours(now.hour.toLong())).toLong()

        val weekStart = startOfToday.minusDays(7)
        weeklyAvgSteps = healthFunctions.aggregateSteps(weekStart, startOfToday) / 7
        weeklyAvgCalories = healthFunctions.aggregateCalories(weekStart, startOfToday).toLong() / 7

        val today = healthFunctions.getHourlyCalories(LocalDate.now())
        val yesterday = healthFunctions.getHourlyCalories(LocalDate.now().minusDays(1))

        chartData = ChartData(today, yesterday)
    }

    LaunchedEffect(Unit) {
        refresh()
    }

    val scrollState = rememberScrollState()

    val nf = NumberFormat.getNumberInstance()

    PullToRefreshBox(
        isRefreshing = isRefreshing,
        onRefresh = {
            scope.launch {
                isRefreshing = true
                refresh()
                isRefreshing = false
            }
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .padding(16.dp)
                .verticalScroll(scrollState),
            verticalArrangement = Arrangement.spacedBy(
                space = 12.dp,
                alignment = Alignment.CenterVertically
            )
        ) {
            Text("Last Updated: $now")
            Text("Today vs Yesterday", style = MaterialTheme.typography.headlineSmall)
            Text("Steps today: ${nf.format(todaySteps)}")
            Text("Steps yesterday (same time): ${nf.format(yesterdaySteps)}")
            Text("Calories today: ${nf.format(todayCalories)}")
            Text("Calories yesterday (same time): ${nf.format(yesterdayCalories)}")
            HorizontalDivider()
            Text("7-Day Averages", style = MaterialTheme.typography.headlineSmall)
            Text("Avg steps/day: ${nf.format(weeklyAvgSteps)}")
            Text("Avg calories/day: ${nf.format(weeklyAvgCalories)}")
            HorizontalDivider()
            CaloriesLineChart(data = chartData)
        }
    }
}
