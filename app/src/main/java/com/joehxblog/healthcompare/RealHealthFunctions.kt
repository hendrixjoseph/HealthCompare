package com.joehxblog.healthcompare

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

class RealHealthFunctions(private val client: HealthConnectClient): HealthFunctions {
    override suspend fun aggregateSteps(
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

    override suspend fun aggregateCalories(
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

    override suspend fun getHourlyCalories(date: LocalDate): List<Pair<LocalDateTime, Double>> {
        val startOfDay = date.atStartOfDay()
        val endOfDay = minOf(date.plusDays(1).atStartOfDay(), LocalDateTime.now())

        val response = client.readRecords(
            ReadRecordsRequest(
                recordType = TotalCaloriesBurnedRecord::class,
                timeRangeFilter = TimeRangeFilter.between(startOfDay, endOfDay)
            )
        )

        // Create hourly buckets
        val hourlyBuckets = mutableMapOf<LocalDateTime, Double>()

        response.records.forEach { record ->
            val hour = record.startTime
                .truncatedTo(ChronoUnit.HOURS)
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime()

            val calories = record.energy.inKilocalories
            hourlyBuckets[hour] = (hourlyBuckets[hour] ?: 0.0) + calories
        }

        // Sort by time
        val sorted = hourlyBuckets.toSortedMap()

        // Convert to cumulative (increasing) list
        var runningTotal = 0.0
        return sorted.map { (time, value) ->
            runningTotal += value
            time to runningTotal
        }
    }
}