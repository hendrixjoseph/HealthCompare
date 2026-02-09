package com.joehxblog.healthcompare.chart

data class ChartData(
    val todayData: List<Double>,
    val yesterdayData: List<Double>,
) {
    fun isEmpty(): Boolean {
        return todayData.isEmpty() && yesterdayData.isEmpty()
    }
}
