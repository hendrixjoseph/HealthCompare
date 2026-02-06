package com.joehxblog.healthcompare

import java.time.LocalDateTime

interface HealthFunctions {

    suspend fun aggregateSteps(start: LocalDateTime, end: LocalDateTime): Long
    suspend fun aggregateCalories(start: LocalDateTime, end: LocalDateTime): Double
    suspend fun getHourlyCaloriesToday(): List<Pair<LocalDateTime, Double>>
}