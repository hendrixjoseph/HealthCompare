package com.joehxblog.healthcompare.chart


import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import com.joehxblog.healthcompare.ChartDataProvider
import com.patrykandpatrick.vico.compose.cartesian.CartesianChartHost
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberBottom
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberStart
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberLineCartesianLayer
import com.patrykandpatrick.vico.compose.cartesian.rememberCartesianChart
import com.patrykandpatrick.vico.compose.cartesian.rememberVicoZoomState
import com.patrykandpatrick.vico.compose.common.component.rememberTextComponent
import com.patrykandpatrick.vico.core.cartesian.Zoom
import com.patrykandpatrick.vico.core.cartesian.axis.HorizontalAxis
import com.patrykandpatrick.vico.core.cartesian.axis.VerticalAxis
import com.patrykandpatrick.vico.core.cartesian.data.CartesianChartModelProducer
import com.patrykandpatrick.vico.core.cartesian.data.lineSeries

@Preview
@Composable
fun LineChart(
    @PreviewParameter(ChartDataProvider::class)
    data: ChartData,
    yAxisLabel: String = "kCal"
) {
    when {
        data.isEmpty() -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }
        else -> {
            val modelProducer = remember { CartesianChartModelProducer() }

            LaunchedEffect(data) {
                modelProducer.runTransaction {
                    lineSeries {
                        series(
                            data.yesterdayData.indices.map{ i -> i / 4.0},
                            data.yesterdayData
                        )
                        series(
                            data.todayData.indices.map{ i -> i / 4.0},
                            data.todayData
                        )
                    }
                }
            }

            CartesianChartHost(
                chart = rememberCartesianChart(
                        rememberLineCartesianLayer(),
                    startAxis = VerticalAxis.rememberStart(
                        label = rememberTextComponent(),
                        valueFormatter = {_, value, _ -> "${value.toInt()}"},
                        titleComponent = rememberTextComponent(),
                        title = yAxisLabel
                    ),
                    bottomAxis = HorizontalAxis.rememberBottom(
                        label = rememberTextComponent(),
                        itemPlacer = HorizontalAxis.ItemPlacer.aligned(
                            spacing = { 4 },
                            offset = { 0 }
                        ),
                        valueFormatter = {_, value, _ ->
                            val hour = value.toInt()
                            when {
                                hour == 0 -> "12a"
                                hour < 12 -> "${hour}a"
                                hour == 12 -> "12p"
                                else -> "${hour - 12}p"
                            }
                         },
                        titleComponent = rememberTextComponent(),
                        title = "Time of Day"
                    ),
                ),
                zoomState = rememberVicoZoomState(
                    initialZoom = remember { Zoom.fixed(0.25f) }
                ),
                modelProducer =  modelProducer,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp)
            )
        }
    }
}