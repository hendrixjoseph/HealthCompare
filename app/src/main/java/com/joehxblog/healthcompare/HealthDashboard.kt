package com.joehxblog.healthcompare

import android.graphics.Color
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import java.time.LocalDate
import java.time.LocalDateTime

@Preview(showBackground = false, showSystemUi = false, backgroundColor = 0xFF3F51B5)
@Composable
fun HealthDashboard(
    @PreviewParameter(HealthFunctionsProvider::class)
    healthFunctions: HealthFunctions
) {
    var todaySteps by remember { mutableLongStateOf(0L) }
    var yesterdaySteps by remember { mutableLongStateOf(0L) }
    var todayCalories by remember { mutableDoubleStateOf(0.0) }
    var yesterdayCalories by remember { mutableDoubleStateOf(0.0) }
    var weeklyAvgSteps by remember { mutableLongStateOf(0L) }
    var weeklyAvgCalories by remember { mutableDoubleStateOf(0.0) }

    var chartData by remember { mutableStateOf(ChartData(emptyList(), emptyList())) }


    LaunchedEffect(Unit) {

        val now = LocalDateTime.now()
        val startOfToday = LocalDate.now().atStartOfDay()
        val startOfYesterday = startOfToday.minusDays(1)

        todaySteps = healthFunctions.aggregateSteps(startOfToday, now)
        yesterdaySteps = healthFunctions.aggregateSteps(startOfYesterday, startOfYesterday.plusHours(now.hour.toLong()))

        todayCalories = healthFunctions.aggregateCalories(startOfToday, now)
        yesterdayCalories = healthFunctions.aggregateCalories(startOfYesterday, startOfYesterday.plusHours(now.hour.toLong()))

        val weekStart = startOfToday.minusDays(7)
        weeklyAvgSteps = healthFunctions.aggregateSteps(weekStart, startOfToday) / 7
        weeklyAvgCalories = healthFunctions.aggregateCalories(weekStart, startOfToday) / 7.0

        val today = healthFunctions.getHourlyCalories(LocalDate.now())
        val yesterday = healthFunctions.getHourlyCalories(LocalDate.now().minusDays(1))

        chartData = ChartData(today, yesterday)
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(
            space = 12.dp,
            alignment = Alignment.CenterVertically
        )
    ) {
        Text("Today vs Yesterday", style = MaterialTheme.typography.headlineSmall)
        Text("Steps today: $todaySteps")
        Text("Steps yesterday (same time): $yesterdaySteps")
        Text("Calories today: ${"%.0f".format(todayCalories)}")
        Text("Calories yesterday (same time): ${"%.0f".format(yesterdayCalories)}")
        HorizontalDivider()
        Text("7-Day Averages", style = MaterialTheme.typography.headlineSmall)
        Text("Avg steps/day: $weeklyAvgSteps")
        Text("Avg calories/day: ${"%.0f".format(weeklyAvgCalories)}")
        HorizontalDivider()
        CaloriesLineChart(data = chartData)
    }
}
