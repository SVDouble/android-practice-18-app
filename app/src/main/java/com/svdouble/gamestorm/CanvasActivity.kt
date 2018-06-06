package com.svdouble.gamestorm

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Paint.Style.STROKE
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.MotionEvent
import android.view.View

data class Point2D(var x: Float = 0f, var y: Float = 0f)

class Draw2D(context: Context) : View(context) {

    private val mPaint = Paint()
    private val shadowPaint = Paint()
    private var path = Point2D()
    private var urg:Int = 0

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_DOWN) {
            this.path.x = event.x
            this.path.y = event.y
            urg+=1
            this.invalidate()
        }
        return super.onTouchEvent(event)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        mPaint.color = Color.YELLOW
        mPaint.style = Paint.Style.FILL
        canvas.drawPaint(mPaint)

        val w: Float = width.toFloat()
        val h: Float = height.toFloat()
        var i = 0F

        mPaint.color = Color.RED
        mPaint.strokeWidth = 7F
        while (i <= h * 3 / 4) {
            canvas.drawLine(i, 0F, i, w, mPaint)
            i += 60F
        }
        i = 0F
        while (i + 80f <= h * 3 / 4) {
            canvas.drawLine(0F, i, h, i, mPaint)
            i += 60F
        }
        shadowPaint.isAntiAlias = true
        shadowPaint.color = Color.BLUE
        shadowPaint.textSize = 45.0f
        shadowPaint.strokeWidth = 3.0f
        shadowPaint.style = Paint.Style.FILL
        shadowPaint.setShadowLayer(5.0f, 10.0f, 10.0f, Color.GRAY)
        shadowPaint.textSize = 50.0f
        shadowPaint.strokeWidth = 4.0f
        canvas.drawText("${path.x}    ${path.y}", 20f, 540f, shadowPaint)
        if(urg % 2 == 0)
          drawCircle(canvas, path)
        else
          drawCross(canvas, path)
    }

    private fun drawCross(canvas: Canvas, arg: Point2D) {
        val arg11:Float = arg.x - (arg.x % 60) + 30f
        val arg22:Float = arg.y + 30f - (arg.y % 60)
        mPaint.color = Color.BLACK
        mPaint.strokeWidth = 7F
        canvas.drawLine(arg11 - 25F, arg22 - 25F, arg11 + 25F, arg22 + 25F, mPaint)
        canvas.drawLine(arg11 + 25F, arg22 - 25F, arg11 - 25F, arg22 + 25F, mPaint)
    }

    private fun drawCircle(canvas: Canvas, arg: Point2D) {
        val arg11:Float = arg.x - (arg.x % 60) + 30f
        val arg22:Float = arg.y + 30f - (arg.y % 60)
        mPaint.isAntiAlias = true
        mPaint.color = Color.GREEN
        mPaint.style = STROKE
        mPaint.strokeWidth = 5F
        canvas.drawCircle(arg11, arg22, 27f, mPaint)
    }

    fun drawChip(point: Point2D, chip_id: Int) {
        TODO()
    }

    fun drawBrokenLine(points: ArrayList<Point2D>, color: Int) {
        TODO()
    }

    fun paintCells(points: ArrayList<Point2D>, color: Int) {
        TODO()
    }

    /* Will be updated later
    * TODO:
    * - custom field size
    * - endless field
    * */
}

class CanvasActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val draw2D = Draw2D(this)
        setContentView(draw2D)
    }
}
