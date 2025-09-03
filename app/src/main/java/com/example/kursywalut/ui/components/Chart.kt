package com.example.kursywalut.ui.components

import android.annotation.SuppressLint
import android.content.Context
import android.widget.TextView
import com.example.kursywalut.R
import com.example.kursywalut.data.CurrencyRateDto
import com.github.mikephil.charting.components.MarkerView
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.utils.MPPointF

@SuppressLint("ViewConstructor")
class RateMarkerView(context: Context, private val rates: List<CurrencyRateDto>) :
    MarkerView(context, R.layout.marker_view) {

    private val tvContent: TextView = findViewById(R.id.marker_text)

    override fun getOffset(): MPPointF = MPPointF(-(width / 2).toFloat(), -height.toFloat())

    override fun refreshContent(e: Entry?, highlight: Highlight?) {
        val idx = e?.x?.toInt() ?: return
        val text = rates.getOrNull(idx)?.internetAnswer ?: ""
        tvContent.isSingleLine = false
        tvContent.text = text
        super.refreshContent(e, highlight)
    }
}

class XAxisDateFormatter(private val labels: List<String>) : ValueFormatter() {
    override fun getFormattedValue(value: Float): String {
        val i = value.toInt()
        return labels.getOrNull(i)?.substring(5) ?: ""
    }
}


