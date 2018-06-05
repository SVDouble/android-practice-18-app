package com.svdouble.gamestorm

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View

class BaseCanvasActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val draw2D = Draw2D(this)
        setContentView(draw2D)
    }
}

class Draw2D(context: Context) : View(context) {

    private val mPaint = Paint()

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        mPaint.color = Color.YELLOW
        mPaint.style = Paint.Style.FILL // Style: fill
        canvas.drawPaint(mPaint)

        val w: Float = width.toFloat()
        val h: Float = height.toFloat()
        var i = 0F

        mPaint.color = Color.RED
        mPaint.strokeWidth = 5F
        while(i <= h*3/4) {
            canvas.drawLine(i, 0F, i, w, mPaint)
            i+=30F
        }
        i = 0F
        while(i <= h*3/4) {
            canvas.drawLine(0F, i, h, i, mPaint)
            i+=30F
        }

        fun Cross(arg1: Int, arg2: Int) {
            mPaint.color = Color.RED
            mPaint.strokeWidth = 5F
            canvas.drawLine(0F, 0F, 0F, 0F, mPaint)
        }
    }
}
