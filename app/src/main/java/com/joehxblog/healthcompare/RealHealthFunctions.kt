package com.joehxblog.healthcompare

import androidx.health.connect.client.HealthConnectClient
import androidx.health.connect.client.aggregate.AggregateMetric
import androidx.health.connect.client.records.StepsRecord
import androidx.health.connect.client.records.TotalCaloriesBurnedRecord
import androidx.health.connect.client.request.AggregateRequest
import androidx.health.connect.client.time.TimeRangeFilter
import java.time.LocalDate
import java.time.LocalDateTime

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

    override suspend fun getHourlyCalories(date: LocalDate): List<Double> {
        return getHourly(date, this::aggregateCalories)
    }

    override suspend fun getHourlySteps(date: LocalDate): List<Double> {
        return getHourly(date, this::aggregateSteps)
    }

    suspend fun getHourly(date: LocalDate, aggregate: suspend (LocalDateTime, LocalDateTime) -> Number): List<Double> {
        val startOfDay = date.atStartOfDay()
        val endOfDay = minOf(date.plusDays(1).atStartOfDay(), LocalDateTime.now())

        val hours = if (endOfDay.hour == 0) 24 else endOfDay.hour

        var runningTotal = 0.0

        val list = mutableListOf<Double>()

        list.add(0.0);

        for (hour in 1..hours) {
            val currentTime = startOfDay.plusHours(hour.toLong())
            runningTotal += aggregate(currentTime.minusHours(1), currentTime).toDouble()
            list.add(runningTotal)
        }

        return list
    }
}