package com.joehxblog.healthcompare

import android.util.Log
import androidx.health.connect.client.HealthConnectClient
import androidx.health.connect.client.aggregate.AggregateMetric
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
        return aggregate(start, end, StepsRecord.COUNT_TOTAL) ?: 0L
    }

    override suspend fun aggregateCalories(
        start: LocalDateTime,
        end: LocalDateTime
    ): Double {
        return aggregate(start, end, TotalCaloriesBurnedRecord.ENERGY_TOTAL)?.inKilocalories ?: 0.0
    }

    suspend fun <T : Any> aggregate(
        start: LocalDateTime,
        end: LocalDateTime,
        what: AggregateMetric<T>
    ): T? {
        val response = client.aggregate(
            AggregateRequest(
                metrics = setOf(what),
                timeRangeFilter = TimeRangeFilter.between(start, end)
            )
        )
        return response[what]
    }

    override suspend fun getHourlyCalories(date: LocalDate): List<Pair<LocalDateTime, Double>> {
        val startOfDay = date.atStartOfDay()
        val endOfDay = minOf(date.plusDays(1).atStartOfDay(), LocalDateTime.now())

        val hours = if (endOfDay.hour == 0) 24 else endOfDay.hour

        var runningTotal = 0.0

        var list = ArrayList<Pair<LocalDateTime, Double>>()

        list.add(Pair(startOfDay, 0.0));

        for (hour in 1..hours) {
            val currentTime = startOfDay.plusHours(hour.toLong())
            runningTotal += aggregateCalories(currentTime.minusHours(1), currentTime)
            list.add(Pair(currentTime, runningTotal))
        }

        Log.d("hourly calories", list.toString())

        return list
//
//        val response = client.readRecords(
//            ReadRecordsRequest(
//                recordType = TotalCaloriesBurnedRecord::class,
//                timeRangeFilter = TimeRangeFilter.between(startOfDay, endOfDay)
//            )
//        )
//
//        // Create hourly buckets
//        val hourlyBuckets = mutableMapOf<LocalDateTime, Double>()
//
//        response.records.forEach { record ->
//            val hour = record.startTime
//                .truncatedTo(ChronoUnit.HOURS)
//                .atZone(ZoneId.systemDefault())
//                .toLocalDateTime()
//
//            val calories = record.energy.inKilocalories
//            hourlyBuckets[hour] = (hourlyBuckets[hour] ?: 0.0) + calories
//        }
//
//        // Sort by time
//        val sorted = hourlyBuckets.toSortedMap()
//
//        // Convert to cumulative (increasing) list
////        var runningTotal = 0.0
//        return sorted.map { (time, value) ->
//            runningTotal += value
//            time to runningTotal
//        }
    }
}