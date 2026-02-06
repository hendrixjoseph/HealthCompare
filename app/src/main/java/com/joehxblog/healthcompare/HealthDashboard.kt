package com.joehxblog.healthcompare

import androidx.compose.foundation.layout.Column
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
import androidx.health.connect.client.HealthConnectClient
import androidx.health.connect.client.records.StepsRecord
import androidx.health.connect.client.records.TotalCaloriesBurnedRecord
import androidx.health.connect.client.request.AggregateRequest
import androidx.health.connect.client.request.ReadRecordsRequest
import androidx.health.connect.client.time.TimeRangeFilter
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.temporal.ChronoUnit

@Composable
fun HealthDashboard(healthFunctions: HealthFunctions) {
    var todaySteps by remember { mutableLongStateOf(0L) }
    var yesterdaySteps by remember { mutableLongStateOf(0L) }
    var todayCalories by remember { mutableDoubleStateOf(0.0) }
    var yesterdayCalories by remember { mutableDoubleStateOf(0.0) }
    var weeklyAvgSteps by remember { mutableLongStateOf(0L) }
    var weeklyAvgCalories by remember { mutableDoubleStateOf(0.0) }

    var chartData by remember { mutableStateOf<List<Pair<LocalDateTime, Double>>>(emptyList()) }


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

        chartData = healthFunctions.getHourlyCaloriesToday()
    }

    var i = 0;

    Column {
        Text((i++).toString())
        Text((i++).toString())
        Text((i++).toString())
        Text((i++).toString())
        Text((i++).toString())
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
