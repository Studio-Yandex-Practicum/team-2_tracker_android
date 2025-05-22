package com.nemislimus.tratometr.common.util

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.util.TypedValue
import android.view.View
import androidx.core.content.ContextCompat
import com.nemislimus.tratometr.R
import java.math.BigDecimal
import java.math.RoundingMode

class RingChartView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val paint = Paint()
    private val rect = RectF()
    val data = mutableListOf<BigDecimal>()
    private val colors = mutableListOf<Int>()

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        drawRingChart(canvas)
    }

    @SuppressLint("DefaultLocale")
    private fun drawRingChart(canvas: Canvas) {
        val total = data.sumOf { it }
        var startAngle = -90f
        var sweepAngle: Float
        val ratio = 0.12f
        val rectInternal = createInnerRectF(rect, width.toFloat() * ratio, height.toFloat() * ratio)
        val halfOffset: Float

        if (data.isEmpty()) {
            sweepAngle = 360f
            halfOffset = 0f
            paint.color = getAppNotActiveColor(context)
            canvas.drawArc(rect, startAngle + halfOffset, sweepAngle - halfOffset, true, paint)
        } else if (data.size == 1) {
            sweepAngle = 360f
            halfOffset = 0f
            paint.color = colors[0]
            canvas.drawArc(rect, startAngle + halfOffset, sweepAngle - halfOffset, true, paint)
        } else {
            halfOffset = 2.5f
            for (i in data.indices) {
                sweepAngle = data[i].divide(total, 5, RoundingMode.HALF_UP).multiply(BigDecimal("360")).toFloat()
                paint.color = colors[i]
                canvas.drawArc(rect, startAngle + halfOffset, sweepAngle - halfOffset, true, paint)
                startAngle += sweepAngle
            }
        }
        drawInternalCircle(canvas, rectInternal)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        rect.set(0f, 0f, w.toFloat(), h.toFloat()) // Устанавливаем размеры диаграммы
    }

    private fun createInnerRectF(outerRect: RectF, offsetHorizontal: Float, offsetVertical: Float): RectF {
        // Вычисление координат нового RectF с учетом отступов
        val left = outerRect.left + offsetHorizontal
        val right = outerRect.right - offsetHorizontal
        val top = outerRect.top + offsetVertical
        val bottom = outerRect.bottom - offsetVertical
        return RectF(left, top, right, bottom)
    }

    private fun drawInternalCircle(canvas: Canvas, innerRect: RectF) {
        val color = getAppBackgroundColor(context)
        paint.color = color
        paint.style = Paint.Style.FILL
        canvas.drawArc(innerRect, 0f, 360f, true, paint)
    }

    private fun getAppBackgroundColor(context: Context): Int {
        val typedValue = TypedValue()
        context.theme.resolveAttribute(R.attr.appBackgroundColor, typedValue, true)
        return typedValue.data
    }

    private fun getAppNotActiveColor(context: Context): Int {
        val typedValue = TypedValue()
        context.theme.resolveAttribute(R.attr.appNotActiveColor, typedValue, true)
        return typedValue.data
    }

    fun setData(sumList: List<BigDecimal>) {
        data.clear()
        data.addAll(sumList)

        if (colors.isEmpty()) {
            attachColorSet()
        }

        invalidate()
    }

    private fun attachColorSet() {
        RingChartColorsResources.entries.forEach { color ->
            colors.add(
                ContextCompat.getColor(context, color.resId)
            )
        }
    }

}