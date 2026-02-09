package com.joehxblog.healthcompare


import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import com.patrykandpatrick.vico.compose.cartesian.CartesianChartHost
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberBottom
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberStart
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberLineCartesianLayer
import com.patrykandpatrick.vico.compose.cartesian.rememberCartesianChart
import com.patrykandpatrick.vico.compose.common.component.rememberTextComponent
import com.patrykandpatrick.vico.core.cartesian.axis.HorizontalAxis
import com.patrykandpatrick.vico.core.cartesian.axis.VerticalAxis
import com.patrykandpatrick.vico.core.cartesian.data.CartesianChartModelProducer
import com.patrykandpatrick.vico.core.cartesian.data.lineSeries

data class ChartData(
    val todayData: List<Double>,
    val yesterdayData: List<Double>,
) {
    fun isEmpty(): Boolean {
        return todayData.isEmpty() && yesterdayData.isEmpty()
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

    val modelProducer = remember { CartesianChartModelProducer() }

    LaunchedEffect(data) {
        modelProducer.runTransaction {
            lineSeries {
                series(data.yesterdayData)
                series(data.todayData)
            }
        }
    }

    CartesianChartHost(
        rememberCartesianChart(
                rememberLineCartesianLayer(),
            startAxis = VerticalAxis.rememberStart(
                label = rememberTextComponent(),
                valueFormatter = {_, value, _ -> "${value.toInt()}"},
                titleComponent = rememberTextComponent(),
                title = "kCal"
            ),
            bottomAxis = HorizontalAxis.rememberBottom(
                label = rememberTextComponent(),
                valueFormatter = {_, value, _ ->
                    val hour = value.toInt()
                    when {
                        hour == 0 -> "12a"
                        hour < 12 -> "${value.toInt()}a"
                        hour == 12 -> "12p"
                        else -> "${value.toInt() - 12}p"
                    }
                 },
                titleComponent = rememberTextComponent(),
                title = "Time of Day"
            ),
        ),
        modelProducer,
        modifier
            .fillMaxWidth()
            .height(300.dp)
    )
}