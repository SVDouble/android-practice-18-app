package com.svdouble.gamestorm

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.view.MotionEvent
import android.graphics.Paint.Style.STROKE
import android.widget.TextView


class Draw2D(context: Context) : View(context) {
    private val mPaint = Paint()


    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        //крестик
        fun Cross(arg1: Float, arg2: Float) {
            mPaint.color = Color.BLACK
            mPaint.strokeWidth = 5F
            canvas.drawLine(arg1-15F, arg2-15F, arg1+15F, arg2+15F, mPaint)
            canvas.drawLine(arg1+15F, arg2-15F, arg1-15F, arg2+15F, mPaint)
        }
        //нолик
        fun Circle(arg1: Float, arg2: Float) {
            mPaint.setAntiAlias(true)
            mPaint.color = Color.GREEN // установим зеленый цвет
            mPaint.style = STROKE
            mPaint.setStrokeWidth(5F)
            canvas.drawCircle(arg1, arg2, 14f, mPaint)
        }

        mPaint.color = Color.YELLOW
        mPaint.style = Paint.Style.FILL
        canvas.drawPaint(mPaint)

        val w: Float = width.toFloat()
        val h: Float = height.toFloat()
        var i = 0F

        mPaint.color = Color.RED
        mPaint.strokeWidth = 5F
        while(i <= h*3/4) {
            canvas.drawLine(i, 0F, i, w, mPaint)
            i+=40F
        }
        i = 0F
        while(i+80F <= h*3/4) {
            canvas.drawLine(0F, i, h, i, mPaint)
            i+=40F
        }


        val shadowPaint = Paint()
        shadowPaint.isAntiAlias = true
        shadowPaint.color = Color.BLUE
        shadowPaint.textSize = 45.0f
        shadowPaint.strokeWidth = 3.0f
        shadowPaint.style = Paint.Style.FILL
        shadowPaint.setShadowLayer(5.0f, 10.0f, 10.0f, Color.GRAY)
        canvas.drawText("моя программа, ребят", 20f, 510f, shadowPaint)
        canvas.drawText("X:$x   Y:$y", 20f, 540f, shadowPaint)

    }
}



class CanvasActivity : AppCompatActivity() {
    internal var x: Float = 0.toFloat()
    internal var y: Float = 0.toFloat()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val draw2D = Draw2D(this)
        setContentView(draw2D)
       }

    fun onTouch(event: MotionEvent): Boolean {
        x = event.x
        y = event.y
        return true
    }

}

