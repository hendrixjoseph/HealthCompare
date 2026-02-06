package com.joehxblog.healthcompare

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import com.patrykandpatrick.vico.compose.axis.axisLabelComponent
import com.patrykandpatrick.vico.compose.axis.horizontal.rememberBottomAxis
import com.patrykandpatrick.vico.compose.axis.vertical.rememberStartAxis
import com.patrykandpatrick.vico.compose.chart.Chart
import com.patrykandpatrick.vico.compose.chart.line.lineChart
import com.patrykandpatrick.vico.core.component.text.TextComponent
import com.patrykandpatrick.vico.core.entry.entryModelOf
import java.time.LocalDateTime
import com.patrykandpatrick.vico.compose.component.textComponent

@Preview
@Composable
fun CaloriesLineChart(
    @PreviewParameter(ChartDataProvider::class)
    data: List<Pair<LocalDateTime, Double>>,
    modifier: Modifier = Modifier
) {
    if (data.isEmpty()) return

    val entries = data.mapIndexed { index, pair ->
        index.toFloat() to pair.second.toFloat()
    }

    val model = entryModelOf(*entries.toTypedArray())

    Chart(
        chart = lineChart(),
        model = model,
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
                data.getOrNull(index)?.first?.let { time ->
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