package com.joehxblog.healthcompare

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import java.time.LocalDate
import java.time.LocalDateTime
import kotlin.random.Random

class HealthFunctionsProvider : PreviewParameterProvider<HealthFunctions> {
    override val values = listOf(MockHealthFunctions()).asSequence()
}

class ChartDataProvider : PreviewParameterProvider<ChartData> {
    override val values = listOf(
        ChartData(
        MockHealthFunctions().getHourlyCaloriesToday(),
        MockHealthFunctions().getHourlyCaloriesYesterday(),

        )).asSequence()
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

    override suspend fun getHourlyCalories(date: LocalDate): List<Pair<LocalDateTime, Double>> {
        return getHourlyCaloriesToday()
    }

    fun getHourlyCaloriesToday(): List<Pair<LocalDateTime, Double>> {
        return _getHourlyCalories(13)
    }

    fun getHourlyCaloriesYesterday(): List<Pair<LocalDateTime, Double>> {
        return _getHourlyCalories(24)
    }

    fun _getHourlyCalories(hoursSoFar: Long): List<Pair<LocalDateTime, Double>> {
        val startOfDay = LocalDate.now().atStartOfDay()

        val results = mutableListOf<Pair<LocalDateTime, Double>>()

        var runningTotal = 0.0

        for (hour in 0..hoursSoFar) {

            val time = startOfDay.plusHours(hour)

            val wiggle = Random(1).nextInt(20)

            // Simulate realistic calorie burn pattern
            val hourlyBurn = when (hour) {
                in 0..5 -> 40.0 + wiggle     // sleeping/resting
                in 6..8 -> 80.0 + wiggle     // morning activity
                in 9..16 -> 120.0 + wiggle   // active daytime
                in 17..19 -> 150.0 + wiggle // workout/evening activity
                else -> 90.0 + wiggle       // wind down
            }

            runningTotal += hourlyBurn

            results.add(time to runningTotal)
        }

        return results
    }


}