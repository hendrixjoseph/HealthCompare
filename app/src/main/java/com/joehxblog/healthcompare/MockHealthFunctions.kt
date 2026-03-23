package com.joehxblog.healthcompare

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.joehxblog.healthcompare.chart.ChartData
import java.time.Duration
import java.time.LocalDate
import java.time.LocalDateTime
import kotlin.random.Random


class HealthFunctionsProvider : PreviewParameterProvider<HealthFunctions> {
    override val values = sequenceOf(MockHealthFunctions())
}

class ChartDataProvider : PreviewParameterProvider<ChartData> {
    override val values = sequenceOf(
        ChartData(
            MockHealthFunctions().getHourlyCaloriesToday(),
            MockHealthFunctions().getHourlyCaloriesYesterday(),

            )
    )
}

class MockHealthFunctions: HealthFunctions {

    val random = Random(1)

    override suspend fun aggregateSteps(
        start: LocalDateTime,
        end: LocalDateTime
    ): Long {
        val minutes = Duration.between(start, end).toMinutes()
        return 5321 * minutes / 1440
    }

    override suspend fun aggregateCalories(
        start: LocalDateTime,
        end: LocalDateTime
    ): Double {
        val minutes = Duration.between(start, end).toMinutes()
        return 2010.5 * minutes / 1440.0
    }

    override suspend fun getHourlyCalories(date: LocalDate): List<Double> {
        return getHourlyCaloriesToday()
    }

    override suspend fun getHourlySteps(date: LocalDate): List<Double> {
        return _getHourlySteps(24)
    }

    fun getHourlyCaloriesToday(): List<Double> {
        return _getHourlyCalories(13)
    }

    fun getHourlyCaloriesYesterday(): List<Double> {
        return _getHourlyCalories(24)
    }

    fun _getHourlySteps(hoursSoFar: Long): List<Double> {
        val results = mutableListOf<Double>()

        var runningTotal = 0.0

        for (quarter in 0..hoursSoFar * 4) {
            val wiggle = random.nextInt(60) - 30

            // Simulate realistic calorie burn pattern
            val hourlyBurn = when (quarter / 4) {
                in 0..5 -> 0     // sleeping/resting
                in 6..8 -> 80 + wiggle     // morning activity
                in 9..16 -> 120 + wiggle   // active daytime
                in 17..19 -> 150 + wiggle // workout/evening activity
                else -> 0       // wind down
            }

            runningTotal += (hourlyBurn / 4)

            results.add(runningTotal)
        }

        return results
    }

    fun _getHourlyCalories(hoursSoFar: Long): List<Double> {
        val results = mutableListOf<Double>()

        var runningTotal = 0.0

        for (quarter in 0..hoursSoFar * 4) {
            val wiggle = random.nextInt(60) - 30

            // Simulate realistic calorie burn pattern
            val hourlyBurn = when (quarter / 4) {
                in 0..5 -> 40.0 + wiggle     // sleeping/resting
                in 6..8 -> 80.0 + wiggle     // morning activity
                in 9..16 -> 120.0 + wiggle   // active daytime
                in 17..19 -> 150.0 + wiggle // workout/evening activity
                else -> 90.0 + wiggle       // wind down
            }

            runningTotal += (hourlyBurn / 4)

            results.add(runningTotal)
        }

        return results
    }
}