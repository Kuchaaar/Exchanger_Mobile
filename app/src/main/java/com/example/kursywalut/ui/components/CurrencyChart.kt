package com.example.kursywalut.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.kursywalut.data.CurrencyRateDto
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet

@Composable
fun CurrencyChart(rates: List<CurrencyRateDto>, code: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(420.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(Modifier.fillMaxSize().padding(12.dp)) {
            Text(
                "Exchange rate of $code",
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(Modifier.height(8.dp))
            HorizontalDivider()
            Spacer(Modifier.height(8.dp))

            if (rates.isEmpty()) {
                Text(
                    "No data in specified date range",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.bodyMedium
                )
                return@Column
            }
            val onSurface = MaterialTheme.colorScheme.onSurface.toArgb()
            val onSurfaceVariant = MaterialTheme.colorScheme.onSurfaceVariant.toArgb()
            val primary = MaterialTheme.colorScheme.primary.toArgb()
            val secondary = MaterialTheme.colorScheme.secondary.toArgb()
            val surface = MaterialTheme.colorScheme.surface.toArgb()
            AndroidView(
                modifier = Modifier.fillMaxSize(),
                factory = { ctx ->

                    LineChart(ctx).apply {
                        setBackgroundColor(android.graphics.Color.TRANSPARENT)
                        setViewPortOffsets(56f, 16f, 16f, 44f)
                        description.isEnabled = false
                        legend.isEnabled = false
                        axisRight.isEnabled = false
                        setTouchEnabled(true)
                        isDragEnabled = false
                        setScaleEnabled(false)
                        setPinchZoom(false)
                        isHighlightPerTapEnabled = true
                        isHighlightPerDragEnabled = false
                        xAxis.apply {
                            position = XAxis.XAxisPosition.BOTTOM
                            granularity = 1f
                            labelRotationAngle = -35f
                            valueFormatter = XAxisDateFormatter(rates.map { it.date })
                            setDrawGridLines(false)
                            setAvoidFirstLastClipping(true)
                            textColor = onSurfaceVariant
                            textSize = 11f
                            axisLineColor = onSurfaceVariant
                            axisMinimum = 0f
                            axisMaximum = (rates.size - 1).toFloat()
                        }
                        axisLeft.apply {
                            setDrawGridLines(true)
                            enableGridDashedLine(8f, 6f, 0f)
                            setDrawZeroLine(false)
                            textColor = onSurfaceVariant
                            textSize = 11f
                            axisLineColor = onSurfaceVariant
                            gridColor = onSurfaceVariant
                            axisMinimum = 0f
                            axisMaximum = rates.maxOf { it.mid.toFloat() } * 1.1f
                        }
                        marker = RateMarkerView(ctx, rates)
                        setNoDataText("No data")
                        setNoDataTextColor(onSurface)
                        animateX(900, Easing.EaseInOutQuad)
                    }
                },
                update = { chart ->

                    val main = createMainLineDataSet(rates, primary)
                    val points = createRedPointsDataSet(rates, secondary, surface)

                    chart.data = LineData(main, points).apply {
                        setDrawValues(false)
                    }
                    chart.xAxis.axisMaximum = (rates.size - 1).toFloat()
                    chart.axisLeft.axisMaximum = rates.maxOf { it.mid.toFloat() } * 1.1f
                    chart.invalidate()
                }
            )
        }
    }
}

fun createMainLineDataSet(
    rates: List<CurrencyRateDto>,
    colorPrimary: Int
): LineDataSet =
    LineDataSet(
        rates.mapIndexed { i, r -> Entry(i.toFloat(), r.mid.toFloat()) },
        "Kurs"
    ).apply {
        setDrawValues(false)
        lineWidth = 2.5f
        mode = LineDataSet.Mode.CUBIC_BEZIER
        cubicIntensity = 0.15f
        color = colorPrimary
        setDrawCircles(false)

        // Delikatne wypełnienie pod linią
        setDrawFilled(true)
        fillColor = colorPrimary
        fillAlpha = 55 // ~20% krycia

        // Highlight pod kolor motywu
        highLightColor = colorPrimary
        highlightLineWidth = 1.2f
        setDrawHorizontalHighlightIndicator(false)
        setHighlightEnabled(false)
    }

fun createRedPointsDataSet(
    rates: List<CurrencyRateDto>,
    colorSecondary: Int,
    holeColor: Int
): LineDataSet {
    val entries = rates.mapIndexedNotNull { i, r ->
        if (r.internetAnswer.isNotEmpty()) Entry(i.toFloat(), r.mid.toFloat()) else null
    }
    return LineDataSet(entries, "Info").apply {
        setDrawValues(false)
        setDrawCircles(true)
        circleRadius = 5.5f
        setCircleColor(colorSecondary)
        setDrawCircleHole(true)
        circleHoleRadius = 2.5f
        setCircleHoleColor(holeColor)
        lineWidth = 0f
        mode = LineDataSet.Mode.LINEAR
        setDrawHighlightIndicators(false)
        setHighlightEnabled(true)
    }
}
