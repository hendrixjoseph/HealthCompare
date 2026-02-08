package com.joehxblog.healthcompare


import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import com.patrykandpatrick.vico.compose.cartesian.CartesianChartHost
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberBottom
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberStart
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberLineCartesianLayer
import com.patrykandpatrick.vico.compose.cartesian.rememberCartesianChart
import com.patrykandpatrick.vico.compose.common.component.rememberLineComponent
import com.patrykandpatrick.vico.compose.common.component.rememberTextComponent
import com.patrykandpatrick.vico.core.cartesian.axis.HorizontalAxis
import com.patrykandpatrick.vico.core.cartesian.axis.VerticalAxis
import com.patrykandpatrick.vico.core.cartesian.data.CartesianChartModelProducer
import com.patrykandpatrick.vico.core.cartesian.data.lineSeries
import java.time.LocalDateTime

import com.patrykandpatrick.vico.compose.cartesian.layer.rememberLine
import com.patrykandpatrick.vico.core.cartesian.layer.LineCartesianLayer
import com.patrykandpatrick.vico.core.cartesian.layer.LineCartesianLayer.LineFill
import com.patrykandpatrick.vico.core.common.Fill

data class ChartData(
    val todayData: List<Pair<LocalDateTime, Double>>,
    val yesterdayData: List<Pair<LocalDateTime, Double>>,
) {
    fun isEmpty(): Boolean {
        return todayData.isEmpty() && yesterdayData.isEmpty()
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

    val modelProducer = CartesianChartModelProducer()

    LaunchedEffect(data) {
        modelProducer.runTransaction {
            lineSeries {
                series(data.todayData.map { it.second })     // Today
                series(data.yesterdayData.map { it.second }) // Yesterday
            }
        }
    }

    CartesianChartHost(
        rememberCartesianChart(
                rememberLineCartesianLayer(),
            startAxis = VerticalAxis.rememberStart(
                valueFormatter = {_, value, _ -> "${value.toInt()}"},
                titleComponent = rememberTextComponent(),
                title = "kCal"
            ),
            bottomAxis = HorizontalAxis.rememberBottom(
                valueFormatter = {_, value, _ -> "${value.toInt()}"},
                titleComponent = rememberTextComponent(),
                title = "Time of Day"
            ),
        ),
        modelProducer,
        modifier
            .fillMaxWidth()
            .height(300.dp)
    )

//    Chart(
//        chart = lineChart(
//            lines = listOf(
//                // Today (blue)
//                lineSpec(
//                    lineColor = Color.Blue
//                ),
//                // Yesterday (gray)
//                lineSpec(
//                    lineColor = Color.Gray
//                )
//            )
//        ),
//        model = data.getModel(),
//        startAxis = rememberStartAxis(
//            title = "kcal",
//            titleComponent = textComponent(),
//            valueFormatter = { value, _ ->
//            "${value.toInt()}"
//        }),
//        bottomAxis = rememberBottomAxis(
//            title = "time of day",
//            titleComponent = textComponent(),
//            valueFormatter = { value, _ ->
//                val index = value.toInt()
//                data.yesterdayData.getOrNull(index)?.first?.let { time ->
//                    val hour = time.hour
//                    when {
//                        hour == 0 -> "12a"
//                        hour < 12 -> "${hour}a"
//                        hour == 12 -> "12p"
//                        else -> "${hour - 12}p"
//                    }
//                } ?: ""
//            }
//        ),
//        modifier = modifier
//            .fillMaxWidth()
//            .height(300.dp)
//    )
}