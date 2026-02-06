package com.joehxblog.healthcompare

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.health.connect.client.HealthConnectClient
import androidx.health.connect.client.PermissionController
import androidx.health.connect.client.permission.HealthPermission
import androidx.health.connect.client.records.StepsRecord
import androidx.health.connect.client.records.TotalCaloriesBurnedRecord
import androidx.health.connect.client.request.AggregateRequest
import androidx.health.connect.client.time.TimeRangeFilter
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import java.time.*


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val healthClient = HealthConnectClient.getOrCreate(this)

        val permissions = setOf(
            HealthPermission.getReadPermission(StepsRecord::class),
            HealthPermission.getReadPermission(TotalCaloriesBurnedRecord::class)
        )

        val requestPermissionActivityContract = PermissionController.createRequestPermissionResultContract()

        fun launch() {
            setContent {
                MaterialTheme {
                    Surface {
                        HealthDashboard(healthClient)
                    }
                }
            }
        }

        val requestPermissions = registerForActivityResult(requestPermissionActivityContract) { granted ->
            if (granted.containsAll(permissions)) {
                launch()
            } else {
                // Lack of required permissions
            }
        }

        suspend fun checkPermissionsAndRun(healthConnectClient: HealthConnectClient) {
            val granted = healthConnectClient.permissionController.getGrantedPermissions()
            if (granted.containsAll(permissions)) {
                launch()
            } else {
                requestPermissions.launch(permissions)
            }
        }

        lifecycleScope.launch {
            checkPermissionsAndRun(healthClient)
        }
    }
}

@Composable
fun HealthDashboard(client: HealthConnectClient) {
    var todaySteps by remember { mutableStateOf(0L) }
    var yesterdaySteps by remember { mutableStateOf(0L) }
    var todayCalories by remember { mutableStateOf(0.0) }
    var yesterdayCalories by remember { mutableStateOf(0.0) }
    var weeklyAvgSteps by remember { mutableStateOf(0L) }
    var weeklyAvgCalories by remember { mutableStateOf(0.0) }


    LaunchedEffect(Unit) {

        val now = LocalDateTime.now()
        val startOfToday = LocalDate.now().atStartOfDay()
        val startOfYesterday = startOfToday.minusDays(1)


        todaySteps = aggregateSteps(client, startOfToday, now)
        yesterdaySteps = aggregateSteps(client, startOfYesterday, startOfYesterday.plusHours(now.hour.toLong()))


        todayCalories = aggregateCalories(client, startOfToday, now)
        yesterdayCalories = aggregateCalories(client, startOfYesterday, startOfYesterday.plusHours(now.hour.toLong()))


        val weekStart = startOfToday.minusDays(7)
        weeklyAvgSteps = aggregateSteps(client, weekStart, startOfToday) / 7
        weeklyAvgCalories = aggregateCalories(client, weekStart, startOfToday) / 7.0
    }


    Column {
        Text("Today vs Yesterday", style = MaterialTheme.typography.headlineSmall)
        Text("Steps today: $todaySteps")
        Text("Steps yesterday (same time): $yesterdaySteps")
        Text("Calories today: ${"%.0f".format(todayCalories)}")
        Text("Calories yesterday (same time): ${"%.0f".format(yesterdayCalories)}")
        Divider()
        Text("7-Day Averages", style = MaterialTheme.typography.headlineSmall)
        Text("Avg steps/day: $weeklyAvgSteps")
        Text("Avg calories/day: ${"%.0f".format(weeklyAvgCalories)}")
    }
}


suspend fun aggregateSteps(
    client: HealthConnectClient,
    start: LocalDateTime,
    end: LocalDateTime
): Long {
    val response = client.aggregate(
        AggregateRequest(
            metrics = setOf(StepsRecord.COUNT_TOTAL),
            timeRangeFilter = TimeRangeFilter.between(start, end)
        )
    )
    return response[StepsRecord.COUNT_TOTAL] ?: 0L
}


suspend fun aggregateCalories(
    client: HealthConnectClient,
    start: LocalDateTime,
    end: LocalDateTime
): Double {
    val response = client.aggregate(
        AggregateRequest(
            metrics = setOf(TotalCaloriesBurnedRecord.ENERGY_TOTAL),
            timeRangeFilter = TimeRangeFilter.between(start, end)
        )
    )
    return response[TotalCaloriesBurnedRecord.ENERGY_TOTAL]?.inKilocalories ?: 0.0
}