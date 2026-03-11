package com.joehxblog.healthcompare

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import com.joehxblog.healthcompare.chart.ChartData
import com.joehxblog.healthcompare.chart.LineChart
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.Locale

@Preview(showBackground = true, showSystemUi = false, backgroundColor = 0xFFFFFF)
@Composable
fun HealthDashboard(
    @PreviewParameter(HealthFunctionsProvider::class)
    healthFunctions: HealthFunctions
) {
    var todaySteps by rememberSaveable { mutableLongStateOf(0L) }
    var yesterdaySteps by rememberSaveable { mutableLongStateOf(0L) }
    var todayCalories by rememberSaveable { mutableLongStateOf(0L) }
    var yesterdayCalories by rememberSaveable { mutableLongStateOf(0L) }
    var weeklyAvgSteps by rememberSaveable { mutableLongStateOf(0L) }
    var weeklyAvgCalories by rememberSaveable { mutableLongStateOf(0L) }

    var calorieChartData by rememberSaveable {
        mutableStateOf(ChartData(emptyList(), emptyList()))
    }
    var stepChartData by rememberSaveable {
        mutableStateOf(ChartData(emptyList(), emptyList()))
    }

    var isRefreshing by rememberSaveable { mutableStateOf(true) }

    var now by rememberSaveable { mutableStateOf(LocalDateTime.now()) }

    val formatter = DateTimeFormatter
        .ofLocalizedDateTime(FormatStyle.MEDIUM)
        .withLocale(Locale.getDefault())

    val scope = rememberCoroutineScope()

    suspend fun refresh() {
        isRefreshing = true

        now = LocalDateTime.now()
        val startOfToday = now.toLocalDate().atStartOfDay()
        val startOfYesterday = startOfToday.minusDays(1)
        val sameTimeYesterday = now.minusDays(1)

        todaySteps = healthFunctions.aggregateSteps(startOfToday, now)
        yesterdaySteps = healthFunctions.aggregateSteps(startOfYesterday, sameTimeYesterday)

        todayCalories = healthFunctions.aggregateCalories(startOfToday, now).toLong()
        yesterdayCalories = healthFunctions.aggregateCalories(startOfYesterday, sameTimeYesterday).toLong()

        val weekStart = startOfToday.minusDays(7)
        weeklyAvgSteps = healthFunctions.aggregateSteps(weekStart, startOfToday) / 7
        weeklyAvgCalories = healthFunctions.aggregateCalories(weekStart, startOfToday).toLong() / 7

        val today = now.toLocalDate()
        val yesterday = today.minusDays(1)

        calorieChartData = ChartData(
            healthFunctions.getHourlyCalories(today),
            healthFunctions.getHourlyCalories(yesterday)
        )

        stepChartData = ChartData(
            healthFunctions.getHourlySteps(today),
            healthFunctions.getHourlySteps(yesterday)
        )

        isRefreshing = false
    }

    LaunchedEffect(Unit) {
        if (calorieChartData.isEmpty()) {
            Log.i("refresh", "from LaunchedEffect")
            refresh()
        }
    }

    val scrollState = rememberScrollState()

    PullToRefreshBox(
        isRefreshing = isRefreshing,
        onRefresh = {
            scope.launch {
                Log.i("refresh", "from PullToRefreshBox")
                refresh()
            }
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .padding(16.dp)
                .statusBarsPadding()
                .verticalScroll(scrollState),
            verticalArrangement = Arrangement.spacedBy(
                space = 12.dp,
                alignment = Alignment.CenterVertically
            )
        ) {
            Text("Last Updated: ${now.format(formatter)}")

            HorizontalDivider()
            HealthChart(
                "Calories Burned",
                "kCal",
                todayCalories,
                yesterdayCalories,
                weeklyAvgCalories,
                calorieChartData,
                isRefreshing
            )
            HorizontalDivider()
            HealthChart(
                "Steps Taken",
                "Steps",
                todaySteps,
                yesterdaySteps,
                weeklyAvgSteps,
                stepChartData,
                isRefreshing
            )
            HorizontalDivider()
            Button(
                onClick = {
                    scope.launch {
                        Log.i("refresh", "from Button")
                        refresh()
                    }
                },
                enabled = !isRefreshing,
                modifier = Modifier.fillMaxWidth()
            ) {
                if (isRefreshing) {
                    CircularProgressIndicator()
                } else {
                    Text("Refresh")
                }
            }
        }
    }
}

@Composable
fun HealthChart(
    title: String,
    yAxisLabel: String,
    today: Number,
    yesterday: Number,
    weekly: Number,
    chartData: ChartData,
    isRefreshing: Boolean
) {
    val nf = NumberFormat.getNumberInstance()

    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(title, style = MaterialTheme.typography.headlineSmall)
        if (isRefreshing) {
            Spacer(
                Modifier.width(5.dp)
            )
            LinearProgressIndicator()
        }
    }
    Row {
        Text("Today", Modifier.weight(1.0F))
        Text("Yesterday", Modifier.weight(1.0F))
        Text("Weekly", Modifier.weight(1.0F))
    }
    Row {
        Text(nf.format(today), Modifier.weight(1.0F))
        Text(nf.format(yesterday), Modifier.weight(1.0F))
        Text(nf.format(weekly), Modifier.weight(1.0F))
    }
    LineChart(
        data = chartData,
        yAxisLabel = yAxisLabel
    )
}
