package com.svdouble.gamestorm

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color.*
import android.graphics.Paint
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.view.MotionEvent
import android.graphics.Paint.Style.STROKE
import android.util.Log

const val TAG = "GameStorm"
const val bottomMargin: Float = 3f / 4

/* Handle calls */
sealed class BaseCall

class CallDrawGrid(val rows: Int, val columns: Int, val view: View) : BaseCall()
class CallDrawChip(val point: Cell2D, val chipId: Int) : BaseCall()
class CallDrawGridCells(val points: Array<Cell2D>, val color: Int) : BaseCall()

class DrawStorage(private val draw: Draw2D) {

    init {
        Log.d(TAG, "Storage created!")
    }

    val drawCalls: ArrayList<BaseCall> = arrayListOf()
    fun drawFromStorage(canvas: Canvas) {
        Log.d(TAG, "Start drawing from storage!")
        for (call in drawCalls)
            when (call) {
                is CallDrawGridCells -> draw.fillGridCells(call, canvas)
                is CallDrawChip -> draw.drawChip(call, canvas)
                is CallDrawGrid -> draw.drawGrid(call, canvas)
            }
    }
}


class Draw2D(context: Context) : View(context) {

    val storage = DrawStorage(this)
    private var rown = 0
    private val mPaint = Paint()
    private val shadowPaint = Paint()
    private var xPath: Float = 0.0f
    private var yPath: Float = 0.0f
    private var w: Int = width
    private var h: Int = height

    private var urg: Int = 0


    private var squareSide: Int = 0

    private val numHorizLines: Float = rown.toFloat()
    private val lastLineY = numHorizLines * squareSide


    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_DOWN) {
            this.xPath = event.x
            this.yPath = event.y
            Log.d(TAG, "Touch detected!")
            urg = (urg + 1) % 2
            /*for ( row in rows ) {

                val cell = row.onTouch(event.x, event.y, urg + 1)
                if ( cell != null ) {

                    //var upRow = rows[ row.num - 1 ]
                    //upRow.num

                    if ( cell.num > 0 && cell.num < row.arr.size - 1)
                        if (row.arr[cell.num - 1].type == cell.type && row.arr[cell.num + 1].type == cell.type) {
                            row.arr[cell.num - 1].bgcolor = Color.GREEN
                            row.arr[cell.num + 1].bgcolor = Color.GREEN
                            cell.bgcolor = Color.GREEN
                        }

                    break
                }
            }*/
            this.invalidate()
        }
        return super.onTouchEvent(event)
    }


    override fun onDraw(canvas: Canvas) {

        super.onDraw(canvas)
        w = width
        h = height
        Log.d(TAG, "width $w")
        //фон
        mPaint.color = YELLOW
        mPaint.style = Paint.Style.FILL
        canvas.drawPaint(mPaint)

        storage.drawFromStorage(canvas)

        /*
        shadowPaint.isAntiAlias = true
        shadowPaint.color = BLUE
        shadowPaint.textSize = 45.0f
        shadowPaint.strokeWidth = 3.0f
        shadowPaint.style = Paint.Style.FILL
        shadowPaint.setShadowLayer(5.0f, 10.0f, 10.0f, GRAY)
        shadowPaint.textSize = 50.0f
        shadowPaint.strokeWidth = 4.0f
        canvas.drawText("$xPath    $yPath", 20f, /*rows[ rows.size - 1 ].bottom()*/lastLineY + 50f, shadowPaint) */
    }

    fun drawGrid(call: CallDrawGrid, canvas: Canvas) {
        var squareSide: Int = w / call.columns
        val lastLineY = 1f * call.rows * squareSide

        if (w > (h * bottomMargin))
            squareSide = (h * bottomMargin / rown).toInt()

        this.squareSide= squareSide
        rown = call.rows

        mPaint.strokeWidth = 7F
        mPaint.color = RED

        for (ii in 0..call.rows) {
            canvas.drawLine(0f, (ii * squareSide).toFloat(), w.toFloat(), (ii * squareSide).toFloat(), mPaint)
        }
        for (ii in 0..call.columns) {
            canvas.drawLine((ii * squareSide).toFloat(), 0f, (ii * squareSide).toFloat(), lastLineY, mPaint)
        }
    }

    fun drawChip(call: CallDrawChip, canvas: Canvas) {
        when (call.chipId) {
            0 -> { // cross
                val xxx = (call.point.x * 1f * squareSide - 1)
                val yyy = (call.point.y * 1f * squareSide - 1)
                val arg11: Float = xxx - (xxx % squareSide) + squareSide / 2
                val arg22: Float = yyy + squareSide / 2 - (yyy % squareSide)
                mPaint.color = BLACK
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
                mPaint.color = GREEN // установим зеленый цвет
                mPaint.style = STROKE
                mPaint.strokeWidth = 6F
                canvas.drawCircle(arg11, arg22, squareSide / 2 - 4f, mPaint)

            }
        }

    }

    fun fillGridCells(call: CallDrawGridCells, canvas: Canvas) {
        mPaint.color = call.color
        call.points.forEach {
            val xxx = ((it.x - 1f) * squareSide)
            val yyy = ((it.y - 1f) * squareSide)
            val bottom: Float = yyy + squareSide
            val right: Float = xxx + squareSide
            canvas.drawRect(xxx, yyy, right, bottom, mPaint)
        }
    }
}

data class Cell2D(val x: Int, val y: Int)

class CanvasActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val draw2D = Draw2D(this)
        draw2D.storage.drawCalls.add(CallDrawGrid(5, 5, draw2D))
        draw2D.storage.drawCalls.add(CallDrawChip(Cell2D(1, 2), 0))
        draw2D.storage.drawCalls.add(CallDrawGridCells(arrayOf(Cell2D(2, 2), Cell2D(2, 3)), BLACK))
        setContentView(draw2D)
    }
}

