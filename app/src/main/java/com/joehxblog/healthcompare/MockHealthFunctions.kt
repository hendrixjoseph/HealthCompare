package com.joehxblog.healthcompare

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import java.time.LocalDate
import java.time.LocalDateTime

class HealthFunctionsProvider : PreviewParameterProvider<HealthFunctions> {
    override val values = listOf(MockHealthFunctions()).asSequence()
}

class ChartDataProvider : PreviewParameterProvider<List<Pair<LocalDateTime, Double>>> {
    override val values = listOf(MockHealthFunctions()._getHourlyCaloriesToday()).asSequence()
}

class MockHealthFunctions: HealthFunctions {
    override suspend fun aggregateSteps(
        start: LocalDateTime,
        end: LocalDateTime
    ): Long {
        return 5321
    }

    override suspend fun aggregateCalories(
        start: LocalDateTime,
        end: LocalDateTime
    ): Double {
        return 1200.0
    }

    override suspend fun getHourlyCaloriesToday(): List<Pair<LocalDateTime, Double>> {
        return _getHourlyCaloriesToday()
    }

    fun _getHourlyCaloriesToday(): List<Pair<LocalDateTime, Double>> {
        val startOfDay = LocalDate.of(2026, 2, 25).atStartOfDay()

        val hoursSoFar = 13L

        val results = mutableListOf<Pair<LocalDateTime, Double>>()

        var runningTotal = 0.0

        for (hour in 0..hoursSoFar) {

            val time = startOfDay.plusHours(hour)

            // Simulate realistic calorie burn pattern
            val hourlyBurn = when (hour) {
                in 0..5 -> 40.0     // sleeping/resting
                in 6..8 -> 80.0     // morning activity
                in 9..16 -> 120.0   // active daytime
                in 17..19 -> 150.0  // workout/evening activity
                else -> 90.0        // wind down
            }

            runningTotal += hourlyBurn

            results.add(time to runningTotal)
        }

        return results
    }


}