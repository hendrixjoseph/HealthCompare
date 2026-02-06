package com.joehxblog.healthcompare

import java.time.LocalDate
import java.time.LocalDateTime

interface HealthFunctions {

    suspend fun aggregateSteps(start: LocalDateTime, end: LocalDateTime): Long
    suspend fun aggregateCalories(start: LocalDateTime, end: LocalDateTime): Double
    suspend fun getHourlyCalories(date: LocalDate): List<Pair<LocalDateTime, Double>>
}