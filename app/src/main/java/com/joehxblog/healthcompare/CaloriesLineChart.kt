package com.joehxblog.healthcompare

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.patrykandpatrick.vico.compose.chart.Chart
import com.patrykandpatrick.vico.compose.chart.line.lineChart
import com.patrykandpatrick.vico.core.entry.entryModelOf
import java.time.LocalDateTime

@Composable
fun CaloriesLineChart(
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
//        marker = rememberMarker(),
        modifier = modifier
            .fillMaxWidth()
            .height(300.dp)
    )
}