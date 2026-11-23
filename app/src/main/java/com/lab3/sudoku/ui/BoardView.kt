package com.lab3.sudoku.ui

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import com.lab3.sudoku.domain.Board
import com.lab3.sudoku.domain.Validator
import kotlin.math.min

class BoardView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : View(context, attrs) {

    var board: Board? = null
    var selected: Pair<Int, Int>? = null
    var onCellClick: ((Int, Int) -> Unit)? = null

    private val density = resources.displayMetrics.density
    private val thinWidth = maxOf(1f, density)
    private val thickWidth = maxOf(3f, 2.5f * density)

    private val thin = Paint().apply {
        color = Color.GRAY
        strokeWidth = thinWidth
        style = Paint.Style.STROKE
    }
    private val thick = Paint().apply {
        color = Color.BLACK
        strokeWidth = thickWidth
        style = Paint.Style.STROKE
        strokeCap = Paint.Cap.SQUARE
    }
    private val highlightPaint = Paint().apply { color = Color.parseColor("#E3F2FD") }
    private val sameDigitPaint = Paint().apply { color = Color.parseColor("#FFE082") }
    private val selectedPaint = Paint().apply { color = Color.parseColor("#90CAF9") }
    private val text = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        textAlign = Paint.Align.CENTER
        typeface = Typeface.DEFAULT_BOLD
    }

    private var cell = 0f
    private var originX = 0f
    private var originY = 0f

    override fun onMeasure(widthSpec: Int, heightSpec: Int) {
        val w = MeasureSpec.getSize(widthSpec)
        val h = MeasureSpec.getSize(heightSpec)
        val size = if (MeasureSpec.getMode(heightSpec) == MeasureSpec.UNSPECIFIED) w else min(w, h)
        setMeasuredDimension(size, size)
    }

    private fun updateGeometry(n: Int) {
        val side = min(width, height).toFloat()
        cell = ((side - thickWidth) / n).toInt().coerceAtLeast(1).toFloat()
        val grid = cell * n
        originX = ((width - grid) / 2f)
        originY = ((height - grid) / 2f)
    }

    private fun fillCell(canvas: Canvas, row: Int, col: Int, paint: Paint) {
        canvas.drawRect(
            originX + col * cell, originY + row * cell,
            originX + (col + 1) * cell, originY + (row + 1) * cell,
            paint
        )
    }

    override fun onDraw(canvas: Canvas) {
        val b = board ?: return
        val n = b.size
        updateGeometry(n)
        val grid = cell * n
        canvas.drawColor(Color.WHITE)

        val sel = selected
        val selValue = if (sel != null) b[sel.first, sel.second].value else 0

        sel?.let { (sr, sc) ->
            canvas.drawRect(originX, originY + sr * cell, originX + grid, originY + (sr + 1) * cell, highlightPaint)
            canvas.drawRect(originX + sc * cell, originY, originX + (sc + 1) * cell, originY + grid, highlightPaint)
            val br = sr / b.boxSize * b.boxSize
            val bc = sc / b.boxSize * b.boxSize
            canvas.drawRect(
                originX + bc * cell, originY + br * cell,
                originX + (bc + b.boxSize) * cell, originY + (br + b.boxSize) * cell,
                highlightPaint
            )
        }

        if (selValue != 0) {
            for (r in 0 until n) for (c in 0 until n) {
                if (b[r, c].value == selValue) fillCell(canvas, r, c, sameDigitPaint)
            }
        }

        sel?.let { (sr, sc) -> fillCell(canvas, sr, sc, selectedPaint) }

        text.textSize = cell * 0.6f
        val offset = -(text.descent() + text.ascent()) / 2
        for (r in 0 until n) for (c in 0 until n) {
            val cellData = b[r, c]
            if (cellData.value == 0) continue
            text.color = when {
                !Validator.isValid(b, r, c, cellData.value) -> Color.RED
                cellData.isFixed -> Color.BLACK
                else -> Color.parseColor("#1565C0")
            }
            canvas.drawText(
                cellData.value.toString(),
                originX + c * cell + cell / 2,
                originY + r * cell + cell / 2 + offset,
                text
            )
        }

        for (i in 0..n) {
            if (i % b.boxSize == 0) continue
            val p = i * cell
            canvas.drawLine(originX + p, originY, originX + p, originY + grid, thin)
            canvas.drawLine(originX, originY + p, originX + grid, originY + p, thin)
        }
        for (i in 0..n step b.boxSize) {
            val p = i * cell
            canvas.drawLine(originX + p, originY, originX + p, originY + grid, thick)
            canvas.drawLine(originX, originY + p, originX + grid, originY + p, thick)
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        val b = board ?: return false
        if (event.action == MotionEvent.ACTION_DOWN) {
            updateGeometry(b.size)
            val col = ((event.x - originX) / cell).toInt()
            val row = ((event.y - originY) / cell).toInt()
            if (row in 0 until b.size && col in 0 until b.size) onCellClick?.invoke(row, col)
        }
        return true
    }
}