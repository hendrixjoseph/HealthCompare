package com.joehxblog.healthcompare.chart

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ChartData(
    val todayData: List<Double>,
    val yesterdayData: List<Double>,
) : Parcelable {

    fun isEmpty(): Boolean {
        return todayData.isEmpty() && yesterdayData.isEmpty()
    }
}
