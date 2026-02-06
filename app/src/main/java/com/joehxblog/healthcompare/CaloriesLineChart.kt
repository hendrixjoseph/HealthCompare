package com.joehxblog.healthcompare


import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import com.patrykandpatrick.vico.compose.axis.horizontal.rememberBottomAxis
import com.patrykandpatrick.vico.compose.axis.vertical.rememberStartAxis
import com.patrykandpatrick.vico.compose.chart.Chart
import com.patrykandpatrick.vico.compose.chart.line.lineChart
import com.patrykandpatrick.vico.compose.chart.line.lineSpec
import com.patrykandpatrick.vico.compose.component.textComponent
import com.patrykandpatrick.vico.core.chart.DefaultPointConnector
import com.patrykandpatrick.vico.core.entry.ChartEntryModel
import com.patrykandpatrick.vico.core.entry.entryModelOf
import java.time.LocalDateTime

data class ChartData(
    val todayData: List<Pair<LocalDateTime, Double>>,
    val yesterdayData: List<Pair<LocalDateTime, Double>>,
) {
    fun isEmpty(): Boolean {
        return todayData.isEmpty() && yesterdayData.isEmpty()
    }

    fun getModel(): ChartEntryModel {
        return entryModelOf(
            *mapData(todayData),
            *mapData(yesterdayData)
        )
    }

    private fun mapData(data: List<Pair<LocalDateTime, Double>>): Array<Pair<Float, Float>> {
        return data.mapIndexed { index, pair ->
            index.toFloat() to pair.second.toFloat()
        }.toTypedArray()
    }
}

@Preview
@Composable
fun CaloriesLineChart(
    @PreviewParameter(ChartDataProvider::class)
    data: ChartData,
    modifier: Modifier = Modifier
) {
    if (data.isEmpty()) return

    Chart(
        chart = lineChart(
            lines = listOf(
                // Today (blue)
                lineSpec(
                    lineColor = Color.Blue
                ),
                // Yesterday (gray)
                lineSpec(
                    lineColor = Color.Gray
                )
            )
        ),
        model = data.getModel(),
        startAxis = rememberStartAxis(
            title = "kcal",
            titleComponent = textComponent(),
            valueFormatter = { value, _ ->
            "${value.toInt()}"
        }),
        bottomAxis = rememberBottomAxis(
            title = "time of day",
            titleComponent = textComponent(),
            valueFormatter = { value, _ ->
                val index = value.toInt()
                data.yesterdayData.getOrNull(index)?.first?.let { time ->
                    val hour = time.hour
                    when {
                        hour == 0 -> "12a"
                        hour < 12 -> "${hour}a"
                        hour == 12 -> "12p"
                        else -> "${hour - 12}p"
                    }
                } ?: ""
            }
        ),
        modifier = modifier
            .fillMaxWidth()
            .height(300.dp)
    )
}