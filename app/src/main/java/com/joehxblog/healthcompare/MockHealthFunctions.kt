package com.joehxblog.healthcompare

import java.time.LocalDate
import java.time.LocalDateTime

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
        val now = LocalDateTime.now()
        val startOfDay = LocalDate.now().atStartOfDay()

        val hoursSoFar = now.hour.coerceAtLeast(1)

        val results = mutableListOf<Pair<LocalDateTime, Double>>()

        var runningTotal = 0.0

        for (hour in 0..hoursSoFar) {

            val time = startOfDay.plusHours(hour.toLong())

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