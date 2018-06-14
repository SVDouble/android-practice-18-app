package com.svdouble.gamestorm

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color.*
import android.graphics.Paint
import android.view.MotionEvent
import android.view.View

const val bottomMargin: Float = 1f

/* Handle calls */
sealed class BaseCall

data class CallDrawBg(val color: Int = WHITE) : BaseCall()
data class CallDrawGrid(val rows: Int, val columns: Int, val color: Int = BLACK) : BaseCall()
data class CallDrawChip(val point: Cell2D, val chipId: Int, val color: Int = BLUE) : BaseCall()
data class CallDrawGridCells(val points: Array<Cell2D>, val color: Int = GREEN) : BaseCall()

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
            val x = (event.x / squareSide).toInt()
            val y = (event.y / squareSide).toInt()

            if (x < columns && y < rows)
                gameHandler.dispatchEvent(GameEvent(GameEvent.Type.STEP, Cell2D(x, y)))
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
        rows = call.rows
        columns = call.columns
        val squareSide: Int =
                if (width / columns * rows > height * bottomMargin)
                    (height * bottomMargin / rows).toInt()
                else
                    width / columns

        this.squareSide = squareSide

        mPaint.strokeWidth = 14F
        mPaint.color = call.color

        val endX = if (columns * squareSide > width) 1f * width else 1f * columns * squareSide
        val endY = if (rows * squareSide > height * bottomMargin) height * bottomMargin else 1f * rows * squareSide
        for (ii in 0..rows) {
            canvas.drawLine(0f, (ii * squareSide).toFloat(), endX, (ii * squareSide).toFloat(), mPaint)
        }
        for (ii in 0..columns) {
            canvas.drawLine((ii * squareSide).toFloat(), 0f, (ii * squareSide).toFloat(), endY, mPaint)
        }
    }

    private fun drawChip(call: CallDrawChip, canvas: Canvas) {
        when (call.chipId) {
            0 -> { // cross
                val xxx = (call.point.x * 1f * squareSide - 1)
                val yyy = (call.point.y * 1f * squareSide - 1)
                val arg11: Float = xxx - (xxx % squareSide) + squareSide / 2
                val arg22: Float = yyy + squareSide / 2 - (yyy % squareSide)
                mPaint.isAntiAlias = true
                mPaint.color = call.color
                mPaint.style = Paint.Style.STROKE
                mPaint.strokeWidth = 15F
                canvas.drawLine(arg11 - (squareSide / 2 - 5f), arg22 - (squareSide / 2 - 5f), arg11 + (squareSide / 2 - 5f), arg22 + (squareSide / 2 - 5f), mPaint)
                canvas.drawLine(arg11 + (squareSide / 2 - 5f), arg22 - (squareSide / 2 - 5f), arg11 - (squareSide / 2 - 5f), arg22 + (squareSide / 2 - 5f), mPaint)
            }

            1 -> { // circle
                val xxx = 1f * call.point.x * squareSide - 1
                val yyy = 1f * call.point.y * squareSide - 1
                val arg11: Float = xxx - (xxx % squareSide) + squareSide / 2
                val arg22: Float = yyy + squareSide / 2 - (yyy % squareSide)

                mPaint.isAntiAlias = true
                mPaint.color = call.color
                mPaint.style = Paint.Style.STROKE
                mPaint.strokeWidth = 15F
                canvas.drawCircle(arg11, arg22, squareSide / 2 - 4f, mPaint)

            }
        }

    }

    private fun fillGridCells(call: CallDrawGridCells, canvas: Canvas) {
        mPaint.isAntiAlias = true
        mPaint.color = call.color
        mPaint.style = Paint.Style.STROKE
        mPaint.strokeWidth = 15F
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

    private inner class DrawStorage {
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
