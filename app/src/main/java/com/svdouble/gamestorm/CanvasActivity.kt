package com.svdouble.gamestorm

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.view.MotionEvent
import android.graphics.Paint.Style.STROKE
import android.graphics.Path
import android.widget.TextView
import android.text.method.Touch.onTouchEvent
import android.util.Log

const val TAG = "GameStorm"


class Draw2D(context: Context) : View(context) {
    private val mPaint = Paint()
    private val shadowPaint = Paint()
    private var xPath: Float = 0.0f
    private var yPath: Float = 0.0f
    private var urg:Int = 0

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_DOWN) {
            this.xPath = event.x
            this.yPath = event.y
            Log.d(TAG, "Touch detected!")
            urg+=1
            this.invalidate()
        }
        return super.onTouchEvent(event)
    }


    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        //крестик
        fun Cross(arg1: Float, arg2: Float) {
            val arg11:Float = arg1 - (arg1 % 60) + 30f
            val arg22:Float = arg2 + 30f - (arg2 % 60)
            mPaint.color = Color.BLACK
            mPaint.strokeWidth = 7F
            canvas.drawLine(arg11 - 25F, arg22 - 25F, arg11 + 25F, arg22 + 25F, mPaint)
            canvas.drawLine(arg11 + 25F, arg22 - 25F, arg11 - 25F, arg22 + 25F, mPaint)
        }

        //нолик
        fun Circle(arg1: Float, arg2: Float) {
            val arg11:Float = arg1 - (arg1 % 60) + 30f
            val arg22:Float = arg2 + 30f - (arg2 % 60)
            mPaint.isAntiAlias = true
            mPaint.color = Color.GREEN // установим зеленый цвет
            mPaint.style = STROKE
            mPaint.strokeWidth = 5F
            canvas.drawCircle(arg11, arg22, 27f, mPaint)
        }

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
        while (i + 80f<= h * 3 / 4) {
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
        canvas.drawText("$xPath    $yPath", 20f, 540f, shadowPaint)
        if(urg % 2 == 0)
          Circle(xPath,yPath)
        else
          Cross(xPath,yPath)
    }
}

class CanvasActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val draw2D = Draw2D(this)
        setContentView(draw2D)
       }
}

