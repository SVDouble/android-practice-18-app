package com.svdouble.gamestorm

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.view.MotionEvent
import android.view.View

const val bottomMargin: Float = 3f / 4

/* Handle calls */
sealed class BaseCall

data class CallDrawBg(val color: Int) : BaseCall()
data class CallDrawGrid(val rows: Int, val columns: Int) : BaseCall()
data class CallDrawChip(val point: Cell2D, val chipId: Int) : BaseCall()
data class CallDrawGridCells(val points: Array<Cell2D>, val color: Int) : BaseCall()

class CellularDrawEngine2D(context: Context) : View(context) {

    constructor(context: Context, gameHandler: BaseGameHandler) : this(context) {
        this.gameHandler = gameHandler
    }

    private lateinit var gameHandler: BaseGameHandler
    private val storage = DrawStorage()
    private val mPaint = Paint()

    private var rows = 0
    private var columns = 0
    private var squareSide = 0

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_DOWN) {
            val x = (event.x / squareSide).toInt() + 1
            val y = (event.y / squareSide).toInt() + 1

            if (x <= rows && y <= columns)
                gameHandler.dispatchEvent(GameEvent(GameEvent.EventType.STEP, Cell2D(x, y)))
            this.invalidate()
        }
        return super.onTouchEvent(event)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        storage.drawFromStorage(canvas)
    }

    private fun drawBg(call: CallDrawBg, canvas: Canvas) {
        mPaint.color = call.color
        mPaint.style = Paint.Style.FILL
        canvas.drawPaint(mPaint)
    }

    private fun drawGrid(call: CallDrawGrid, canvas: Canvas) {
        var squareSide: Int = width / call.columns
        val lastLineY = 1f * call.rows * squareSide

        if (width > (height * bottomMargin))
            squareSide = (height * bottomMargin / rows).toInt()

        this.squareSide= squareSide
        rows = call.rows
        columns = call.rows

        mPaint.strokeWidth = 7F
        mPaint.color = Color.RED

        for (ii in 0..call.rows) {
            canvas.drawLine(0f, (ii * squareSide).toFloat(), width.toFloat(), (ii * squareSide).toFloat(), mPaint)
        }
        for (ii in 0..call.columns) {
            canvas.drawLine((ii * squareSide).toFloat(), 0f, (ii * squareSide).toFloat(), lastLineY, mPaint)
        }
    }

    private fun drawChip(call: CallDrawChip, canvas: Canvas) {
        when (call.chipId) {
            0 -> { // cross
                val xxx = (call.point.x * 1f * squareSide - 1)
                val yyy = (call.point.y * 1f * squareSide - 1)
                val arg11: Float = xxx - (xxx % squareSide) + squareSide / 2
                val arg22: Float = yyy + squareSide / 2 - (yyy % squareSide)
                mPaint.color = Color.BLACK
                mPaint.strokeWidth = 7F
                canvas.drawLine(arg11 - (squareSide / 2 - 5f), arg22 - (squareSide / 2 - 5f), arg11 + (squareSide / 2 - 5f), arg22 + (squareSide / 2 - 5f), mPaint)
                canvas.drawLine(arg11 + (squareSide / 2 - 5f), arg22 - (squareSide / 2 - 5f), arg11 - (squareSide / 2 - 5f), arg22 + (squareSide / 2 - 5f), mPaint)
            }

            1 -> { // circle
                val xxx = 1f * call.point.x * squareSide - 1
                val yyy = 1f * call.point.y * squareSide - 1
                val arg11: Float = xxx - (xxx % squareSide) + squareSide / 2
                val arg22: Float = yyy + squareSide / 2 - (yyy % squareSide)

                mPaint.isAntiAlias = true
                mPaint.color = Color.GREEN // установим зеленый цвет
                mPaint.style = Paint.Style.STROKE
                mPaint.strokeWidth = 6F
                canvas.drawCircle(arg11, arg22, squareSide / 2 - 4f, mPaint)

            }
        }

    }

    private fun fillGridCells(call: CallDrawGridCells, canvas: Canvas) {
        mPaint.color = call.color
        call.points.forEach {
            val xxx = ((it.x - 1f) * squareSide)
            val yyy = ((it.y - 1f) * squareSide)
            val bottom: Float = yyy + squareSide
            val right: Float = xxx + squareSide
            canvas.drawRect(xxx, yyy, right, bottom, mPaint)
        }
    }

    fun forwardCall(call: BaseCall) {
        storage.drawCalls.add(call)
    }

    inner class DrawStorage {

        val drawCalls: ArrayList<BaseCall> = arrayListOf()
        fun drawFromStorage(canvas: Canvas) {
            for (call in drawCalls)
                when (call) {
                    is CallDrawGridCells -> fillGridCells(call, canvas)
                    is CallDrawChip -> drawChip(call, canvas)
                    is CallDrawGrid -> drawGrid(call, canvas)
                    is CallDrawBg -> drawBg(call, canvas)
                }
        }
    }
}
